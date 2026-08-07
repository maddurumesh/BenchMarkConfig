package com.wexa.benchmark;

import com.wexa.benchmark.adapters.CognoDBAdapter;

public class BenchmarkApplication {

    public static void main(String[] args) {

        System.out.println();
        System.out.println("==========================================");
        System.out.println("WEXA AI - Graph Database Cloud Benchmark");
        System.out.println("==========================================");

        BenchmarkConfig config =
                BenchmarkConfig.fromEnvironment(
                        "CognoDB",
                        "COGNODB_URI",
                        "COGNODB_USERNAME",
                        "COGNODB_PASSWORD"
                );

        CognoDBAdapter adapter =
                new CognoDBAdapter(config);

        try {

            /*
             * ------------------------------------------
             * CONNECT
             * ------------------------------------------
             */

            adapter.connect();

            /*
             * ------------------------------------------
             * VERIFY EXISTING DATASET
             * ------------------------------------------
             *
             * Do NOT clear the CognoDB cloud database.
             * The dataset is already loaded.
             */

            System.out.println();
            System.out.println("==========================================");
            System.out.println("Preparing CognoDB dataset");
            System.out.println("==========================================");

            System.out.println(
                    "Using existing CognoDB dataset..."
            );

            long nodes =
                    adapter.countNodes();

            long relationships =
                    adapter.countRelationships();

            System.out.println(
                    "Nodes: "
                            + nodes
            );

            System.out.println(
                    "Relationships: "
                            + relationships
            );

            System.out.println(
                    "Expected nodes: 91489"
            );

            System.out.println(
                    "Expected relationships: 200000"
            );

            if (nodes != 91489) {

                throw new IllegalStateException(
                        "Unexpected node count. Expected 91489 but found "
                                + nodes
                );
            }

            if (relationships != 200000) {

                throw new IllegalStateException(
                        "Unexpected relationship count. Expected 200000 but found "
                                + relationships
                );
            }

            System.out.println();
            System.out.println(
                    "Dataset verification PASSED."
            );

            /*
             * ------------------------------------------
             * RUN BENCHMARK
             * ------------------------------------------
             */

            BenchmarkRunner runner =
                    new BenchmarkRunner(
                            adapter,
                            config.getWarmupIterations(),
                            config.getMeasurementIterations(),
                            config.getConcurrency()
                    );

            BenchmarkResult result =
                    runner.run();

            /*
             * ------------------------------------------
             * WRITE RESULT
             * ------------------------------------------
             */

            BenchmarkRunner.writeResult(
                    result,
                    "results/cognodb.json"
            );

            System.out.println();
            System.out.println("==========================================");
            System.out.println("CognoDB benchmark complete.");
            System.out.println("Result: results/cognodb.json");
            System.out.println("==========================================");

        } finally {

            adapter.close();
        }
    }
}