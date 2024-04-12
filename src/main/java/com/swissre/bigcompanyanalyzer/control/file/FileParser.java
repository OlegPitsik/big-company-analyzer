package com.swissre.bigcompanyanalyzer.control.file;

import com.swissre.bigcompanyanalyzer.entity.Employee;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * File parser, works only with CSV files, with comma delimiter.
 * File must have 5 columns, headers must have special order:
 * <b>id, firstName, lastName, salary, managerId</b>
 * Names of the headers are case-insensitive
 * <p>
 * Author: Oleg Pitsik
 */
public class FileParser {

    private static final String FILE_DELIMITER = ",";

    /**
     * Parse file and return employee structure as a Map of ids and Employees.
     * <p>
     * Improvements: return new object ParsingResult with map and errors, put in error all invalid rows
     * (with incorrect format, or without ids)
     * Then it is possible to add all errors to the report.
     *
     * @param path Path to the file
     */
    public Map<Long, Employee> parse(Path path) throws IOException {
        if (!Files.exists(path)) {
            throw new FileException("File %s does not exist".formatted(path.toString()));
        }
        try (var headersStream = Files.lines(path);
             var rowsStream = Files.lines(path).skip(1)
        ) {
            var headers = headersStream.findFirst()
                    .orElse(null);
            if (!checkHeaders(headers)) {
                throw new FileException("Invalid file structure, headers must be presented on the first line in the next order: (%s)"
                        .formatted(Column.getNamesByOrder())
                );
            }
            return rowsStream.map(this::convert)
                    .filter(Objects::nonNull)
                    .filter(employee -> Objects.nonNull(employee.getId()))
                    .collect(Collectors.toMap(Employee::getId, Function.identity()));
        }
    }

    private Employee convert(String row) {
        if (row == null || row.length() == 0) {
            return null;
        }
        String[] arr = row.split(FILE_DELIMITER, -1);
        if (arr.length != Column.values().length) {
            return null;
        }

        var id = this.convertToLong(arr[Column.ID.index].trim());
        var firstName = arr[Column.FIRST_NAME.index].trim();
        var lastName = arr[Column.LAST_NAME.index].trim();
        var salary = this.convertToBigDecimalWithScale(arr[Column.SALARY.index].trim());
        var managerId = this.convertToLong(arr[Column.MANAGER_ID.index].trim());

        return new Employee(id, firstName, lastName, salary, managerId);
    }

    private Long convertToLong(String rawValue) {
        if (rawValue.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(rawValue);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private BigDecimal convertToBigDecimalWithScale(String rawValue) {
        if (rawValue.isBlank()) {
            return null;
        }
        try {
            BigDecimal number = new BigDecimal(rawValue);
            return number.setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private boolean checkHeaders(String s) {
        if (s == null || s.length() == 0) {
            return false;
        }
        String[] arr = s.split(FILE_DELIMITER);
        if (arr.length != Column.values().length) {
            return false;
        }
        return arr[Column.ID.index].equalsIgnoreCase(Column.ID.name)
                && arr[Column.FIRST_NAME.index].equalsIgnoreCase(Column.FIRST_NAME.name)
                && arr[Column.LAST_NAME.index].equalsIgnoreCase(Column.LAST_NAME.name)
                && arr[Column.SALARY.index].equalsIgnoreCase(Column.SALARY.name)
                && arr[Column.MANAGER_ID.index].equalsIgnoreCase(Column.MANAGER_ID.name);
    }

    private enum Column {
        ID(0, "id"),
        FIRST_NAME(1, "firstName"),
        LAST_NAME(2, "lastName"),
        SALARY(3, "salary"),
        MANAGER_ID(4, "managerId");

        private final int index;
        private final String name;

        Column(int index, String name) {
            this.index = index;
            this.name = name;
        }

        static String getNamesByOrder() {
            StringBuilder sb = new StringBuilder();
            Column[] columns = Column.values();
            for (Column column : columns) {
                sb.append(column.name);
                sb.append(',');
            }
            sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        }

    }
}
