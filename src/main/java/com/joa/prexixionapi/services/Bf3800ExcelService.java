package com.joa.prexixionapi.services;

import com.joa.prexixionapi.dto.Bf3800DTO;
import com.joa.prexixionapi.utils.ExcelStyleManager;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class Bf3800ExcelService {

    public byte[] generateExcel(List<Bf3800DTO> data) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("BENEFICIARIO FINAL");
            ExcelStyleManager styleManager = new ExcelStyleManager(workbook);

            // Title
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("REPORTE MENSUAL BENEFICIARIO FINAL (BF3800)");
            titleCell.setCellStyle(styleManager.getHeaderStyle(16));
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 14));

            // Headers
            String[] headers = {
                "N°", "ESTADO", "T.S.", "RUC", "Y", "RAZÓN SOCIAL", "PLE", "PRICO", 
                "RT", "PERIODO", "F. REGISTRO", "CONSTANCIA", "REGISTRADO", "MAIL", "OBSERVACIÓN"
            };
            Row headerRow = sheet.createRow(2);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(styleManager.getSubHeaderStyleBlue());
            }

            // Data
            int rowIdx = 3;
            int n = 1;
            for (Bf3800DTO item : data) {
                Row row = sheet.createRow(rowIdx++);
                
                row.createCell(0).setCellValue(n++);
                row.createCell(1).setCellValue(item.getEstado());
                row.createCell(2).setCellValue(item.getTipoServicioAbr());
                row.createCell(3).setCellValue(item.getIdCliente());
                row.createCell(4).setCellValue(item.getY());
                row.createCell(5).setCellValue(item.getRazonSocial());
                row.createCell(6).setCellValue(item.getPle());
                row.createCell(7).setCellValue(item.getPrico());
                row.createCell(8).setCellValue(item.getRegimenTributario());
                row.createCell(9).setCellValue(item.getAnio() + "-" + item.getMes());
                
                boolean hasReg = item.getRegistros() != null && !item.getRegistros().isEmpty();
                if (hasReg) {
                    var reg = item.getRegistros().get(item.getRegistros().size() - 1);
                    row.createCell(10).setCellValue(reg.getFecha());
                    row.createCell(11).setCellValue(reg.getNroOrden());
                    row.createCell(12).setCellValue("SI");
                } else {
                    row.createCell(10).setCellValue("");
                    row.createCell(11).setCellValue("");
                    row.createCell(12).setCellValue("NO");
                }
                
                row.createCell(13).setCellValue(item.getMail() != null && item.getMail() == 1 ? "SI" : "NO");
                row.createCell(14).setCellValue(item.getObservacion());

                // Apply basic data style
                for (int i = 0; i < headers.length; i++) {
                    Cell c = row.getCell(i);
                    if (c != null) {
                        c.setCellStyle(styleManager.getFondoWhiteStyleCenter());
                    }
                }
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }
}
