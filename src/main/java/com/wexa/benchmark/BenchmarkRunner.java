package com.wexa.benchmark;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.LongConsumer;

public class BenchmarkRunner {

    /*
     * Fixed benchmark dataset.
     * Assignment target: 100,000+ relationships.
     */
    private static final long DATASET_NODE_COUNT = 91_489L;
    private static final long DATASET_RELATIONSHIP_COUNT = 200_000L;

    /*
     * Fixed seed makes random start-node selection
     * reproducible across benchmark executions.
     */
    private static final long RANDOM_SEED = 20260806L;

    private final DatabaseAdapter adapter;
    private final int warmupIterations;
    private final int measurementIterations;
    private final int concurrency;

    /*
     * Same randomized start-node sequence is reused
     * across workloads.
     */
    private final List<Long> benchmarkStartNodes =
            new ArrayList<>();

    public BenchmarkRunner(
            DatabaseAdapter adapter,
            int warmupIterations,
            int measurementIterations,
            int concurrency) {

        this.adapter = adapter;
        this.warmupIterations = warmupIterations;
        this.measurementIterations = measurementIterations;
        this.concurrency = concurrency;

        initializeStartNodes();
    }

    /*
     * Creates a reproducible randomized list of node IDs.
     */
    private void initializeStartNodes() {

        for (long id = 1L;
             id <= DATASET_NODE_COUNT;
             id++) {

            benchmarkStartNodes.add(id);
        }

        Collections.shuffle(
                benchmarkStartNodes,
                new Random(RANDOM_SEED)
        );
    }

    /*
     * Execute the complete benchmark.
     */
    public BenchmarkResult run() {

        System.out.println();
        System.out.println("==========================================");
        System.out.println(
                "Benchmark: " + adapter.getName()
        );
        System.out.println("==========================================");

        adapter.connect();

        System.out.println("Connected.");

        BenchmarkResult result =
                new BenchmarkResult(adapter.getName());

        result.setWarmupIterations(
                warmupIterations
        );

        result.setMeasurementIterations(
                measurementIterations
        );

        result.setConcurrency(
                concurrency
        );

        result.setDatasetSource(
                "SNAP soc-Pokec social network dataset; "
                        + "fixed benchmark subset containing "
                        + DATASET_NODE_COUNT
                        + " nodes and "
                        + DATASET_RELATIONSHIP_COUNT
                        + " relationships."
        );

        /*
         * Dataset loading is not performed during this execution.
         * Therefore ingestion throughput is not fabricated.
         */
        result.setLoadMethod(
                "Existing dataset reused for this benchmark execution. "
                        + "No database clear or dataset reload was performed. "
                        + "Ingestion throughput was therefore not measured."
        );

        result.setInstanceSpecs(
                getEnvironmentValue(
                        "BENCHMARK_INSTANCE_SPECS",
                        "Not observable from benchmark client."
                )
        );

        result.setFootprint(
                getEnvironmentValue(
                        "BENCHMARK_FOOTPRINT",
                        "Not observable from benchmark client."
                )
        );

        try {

            /*
             * ------------------------------------------
             * DATASET VERIFICATION
             * ------------------------------------------
             */

            System.out.println();
            System.out.println("Using existing dataset...");

            long nodes =
                    adapter.countNodes();

            long relationships =
                    adapter.countRelationships();

            result.setNodeCount(nodes);
            result.setRelationshipCount(
                    relationships
            );

            System.out.println(
                    "Nodes: " + nodes
            );

            System.out.println(
                    "Relationships: " + relationships
            );

            /*
             * ------------------------------------------
             * INDEXES
             * ------------------------------------------
             */

            System.out.println();
            System.out.println(
                    "Creating/verifying indexes..."
            );

            adapter.createIndexes();

            /*
             * ------------------------------------------
             * WARM-UP
             * ------------------------------------------
             */

            System.out.println();
            System.out.println(
                    "Running warm-up..."
            );

            warmup();

            /*
             * ------------------------------------------
             * 1-HOP TRAVERSAL
             * ------------------------------------------
             */

            System.out.println();
            System.out.println(
                    "Running 1-hop traversal..."
            );

            measure(
                    result,
                    "1-hop",
                    i -> adapter.traverse(
                            1,
                            startNode(i)
                    )
            );

            /*
             * ------------------------------------------
             * 2-HOP TRAVERSAL
             * ------------------------------------------
             */

            System.out.println();
            System.out.println(
                    "Running 2-hop traversal..."
            );

            measure(
                    result,
                    "2-hop",
                    i -> adapter.traverse(
                            2,
                            startNode(i)
                    )
            );

            /*
             * ------------------------------------------
             * 3-HOP TRAVERSAL
             * ------------------------------------------
             */

            System.out.println();
            System.out.println(
                    "Running 3-hop traversal..."
            );

            try {

                measure(
                        result,
                        "3-hop",
                        i -> adapter.traverse(
                                3,
                                startNode(i)
                        )
                );

            } catch (Exception e) {

                System.err.println(
                        "3-hop workload failed: "
                                + e.getMessage()
                );

                appendNote(
                        result,
                        "3-hop failed during this run: "
                                + e.getMessage()
                );
            }

            /*
             * ------------------------------------------
             * POINT LOOKUP
             * ------------------------------------------
             */

            System.out.println();
            System.out.println(
                    "Running point lookup..."
            );

            measure(
                    result,
                    "point-lookup",
                    i -> adapter.pointLookup(
                            startNode(i)
                    )
            );

            /*
             * ------------------------------------------
             * FILTERED LOOKUP
             * ------------------------------------------
             */

            System.out.println();
            System.out.println(
                    "Running filtered lookup..."
            );

            measure(
                    result,
                    "filtered-lookup",
                    i -> adapter.filteredLookup(
                            "User-" + startNode(i)
                    )
            );

            /*
             * ------------------------------------------
             * AGGREGATION
             * ------------------------------------------
             */

            System.out.println();
            System.out.println(
                    "Running aggregation..."
            );

            measure(
                    result,
                    "aggregation",
                    i -> adapter.aggregation()
            );

            /*
             * ------------------------------------------
             * MIXED WORKLOAD
             * ------------------------------------------
             */

            System.out.println();
            System.out.println(
                    "Running mixed workload..."
            );

            double qps =
                    runMixedWorkload();

            result.setMixedQueriesPerSecond(qps);

            /*
             * ------------------------------------------
             * FINAL NOTES
             * ------------------------------------------
             */

            String notes =
                    "Dataset reused; "
                            + "warm-up: "
                            + warmupIterations
                            + "; measurement: "
                            + measurementIterations
                            + "; concurrency: "
                            + concurrency
                            + "; read/write mix: 80/20; "
                            + "random start-node selection uses "
                            + "fixed seed "
                            + RANDOM_SEED
                            + "; ingestion throughput was not "
                            + "measured in this execution.";

            result.setNotes(notes);

            return result;

        } finally {

            adapter.close();
        }
    }

    /*
     * ------------------------------------------
     * WARM-UP
     * ------------------------------------------
     *
     * Every required read workload is warmed up
     * before measurement.
     */
    private void warmup() {

        for (int i = 0;
             i < warmupIterations;
             i++) {

            long id =
                    startNode(i);

            /*
             * 1-hop
             */
            adapter.traverse(
                    1,
                    id
            );

            /*
             * 2-hop
             */
            adapter.traverse(
                    2,
                    id
            );

            /*
             * 3-hop
             */
            adapter.traverse(
                    3,
                    id
            );

            /*
             * Point lookup
             */
            adapter.pointLookup(id);

            /*
             * Filtered lookup
             */
            adapter.filteredLookup(
                    "User-" + id
            );

            /*
             * Aggregation
             */
            adapter.aggregation();
        }
    }

    /*
     * ------------------------------------------
     * LATENCY MEASUREMENT
     * ------------------------------------------
     *
     * Measures each operation individually and
     * calculates p50 and p95 latency.
     */
    private void measure(
            BenchmarkResult result,
            String workload,
            LongConsumer operation) {

        List<Long> latencies =
                new ArrayList<>(
                        measurementIterations
                );

        for (int i = 0;
             i < measurementIterations;
             i++) {

            long start =
                    System.nanoTime();

            operation.accept(i);

            long elapsed =
                    System.nanoTime() - start;

            long latencyMicros =
                    TimeUnit.NANOSECONDS.toMicros(
                            elapsed
                    );

            latencies.add(
                    latencyMicros
            );
        }

        Collections.sort(latencies);

        double p50 =
                percentile(
                        latencies,
                        0.50
                ) / 1000.0;

        double p95 =
                percentile(
                        latencies,
                        0.95
                ) / 1000.0;

        result.addLatency(
                workload,
                p50,
                p95
        );

        System.out.printf(
                "%-20s p50=%8.3f ms p95=%8.3f ms%n",
                workload,
                p50,
                p95
        );
    }

    /*
     * ------------------------------------------
     * PERCENTILE
     * ------------------------------------------
     *
     * Linear interpolation between sorted samples.
     */
    private double percentile(
            List<Long> values,
            double percentile) {

        if (values.isEmpty()) {
            return 0;
        }

        double index =
                percentile * (values.size() - 1);

        int lower =
                (int) Math.floor(index);

        int upper =
                (int) Math.ceil(index);

        if (lower == upper) {
            return values.get(lower);
        }

        double weight =
                index - lower;

        return values.get(lower)
                * (1.0 - weight)
                + values.get(upper)
                * weight;
    }

    /*
     * ------------------------------------------
     * START NODE
     * ------------------------------------------
     *
     * Returns a reproducibly randomized node ID.
     */
    private long startNode(
            long iteration) {

        int index =
                (int) (
                        iteration
                                % benchmarkStartNodes.size()
                );

        return benchmarkStartNodes.get(index);
    }

    /*
     * ------------------------------------------
     * MIXED WORKLOAD
     * ------------------------------------------
     *
     * 80% reads
     * 20% writes
     *
     * Default concurrency is supplied by configuration.
     */
    private double runMixedWorkload() {

        if (concurrency <= 0) {

            throw new IllegalArgumentException(
                    "Concurrency must be greater than zero."
            );
        }

        if (measurementIterations <= 0) {

            throw new IllegalArgumentException(
                    "Measurement iterations must be greater than zero."
            );
        }

        ExecutorService executor =
                Executors.newFixedThreadPool(
                        concurrency
                );

        long start =
                System.nanoTime();

        List<Future<?>> futures =
                new ArrayList<>();

        int totalOperations =
                measurementIterations
                        * concurrency;

        try {

            /*
             * Create one task per concurrent client.
             */
            for (int client = 0;
                 client < concurrency;
                 client++) {

                final int clientId =
                        client;

                futures.add(
                        executor.submit(
                                () -> {

                                    for (
                                            int i = 0;
                                            i < measurementIterations;
                                            i++
                                    ) {

                                        long operationIndex =
                                                (long) clientId
                                                        * measurementIterations
                                                        + i;

                                        long id =
                                                startNode(
                                                        operationIndex
                                                );

                                        /*
                                         * Every fifth operation
                                         * is a write.
                                         *
                                         * 1 write + 4 reads
                                         * = 20% write / 80% read.
                                         */
                                        if (i % 5 == 0) {

                                            adapter.writeOperation(
                                                    id
                                            );

                                        } else {

                                            adapter.traverse(
                                                    1,
                                                    id
                                            );
                                        }
                                    }
                                }
                        )
                );
            }

            /*
             * Wait for every client.
             */
            for (Future<?> future :
                    futures) {

                try {

                    future.get();

                } catch (InterruptedException e) {

                    Thread.currentThread()
                            .interrupt();

                    throw new RuntimeException(
                            "Mixed workload interrupted.",
                            e
                    );

                } catch (ExecutionException e) {

                    throw new RuntimeException(
                            "Mixed workload failed.",
                            e.getCause()
                    );
                }
            }

            long elapsed =
                    System.nanoTime() - start;

            double seconds =
                    elapsed / 1_000_000_000.0;

            double qps =
                    totalOperations / seconds;

            System.out.printf(
                    "Mixed workload: %.2f queries/sec%n",
                    qps
            );

            return qps;

        } finally {

            /*
             * Always shut down the executor.
             */
            executor.shutdown();

            try {

                if (!executor.awaitTermination(
                        30,
                        TimeUnit.SECONDS
                )) {

                    executor.shutdownNow();
                }

            } catch (InterruptedException e) {

                executor.shutdownNow();

                Thread.currentThread()
                        .interrupt();
            }
        }
    }

    /*
     * ------------------------------------------
     * ENVIRONMENT VALUE
     * ------------------------------------------
     */
    private String getEnvironmentValue(
            String variable,
            String defaultValue) {

        String value =
                System.getenv(variable);

        if (value == null
                || value.isBlank()) {

            return defaultValue;
        }

        return value;
    }

    /*
     * ------------------------------------------
     * APPEND NOTE
     * ------------------------------------------
     */
    private void appendNote(
            BenchmarkResult result,
            String note) {

        String existing =
                result.getNotes();

        if (existing == null
                || existing.isBlank()) {

            result.setNotes(note);

        } else {

            result.setNotes(
                    existing
                            + " "
                            + note
            );
        }
    }

    /*
     * ------------------------------------------
     * WRITE RESULT
     * ------------------------------------------
     */
    public static void writeResult(
            BenchmarkResult result,
            String outputFile) {

        try {

            File file =
                    new File(outputFile);

            File parent =
                    file.getParentFile();

            if (parent != null) {
                parent.mkdirs();
            }

            ObjectMapper mapper =
                    new ObjectMapper();

            mapper.enable(
                    SerializationFeature.INDENT_OUTPUT
            );

            mapper.writeValue(
                    file,
                    result
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to write result.",
                    e
            );
        }
    }
}
