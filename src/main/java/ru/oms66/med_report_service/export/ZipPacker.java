package ru.oms66.med_report_service.export;

import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Component
public class ZipPacker {

    public byte[] pack(byte[] content, String entryName) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             ZipOutputStream zip = new ZipOutputStream(out)) {

            zip.putNextEntry(new ZipEntry(entryName));
            zip.write(content);
            zip.closeEntry();
            zip.finish();

            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException("Не удалось упаковать файл в ZIP", e);
        }
    }
}
