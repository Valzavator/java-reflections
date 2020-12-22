package com.maxsvynarchuk;

import com.maxsvynarchuk.metric.Analyzer;

public class Runner {

    public static final String PACKAGE1 = "com.maxsvynarchuk.test.inheretence";
    public static final String PACKAGE2 = "com.maxsvynarchuk.test.methods";
    public static final String PACKAGE3 = "com.maxsvynarchuk.test.fields";

    public static void main(String[] args) {
        Analyzer analyzer = new Analyzer(PACKAGE1);
        analyzer.calculateMetrics().printAll();
    }

}
