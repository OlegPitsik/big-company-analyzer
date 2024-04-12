# Big Company Analyzer

This application is developed as an interview task.

## Description

This application generates reports based on a file representing the employee structure of a company. Each employee has an ID of their manager, except for the CEO who does not have a manager ID. Reports are printed in the console output.

### Types of Reports

1. Managers earning less than average salary of their subordinates, and by how much.
2. Managers earning more than average salary of their subordinates, and by how much.
3. Employees with a reporting line that is too long, and by how much.

## File Format

The file format is CSV with "," as the delimiter. The first line represents the file headers, and the order of headers must not be violated.

### Format Example

| Id  | firstName | lastName     | salary | managerId |
|-----|-----------|--------------|--------|-----------|
| 123 | Joe       | Doe          | 60000  |           |
| 124 | Martin    | Chekov       | 45000  | 123       |
| 125 | Bob       | Ronstad      | 47000  | 123       |
| 300 | Alice     | Hasacat      | 50000  | 124       |
| 305 | Brett     | Hardleaf     | 34000  | 300       |
 -------------------------------------------------------

Place your own file in the project folder `./src/main/resources/files/`.

## Application Configuration

In the `Application` class, you can modify the following settings:
- Basic directory (default: `./src/main/resources/files/`)
- File name (default: `file.csv`)
- Acceptable reporting line level (default: `4`)
- Minimum percentage of employee salary that a manager should earn (default: less than `120` percent of the average salary)
- Maximum percentage of employee salary that a manager should earn (default: more than `150` percent of the average salary)