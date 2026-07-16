package ru.oms66.med_report_service.export;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Component;
import ru.oms66.med_report_service.dto.MkbNode;
import ru.oms66.med_report_service.dto.MoNode;
import ru.oms66.med_report_service.dto.ReportTree;
import ru.oms66.med_report_service.dto.SmoNode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;

@Component
public class ExcelReportBuilder {

    private static final String SHEET_NAME = "Отчет";
    private static final int COL_TYPE = 0;
    private static final int COL_CODE = 1;
    private static final int COL_NAME = 2;
    private static final int COL_COUNT = 3;

    public byte[] build(ReportTree tree) {

        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) {
            SXSSFSheet sheet = workbook.createSheet(SHEET_NAME);

            sheet.trackAllColumnsForAutoSizing();

            RowStyles styles = RowStyles.create(workbook);

            int rowIdx = writeTitleHeader(sheet, styles);
            rowIdx = writeColumnNumbersRow(sheet, styles, rowIdx);
            sheet.setAutoFilter(new CellRangeAddress(rowIdx - 1, rowIdx - 1, COL_TYPE, COL_COUNT));

            rowIdx = writeTotalRow(sheet, styles, rowIdx, tree.totalSmoNode());

            for (SmoNode smo : tree.smoList()) {
                rowIdx = writeSmoRow(sheet, styles, rowIdx, smo);
                for (MoNode mo : smo.moList()) {
                    rowIdx = writeMoRow(sheet, styles, rowIdx, mo);
                    for (MkbNode mkb : mo.mkbList()) {
                        rowIdx = writeMkbRow(sheet, styles, rowIdx, mkb);
                    }
                }
            }

            for (int col = COL_TYPE; col <= COL_COUNT; col++) {
                sheet.autoSizeColumn(col);
            }

            return toBytes(workbook);
        } catch (IOException e) {
            throw new UncheckedIOException("Не удалось сформировать xlsx-отчёт", e);
        }
    }

    private int writeTitleHeader(SXSSFSheet sheet, RowStyles styles) {
        Row row = sheet.createRow(0);
        writeCell(row, COL_TYPE, "Тип строки\n0 – итого, 1 – СМО, 2 – МО, 3 – МКБ", styles.header());
        writeCell(row, COL_CODE, "Код", styles.header());
        writeCell(row, COL_NAME, "Наименование", styles.header());
        writeCell(row, COL_COUNT, "Количество случаев", styles.header());
        return 1;
    }

    private int writeColumnNumbersRow(SXSSFSheet sheet, RowStyles styles, int rowIdx) {
        Row row = sheet.createRow(rowIdx);
        writeCell(row, COL_TYPE, 1, styles.header());
        writeCell(row, COL_CODE, 2, styles.header());
        writeCell(row, COL_NAME, 3, styles.header());
        writeCell(row, COL_COUNT, 4, styles.header());
        return rowIdx + 1;
    }

    private int writeTotalRow(SXSSFSheet sheet, RowStyles styles, int rowIdx, int grandTotal) {
        Row row = sheet.createRow(rowIdx);
        writeCell(row, COL_TYPE, 0, styles.type0());
        writeCell(row, COL_CODE, "", styles.type0());
        writeCell(row, COL_NAME, "Итого:", styles.type0());
        writeCell(row, COL_COUNT, grandTotal, styles.type0());
        return rowIdx + 1;
    }

    private int writeSmoRow(SXSSFSheet sheet, RowStyles styles, int rowIdx, SmoNode smo) {
        Row row = sheet.createRow(rowIdx);
        writeCell(row, COL_TYPE, 1, styles.type1());
        writeCell(row, COL_CODE, smo.code(), styles.type1());
        writeCell(row, COL_NAME, smo.name(), styles.type1());
        writeCell(row, COL_COUNT, smo.totalMoNode(), styles.type1());
        return rowIdx + 1;
    }

    private int writeMoRow(SXSSFSheet sheet, RowStyles styles, int rowIdx, MoNode mo) {
        Row row = sheet.createRow(rowIdx);
        writeCell(row, COL_TYPE, 2, styles.type2());
        writeCell(row, COL_CODE, mo.code(), styles.type2());
        writeCell(row, COL_NAME, mo.name(), styles.type2());
        writeCell(row, COL_COUNT, mo.totalMkbNode(), styles.type2());
        return rowIdx + 1;
    }

    private int writeMkbRow(SXSSFSheet sheet, RowStyles styles, int rowIdx, MkbNode mkb) {
        Row row = sheet.createRow(rowIdx);
        writeCell(row, COL_TYPE, 3, styles.type3());
        writeCell(row, COL_CODE, mkb.code(), styles.type3());
        writeCell(row, COL_NAME, mkb.name(), styles.type3());
        writeCell(row, COL_COUNT, mkb.count(), styles.type3());
        return rowIdx + 1;
    }

    private void writeCell(Row row, int col, String value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void writeCell(Row row, int col, int value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private byte[] toBytes(SXSSFWorkbook workbook) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            workbook.write(out);
            workbook.dispose();
            return out.toByteArray();
        }
    }

    private record RowStyles(
            CellStyle header,
            CellStyle type0,
            CellStyle type1,
            CellStyle type2,
            CellStyle type3
    ) {
        static RowStyles create(SXSSFWorkbook workbook) {
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);

            CellStyle header = workbook.createCellStyle();
            header.setFont(boldFont);
            header.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            header.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            header.setBorderBottom(BorderStyle.THIN);
            header.setWrapText(true);

            CellStyle type0 = workbook.createCellStyle();
            type0.setFont(boldFont);
            type0.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            type0.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle type1 = workbook.createCellStyle();
            type1.setFont(boldFont);
            type1.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
            type1.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle type2 = workbook.createCellStyle();
            type2.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            type2.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle type3 = workbook.createCellStyle();

            return new RowStyles(header, type0, type1, type2, type3);
        }
    }
}
