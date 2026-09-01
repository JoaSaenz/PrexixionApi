package com.joa.prexixionapi.services;

import com.joa.prexixionapi.dto.CasoSunatListDTO;
import com.joa.prexixionapi.dto.CasoSunatRequest;
import com.joa.prexixionapi.utils.ExcelStyleManager;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class CasoSunatExcelService {

    private final CasoSunatService casoSunatService;

    public CasoSunatExcelService(CasoSunatService casoSunatService) {
        this.casoSunatService = casoSunatService;
    }

    public byte[] exportarExcel(CasoSunatRequest request) {
        List<CasoSunatListDTO> list = casoSunatService.list(request);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             Workbook wb = new XSSFWorkbook()) {

            ExcelStyleManager styleManager = new ExcelStyleManager((XSSFWorkbook) wb);

            CellStyle dataStyle2 = styleManager.getDataCenterBoldStyle();
            CellStyle fondoGreyStyleLeft = styleManager.getDataLeftStyle();
            CellStyle subHeaderStyle = styleManager.getSubHeaderStyle();
            CellStyle cabeceraStyle = styleManager.getGenericStyle(ExcelStyleManager.GERENCIA_BLUE_RGB, ExcelStyleManager.WHITE_RGB, 16, true, HorizontalAlignment.CENTER);

            String sheetName = "GESTION_FISCALIZACIONES";
            Sheet sheet = wb.createSheet(sheetName);

            int rowNum = 0;

            // CABECERA PRINCIPAL
            Row cabecera = sheet.createRow(rowNum);
            cabecera.setHeightInPoints(sheet.getDefaultRowHeightInPoints() * 2.5f);
            Cell cellCabecera = cabecera.createCell(0);
            cellCabecera.setCellStyle(cabeceraStyle);
            cellCabecera.setCellValue("GESTIÓN FISCALIZACIONES (CASOS SUNAT)");
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 11));
            rowNum++;
            rowNum++; // Espacio

            // HEADERS TABLA
            Row subHeader1 = sheet.createRow(rowNum);
            String[] headers = {
                    "N°", "RUC", "RAZÓN SOCIAL", "TIPO CASO", "MODALIDAD",
                    "TRIBUTO", "MOTIVO", "TIPO PERÍODO", "PERÍODO",
                    "COORD. TAX", "COORD. FIR", "AVANCE (%)"
            };

            int colNum = 0;
            for (String header : headers) {
                createCell(subHeader1, colNum++, header, subHeaderStyle);
            }
            int inicioFilt = rowNum;
            rowNum++;

            // DATOS
            int i = 1;
            for (CasoSunatListDTO obj : list) {
                Row data = sheet.createRow(rowNum);
                colNum = 0;

                createCell(data, colNum++, String.valueOf(i), subHeaderStyle);
                createCell(data, colNum++, obj.getIdEmpresa(), dataStyle2);
                createCell(data, colNum++, obj.getRazonSocial(), fondoGreyStyleLeft);
                createCell(data, colNum++, obj.getDescTipoCaso(), dataStyle2);
                createCell(data, colNum++, obj.getDescModalidad(), dataStyle2);
                createCell(data, colNum++, obj.getDescTributo(), dataStyle2);
                createCell(data, colNum++, obj.getDescMotivo(), fondoGreyStyleLeft);
                createCell(data, colNum++, obj.getDescTipoPeriodo(), dataStyle2);
                createCell(data, colNum++, obj.getPeriodoTexto(), dataStyle2);

                createCell(data, colNum++, obj.getCoordinacionTax() != null && obj.getCoordinacionTax() == 1 ? "SI" : "NO", dataStyle2);
                createCell(data, colNum++, obj.getCoordinacionFir() != null && obj.getCoordinacionFir() == 1 ? "SI" : "NO", dataStyle2);

                String avanceStr = (obj.getAvance() != null ? obj.getAvance().toString() : "0.00") + "%";
                createCell(data, colNum++, avanceStr, dataStyle2);

                rowNum++;
                i++;
            }

            int finFilt = rowNum - 1;
            if (finFilt >= inicioFilt) {
                sheet.setAutoFilter(new CellRangeAddress(inicioFilt, finFilt, 0, headers.length - 1));
            }
            sheet.createFreezePane(0, inicioFilt + 1);

            // AUTO SIZE
            for (int j = 0; j < headers.length; j++) {
                sheet.autoSizeColumn(j);
            }

            wb.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el excel de Gestión Fiscalizaciones", e);
        }
    }

    private void createCell(Row row, int colIdx, String value, CellStyle style) {
        Cell cell = row.createCell(colIdx);
        cell.setCellValue(value != null ? value : "");
        cell.setCellStyle(style);
    }
}
