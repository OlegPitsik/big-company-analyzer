package com.swissre.bigcompanyanalyzer.control.reportgeneration;

import com.swissre.bigcompanyanalyzer.entity.Employee;
import com.swissre.bigcompanyanalyzer.entity.Report;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SalaryLevelReportGenerationServiceTest {

    int minAllowedPercent = 120;
    int maxAllowedPercent = 150;

    ReportGenerationService service = new SalaryLevelReportGenerationService(minAllowedPercent, maxAllowedPercent);

    @Test
    void shouldThrowGenerationReportExceptionWhenStructureHasMoreThanOneCEO() {
        //Given
        Map<Long, Employee> map = Map.of(
                12L, new Employee(12L, null, null, null, null),
                13L, new Employee(13L, null, null, null, null)
        );

        //When
        Throwable exception = assertThrows(GenerationReportException.class, () -> service.generateReport(map));

        //Then
        assertEquals("There are more than 1 CEO in the company structure", exception.getMessage());
    }

    @Test
    void shouldThrowGenerationReportExceptionWhenStructureHasNoCEO() {
        //Given
        Map<Long, Employee> map = Map.of(
                12L, new Employee(12L, null, null, null, 12L),
                13L, new Employee(13L, null, null, null, 12L)
        );

        //When
        Throwable exception = assertThrows(GenerationReportException.class, () -> service.generateReport(map));

        //Then
        assertEquals("There is no CEO in the company structure", exception.getMessage());
    }

    @Test
    void shouldNotAddEntryInTheReportWhenThereAreNoEmployeesWithSalaryOutOfBoundary() {
        //Given
        Map<Long, Employee> map = Map.of(
                12L, new Employee(12L, null, null, BigDecimal.valueOf(13_000, 2), null),
                13L, new Employee(13L, null, null, BigDecimal.valueOf(10_000, 2), 12L)
        );
        map.get(12L).addSubordinate(map.get(13L));

        //When
        Report report = service.generateReport(map);

        //Then
        assertEquals(Report.ReportType.SALARY_LEVEL_REPORT, report.getType());
        assertEquals(Set.of(), report.getReportEntries());
        assertEquals(Set.of(), report.getErrorsReportEntries());
    }

    @Test
    void shouldAddEntryInTheReportWhenThereIsManagerWhichEarnsLessThanAverageSalaryOfSubordinates() {
        //Given
        long average = 100;
        int lessPercentThenLimit = minAllowedPercent - 1;
        long managerSalary = average * lessPercentThenLimit / 100;
        Map<Long, Employee> map = Map.of(
                12L, new Employee(12L, null, null, BigDecimal.valueOf(managerSalary, 2), null),
                13L, new Employee(13L, null, null, BigDecimal.valueOf(average - 10, 2), 12L),
                14L, new Employee(14L, null, null, BigDecimal.valueOf(average + 10, 2), 12L)
        );
        map.get(12L).addSubordinate(map.get(13L));
        map.get(12L).addSubordinate(map.get(14L));

        //When
        Report report = service.generateReport(map);

        //Then
        assertEquals(Report.ReportType.SALARY_LEVEL_REPORT, report.getType());
        assertEquals(Set.of("Manager with id %d earn %d percent of their subordinates, the minimum allowed level is %d"
                        .formatted(12L, lessPercentThenLimit, minAllowedPercent)),
                report.getReportEntries());
        assertEquals(Set.of(), report.getErrorsReportEntries());
    }

    @Test
    void shouldNotTakeIntoAccountNullOrLessThanZeroValuesOfSubordinatesSalaryAndAddErrorsToTheReport() {
        //Given
        long average = 100;
        int lessPercentThenLimit = minAllowedPercent - 1;
        long managerSalary = average * lessPercentThenLimit / 100;
        Map<Long, Employee> map = Map.of(
                12L, new Employee(12L, null, null, BigDecimal.valueOf(managerSalary, 2), null),
                13L, new Employee(13L, null, null, BigDecimal.valueOf(average - 10, 2), 12L),
                14L, new Employee(14L, null, null, BigDecimal.valueOf(average + 10, 2), 12L),
                15L, new Employee(15L, null, null, null, 12L),
                16L, new Employee(16L, null, null, BigDecimal.valueOf(-0.01).setScale(2, RoundingMode.UNNECESSARY), 12L)
        );
        map.get(12L).addSubordinate(map.get(13L));
        map.get(12L).addSubordinate(map.get(14L));
        map.get(12L).addSubordinate(map.get(15L));
        map.get(12L).addSubordinate(map.get(16L));

        //When
        Report report = service.generateReport(map);

        //Then
        assertEquals(Report.ReportType.SALARY_LEVEL_REPORT, report.getType());
        assertEquals(Set.of("Manager with id %d earn %d percent of their subordinates, the minimum allowed level is %d"
                        .formatted(12L, lessPercentThenLimit, minAllowedPercent)),
                report.getReportEntries());
        assertEquals(Set.of("Employee with id %d has no salary"
                                .formatted(15L),
                        "Employee with id %d has negative salary"
                                .formatted(16L)),
                report.getErrorsReportEntries());
    }

    @Test
    void shouldAddEntryInTheReportWhenThereIsManagerWhichEarnsMoreThanAverageSalaryOfSubordinates() {
        //Given
        long average = 100;
        int morePercentThenLimit = maxAllowedPercent + 1;
        long managerSalary = average * morePercentThenLimit / 100;
        Map<Long, Employee> map = Map.of(
                12L, new Employee(12L, null, null, BigDecimal.valueOf(managerSalary, 2), null),
                13L, new Employee(13L, null, null, BigDecimal.valueOf(average - 10, 2), 12L),
                14L, new Employee(14L, null, null, BigDecimal.valueOf(average + 10, 2), 12L)
        );
        map.get(12L).addSubordinate(map.get(13L));
        map.get(12L).addSubordinate(map.get(14L));

        //When
        Report report = service.generateReport(map);

        //Then
        assertEquals(Report.ReportType.SALARY_LEVEL_REPORT, report.getType());
        assertEquals(Set.of("Manager with id %d earn %d percent of their subordinates, the maximum allowed level is %d"
                        .formatted(12L, morePercentThenLimit, maxAllowedPercent)),
                report.getReportEntries());
        assertEquals(Set.of(), report.getErrorsReportEntries());
    }
}