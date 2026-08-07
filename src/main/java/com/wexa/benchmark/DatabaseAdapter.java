package com.wexa.benchmark;

public interface DatabaseAdapter extends AutoCloseable {

    String getName();

    void connect();

    void clearDatabase();

    long loadDataset(String nodeFile, String relationshipFile);

    long countNodes();

    long countRelationships();

    void createIndexes();

    void pointLookup(long nodeId);

    void filteredLookup(String name);

    void traverse(int hops, long startNodeId);

    void aggregation();

    void writeOperation(long nodeId);

    @Override
    void close();
}
