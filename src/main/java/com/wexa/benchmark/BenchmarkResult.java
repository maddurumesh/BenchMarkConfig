package com.wexa.benchmark;

import java.util.LinkedHashMap;
import java.util.Map;

public class BenchmarkResult {

    private String database;

    private long nodeCount;
    private long relationshipCount;

    private long loadTimeMs;
    private double nodesPerSecond;
    private double relationshipsPerSecond;

    private final Map<String, Double> p50 =
            new LinkedHashMap<>();

    private final Map<String, Double> p95 =
            new LinkedHashMap<>();

    private double mixedQueriesPerSecond;

    private int warmupIterations;
    private int measurementIterations;
    private int concurrency;

    private String instanceSpecs;
    private String datasetSource;
    private String loadMethod;
    private String footprint;
    private String notes;

    public BenchmarkResult() {
    }

    public BenchmarkResult(String database) {
        this.database = database;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public long getNodeCount() {
        return nodeCount;
    }

    public void setNodeCount(long nodeCount) {
        this.nodeCount = nodeCount;
    }

    public long getRelationshipCount() {
        return relationshipCount;
    }

    public void setRelationshipCount(long relationshipCount) {
        this.relationshipCount = relationshipCount;
    }

    public long getLoadTimeMs() {
        return loadTimeMs;
    }

    public void setLoadTimeMs(long loadTimeMs) {
        this.loadTimeMs = loadTimeMs;
    }

    public double getNodesPerSecond() {
        return nodesPerSecond;
    }

    public void setNodesPerSecond(double nodesPerSecond) {
        this.nodesPerSecond = nodesPerSecond;
    }

    public double getRelationshipsPerSecond() {
        return relationshipsPerSecond;
    }

    public void setRelationshipsPerSecond(double relationshipsPerSecond) {
        this.relationshipsPerSecond = relationshipsPerSecond;
    }

    public Map<String, Double> getP50() {
        return p50;
    }

    public Map<String, Double> getP95() {
        return p95;
    }

    public void addLatency(
            String workload,
            double p50Value,
            double p95Value) {

        p50.put(workload, p50Value);
        p95.put(workload, p95Value);
    }

    public double getMixedQueriesPerSecond() {
        return mixedQueriesPerSecond;
    }

    public void setMixedQueriesPerSecond(
            double mixedQueriesPerSecond) {

        this.mixedQueriesPerSecond =
                mixedQueriesPerSecond;
    }

    public int getWarmupIterations() {
        return warmupIterations;
    }

    public void setWarmupIterations(
            int warmupIterations) {

        this.warmupIterations =
                warmupIterations;
    }

    public int getMeasurementIterations() {
        return measurementIterations;
    }

    public void setMeasurementIterations(
            int measurementIterations) {

        this.measurementIterations =
                measurementIterations;
    }

    public int getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(
            int concurrency) {

        this.concurrency = concurrency;
    }

    public String getInstanceSpecs() {
        return instanceSpecs;
    }

    public void setInstanceSpecs(
            String instanceSpecs) {

        this.instanceSpecs = instanceSpecs;
    }

    public String getDatasetSource() {
        return datasetSource;
    }

    public void setDatasetSource(
            String datasetSource) {

        this.datasetSource = datasetSource;
    }

    public String getLoadMethod() {
        return loadMethod;
    }

    public void setLoadMethod(
            String loadMethod) {

        this.loadMethod = loadMethod;
    }

    public String getFootprint() {
        return footprint;
    }

    public void setFootprint(
            String footprint) {

        this.footprint = footprint;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
