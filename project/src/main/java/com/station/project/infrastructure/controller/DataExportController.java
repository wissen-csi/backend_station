package com.station.project.infrastructure.controller;

import java.nio.charset.StandardCharsets;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.station.project.application.port.in.ExportData;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/data/export")
@RequiredArgsConstructor
public class DataExportController {

    private final ExportData dataExport;

    @GetMapping(value = "/csv", produces = "text/csv")
    public ResponseEntity<byte[]> exportCsv() {
        byte[] file = dataExport.exportCsv();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename("meteorological-data.csv").build().toString())
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .contentLength(file.length)
                .body(file);
    }

    @GetMapping(value = "/excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> exportExcel() {
        byte[] file = dataExport.exportExcel();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename("meteorological-data.xlsx").build().toString())
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(file.length)
                .body(file);
    }
}
