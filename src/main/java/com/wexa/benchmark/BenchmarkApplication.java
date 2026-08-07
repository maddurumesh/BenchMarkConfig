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

        BenchmarkRunner runner =
                new BenchmarkRunner(
                        adapter,
                        config.getWarmupIterations(),
                        config.getMeasurementIterations(),
                        config.getConcurrency()
                );

        BenchmarkResult result =
                runner.run();

        BenchmarkRunner.writeResult(
                result,
                "results/cognodb.json"
        );

        System.out.println();
        System.out.println("==========================================");
        System.out.println("Benchmark complete.");
        System.out.println("Result: results/cognodb.json");
        System.out.println("==========================================");
    }
}
