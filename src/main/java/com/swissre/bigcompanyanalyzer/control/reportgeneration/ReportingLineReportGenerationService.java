package com.swissre.bigcompanyanalyzer.control.reportgeneration;

import com.swissre.bigcompanyanalyzer.entity.Employee;
import com.swissre.bigcompanyanalyzer.entity.Report;

import java.util.List;
import java.util.Map;

public class ReportingLineReportGenerationService implements ReportGenerationService {
    private final int allowedReportingLevel;

    public ReportingLineReportGenerationService(int allowedReportingLevel) {
        this.allowedReportingLevel = allowedReportingLevel;
    }

    @Override
    public Report generateReport(Map<Long, Employee> linkedManagersToSubordinatesStructure) {
        var report = new Report(Report.ReportType.REPORTING_LINE_REPORT);

        var ceo = this.findCEO(linkedManagersToSubordinatesStructure);
        var subordinates = this.getSubordinatesFrom(List.of(ceo));
        var reportingLineLevel = 1;

        while (!subordinates.isEmpty()) {
            if (reportingLineLevel > allowedReportingLevel) {
                this.addToReport(subordinates, reportingLineLevel, allowedReportingLevel, report);
            }
            reportingLineLevel++;
            subordinates = this.getSubordinatesFrom(subordinates);
        }
        return report;
    }

    private List<Employee> getSubordinatesFrom(List<Employee> employees) {
        return employees.stream()
                .flatMap(employee -> employee.getSubordinates().stream())
                .toList();
    }

    private void addToReport(List<Employee> employees, int reportingLineLevel, int allowedLevel, Report report) {
        employees.forEach(employee -> report.addReportEntry(
                "The employee with id %d has a reporting line of %d levels, which is %d more than the allowed level %d"
                        .formatted(employee.getId(), reportingLineLevel, reportingLineLevel - allowedLevel, allowedLevel))
        );
    }
}
