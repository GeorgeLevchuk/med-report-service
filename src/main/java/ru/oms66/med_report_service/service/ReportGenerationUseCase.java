package ru.oms66.med_report_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import ru.oms66.med_report_service.dto.ReportRow;
import ru.oms66.med_report_service.dto.ReportTree;
import ru.oms66.med_report_service.export.ExcelReportBuilder;
import ru.oms66.med_report_service.export.ZipPacker;
import ru.oms66.med_report_service.repository.MedRepository;

@Service
public class ReportGenerationUseCase {

    private static final Logger log = LoggerFactory.getLogger(ReportGenerationUseCase.class);
    private static final String XLSX_ENTRY_NAME = "report.xlsx";

    private final MedRepository medRepository;
    private final ReportTreeBuilder reportTreeBuilder;
    private final ExcelReportBuilder excelReportBuilder;
    private final ZipPacker zipPacker;

    public ReportGenerationUseCase(
            MedRepository medRepository,
            ReportTreeBuilder reportTreeBuilder,
            ExcelReportBuilder excelReportBuilder,
            ZipPacker zipPacker
    ) {
        this.medRepository = medRepository;
        this.reportTreeBuilder = reportTreeBuilder;
        this.excelReportBuilder = excelReportBuilder;
        this.zipPacker = zipPacker;
    }

    public byte[] generate() {
        long startedAt = System.currentTimeMillis();

        long t0 = System.currentTimeMillis();
        List<ReportRow> rows = medRepository.fetchFlatRows();
        log.info("Repository: получено {} строк за {} мс", rows.size(), System.currentTimeMillis() - t0);

        long t1 = System.currentTimeMillis();
        ReportTree tree = reportTreeBuilder.build(rows);
        log.info("TreeBuilder: {} СМО, grandTotal={} за {} мс",
                tree.smoList().size(), tree.totalSmoNode(), System.currentTimeMillis() - t1);

        long t2 = System.currentTimeMillis();
        byte[] xlsx = excelReportBuilder.build(tree);
        log.info("ExcelBuilder: {} байт за {} мс", xlsx.length, System.currentTimeMillis() - t2);

        log.info("Отчёт полностью сформирован за {} мс", System.currentTimeMillis() - startedAt);
        return xlsx;
    }

    public byte[] generateZip() {
        byte[] xlsx = generate();

        long t0 = System.currentTimeMillis();
        byte[] zip = zipPacker.pack(xlsx, XLSX_ENTRY_NAME);
        log.info("ZipPacker: {} байт за {} мс", zip.length, System.currentTimeMillis() - t0);

        return zip;
    }
}

