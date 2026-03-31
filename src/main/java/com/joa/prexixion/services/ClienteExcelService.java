package com.joa.prexixion.services;

import com.joa.prexixion.dto.ClienteExcelProjection;
import com.joa.prexixion.utils.PoiUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ClienteExcelService {

        private final com.joa.prexixion.repositories.ClienteExcelRepository repository;

        public ClienteExcelService(com.joa.prexixion.repositories.ClienteExcelRepository repository) {
                this.repository = repository;
        }

        private String getX(Integer i) {
                return (i != null && i == 1) ? "X" : "";
        }

        public byte[] exportarExcelCliente(String estados, String grupos) {
                java.util.List<Integer> listEstados = java.util.Arrays.stream(estados.split(","))
                                .map(String::trim).map(Integer::valueOf).toList();
                java.util.List<Integer> listGrupos = java.util.Arrays.stream(grupos.split(","))
                                .map(String::trim).map(Integer::valueOf).toList();

                List<ClienteExcelProjection> data = repository.getExcelData(listEstados, listGrupos);
                return exportDataToExcel(data);
        }

        private static class ResumenState {
                int[] activos = new int[10];
                int[] bajas = new int[10];
                int[] libres = new int[10];
                int[] retirados = new int[10];
                int[] suspendidos = new int[10];
                int[] externos = new int[10];
                int[] ceros = new int[10];
                int[] morosos = new int[10];
                int totalActivos, totalBaja, totalLibre, totalRetirados, totalSuspendidos, totalExt, totalCer, totalMor;

                void add(String estado, String yStr) {
                        if (yStr == null || yStr.isBlank())
                                return;
                        int y;
                        try {
                                y = Integer.parseInt(yStr.trim());
                        } catch (NumberFormatException e) {
                                return;
                        }
                        if (y < 0 || y > 9)
                                return;
                        if (estado == null)
                                return;
                        switch (estado.trim()) {
                                case "Activo":
                                        activos[y]++;
                                        totalActivos++;
                                        break;
                                case "Baja":
                                        bajas[y]++;
                                        totalBaja++;
                                        break;
                                case "Libre":
                                        libres[y]++;
                                        totalLibre++;
                                        break;
                                case "Retirado":
                                        retirados[y]++;
                                        totalRetirados++;
                                        break;
                                case "Suspendido":
                                        suspendidos[y]++;
                                        totalSuspendidos++;
                                        break;
                                case "Externo":
                                        externos[y]++;
                                        totalExt++;
                                        break;
                                case "Cero":
                                        ceros[y]++;
                                        totalCer++;
                                        break;
                                case "Moroso":
                                        morosos[y]++;
                                        totalMor++;
                                        break;
                        }
                }

                int total(int y) {
                        return activos[y] + bajas[y] + libres[y] + retirados[y] + suspendidos[y] + externos[y]
                                        + ceros[y] + morosos[y];
                }
        }

        public byte[] exportDataToExcel(List<ClienteExcelProjection> list) {
                try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

                        ResumenState state = new ResumenState();

                        XSSFWorkbook wb = new XSSFWorkbook();

                        // <editor-fold defaultstate="collapsed" desc=" E S T I L O S ">
                        // <editor-fold defaultstate="collapsed" desc="COLORES PERSONALIZADOS">
                        XSSFColor GERENCIA_BLUE = PoiUtils.createColor(0, 51, 204);
                        XSSFColor GERENCIA_GREY = PoiUtils.createColor(214, 220, 228);
                        XSSFColor MATTE_BLACK = PoiUtils.createColor(43, 43, 43);

                        XSSFColor VERY_LIGHT_RED = PoiUtils.createColor(253, 237, 236);
                        XSSFColor LIGHT_RED = PoiUtils.createColor(255, 51, 51);
                        XSSFColor RED = PoiUtils.createColor(255, 0, 0);
                        XSSFColor DARK_RED = PoiUtils.createColor(204, 0, 0);
                        XSSFColor VERY_DARK_RED = PoiUtils.createColor(153, 0, 0);

                        XSSFColor VERY_LIGHT_BLUE = PoiUtils.createColor(235, 245, 251);
                        XSSFColor LIGHT_BLUE = PoiUtils.createColor(51, 153, 255);
                        XSSFColor BLUE = PoiUtils.createColor(0, 0, 255);
                        XSSFColor DARK_BLUE = PoiUtils.createColor(0, 51, 204);
                        XSSFColor VERY_DARK_BLUE = PoiUtils.createColor(0, 0, 153);

                        XSSFColor VERY_LIGHT_GREEN = PoiUtils.createColor(244, 251, 235);
                        XSSFColor LIGHT_GREEN = PoiUtils.createColor(0, 255, 51);
                        XSSFColor GREEN = PoiUtils.createColor(0, 204, 0);
                        XSSFColor DARK_GREEN = PoiUtils.createColor(0, 153, 0);
                        XSSFColor VERY_DARK_GREEN = PoiUtils.createColor(0, 102, 0);

                        XSSFColor VERY_LIGHT_YELLOW = PoiUtils.createColor(254, 249, 231);
                        XSSFColor LIGHT_YELLOW = PoiUtils.createColor(255, 255, 153);
                        XSSFColor YELLOW = PoiUtils.createColor(255, 255, 0);
                        XSSFColor DARK_YELLOW = PoiUtils.createColor(255, 204, 0);

                        XSSFColor VERY_LIGHT_ORANGE = PoiUtils.createColor(245, 203, 167);
                        XSSFColor LIGHT_ORANGE = PoiUtils.createColor(255, 153, 0);
                        XSSFColor ORANGE = PoiUtils.createColor(255, 102, 0);

                        XSSFColor GOLD = PoiUtils.createColor(255, 204, 51);

                        XSSFColor LIGHT_GREY = PoiUtils.createColor(242, 242, 242);
                        XSSFColor GREY = PoiUtils.createColor(128, 128, 128);
                        XSSFColor DARK_GREY = PoiUtils.createColor(102, 102, 102);
                        XSSFColor VERY_DARK_GREY = PoiUtils.createColor(51, 51, 51);

                        XSSFColor LIGHT_BROWN = PoiUtils.createColor(153, 102, 0);
                        XSSFColor BROWN = PoiUtils.createColor(102, 51, 0);
                        XSSFColor VERY_DARK_BROWN = PoiUtils.createColor(51, 0, 0);

                        XSSFColor PURPLE = PoiUtils.createColor(102, 0, 153);

                        XSSFColor BLACK = PoiUtils.createColor(0, 0, 0);

                        XSSFColor WHITE = PoiUtils.createColor(255, 255, 255);

                        XSSFColor CELESTE_COLOR = PoiUtils.createColor(138, 193, 255);
                        XSSFColor TURQUESA_COLOR = PoiUtils.createColor(81, 198, 218);
                        XSSFColor AMARILLO_COLOR = PoiUtils.createColor(254, 255, 155);
                        XSSFColor NARANJA_COLOR = PoiUtils.createColor(252, 184, 72);
                        XSSFColor ROJO_COLOR = PoiUtils.createColor(169, 0, 54);
                        // </editor-fold>

                        // <editor-fold defaultstate="collapsed" desc="FONTS">
                        Boolean negrita = true;
                        XSSFFont fontWhite17b = PoiUtils.fuente((XSSFWorkbook) wb, WHITE, 17, negrita);
                        XSSFFont fontWhite9b = PoiUtils.fuente((XSSFWorkbook) wb, WHITE, 9, negrita);
                        XSSFFont fontWhite9 = PoiUtils.fuente((XSSFWorkbook) wb, WHITE, 9);
                        XSSFFont fontBlack9b = PoiUtils.fuente((XSSFWorkbook) wb, MATTE_BLACK, 9, negrita);
                        XSSFFont fontBlack9 = PoiUtils.fuente((XSSFWorkbook) wb, MATTE_BLACK, 9);

                        XSSFFont fontGreen9b = PoiUtils.fuente((XSSFWorkbook) wb, GREEN, 9, negrita);
                        XSSFFont fontRed9b = PoiUtils.fuente((XSSFWorkbook) wb, RED, 9, negrita);
                        XSSFFont fontGrey9b = PoiUtils.fuente((XSSFWorkbook) wb, GREY, 9, negrita);
                        XSSFFont fontYellow9b = PoiUtils.fuente((XSSFWorkbook) wb, ORANGE, 9, negrita);
                        XSSFFont fontLightBlue9b = PoiUtils.fuente((XSSFWorkbook) wb, LIGHT_BLUE, 9, negrita);
                        XSSFFont fontBlue9b = PoiUtils.fuente((XSSFWorkbook) wb, BLUE, 9, negrita);

                        XSSFFont cabeceraFont = PoiUtils.fuente((XSSFWorkbook) wb, WHITE, 17, negrita);
                        // </editor-fold>

                        CreationHelper ch = ((XSSFWorkbook) wb).getCreationHelper();

                        // <editor-fold defaultstate="collapsed" desc="CELL STYLES">
                        XSSFCellStyle fondoWhiteStyleCenter = PoiUtils.createCellStyle((XSSFWorkbook) wb,
                                        HorizontalAlignment.CENTER, WHITE, fontBlack9);
                        PoiUtils.addBorders(fondoWhiteStyleCenter, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);
                        XSSFCellStyle fondoWhiteStyleLeft = PoiUtils.createCellStyle((XSSFWorkbook) wb,
                                        HorizontalAlignment.LEFT,
                                        WHITE, fontBlack9);
                        PoiUtils.addBorders(fondoWhiteStyleLeft, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);
                        XSSFCellStyle fondoWhiteStyleRight = PoiUtils.createCellStyle((XSSFWorkbook) wb,
                                        HorizontalAlignment.RIGHT,
                                        WHITE, fontBlack9);
                        PoiUtils.addBorders(fondoWhiteStyleRight, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);

                        XSSFCellStyle fondoBlackStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb,
                                        HorizontalAlignment.CENTER,
                                        BLACK, fontWhite9b);

                        XSSFCellStyle fondoBlackStyleSinNegrita = PoiUtils.createCellStyle((XSSFWorkbook) wb,
                                        HorizontalAlignment.CENTER, BLACK, fontWhite9);
                        PoiUtils.addBorders(fondoBlackStyleSinNegrita, BorderStyle.THIN, IndexedColors.WHITE);

                        // FONDO BLANCO
                        XSSFCellStyle fondoWhiteStyleGreyFont = PoiUtils.createCellStyle((XSSFWorkbook) wb,
                                        HorizontalAlignment.CENTER, WHITE, fontGrey9b);
                        XSSFCellStyle fondoWhiteStyleBlue = PoiUtils.createCellStyle((XSSFWorkbook) wb,
                                        HorizontalAlignment.LEFT,
                                        WHITE, fontBlue9b);
                        PoiUtils.addBorders(fondoWhiteStyleBlue, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);
                        XSSFCellStyle fondoWhiteStyleGreenFont = PoiUtils.createCellStyle((XSSFWorkbook) wb,
                                        HorizontalAlignment.CENTER, WHITE, fontGreen9b);
                        PoiUtils.addBorders(fondoWhiteStyleGreenFont, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);
                        XSSFCellStyle fondoWhiteStyleRedFont = PoiUtils.createCellStyle((XSSFWorkbook) wb,
                                        HorizontalAlignment.CENTER, WHITE, fontRed9b);
                        PoiUtils.addBorders(fondoWhiteStyleRedFont, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);

                        // FONDO DOLORES
                        XSSFCellStyle fondoGreenStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb,
                                        HorizontalAlignment.CENTER,
                                        GREEN, fontWhite9b);
                        PoiUtils.addBorders(fondoGreenStyle, BorderStyle.THIN, IndexedColors.WHITE);
                        XSSFCellStyle fondoYellowStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb,
                                        HorizontalAlignment.LEFT,
                                        YELLOW, fontBlack9b);

                        // FONDO COLORES OSCUROS
                        XSSFCellStyle fondoRedStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb,
                                        HorizontalAlignment.CENTER,
                                        DARK_RED, fontWhite9b);
                        PoiUtils.addBorders(fondoRedStyle, BorderStyle.THIN, IndexedColors.WHITE);
                        XSSFCellStyle fondoBlueStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb,
                                        HorizontalAlignment.CENTER,
                                        DARK_BLUE, fontWhite9b);
                        PoiUtils.addBorders(fondoBlueStyle, BorderStyle.THIN, IndexedColors.WHITE);
                        XSSFCellStyle dataStyleCellCeleste = PoiUtils.createCellStyle((XSSFWorkbook) wb,
                                        HorizontalAlignment.CENTER,
                                        CELESTE_COLOR, fontWhite9b);
                        PoiUtils.addBorders(dataStyleCellCeleste, BorderStyle.THIN, IndexedColors.WHITE);
                        XSSFCellStyle dataStyleCellTurquesa = PoiUtils.createCellStyle((XSSFWorkbook) wb,
                                        HorizontalAlignment.CENTER, TURQUESA_COLOR, fontWhite9b);
                        PoiUtils.addBorders(dataStyleCellTurquesa, BorderStyle.THIN, IndexedColors.WHITE);
                        XSSFCellStyle dataStyleCellAmarillo = PoiUtils.createCellStyle((XSSFWorkbook) wb,
                                        HorizontalAlignment.CENTER, AMARILLO_COLOR, fontBlack9b);
                        PoiUtils.addBorders(dataStyleCellAmarillo, BorderStyle.THIN, IndexedColors.WHITE);
                        XSSFCellStyle dataStyleCellNaranja = PoiUtils.createCellStyle((XSSFWorkbook) wb,
                                        HorizontalAlignment.CENTER,
                                        NARANJA_COLOR, fontWhite9b);
                        PoiUtils.addBorders(dataStyleCellNaranja, BorderStyle.THIN, IndexedColors.WHITE);
                        XSSFCellStyle dataStyleCellRojo = PoiUtils.createCellStyle((XSSFWorkbook) wb,
                                        HorizontalAlignment.CENTER,
                                        ROJO_COLOR, fontWhite9b);
                        PoiUtils.addBorders(dataStyleCellRojo, BorderStyle.THIN, IndexedColors.WHITE);

                        // FONDO COLORES SUAVES
                        XSSFCellStyle fondoLightRedStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb,
                                        HorizontalAlignment.CENTER,
                                        VERY_LIGHT_RED, fontBlack9b);
                        PoiUtils.addBorders(fondoLightRedStyle, BorderStyle.THIN, IndexedColors.WHITE);
                        XSSFCellStyle fondoLightBlueStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb,
                                        HorizontalAlignment.CENTER,
                                        VERY_LIGHT_BLUE, fontBlack9);
                        PoiUtils.addBorders(fondoLightBlueStyle, BorderStyle.THIN, IndexedColors.WHITE);
                        XSSFCellStyle fondoLightOrangeStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb,
                                        HorizontalAlignment.CENTER, VERY_LIGHT_ORANGE, fontBlack9b);
                        PoiUtils.addBorders(fondoLightOrangeStyle, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);
                        XSSFCellStyle fondoLightWhiteStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb,
                                        HorizontalAlignment.CENTER,
                                        WHITE, fontBlack9b);
                        PoiUtils.addBorders(fondoLightWhiteStyle, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);
                        XSSFCellStyle fondoLightWhiteStyleRight = PoiUtils.createCellStyle((XSSFWorkbook) wb,
                                        HorizontalAlignment.RIGHT, WHITE, fontBlack9b);
                        PoiUtils.addBorders(fondoLightWhiteStyleRight, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);
                        XSSFCellStyle fondoLightYellowStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb,
                                        HorizontalAlignment.CENTER, VERY_LIGHT_YELLOW, fontBlack9b);
                        PoiUtils.addBorders(fondoLightYellowStyle, BorderStyle.THIN, IndexedColors.WHITE);
                        XSSFCellStyle fondoLightGreenStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb,
                                        HorizontalAlignment.CENTER,
                                        VERY_LIGHT_GREEN, fontBlack9b);
                        PoiUtils.addBorders(fondoLightGreenStyle, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);
                        XSSFCellStyle fondoLightRedStyleRedFont = PoiUtils.createCellStyle((XSSFWorkbook) wb,
                                        HorizontalAlignment.CENTER, VERY_LIGHT_RED, fontRed9b);
                        PoiUtils.addBorders(fondoLightRedStyleRedFont, BorderStyle.THIN, IndexedColors.WHITE);
                        XSSFCellStyle fondoLightRedStyleRightRedFont = PoiUtils.createCellStyle((XSSFWorkbook) wb,
                                        HorizontalAlignment.RIGHT, VERY_LIGHT_RED, fontRed9b);
                        PoiUtils.addBorders(fondoLightRedStyleRightRedFont, BorderStyle.THIN, IndexedColors.WHITE);

                        // FONDO AZUL
                        XSSFCellStyle cabeceraStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb,
                                        HorizontalAlignment.LEFT,
                                        GERENCIA_BLUE, cabeceraFont);
                        XSSFCellStyle subHeaderStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb,
                                        HorizontalAlignment.CENTER,
                                        GERENCIA_BLUE, fontWhite9b);
                        PoiUtils.addBorders(subHeaderStyle, BorderStyle.THIN, IndexedColors.WHITE);

                        // FONDO PLOMO
                        XSSFCellStyle dataStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb,
                                        HorizontalAlignment.CENTER,
                                        GERENCIA_GREY, fontBlack9b);
                        PoiUtils.addBorders(dataStyle, BorderStyle.THIN, IndexedColors.WHITE);
                        XSSFCellStyle dataLeftStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb,
                                        HorizontalAlignment.LEFT,
                                        GERENCIA_GREY, fontBlack9b);
                        PoiUtils.addBorders(dataLeftStyle, BorderStyle.THIN, IndexedColors.WHITE);

                        // FONDO PLOMO BAJO
                        XSSFCellStyle dataGreenStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb,
                                        HorizontalAlignment.CENTER,
                                        LIGHT_GREY, fontGreen9b);
                        PoiUtils.addBorders(dataGreenStyle, BorderStyle.THIN, IndexedColors.WHITE);
                        XSSFCellStyle dataRedStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb,
                                        HorizontalAlignment.CENTER,
                                        LIGHT_GREY, fontRed9b);
                        PoiUtils.addBorders(dataRedStyle, BorderStyle.THIN, IndexedColors.WHITE);
                        XSSFCellStyle dataGreyStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb,
                                        HorizontalAlignment.CENTER,
                                        LIGHT_GREY, fontGrey9b);
                        PoiUtils.addBorders(dataGreyStyle, BorderStyle.THIN, IndexedColors.WHITE);
                        XSSFCellStyle dataYellowStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb,
                                        HorizontalAlignment.CENTER,
                                        LIGHT_GREY, fontYellow9b);
                        PoiUtils.addBorders(dataYellowStyle, BorderStyle.THIN, IndexedColors.WHITE);
                        XSSFCellStyle dataLightBlueStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb,
                                        HorizontalAlignment.CENTER,
                                        LIGHT_GREY, fontLightBlue9b);
                        PoiUtils.addBorders(dataLightBlueStyle, BorderStyle.THIN, IndexedColors.WHITE);

                        XSSFCellStyle dataStyle2 = PoiUtils.createCellStyle((XSSFWorkbook) wb,
                                        HorizontalAlignment.CENTER,
                                        LIGHT_GREY, fontBlack9b);
                        PoiUtils.addBorders(dataStyle2, BorderStyle.THIN, IndexedColors.WHITE);
                        XSSFCellStyle fondoGreyStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb,
                                        HorizontalAlignment.CENTER,
                                        LIGHT_GREY, fontBlack9);
                        PoiUtils.addBorders(fondoGreyStyle, BorderStyle.THIN, IndexedColors.WHITE);
                        XSSFCellStyle dataStyleRight = PoiUtils.createCellStyle((XSSFWorkbook) wb,
                                        HorizontalAlignment.RIGHT,
                                        LIGHT_GREY, fontBlack9b);
                        PoiUtils.addBorders(dataStyleRight, BorderStyle.THIN, IndexedColors.WHITE);
                        XSSFCellStyle fondoGreyStyleRight = PoiUtils.createCellStyle((XSSFWorkbook) wb,
                                        HorizontalAlignment.RIGHT,
                                        LIGHT_GREY, fontBlack9);
                        PoiUtils.addBorders(fondoGreyStyleRight, BorderStyle.THIN, IndexedColors.WHITE);
                        XSSFCellStyle dataStyleLeft = PoiUtils.createCellStyle((XSSFWorkbook) wb,
                                        HorizontalAlignment.LEFT,
                                        LIGHT_GREY, fontBlack9b);
                        PoiUtils.addBorders(dataStyleLeft, BorderStyle.THIN, IndexedColors.WHITE);
                        XSSFCellStyle fondoGreyStyleLeft = PoiUtils.createCellStyle((XSSFWorkbook) wb,
                                        HorizontalAlignment.LEFT,
                                        LIGHT_GREY, fontBlack9);
                        PoiUtils.addBorders(fondoGreyStyleLeft, BorderStyle.THIN, IndexedColors.WHITE);
                        XSSFCellStyle fondogGreyStyleBlue = PoiUtils.createCellStyle((XSSFWorkbook) wb,
                                        HorizontalAlignment.CENTER,
                                        LIGHT_GREY, fontBlue9b);
                        PoiUtils.addBorders(fondogGreyStyleBlue, BorderStyle.THIN, IndexedColors.WHITE);

                        // </editor-fold>
                        // </editor-fold>
                        String sheetName = "REPORTE";
                        XSSFSheet sheet = wb.createSheet(sheetName);

                        int rowNum = 0;

                        // <editor-fold defaultstate="collapsed" desc=" R E S U M E N ">
                        Row res0 = sheet.createRow(rowNum);
                        int colNum = 0;

                        Cell resumenTit = res0.createCell(colNum);
                        resumenTit.setCellValue(" R E S U M E N ");
                        resumenTit.setCellStyle(fondoBlackStyle);
                        CellRangeAddress region = CellRangeAddress.valueOf("A1:J1");
                        sheet.addMergedRegion(region);
                        PoiUtils.addBorders(region, BorderStyle.THIN, IndexedColors.WHITE, sheet);

                        rowNum++;
                        colNum = 0;

                        Row res2 = sheet.createRow(rowNum);

                        Cell rucRes = res2.createCell(colNum);
                        rucRes.setCellValue("RUC");
                        rucRes.setCellStyle(fondoBlackStyle);
                        colNum++;

                        Cell actRes = res2.createCell(colNum);
                        actRes.setCellValue("ACTIVO");
                        actRes.setCellStyle(fondoBlackStyle);
                        colNum++;

                        Cell bajaRes = res2.createCell(colNum);
                        bajaRes.setCellValue("BAJA");
                        bajaRes.setCellStyle(fondoBlackStyle);
                        colNum++;

                        Cell libreRes = res2.createCell(colNum);
                        libreRes.setCellValue("LIBRE");
                        libreRes.setCellStyle(fondoBlackStyle);
                        colNum++;

                        Cell retRes = res2.createCell(colNum);
                        retRes.setCellValue("RETIRADO");
                        retRes.setCellStyle(fondoBlackStyle);
                        colNum++;

                        Cell suspRes = res2.createCell(colNum);
                        suspRes.setCellValue("SUSPENDIDO");
                        suspRes.setCellStyle(fondoBlackStyle);
                        colNum++;

                        Cell extRes = res2.createCell(colNum);
                        extRes.setCellValue("EXTERNO");
                        extRes.setCellStyle(fondoBlackStyle);
                        colNum++;

                        Cell cerRes = res2.createCell(colNum);
                        cerRes.setCellValue("CERO");
                        cerRes.setCellStyle(fondoBlackStyle);
                        colNum++;

                        Cell morRes = res2.createCell(colNum);
                        morRes.setCellValue("MOROSO");
                        morRes.setCellStyle(fondoBlackStyle);
                        colNum++;

                        Cell totalRes = res2.createCell(colNum);
                        totalRes.setCellValue("TOTAL");
                        totalRes.setCellStyle(fondoBlackStyle);

                        rowNum++;
                        colNum = 0;

                        Row res3 = sheet.createRow(rowNum);

                        Cell ruc0 = res3.createCell(colNum);
                        ruc0.setCellValue("0");
                        ruc0.setCellStyle(dataLightBlueStyle);
                        colNum++;

                        Cell activo0 = res3.createCell(colNum);
                        activo0.setCellStyle(dataStyle2);
                        colNum++;

                        Cell baja0 = res3.createCell(colNum);
                        baja0.setCellStyle(dataStyle2);
                        colNum++;

                        Cell libre0 = res3.createCell(colNum);
                        libre0.setCellStyle(dataStyle2);
                        colNum++;

                        Cell retir0 = res3.createCell(colNum);
                        retir0.setCellStyle(dataStyle2);
                        colNum++;

                        Cell susp0 = res3.createCell(colNum);
                        susp0.setCellStyle(dataStyle2);
                        colNum++;

                        Cell externo0 = res3.createCell(colNum);
                        externo0.setCellStyle(dataStyle2);
                        colNum++;

                        Cell cero0 = res3.createCell(colNum);
                        cero0.setCellStyle(dataStyle2);
                        colNum++;

                        Cell moroso0 = res3.createCell(colNum);
                        moroso0.setCellStyle(dataStyle2);
                        colNum++;

                        Cell total0 = res3.createCell(colNum);
                        total0.setCellStyle(dataLightBlueStyle);

                        rowNum++;
                        colNum = 0;

                        Row res4 = sheet.createRow(rowNum);

                        Cell ruc1 = res4.createCell(colNum);
                        ruc1.setCellValue("1");
                        ruc1.setCellStyle(dataLightBlueStyle);
                        colNum++;

                        Cell activo1 = res4.createCell(colNum);
                        activo1.setCellStyle(dataStyle2);
                        colNum++;

                        Cell baja1 = res4.createCell(colNum);
                        baja1.setCellStyle(dataStyle2);
                        colNum++;

                        Cell libre1 = res4.createCell(colNum);
                        libre1.setCellStyle(dataStyle2);
                        colNum++;

                        Cell retir1 = res4.createCell(colNum);
                        retir1.setCellStyle(dataStyle2);
                        colNum++;

                        Cell susp1 = res4.createCell(colNum);
                        susp1.setCellStyle(dataStyle2);
                        colNum++;

                        Cell externo1 = res4.createCell(colNum);
                        externo1.setCellStyle(dataStyle2);
                        colNum++;

                        Cell cero1 = res4.createCell(colNum);
                        cero1.setCellStyle(dataStyle2);
                        colNum++;

                        Cell moroso1 = res4.createCell(colNum);
                        moroso1.setCellStyle(dataStyle2);
                        colNum++;

                        Cell total1 = res4.createCell(colNum);
                        total1.setCellStyle(dataLightBlueStyle);

                        rowNum++;
                        colNum = 0;

                        Row res5 = sheet.createRow(rowNum);

                        Cell ruc2 = res5.createCell(colNum);
                        ruc2.setCellValue("2");
                        ruc2.setCellStyle(dataLightBlueStyle);
                        colNum++;

                        Cell activo2 = res5.createCell(colNum);
                        activo2.setCellStyle(dataStyle2);
                        colNum++;

                        Cell baja2 = res5.createCell(colNum);
                        baja2.setCellStyle(dataStyle2);
                        colNum++;

                        Cell libre2 = res5.createCell(colNum);
                        libre2.setCellStyle(dataStyle2);
                        colNum++;

                        Cell retir2 = res5.createCell(colNum);
                        retir2.setCellStyle(dataStyle2);
                        colNum++;

                        Cell susp2 = res5.createCell(colNum);
                        susp2.setCellStyle(dataStyle2);
                        colNum++;

                        Cell externo2 = res5.createCell(colNum);
                        externo2.setCellStyle(dataStyle2);
                        colNum++;

                        Cell cero2 = res5.createCell(colNum);
                        cero2.setCellStyle(dataStyle2);
                        colNum++;

                        Cell moroso2 = res5.createCell(colNum);
                        moroso2.setCellStyle(dataStyle2);
                        colNum++;

                        Cell total2 = res5.createCell(colNum);
                        total2.setCellStyle(dataLightBlueStyle);

                        rowNum++;
                        colNum = 0;

                        Row res6 = sheet.createRow(rowNum);

                        Cell ruc3 = res6.createCell(colNum);
                        ruc3.setCellValue("3");
                        ruc3.setCellStyle(dataLightBlueStyle);
                        colNum++;

                        Cell activo3 = res6.createCell(colNum);
                        activo3.setCellStyle(dataStyle2);
                        colNum++;

                        Cell baja3 = res6.createCell(colNum);
                        baja3.setCellStyle(dataStyle2);
                        colNum++;

                        Cell libre3 = res6.createCell(colNum);
                        libre3.setCellStyle(dataStyle2);
                        colNum++;

                        Cell retir3 = res6.createCell(colNum);
                        retir3.setCellStyle(dataStyle2);
                        colNum++;

                        Cell susp3 = res6.createCell(colNum);
                        susp3.setCellStyle(dataStyle2);
                        colNum++;

                        Cell externo3 = res6.createCell(colNum);
                        externo3.setCellStyle(dataStyle2);
                        colNum++;

                        Cell cero3 = res6.createCell(colNum);
                        cero3.setCellStyle(dataStyle2);
                        colNum++;

                        Cell moroso3 = res6.createCell(colNum);
                        moroso3.setCellStyle(dataStyle2);
                        colNum++;

                        Cell total3 = res6.createCell(colNum);
                        total3.setCellStyle(dataLightBlueStyle);

                        rowNum++;
                        colNum = 0;

                        Row res7 = sheet.createRow(rowNum);

                        Cell ruc4 = res7.createCell(colNum);
                        ruc4.setCellValue("4");
                        ruc4.setCellStyle(dataLightBlueStyle);
                        colNum++;

                        Cell activo4 = res7.createCell(colNum);
                        activo4.setCellStyle(dataStyle2);
                        colNum++;

                        Cell baja4 = res7.createCell(colNum);
                        baja4.setCellStyle(dataStyle2);
                        colNum++;

                        Cell libre4 = res7.createCell(colNum);
                        libre4.setCellStyle(dataStyle2);
                        colNum++;

                        Cell retir4 = res7.createCell(colNum);
                        retir4.setCellStyle(dataStyle2);
                        colNum++;

                        Cell susp4 = res7.createCell(colNum);
                        susp4.setCellStyle(dataStyle2);
                        colNum++;

                        Cell externo4 = res7.createCell(colNum);
                        externo4.setCellStyle(dataStyle2);
                        colNum++;

                        Cell cero4 = res7.createCell(colNum);
                        cero4.setCellStyle(dataStyle2);
                        colNum++;

                        Cell moroso4 = res7.createCell(colNum);
                        moroso4.setCellStyle(dataStyle2);
                        colNum++;

                        Cell total4 = res7.createCell(colNum);
                        total4.setCellStyle(dataLightBlueStyle);

                        rowNum++;
                        colNum = 0;

                        Row res8 = sheet.createRow(rowNum);

                        Cell ruc5 = res8.createCell(colNum);
                        ruc5.setCellValue("5");
                        ruc5.setCellStyle(dataLightBlueStyle);
                        colNum++;

                        Cell activo5 = res8.createCell(colNum);
                        activo5.setCellStyle(dataStyle2);
                        colNum++;

                        Cell baja5 = res8.createCell(colNum);
                        baja5.setCellStyle(dataStyle2);
                        colNum++;

                        Cell libre5 = res8.createCell(colNum);
                        libre5.setCellStyle(dataStyle2);
                        colNum++;

                        Cell retir5 = res8.createCell(colNum);
                        retir5.setCellStyle(dataStyle2);
                        colNum++;

                        Cell susp5 = res8.createCell(colNum);
                        susp5.setCellStyle(dataStyle2);
                        colNum++;

                        Cell externo5 = res8.createCell(colNum);
                        externo5.setCellStyle(dataStyle2);
                        colNum++;

                        Cell cero5 = res8.createCell(colNum);
                        cero5.setCellStyle(dataStyle2);
                        colNum++;

                        Cell moroso5 = res8.createCell(colNum);
                        moroso5.setCellStyle(dataStyle2);
                        colNum++;

                        Cell total5 = res8.createCell(colNum);
                        total5.setCellStyle(dataLightBlueStyle);

                        rowNum++;
                        colNum = 0;

                        Row res9 = sheet.createRow(rowNum);

                        Cell ruc6 = res9.createCell(colNum);
                        ruc6.setCellValue("6");
                        ruc6.setCellStyle(dataLightBlueStyle);
                        colNum++;

                        Cell activo6 = res9.createCell(colNum);
                        activo6.setCellStyle(dataStyle2);
                        colNum++;

                        Cell baja6 = res9.createCell(colNum);
                        baja6.setCellStyle(dataStyle2);
                        colNum++;

                        Cell libre6 = res9.createCell(colNum);
                        libre6.setCellStyle(dataStyle2);
                        colNum++;

                        Cell retir6 = res9.createCell(colNum);
                        retir6.setCellStyle(dataStyle2);
                        colNum++;

                        Cell susp6 = res9.createCell(colNum);
                        susp6.setCellStyle(dataStyle2);
                        colNum++;

                        Cell externo6 = res9.createCell(colNum);
                        externo6.setCellStyle(dataStyle2);
                        colNum++;

                        Cell cero6 = res9.createCell(colNum);
                        cero6.setCellStyle(dataStyle2);
                        colNum++;

                        Cell moroso6 = res9.createCell(colNum);
                        moroso6.setCellStyle(dataStyle2);
                        colNum++;

                        Cell total6 = res9.createCell(colNum);
                        total6.setCellStyle(dataLightBlueStyle);

                        rowNum++;
                        colNum = 0;

                        Row res10 = sheet.createRow(rowNum);

                        Cell ruc7 = res10.createCell(colNum);
                        ruc7.setCellValue("7");
                        ruc7.setCellStyle(dataLightBlueStyle);
                        colNum++;

                        Cell activo7 = res10.createCell(colNum);
                        activo7.setCellStyle(dataStyle2);
                        colNum++;

                        Cell baja7 = res10.createCell(colNum);
                        baja7.setCellStyle(dataStyle2);
                        colNum++;

                        Cell libre7 = res10.createCell(colNum);
                        libre7.setCellStyle(dataStyle2);
                        colNum++;

                        Cell retir7 = res10.createCell(colNum);
                        retir7.setCellStyle(dataStyle2);
                        colNum++;

                        Cell susp7 = res10.createCell(colNum);
                        susp7.setCellStyle(dataStyle2);
                        colNum++;

                        Cell externo7 = res10.createCell(colNum);
                        externo7.setCellStyle(dataStyle2);
                        colNum++;

                        Cell cero7 = res10.createCell(colNum);
                        cero7.setCellStyle(dataStyle2);
                        colNum++;

                        Cell moroso7 = res10.createCell(colNum);
                        moroso7.setCellStyle(dataStyle2);
                        colNum++;

                        Cell total7 = res10.createCell(colNum);
                        total7.setCellStyle(dataLightBlueStyle);

                        rowNum++;
                        colNum = 0;

                        Row res11 = sheet.createRow(rowNum);

                        Cell ruc8 = res11.createCell(colNum);
                        ruc8.setCellValue("8");
                        ruc8.setCellStyle(dataLightBlueStyle);
                        colNum++;

                        Cell activo8 = res11.createCell(colNum);
                        activo8.setCellStyle(dataStyle2);
                        colNum++;

                        Cell baja8 = res11.createCell(colNum);
                        baja8.setCellStyle(dataStyle2);
                        colNum++;

                        Cell libre8 = res11.createCell(colNum);
                        libre8.setCellStyle(dataStyle2);
                        colNum++;

                        Cell retir8 = res11.createCell(colNum);
                        retir8.setCellStyle(dataStyle2);
                        colNum++;

                        Cell susp8 = res11.createCell(colNum);
                        susp8.setCellStyle(dataStyle2);
                        colNum++;

                        Cell externo8 = res11.createCell(colNum);
                        externo8.setCellStyle(dataStyle2);
                        colNum++;

                        Cell cero8 = res11.createCell(colNum);
                        cero8.setCellStyle(dataStyle2);
                        colNum++;

                        Cell moroso8 = res11.createCell(colNum);
                        moroso8.setCellStyle(dataStyle2);
                        colNum++;

                        Cell total8 = res11.createCell(colNum);
                        total8.setCellStyle(dataLightBlueStyle);

                        rowNum++;
                        colNum = 0;

                        Row res12 = sheet.createRow(rowNum);

                        Cell ruc9 = res12.createCell(colNum);
                        ruc9.setCellValue("9");
                        ruc9.setCellStyle(dataLightBlueStyle);
                        colNum++;

                        Cell activo9 = res12.createCell(colNum);
                        activo9.setCellStyle(dataStyle2);
                        colNum++;

                        Cell baja9 = res12.createCell(colNum);
                        baja9.setCellStyle(dataStyle2);
                        colNum++;

                        Cell libre9 = res12.createCell(colNum);
                        libre9.setCellStyle(dataStyle2);
                        colNum++;

                        Cell retir9 = res12.createCell(colNum);
                        retir9.setCellStyle(dataStyle2);
                        colNum++;

                        Cell susp9 = res12.createCell(colNum);
                        susp9.setCellStyle(dataStyle2);
                        colNum++;

                        Cell externo9 = res12.createCell(colNum);
                        externo9.setCellStyle(dataStyle2);
                        colNum++;

                        Cell cero9 = res12.createCell(colNum);
                        cero9.setCellStyle(dataStyle2);
                        colNum++;

                        Cell moroso9 = res12.createCell(colNum);
                        moroso9.setCellStyle(dataStyle2);
                        colNum++;

                        Cell total9 = res12.createCell(colNum);
                        total9.setCellStyle(dataLightBlueStyle);

                        rowNum++;
                        colNum = 0;

                        Row res13 = sheet.createRow(rowNum);

                        Cell rucTot = res13.createCell(colNum);
                        rucTot.setCellValue("TOTAL");
                        rucTot.setCellStyle(subHeaderStyle);
                        colNum++;

                        Cell activoTot = res13.createCell(colNum);
                        activoTot.setCellStyle(subHeaderStyle);
                        colNum++;

                        Cell bajaTot = res13.createCell(colNum);
                        bajaTot.setCellStyle(subHeaderStyle);
                        colNum++;

                        Cell libreTot = res13.createCell(colNum);
                        libreTot.setCellStyle(subHeaderStyle);
                        colNum++;

                        Cell retirTot = res13.createCell(colNum);
                        retirTot.setCellStyle(subHeaderStyle);
                        colNum++;

                        Cell suspTot = res13.createCell(colNum);
                        suspTot.setCellStyle(subHeaderStyle);
                        colNum++;

                        Cell externoTot = res13.createCell(colNum);
                        externoTot.setCellStyle(subHeaderStyle);
                        colNum++;

                        Cell ceroTot = res13.createCell(colNum);
                        ceroTot.setCellStyle(subHeaderStyle);
                        colNum++;

                        Cell morosoTot = res13.createCell(colNum);
                        morosoTot.setCellStyle(subHeaderStyle);
                        colNum++;

                        Cell totalTot = res13.createCell(colNum);
                        totalTot.setCellStyle(subHeaderStyle);
                        // </editor-fold>

                        rowNum = 15;
                        colNum = 0;

                        // <editor-fold defaultstate="collapsed" desc="CABECERA">
                        Row cabecera = sheet.createRow(rowNum);

                        Cell cabeceraN = cabecera.createCell(colNum);
                        cabeceraN.setCellStyle(subHeaderStyle);
                        cabeceraN.setCellValue("N°");
                        colNum++;

                        if (true) {
                                Cell cabeceraCategoria = cabecera.createCell(colNum);
                                cabeceraCategoria.setCellStyle(subHeaderStyle);
                                cabeceraCategoria.setCellValue("CAT");
                                colNum++;

                                Cell cabeceraCategoriaGrupoE = cabecera.createCell(colNum);
                                cabeceraCategoriaGrupoE.setCellStyle(subHeaderStyle);
                                cabeceraCategoriaGrupoE.setCellValue("CGE");
                                colNum++;

                                Cell cabeceraCategoriaStore = cabecera.createCell(colNum);
                                cabeceraCategoriaStore.setCellStyle(subHeaderStyle);
                                cabeceraCategoriaStore.setCellValue("STO");
                                colNum++;

                                Cell cabeceraEstado = cabecera.createCell(colNum);
                                cabeceraEstado.setCellStyle(subHeaderStyle);
                                cabeceraEstado.setCellValue("Estado");
                                colNum++;

                                Cell cabeceraAltaCom = cabecera.createCell(colNum);
                                cabeceraAltaCom.setCellStyle(subHeaderStyle);
                                cabeceraAltaCom.setCellValue("Alta.com");
                                colNum++;

                                Cell cabeceraPInicioCom = cabecera.createCell(colNum);
                                cabeceraPInicioCom.setCellStyle(subHeaderStyle);
                                cabeceraPInicioCom.setCellValue("P. Inicio.Com");
                                colNum++;

                                Cell cabeceraCodigoG = cabecera.createCell(colNum);
                                cabeceraCodigoG.setCellStyle(subHeaderStyle);
                                cabeceraCodigoG.setCellValue("Código G");
                                colNum++;

                                Cell cabeceraRuc = cabecera.createCell(colNum);
                                cabeceraRuc.setCellStyle(subHeaderStyle);
                                cabeceraRuc.setCellValue("RUC");
                                colNum++;

                                Cell cabeceraY = cabecera.createCell(colNum);
                                cabeceraY.setCellStyle(subHeaderStyle);
                                cabeceraY.setCellValue("Y");
                                colNum++;

                                Cell cabeceraC = cabecera.createCell(colNum);
                                cabeceraC.setCellStyle(subHeaderStyle);
                                cabeceraC.setCellValue("C");
                                colNum++;

                                Cell cabeceraRZ = cabecera.createCell(colNum);
                                cabeceraRZ.setCellStyle(subHeaderStyle);
                                cabeceraRZ.setCellValue("Razón Social");
                                colNum++;

                                Cell cabeceraNombreCorto = cabecera.createCell(colNum);
                                cabeceraNombreCorto.setCellStyle(subHeaderStyle);
                                cabeceraNombreCorto.setCellValue("Nombre Corto");
                                colNum++;

                                Cell cabeceraRubro = cabecera.createCell(colNum);
                                cabeceraRubro.setCellStyle(subHeaderStyle);
                                cabeceraRubro.setCellValue("Rubro");
                                colNum++;

                                Cell cabeceraTServicio = cabecera.createCell(colNum);
                                cabeceraTServicio.setCellStyle(subHeaderStyle);
                                cabeceraTServicio.setCellValue("T. Servicio");
                                colNum++;

                                Cell cabeceraGEconomico = cabecera.createCell(colNum);
                                cabeceraGEconomico.setCellStyle(subHeaderStyle);
                                cabeceraGEconomico.setCellValue("G. Económico");
                                colNum++;

                                Cell cabeceraPFinCom = cabecera.createCell(colNum);
                                cabeceraPFinCom.setCellStyle(subHeaderStyle);
                                cabeceraPFinCom.setCellValue("P. Fin.Com");
                                colNum++;
                        }

                        if (true) {
                                Cell cabeceraFInscripcionT = cabecera.createCell(colNum);
                                cabeceraFInscripcionT.setCellStyle(subHeaderStyle);
                                cabeceraFInscripcionT.setCellValue("F. Incrip. T.");
                                colNum++;

                                Cell cabeceraFInicioT = cabecera.createCell(colNum);
                                cabeceraFInicioT.setCellStyle(subHeaderStyle);
                                cabeceraFInicioT.setCellValue("F. Inicio T.");
                                colNum++;

                                Cell cabeceraFSuspensionT = cabecera.createCell(colNum);
                                cabeceraFSuspensionT.setCellStyle(subHeaderStyle);
                                cabeceraFSuspensionT.setCellValue("F. Suspensión T.");
                                colNum++;

                                Cell cabeceraFBajaT = cabecera.createCell(colNum);
                                cabeceraFBajaT.setCellStyle(subHeaderStyle);
                                cabeceraFBajaT.setCellValue("F. Baja T.");
                                colNum++;

                                Cell cabeceraFRetiroT = cabecera.createCell(colNum);
                                cabeceraFRetiroT.setCellStyle(subHeaderStyle);
                                cabeceraFRetiroT.setCellValue("F. Retiro T.");
                                colNum++;

                                Cell cabeceraPle = cabecera.createCell(colNum);
                                cabeceraPle.setCellStyle(subHeaderStyle);
                                cabeceraPle.setCellValue("PLE");
                                colNum++;

                                Cell cabeceraPrico = cabecera.createCell(colNum);
                                cabeceraPrico.setCellStyle(subHeaderStyle);
                                cabeceraPrico.setCellValue("PRICO");
                                colNum++;

                                Cell cabeceraBuc = cabecera.createCell(colNum);
                                cabeceraBuc.setCellStyle(subHeaderStyle);
                                cabeceraBuc.setCellValue("BUC");
                                colNum++;

                                Cell cabeceraAgentePer = cabecera.createCell(colNum);
                                cabeceraAgentePer.setCellStyle(subHeaderStyle);
                                cabeceraAgentePer.setCellValue("Ag. Per.");
                                colNum++;

                                Cell cabeceraAgenteRet = cabecera.createCell(colNum);
                                cabeceraAgenteRet.setCellStyle(subHeaderStyle);
                                cabeceraAgenteRet.setCellValue("Ag. Ret.");
                                colNum++;

                                Cell cabeceraEmisorED = cabecera.createCell(colNum);
                                cabeceraEmisorED.setCellStyle(subHeaderStyle);
                                cabeceraEmisorED.setCellValue("Emisor E. Desde");
                                colNum++;

                                Cell cabeceraPortalSunat = cabecera.createCell(colNum);
                                cabeceraPortalSunat.setCellStyle(subHeaderStyle);
                                cabeceraPortalSunat.setCellValue("Portal SUNAT");
                                colNum++;

                                Cell cabeceraSistemaContribuyente = cabecera.createCell(colNum);
                                cabeceraSistemaContribuyente.setCellStyle(subHeaderStyle);
                                cabeceraSistemaContribuyente.setCellValue("Sist. Contri.");
                                colNum++;

                                Cell cabeceraRT = cabecera.createCell(colNum);
                                cabeceraRT.setCellStyle(subHeaderStyle);
                                cabeceraRT.setCellValue("RT");
                                colNum++;

                                Cell cabecera1RA = cabecera.createCell(colNum);
                                cabecera1RA.setCellStyle(subHeaderStyle);
                                cabecera1RA.setCellValue("1RA");
                                colNum++;

                                Cell cabecera2DA = cabecera.createCell(colNum);
                                cabecera2DA.setCellStyle(subHeaderStyle);
                                cabecera2DA.setCellValue("2DA");
                                colNum++;

                                Cell cabecera3RA = cabecera.createCell(colNum);
                                cabecera3RA.setCellStyle(subHeaderStyle);
                                cabecera3RA.setCellValue("3RA");
                                colNum++;

                                Cell cabecera4TA = cabecera.createCell(colNum);
                                cabecera4TA.setCellStyle(subHeaderStyle);
                                cabecera4TA.setCellValue("4TA");
                                colNum++;

                                Cell cabecera5TA = cabecera.createCell(colNum);
                                cabecera5TA.setCellStyle(subHeaderStyle);
                                cabecera5TA.setCellValue("5TA");
                                colNum++;

                                Cell cabeceraRLMicro = cabecera.createCell(colNum);
                                cabeceraRLMicro.setCellStyle(subHeaderStyle);
                                cabeceraRLMicro.setCellValue("RL. Micro");
                                colNum++;

                                Cell cabeceraRLPequeña = cabecera.createCell(colNum);
                                cabeceraRLPequeña.setCellStyle(subHeaderStyle);
                                cabeceraRLPequeña.setCellValue("RL. Pequeña");
                                colNum++;

                                Cell cabeceraRLGeneral = cabecera.createCell(colNum);
                                cabeceraRLGeneral.setCellStyle(subHeaderStyle);
                                cabeceraRLGeneral.setCellValue("RL. General");
                                colNum++;

                                Cell cabeceraRLConstruccion = cabecera.createCell(colNum);
                                cabeceraRLConstruccion.setCellStyle(subHeaderStyle);
                                cabeceraRLConstruccion.setCellValue("RL. Construcción");
                                colNum++;

                                Cell cabeceraRLAgrario = cabecera.createCell(colNum);
                                cabeceraRLAgrario.setCellStyle(subHeaderStyle);
                                cabeceraRLAgrario.setCellValue("RL. Agrario");
                                colNum++;

                                Cell cabeceraRLAcreditado = cabecera.createCell(colNum);
                                cabeceraRLAcreditado.setCellStyle(subHeaderStyle);
                                cabeceraRLAcreditado.setCellValue("RL. Acreditado");
                                colNum++;

                                Cell cabeceraRLNoAcreditado = cabecera.createCell(colNum);
                                cabeceraRLNoAcreditado.setCellStyle(subHeaderStyle);
                                cabeceraRLNoAcreditado.setCellValue("RL. No Acreditado");
                                colNum++;
                        }

                        // //CLAVE SOL -> MAIN
                        // if (true) {
                        // Cell cabeceraSolU = cabecera.createCell(colNum);
                        // cabeceraSolU.setCellStyle(subHeaderStyle);
                        // cabeceraSolU.setCellValue("Main U.");
                        // colNum++;
                        //
                        // Cell cabeceraSolC = cabecera.createCell(colNum);
                        // cabeceraSolC.setCellStyle(subHeaderStyle);
                        // cabeceraSolC.setCellValue("Main C.");
                        // colNum++;
                        // }
                        // if (true) {
                        // Cell cabeceraUpsU = cabecera.createCell(colNum);
                        // cabeceraUpsU.setCellStyle(subHeaderStyle);
                        // cabeceraUpsU.setCellValue("UPS U.");
                        // colNum++;
                        //
                        // Cell cabeceraUpsC = cabecera.createCell(colNum);
                        // cabeceraUpsC.setCellStyle(subHeaderStyle);
                        // cabeceraUpsC.setCellValue("UPS C.");
                        // colNum++;
                        // }
                        // if (true) {
                        // //CLAVE SOLDIER -> GEAR
                        // Cell cabeceraSoldierU = cabecera.createCell(colNum);
                        // cabeceraSoldierU.setCellStyle(subHeaderStyle);
                        // cabeceraSoldierU.setCellValue("Gear U.");
                        // colNum++;
                        //
                        // Cell cabeceraSoldierC = cabecera.createCell(colNum);
                        // cabeceraSoldierC.setCellStyle(subHeaderStyle);
                        // cabeceraSoldierC.setCellValue("Gear C.");
                        // colNum++;
                        //
                        // Cell cabeceraSignerU = cabecera.createCell(colNum);
                        // cabeceraSignerU.setCellStyle(subHeaderStyle);
                        // cabeceraSignerU.setCellValue("Signer U.");
                        // colNum++;
                        //
                        // Cell cabeceraSignerC = cabecera.createCell(colNum);
                        // cabeceraSignerC.setCellStyle(subHeaderStyle);
                        // cabeceraSignerC.setCellValue("Signer C.");
                        // colNum++;
                        //
                        // Cell cabeceraAfpNetU = cabecera.createCell(colNum);
                        // cabeceraAfpNetU.setCellStyle(subHeaderStyle);
                        // cabeceraAfpNetU.setCellValue("AfpNet U.");
                        // colNum++;
                        //
                        // Cell cabeceraAfpNetC = cabecera.createCell(colNum);
                        // cabeceraAfpNetC.setCellStyle(subHeaderStyle);
                        // cabeceraAfpNetC.setCellValue("AfpNet C.");
                        // colNum++;
                        //
                        // Cell cabeceraSisU = cabecera.createCell(colNum);
                        // cabeceraSisU.setCellStyle(subHeaderStyle);
                        // cabeceraSisU.setCellValue("Sis U.");
                        // colNum++;
                        //
                        // Cell cabeceraSisC = cabecera.createCell(colNum);
                        // cabeceraSisC.setCellStyle(subHeaderStyle);
                        // cabeceraSisC.setCellValue("Sis C.");
                        // colNum++;
                        // }
                        if (true) {
                                Cell cabeceraDeoCuenta = cabecera.createCell(colNum);
                                cabeceraDeoCuenta.setCellStyle(subHeaderStyle);
                                cabeceraDeoCuenta.setCellValue("DEO Cuenta");
                                colNum++;

                                Cell cabeceraDeoDNI = cabecera.createCell(colNum);
                                cabeceraDeoDNI.setCellStyle(subHeaderStyle);
                                cabeceraDeoDNI.setCellValue("DEO DNI");
                                colNum++;

                                Cell cabeceraDeoClave = cabecera.createCell(colNum);
                                cabeceraDeoClave.setCellStyle(subHeaderStyle);
                                cabeceraDeoClave.setCellValue("DEO Clave");
                                colNum++;
                        }

                        if (true) {
                                Cell cabeceraPdtAnualDesde = cabecera.createCell(colNum);
                                cabeceraPdtAnualDesde.setCellStyle(subHeaderStyle);
                                cabeceraPdtAnualDesde.setCellValue("PDT Anual D.");
                                colNum++;

                                Cell cabeceraPdtAnualHasta = cabecera.createCell(colNum);
                                cabeceraPdtAnualHasta.setCellStyle(subHeaderStyle);
                                cabeceraPdtAnualHasta.setCellValue("PDT Anual H.");
                                colNum++;

                                Cell cabeceraPdt621Desde = cabecera.createCell(colNum);
                                cabeceraPdt621Desde.setCellStyle(subHeaderStyle);
                                cabeceraPdt621Desde.setCellValue("PDT 621 D.");
                                colNum++;

                                Cell cabeceraPdt621Hasta = cabecera.createCell(colNum);
                                cabeceraPdt621Hasta.setCellStyle(subHeaderStyle);
                                cabeceraPdt621Hasta.setCellValue("PDT 621 H.");
                                colNum++;

                                Cell cabeceraPdt601Desde = cabecera.createCell(colNum);
                                cabeceraPdt601Desde.setCellStyle(subHeaderStyle);
                                cabeceraPdt601Desde.setCellValue("PDT 601 D.");
                                colNum++;

                                Cell cabeceraPdt601Hasta = cabecera.createCell(colNum);
                                cabeceraPdt601Hasta.setCellStyle(subHeaderStyle);
                                cabeceraPdt601Hasta.setCellValue("PDT 601 H.");
                                colNum++;

                                Cell cabeceraPdt617Desde = cabecera.createCell(colNum);
                                cabeceraPdt617Desde.setCellStyle(subHeaderStyle);
                                cabeceraPdt617Desde.setCellValue("PDT 617 D.");
                                colNum++;

                                Cell cabeceraPdt617Hasta = cabecera.createCell(colNum);
                                cabeceraPdt617Hasta.setCellStyle(subHeaderStyle);
                                cabeceraPdt617Hasta.setCellValue("PDT 617 H.");
                                colNum++;

                                Cell cabeceraPleComprasVentasDesde = cabecera.createCell(colNum);
                                cabeceraPleComprasVentasDesde.setCellStyle(subHeaderStyle);
                                cabeceraPleComprasVentasDesde.setCellValue("Ple C-V D.");
                                colNum++;

                                Cell cabeceraPleComprasVentasHasta = cabecera.createCell(colNum);
                                cabeceraPleComprasVentasHasta.setCellStyle(subHeaderStyle);
                                cabeceraPleComprasVentasHasta.setCellValue("Ple C-V H.");
                                colNum++;

                                Cell cabeceraPleDiarioDesde = cabecera.createCell(colNum);
                                cabeceraPleDiarioDesde.setCellStyle(subHeaderStyle);
                                cabeceraPleDiarioDesde.setCellValue("Ple Diario D.");
                                colNum++;

                                Cell cabeceraPleDiarioHasta = cabecera.createCell(colNum);
                                cabeceraPleDiarioHasta.setCellStyle(subHeaderStyle);
                                cabeceraPleDiarioHasta.setCellValue("Ple Diario H.");
                                colNum++;
                        }
                        rowNum++;
                        // </editor-fold>

                        int i = 1;

                        int inicioFilt = rowNum;
                        rowNum++;

                        // <editor-fold defaultstate="collapsed" desc="DATA">
                        for (ClienteExcelProjection c : list) {
                                Row data = sheet.createRow(rowNum);
                                colNum = 0;

                                Cell dataN = data.createCell(colNum);
                                dataN.setCellStyle(fondoBlackStyle);
                                dataN.setCellValue(i);
                                colNum++;

                                if (true) {
                                        Cell dataCategoria = data.createCell(colNum);
                                        dataCategoria.setCellStyle(dataStyle2);
                                        dataCategoria.setCellValue(c.getAbrCategoria());
                                        colNum++;

                                        Cell dataCategoriaGrupoE = data.createCell(colNum);
                                        dataCategoriaGrupoE.setCellStyle(dataStyle2);
                                        switch (c.getCategoriaGrupoE()) {
                                                case 0:
                                                        dataCategoriaGrupoE.setCellStyle(dataStyle2);
                                                        dataCategoriaGrupoE.setCellValue("");
                                                        break;
                                                case 1:
                                                        dataCategoriaGrupoE.setCellStyle(dataRedStyle);
                                                        dataCategoriaGrupoE.setCellValue("SI");
                                                        break;
                                        }
                                        colNum++;

                                        Cell dataCategoriaStore = data.createCell(colNum);
                                        dataCategoriaStore.setCellStyle(dataStyle2);
                                        switch (c.getCategoriaStore()) {
                                                case 0:
                                                        dataCategoriaStore.setCellStyle(dataStyle2);
                                                        dataCategoriaStore.setCellValue("");
                                                        break;
                                                case 1:
                                                        dataCategoriaStore.setCellStyle(dataRedStyle);
                                                        dataCategoriaStore.setCellValue("SI");
                                                        break;
                                        }
                                        colNum++;

                                        Cell dataEstado = data.createCell(colNum);
                                        dataEstado.setCellStyle(dataStyle2);
                                        dataEstado.setCellValue(c.getDescEstado());
                                        colNum++;

                                        Cell dataAltaCom = data.createCell(colNum);
                                        dataAltaCom.setCellStyle(fondoLightBlueStyle);
                                        dataAltaCom.setCellValue(c.getAcInicioCom());
                                        colNum++;

                                        Cell dataPInicioCom = data.createCell(colNum);
                                        dataPInicioCom.setCellStyle(fondoLightYellowStyle);
                                        dataPInicioCom.setCellValue(c.getAcPeriodoInicioCom());
                                        colNum++;

                                        Cell dataCodigoG = data.createCell(colNum);
                                        dataCodigoG.setCellStyle(fondoGreyStyle);
                                        dataCodigoG.setCellValue(c.getCodigoCliente());
                                        colNum++;

                                        Cell dataRuc = data.createCell(colNum);
                                        dataRuc.setCellStyle(dataStyle2);
                                        dataRuc.setCellValue(c.getRuc());
                                        colNum++;

                                        Cell dataY = data.createCell(colNum);
                                        dataY.setCellStyle(dataStyle2);
                                        dataY.setCellValue(c.getY());
                                        colNum++;

                                        Cell dataC = data.createCell(colNum);
                                        dataC.setCellStyle(dataStyle2);
                                        dataC.setCellValue(c.getAbrContribuyente());
                                        state.add(c.getDescEstado(), c.getY());
                                        colNum++;

                                        Cell dataRZ = data.createCell(colNum);
                                        dataRZ.setCellStyle(fondoGreyStyleLeft);
                                        dataRZ.setCellValue(c.getRazonSocial());
                                        colNum++;

                                        Cell dataNombreCorto = data.createCell(colNum);
                                        dataNombreCorto.setCellStyle(fondoGreyStyleLeft);
                                        dataNombreCorto.setCellValue(c.getNombreCorto());
                                        colNum++;

                                        Cell dataRubro = data.createCell(colNum);
                                        dataRubro.setCellStyle(fondoGreyStyleLeft);
                                        dataRubro.setCellValue(c.getDescRubro());
                                        colNum++;

                                        Cell dataTServicio = data.createCell(colNum);
                                        dataTServicio.setCellStyle(fondoGreyStyleLeft);
                                        dataTServicio.setCellValue(c.getDescServicio());
                                        colNum++;

                                        Cell dataGEconomico = data.createCell(colNum);
                                        dataGEconomico.setCellStyle(dataStyle2);
                                        dataGEconomico.setCellValue(c.getDescGrupoEconomico());
                                        colNum++;

                                        Cell dataPFinCom = data.createCell(colNum);
                                        dataPFinCom.setCellStyle(fondoLightRedStyle);
                                        dataPFinCom.setCellValue(c.getAcPeriodoFinCom());
                                        colNum++;
                                }

                                if (true) {
                                        Cell dataFInscripcionT = data.createCell(colNum);
                                        dataFInscripcionT.setCellStyle(dataStyle2);
                                        dataFInscripcionT.setCellValue(c.getFInscripcion());
                                        colNum++;

                                        Cell dataFInicioT = data.createCell(colNum);
                                        dataFInicioT.setCellStyle(dataStyle2);
                                        dataFInicioT.setCellValue(c.getAtInicio());
                                        colNum++;

                                        Cell dataFSuspensionT = data.createCell(colNum);
                                        dataFSuspensionT.setCellStyle(dataStyle2);
                                        dataFSuspensionT.setCellValue(c.getAtSuspension());
                                        colNum++;

                                        Cell dataFBajaT = data.createCell(colNum);
                                        dataFBajaT.setCellStyle(dataStyle2);
                                        dataFBajaT.setCellValue(c.getAtBaja());
                                        colNum++;

                                        Cell dataFRetiroT = data.createCell(colNum);
                                        dataFRetiroT.setCellStyle(dataStyle2);
                                        dataFRetiroT.setCellValue(c.getFRetiro());
                                        colNum++;

                                        Cell dataPle = data.createCell(colNum);
                                        dataPle.setCellStyle(dataStyle2);
                                        dataPle.setCellValue(c.getFPle());
                                        colNum++;

                                        Cell dataPrico = data.createCell(colNum);
                                        dataPrico.setCellStyle(dataStyle2);
                                        dataPrico.setCellValue(c.getFPrico());
                                        colNum++;

                                        Cell dataBuc = data.createCell(colNum);
                                        dataBuc.setCellStyle(dataStyle2);
                                        dataBuc.setCellValue(c.getFBuc());
                                        colNum++;

                                        Cell dataAgentePer = data.createCell(colNum);
                                        dataAgentePer.setCellStyle(dataStyle2);
                                        dataAgentePer.setCellValue(c.getFAgentePer());
                                        colNum++;

                                        Cell dataAgenteRet = data.createCell(colNum);
                                        dataAgenteRet.setCellStyle(dataStyle2);
                                        dataAgenteRet.setCellValue(c.getFAgenteRet());
                                        colNum++;

                                        Cell dataEmisorED = data.createCell(colNum);
                                        dataEmisorED.setCellStyle(dataStyle2);
                                        dataEmisorED.setCellValue(c.getFEmElec());
                                        colNum++;

                                        Cell dataPortalSunat = data.createCell(colNum);
                                        dataPortalSunat.setCellStyle(dataStyle2);
                                        dataPortalSunat.setCellValue(c.getFPortSunat());
                                        colNum++;

                                        Cell dataSistemaContribuyente = data.createCell(colNum);
                                        dataSistemaContribuyente.setCellStyle(dataStyle2);
                                        dataSistemaContribuyente.setCellValue(c.getFSistCont());
                                        colNum++;

                                        Cell dataRT = data.createCell(colNum);
                                        dataRT.setCellStyle(dataStyle2);
                                        dataRT.setCellValue(c.getRt());
                                        colNum++;

                                        Cell data1RA = data.createCell(colNum);
                                        data1RA.setCellStyle(dataStyle2);
                                        data1RA.setCellValue(getX(c.getRt1ra()));
                                        colNum++;

                                        Cell data2DA = data.createCell(colNum);
                                        data2DA.setCellStyle(dataStyle2);
                                        data2DA.setCellValue(getX(c.getRt2da()));
                                        colNum++;

                                        Cell data3RA = data.createCell(colNum);
                                        data3RA.setCellStyle(dataStyle2);
                                        data3RA.setCellValue(getX(c.getRt3ra()));
                                        colNum++;

                                        Cell data4TA = data.createCell(colNum);
                                        data4TA.setCellStyle(dataStyle2);
                                        data4TA.setCellValue(getX(c.getRt4ta()));
                                        colNum++;

                                        Cell data5TA = data.createCell(colNum);
                                        data5TA.setCellStyle(dataStyle2);
                                        data5TA.setCellValue(getX(c.getRt5ta()));
                                        colNum++;

                                        Cell dataRLMicro = data.createCell(colNum);
                                        dataRLMicro.setCellStyle(dataStyle2);
                                        dataRLMicro.setCellValue(getX(c.getRlMicro()));
                                        colNum++;

                                        Cell dataRLPequeña = data.createCell(colNum);
                                        dataRLPequeña.setCellStyle(dataStyle2);
                                        dataRLPequeña.setCellValue(getX(c.getRlPequenia()));
                                        colNum++;

                                        Cell dataRLGeneral = data.createCell(colNum);
                                        dataRLGeneral.setCellStyle(dataStyle2);
                                        dataRLGeneral.setCellValue(getX(c.getRlGeneral()));
                                        colNum++;

                                        Cell dataRLConstruccion = data.createCell(colNum);
                                        dataRLConstruccion.setCellStyle(dataStyle2);
                                        dataRLConstruccion.setCellValue(getX(c.getRlConstruccion()));
                                        colNum++;

                                        Cell dataRLAgrario = data.createCell(colNum);
                                        dataRLAgrario.setCellStyle(dataStyle2);
                                        dataRLAgrario.setCellValue(getX(c.getRlAgrario()));
                                        colNum++;

                                        Cell dataRLAcreditado = data.createCell(colNum);
                                        dataRLAcreditado.setCellStyle(dataStyle2);
                                        dataRLAcreditado.setCellValue(getX(c.getRlAcreditado()));
                                        colNum++;

                                        Cell dataRLNoAcreditado = data.createCell(colNum);
                                        dataRLNoAcreditado.setCellStyle(dataStyle2);
                                        dataRLNoAcreditado.setCellValue(getX(c.getRlNoAcreditado()));
                                        colNum++;
                                }

                                // if (true) {
                                // Cell dataSolU = data.createCell(colNum);
                                // dataSolU.setCellStyle(fondoGreyStyle);
                                // dataSolU.setCellValue(c.getSolU());
                                // colNum++;
                                //
                                // Cell dataSolC = data.createCell(colNum);
                                // dataSolC.setCellStyle(fondoGreyStyle);
                                // dataSolC.setCellValue(c.getSolC());
                                // colNum++;
                                // }
                                // if (true) {
                                // Cell dataUpsU = data.createCell(colNum);
                                // dataUpsU.setCellStyle(fondoGreyStyle);
                                // dataUpsU.setCellValue(c.getUpsU());
                                // colNum++;
                                //
                                // Cell dataUpsC = data.createCell(colNum);
                                // dataUpsC.setCellStyle(fondoGreyStyle);
                                // dataUpsC.setCellValue(c.getUpsC());
                                // colNum++;
                                // }
                                // if (true) {
                                // Cell dataSoldierU = data.createCell(colNum);
                                // dataSoldierU.setCellStyle(fondoGreyStyle);
                                // dataSoldierU.setCellValue(c.getSoldierU());
                                // colNum++;
                                //
                                // Cell dataSoldierC = data.createCell(colNum);
                                // dataSoldierC.setCellStyle(fondoGreyStyle);
                                // dataSoldierC.setCellValue(c.getSoldierC());
                                // colNum++;
                                //
                                // Cell dataSignerU = data.createCell(colNum);
                                // dataSignerU.setCellStyle(fondoGreyStyle);
                                // dataSignerU.setCellValue(c.getSignerU());
                                // colNum++;
                                //
                                // Cell dataSignerC = data.createCell(colNum);
                                // dataSignerC.setCellStyle(fondoGreyStyle);
                                // dataSignerC.setCellValue(c.getSignerC());
                                // colNum++;
                                //
                                // Cell dataAfpNetU = data.createCell(colNum);
                                // dataAfpNetU.setCellStyle(fondoGreyStyle);
                                // dataAfpNetU.setCellValue(c.getAfpU());
                                // colNum++;
                                //
                                // Cell dataAfpNetC = data.createCell(colNum);
                                // dataAfpNetC.setCellStyle(fondoGreyStyle);
                                // dataAfpNetC.setCellValue(c.getAfpC());
                                // colNum++;
                                //
                                // Cell dataSisU = data.createCell(colNum);
                                // dataSisU.setCellStyle(fondoGreyStyle);
                                // dataSisU.setCellValue(c.getSisU());
                                // colNum++;
                                //
                                // Cell dataSisC = data.createCell(colNum);
                                // dataSisC.setCellStyle(fondoGreyStyle);
                                // dataSisC.setCellValue(c.getSisC());
                                // colNum++;
                                // }
                                if (true) {
                                        Cell dataDeoCuenta = data.createCell(colNum);
                                        dataDeoCuenta.setCellStyle(fondoGreyStyle);
                                        dataDeoCuenta.setCellValue(c.getCcbCuenta());
                                        colNum++;

                                        Cell dataDeoDNI = data.createCell(colNum);
                                        dataDeoDNI.setCellStyle(fondoGreyStyle);
                                        dataDeoDNI.setCellValue(c.getCcbUsuario());
                                        colNum++;

                                        Cell dataDeoClave = data.createCell(colNum);
                                        dataDeoClave.setCellStyle(fondoGreyStyle);
                                        dataDeoClave.setCellValue(c.getCcbClave());
                                        colNum++;
                                }

                                if (true) {
                                        Cell dataPdtAnualDesde = data.createCell(colNum);
                                        dataPdtAnualDesde.setCellStyle(dataStyle2);
                                        dataPdtAnualDesde.setCellValue(c.getStDesdePdtAnual());
                                        colNum++;

                                        Cell dataPdtAnualHasta = data.createCell(colNum);
                                        dataPdtAnualHasta.setCellStyle(dataStyle2);
                                        dataPdtAnualHasta.setCellValue(c.getStHastaPdtAnual());
                                        colNum++;

                                        Cell dataPdt621Desde = data.createCell(colNum);
                                        dataPdt621Desde.setCellStyle(dataStyle2);
                                        dataPdt621Desde.setCellValue(c.getStDesdePdt621());
                                        colNum++;

                                        Cell dataPdt621Hasta = data.createCell(colNum);
                                        dataPdt621Hasta.setCellStyle(dataStyle2);
                                        dataPdt621Hasta.setCellValue(c.getStHastaPdt621());
                                        colNum++;

                                        Cell dataPdt601Desde = data.createCell(colNum);
                                        dataPdt601Desde.setCellStyle(dataStyle2);
                                        dataPdt601Desde.setCellValue(c.getStDesdePdt601());
                                        colNum++;

                                        Cell dataPdt601Hasta = data.createCell(colNum);
                                        dataPdt601Hasta.setCellStyle(dataStyle2);
                                        dataPdt601Hasta.setCellValue(c.getStHastaPdt601());
                                        colNum++;

                                        Cell dataPdt617Desde = data.createCell(colNum);
                                        dataPdt617Desde.setCellStyle(dataStyle2);
                                        dataPdt617Desde.setCellValue(c.getStDesdePdt617());
                                        colNum++;

                                        Cell dataPdt617Hasta = data.createCell(colNum);
                                        dataPdt617Hasta.setCellStyle(dataStyle2);
                                        dataPdt617Hasta.setCellValue(c.getStHastaPdt617());
                                        colNum++;

                                        Cell dataPleComprasVentasDesde = data.createCell(colNum);
                                        dataPleComprasVentasDesde.setCellStyle(dataStyle2);
                                        dataPleComprasVentasDesde.setCellValue(c.getStDesdePleCompras());
                                        colNum++;

                                        Cell dataPleComprasVentasHasta = data.createCell(colNum);
                                        dataPleComprasVentasHasta.setCellStyle(dataStyle2);
                                        dataPleComprasVentasHasta.setCellValue(c.getStHastaPleCompras());
                                        colNum++;

                                        Cell dataPleDiarioDesde = data.createCell(colNum);
                                        dataPleDiarioDesde.setCellStyle(dataStyle2);
                                        dataPleDiarioDesde.setCellValue(c.getStDesdePleDiario());
                                        colNum++;

                                        Cell dataPleDiarioHasta = data.createCell(colNum);
                                        dataPleDiarioHasta.setCellStyle(dataStyle2);
                                        dataPleDiarioHasta.setCellValue(c.getStHastaPleDiario());
                                        colNum++;
                                }

                                rowNum++;
                                i++;
                        }
                        // </editor-fold>

                        int finFilt = rowNum - 1;
                        sheet.setAutoFilter(new CellRangeAddress(inicioFilt, finFilt, 0, 57));

                        // <editor-fold defaultstate="collapsed" desc=" L L E N A D O D E R E S U M E N
                        // ">
                        activo0.setCellValue(state.activos[0]);
                        baja0.setCellValue(state.bajas[0]);
                        libre0.setCellValue(state.libres[0]);
                        retir0.setCellValue(state.retirados[0]);
                        susp0.setCellValue(state.suspendidos[0]);
                        externo0.setCellValue(state.externos[0]);
                        cero0.setCellValue(state.ceros[0]);
                        moroso0.setCellValue(state.morosos[0]);

                        total0.setCellValue(state.activos[0] + state.bajas[0] + state.libres[0] + state.retirados[0]
                                        + state.suspendidos[0] + state.externos[0]
                                        + state.ceros[0] + state.morosos[0]);

                        activo1.setCellValue(state.activos[1]);
                        baja1.setCellValue(state.bajas[1]);
                        libre1.setCellValue(state.libres[1]);
                        retir1.setCellValue(state.retirados[1]);
                        susp1.setCellValue(state.suspendidos[1]);
                        externo1.setCellValue(state.externos[1]);
                        cero1.setCellValue(state.ceros[1]);
                        moroso1.setCellValue(state.morosos[1]);
                        total1.setCellValue(state.activos[1] + state.bajas[1] + state.libres[1] + state.retirados[1]
                                        + state.suspendidos[1] + state.externos[1]
                                        + state.ceros[1] + state.morosos[1]);

                        activo2.setCellValue(state.activos[2]);
                        baja2.setCellValue(state.bajas[2]);
                        libre2.setCellValue(state.libres[2]);
                        retir2.setCellValue(state.retirados[2]);
                        susp2.setCellValue(state.suspendidos[2]);
                        externo2.setCellValue(state.externos[2]);
                        cero2.setCellValue(state.ceros[2]);
                        moroso2.setCellValue(state.morosos[2]);
                        total2.setCellValue(state.activos[2] + state.bajas[2] + state.libres[2] + state.retirados[2]
                                        + state.suspendidos[2] + state.externos[2]
                                        + state.ceros[2] + state.morosos[2]);

                        activo3.setCellValue(state.activos[3]);
                        baja3.setCellValue(state.bajas[3]);
                        libre3.setCellValue(state.libres[3]);
                        retir3.setCellValue(state.retirados[3]);
                        susp3.setCellValue(state.suspendidos[3]);
                        externo3.setCellValue(state.externos[3]);
                        cero3.setCellValue(state.ceros[3]);
                        moroso3.setCellValue(state.morosos[3]);
                        total3.setCellValue(state.activos[3] + state.bajas[3] + state.libres[3] + state.retirados[3]
                                        + state.suspendidos[3] + state.externos[3]
                                        + state.ceros[3] + state.morosos[3]);

                        activo4.setCellValue(state.activos[4]);
                        baja4.setCellValue(state.bajas[4]);
                        libre4.setCellValue(state.libres[4]);
                        retir4.setCellValue(state.retirados[4]);
                        susp4.setCellValue(state.suspendidos[4]);
                        externo4.setCellValue(state.externos[4]);
                        cero4.setCellValue(state.ceros[4]);
                        moroso4.setCellValue(state.morosos[4]);
                        total4.setCellValue(state.activos[4] + state.bajas[4] + state.libres[4] + state.retirados[4]
                                        + state.suspendidos[4] + state.externos[4]
                                        + state.ceros[4] + state.morosos[4]);

                        activo5.setCellValue(state.activos[5]);
                        baja5.setCellValue(state.bajas[5]);
                        libre5.setCellValue(state.libres[5]);
                        retir5.setCellValue(state.retirados[5]);
                        susp5.setCellValue(state.suspendidos[5]);
                        externo5.setCellValue(state.externos[5]);
                        cero5.setCellValue(state.ceros[5]);
                        moroso5.setCellValue(state.morosos[5]);
                        total5.setCellValue(state.activos[5] + state.bajas[5] + state.libres[5] + state.retirados[5]
                                        + state.suspendidos[5] + state.externos[5]
                                        + state.ceros[5] + state.morosos[5]);

                        activo6.setCellValue(state.activos[6]);
                        baja6.setCellValue(state.bajas[6]);
                        libre6.setCellValue(state.libres[6]);
                        retir6.setCellValue(state.retirados[6]);
                        susp6.setCellValue(state.suspendidos[6]);
                        externo6.setCellValue(state.externos[6]);
                        cero6.setCellValue(state.ceros[6]);
                        moroso6.setCellValue(state.morosos[6]);
                        total6.setCellValue(state.activos[6] + state.bajas[6] + state.libres[6] + state.retirados[6]
                                        + state.suspendidos[6] + state.externos[6]
                                        + state.ceros[6] + state.morosos[6]);

                        activo7.setCellValue(state.activos[7]);
                        baja7.setCellValue(state.bajas[7]);
                        libre7.setCellValue(state.libres[7]);
                        retir7.setCellValue(state.retirados[7]);
                        susp7.setCellValue(state.suspendidos[7]);
                        externo7.setCellValue(state.externos[7]);
                        cero7.setCellValue(state.ceros[7]);
                        moroso7.setCellValue(state.morosos[7]);
                        total7.setCellValue(state.activos[7] + state.bajas[7] + state.libres[7] + state.retirados[7]
                                        + state.suspendidos[7] + state.externos[7]
                                        + state.ceros[7] + state.morosos[7]);

                        activo8.setCellValue(state.activos[8]);
                        baja8.setCellValue(state.bajas[8]);
                        libre8.setCellValue(state.libres[8]);
                        retir8.setCellValue(state.retirados[8]);
                        susp8.setCellValue(state.suspendidos[8]);
                        externo8.setCellValue(state.externos[8]);
                        cero8.setCellValue(state.ceros[8]);
                        moroso8.setCellValue(state.morosos[8]);
                        total8.setCellValue(state.activos[8] + state.bajas[8] + state.libres[8] + state.retirados[8]
                                        + state.suspendidos[8] + state.externos[8]
                                        + state.ceros[8] + state.morosos[8]);

                        activo9.setCellValue(state.activos[9]);
                        baja9.setCellValue(state.bajas[9]);
                        libre9.setCellValue(state.libres[9]);
                        retir9.setCellValue(state.retirados[9]);
                        susp9.setCellValue(state.suspendidos[9]);
                        externo9.setCellValue(state.externos[9]);
                        cero9.setCellValue(state.ceros[9]);
                        moroso9.setCellValue(state.morosos[9]);
                        total9.setCellValue(state.activos[9] + state.bajas[9] + state.libres[9] + state.retirados[9]
                                        + state.suspendidos[9] + state.externos[9]
                                        + state.ceros[9] + state.morosos[9]);

                        activoTot.setCellValue(state.totalActivos);
                        bajaTot.setCellValue(state.totalBaja);
                        libreTot.setCellValue(state.totalLibre);
                        retirTot.setCellValue(state.totalRetirados);
                        suspTot.setCellValue(state.totalSuspendidos);
                        externoTot.setCellValue(state.totalExt);
                        ceroTot.setCellValue(state.totalCer);
                        morosoTot.setCellValue(state.totalMor);
                        totalTot.setCellValue(state.totalActivos + state.totalBaja + state.totalLibre
                                        + state.totalRetirados + state.totalSuspendidos
                                        + state.totalExt + state.totalCer + state.totalMor);

                        // </editor-fold>
                        sheet.createFreezePane(0, 17);

                        // <editor-fold defaultstate="collapsed" desc="ANCHO DE COLUMNAS">
                        for (int contCol = 0; contCol < colNum; contCol++) {
                                sheet.autoSizeColumn(contCol);
                        }
                        // </editor-fold>

                        wb.write(out);
                        wb.close();
                        return out.toByteArray();
                } catch (Exception e) {
                        e.printStackTrace();
                        return new byte[0];
                }
        }
}
