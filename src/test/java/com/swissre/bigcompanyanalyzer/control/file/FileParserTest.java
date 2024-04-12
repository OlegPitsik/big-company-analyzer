package com.swissre.bigcompanyanalyzer.control.file;

import com.swissre.bigcompanyanalyzer.entity.Employee;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FileParserTest {
    FileParser parser = new FileParser();
    String basicDirectory = "./src/test/resources/files/";

    @Test
    void shouldThrowWhenFileDoesNotExist() {
        //Given
        String fileName = "nonExisted.csv";
        Path path = Path.of(basicDirectory + fileName);

        //When
        Throwable exception = assertThrows(FileException.class, () -> parser.parse(path));

        //Then
        assertEquals("File %s does not exist".formatted(
                path.toString()
        ), exception.getMessage());
    }

    @Test
    void shouldThrowWhenFileHasWrongHeaderName() {
        //Given
        String fileName = "withWrongHeaderName.csv";
        Path path = Path.of(basicDirectory + fileName);

        //When
        Throwable exception = assertThrows(FileException.class, () -> parser.parse(path));

        //Then
        assertEquals("Invalid file structure, headers must be presented on the first line in the next order: (id,firstName,lastName,salary,managerId)",
                exception.getMessage());
    }

    @Test
    void shouldThrowWhenFileHasLessHeaders() {
        //Given
        String fileName = "withLessHeadersColumn.csv";
        Path path = Path.of(basicDirectory + fileName);

        //When
        Throwable exception = assertThrows(FileException.class, () -> parser.parse(path));

        //Then
        assertEquals("Invalid file structure, headers must be presented on the first line in the next order: (id,firstName,lastName,salary,managerId)",
                exception.getMessage());
    }

    @Test
    void shouldThrowWhenFileIsEmpty() {
        //Given
        String fileName = "empty.csv";
        Path path = Path.of(basicDirectory + fileName);

        //When
        Throwable exception = assertThrows(FileException.class, () -> {
            parser.parse(path);
        });

        //Then
        assertEquals("Invalid file structure, headers must be presented on the first line in the next order: (id,firstName,lastName,salary,managerId)",
                exception.getMessage());
    }

    @Test
    void shouldReturnMapOfIdsToEmployees() throws IOException {
        //Given
        Map<Long, Employee> expectedMap = Map.of(
                123L, new Employee(123L, "Joe", "Doe", BigDecimal.valueOf(60000).setScale(2, RoundingMode.HALF_UP), null),
                124L, new Employee(124L, "Martin", "Chekov", BigDecimal.valueOf(45000).setScale(2, RoundingMode.HALF_UP), 123L)
        );

        String fileName = "correct.csv";
        Path path = Path.of(basicDirectory + fileName);

        //When
        Map<Long, Employee> actualMap = parser.parse(path);

        //Then
        assertEquals(actualMap, expectedMap);
    }

    @Test
    void shouldParseFileAndReturnOnlyRowsWithFilledId() throws IOException {
        //Given
        Map<Long, Employee> expectedMap = Map.of(
                123L, new Employee(123L, "Joe", "Doe", BigDecimal.valueOf(60000).setScale(2, RoundingMode.HALF_UP), null)
        );

        String fileName = "withOneRowWithoutId.csv";
        Path path = Path.of(basicDirectory + fileName);

        //When
        Map<Long, Employee> actualMap = parser.parse(path);

        //Then
        assertEquals(actualMap.size(), expectedMap.size());
        assertEquals(actualMap.get(123L), expectedMap.get(123L));
    }

    @Test
    void shouldSkipFieldWhenItIsFilledWithWrongFormat() throws IOException {
        //Given
        Map<Long, Employee> expectedMap = Map.of(
                123L, new Employee(123L, "Joe", "Doe", BigDecimal.valueOf(60000).setScale(2, RoundingMode.HALF_UP), null),
                124L, new Employee(124L, "Martin", "Chekov", null, null)
        );

        String fileName = "withOneRowWithStringInSalaryAndManagerId.csv";
        Path path = Path.of(basicDirectory + fileName);

        //When
        Map<Long, Employee> actualMap = parser.parse(path);

        //Then
        assertEquals(actualMap.size(), expectedMap.size());
        assertEquals(actualMap.get(124L), expectedMap.get(124L));
    }

    @Test
    void shouldSkipRowsWithWrongColumnAmount() throws IOException {
        //Given
        Map<Long, Employee> expectedMap = Map.of(
                124L, new Employee(124L, "Martin", "Chekov", BigDecimal.valueOf(6000).setScale(2, RoundingMode.HALF_UP), 12L)
        );

        String fileName = "withOneRowWithLessColumnThenNeeded.csv";
        Path path = Path.of(basicDirectory + fileName);

        //When
        Map<Long, Employee> actualMap = parser.parse(path);

        //Then
        assertEquals(actualMap.size(), expectedMap.size());
        assertEquals(actualMap.get(124L), expectedMap.get(124L));
    }

}