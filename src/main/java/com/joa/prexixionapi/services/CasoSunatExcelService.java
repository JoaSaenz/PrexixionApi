package com.joa.prexixionapi.services;

import com.joa.prexixionapi.dto.CasoSunatListDTO;
import com.joa.prexixionapi.dto.CasoSunatRequest;
import com.joa.prexixionapi.dto.CasoSunatSeguimientoDTO;
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
import java.util.*;
import java.util.stream.Collectors;

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
                    whiteRgb, blackRgb, 8, false, HorizontalAlignment.CENTER, BorderStyle.THIN,
                    IndexedColors.GREY_25_PERCENT);

            XSSFCellStyle dataLeftStyle = styleManager.getCustomStyle(
                    whiteRgb, blackRgb, 8, false, HorizontalAlignment.LEFT, BorderStyle.THIN,
                    IndexedColors.GREY_25_PERCENT);

            XSSFCellStyle moneyStyle = styleManager.getMoneyStyle(
                    whiteRgb, blackRgb, 8, false, IndexedColors.GREY_25_PERCENT);

            XSSFCellStyle percentStyle = styleManager.getPercentStyle(
                    whiteRgb, blackRgb, 8, false, IndexedColors.GREY_25_PERCENT);

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
                createCell(dataRow, c++, itemNum++, subHeaderStyle);

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

            // 8. AUTO SIZE COLUMNS con padding y ancho mínimo para evitar recorte en
            // cabeceras combinadas
            for (int j = 0; j < 16; j++) {
                sheet.autoSizeColumn(j);
                int currentWidth = sheet.getColumnWidth(j);
                int minWidth = 3200;
                sheet.setColumnWidth(j, Math.max(currentWidth + 768, minWidth));
            }

            wb.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el excel de Gestión Fiscalizaciones", e);
        }
    }

    public byte[] exportarExcelSeguimiento(CasoSunatRequest request) {
        List<CasoSunatSeguimientoDTO> list = casoSunatService.listSeguimiento(request);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             XSSFWorkbook wb = new XSSFWorkbook()) {

            ExcelStyleManager styleManager = new ExcelStyleManager(wb);

            // --- PALETA DE COLORES ---
            byte[] darkBlueBg = ExcelStyleManager.NAVY_BLUE_HEADER_RGB;
            byte[] skyBlueBg = ExcelStyleManager.SKYBLUE_GREY_RGB;
            byte[] whiteRgb = ExcelStyleManager.WHITE_RGB;
            byte[] blackRgb = ExcelStyleManager.BLACK_RGB;
            byte[] matteBlackRgb = ExcelStyleManager.MATTE_BLACK_RGB;
            byte[] greenBg = ExcelStyleManager.VERY_LIGHT_GREEN_RGB;
            byte[] greenText = ExcelStyleManager.DARK_GREEN_TEXT_RGB;
            byte[] redBg = ExcelStyleManager.VERY_LIGHT_RED_RGB;
            byte[] redText = ExcelStyleManager.DARK_RED_TEXT_RGB;

            // Colores específicos por Tipo de Documento
            byte[] celesteBg = {(byte) 222, (byte) 235, (byte) 247}; // #DEEBF7
            byte[] celesteText = {(byte) 31, (byte) 78, (byte) 121}; // #1F4E79
            byte[] redPastelBg = {(byte) 252, (byte) 228, (byte) 228}; // #FCE4E4
            byte[] yellowPastelBg = {(byte) 255, (byte) 242, (byte) 204}; // #FFF2CC
            byte[] orangeText = {(byte) 198, (byte) 89, (byte) 17}; // #C65911

            // Estilos generales de cabecera
            XSSFCellStyle titleStyle = styleManager.getCustomStyle(
                    darkBlueBg, whiteRgb, 11, true, HorizontalAlignment.CENTER, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);

            XSSFCellStyle subHeaderStyle = styleManager.getCustomStyle(
                    skyBlueBg, blackRgb, 8, true, HorizontalAlignment.CENTER, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);

            XSSFCellStyle filterRowStyle = styleManager.getCustomStyle(
                    whiteRgb, blackRgb, 8, false, HorizontalAlignment.CENTER, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);

            // Estilos estáticos de la parte "Caso" (Cols A-E)
            XSSFCellStyle casoCenterStyle = styleManager.getCustomStyle(
                    whiteRgb, blackRgb, 8, true, HorizontalAlignment.CENTER, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);

            XSSFCellStyle casoLeftStyle = styleManager.getCustomStyle(
                    whiteRgb, blackRgb, 8, true, HorizontalAlignment.LEFT, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);

            XSSFCellStyle siStyle = styleManager.getCustomStyle(
                    greenBg, greenText, 8, true, HorizontalAlignment.CENTER, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);

            XSSFCellStyle noStyle = casoCenterStyle;

            // Estilos para ESTADO
            XSSFCellStyle estadoPendienteStyle = styleManager.getCustomStyle(
                    redBg, redText, 8, true, HorizontalAlignment.CENTER, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);

            XSSFCellStyle estadoPresentadoStyle = styleManager.getCustomStyle(
                    greenBg, greenText, 8, true, HorizontalAlignment.CENTER, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);

            XSSFCellStyle estadoDefaultStyle = styleManager.getCustomStyle(
                    whiteRgb, matteBlackRgb, 8, true, HorizontalAlignment.CENTER, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);

            // Estilos dinámicos por tipo de documento
            // 1. Celeste (Carta de Presentación y Esquela)
            XSSFCellStyle celesteDocTextStyle = styleManager.getCustomStyle(
                    celesteBg, celesteText, 8, true, HorizontalAlignment.LEFT, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);
            XSSFCellStyle celesteCenterStyle = styleManager.getCustomStyle(
                    celesteBg, matteBlackRgb, 8, false, HorizontalAlignment.CENTER, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);
            XSSFCellStyle celesteLeftStyle = styleManager.getCustomStyle(
                    celesteBg, matteBlackRgb, 8, false, HorizontalAlignment.LEFT, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);
            XSSFCellStyle celesteMoneyStyle = styleManager.getMoneyStyle(
                    celesteBg, matteBlackRgb, 8, false, IndexedColors.GREY_25_PERCENT);

            // 2. Rojo Pastel (Reclamo)
            XSSFCellStyle redDocTextStyle = styleManager.getCustomStyle(
                    redPastelBg, redText, 8, true, HorizontalAlignment.LEFT, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);
            XSSFCellStyle redCenterStyle = styleManager.getCustomStyle(
                    redPastelBg, matteBlackRgb, 8, false, HorizontalAlignment.CENTER, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);
            XSSFCellStyle redLeftStyle = styleManager.getCustomStyle(
                    redPastelBg, matteBlackRgb, 8, false, HorizontalAlignment.LEFT, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);
            XSSFCellStyle redMoneyStyle = styleManager.getMoneyStyle(
                    redPastelBg, matteBlackRgb, 8, false, IndexedColors.GREY_25_PERCENT);

            // 3. Amarillo Pastel (Apelación)
            XSSFCellStyle yellowDocTextStyle = styleManager.getCustomStyle(
                    yellowPastelBg, orangeText, 8, true, HorizontalAlignment.LEFT, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);
            XSSFCellStyle yellowCenterStyle = styleManager.getCustomStyle(
                    yellowPastelBg, matteBlackRgb, 8, false, HorizontalAlignment.CENTER, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);
            XSSFCellStyle yellowLeftStyle = styleManager.getCustomStyle(
                    yellowPastelBg, matteBlackRgb, 8, false, HorizontalAlignment.LEFT, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);
            XSSFCellStyle yellowMoneyStyle = styleManager.getMoneyStyle(
                    yellowPastelBg, matteBlackRgb, 8, false, IndexedColors.GREY_25_PERCENT);

            // 4. Default White
            XSSFCellStyle whiteDocTextStyle = styleManager.getCustomStyle(
                    whiteRgb, matteBlackRgb, 8, true, HorizontalAlignment.LEFT, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);
            XSSFCellStyle whiteCenterStyle = styleManager.getCustomStyle(
                    whiteRgb, matteBlackRgb, 8, false, HorizontalAlignment.CENTER, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);
            XSSFCellStyle whiteLeftStyle = styleManager.getCustomStyle(
                    whiteRgb, matteBlackRgb, 8, false, HorizontalAlignment.LEFT, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);
            XSSFCellStyle whiteMoneyStyle = styleManager.getMoneyStyle(
                    whiteRgb, matteBlackRgb, 8, false, IndexedColors.GREY_25_PERCENT);

            // --- HOJA Y CABECERA ---
            Sheet sheet = wb.createSheet("REPORTE DE SEGUIMIENTO");
            sheet.setDisplayGridlines(true);

            int rowNum = 0;

            // 1. FILA TITULO PRINCIPAL
            Row titleRow = sheet.createRow(rowNum++);
            titleRow.setHeightInPoints(26);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("REPORTE DE SEGUIMIENTO - REQUERIMIENTO, ESQUELAS Y PROCESOS IMPUGNATORIOS");
            titleCell.setCellStyle(titleStyle);
            for (int c = 1; c < 21; c++) {
                Cell cell = titleRow.createCell(c);
                cell.setCellStyle(titleStyle);
            }
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 20));

            // 2. SUBCABECERAS (Filas 2 y 3 -> index 1 y 2)
            int headerRowStart = rowNum;
            Row headerRow1 = sheet.createRow(rowNum++);
            Row headerRow2 = sheet.createRow(rowNum++);

            for (int c = 0; c < 21; c++) {
                Cell c1 = headerRow1.createCell(c);
                c1.setCellStyle(subHeaderStyle);
                Cell c2 = headerRow2.createCell(c);
                c2.setCellStyle(subHeaderStyle);
            }

            // Col 0 (A): N°
            headerRow1.getCell(0).setCellValue("N°");
            sheet.addMergedRegion(new CellRangeAddress(headerRowStart, headerRowStart + 1, 0, 0));

            // Col 1-2 (B-C): COORDINADO
            headerRow1.getCell(1).setCellValue("COORDINADO");
            sheet.addMergedRegion(new CellRangeAddress(headerRowStart, headerRowStart, 1, 2));
            headerRow2.getCell(1).setCellValue("TAX");
            headerRow2.getCell(2).setCellValue("FIR");

            // Cols 3 a 11 (D a L) -> Merge vertical
            String[] headersMid1 = {"RAZÓN SOCIAL", "TIPO CASO", "DOCUMENTO", "N° DE DOCUMENTO", "MODALIDAD", "TRIBUTO", "MOTIVO", "PERIODO", "AUDITOR"};
            for (int i = 0; i < headersMid1.length; i++) {
                int colIdx = 3 + i;
                headerRow1.getCell(colIdx).setCellValue(headersMid1[i]);
                sheet.addMergedRegion(new CellRangeAddress(headerRowStart, headerRowStart + 1, colIdx, colIdx));
            }

            // Cols 12-16 (M a Q): FECHAS
            headerRow1.getCell(12).setCellValue("FECHAS");
            sheet.addMergedRegion(new CellRangeAddress(headerRowStart, headerRowStart, 12, 16));
            headerRow2.getCell(12).setCellValue("F. RECEPCION");
            headerRow2.getCell(13).setCellValue("F. ENVIO");
            headerRow2.getCell(14).setCellValue("F. PRESENTACON");
            headerRow2.getCell(15).setCellValue("HORA");
            headerRow2.getCell(16).setCellValue("R. RESULTADO");

            // Cols 17 a 20 (R a U) -> Merge vertical
            String[] headersEnd = {"ESTADO", "IMP. OBSERVADO", "RECTIFIC.", "IMP. RECTIFIC."};
            for (int i = 0; i < headersEnd.length; i++) {
                int colIdx = 17 + i;
                headerRow1.getCell(colIdx).setCellValue(headersEnd[i]);
                sheet.addMergedRegion(new CellRangeAddress(headerRowStart, headerRowStart + 1, colIdx, colIdx));
            }

            // 3. FILA DE FILTROS (Fila 4 -> index 3)
            int filterRowIdx = rowNum++;
            Row filterRow = sheet.createRow(filterRowIdx);
            filterRow.setHeightInPoints(18);
            for (int c = 0; c < 21; c++) {
                Cell filterCell = filterRow.createCell(c);
                filterCell.setCellValue("");
                filterCell.setCellStyle(filterRowStyle);
            }

            // 4. AGRUPACIÓN Y LLENADO DE DATOS POR CASO
            Map<Integer, List<CasoSunatSeguimientoDTO>> groupedMap = list.stream()
                    .collect(Collectors.groupingBy(CasoSunatSeguimientoDTO::getIdCaso, LinkedHashMap::new, Collectors.toList()));

            int itemNum = 1;

            for (Map.Entry<Integer, List<CasoSunatSeguimientoDTO>> entry : groupedMap.entrySet()) {
                List<CasoSunatSeguimientoDTO> docList = entry.getValue();
                int numDocs = docList.size();
                int startRow = rowNum;
                int endRow = rowNum + numDocs - 1;

                for (int dIdx = 0; dIdx < numDocs; dIdx++) {
                    CasoSunatSeguimientoDTO dto = docList.get(dIdx);
                    Row dataRow = sheet.createRow(rowNum++);
                    int c = 0;

                    // Col 0: N°
                    createCell(dataRow, c++, itemNum, subHeaderStyle);

                    // Col 1: TAX
                    boolean isTaxSi = dto.getCoordinacionTax() != null && dto.getCoordinacionTax() == 1;
                    createCell(dataRow, c++, isTaxSi ? "SI" : "NO", isTaxSi ? siStyle : noStyle);

                    // Col 2: FIR
                    boolean isFirSi = dto.getCoordinacionFir() != null && dto.getCoordinacionFir() == 1;
                    createCell(dataRow, c++, isFirSi ? "SI" : "NO", isFirSi ? siStyle : noStyle);

                    // Col 3: RAZON SOCIAL
                    createCell(dataRow, c++, dto.getRazonSocial(), casoLeftStyle);

                    // Col 4: TIPO CASO
                    createCell(dataRow, c++, dto.getDescTipoCaso(), casoCenterStyle);

                    // Determinar estilos según Tipo de Documento
                    String descDoc = dto.getDescTipoDocumento() != null ? dto.getDescTipoDocumento() : "";
                    String descDocUpper = descDoc.toUpperCase().trim();

                    XSSFCellStyle rowDocTextStyle;
                    XSSFCellStyle rowCenterStyle;
                    XSSFCellStyle rowLeftStyle;
                    XSSFCellStyle rowMoneyStyle;

                    if (descDocUpper.contains("CARTA DE PRESENTACIÓN") || descDocUpper.contains("CARTA DE PRESENTACION") || descDocUpper.contains("ESQUELA")) {
                        rowDocTextStyle = celesteDocTextStyle;
                        rowCenterStyle = celesteCenterStyle;
                        rowLeftStyle = celesteLeftStyle;
                        rowMoneyStyle = celesteMoneyStyle;
                    } else if (descDocUpper.contains("RECLAMO")) {
                        rowDocTextStyle = redDocTextStyle;
                        rowCenterStyle = redCenterStyle;
                        rowLeftStyle = redLeftStyle;
                        rowMoneyStyle = redMoneyStyle;
                    } else if (descDocUpper.contains("APELACION") || descDocUpper.contains("APELACIÓN")) {
                        rowDocTextStyle = yellowDocTextStyle;
                        rowCenterStyle = yellowCenterStyle;
                        rowLeftStyle = yellowLeftStyle;
                        rowMoneyStyle = yellowMoneyStyle;
                    } else {
                        rowDocTextStyle = whiteDocTextStyle;
                        rowCenterStyle = whiteCenterStyle;
                        rowLeftStyle = whiteLeftStyle;
                        rowMoneyStyle = whiteMoneyStyle;
                    }

                    // Col 5: DOCUMENTO
                    createCell(dataRow, c++, descDoc, rowDocTextStyle);

                    // Col 6: N° DE DOCUMENTO
                    createCell(dataRow, c++, dto.getNroDocumento(), rowCenterStyle);

                    // Col 7: MODALIDAD
                    createCell(dataRow, c++, dto.getDescModalidad(), rowCenterStyle);

                    // Col 8: TRIBUTO
                    createCell(dataRow, c++, dto.getDescTributo(), rowCenterStyle);

                    // Col 9: MOTIVO
                    createCell(dataRow, c++, dto.getDescMotivo(), rowCenterStyle);

                    // Col 10: PERIODO
                    createCell(dataRow, c++, dto.getPeriodoTexto(), rowCenterStyle);

                    // Col 11: AUDITOR (Último auditor del caso)
                    createCell(dataRow, c++, dto.getUltimoAuditor(), rowCenterStyle);

                    // REGLAS "NO APLICA" PARA FECHAS
                    String fRecepcion = dto.getFechaRecepcion() != null ? dto.getFechaRecepcion() : "-";
                    String fEnvio = dto.getFechaEnvio() != null ? dto.getFechaEnvio() : "-";
                    String fPresentacion = dto.getFechaPresentacion() != null ? dto.getFechaPresentacion() : "-";
                    String hora = dto.getHora() != null ? dto.getHora() : "-";
                    String fResultado = dto.getFechaResultado() != null ? dto.getFechaResultado() : "-";

                    if (descDocUpper.contains("CARTA DE PRESENTACIÓN") || descDocUpper.contains("CARTA DE PRESENTACION")) {
                        fEnvio = "NO APLICA";
                        fPresentacion = "NO APLICA";
                        fResultado = "NO APLICA";
                    } else if (descDocUpper.contains("RECLAMO") || descDocUpper.contains("APELACION") || descDocUpper.contains("APELACIÓN")) {
                        fRecepcion = "NO APLICA";
                        fEnvio = "NO APLICA";
                    }

                    // Col 12: F. RECEPCION
                    createCell(dataRow, c++, fRecepcion, rowCenterStyle);

                    // Col 13: F. ENVIO
                    createCell(dataRow, c++, fEnvio, rowCenterStyle);

                    // Col 14: F. PRESENTACION
                    createCell(dataRow, c++, fPresentacion, rowCenterStyle);

                    // Col 15: HORA
                    createCell(dataRow, c++, hora, rowCenterStyle);

                    // Col 16: R. RESULTADO
                    createCell(dataRow, c++, fResultado, rowCenterStyle);

                    // Col 17: ESTADO
                    String descEst = dto.getDescEstado() != null ? dto.getDescEstado() : "";
                    XSSFCellStyle estStyle = estadoDefaultStyle;
                    if ("PENDIENTE".equalsIgnoreCase(descEst)) {
                        estStyle = estadoPendienteStyle;
                    } else if ("PRESENTADO".equalsIgnoreCase(descEst)) {
                        estStyle = estadoPresentadoStyle;
                    }
                    createCell(dataRow, c++, descEst, estStyle);

                    // Col 18: IMP. OBSERVADO
                    BigDecimal impObs = dto.getImporteObservado();
                    if (impObs != null && impObs.compareTo(BigDecimal.ZERO) > 0) {
                        createNumericCell(dataRow, c++, impObs.doubleValue(), rowMoneyStyle);
                    } else {
                        createCell(dataRow, c++, "-", rowCenterStyle);
                    }

                    // Col 19: RECTIFIC.
                    boolean isRect = dto.getRectificatoria() != null && dto.getRectificatoria() == 1;
                    createCell(dataRow, c++, isRect ? "SI" : "NO", rowCenterStyle);

                    // Col 20: IMP. RECTIFIC.
                    BigDecimal impRect = dto.getImporteRectificado();
                    if (impRect != null && impRect.compareTo(BigDecimal.ZERO) > 0) {
                        createNumericCell(dataRow, c++, impRect.doubleValue(), rowMoneyStyle);
                    } else {
                        createCell(dataRow, c++, "-", rowCenterStyle);
                    }
                }

                // Fusionar verticalmente columnas A a E si hay más de 1 documento para el Caso SUNAT
                if (numDocs > 1) {
                    sheet.addMergedRegion(new CellRangeAddress(startRow, endRow, 0, 0));
                    sheet.addMergedRegion(new CellRangeAddress(startRow, endRow, 1, 1));
                    sheet.addMergedRegion(new CellRangeAddress(startRow, endRow, 2, 2));
                    sheet.addMergedRegion(new CellRangeAddress(startRow, endRow, 3, 3));
                    sheet.addMergedRegion(new CellRangeAddress(startRow, endRow, 4, 4));
                }

                itemNum++;
            }

            // 5. AUTOFILTER EN FILA DE FILTROS Y FREEZE PANE
            sheet.setAutoFilter(new CellRangeAddress(filterRowIdx, filterRowIdx, 0, 20));
            sheet.createFreezePane(0, filterRowIdx + 1);

            // 6. AUTO SIZE COLUMNS CON MARGEN DE SEGURIDAD
            for (int j = 0; j < 21; j++) {
                sheet.autoSizeColumn(j);
                int currentWidth = sheet.getColumnWidth(j);
                int minWidth = 3200;
                sheet.setColumnWidth(j, Math.max(currentWidth + 768, minWidth));
            }

            wb.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el Reporte de Seguimiento Excel", e);
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
