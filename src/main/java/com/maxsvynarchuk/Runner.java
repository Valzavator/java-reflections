package com.maxsvynarchuk;

import com.maxsvynarchuk.metric.Analyzer;

public class Runner {

//    public static final String PACKAGE = "com.maxsvynarchuk.test.inheretence";
    public static final String PACKAGE = "com.maxsvynarchuk.test.methods";
//    public static final String PACKAGE = "com.maxsvynarchuk.test.fields";

    public static void main(String[] args) {
        Analyzer analyzer = new Analyzer(PACKAGE);
        analyzer.calculateMetrics().printAll();
    }

}
