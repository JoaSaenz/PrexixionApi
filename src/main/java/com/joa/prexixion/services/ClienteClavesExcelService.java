package com.joa.prexixion.services;

import com.joa.prexixion.dto.ClienteClavesProjection;
import com.joa.prexixion.repositories.ClienteClavesRepository;
import com.joa.prexixion.utils.PoiUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

@Service
public class ClienteClavesExcelService {

    private final ClienteClavesRepository repository;

    public ClienteClavesExcelService(ClienteClavesRepository repository) {
        this.repository = repository;
    }

    public byte[] exportarExcelClaves(String estados, String grupos) {
        List<Integer> listEstados = Arrays.stream(estados.split(","))
                .map(String::trim).map(Integer::valueOf).toList();
        List<Integer> listGrupos = Arrays.stream(grupos.split(","))
                .map(String::trim).map(Integer::valueOf).toList();

        List<ClienteClavesProjection> list = repository.getClavesData(listEstados, listGrupos);
        return generateExcel(list);
    }

    private byte[] generateExcel(List<ClienteClavesProjection> list) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            XSSFWorkbook wb = new XSSFWorkbook();

            // --- COLORES ---
            XSSFColor GERENCIA_BLUE = PoiUtils.createColor(0, 51, 204);
            XSSFColor GERENCIA_GREY = PoiUtils.createColor(214, 220, 228);
            XSSFColor MATTE_BLACK   = PoiUtils.createColor(43, 43, 43);

            XSSFColor VERY_LIGHT_RED = PoiUtils.createColor(253, 237, 236);
            XSSFColor RED            = PoiUtils.createColor(255, 0, 0);
            XSSFColor DARK_RED       = PoiUtils.createColor(204, 0, 0);
            XSSFColor VERY_LIGHT_BLUE  = PoiUtils.createColor(235, 245, 251);
            XSSFColor LIGHT_BLUE       = PoiUtils.createColor(51, 153, 255);
            XSSFColor BLUE             = PoiUtils.createColor(0, 0, 255);
            XSSFColor DARK_BLUE        = PoiUtils.createColor(0, 51, 204);
            XSSFColor VERY_LIGHT_GREEN = PoiUtils.createColor(244, 251, 235);
            XSSFColor GREEN            = PoiUtils.createColor(0, 204, 0);
            XSSFColor VERY_LIGHT_YELLOW  = PoiUtils.createColor(254, 249, 231);
            XSSFColor YELLOW             = PoiUtils.createColor(255, 255, 0);
            XSSFColor VERY_LIGHT_ORANGE  = PoiUtils.createColor(245, 203, 167);
            XSSFColor ORANGE             = PoiUtils.createColor(255, 102, 0);
            XSSFColor GOLD               = PoiUtils.createColor(255, 204, 51);
            XSSFColor LIGHT_GREY  = PoiUtils.createColor(242, 242, 242);
            XSSFColor GREY        = PoiUtils.createColor(128, 128, 128);
            XSSFColor DARK_GREY   = PoiUtils.createColor(102, 102, 102);
            XSSFColor BLACK       = PoiUtils.createColor(0, 0, 0);
            XSSFColor WHITE       = PoiUtils.createColor(255, 255, 255);
            XSSFColor CELESTE_COLOR  = PoiUtils.createColor(138, 193, 255);
            XSSFColor TURQUESA_COLOR = PoiUtils.createColor(81, 198, 218);
            XSSFColor AMARILLO_COLOR = PoiUtils.createColor(254, 255, 155);
            XSSFColor NARANJA_COLOR  = PoiUtils.createColor(252, 184, 72);
            XSSFColor ROJO_COLOR     = PoiUtils.createColor(169, 0, 54);

            // --- FONTS ---
            boolean negrita = true;
            XSSFFont cabeceraFont  = PoiUtils.fuente(wb, WHITE, 17, negrita);
            XSSFFont fontWhite9b   = PoiUtils.fuente(wb, WHITE, 9, negrita);
            XSSFFont fontWhite9    = PoiUtils.fuente(wb, WHITE, 9);
            XSSFFont fontBlack9b   = PoiUtils.fuente(wb, MATTE_BLACK, 9, negrita);
            XSSFFont fontBlack9    = PoiUtils.fuente(wb, MATTE_BLACK, 9);
            XSSFFont fontGreen9b   = PoiUtils.fuente(wb, GREEN, 9, negrita);
            XSSFFont fontRed9b     = PoiUtils.fuente(wb, RED, 9, negrita);
            XSSFFont fontGrey9b    = PoiUtils.fuente(wb, GREY, 9, negrita);
            XSSFFont fontYellow9b  = PoiUtils.fuente(wb, ORANGE, 9, negrita);
            XSSFFont fontLightBlue9b = PoiUtils.fuente(wb, LIGHT_BLUE, 9, negrita);
            XSSFFont fontBlue9b    = PoiUtils.fuente(wb, BLUE, 9, negrita);

            // --- ESTILOS ---
            XSSFCellStyle fondoGreyStyle = PoiUtils.createCellStyle(wb, HorizontalAlignment.CENTER, LIGHT_GREY, fontBlack9);
            PoiUtils.addBorders(fondoGreyStyle, BorderStyle.THIN, IndexedColors.WHITE);
            XSSFCellStyle fondoGreyStyleLeft = PoiUtils.createCellStyle(wb, HorizontalAlignment.LEFT, LIGHT_GREY, fontBlack9);
            PoiUtils.addBorders(fondoGreyStyleLeft, BorderStyle.THIN, IndexedColors.WHITE);

            XSSFCellStyle dataStyle2 = PoiUtils.createCellStyle(wb, HorizontalAlignment.CENTER, LIGHT_GREY, fontBlack9b);
            PoiUtils.addBorders(dataStyle2, BorderStyle.THIN, IndexedColors.WHITE);

            XSSFCellStyle fondoBlackStyle = PoiUtils.createCellStyle(wb, HorizontalAlignment.CENTER, BLACK, fontWhite9b);

            XSSFCellStyle dataRedStyle = PoiUtils.createCellStyle(wb, HorizontalAlignment.CENTER, LIGHT_GREY, fontRed9b);
            PoiUtils.addBorders(dataRedStyle, BorderStyle.THIN, IndexedColors.WHITE);

            XSSFCellStyle cabeceraStyle = PoiUtils.createCellStyle(wb, HorizontalAlignment.CENTER, GERENCIA_BLUE, cabeceraFont);
            XSSFCellStyle subHeaderStyle = PoiUtils.createCellStyle(wb, HorizontalAlignment.CENTER, GERENCIA_BLUE, fontWhite9b);
            PoiUtils.addBorders(subHeaderStyle, BorderStyle.THIN, IndexedColors.WHITE);

            // --- HOJA ---
            XSSFSheet sheet = wb.createSheet("REPORTE");
            int rowNum = 0;
            int colNum = 0;

            // CABECERA PRINCIPAL
            Row cabecera = sheet.createRow(rowNum);
            cabecera.setHeightInPoints(sheet.getDefaultRowHeightInPoints() * 3);
            Cell cellCabecera = cabecera.createCell(0);
            cellCabecera.setCellStyle(cabeceraStyle);
            cellCabecera.setCellValue("C  O  R  P  O  R  A  C  I  O  N      G  E  R  E  N  C  I  A.  C  O  M      S. A. C.");
            sheet.addMergedRegion(CellRangeAddress.valueOf("A1:U1"));

            rowNum++;
            Row subHeader = sheet.createRow(rowNum);

            String[] headers = {"N°", "CAT", "CGE", "STO", "Estado", "RUC", "Y", "C", "Razón Social",
                    "Main U.", "Main C.", "UPS U.", "UPS C.", "Gear U.", "Gear C.",
                    "Signer U.", "Signer C.", "AfpNet U.", "AfpNet C.", "Sis U.", "Sis C."};
            for (String h : headers) {
                Cell c = subHeader.createCell(colNum++);
                c.setCellStyle(subHeaderStyle);
                c.setCellValue(h);
            }

            rowNum++;
            int inicioFilt = rowNum - 1;
            rowNum++;

            // --- DATA ---
            int i = 1;
            for (ClienteClavesProjection c : list) {
                Row data = sheet.createRow(rowNum);
                colNum = 0;

                // N°
                Cell dataN = data.createCell(colNum++);
                dataN.setCellStyle(fondoBlackStyle);
                dataN.setCellValue(i);

                // CAT
                Cell dataCategoria = data.createCell(colNum++);
                dataCategoria.setCellStyle(dataStyle2);
                dataCategoria.setCellValue(c.getAbrCategoria() != null ? c.getAbrCategoria() : "");

                // CGE
                Cell dataCGE = data.createCell(colNum++);
                Integer cge = c.getCategoriaGrupoE();
                if (cge != null && cge == 1) {
                    dataCGE.setCellStyle(dataRedStyle);
                    dataCGE.setCellValue("SI");
                } else {
                    dataCGE.setCellStyle(dataStyle2);
                    dataCGE.setCellValue("");
                }

                // STO
                Cell dataSTO = data.createCell(colNum++);
                Integer sto = c.getCategoriaStore();
                if (sto != null && sto == 1) {
                    dataSTO.setCellStyle(dataRedStyle);
                    dataSTO.setCellValue("SI");
                } else {
                    dataSTO.setCellStyle(dataStyle2);
                    dataSTO.setCellValue("");
                }

                // Estado
                Cell dataEstado = data.createCell(colNum++);
                dataEstado.setCellStyle(dataStyle2);
                dataEstado.setCellValue(c.getDescEstado() != null ? c.getDescEstado() : "");

                // RUC
                Cell dataRuc = data.createCell(colNum++);
                dataRuc.setCellStyle(dataStyle2);
                dataRuc.setCellValue(c.getRuc() != null ? c.getRuc() : "");

                // Y
                Cell dataY = data.createCell(colNum++);
                dataY.setCellStyle(dataStyle2);
                dataY.setCellValue(c.getY() != null ? c.getY() : "");

                // C (Contribuyente)
                Cell dataC = data.createCell(colNum++);
                dataC.setCellStyle(dataStyle2);
                dataC.setCellValue(c.getAbrContribuyente() != null ? c.getAbrContribuyente() : "");

                // Razón Social
                Cell dataRZ = data.createCell(colNum++);
                dataRZ.setCellStyle(fondoGreyStyleLeft);
                dataRZ.setCellValue(c.getRazonSocial() != null ? c.getRazonSocial() : "");

                // Main U / C
                createStringCell(data, colNum++, c.getSolU(), fondoGreyStyle);
                createStringCell(data, colNum++, c.getSolC(), fondoGreyStyle);

                // UPS U / C
                createStringCell(data, colNum++, c.getUpsU(), fondoGreyStyle);
                createStringCell(data, colNum++, c.getUpsC(), fondoGreyStyle);

                // Gear U / C
                createStringCell(data, colNum++, c.getSoldierU(), fondoGreyStyle);
                createStringCell(data, colNum++, c.getSoldierC(), fondoGreyStyle);

                // Signer U / C
                createStringCell(data, colNum++, c.getSignerU(), fondoGreyStyle);
                createStringCell(data, colNum++, c.getSignerC(), fondoGreyStyle);

                // AfpNet U / C
                createStringCell(data, colNum++, c.getAfpU(), fondoGreyStyle);
                createStringCell(data, colNum++, c.getAfpC(), fondoGreyStyle);

                // Sis U / C
                createStringCell(data, colNum++, c.getSisU(), fondoGreyStyle);
                createStringCell(data, colNum++, c.getSisC(), fondoGreyStyle);

                rowNum++;
                i++;
            }

            int finFilt = rowNum - 1;
            sheet.setAutoFilter(new CellRangeAddress(inicioFilt, finFilt, 0, 20));
            sheet.createFreezePane(0, 3);

            for (int contCol = 0; contCol < colNum; contCol++) {
                sheet.autoSizeColumn(contCol);
            }

            wb.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generando Excel de claves", e);
        }
    }

    private void createStringCell(Row row, int col, String value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellStyle(style);
        cell.setCellValue(value != null ? value : "");
    }
}
