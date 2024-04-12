package com.swissre.bigcompanyanalyzer.control.reportgeneration;

import com.swissre.bigcompanyanalyzer.entity.Employee;
import com.swissre.bigcompanyanalyzer.entity.Report;

import java.util.Map;

public interface ReportGenerationService {
    Report generateReport(Map<Long, Employee> idsToEmployees);

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
