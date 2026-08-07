package com.wexa.benchmark.adapters;

import com.wexa.benchmark.BenchmarkConfig;

public class FalkorDBAdapter extends CognoDBAdapter {

    public FalkorDBAdapter(BenchmarkConfig config) {
        super(config);
    }

    @Override
    public String getName() {
        return "FalkorDB";
    }
}