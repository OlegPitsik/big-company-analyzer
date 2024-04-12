package com.swissre.bigcompanyanalyzer.control;

public class ReportException extends RuntimeException {
    protected ReportException(String error) {
        super(error);
    }
}
