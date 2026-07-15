package ru.oms66.med_report_service.dto;

public record ReportRow(
        int smoCode,
        String smoName,
        int moCode,
        String moName,
        String mkbCode,
        String mkbName,
        int count
) {}