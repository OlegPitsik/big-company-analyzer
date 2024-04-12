package com.swissre.bigcompanyanalyzer.facade;

import com.swissre.bigcompanyanalyzer.control.ReportService;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


class AnalyzerTest {
    String baseDirectory = "./src/test/resources/files/";

    ReportService reportService = mock(ReportService.class);
    Analyzer analyzer = new Analyzer(reportService);

    @Test
    void shouldCreatePathWithFileNameAndPassToFileAnalyzer() {
        //Given
        String fileName = "someFile";
        Path expectedPath = Path.of(baseDirectory + fileName);

        //When
        analyzer.analyzeFromFile(fileName, baseDirectory);

        //Then
        verify(reportService).analyzeAndMakeReportFromFile(expectedPath);
    }

}