package com.maxsvynarchuk;

import com.maxsvynarchuk.metric.Analyzer;
import lombok.Setter;

@Setter
public class Runner {

//    public static final String PACKAGE = "com.maxsvynarchuk.test.inheretence";
//    public static final String PACKAGE = "com.maxsvynarchuk.test.methods";
    public static final String PACKAGE = "com.maxsvynarchuk.test.fields";

    public static void main(String[] args) {
        try {
            Analyzer analyzer = new Analyzer(PACKAGE);
            analyzer.calculateMetrics().printAll();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
