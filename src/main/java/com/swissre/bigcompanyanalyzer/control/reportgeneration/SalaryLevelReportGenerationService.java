package com.swissre.bigcompanyanalyzer.control.reportgeneration;

import com.swissre.bigcompanyanalyzer.entity.Employee;
import com.swissre.bigcompanyanalyzer.entity.Report;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class SalaryLevelReportGenerationService implements ReportGenerationService {

    private final int minAllowedPercent;
    private final int maxAllowedPercent;

    public SalaryLevelReportGenerationService(int minAllowedPercent,
                                              int maxAllowedPercent) {
        this.minAllowedPercent = minAllowedPercent;
        this.maxAllowedPercent = maxAllowedPercent;
    }

    @Override
    public Report generateReport(Map<Long, Employee> linkedManagersToSubordinatesStructure) {
        var report = new Report(Report.ReportType.SALARY_LEVEL_REPORT);

        var employees = List.of(this.findCEO(linkedManagersToSubordinatesStructure));
        while (!employees.isEmpty()) {
            employees.forEach(employee ->
                    getRelativeToSubordinatesSalaryPercent(employee, report)
                            .ifPresent(salaryPercent -> this.addToReportIfNeeded(employee, salaryPercent, report))
            );
            employees = this.getSubordinatesFrom(employees);
        }
        return report;
    }

    private Optional<Integer> getRelativeToSubordinatesSalaryPercent(Employee employee, Report report) {
        var employeeSalary = employee.getSalary();
        if (employeeSalary == null) {
            report.addErrorsReportEntry("Employee with id %d has no salary".formatted(employee.getId()));
            return Optional.empty();
        } else if (employeeSalary.compareTo(BigDecimal.ZERO) < 0) {
            report.addErrorsReportEntry("Employee with id %d has negative salary".formatted(employee.getId()));
            return Optional.empty();
        } else if (!employee.hasSubordinates()) {
            return Optional.empty();
        }

        var subordinateSalaries = employee.getSubordinates()
                .stream()
                .map(Employee::getSalary)
                .filter(Objects::nonNull)
                .filter(subordinate -> subordinate.compareTo(BigDecimal.ZERO) > 0)
                .toList();

        var averageSalary = subordinateSalaries.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(new BigDecimal(subordinateSalaries.size()), RoundingMode.HALF_UP);

        return Optional.of(
                employeeSalary.divide(averageSalary, averageSalary.scale() + employeeSalary.scale(), RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .intValue()
        );
    }

    private void addToReportIfNeeded(Employee employee, int salaryPercent, Report report) {
        if (salaryPercent > maxAllowedPercent) {
            report.addReportEntry("Manager with id %d earn %d percent of their subordinates, the maximum allowed level is %d".formatted(
                    employee.getId(), salaryPercent, maxAllowedPercent));
        } else if (salaryPercent < minAllowedPercent) {
            report.addReportEntry("Manager with id %d earn %d percent of their subordinates, the minimum allowed level is %d".formatted(
                    employee.getId(), salaryPercent, minAllowedPercent));
        }
    }

    private List<Employee> getSubordinatesFrom(List<Employee> employees) {
        return employees.stream()
                .flatMap(employee -> employee.getSubordinates().stream())
                .toList();
    }
}
