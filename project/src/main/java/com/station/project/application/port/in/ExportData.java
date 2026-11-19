package com.station.project.application.port.in;

public interface ExportData {
    byte[] exportCsv();

    byte[] exportExcel();
}
