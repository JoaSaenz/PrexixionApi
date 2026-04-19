package com.joa.prexixionapi.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class ExcelStyleManager {

    private final XSSFWorkbook workbook;
    private final Map<String, XSSFCellStyle> styleCache = new ConcurrentHashMap<>();
    private final Map<String, XSSFFont> fontCache = new ConcurrentHashMap<>();

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
    public static final byte[] DARK_BLUE_RGB = GERENCIA_BLUE_RGB;
    public static final byte[] BLUE_RGB = GERENCIA_BLUE_RGB;

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
        return getHeaderStyle(17);
    }

    public XSSFCellStyle getHeaderStyle(int fontSize) {
        return getCustomStyle(GERENCIA_BLUE_RGB, WHITE_RGB, fontSize, true, HorizontalAlignment.CENTER, BorderStyle.THIN, IndexedColors.WHITE);
    }

    public XSSFCellStyle getSubHeaderStyle() {
        return getSubHeaderStyle(9);
    }

    public XSSFCellStyle getSubHeaderStyle(int fontSize) {
        return getCustomStyle(MATTE_BLACK_RGB, WHITE_RGB, fontSize, true, HorizontalAlignment.CENTER, BorderStyle.THIN, IndexedColors.WHITE);
    }

    public XSSFCellStyle getSubHeaderStyleBlue() {
        return getSubHeaderStyleBlue(9);
    }

    public XSSFCellStyle getSubHeaderStyleBlue(int fontSize) {
        return getCustomStyle(GERENCIA_BLUE_RGB, WHITE_RGB, fontSize, true, HorizontalAlignment.CENTER, BorderStyle.THIN, IndexedColors.WHITE);
    }

    public XSSFCellStyle getDataCenterStyle() {
        return getDataCenterStyle(9);
    }

    public XSSFCellStyle getDataCenterStyle(int fontSize) {
        return getCustomStyle(GERENCIA_GREY_RGB, MATTE_BLACK_RGB, fontSize, false, HorizontalAlignment.CENTER, BorderStyle.THIN, IndexedColors.WHITE);
    }

    public XSSFCellStyle getDataCenterBoldStyle() {
        return getDataCenterBoldStyle(9);
    }

    public XSSFCellStyle getDataCenterBoldStyle(int fontSize) {
        return getCustomStyle(GERENCIA_GREY_RGB, MATTE_BLACK_RGB, fontSize, true, HorizontalAlignment.CENTER, BorderStyle.THIN, IndexedColors.WHITE);
    }

    public XSSFCellStyle getDataLeftStyle() {
        return getDataLeftStyle(9);
    }

    public XSSFCellStyle getDataLeftStyle(int fontSize) {
        return getCustomStyle(GERENCIA_GREY_RGB, MATTE_BLACK_RGB, fontSize, false, HorizontalAlignment.LEFT, BorderStyle.THIN, IndexedColors.WHITE);
    }

    public XSSFCellStyle getDataLeftBoldStyle() {
        return getDataLeftBoldStyle(9);
    }

    public XSSFCellStyle getDataLeftBoldStyle(int fontSize) {
        return getCustomStyle(GERENCIA_GREY_RGB, MATTE_BLACK_RGB, fontSize, true, HorizontalAlignment.LEFT, BorderStyle.THIN, IndexedColors.WHITE);
    }

    public XSSFCellStyle getFondoWhiteStyleCenter() {
        return getFondoWhiteStyleCenter(9);
    }

    public XSSFCellStyle getFondoWhiteStyleCenter(int fontSize) {
        return getCustomStyle(WHITE_RGB, MATTE_BLACK_RGB, fontSize, false, HorizontalAlignment.CENTER, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);
    }

    public XSSFCellStyle getFondoWhiteStyleLeft() {
        return getFondoWhiteStyleLeft(9);
    }

    public XSSFCellStyle getFondoWhiteStyleLeft(int fontSize) {
        return getCustomStyle(WHITE_RGB, MATTE_BLACK_RGB, fontSize, false, HorizontalAlignment.LEFT, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);
    }

    public XSSFCellStyle getFondoBlackStyle() {
        return getFondoBlackStyle(9);
    }

    public XSSFCellStyle getFondoBlackStyle(int fontSize) {
        return getCustomStyle(BLACK_RGB, WHITE_RGB, fontSize, true, HorizontalAlignment.CENTER, BorderStyle.THIN, IndexedColors.WHITE);
    }

    public XSSFCellStyle getFondoGreenStyle() {
        return getFondoGreenStyle(9);
    }

    public XSSFCellStyle getFondoGreenStyle(int fontSize) {
        return getCustomStyle(GREEN_RGB, WHITE_RGB, fontSize, true, HorizontalAlignment.CENTER, BorderStyle.THIN, IndexedColors.WHITE);
    }

    public XSSFCellStyle getFondoRedStyle() {
        return getFondoRedStyle(9);
    }

    public XSSFCellStyle getFondoRedStyle(int fontSize) {
        return getCustomStyle(ROJO_RGB, WHITE_RGB, fontSize, true, HorizontalAlignment.CENTER, BorderStyle.THIN, IndexedColors.WHITE);
    }

    public XSSFCellStyle getFondoYellowStyle() {
        return getFondoYellowStyle(9);
    }

    public XSSFCellStyle getFondoYellowStyle(int fontSize) {
        return getCustomStyle(YELLOW_RGB, MATTE_BLACK_RGB, fontSize, true, HorizontalAlignment.LEFT, BorderStyle.THIN, IndexedColors.WHITE);
    }

    public XSSFCellStyle getFondoLightRedStyle() {
        return getFondoLightRedStyle(9);
    }

    public XSSFCellStyle getFondoLightRedStyle(int fontSize) {
        return getCustomStyle(VERY_LIGHT_RED_RGB, MATTE_BLACK_RGB, fontSize, true, HorizontalAlignment.CENTER, BorderStyle.THIN, IndexedColors.WHITE);
    }

    public XSSFCellStyle getFondoLightBlueStyle() {
        return getFondoLightBlueStyle(9);
    }

    public XSSFCellStyle getFondoLightBlueStyle(int fontSize) {
        return getCustomStyle(VERY_LIGHT_BLUE_RGB, MATTE_BLACK_RGB, fontSize, false, HorizontalAlignment.CENTER, BorderStyle.THIN, IndexedColors.WHITE);
    }

    public XSSFCellStyle getFondoLightOrangeStyle() {
        return getFondoLightOrangeStyle(9);
    }

    public XSSFCellStyle getFondoLightOrangeStyle(int fontSize) {
        return getCustomStyle(VERY_LIGHT_ORANGE_RGB, MATTE_BLACK_RGB, fontSize, true, HorizontalAlignment.CENTER, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);
    }

    public XSSFCellStyle getFondoLightYellowStyle() {
        return getFondoLightYellowStyle(9);
    }

    public XSSFCellStyle getFondoLightYellowStyle(int fontSize) {
        return getCustomStyle(VERY_LIGHT_YELLOW_RGB, MATTE_BLACK_RGB, fontSize, true, HorizontalAlignment.CENTER, BorderStyle.THIN, IndexedColors.WHITE);
    }

    public XSSFCellStyle getFondoLightGreenStyle() {
        return getFondoLightGreenStyle(9);
    }

    public XSSFCellStyle getFondoLightGreenStyle(int fontSize) {
        return getCustomStyle(VERY_LIGHT_GREEN_RGB, MATTE_BLACK_RGB, fontSize, true, HorizontalAlignment.CENTER, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);
    }

    public XSSFCellStyle getFondoLightWhiteStyle() {
        return getFondoLightWhiteStyle(9);
    }

    public XSSFCellStyle getFondoLightWhiteStyle(int fontSize) {
        return getCustomStyle(WHITE_RGB, MATTE_BLACK_RGB, fontSize, true, HorizontalAlignment.CENTER, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);
    }

    public XSSFCellStyle getStatusBlueStyle() {
        return getStatusBlueStyle(9);
    }

    public XSSFCellStyle getStatusBlueStyle(int fontSize) {
        return getCustomStyle(DARK_BLUE_RGB, WHITE_RGB, fontSize, true, HorizontalAlignment.CENTER, BorderStyle.THIN, IndexedColors.WHITE);
    }

    public XSSFCellStyle getDataStatusStyle(byte[] fontColor) {
        return getDataStatusStyle(fontColor, 9);
    }

    public XSSFCellStyle getDataStatusStyle(byte[] fontColor, int fontSize) {
        return getCustomStyle(LIGHT_GREY_RGB, fontColor, fontSize, true, HorizontalAlignment.CENTER, BorderStyle.THIN, IndexedColors.WHITE);
    }

    public XSSFCellStyle getMoneyStyle(byte[] bgColor, byte[] fontColor, int fontSize, boolean bold) {
        return getMoneyStyle(bgColor, fontColor, fontSize, bold, IndexedColors.WHITE);
    }

    public XSSFCellStyle getMoneyStyle(byte[] bgColor, byte[] fontColor, int fontSize, boolean bold, IndexedColors borderColor) {
        String key = "money_" + java.util.Arrays.toString(bgColor) + "_" + java.util.Arrays.toString(fontColor) + "_" + fontSize + "_" + bold + "_" + borderColor.name();
        
        // Usamos una verificación manual antes del compute para evitar anidamiento y asegurar independencia
        XSSFCellStyle style = styleCache.get(key);
        if (style == null) {
            style = createBaseStyle(bgColor, fontColor, fontSize, bold, HorizontalAlignment.RIGHT, BorderStyle.THIN, borderColor);
            style.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("#,###.00"));
            styleCache.put(key, style);
        }
        return style;
    }

    public XSSFCellStyle getDateStyle(byte[] bgColor, byte[] fontColor, int fontSize, boolean bold) {
        return getDateStyle(bgColor, fontColor, fontSize, bold, IndexedColors.WHITE);
    }

    public XSSFCellStyle getDateStyle(byte[] bgColor, byte[] fontColor, int fontSize, boolean bold, IndexedColors borderColor) {
        String key = "date_" + java.util.Arrays.toString(bgColor) + "_" + java.util.Arrays.toString(fontColor) + "_" + fontSize + "_" + bold + "_" + borderColor.name();
        
        XSSFCellStyle style = styleCache.get(key);
        if (style == null) {
            style = createBaseStyle(bgColor, fontColor, fontSize, bold, HorizontalAlignment.CENTER, BorderStyle.THIN, borderColor);
            style.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("dd/MM/yyyy"));
            styleCache.put(key, style);
        }
        return style;
    }

    public XSSFCellStyle getGenericStyle(byte[] bgColor, byte[] fontColor, boolean bold) {
        return getGenericStyle(bgColor, fontColor, 9, bold, HorizontalAlignment.CENTER);
    }

    public XSSFCellStyle getGenericStyle(byte[] bgColor, byte[] fontColor, int fontSize, boolean bold, HorizontalAlignment alignment) {
        return getCustomStyle(bgColor, fontColor, fontSize, bold, alignment, BorderStyle.THIN, IndexedColors.WHITE);
    }

    public XSSFCellStyle getCustomStyle(byte[] bgColor, byte[] fontColor, int fontSize, boolean bold, HorizontalAlignment alignment, BorderStyle borderStyle, IndexedColors borderColor) {
        String key = String.format("custom_%s_%s_%d_%b_%s_%s_%s", 
            java.util.Arrays.toString(bgColor), 
            java.util.Arrays.toString(fontColor), 
            fontSize, bold, alignment.name(), 
            borderStyle.name(), borderColor.name());
        
        return styleCache.computeIfAbsent(key, k -> createBaseStyle(bgColor, fontColor, fontSize, bold, alignment, borderStyle, borderColor));
    }

    private XSSFCellStyle createBaseStyle(byte[] bgColor, byte[] fontColor, int fontSize, boolean bold, HorizontalAlignment alignment, BorderStyle borderStyle, IndexedColors borderColor) {
        String fontKey = java.util.Arrays.toString(fontColor) + "_" + fontSize + "_" + bold;
        XSSFFont font = fontCache.computeIfAbsent(fontKey, k -> PoiUtils.fuente(workbook, getColor(fontColor), fontSize, bold));
        
        XSSFCellStyle style = PoiUtils.createCellStyle(workbook, alignment, getColor(bgColor), font);
        PoiUtils.addBorders(style, borderStyle, borderColor);
        return style;
    }
}
