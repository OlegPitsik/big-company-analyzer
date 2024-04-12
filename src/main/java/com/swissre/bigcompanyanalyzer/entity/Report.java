package com.swissre.bigcompanyanalyzer.entity;

import java.util.HashSet;
import java.util.Set;

public class Report {
    private final ReportType type;
    private final Set<String> reportEntries = new HashSet<>();
    private final Set<String> errorsReportEntries = new HashSet<>();

    public Report(ReportType type) {
        this.type = type;
    }

    public ReportType getType() {
        return type;
    }

    public Set<String> getReportEntries() {
        return reportEntries;
    }

    public Set<String> getErrorsReportEntries() {
        return errorsReportEntries;
    }

    public void addReportEntry(String reportLine) {
        reportEntries.add(reportLine);
    }

    public void addErrorsReportEntry(String reportLine) {
        errorsReportEntries.add(reportLine);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Report report = (Report) o;

        if (type != report.type) return false;
        if (!reportEntries.equals(report.reportEntries)) return false;
        return errorsReportEntries.equals(report.errorsReportEntries);
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + reportEntries.hashCode();
        result = 31 * result + errorsReportEntries.hashCode();
        return result;
    }

    public enum ReportType {
        SALARY_LEVEL_REPORT("SALARY REPORT"),
        REPORTING_LINE_REPORT("REPORTING LINE REPORT"),

        UNRECOVERABLE_ERROR_REPORT("UNRECOVERABLE ERROR REPORT");

        private final String reportName;

        ReportType(String reportName) {
            this.reportName = reportName;
        }

        public String getReportName() {
            return reportName;
        }

    }

}
