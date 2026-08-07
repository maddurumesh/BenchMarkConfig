package com.wexa.benchmark.adapters;

import com.wexa.benchmark.BenchmarkConfig;

public class MemgraphAdapter extends CognoDBAdapter {

    public MemgraphAdapter(BenchmarkConfig config) {
        super(config);
    }

    @Override
    public String getName() {
        return "Memgraph";
    }
}