package com.swissre.bigcompanyanalyzer.control.file;

import com.swissre.bigcompanyanalyzer.entity.Employee;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FileDataRetrieverTest {
    FileParser fileParser = mock(FileParser.class);
    FileDataRetriever fileDataRetriever = new FileDataRetriever(fileParser);

    @Test
    void shouldThrowFileExceptionWhenParserThrowsIOException() throws IOException {
        //Given
        Path path = Path.of("myFolder");
        when(fileParser.parse(path)).thenThrow(new IOException("error"));
        String expectedMessage = "Impossible to read file. Additional information: error";

        //When
        Exception exception = assertThrows(FileException.class, () ->
                fileDataRetriever.retrieveLinkedCompanyEmployeesData(path)
        );

        //Then
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void shouldLinkSubordinatesToTheirManagers() throws IOException {
        //Given
        Path path = Path.of("myFolder");

        //And
        Map<Long, Employee> map = Map.of(
                12L, new Employee(12L, null, null, null, null),
                13L, new Employee(13L, null, null, null, 12L),
                14L, new Employee(14L, null, null, null, 12L),
                15L, new Employee(15L, null, null, null, 13L)
        );
        when(fileParser.parse(path)).thenReturn(map);

        //When
        Map<Long, Employee> actualMap = fileDataRetriever.retrieveLinkedCompanyEmployeesData(path);

        //Then
        verify(fileParser).parse(path);

        //And
        assertEquals(2, actualMap.get(12L).getSubordinates().size());
        assertEquals(Set.of(actualMap.get(13L), actualMap.get(14L)),
                Set.copyOf(actualMap.get(12L).getSubordinates()));

        assertEquals(1, actualMap.get(13L).getSubordinates().size());
        assertEquals(List.of(actualMap.get(15L)),
                actualMap.get(13L).getSubordinates());
    }

    @Test
    void shouldThrowFileExceptionWhenEmployeeHasInvalidManagerId() throws IOException {
        //Given
        Path path = Path.of("myFolder");

        //And
        Map<Long, Employee> map = Map.of(
                12L, new Employee(12L, null, null, null, null),
                13L, new Employee(13L, null, null, null, 588L)
        );
        when(fileParser.parse(path)).thenReturn(map);

        String expectedMessage = "Employer with Id: 13 has non-existed manager id 588";

        //When
        Exception exception = assertThrows(FileException.class, () ->
                fileDataRetriever.retrieveLinkedCompanyEmployeesData(path)
        );

        //Then
        assertEquals(expectedMessage, exception.getMessage());
    }
}