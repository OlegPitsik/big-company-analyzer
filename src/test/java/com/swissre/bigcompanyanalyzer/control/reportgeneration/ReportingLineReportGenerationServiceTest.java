package com.swissre.bigcompanyanalyzer.control.reportgeneration;

import com.swissre.bigcompanyanalyzer.entity.Employee;
import com.swissre.bigcompanyanalyzer.entity.Report;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReportingLineReportGenerationServiceTest {
    int reportingLineLimit = 2;
    ReportGenerationService service = new ReportingLineReportGenerationService(reportingLineLimit);

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
    void shouldNotAddEntryInTheReportWhenThereAreNoEmployeesWithReportLineMoreThenLimit() {
        //Given
        Map<Long, Employee> map = Map.of(
                12L, new Employee(12L, null, null, null, null),
                13L, new Employee(13L, null, null, null, 12L)
        );
        map.get(12L).addSubordinate(map.get(13L));

        //When
        Report report = service.generateReport(map);

        //Then
        assertEquals(Report.ReportType.REPORTING_LINE_REPORT, report.getType());
        assertEquals(Set.of(), report.getReportEntries());
        assertEquals(Set.of(), report.getErrorsReportEntries());
    }

    @Test
    void shouldAddEntryInTheReportWhenThereAreEmployeesWithReportLineMoreThenLimit() {
        //Given
        Map<Long, Employee> map = Map.of(
                12L, new Employee(12L, null, null, null, null),
                13L, new Employee(13L, null, null, null, 12L),
                14L, new Employee(14L, null, null, null, 13L),
                15L, new Employee(15L, null, null, null, 14L)
        );
        map.get(12L).addSubordinate(map.get(13L));
        map.get(13L).addSubordinate(map.get(14L));
        map.get(14L).addSubordinate(map.get(15L));

        //When
        Report report = service.generateReport(map);

        //Then
        assertEquals(Report.ReportType.REPORTING_LINE_REPORT, report.getType());
        assertEquals(Set.of("The employee with id %d has a reporting line of %d levels, which is %d more than the allowed level %d".
                        formatted(15L, 3, 3 - reportingLineLimit, reportingLineLimit)),
                report.getReportEntries());
        assertEquals(Set.of(), report.getErrorsReportEntries());
    }

}