package com.swissre.bigcompanyanalyzer.control;

import com.swissre.bigcompanyanalyzer.control.file.FileDataRetriever;
import com.swissre.bigcompanyanalyzer.control.output.ReportOutputService;
import com.swissre.bigcompanyanalyzer.control.reportgeneration.ReportGenerationService;
import com.swissre.bigcompanyanalyzer.entity.Report;

import java.nio.file.Path;
import java.util.List;

public class ReportService {
    private final FileDataRetriever fileDataRetriever;
    private final ReportOutputService reportOutputService;
    private final List<ReportGenerationService> reportGenerationServices;

    public ReportService(FileDataRetriever fileDataRetriever,
                         ReportOutputService reportOutputService,
                         List<ReportGenerationService> reportGenerationServices) {
        this.fileDataRetriever = fileDataRetriever;
        this.reportOutputService = reportOutputService;
        this.reportGenerationServices = reportGenerationServices;
    }

    public void analyzeAndMakeReportFromFile(Path path) {
        try {
            var idsToEmployeesMap = fileDataRetriever.retrieveLinkedCompanyEmployeesData(path);
            var reports = this.reportGenerationServices.stream()
                    .map(reportGenerationService -> reportGenerationService.generateReport(idsToEmployeesMap))
                    .toList();
            this.reportOutputService.writeReports(reports);
        } catch (ReportException ex) {
            writeErrorReport(ex.getMessage());
        }
    }

    private void writeErrorReport(String error) {
        var errorReport = new Report(Report.ReportType.UNRECOVERABLE_ERROR_REPORT);
        errorReport.addErrorsReportEntry(error);
        this.reportOutputService.writeErrorReport(errorReport);
    }
}
