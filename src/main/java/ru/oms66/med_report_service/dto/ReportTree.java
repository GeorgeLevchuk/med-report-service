package ru.oms66.med_report_service.dto;

import java.util.List;

public record ReportTree(
        int totalSmoNode,
        List<SmoNode> smoList
) {}