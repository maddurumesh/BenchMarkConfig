package com.wexa.benchmark;

public class BenchmarkConfig {

    private final String name;
    private final String uri;
    private final String username;
    private final String password;
    private final int warmupIterations;
    private final int measurementIterations;
    private final int concurrency;

    public BenchmarkConfig(
            String name,
            String uri,
            String username,
            String password,
            int warmupIterations,
            int measurementIterations,
            int concurrency) {

        this.name = name;
        this.uri = uri;
        this.username = username;
        this.password = password;
        this.warmupIterations = warmupIterations;
        this.measurementIterations = measurementIterations;
        this.concurrency = concurrency;
    }

    public String getName() {
        return name;
    }

    public String getUri() {
        return uri;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getWarmupIterations() {
        return warmupIterations;
    }

    public int getMeasurementIterations() {
        return measurementIterations;
    }

    public int getConcurrency() {
        return concurrency;
    }

    public static BenchmarkConfig fromEnvironment(
            String name,
            String uriVariable,
            String usernameVariable,
            String passwordVariable) {

        String uri = System.getenv(uriVariable);
        String username = System.getenv(usernameVariable);
        String password = System.getenv(passwordVariable);

        if (uri == null || uri.isBlank()) {
            throw new IllegalStateException(
                    "Missing environment variable: " + uriVariable);
        }

        if (username == null || username.isBlank()) {
            throw new IllegalStateException(
                    "Missing environment variable: " + usernameVariable);
        }

        if (password == null || password.isBlank()) {
            throw new IllegalStateException(
                    "Missing environment variable: " + passwordVariable);
        }

        return new BenchmarkConfig(
                name,
                uri,
                username,
                password,
                20,
                100,
                10
        );
    }
}
