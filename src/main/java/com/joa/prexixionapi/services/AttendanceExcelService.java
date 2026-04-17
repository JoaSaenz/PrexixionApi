package com.joa.prexixionapi.services;

import com.joa.prexixionapi.dto.AttendanceDTO;
import com.joa.prexixionapi.utils.ExcelStyleManager;
import com.joa.prexixionapi.utils.PoiUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceExcelService {

    public byte[] generateDailyExcel(String fecha, List<AttendanceDTO> data) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            ExcelStyleManager styleManager = new ExcelStyleManager(workbook);
            XSSFSheet sheet = workbook.createSheet("Asistencia Diaria");

            int rowNum = 0;

            // Título
            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(1);
            titleCell.setCellValue("REPORTE DE ASISTENCIA DIARIA - " + fecha);
            titleCell.setCellStyle(styleManager.getHeaderStyle(14));
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 7));

            rowNum++; // Espacio

            // Cabeceras
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {"Personal", "Área", "Puesto", "E. Mañana", "S. Mañana", "E. Tarde", "S. Tarde", "Tardanza (min)"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i + 1);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(styleManager.getSubHeaderStyleBlue(10));
            }

            // Datos
            for (AttendanceDTO item : data) {
                Row row = sheet.createRow(rowNum++);
                
                Cell cellNombre = row.createCell(1);
                cellNombre.setCellValue(item.getApellido() + " " + item.getNombre());
                cellNombre.setCellStyle(styleManager.getDataLeftStyle());

                row.createCell(2).setCellValue(item.getArea());
                row.createCell(2).setCellStyle(styleManager.getDataCenterStyle());

                row.createCell(3).setCellValue(item.getPuesto());
                row.createCell(3).setCellStyle(styleManager.getDataCenterStyle());

                row.createCell(4).setCellValue(item.getMi());
                row.createCell(4).setCellStyle(styleManager.getDataCenterStyle());

                row.createCell(5).setCellValue(item.getMs());
                row.createCell(5).setCellStyle(styleManager.getDataCenterStyle());

                row.createCell(6).setCellValue(item.getTi());
                row.createCell(6).setCellStyle(styleManager.getDataCenterStyle());

                row.createCell(7).setCellValue(item.getTs());
                row.createCell(7).setCellStyle(styleManager.getDataCenterStyle());

                Cell cellTardanza = row.createCell(8);
                cellTardanza.setCellValue(item.getMinutosTardanza());
                if (item.getMinutosTardanza() > 0) {
                    cellTardanza.setCellStyle(styleManager.getFondoLightRedStyle());
                } else {
                    cellTardanza.setCellStyle(styleManager.getDataCenterStyle());
                }
            }

            // Autoajustar columnas (selectivo para performance)
            for (int i = 1; i <= headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            return bos.toByteArray();
        }
    }
}
