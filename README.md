# report-annotation

Generate report file through entity bean annotation

## Code Example

Annotation report bean. name attribute is column label and columnIndex is the order of column in the report

    @ReportField(name = "Brewer", columnIndex = 0)
    private String name;

## Motivation

Using report framework(Jasper etc..) for simple tabular report is overload. Those framework designed for visual report so it requires
undesirable resources for creating simple text list.

When you have well defined report bean, always it becomes tedious task to create individual report by format by hand written code.
So I designed annotation mechanism to populate Java bean to three popular file formats which are CSV, Excel and PDF.

## Installation

report-annotation running on Spring Boot framework with Maven build.
Get the code and then run mvn package. Java version 1.7.X used with Maven 3.1.X

## Tests

To start application, enter 'mvn spring-boot:run", default application shall run on port 8080, you can run below line to run the tests.

To generate CSV report:
curl http://localhost:8080/brewer/contacts.csv

To generate PDF report:
curl http://localhost:8080/brewer/contacts.pdf

To generate Excel report:
curl http://localhost:8080/brewer/contacts.xls

