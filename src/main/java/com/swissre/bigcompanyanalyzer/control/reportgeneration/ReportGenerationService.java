package com.swissre.bigcompanyanalyzer.control.reportgeneration;

import com.swissre.bigcompanyanalyzer.entity.Employee;
import com.swissre.bigcompanyanalyzer.entity.Report;

import java.util.Map;

public interface ReportGenerationService {
    /**
     * Generate report based on Company employees structure
     * <p>
     * Improvements: Better to introduce new type for incoming param, to specify that the data must be structured,
     * because now there is no way to check that all employees are really linked to managers
     *
     * @param linkedManagersToSubordinatesStructure Company employees structure
     */
    Report generateReport(Map<Long, Employee> linkedManagersToSubordinatesStructure);

    default Employee findCEO(Map<Long, Employee> idsToEmployees) {
        Employee ceo = null;
        for (var employee : idsToEmployees.values()) {
            var managerId = employee.getManagerId();
            if (managerId == null) {
                if (ceo != null) {
                    throw new GenerationReportException("There are more than 1 CEO in the company structure");
                } else {
                    ceo = employee;
                }
            }
        }
        if (ceo == null) {
            throw new GenerationReportException("There is no CEO in the company structure");
        }
        return ceo;
    }


}
