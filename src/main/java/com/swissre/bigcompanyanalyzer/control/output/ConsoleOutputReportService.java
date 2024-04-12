package com.swissre.bigcompanyanalyzer.control.output;

import com.swissre.bigcompanyanalyzer.entity.Report;

import java.util.Collection;

public class ConsoleOutputReportService implements ReportOutputService {

    @Override
    public void writeErrorReport(Report report) {
        System.err.println(report.getType().getReportName());
        System.err.println(report.getErrorsReportEntries());
    }

    @Override
    public void writeReports(Collection<Report> reports) {
        reports.forEach(report -> {
            System.out.println("------------------------");
            System.out.println(report.getType().getReportName());

            System.out.println("Errors:");
            if (report.getErrorsReportEntries().isEmpty()) {
                System.out.println("No errors.");
            } else {
                report.getErrorsReportEntries().forEach(System.out::println);
            }

            System.out.println("Report:");
            if (report.getReportEntries().isEmpty()) {
                System.out.println("No issues.");
            } else {
                report.getReportEntries().forEach(System.out::println);
            }
            System.out.println("------------------------");
        });
    }
}
