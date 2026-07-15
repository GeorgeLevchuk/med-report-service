package ru.oms66.med_report_service.dto;

import java.util.List;

public record SmoNode(
        int code,
        String name,
        int totalMoNode,
        List<MoNode> moList
) {}
