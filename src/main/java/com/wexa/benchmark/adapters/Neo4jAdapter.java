package com.wexa.benchmark.adapters;

import com.wexa.benchmark.BenchmarkConfig;

public class Neo4jAdapter extends CognoDBAdapter {

    public Neo4jAdapter(BenchmarkConfig config) {
        super(config);
    }

    @Override
    public String getName() {
        return "Neo4j";
    }
}