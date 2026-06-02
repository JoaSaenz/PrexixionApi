package com.joa.prexixionapi.services;

import java.io.ByteArrayOutputStream;
import java.util.List;


import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Row;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joa.prexixionapi.entities.Cliente;
import com.joa.prexixionapi.entities.LoginVenta;
import com.joa.prexixionapi.repositories.LoginVentaRepository;
import com.joa.prexixionapi.utils.ExcelStyleManager;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LoginVentaExcelService {

    @Autowired
    private LoginVentaRepository loginVentaRepository;

    public byte[] exportarExcel(String anio, String mes, String estados, String grupos) {
        List<Cliente> list = loginVentaRepository.list(anio, mes, estados, grupos);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            XSSFWorkbook wb = new XSSFWorkbook();
            ExcelStyleManager styleManager = new ExcelStyleManager(wb);

            XSSFCellStyle cabeceraStyle = styleManager.getSubHeaderStyleBlue();
            XSSFCellStyle subHeaderStyle = styleManager.getSubHeaderStyleBlue();
            XSSFCellStyle dataStyle = styleManager.getFondoBlackStyle();
            XSSFCellStyle dataStyle2 = styleManager.getGenericStyle(ExcelStyleManager.LIGHT_GREY_RGB,
                    ExcelStyleManager.MATTE_BLACK_RGB, 9, false, HorizontalAlignment.CENTER);
            XSSFCellStyle dataGreenStyle = styleManager.getGenericStyle(ExcelStyleManager.LIGHT_GREY_RGB,
                    ExcelStyleManager.GREEN_RGB, 9, true, HorizontalAlignment.CENTER);
            XSSFCellStyle dataRedStyle = styleManager.getGenericStyle(ExcelStyleManager.LIGHT_GREY_RGB,
                    ExcelStyleManager.RED_RGB, 9, true, HorizontalAlignment.CENTER);

            String sheetName = "Ventas_" + anio + mes;
            XSSFSheet sheet = wb.createSheet(sheetName);

            int rowNum = 0;

            // CABECERA
            Row cabecera = sheet.createRow(rowNum);
            cabecera.setHeightInPoints(sheet.getDefaultRowHeightInPoints() * 3);
            Cell cellCabecera = cabecera.createCell(0);
            cellCabecera.setCellStyle(cabeceraStyle);
            cellCabecera.setCellValue("REPORTE MENSUAL - LOGIN VENTAS");
            sheet.addMergedRegion(CellRangeAddress.valueOf("A1:K1"));
            rowNum++;

            // Titulos
            Row title = sheet.createRow(rowNum);
            Cell cellTitle1 = title.createCell(0);
            cellTitle1.setCellStyle(subHeaderStyle);
            cellTitle1.setCellValue("CONTRIBUYENTE");
            sheet.addMergedRegion(CellRangeAddress.valueOf("A2:F2"));

            Cell cellTitle2 = title.createCell(6);
            cellTitle2.setCellStyle(subHeaderStyle);
            cellTitle2.setCellValue("DATOS");
            sheet.addMergedRegion(CellRangeAddress.valueOf("G2:K2"));
            rowNum++;

            // Sub Titulos
            Row subheader2 = sheet.createRow(rowNum);
            int colNum = 0;
            String[] headers = { "N°", "ESTADO", "Y", "RUC", "RAZÓN SOCIAL", "RESPONSABLE", "REG", "REV S", "VAL",
                    "CON", "OBSERVACION" };
            for (String h : headers) {
                Cell c = subheader2.createCell(colNum++);
                c.setCellStyle(subHeaderStyle);
                c.setCellValue(h);
            }
            rowNum++;

            int i = 1;
            for (Cliente clie : list) {
                Row data = sheet.createRow(rowNum);
                colNum = 0;
                LoginVenta lv = clie.getLoginVenta();

                Cell dataN = data.createCell(colNum++);
                dataN.setCellStyle(dataStyle);
                dataN.setCellValue(i++);

                Cell dataEstado = data.createCell(colNum++);
                dataEstado.setCellStyle(dataStyle2);
                dataEstado.setCellValue(clie.getEstado() != null ? clie.getEstado().getDescripcion() : "");

                Cell dataY = data.createCell(colNum++);
                dataY.setCellStyle(dataStyle2);
                dataY.setCellValue(clie.getY());

                Cell dataRuc = data.createCell(colNum++);
                dataRuc.setCellStyle(dataStyle2);
                dataRuc.setCellValue(clie.getRuc());

                Cell dataRs = data.createCell(colNum++);
                dataRs.setCellStyle(dataStyle2);
                dataRs.setCellValue(clie.getRazonSocial());

                Cell dataResp = data.createCell(colNum++);
                dataResp.setCellStyle(dataStyle2);
                dataResp.setCellValue(lv != null ? lv.getDescResponsable() : "");

                Cell dataReg = data.createCell(colNum++);
                setIndicatorCell(dataReg, lv != null ? lv.getRegistro() : 0, dataGreenStyle, dataRedStyle, dataStyle2);

                Cell dataRev = data.createCell(colNum++);
                setIndicatorCell(dataRev, lv != null ? lv.getRevisionSunat() : 0, dataGreenStyle, dataRedStyle,
                        dataStyle2);

                Cell dataVal = data.createCell(colNum++);
                setIndicatorCell(dataVal, lv != null ? lv.getValidacion() : 0, dataGreenStyle, dataRedStyle,
                        dataStyle2);

                Cell dataCon = data.createCell(colNum++);
                setIndicatorCell(dataCon, lv != null ? lv.getConfirmacion() : 0, dataGreenStyle, dataRedStyle,
                        dataStyle2);

                Cell dataObs = data.createCell(colNum++);
                dataObs.setCellStyle(dataStyle2);
                dataObs.setCellValue(lv != null ? lv.getObservacion() : "");

                rowNum++;
            }

            for (int contCol = 0; contCol < headers.length; contCol++) {
                sheet.autoSizeColumn(contCol);
            }

            wb.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            log.error("Error generating excel", e);
            throw new RuntimeException("Error generating excel");
        }
    }

    private void setIndicatorCell(Cell cell, Integer value, XSSFCellStyle green, XSSFCellStyle red,
            XSSFCellStyle grey) {
        if (value == null)
            value = 0;
        if (value == 1) {
            cell.setCellStyle(green);
            cell.setCellValue("✓");
        } else if (value == 2) {
            cell.setCellStyle(grey);
            cell.setCellValue("N/A");
        } else {
            cell.setCellStyle(red);
            cell.setCellValue("X");
        }
    }

    public byte[] exportarExcelDiario(String anio, String mes, String proceso, String fecha) {
        try {
            List<Cliente> list = loginVentaRepository.listExcelDiario(proceso, fecha, anio, mes);
            int acumulado = loginVentaRepository.countAcumulado(proceso, fecha, anio, mes);
            int total = loginVentaRepository.countTotal(anio, mes);

            int realizado = 0;
            switch (proceso) {
                case "registro":
                    realizado = (int) (list.isEmpty() ? 0 : list.stream().filter(e -> e.getLoginVenta().getRegistro() == 1 || e.getLoginVenta().getRegistro() == 2).count());
                    break;
                case "validacion":
                    realizado = (int) (list.isEmpty() ? 0 : list.stream().filter(e -> e.getLoginVenta().getValidacion() == 1 || e.getLoginVenta().getValidacion() == 2).count());
                    break;
                case "confirmacion":
                    realizado = (int) (list.isEmpty() ? 0 : list.stream().filter(e -> e.getLoginVenta().getConfirmacion() == 1).count());
                    break;
            }
            int contadorDia = list.isEmpty() ? 0 : list.size();

            String sheetName = "Reporte";

            Workbook wb = new XSSFWorkbook();
            Sheet sheet = wb.createSheet(sheetName);

            //<editor-fold defaultstate="collapsed" desc="ESTILOS">
            //<editor-fold defaultstate="collapsed" desc="COLORES PERSONALIZADOS">
            XSSFColor GERENCIA_BLUE = new XSSFColor(new byte[]{(byte) 0, (byte) 51, (byte) 204});
            XSSFColor GERENCIA_GREY = new XSSFColor(new byte[]{(byte) 214, (byte) 220, (byte) 228});
            XSSFColor MATTE_BLACK = new XSSFColor(new byte[]{(byte) 43, (byte) 43, (byte) 43});

            XSSFColor VERY_LIGHT_RED = new XSSFColor(new byte[]{(byte) 255, (byte) 102, (byte) 102});
            XSSFColor LIGHT_RED = new XSSFColor(new byte[]{(byte) 255, (byte) 51, (byte) 51});
            XSSFColor RED = new XSSFColor(new byte[]{(byte) 255, (byte) 0, (byte) 0});
            XSSFColor DARK_RED = new XSSFColor(new byte[]{(byte) 204, (byte) 0, (byte) 0});
            XSSFColor VERY_DARK_RED = new XSSFColor(new byte[]{(byte) 153, (byte) 0, (byte) 0});
            XSSFColor SUPERLIGHT_RED = new XSSFColor(new byte[]{(byte) 253, (byte) 237, (byte) 236});

            XSSFColor VERY_LIGHT_BLUE = new XSSFColor(new byte[]{(byte) 51, (byte) 204, (byte) 255});
            XSSFColor LIGHT_BLUE = new XSSFColor(new byte[]{(byte) 51, (byte) 153, (byte) 255});
            XSSFColor BLUE = new XSSFColor(new byte[]{(byte) 0, (byte) 0, (byte) 255});
            XSSFColor DARK_BLUE = new XSSFColor(new byte[]{(byte) 0, (byte) 0, (byte) 204});
            XSSFColor VERY_DARK_BLUE = new XSSFColor(new byte[]{(byte) 0, (byte) 0, (byte) 153});
            XSSFColor LIGHT_SKYBLUE = new XSSFColor(new byte[]{(byte) 235, (byte) 245, (byte) 251});

            XSSFColor VERY_LIGHT_GREEN = new XSSFColor(new byte[]{(byte) 102, (byte) 255, (byte) 102});
            XSSFColor LIGHT_GREEN = new XSSFColor(new byte[]{(byte) 0, (byte) 255, (byte) 51});
            XSSFColor GREEN = new XSSFColor(new byte[]{(byte) 0, (byte) 204, (byte) 0});
            XSSFColor DARK_GREEN = new XSSFColor(new byte[]{(byte) 0, (byte) 153, (byte) 0});
            XSSFColor VERY_DARK_GREEN = new XSSFColor(new byte[]{(byte) 0, (byte) 102, (byte) 0});

            XSSFColor VERY_LIGHT_YELLOW = new XSSFColor(new byte[]{(byte) 255, (byte) 255, (byte) 204});
            XSSFColor LIGHT_YELLOW = new XSSFColor(new byte[]{(byte) 255, (byte) 255, (byte) 153});
            XSSFColor YELLOW = new XSSFColor(new byte[]{(byte) 255, (byte) 255, (byte) 0});
            XSSFColor DARK_YELLOW = new XSSFColor(new byte[]{(byte) 255, (byte) 204, (byte) 0});

            XSSFColor LIGHT_ORANGE = new XSSFColor(new byte[]{(byte) 255, (byte) 153, (byte) 0});
            XSSFColor ORANGE = new XSSFColor(new byte[]{(byte) 255, (byte) 102, (byte) 0});

            XSSFColor GOLD = new XSSFColor(new byte[]{(byte) 255, (byte) 204, (byte) 51});

            XSSFColor LIGHT_GREY = new XSSFColor(new byte[]{(byte) 240, (byte) 240, (byte) 240});
            XSSFColor GREY = new XSSFColor(new byte[]{(byte) 153, (byte) 153, (byte) 153});
            XSSFColor DARK_GREY = new XSSFColor(new byte[]{(byte) 102, (byte) 102, (byte) 102});
            XSSFColor VERY_DARK_GREY = new XSSFColor(new byte[]{(byte) 51, (byte) 51, (byte) 51});

            XSSFColor LIGHT_BROWN = new XSSFColor(new byte[]{(byte) 153, (byte) 102, (byte) 0});
            XSSFColor BROWN = new XSSFColor(new byte[]{(byte) 102, (byte) 51, (byte) 0});
            XSSFColor VERY_DARK_BROWN = new XSSFColor(new byte[]{(byte) 51, (byte) 0, (byte) 0});

            XSSFColor PURPLE = new XSSFColor(new byte[]{(byte) 102, (byte) 0, (byte) 153});

            XSSFColor BLACK = new XSSFColor(new byte[]{(byte) 0, (byte) 0, (byte) 0});

            XSSFColor WHITE = new XSSFColor(new byte[]{(byte) 255, (byte) 255, (byte) 255});
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="FONTS">
            Boolean negrita = true;
            XSSFFont cabeceraFont = com.joa.prexixionapi.utils.PoiUtils.fuente((XSSFWorkbook) wb, WHITE, 17, negrita);
            XSSFFont subHeaderFont = com.joa.prexixionapi.utils.PoiUtils.fuente((XSSFWorkbook) wb, WHITE, 9, negrita);
            XSSFFont dataSimpleFont = com.joa.prexixionapi.utils.PoiUtils.fuente((XSSFWorkbook) wb, MATTE_BLACK, 9, negrita);
            XSSFFont dataGreenFont = com.joa.prexixionapi.utils.PoiUtils.fuente((XSSFWorkbook) wb, GREEN, 9, negrita);
            XSSFFont dataRedFont = com.joa.prexixionapi.utils.PoiUtils.fuente((XSSFWorkbook) wb, RED, 9, negrita);
            XSSFFont dataGreyFont = com.joa.prexixionapi.utils.PoiUtils.fuente((XSSFWorkbook) wb, GREY, 9, negrita);
            XSSFFont dataYellowFont = com.joa.prexixionapi.utils.PoiUtils.fuente((XSSFWorkbook) wb, YELLOW, 9, negrita);
            XSSFFont dataOrangeFont = com.joa.prexixionapi.utils.PoiUtils.fuente((XSSFWorkbook) wb, ORANGE, 9, negrita);
            XSSFFont dataLightBlueFont = com.joa.prexixionapi.utils.PoiUtils.fuente((XSSFWorkbook) wb, LIGHT_BLUE, 9, negrita);
            XSSFFont resStyleFont = com.joa.prexixionapi.utils.PoiUtils.fuente((XSSFWorkbook) wb, GERENCIA_BLUE, 9, negrita);
            XSSFFont dataStarFont = com.joa.prexixionapi.utils.PoiUtils.fuente((XSSFWorkbook) wb, MATTE_BLACK, 9, negrita);
            dataStarFont.setFontName("Wingdings");
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="CELL STYLES">
            CellStyle cabeceraStyle = com.joa.prexixionapi.utils.PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, GERENCIA_BLUE, cabeceraFont);
            CellStyle subHeaderStyle = com.joa.prexixionapi.utils.PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, MATTE_BLACK, subHeaderFont);
            com.joa.prexixionapi.utils.PoiUtils.addBorders(subHeaderStyle, BorderStyle.THIN, IndexedColors.WHITE);
            CellStyle subHeaderResStyle = com.joa.prexixionapi.utils.PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, GERENCIA_BLUE, subHeaderFont);
            com.joa.prexixionapi.utils.PoiUtils.addBorders(subHeaderResStyle, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataSimpleStyle = com.joa.prexixionapi.utils.PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.LEFT, WHITE, dataSimpleFont);
            com.joa.prexixionapi.utils.PoiUtils.addBorders(dataSimpleStyle, BorderStyle.THIN, IndexedColors.WHITE);
            dataSimpleStyle.setRightBorderColor(GREY.getIndex());
            CellStyle dataStyle = com.joa.prexixionapi.utils.PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, GERENCIA_GREY, dataSimpleFont);
            com.joa.prexixionapi.utils.PoiUtils.addBorders(dataStyle, BorderStyle.THIN, IndexedColors.WHITE);
            CellStyle dataLeftStyle = com.joa.prexixionapi.utils.PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.LEFT, GERENCIA_GREY, dataSimpleFont);
            com.joa.prexixionapi.utils.PoiUtils.addBorders(dataLeftStyle, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataStyle2 = com.joa.prexixionapi.utils.PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, LIGHT_GREY, dataSimpleFont);
            com.joa.prexixionapi.utils.PoiUtils.addBorders(dataStyle2, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataGreenStyle = com.joa.prexixionapi.utils.PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, LIGHT_GREY, dataGreenFont);
            com.joa.prexixionapi.utils.PoiUtils.addBorders(dataGreenStyle, BorderStyle.THIN, IndexedColors.WHITE);
            CellStyle dataRedStyle = com.joa.prexixionapi.utils.PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, LIGHT_GREY, dataRedFont);
            com.joa.prexixionapi.utils.PoiUtils.addBorders(dataRedStyle, BorderStyle.THIN, IndexedColors.WHITE);
            CellStyle dataGreyStyle = com.joa.prexixionapi.utils.PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, LIGHT_GREY, dataGreyFont);
            com.joa.prexixionapi.utils.PoiUtils.addBorders(dataGreyStyle, BorderStyle.THIN, IndexedColors.WHITE);
            CellStyle dataYellowStyle = com.joa.prexixionapi.utils.PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, LIGHT_GREY, dataYellowFont);
            com.joa.prexixionapi.utils.PoiUtils.addBorders(dataYellowStyle, BorderStyle.THIN, IndexedColors.WHITE);
            CellStyle dataLightBlueStyle = com.joa.prexixionapi.utils.PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, LIGHT_GREY, dataLightBlueFont);
            com.joa.prexixionapi.utils.PoiUtils.addBorders(dataLightBlueStyle, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataGreenStyleNeg = com.joa.prexixionapi.utils.PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, GERENCIA_GREY, dataGreenFont);
            com.joa.prexixionapi.utils.PoiUtils.addBorders(dataGreenStyleNeg, BorderStyle.THIN, IndexedColors.WHITE);
            CellStyle dataRedStyleNeg = com.joa.prexixionapi.utils.PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, GERENCIA_GREY, dataRedFont);
            com.joa.prexixionapi.utils.PoiUtils.addBorders(dataRedStyleNeg, BorderStyle.THIN, IndexedColors.WHITE);
            CellStyle dataStyleNeg = com.joa.prexixionapi.utils.PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, GERENCIA_GREY, dataSimpleFont);
            com.joa.prexixionapi.utils.PoiUtils.addBorders(dataStyleNeg, BorderStyle.THIN, IndexedColors.WHITE);
            CellStyle dataOrangeStyleNeg = com.joa.prexixionapi.utils.PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, LIGHT_GREY, dataOrangeFont);
            com.joa.prexixionapi.utils.PoiUtils.addBorders(dataOrangeStyleNeg, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataSimpleRedStyle = com.joa.prexixionapi.utils.PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, WHITE, dataRedFont);
            com.joa.prexixionapi.utils.PoiUtils.addBorders(dataSimpleRedStyle, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle resStyle = com.joa.prexixionapi.utils.PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.LEFT, WHITE, resStyleFont);
            CellStyle resStyleG = com.joa.prexixionapi.utils.PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, WHITE, dataGreenFont);
            CellStyle resStyleR = com.joa.prexixionapi.utils.PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, WHITE, dataRedFont);

            CellStyle dataStarStyle = com.joa.prexixionapi.utils.PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, GERENCIA_GREY, dataStarFont);
            com.joa.prexixionapi.utils.PoiUtils.addBorders(dataStarStyle, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle subCabeceraStyleBlue = com.joa.prexixionapi.utils.PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, GERENCIA_BLUE, subHeaderFont);
            com.joa.prexixionapi.utils.PoiUtils.addBorders(subCabeceraStyleBlue, BorderStyle.THIN, IndexedColors.WHITE);
            CellStyle subCabeceraStyleRed = com.joa.prexixionapi.utils.PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, RED, subHeaderFont);
            com.joa.prexixionapi.utils.PoiUtils.addBorders(subCabeceraStyleRed, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataAcumulado = com.joa.prexixionapi.utils.PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, WHITE, dataSimpleFont);
            com.joa.prexixionapi.utils.PoiUtils.addBorders(dataAcumulado, BorderStyle.THIN, IndexedColors.WHITE);
            CellStyle dataRealizado = com.joa.prexixionapi.utils.PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, LIGHT_SKYBLUE, dataSimpleFont);
            com.joa.prexixionapi.utils.PoiUtils.addBorders(dataRealizado, BorderStyle.THIN, IndexedColors.WHITE);
            CellStyle dataFaltantes = com.joa.prexixionapi.utils.PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, SUPERLIGHT_RED, dataSimpleFont);
            com.joa.prexixionapi.utils.PoiUtils.addBorders(dataFaltantes, BorderStyle.THIN, IndexedColors.WHITE);
            //</editor-fold>

            //</editor-fold>
            int rowNum = 0;

            //<editor-fold defaultstate="collapsed" desc="RESUMEN">
            Row res0 = sheet.createRow(rowNum);
            int colNumRes = 6;

            Cell resumenTit = res0.createCell(colNumRes);
            resumenTit.setCellValue(" R E S U M E N ");
            resumenTit.setCellStyle(cabeceraStyle);
            CellRangeAddress regionRes = CellRangeAddress.valueOf("G1:J1");
            sheet.addMergedRegion(regionRes);
            com.joa.prexixionapi.utils.PoiUtils.addBorders(regionRes, BorderStyle.THIN, IndexedColors.WHITE, sheet);

            rowNum++;
            colNumRes = 6;

            Row res1 = sheet.createRow(rowNum);

            Cell resSubTitAcumulado = res1.createCell(colNumRes);
            resSubTitAcumulado.setCellValue("ACUMULADO");
            resSubTitAcumulado.setCellStyle(subHeaderStyle);
            colNumRes++;

            Cell resSubTitRealizado = res1.createCell(colNumRes);
            resSubTitRealizado.setCellValue("HOY (Cerrados)");
            resSubTitRealizado.setCellStyle(subCabeceraStyleBlue);
            colNumRes++;

            Cell resSubTitFaltantes = res1.createCell(colNumRes);
            resSubTitFaltantes.setCellValue("FALTANTES");
            resSubTitFaltantes.setCellStyle(subCabeceraStyleRed);
            colNumRes++;

            Cell resSubTitTotal = res1.createCell(colNumRes);
            resSubTitTotal.setCellValue("TOTAL");
            resSubTitTotal.setCellStyle(subHeaderStyle);
            colNumRes++;

            Cell resSubTitContadorDia = res1.createCell(colNumRes);
            resSubTitContadorDia.setCellValue("Contador Día");
            resSubTitContadorDia.setCellStyle(subHeaderStyle);
            colNumRes++;

            rowNum++;
            colNumRes = 6;

            Row res2 = sheet.createRow(rowNum);

            Cell resAcumulado = res2.createCell(colNumRes);
            resAcumulado.setCellValue(acumulado);
            resAcumulado.setCellStyle(dataAcumulado);
            colNumRes++;

            Cell resRealizado = res2.createCell(colNumRes);
            resRealizado.setCellValue(realizado);
            resRealizado.setCellStyle(dataRealizado);
            colNumRes++;

            Cell resFaltantes = res2.createCell(colNumRes);
            int faltantes = total - acumulado - realizado;
            resFaltantes.setCellValue(faltantes);
            resFaltantes.setCellStyle(dataFaltantes);
            colNumRes++;

            Cell resTotal = res2.createCell(colNumRes);
            resTotal.setCellValue(total);
            resTotal.setCellStyle(dataStyle2);
            colNumRes++;

            Cell resContadorDoa = res2.createCell(colNumRes);
            resContadorDoa.setCellValue(contadorDia);
            resContadorDoa.setCellStyle(dataStyle2);
            colNumRes++;

            rowNum++;
            colNumRes = 6;

            Row res3 = sheet.createRow(rowNum);

            Cell resAcumuladoPercent = res3.createCell(colNumRes);
            double acumuladoPercent = ((double) acumulado * (double) 100) / (double) total;
            resAcumuladoPercent.setCellValue(Math.round(acumuladoPercent) + " %");
            resAcumuladoPercent.setCellStyle(dataAcumulado);
            colNumRes++;

            Cell resRealizadoPercent = res3.createCell(colNumRes);
            double realizadoPercent = ((double) realizado * (double) 100) / (double) total;
            resRealizadoPercent.setCellValue(Math.round(realizadoPercent) + " %");
            resRealizadoPercent.setCellStyle(dataRealizado);
            colNumRes++;

            Cell resFaltantePercent = res3.createCell(colNumRes);
            double faltantePercent = ((double) faltantes * (double) 100) / (double) total;
            resFaltantePercent.setCellValue(Math.round(faltantePercent) + " %");
            resFaltantePercent.setCellStyle(dataFaltantes);
            colNumRes++;

            Cell resTotalPercent = res3.createCell(colNumRes);
            double totalPercent = ((double) total * (double) 100) / (double) total;
            resTotalPercent.setCellValue(Math.round(totalPercent) + " %");
            resTotalPercent.setCellStyle(dataStyle2);
            colNumRes++;
            rowNum++;
            rowNum++;
            //</editor-fold>

            // <editor-fold  defaultstate="collapsed" desc="CABECERA">
            Row cabecera = sheet.createRow(rowNum);
            cabecera.setHeightInPoints(sheet.getDefaultRowHeightInPoints() * 3);

            Cell cellCabecera = cabecera.createCell(0);
            cellCabecera.setCellStyle(cabeceraStyle);
            cellCabecera.setCellValue("REPORTE DIARIO - LOGIN VENTAS");
            sheet.addMergedRegion(CellRangeAddress.valueOf("A6:L6"));

            rowNum++;
            int colNum = 0;

            Row title = sheet.createRow(rowNum);

            Cell cellTitle1 = title.createCell(colNum);
            cellTitle1.setCellStyle(subHeaderStyle);
            cellTitle1.setCellValue("CONTRIBUYENTE");
            CellRangeAddress region = CellRangeAddress.valueOf("A7:I7");
            sheet.addMergedRegion(region);
            com.joa.prexixionapi.utils.PoiUtils.addBorders(region, BorderStyle.THIN, IndexedColors.WHITE, sheet);
            colNum = colNum + 9;

            Cell cellTitle2 = title.createCell(colNum);
            cellTitle2.setCellStyle(subHeaderStyle);
            cellTitle2.setCellValue("PROCESO");
            region = CellRangeAddress.valueOf("J7:K7");
            sheet.addMergedRegion(region);
            com.joa.prexixionapi.utils.PoiUtils.addBorders(region, BorderStyle.THIN, IndexedColors.WHITE, sheet);
            colNum = colNum + 2;

            Cell cellEncargado = title.createCell(colNum);
            cellEncargado.setCellStyle(subHeaderStyle);
            cellEncargado.setCellValue("REGISTRADO POR");
            sheet.addMergedRegion(CellRangeAddress.valueOf("L7:L8"));
            com.joa.prexixionapi.utils.PoiUtils.addBorders(region, BorderStyle.THIN, IndexedColors.WHITE, sheet);

            rowNum++;
            colNum = 0;

            Row subHeader = sheet.createRow(rowNum);

            Cell cellHeaderNro = subHeader.createCell(colNum);
            cellHeaderNro.setCellStyle(subHeaderStyle);
            cellHeaderNro.setCellValue("N°");
            colNum++;

            Cell cellHeaderRuc = subHeader.createCell(colNum);
            cellHeaderRuc.setCellStyle(subHeaderStyle);
            cellHeaderRuc.setCellValue("RUC");
            colNum++;

            Cell cellHeaderStar = subHeader.createCell(colNum);
            cellHeaderStar.setCellStyle(subHeaderStyle);
            cellHeaderStar.setCellValue("star");
            colNum++;

            Cell cellHeaderRs = subHeader.createCell(colNum);
            cellHeaderRs.setCellStyle(subHeaderStyle);
            cellHeaderRs.setCellValue("RAZÓN SOCIAL");
            colNum++;

            Cell cellHeaderEstado = subHeader.createCell(colNum);
            cellHeaderEstado.setCellStyle(subHeaderStyle);
            cellHeaderEstado.setCellValue("ESTADO");
            colNum++;

            Cell cellHeaderTipoServicio = subHeader.createCell(colNum);
            cellHeaderTipoServicio.setCellStyle(subHeaderStyle);
            cellHeaderTipoServicio.setCellValue("T. SERV.");
            colNum++;

            Cell cellHeaderPle = subHeader.createCell(colNum);
            cellHeaderPle.setCellStyle(subHeaderStyle);
            cellHeaderPle.setCellValue("PLE");
            colNum++;

            Cell cellHeaderRt = subHeader.createCell(colNum);
            cellHeaderRt.setCellStyle(subHeaderStyle);
            cellHeaderRt.setCellValue("RT");
            colNum++;

            Cell cellHeaderMovimiento = subHeader.createCell(colNum);
            cellHeaderMovimiento.setCellStyle(subHeaderStyle);
            cellHeaderMovimiento.setCellValue("MOV");
            colNum++;

            Cell cellHeaderRec = subHeader.createCell(colNum);
            cellHeaderRec.setCellStyle(subHeaderStyle);
            cellHeaderRec.setCellValue(proceso.toUpperCase());
            colNum++;

            Cell cellHeaderProcesoFecha = subHeader.createCell(colNum);
            cellHeaderProcesoFecha.setCellStyle(subHeaderStyle);
            cellHeaderProcesoFecha.setCellValue("FECHA");
            colNum++;
            // </editor-fold>

            rowNum++;
            int inicioFilt = rowNum;
            rowNum++;

            // <editor-fold  defaultstate="collapsed" desc=" L L E N A D O   D E   L A   D A T A ">
            for (Cliente clie : list) {
                colNum = 0;

                Row data = sheet.createRow(rowNum);

                Cell yData = data.createCell(colNum);
                yData.setCellValue(clie.getY());
                yData.setCellStyle(dataStyle);
                colNum++;

                Cell rucData = data.createCell(colNum);
                rucData.setCellValue(clie.getRuc());
                rucData.setCellStyle(dataStyle);
                colNum++;

                Cell prioriData = data.createCell(colNum);
                if (clie.getLoginVenta().getPrioridad() != null) {
                    switch (clie.getLoginVenta().getPrioridad()) {
                        case "ALTA - PRE":
                            prioriData.setCellValue("☆ - PRE");
                            break;
                        case "ALTA":
                            prioriData.setCellValue("☆");
                            break;
                        case "PRE":
                            prioriData.setCellValue("PRE");
                            break;
                        case "NORMAL":
                            prioriData.setCellValue("");
                            break;
                    }
                } else {
                    prioriData.setCellValue("");
                }

                prioriData.setCellStyle(dataStyle);
                colNum++;

                Cell rsData = data.createCell(colNum);
                rsData.setCellValue(clie.getRazonSocial());
                rsData.setCellStyle(dataLeftStyle);
                colNum++;

                Cell estadoData = data.createCell(colNum);
                estadoData.setCellStyle(dataStyle2);
                estadoData.setCellValue(clie.getEstado() != null && clie.getEstado().getDescripcion() != null ? clie.getEstado().getDescripcion().toUpperCase() : "");
                switch (clie.getEstado() != null ? clie.getEstado().getId() : 0) {
                    case 1:
                        estadoData.setCellStyle(dataGreenStyle);
                        break;
                    case 2:
                        estadoData.setCellStyle(dataRedStyle);
                        break;
                    case 3:
                        estadoData.setCellStyle(dataLightBlueStyle);
                        break;
                    case 4:
                        estadoData.setCellStyle(dataYellowStyle);
                        break;
                    case 5:
                        estadoData.setCellStyle(dataGreyStyle);
                        break;
                }
                colNum++;

                Cell tipoServicioData = data.createCell(colNum);
                tipoServicioData.setCellValue(clie.getServicio() != null && clie.getServicio().getAbreviatura() != null ? clie.getServicio().getAbreviatura() : "");
                tipoServicioData.setCellStyle(dataStyle2);
                colNum++;

                Cell pleData = data.createCell(colNum);
                pleData.setCellValue(clie.getLoginVenta() != null && clie.getLoginVenta().getPle() != null ? clie.getLoginVenta().getPle() : "");
                pleData.setCellStyle(dataStyle2);
                colNum++;

                Cell rtData = data.createCell(colNum);
                rtData.setCellValue(clie.getRegimenTributario());
                rtData.setCellStyle(dataStyle2);
                colNum++;

                Cell movData = data.createCell(colNum);
                movData.setCellValue(clie.getLoginVenta() != null && clie.getLoginVenta().getMovimiento() != null ? clie.getLoginVenta().getMovimiento() : "");
                movData.setCellStyle(dataStyle2);
                colNum++;

                //PROCESO DATA
                Cell procesoData = data.createCell(colNum);

                int procesoValor = 0;
                int procesoValidacionValor = 0;
                int procesoConfirmacionValor = 0;
                String procesoFecha = "";
                String procesoRegistradoPor = "";
                switch (proceso) {
                    case "registro":
                        procesoValor = clie.getLoginVenta().getRegistro();
                        procesoFecha = clie.getLoginVenta().getRegistroFecha();
                        procesoRegistradoPor = clie.getLoginVenta().getRegistroUsuario();
                        break;
                    case "validacion":
                        procesoValidacionValor = clie.getLoginVenta().getValidacion();
                        procesoFecha = clie.getLoginVenta().getValidacionFecha();
                        procesoRegistradoPor = clie.getLoginVenta().getValidacionUsuario();
                        break;
                    case "confirmacion":
                        procesoConfirmacionValor = clie.getLoginVenta().getConfirmacion();
                        procesoFecha = clie.getLoginVenta().getConfirmacionFecha();
                        procesoRegistradoPor = clie.getLoginVenta().getConfirmacionUsuario();
                        break;
                }

                if (proceso.equals("registro")) {
                    switch (procesoValor) {
                        case 0:
                            procesoData.setCellValue("X");
                            procesoData.setCellStyle(dataSimpleRedStyle);
                            break;
                        case 1:
                            procesoData.setCellValue("✓");
                            procesoData.setCellStyle(dataGreenStyle);
                            break;
                        case 2:
                            procesoData.setCellValue("N/A");
                            procesoData.setCellStyle(dataStyleNeg);
                            break;
                    }
                }
                if (proceso.equals("validacion")) {
                    switch (procesoValidacionValor) {
                        case 0:
                            procesoData.setCellValue("X");
                            procesoData.setCellStyle(dataSimpleRedStyle);
                            break;
                        case 1:
                            procesoData.setCellValue("✓");
                            procesoData.setCellStyle(dataGreenStyle);
                            break;
                        case 2:
                            procesoData.setCellValue("N/A");
                            procesoData.setCellStyle(dataStyleNeg);
                            break;
                        case 3:
                            procesoData.setCellValue("VAL");
                            procesoData.setCellStyle(dataOrangeStyleNeg);
                            break;
                    }
                }
                if (proceso.equals("confirmacion")) {
                    switch (procesoConfirmacionValor) {
                        case 0:
                            procesoData.setCellValue("X");
                            procesoData.setCellStyle(dataSimpleRedStyle);
                            break;
                        case 1:
                            procesoData.setCellValue("✓");
                            procesoData.setCellStyle(dataGreenStyle);
                            break;
                    }
                }
                colNum++;

                Cell procesoFechaData = data.createCell(colNum);
                procesoFechaData.setCellValue(procesoFecha);
                procesoFechaData.setCellStyle(dataStyle2);
                colNum++;

                Cell procesoRegistradoPorData = data.createCell(colNum);
                procesoRegistradoPorData.setCellValue(procesoRegistradoPor);
                procesoRegistradoPorData.setCellStyle(dataStyle2);
                colNum++;
                rowNum++;

            }
            // </editor-fold>

            sheet.createFreezePane(0, 9);

            int finFilt = rowNum - 1;
            sheet.setAutoFilter(new CellRangeAddress(inicioFilt, finFilt, 0, 11));

            //<editor-fold defaultstate="collapsed" desc=" A N C H O   D E   C O L U M N A S ">
            for (int contCol = 0; contCol < colNum; contCol++) {
                sheet.autoSizeColumn(contCol);
            }
            //</editor-fold>

                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
            wb.write(bos);
            wb.close();
            return bos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}