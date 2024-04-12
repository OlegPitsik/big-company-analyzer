package com.swissre.bigcompanyanalyzer.control.file;

import com.swissre.bigcompanyanalyzer.entity.Employee;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class FileDataRetriever {
    private final FileParser fileParser;

    public FileDataRetriever(FileParser fileParser) {
        this.fileParser = fileParser;
    }

    public Map<Long, Employee> retrieveLinkedCompanyEmployeesData(Path path) {
        try {
            var idsToEmployees = fileParser.parse(path);
            this.linkSubordinatesWithManagers(idsToEmployees);
            return idsToEmployees;
        } catch (IOException ex) {
            throw new FileException("Impossible to read file. Additional information: %s"
                    .formatted(ex.getMessage()));
        }
    }

    private void linkSubordinatesWithManagers(Map<Long, Employee> idToEmployeeMap) {
        for (var employee : idToEmployeeMap.values()) {
            var managerId = employee.getManagerId();
            if (managerId != null) {
                if (idToEmployeeMap.containsKey(managerId)) {
                    idToEmployeeMap.get(managerId)
                            .addSubordinate(employee);
                } else {
                    throw new FileException("Employer with Id: %d has non-existed manager id %d".formatted(
                            employee.getId(), managerId
                    ));
                }
            }
        }
    }

}
