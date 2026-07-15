package ru.oms66.med_report_service.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.oms66.med_report_service.dto.ReportRow;

import java.util.List;

@Repository
public class MedRepository {

    private final JdbcTemplate jdbcTemplate;

    public MedRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String SQL = """
            SELECT
                s.code  AS smo_code,
                s.name  AS smo_name,
                mo.code AS mo_code,
                mo.name AS mo_name,
                k.code  AS mkb_code,
                k.name  AS mkb_name,
                COUNT(*) AS cnt
            FROM med m
            JOIN smo s
                ON s.code = m.cont
               AND m.date_2 BETWEEN s.dbegin AND s.dend
            JOIN mo
                ON mo.code = m.mo
               AND m.date_2 BETWEEN mo.dbegin AND mo.dend
            JOIN mkb k
                ON k.code = m.ds1
               AND m.date_2 BETWEEN k.dbegin AND k.dend
            GROUP BY s.code, mo.code, k.code
            ORDER BY s.code, mo.code, k.code
            """;

    public List<ReportRow> fetchFlatRows() {
        return jdbcTemplate.query(SQL, (rs, rowNum) -> new ReportRow(
                rs.getInt("smo_code"),
                rs.getString("smo_name"),
                rs.getInt("mo_code"),
                rs.getString("mo_name"),
                rs.getString("mkb_code"),
                rs.getString("mkb_name"),
                rs.getInt("cnt")
        ));
    }
}