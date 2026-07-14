package ru.oms66.med_report_service.config;

import org.sqlite.SQLiteDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;
import java.io.File;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource dataSource(@Value("${app.db.path}") String dbPath) {
        File dbFile = new File(dbPath);
        if (!dbFile.exists()) {
            throw new IllegalStateException(
                    "База данных SQLite не найдена по адресу: " + dbFile.getAbsolutePath() +
                            ". Установите переменную окружения MED_DB_PATH или поместите файл в папку ./data/test-data.db3");
        }
        SQLiteDataSource ds = new SQLiteDataSource();
        ds.setUrl("jdbc:sqlite:" + dbFile.getAbsolutePath());
        return ds;
    }
}