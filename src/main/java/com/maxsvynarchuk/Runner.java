package com.maxsvynarchuk;

import com.maxsvynarchuk.metric.Analyzer;

public class Runner {

    public static final String PACKAGE = "com.maxsvynarchuk.test2";

    public static void main(String[] args) {
        Analyzer analyzer = new Analyzer(PACKAGE);
        analyzer.calculateMetrics().printAll();
    }

}
