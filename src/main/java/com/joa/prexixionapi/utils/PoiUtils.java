package com.joa.prexixionapi.utils;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class PoiUtils {

    public static Font fuente(Workbook wb, IndexedColors color, int tamano) {
        Font font = wb.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) tamano);
        font.setColor(color.getIndex());
        return font;
    }

    public static Font fuente(Workbook wb, IndexedColors color, int tamano, Boolean neg) {
        Font font = wb.createFont();
        font.setBold(neg);
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) tamano);
        font.setColor(color.getIndex());
        return font;
    }

    public static void addBorders(CellStyle style, BorderStyle borderStyle, IndexedColors colorBorde) {
        style.setBorderBottom(borderStyle);
        style.setBottomBorderColor(colorBorde.getIndex());
        style.setBorderTop(borderStyle);
        style.setTopBorderColor(colorBorde.getIndex());
        style.setBorderRight(borderStyle);
        style.setRightBorderColor(colorBorde.getIndex());
        style.setBorderLeft(borderStyle);
        style.setLeftBorderColor(colorBorde.getIndex());
    }

    public static void addBorders(CellRangeAddress region, BorderStyle borderStyle, IndexedColors color, Sheet sheet) {
        RegionUtil.setBorderTop(borderStyle, region, sheet);
        RegionUtil.setTopBorderColor(color.getIndex(), region, sheet);
        RegionUtil.setBorderBottom(borderStyle, region, sheet);
        RegionUtil.setBottomBorderColor(color.getIndex(), region, sheet);
        RegionUtil.setBorderLeft(borderStyle, region, sheet);
        RegionUtil.setLeftBorderColor(color.getIndex(), region, sheet);
        RegionUtil.setBorderRight(borderStyle, region, sheet);
        RegionUtil.setRightBorderColor(color.getIndex(), region, sheet);
    }

    // XSSF
    public static XSSFFont fuente(XSSFWorkbook wb, XSSFColor color, int tamano, Boolean neg) {
        XSSFFont font = wb.createFont();
        font.setBold(neg);
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) tamano);
        font.setColor(color);
        return font;
    }

    public static XSSFFont fuente(XSSFWorkbook wb, XSSFColor color, int tamano) {
        XSSFFont font = wb.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) tamano);
        font.setColor(color);
        return font;
    }

    public static XSSFCellStyle createCellStyle(XSSFWorkbook wb, HorizontalAlignment alignment, XSSFColor colorFondo, XSSFFont font) {
        XSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(alignment);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFont(font);
        if (colorFondo != null) {
            style.setFillForegroundColor(colorFondo);
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        return style;
    }
}
