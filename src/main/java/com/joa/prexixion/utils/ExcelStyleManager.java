package com.joa.prexixion.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import java.util.HashMap;
import java.util.Map;

public class ExcelStyleManager {

    private final XSSFWorkbook workbook;
    private final Map<String, XSSFCellStyle> styleCache = new HashMap<>();

    // --- COLORES ---
    public static final byte[] GERENCIA_BLUE_RGB = {(byte) 0, (byte) 51, (byte) 204};
    public static final byte[] GERENCIA_GREY_RGB = {(byte) 214, (byte) 220, (byte) 228};
    public static final byte[] MATTE_BLACK_RGB = {(byte) 43, (byte) 43, (byte) 43};
    public static final byte[] LIGHT_GREY_RGB = {(byte) 242, (byte) 242, (byte) 242};

    public static final byte[] CELESTE_RGB = {(byte) 138, (byte) 193, (byte) 255};
    public static final byte[] TURQUESA_RGB = {(byte) 81, (byte) 198, (byte) 218};
    public static final byte[] AMARILLO_RGB = {(byte) 254, (byte) 255, (byte) 155};
    public static final byte[] NARANJA_RGB = {(byte) 252, (byte) 184, (byte) 72};
    public static final byte[] ROJO_RGB = {(byte) 169, (byte) 0, (byte) 54};
    
    public static final byte[] WHITE_RGB = {(byte) 255, (byte) 255, (byte) 255};
    public static final byte[] BLACK_RGB = {(byte) 0, (byte) 0, (byte) 0};
    public static final byte[] GREEN_RGB = {(byte) 0, (byte) 204, (byte) 0};
    public static final byte[] RED_RGB = {(byte) 255, (byte) 0, (byte) 0};
    public static final byte[] YELLOW_RGB = {(byte) 255, (byte) 255, (byte) 0};
    public static final byte[] GREY_RGB = {(byte) 128, (byte) 128, (byte) 128};
    public static final byte[] ORANGE_RGB = {(byte) 255, (byte) 102, (byte) 0};
    public static final byte[] LIGHT_BLUE_RGB = {(byte) 51, (byte) 153, (byte) 255};
    public static final byte[] DARK_BLUE_RGB = {(byte) 0, (byte) 51, (byte) 204};
    public static final byte[] BLUE_RGB = {(byte) 0, (byte) 0, (byte) 255};

    public static final byte[] VERY_LIGHT_RED_RGB = {(byte) 253, (byte) 237, (byte) 236};
    public static final byte[] VERY_LIGHT_BLUE_RGB = {(byte) 235, (byte) 245, (byte) 251};
    public static final byte[] VERY_LIGHT_ORANGE_RGB = {(byte) 245, (byte) 203, (byte) 167};
    public static final byte[] VERY_LIGHT_YELLOW_RGB = {(byte) 254, (byte) 249, (byte) 231};
    public static final byte[] VERY_LIGHT_GREEN_RGB = {(byte) 244, (byte) 251, (byte) 235};

    public ExcelStyleManager(XSSFWorkbook workbook) {
        this.workbook = workbook;
    }

    public XSSFColor getColor(byte[] rgb) {
        return new XSSFColor(rgb, new DefaultIndexedColorMap());
    }

    public XSSFCellStyle getHeaderStyle() {
        return styleCache.computeIfAbsent("header", k -> {
            XSSFFont font = PoiUtils.fuente(workbook, getColor(WHITE_RGB), 17, true);
            XSSFCellStyle style = PoiUtils.createCellStyle(workbook, HorizontalAlignment.CENTER, getColor(GERENCIA_BLUE_RGB), font);
            return style;
        });
    }

    public XSSFCellStyle getSubHeaderStyle() {
        return styleCache.computeIfAbsent("subHeader", k -> {
            XSSFFont font = PoiUtils.fuente(workbook, getColor(WHITE_RGB), 9, true);
            XSSFCellStyle style = PoiUtils.createCellStyle(workbook, HorizontalAlignment.CENTER, getColor(MATTE_BLACK_RGB), font);
            PoiUtils.addBorders(style, BorderStyle.THIN, IndexedColors.WHITE);
            return style;
        });
    }

    public XSSFCellStyle getSubHeaderStyleBlue() {
        return styleCache.computeIfAbsent("subHeaderBlue", k -> {
            XSSFFont font = PoiUtils.fuente(workbook, getColor(WHITE_RGB), 9, true);
            XSSFCellStyle style = PoiUtils.createCellStyle(workbook, HorizontalAlignment.CENTER, getColor(GERENCIA_BLUE_RGB), font);
            PoiUtils.addBorders(style, BorderStyle.THIN, IndexedColors.WHITE);
            return style;
        });
    }

    public XSSFCellStyle getDataCenterStyle() {
        return styleCache.computeIfAbsent("dataCenter", k -> {
            XSSFFont font = PoiUtils.fuente(workbook, getColor(MATTE_BLACK_RGB), 9, false);
            XSSFCellStyle style = PoiUtils.createCellStyle(workbook, HorizontalAlignment.CENTER, getColor(GERENCIA_GREY_RGB), font);
            PoiUtils.addBorders(style, BorderStyle.THIN, IndexedColors.WHITE);
            return style;
        });
    }

    public XSSFCellStyle getDataCenterBoldStyle() {
        return styleCache.computeIfAbsent("dataCenterBold", k -> {
            XSSFFont font = PoiUtils.fuente(workbook, getColor(MATTE_BLACK_RGB), 9, true);
            XSSFCellStyle style = PoiUtils.createCellStyle(workbook, HorizontalAlignment.CENTER, getColor(GERENCIA_GREY_RGB), font);
            PoiUtils.addBorders(style, BorderStyle.THIN, IndexedColors.WHITE);
            return style;
        });
    }

    public XSSFCellStyle getDataLeftStyle() {
        return styleCache.computeIfAbsent("dataLeft", k -> {
            XSSFFont font = PoiUtils.fuente(workbook, getColor(MATTE_BLACK_RGB), 9, false);
            XSSFCellStyle style = PoiUtils.createCellStyle(workbook, HorizontalAlignment.LEFT, getColor(GERENCIA_GREY_RGB), font);
            PoiUtils.addBorders(style, BorderStyle.THIN, IndexedColors.WHITE);
            return style;
        });
    }

    public XSSFCellStyle getDataLeftBoldStyle() {
        return styleCache.computeIfAbsent("dataLeftBold", k -> {
            XSSFFont font = PoiUtils.fuente(workbook, getColor(MATTE_BLACK_RGB), 9, true);
            XSSFCellStyle style = PoiUtils.createCellStyle(workbook, HorizontalAlignment.LEFT, getColor(GERENCIA_GREY_RGB), font);
            PoiUtils.addBorders(style, BorderStyle.THIN, IndexedColors.WHITE);
            return style;
        });
    }

    public XSSFCellStyle getFondoWhiteStyleCenter() {
        return styleCache.computeIfAbsent("whiteCenter", k -> {
            XSSFFont font = PoiUtils.fuente(workbook, getColor(MATTE_BLACK_RGB), 9, false);
            XSSFCellStyle style = PoiUtils.createCellStyle(workbook, HorizontalAlignment.CENTER, getColor(WHITE_RGB), font);
            PoiUtils.addBorders(style, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);
            return style;
        });
    }

    public XSSFCellStyle getFondoWhiteStyleLeft() {
        return styleCache.computeIfAbsent("whiteLeft", k -> {
            XSSFFont font = PoiUtils.fuente(workbook, getColor(MATTE_BLACK_RGB), 9, false);
            XSSFCellStyle style = PoiUtils.createCellStyle(workbook, HorizontalAlignment.LEFT, getColor(WHITE_RGB), font);
            PoiUtils.addBorders(style, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);
            return style;
        });
    }

    public XSSFCellStyle getFondoBlackStyle() {
        return styleCache.computeIfAbsent("blackCenterBold", k -> {
            XSSFFont font = PoiUtils.fuente(workbook, getColor(WHITE_RGB), 9, true);
            XSSFCellStyle style = PoiUtils.createCellStyle(workbook, HorizontalAlignment.CENTER, getColor(BLACK_RGB), font);
            return style;
        });
    }

    public XSSFCellStyle getFondoGreenStyle() {
        return styleCache.computeIfAbsent("greenCenterBold", k -> {
            XSSFFont font = PoiUtils.fuente(workbook, getColor(WHITE_RGB), 9, true);
            XSSFCellStyle style = PoiUtils.createCellStyle(workbook, HorizontalAlignment.CENTER, getColor(GREEN_RGB), font);
            PoiUtils.addBorders(style, BorderStyle.THIN, IndexedColors.WHITE);
            return style;
        });
    }

    public XSSFCellStyle getFondoRedStyle() {
        return styleCache.computeIfAbsent("darkRedCenterBold", k -> {
            XSSFFont font = PoiUtils.fuente(workbook, getColor(WHITE_RGB), 9, true);
            XSSFCellStyle style = PoiUtils.createCellStyle(workbook, HorizontalAlignment.CENTER, getColor(ROJO_RGB), font);
            PoiUtils.addBorders(style, BorderStyle.THIN, IndexedColors.WHITE);
            return style;
        });
    }

    public XSSFCellStyle getFondoYellowStyle() {
        return styleCache.computeIfAbsent("yellowLeftBold", k -> {
            XSSFFont font = PoiUtils.fuente(workbook, getColor(MATTE_BLACK_RGB), 9, true);
            XSSFCellStyle style = PoiUtils.createCellStyle(workbook, HorizontalAlignment.LEFT, getColor(YELLOW_RGB), font);
            return style;
        });
    }

    public XSSFCellStyle getFondoLightRedStyle() {
        return styleCache.computeIfAbsent("lightRedCenterBold", k -> {
            XSSFFont font = PoiUtils.fuente(workbook, getColor(MATTE_BLACK_RGB), 9, true);
            XSSFCellStyle style = PoiUtils.createCellStyle(workbook, HorizontalAlignment.CENTER, getColor(VERY_LIGHT_RED_RGB), font);
            PoiUtils.addBorders(style, BorderStyle.THIN, IndexedColors.WHITE);
            return style;
        });
    }

    public XSSFCellStyle getFondoLightBlueStyle() {
        return styleCache.computeIfAbsent("lightBlueCenter", k -> {
            XSSFFont font = PoiUtils.fuente(workbook, getColor(MATTE_BLACK_RGB), 9, false);
            XSSFCellStyle style = PoiUtils.createCellStyle(workbook, HorizontalAlignment.CENTER, getColor(VERY_LIGHT_BLUE_RGB), font);
            PoiUtils.addBorders(style, BorderStyle.THIN, IndexedColors.WHITE);
            return style;
        });
    }

    public XSSFCellStyle getFondoLightOrangeStyle() {
        return styleCache.computeIfAbsent("lightOrangeCenterBold", k -> {
            XSSFFont font = PoiUtils.fuente(workbook, getColor(MATTE_BLACK_RGB), 9, true);
            XSSFCellStyle style = PoiUtils.createCellStyle(workbook, HorizontalAlignment.CENTER, getColor(VERY_LIGHT_ORANGE_RGB), font);
            PoiUtils.addBorders(style, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);
            return style;
        });
    }

    public XSSFCellStyle getFondoLightYellowStyle() {
        return styleCache.computeIfAbsent("lightYellowCenterBold", k -> {
            XSSFFont font = PoiUtils.fuente(workbook, getColor(MATTE_BLACK_RGB), 9, true);
            XSSFCellStyle style = PoiUtils.createCellStyle(workbook, HorizontalAlignment.CENTER, getColor(VERY_LIGHT_YELLOW_RGB), font);
            PoiUtils.addBorders(style, BorderStyle.THIN, IndexedColors.WHITE);
            return style;
        });
    }

    public XSSFCellStyle getFondoLightGreenStyle() {
        return styleCache.computeIfAbsent("lightGreenCenterBold", k -> {
            XSSFFont font = PoiUtils.fuente(workbook, getColor(MATTE_BLACK_RGB), 9, true);
            XSSFCellStyle style = PoiUtils.createCellStyle(workbook, HorizontalAlignment.CENTER, getColor(VERY_LIGHT_GREEN_RGB), font);
            PoiUtils.addBorders(style, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);
            return style;
        });
    }

    public XSSFCellStyle getFondoLightWhiteStyle() {
        return styleCache.computeIfAbsent("lightWhiteCenterBold", k -> {
            XSSFFont font = PoiUtils.fuente(workbook, getColor(MATTE_BLACK_RGB), 9, true);
            XSSFCellStyle style = PoiUtils.createCellStyle(workbook, HorizontalAlignment.CENTER, getColor(WHITE_RGB), font);
            PoiUtils.addBorders(style, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);
            return style;
        });
    }

    public XSSFCellStyle getStatusBlueStyle() {
        return styleCache.computeIfAbsent("statusBlue", k -> {
            XSSFFont font = PoiUtils.fuente(workbook, getColor(WHITE_RGB), 9, true);
            XSSFCellStyle style = PoiUtils.createCellStyle(workbook, HorizontalAlignment.CENTER, getColor(DARK_BLUE_RGB), font);
            PoiUtils.addBorders(style, BorderStyle.THIN, IndexedColors.WHITE);
            return style;
        });
    }

    public XSSFCellStyle getDataStatusStyle(byte[] fontColor) {
        String key = "dataStatus_" + java.util.Arrays.toString(fontColor);
        return styleCache.computeIfAbsent(key, k -> {
            XSSFFont font = PoiUtils.fuente(workbook, getColor(fontColor), 9, true);
            XSSFCellStyle style = PoiUtils.createCellStyle(workbook, HorizontalAlignment.CENTER, getColor(LIGHT_GREY_RGB), font);
            PoiUtils.addBorders(style, BorderStyle.THIN, IndexedColors.WHITE);
            return style;
        });
    }

    public XSSFCellStyle getGenericStyle(byte[] bgColor, byte[] fontColor, boolean bold) {
        String key = "generic_" + java.util.Arrays.toString(bgColor) + "_" + java.util.Arrays.toString(fontColor) + "_" + bold;
        return styleCache.computeIfAbsent(key, k -> {
            XSSFFont font = PoiUtils.fuente(workbook, getColor(fontColor), 9, bold);
            XSSFCellStyle style = PoiUtils.createCellStyle(workbook, HorizontalAlignment.CENTER, getColor(bgColor), font);
            PoiUtils.addBorders(style, BorderStyle.THIN, IndexedColors.WHITE);
            return style;
        });
    }
}
