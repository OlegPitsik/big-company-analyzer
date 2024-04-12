package com.swissre.bigcompanyanalyzer.control.output;

import com.swissre.bigcompanyanalyzer.entity.Report;

import java.util.Collection;

public interface ReportOutputService {

    void writeReports(Collection<Report> reports);

    void writeErrorReport(Report report);
}
