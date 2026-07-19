package ru.oms66.med_report_service.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.oms66.med_report_service.service.ReportGenerationUseCase;

@RestController
public class ReportController {

    private static final MediaType XLSX_MEDIA_TYPE = MediaType.parseMediaType(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    private static final MediaType ZIP_MEDIA_TYPE = MediaType.parseMediaType("application/zip");

    private final ReportGenerationUseCase reportGenerationUseCase;

    public ReportController(ReportGenerationUseCase reportGenerationUseCase) {
        this.reportGenerationUseCase = reportGenerationUseCase;
    }

    @GetMapping("/api/report")
    public ResponseEntity<byte[]> generateReport(
            @RequestParam(name = "zip", defaultValue = "false") boolean zip
    ) {
        if (zip) {
            byte[] archive = reportGenerationUseCase.generateZip();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.zip")
                    .contentType(ZIP_MEDIA_TYPE)
                    .contentLength(archive.length)
                    .body(archive);
        }

        byte[] xlsx = reportGenerationUseCase.generate();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.xlsx")
                .contentType(XLSX_MEDIA_TYPE)
                .contentLength(xlsx.length)
                .body(xlsx);
    }
}
