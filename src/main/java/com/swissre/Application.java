package com.swissre;

import com.swissre.bigcompanyanalyzer.control.ReportService;
import com.swissre.bigcompanyanalyzer.control.file.FileDataRetriever;
import com.swissre.bigcompanyanalyzer.control.file.FileParser;
import com.swissre.bigcompanyanalyzer.control.output.ConsoleOutputReportService;
import com.swissre.bigcompanyanalyzer.control.reportgeneration.ReportingLineReportGenerationService;
import com.swissre.bigcompanyanalyzer.control.reportgeneration.SalaryLevelReportGenerationService;
import com.swissre.bigcompanyanalyzer.facade.Analyzer;

import java.util.List;

/**
 * Main class for starting analysis and providing reports.
 * Currently only possible to analyze CSV files and print reports in the console output.
 * <p>
 * Available configurations:
 * BASIC_DIRECTORY - directory for storing the file with input data
 * FILE_NAME - name of the file
 * ALLOWED_REPORTING_LEVEL - maximum level of the reporting line, if Employee has longer line, his id will be presented in the report
 * MIN_ALLOWED_SALARY_PERCENT - minimal level of managers salary in percents from average salary of their subordinates
 * MAX_ALLOWED_SALARY_PERCENT - maximum level of managers salary in percents from average salary of their subordinates
 * <p>
 * Author: Oleg Pitsik
 */

public class Application {
    //Configs, better move to property file
    private static final String BASIC_DIRECTORY = "./src/main/resources/files/";
    private static final String FILE_NAME = "file.csv";

    private static final int ALLOWED_REPORTING_LEVEL = 4;
    private static final int MIN_ALLOWED_SALARY_PERCENT = 120;
    private static final int MAX_ALLOWED_SALARY_PERCENT = 150;


    public static void main(String[] args) {
        // Context creation, better move to separate Factory
        var fileParser = new FileParser();
        var consoleOutputReportService = new ConsoleOutputReportService();
        var reportGenerationService = new ReportingLineReportGenerationService(ALLOWED_REPORTING_LEVEL);
        var salaryLevelReportGenerationService = new SalaryLevelReportGenerationService(MIN_ALLOWED_SALARY_PERCENT, MAX_ALLOWED_SALARY_PERCENT);
        var fileDataRetriever = new FileDataRetriever(fileParser);
        var reportManager = new ReportService(fileDataRetriever, consoleOutputReportService, List.of(reportGenerationService, salaryLevelReportGenerationService));

        var analyzer = new Analyzer(reportManager);
        analyzer.analyzeFromFile(FILE_NAME, BASIC_DIRECTORY);
    }
}