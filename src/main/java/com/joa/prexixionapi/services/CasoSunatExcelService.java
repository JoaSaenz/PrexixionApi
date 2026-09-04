package com.joa.prexixionapi.services;

import com.joa.prexixionapi.dto.CasoSunatListDTO;
import com.joa.prexixionapi.dto.CasoSunatRequest;
import com.joa.prexixionapi.utils.ExcelStyleManager;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
                XSSFWorkbook wb = new XSSFWorkbook()) {

            ExcelStyleManager styleManager = new ExcelStyleManager(wb);

            // --- COLORES & ESTILOS ---
            byte[] skyBlueBg = ExcelStyleManager.SKYBLUE_GREY_RGB;
            byte[] darkBlueBg = ExcelStyleManager.NAVY_BLUE_HEADER_RGB;
            byte[] whiteRgb = ExcelStyleManager.WHITE_RGB;
            byte[] blackRgb = ExcelStyleManager.BLACK_RGB;
            byte[] matteBlackRgb = ExcelStyleManager.MATTE_BLACK_RGB;
            byte[] greenBg = ExcelStyleManager.VERY_LIGHT_GREEN_RGB;
            byte[] greenText = ExcelStyleManager.DARK_GREEN_TEXT_RGB;
            byte[] redBg = ExcelStyleManager.VERY_LIGHT_RED_RGB;
            byte[] redText = ExcelStyleManager.DARK_RED_TEXT_RGB;

            // Header Title Style (Row 1) - 11pt Bold
            XSSFCellStyle titleStyle = styleManager.getCustomStyle(
                    skyBlueBg, blackRgb, 11, true, HorizontalAlignment.CENTER, BorderStyle.THIN,
                    IndexedColors.GREY_25_PERCENT);

            // SubHeader Style (Rows 2 & 3) - 8pt Bold
            XSSFCellStyle subHeaderStyle = styleManager.getCustomStyle(
                    skyBlueBg, blackRgb, 8, true, HorizontalAlignment.CENTER, BorderStyle.THIN,
                    IndexedColors.GREY_25_PERCENT);

            // Filter Row Style (Fila separada sin color de fondo - Blanco)
            XSSFCellStyle filterRowStyle = styleManager.getCustomStyle(
                    whiteRgb, blackRgb, 8, false, HorizontalAlignment.CENTER, BorderStyle.THIN,
                    IndexedColors.GREY_25_PERCENT);

            // Data Styles - 8pt Normal (igual a TradeClienteController action 9)
            XSSFCellStyle dataCenterStyle = styleManager.getCustomStyle(
                    whiteRgb, matteBlackRgb, 8, false, HorizontalAlignment.CENTER, BorderStyle.THIN,
                    IndexedColors.GREY_25_PERCENT);

            XSSFCellStyle dataLeftStyle = styleManager.getCustomStyle(
                    whiteRgb, matteBlackRgb, 8, false, HorizontalAlignment.LEFT, BorderStyle.THIN,
                    IndexedColors.GREY_25_PERCENT);

            XSSFCellStyle moneyStyle = styleManager.getMoneyStyle(
                    whiteRgb, matteBlackRgb, 8, false, IndexedColors.GREY_25_PERCENT);

            XSSFCellStyle percentStyle = styleManager.getPercentStyle(
                    whiteRgb, matteBlackRgb, 8, false, IndexedColors.GREY_25_PERCENT);

            // Conditional Data Styles - 8pt
            XSSFCellStyle siStyle = styleManager.getCustomStyle(
                    greenBg, greenText, 8, true, HorizontalAlignment.CENTER, BorderStyle.THIN,
                    IndexedColors.GREY_25_PERCENT);

            XSSFCellStyle noStyle = dataCenterStyle;

            XSSFCellStyle estadoPendienteStyle = styleManager.getCustomStyle(
                    redBg, redText, 8, true, HorizontalAlignment.CENTER, BorderStyle.THIN,
                    IndexedColors.GREY_25_PERCENT);

            // Footer Total Styles - 8pt Bold
            XSSFCellStyle footerTotalStyle = styleManager.getCustomStyle(
                    darkBlueBg, whiteRgb, 8, true, HorizontalAlignment.CENTER, BorderStyle.THIN,
                    IndexedColors.GREY_25_PERCENT);

            XSSFCellStyle footerMoneyStyle = styleManager.getMoneyStyle(
                    darkBlueBg, whiteRgb, 8, true, IndexedColors.GREY_25_PERCENT);

            XSSFCellStyle footerEmptyStyle = styleManager.getCustomStyle(
                    darkBlueBg, whiteRgb, 8, true, HorizontalAlignment.CENTER, BorderStyle.THIN,
                    IndexedColors.GREY_25_PERCENT);

            // 1. NOMBRE DE LA HOJA
            Sheet sheet = wb.createSheet("REPORTE");
            sheet.setDisplayGridlines(true);

            int rowNum = 0;

            // 2. FILA TITULO PRINCIPAL
            Row titleRow = sheet.createRow(rowNum++);
            titleRow.setHeightInPoints(24);
            String fechaHoyStr = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("REPORTE FISCALIZACIONES AL  " + fechaHoyStr);
            titleCell.setCellStyle(titleStyle);
            for (int c = 1; c < 16; c++) {
                Cell cell = titleRow.createCell(c);
                cell.setCellStyle(titleStyle);
            }
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 15));

            // 3. FILAS CABECERA TABLA (Filas 2 y 3)
            int headerRowStart = rowNum;
            Row headerRow1 = sheet.createRow(rowNum++);
            Row headerRow2 = sheet.createRow(rowNum++);

            // Inicializar celdas de ambas filas de cabecera con estilo subHeader
            for (int c = 0; c < 16; c++) {
                Cell cell1 = headerRow1.createCell(c);
                cell1.setCellStyle(subHeaderStyle);
                Cell cell2 = headerRow2.createCell(c);
                cell2.setCellStyle(subHeaderStyle);
            }

            // Asignar textos y combinaciones
            // Col A (0): N° -> merge vertical (row1 & row2)
            headerRow1.getCell(0).setCellValue("N°");
            sheet.addMergedRegion(new CellRangeAddress(headerRowStart, headerRowStart + 1, 0, 0));

            // Col B-C (1-2): COORDINADO -> merge horizontal en row1, subheaders en row2
            headerRow1.getCell(1).setCellValue("COORDINADO");
            sheet.addMergedRegion(new CellRangeAddress(headerRowStart, headerRowStart, 1, 2));
            headerRow2.getCell(1).setCellValue("TAX");
            headerRow2.getCell(2).setCellValue("FIR");

            // Resto de columnas (3 a 15) -> merge vertical
            String[] headers = {
                    "RAZÓN SOCIAL", "TIPO CASO", "DOCUMENTO", "MODALIDAD",
                    "TRIBUTO", "PERIODO", "PRESENTACIÓN", "HORA",
                    "MOTIVO", "IMPORTE", "ESTADO", "COMPLETO", "INCOMPLETO"
            };

            for (int i = 0; i < headers.length; i++) {
                int colIdx = 3 + i;
                headerRow1.getCell(colIdx).setCellValue(headers[i]);
                sheet.addMergedRegion(new CellRangeAddress(headerRowStart, headerRowStart + 1, colIdx, colIdx));
            }

            // 4. FILA EXPLICITA DE FILTROS AUTOFILTER (Fila separada después de
            // subcabeceras, ej. fila 18 de la imagen)
            int filterRowIdx = rowNum++;
            Row filterRow = sheet.createRow(filterRowIdx);
            filterRow.setHeightInPoints(18);
            for (int c = 0; c < 16; c++) {
                Cell filterCell = filterRow.createCell(c);
                filterCell.setCellValue("");
                filterCell.setCellStyle(filterRowStyle);
            }

            int inicioData = rowNum;

            // 5. DATOS
            int itemNum = 1;
            BigDecimal totalImporte = BigDecimal.ZERO;

            for (CasoSunatListDTO obj : list) {
                Row dataRow = sheet.createRow(rowNum++);
                int c = 0;

                // N°
                createCell(dataRow, c++, itemNum++, dataCenterStyle);

                // TAX
                boolean isTaxSi = obj.getCoordinacionTax() != null && obj.getCoordinacionTax() == 1;
                createCell(dataRow, c++, isTaxSi ? "SI" : "NO", isTaxSi ? siStyle : noStyle);

                // FIR
                boolean isFirSi = obj.getCoordinacionFir() != null && obj.getCoordinacionFir() == 1;
                createCell(dataRow, c++, isFirSi ? "SI" : "NO", isFirSi ? siStyle : noStyle);

                // RAZON SOCIAL
                createCell(dataRow, c++, obj.getRazonSocial(), dataLeftStyle);

                // TIPO CASO
                createCell(dataRow, c++, obj.getDescTipoCaso(), dataCenterStyle);

                // DOCUMENTO
                createCell(dataRow, c++, obj.getDescTipoDocumento(), dataCenterStyle);

                // MODALIDAD
                createCell(dataRow, c++, obj.getDescModalidad(), dataCenterStyle);

                // TRIBUTO
                createCell(dataRow, c++, obj.getDescTributo(), dataCenterStyle);

                // PERIODO
                createCell(dataRow, c++, obj.getPeriodoTexto(), dataCenterStyle);

                // PRESENTACION
                createCell(dataRow, c++, obj.getFechaPresentacion(), dataCenterStyle);

                // HORA
                createCell(dataRow, c++, obj.getHora(), dataCenterStyle);

                // MOTIVO
                createCell(dataRow, c++, obj.getDescMotivo(), dataCenterStyle);

                // IMPORTE
                BigDecimal imp = obj.getImporteObservado() != null ? obj.getImporteObservado() : BigDecimal.ZERO;
                totalImporte = totalImporte.add(imp);
                createNumericCell(dataRow, c++, imp.doubleValue(), moneyStyle);

                // ESTADO
                String estadoStr = obj.getDescEstado() != null ? obj.getDescEstado() : "";
                boolean isPendiente = "PENDIENTE".equalsIgnoreCase(estadoStr);
                createCell(dataRow, c++, estadoStr, isPendiente ? estadoPendienteStyle : dataCenterStyle);

                // COMPLETO (%)
                double avanceVal = obj.getAvance() != null ? obj.getAvance().doubleValue() / 100.0 : 0.0;
                createNumericCell(dataRow, c++, avanceVal, percentStyle);

                // INCOMPLETO (%)
                double incompletoVal = 1.0 - avanceVal;
                createNumericCell(dataRow, c++, incompletoVal, percentStyle);
            }

            // 6. FILA DE TOTALES (Pie de Tabla)
            Row totalRow = sheet.createRow(rowNum++);
            for (int col = 0; col < 16; col++) {
                Cell cell = totalRow.createCell(col);
                if (col < 12) {
                    cell.setCellStyle(footerTotalStyle);
                } else if (col == 12) {
                    cell.setCellStyle(footerMoneyStyle);
                } else {
                    cell.setCellStyle(footerEmptyStyle);
                }
            }

            // Merge A-L (0-11) para "TOTAL IMPORTE NOTIFICADO"
            totalRow.getCell(0).setCellValue("TOTAL IMPORTE NOTIFICADO");
            sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 11));

            // Col M (12): Suma total de Importe Notificado
            if (rowNum - 1 > inicioData) {
                String formulaStr = String.format("SUM(M%d:M%d)", inicioData + 1, rowNum - 1);
                totalRow.getCell(12).setCellFormula(formulaStr);
            } else {
                totalRow.getCell(12).setCellValue(totalImporte.doubleValue());
            }

            // 7. AUTOFILTER EN LA FILA EXPLICITA DE FILTROS (filterRowIdx) Y FREEZE PANE
            sheet.setAutoFilter(new CellRangeAddress(filterRowIdx, filterRowIdx, 0, 15));
            sheet.createFreezePane(0, filterRowIdx + 1);

            // 8. AUTO SIZE COLUMNS (100% Dinámico, igual a TradeClienteController.java
            // action 9)
            for (int j = 0; j < 16; j++) {
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

    private void createCell(Row row, int colIdx, int value, CellStyle style) {
        Cell cell = row.createCell(colIdx);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void createNumericCell(Row row, int colIdx, double value, CellStyle style) {
        Cell cell = row.createCell(colIdx);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }
}
