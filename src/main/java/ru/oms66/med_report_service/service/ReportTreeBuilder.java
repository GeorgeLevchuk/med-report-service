package ru.oms66.med_report_service.service;

import org.springframework.stereotype.Service;
import ru.oms66.med_report_service.dto.MkbNode;
import ru.oms66.med_report_service.dto.MoNode;
import ru.oms66.med_report_service.dto.ReportRow;
import ru.oms66.med_report_service.dto.ReportTree;
import ru.oms66.med_report_service.dto.SmoNode;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class ReportTreeBuilder {

    public ReportTree build(List<ReportRow> rows) {
        Map<Integer, List<ReportRow>> bySmo = rows.stream()
                .collect(Collectors.groupingBy(
                        ReportRow::smoCode, TreeMap::new, Collectors.toList()));

        List<SmoNode> smoNodes = bySmo.entrySet().stream()
                .map(entry -> buildSmoNode(entry.getKey(), entry.getValue()))
                .toList();

        int totalMoNode = smoNodes.stream()
                .mapToInt(SmoNode::totalMoNode)
                .sum();

        return new ReportTree(totalMoNode, smoNodes);
    }

    private SmoNode buildSmoNode(int smoCode, List<ReportRow> smoRows) {
        String smoName = smoRows.get(0).smoName();

        Map<Integer, List<ReportRow>> byMo = smoRows.stream()
                .collect(Collectors.groupingBy(
                        ReportRow::moCode, TreeMap::new, Collectors.toList()));

        List<MoNode> moNodes = byMo.entrySet().stream()
                .map(entry -> buildMoNode(entry.getKey(), entry.getValue()))
                .toList();

        int totalMkbNode = moNodes.stream()
                .mapToInt(MoNode::totalMkbNode)
                .sum();

        return new SmoNode(smoCode, smoName, totalMkbNode, moNodes);
    }

    private MoNode buildMoNode(int moCode, List<ReportRow> moRows) {
        String moName = moRows.get(0).moName();

        List<MkbNode> mkbNodes = moRows.stream()
                .sorted(Comparator.comparing(ReportRow::mkbCode))
                .map(r -> new MkbNode(r.mkbCode(), r.mkbName(), r.count()))
                .toList();

        int count = mkbNodes.stream()
                .mapToInt(MkbNode::count)
                .sum();

        return new MoNode(moCode, moName, count, mkbNodes);
    }
}