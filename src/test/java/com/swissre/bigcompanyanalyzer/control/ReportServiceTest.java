package com.swissre.bigcompanyanalyzer.control;

import com.swissre.bigcompanyanalyzer.control.file.FileDataRetriever;
import com.swissre.bigcompanyanalyzer.control.output.ReportOutputService;
import com.swissre.bigcompanyanalyzer.control.reportgeneration.ReportGenerationService;
import com.swissre.bigcompanyanalyzer.entity.Employee;
import com.swissre.bigcompanyanalyzer.entity.Report;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReportServiceTest {
    FileDataRetriever fileDataRetriever = mock(FileDataRetriever.class);
    ReportOutputService reportOutputService = mock(ReportOutputService.class);
    ReportGenerationService generationService = mock(ReportGenerationService.class);
    List<ReportGenerationService> reportGenerationServices = List.of(generationService);

    ReportService reportService = new ReportService(fileDataRetriever, reportOutputService, reportGenerationServices);


    @Test
    void shouldProvideErrorReportToOutputServiceWhenDataRetrieverThrowsReportException() {
        //Given
        Path path = Path.of("myFolder");

        Report expected = new Report(Report.ReportType.UNRECOVERABLE_ERROR_REPORT);
        expected.addErrorsReportEntry("error");

        when(fileDataRetriever.retrieveLinkedCompanyEmployeesData(path)).thenThrow(new ReportException("error"));

        //When
        reportService.analyzeAndMakeReportFromFile(path);

        //Then
        verify(reportOutputService).writeErrorReport(expected);
    }

    @Test
    void shouldProvideErrorReportToOutputServiceWhenGenerationServiceThrowsReportException() {
        //Given
        Path path = Path.of("myFolder");
        Map<Long, Employee> teamStructure = Map.of(123L, new Employee(123L, null, null, null, null));

        Report expected = new Report(Report.ReportType.UNRECOVERABLE_ERROR_REPORT);
        expected.addErrorsReportEntry("error");

        when(fileDataRetriever.retrieveLinkedCompanyEmployeesData(path)).thenReturn(teamStructure);
        when(generationService.generateReport(teamStructure)).thenThrow(new ReportException("error"));

        //When
        reportService.analyzeAndMakeReportFromFile(path);

        //Then
        verify(reportOutputService).writeErrorReport(expected);
    }

    @Test
    void shouldRetrieveDataGenerateReportsAndProvideThemToOutputService() {
        //Given
        Path path = Path.of("myFolder");
        Map<Long, Employee> teamStructure = Map.of(123L, new Employee(123L, null, null, null, null));
        Report report = new Report(Report.ReportType.REPORTING_LINE_REPORT);


        when(fileDataRetriever.retrieveLinkedCompanyEmployeesData(path)).thenReturn(teamStructure);
        when(generationService.generateReport(teamStructure)).thenReturn(report);

        //When
        reportService.analyzeAndMakeReportFromFile(path);

        //Then
        verify(fileDataRetriever).retrieveLinkedCompanyEmployeesData(path);
        verify(generationService).generateReport(teamStructure);
        verify(reportOutputService).writeReports(List.of(report));
    }

}