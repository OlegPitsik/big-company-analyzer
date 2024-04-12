package com.swissre.bigcompanyanalyzer.facade;

import com.swissre.bigcompanyanalyzer.control.ReportService;

import java.nio.file.Path;

/**
 * Entry point for analyzing a company's structure and generating reports.
 * Currently, supports analysis from files.
 * <p>
 * Author: Oleg Pitsik
 */
public class Analyzer {
    private final ReportService reportService;

    public Analyzer(ReportService reportService) {
        this.reportService = reportService;
    }

    public void analyzeFromFile(String fileName, String basicDirectory) {
        Path path = Path.of(basicDirectory, fileName);
        reportService.analyzeAndMakeReportFromFile(path);
    }

}
