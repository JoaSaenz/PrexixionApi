package com.joa.prexixionapi.services;

import com.joa.prexixionapi.dto.AttendanceDTO;
import com.joa.prexixionapi.utils.ExcelStyleManager;
import com.joa.prexixionapi.utils.PoiUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceExcelService {

    public byte[] generateDailyExcel(String fecha, List<AttendanceDTO> data) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            ExcelStyleManager styleManager = new ExcelStyleManager(workbook);
            XSSFSheet sheet = workbook.createSheet("Asistencia Diaria");

            // --- SECCIÓN 1: LISTA PRINCIPAL ---
            int rowNum = 0;
            Row titleRow = getOrCreateRow(sheet, rowNum++);
            applyMergedStyle(sheet, new CellRangeAddress(0, 0, 1, 10), styleManager.getHeaderStyle(14), "REPORTE DE ASISTENCIA DIARIA  |  " + fecha);

            rowNum++; // Espacio

            // Cabeceras Lista Principal
            Row headerRow = getOrCreateRow(sheet, rowNum++);
            String[] headers = {"Personal", "Empresa", "Puesto", "E. Mañana", "S. Mañana", "E. Tarde", "S. Tarde", "Tardanza (min)", "Estado", "Tipo"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i + 1);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(styleManager.getSubHeaderStyleBlue(10));
            }

            // Datos Lista Principal (Fondo Blanco Premium)
            for (AttendanceDTO item : data) {
                Row row = getOrCreateRow(sheet, rowNum++);
                
                setCellValueAndStyle(row, 1, (item.getApellido() != null ? item.getApellido() : "") + " " + (item.getNombre() != null ? item.getNombre() : ""), styleManager.getFondoWhiteStyleLeft());
                setCellValueAndStyle(row, 2, item.getEmpresa(), styleManager.getFondoWhiteStyleCenter());
                setCellValueAndStyle(row, 3, item.getPuesto(), styleManager.getFondoWhiteStyleCenter());
                setCellValueAndStyle(row, 4, item.getMi(), styleManager.getFondoWhiteStyleCenter());
                setCellValueAndStyle(row, 5, item.getMs(), styleManager.getFondoWhiteStyleCenter());
                setCellValueAndStyle(row, 6, item.getTi(), styleManager.getFondoWhiteStyleCenter());
                setCellValueAndStyle(row, 7, item.getTs(), styleManager.getFondoWhiteStyleCenter());

                Cell cellTardanza = row.createCell(8);
                cellTardanza.setCellValue(item.getMinutosTardanza());
                if (item.getMinutosTardanza() > 0) {
                    cellTardanza.setCellStyle(styleManager.getFondoLightRedStyle());
                } else {
                    cellTardanza.setCellStyle(styleManager.getFondoWhiteStyleCenter());
                }

                setCellValueAndStyle(row, 9, item.getEstado(), styleManager.getFondoWhiteStyleCenter());
                setCellValueAndStyle(row, 10, item.getTipo(), styleManager.getFondoWhiteStyleCenter());
            }

            // --- SECCIÓN 2: TABLAS DE RESUMEN ---
            createSummaryTables(sheet, data, styleManager);

            // --- SECCIÓN 3: TARDANZAS DEL DÍA ---
            createLatenessTables(sheet, data, styleManager);

            // Autoajustar columnas principales
            for (int i = 1; i <= 10; i++) {
                sheet.autoSizeColumn(i);
            }
            // Columnas resumen/tardanzas
            for (int i = 12; i <= 24; i++) {
                sheet.setColumnWidth(i, 3500);
            }
            sheet.setColumnWidth(13, 8000); // Nombre Mañana
            sheet.setColumnWidth(19, 8000); // Nombre Tarde

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            return bos.toByteArray();
        }
    }

    private void createSummaryTables(XSSFSheet sheet, List<AttendanceDTO> data, ExcelStyleManager style) {
        Map<String, List<AttendanceDTO>> groupedByLabel = new HashMap<>();
        String[] labels = {"SIN TIPO", "FIJO", "FIJO 1", "PRACTICANTE", "EXTERNO", "OTROS"};
        for (String l : labels) groupedByLabel.put(l, new ArrayList<>());

        for (AttendanceDTO d : data) {
            String label;
            if (d.getIdTipo() == null || d.getIdTipo() < 1 || d.getIdTipo() > 5) {
                label = "OTROS";
            } else {
                label = labels[d.getIdTipo() - 1];
            }
            groupedByLabel.get(label).add(d);
        }

        int startRow = 2;
        applyMergedStyle(sheet, new CellRangeAddress(startRow, startRow, 14, 19), style.getHeaderStyle(11), "RESUMEN DE ASISTENCIA (POR TIPO)");

        Row h2 = getOrCreateRow(sheet, startRow + 1);
        String[] subHeaders = {"TIPO", "M. ASIST", "M. FALTA", "TOTAL", "T. ASIST", "T. FALTA"};
        for (int i = 0; i < subHeaders.length; i++) {
            Cell c = h2.createCell(14 + i);
            c.setCellValue(subHeaders[i]);
            c.setCellStyle(style.getSubHeaderStyleBlue(9));
        }

        int currentRow = startRow + 2;
        int tMAsist = 0, tMFalta = 0, tTotal = 0, tTAsist = 0, tTFalta = 0;

        for (String label : labels) {
            List<AttendanceDTO> list = groupedByLabel.get(label);
            if (list.isEmpty() && !label.equals("OTROS") && !label.equals("FIJO") && !label.equals("PRACTICANTE")) continue;

            int mAsist = (int) list.stream().filter(d -> d.getMi() != null && !d.getMi().isEmpty()).count();
            int mFalta = list.size() - mAsist;
            int tAsist = (int) list.stream().filter(d -> d.getTi() != null && !d.getTi().isEmpty()).count();
            int tFalta = list.size() - tAsist;

            Row r = getOrCreateRow(sheet, currentRow++);
            setCellValueAndStyle(r, 14, label, style.getFondoWhiteStyleLeft(9));
            // Aplicar negrita al label
            Font boldFont = sheet.getWorkbook().createFont();
            boldFont.setFontName("Aptos Narrow");
            boldFont.setBold(true);
            boldFont.setFontHeightInPoints((short)9);
            r.getCell(14).getRichStringCellValue().applyFont(boldFont);

            setCellValueAndStyle(r, 15, mAsist, style.getFondoWhiteStyleCenter(9));
            setCellValueAndStyle(r, 16, mFalta, style.getFondoWhiteStyleCenter(9));
            setCellValueAndStyle(r, 17, list.size(), style.getFondoWhiteStyleCenter(9));
            setCellValueAndStyle(r, 18, tAsist, style.getFondoWhiteStyleCenter(9));
            setCellValueAndStyle(r, 19, tFalta, style.getFondoWhiteStyleCenter(9));

            tMAsist += mAsist; tMFalta += mFalta; tTotal += list.size();
            tTAsist += tAsist; tTFalta += tFalta;
        }

        Row rt = getOrCreateRow(sheet, currentRow);
        setCellValueAndStyle(rt, 14, "TOTAL GENERAL", style.getSubHeaderStyle(9));
        setCellValueAndStyle(rt, 15, tMAsist, style.getSubHeaderStyle(9));
        setCellValueAndStyle(rt, 16, tMFalta, style.getSubHeaderStyle(9));
        setCellValueAndStyle(rt, 17, tTotal, style.getSubHeaderStyle(9));
        setCellValueAndStyle(rt, 18, tTAsist, style.getSubHeaderStyle(9));
        setCellValueAndStyle(rt, 19, tTFalta, style.getSubHeaderStyle(9));
    }

    private void createLatenessTables(XSSFSheet sheet, List<AttendanceDTO> data, ExcelStyleManager style) {
        List<AttendanceDTO> allLate = data.stream().filter(d -> d.getMinutosTardanza() > 0).collect(Collectors.toList());
        List<AttendanceDTO> mLate = allLate.stream().filter(d -> {
            if (d.getMi() == null || d.getMi().isEmpty()) return false;
            try { return Integer.parseInt(d.getMi().split(":")[0]) < 12; } catch (Exception e) { return false; }
        }).collect(Collectors.toList());

        List<AttendanceDTO> tLate = allLate.stream().filter(d -> {
            if (d.getTi() == null || d.getTi().isEmpty()) return false;
            try { return Integer.parseInt(d.getTi().split(":")[0]) >= 12; } catch (Exception e) { return false; }
        }).collect(Collectors.toList());

        int startRow = 12;
        applyMergedStyle(sheet, new CellRangeAddress(startRow, startRow, 12, 23), style.getHeaderStyle(11), "TARDANZAS DEL DÍA (ALERTAS)");

        applyMergedStyle(sheet, new CellRangeAddress(startRow + 1, startRow + 1, 12, 17), style.getSubHeaderStyleBlue(9), "TURNO MAÑANA");
        applyMergedStyle(sheet, new CellRangeAddress(startRow + 1, startRow + 1, 18, 23), style.getSubHeaderStyleBlue(9), "TURNO TARDE");

        Row h3 = getOrCreateRow(sheet, startRow + 2);
        String[] subCols = {"DNI", "NOMBRE", "INGRESO"};
        for (int i = 0; i < 3; i++) {
            if (i == 1) {
                applyMergedStyle(sheet, new CellRangeAddress(startRow+2, startRow+2, 13, 16), style.getSubHeaderStyle(9), subCols[i]);
                applyMergedStyle(sheet, new CellRangeAddress(startRow+2, startRow+2, 19, 22), style.getSubHeaderStyle(9), subCols[i]);
            } else {
                int colM = 12 + (i == 2 ? 5 : i); // M es 12, Ingreso es 17
                int colT = 18 + (i == 2 ? 5 : i); // T es 18, Ingreso es 23
                setCellValueAndStyle(h3, 12 + (i == 2 ? 5 : i), subCols[i], style.getSubHeaderStyle(9));
                setCellValueAndStyle(h3, 18 + (i == 2 ? 5 : i), subCols[i], style.getSubHeaderStyle(9));
            }
        }

        int maxLen = Math.max(mLate.size(), tLate.size());
        for (int i = 0; i < maxLen; i++) {
            Row r = getOrCreateRow(sheet, startRow + 3 + i);
            if (i < mLate.size()) {
                AttendanceDTO d = mLate.get(i);
                setCellValueAndStyle(r, 12, d.getDni(), style.getFondoWhiteStyleCenter(9));
                applyMergedStyle(sheet, new CellRangeAddress(startRow+3+i, startRow+3+i, 13, 16), style.getFondoWhiteStyleLeft(9), d.getApellido() + " " + d.getNombre());
                setCellValueAndStyle(r, 17, d.getMi(), style.getFondoLightRedStyle(9));
            }
            if (i < tLate.size()) {
                AttendanceDTO d = tLate.get(i);
                setCellValueAndStyle(r, 18, d.getDni(), style.getFondoWhiteStyleCenter(9));
                applyMergedStyle(sheet, new CellRangeAddress(startRow+3+i, startRow+3+i, 19, 22), style.getFondoWhiteStyleLeft(9), d.getApellido() + " " + d.getNombre());
                setCellValueAndStyle(r, 23, d.getTi(), style.getFondoLightRedStyle(9));
            }
        }
    }

    private void applyMergedStyle(XSSFSheet sheet, CellRangeAddress region, XSSFCellStyle style, String value) {
        sheet.addMergedRegion(region);
        for (int r = region.getFirstRow(); r <= region.getLastRow(); r++) {
            Row row = getOrCreateRow(sheet, r);
            for (int c = region.getFirstColumn(); c <= region.getLastColumn(); c++) {
                Cell cell = row.getCell(c);
                if (cell == null) cell = row.createCell(c);
                cell.setCellStyle(style);
                if (r == region.getFirstRow() && c == region.getFirstColumn()) {
                    cell.setCellValue(value);
                }
            }
        }
    }

    private void setCellValueAndStyle(Row row, int colIdx, Object value, XSSFCellStyle style) {
        Cell cell = row.getCell(colIdx);
        if (cell == null) cell = row.createCell(colIdx);
        if (value != null) {
            if (value instanceof Number) cell.setCellValue(((Number) value).doubleValue());
            else cell.setCellValue(value.toString());
        }
        cell.setCellStyle(style);
    }

    private Row getOrCreateRow(XSSFSheet sheet, int rowIdx) {
        Row row = sheet.getRow(rowIdx);
        return row == null ? sheet.createRow(rowIdx) : row;
    }
}
