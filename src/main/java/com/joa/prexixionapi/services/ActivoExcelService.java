package com.joa.prexixionapi.services;

import com.joa.prexixionapi.dto.ActivoDTO;
import com.joa.prexixionapi.dto.ActivoDepreciacionDTO;
import com.joa.prexixionapi.utils.DateUtils;
import com.joa.prexixionapi.utils.ExcelStyleManager;
import com.joa.prexixionapi.utils.PoiUtils;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivoExcelService {

    private final ActivoService activoService;

    public byte[] generateActivosFijosExcel(String idCliente, String anio, String ruc, String razonSocial)
            throws Exception {
        List<ActivoDTO> list = activoService.getActivosFijosExcelWithDepreciations(idCliente, anio);
        List<ActivoDTO> listRV = activoService.getActivosFijosRVExcel(idCliente);

        ClassPathResource resource = new ClassPathResource("templates/FormatoRegistroActivosFijos.xlsx");
        try (InputStream is = resource.getInputStream();
                XSSFWorkbook wb = new XSSFWorkbook(is);
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            ExcelStyleManager styleManager = new ExcelStyleManager(wb);

            // STYLES
            XSSFCellStyle styleCenter = styleManager.getCustomStyle(null, ExcelStyleManager.MATTE_BLACK_RGB, 9, false,
                    HorizontalAlignment.CENTER, BorderStyle.THIN, IndexedColors.BLACK);
            XSSFCellStyle styleLeft = styleManager.getCustomStyle(null, ExcelStyleManager.MATTE_BLACK_RGB, 9, false,
                    HorizontalAlignment.LEFT, BorderStyle.THIN, IndexedColors.BLACK);
            XSSFCellStyle styleRight = styleManager.getCustomStyle(null, ExcelStyleManager.MATTE_BLACK_RGB, 9, false,
                    HorizontalAlignment.RIGHT, BorderStyle.THIN, IndexedColors.BLACK);

            XSSFCellStyle styleLeft2 = styleManager.getCustomStyle(ExcelStyleManager.GERENCIA_BLUE_RGB,
                    ExcelStyleManager.WHITE_RGB, 9, true, HorizontalAlignment.LEFT, BorderStyle.THIN,
                    IndexedColors.WHITE);
            XSSFCellStyle styleCenter2 = styleManager.getCustomStyle(ExcelStyleManager.GERENCIA_BLUE_RGB,
                    ExcelStyleManager.WHITE_RGB, 9, true, HorizontalAlignment.CENTER, BorderStyle.THIN,
                    IndexedColors.WHITE);

            XSSFCellStyle styleMoney = styleManager.getMoneyStyle(null, ExcelStyleManager.MATTE_BLACK_RGB, 9, false,
                    IndexedColors.BLACK);

            XSSFCellStyle fondoGreyStyle = styleManager.getGenericStyle(ExcelStyleManager.LIGHT_GREY_RGB,
                    ExcelStyleManager.MATTE_BLACK_RGB, 9, true, HorizontalAlignment.RIGHT);
            PoiUtils.addBorders(fondoGreyStyle, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);

            XSSFCellStyle fondoGreyRightStyle = styleManager.getMoneyStyle(ExcelStyleManager.LIGHT_GREY_RGB,
                    ExcelStyleManager.MATTE_BLACK_RGB, 9, true, IndexedColors.GREY_25_PERCENT);

            // HOJA F71
            XSSFSheet hojaF71 = wb.getSheetAt(0);
            String anio2 = String.valueOf(Integer.parseInt(anio) - 1);
            fillCell(hojaF71, "C3", anio, styleLeft2);
            fillCell(hojaF71, "C4", ruc, styleLeft2);
            fillCell(hojaF71, "C5", razonSocial, styleLeft2);
            fillCell(hojaF71, "L7", "VALOR HISTÓRICO DEL A° FIJO AL 31.12." + anio2, styleCenter2);
            fillCell(hojaF71, "N7", "VALOR AJUSTADO DEL Aº FIJO AL 31.12." + anio2, styleCenter2);

            int rowNumF71 = 8;
            double totalSaldoI = 0, totalAdq = 0, totalRetiros = 0, totalValorHist = 0, totalValorAjust = 0;
            double totalAcumCier = 0, totalEjercicio = 0, totalRelactRt = 0, totalAcumHist = 0, totalAcumAjust = 0;

            for (ActivoDTO activo : list) {
                Row row = hojaF71.createRow(rowNumF71);
                int col = 0;
                createCell(row, col++, activo.getId(), styleCenter);
                createCell(row, col++, activo.getCuenta(), styleCenter);
                createCell(row, col++, activo.getDescripcion(), styleLeft);
                createCell(row, col++, activo.getMarca(), styleLeft);
                createCell(row, col++, activo.getModelo(), styleLeft);
                createCell(row, col++, activo.getSeriePlaca(), styleCenter);

                for (ActivoDepreciacionDTO dc : activo.getDepreciacionesContables()) {
                    createCell(row, col++, dc.getActivoSaldoInicial(), styleMoney);
                    totalSaldoI += dc.getActivoSaldoInicial();
                    createCell(row, col++, dc.getActivoCompras(), styleMoney);
                    totalAdq += dc.getActivoCompras();
                    createCell(row, col++, "", styleCenter); // Mejoras
                    createCell(row, col++, dc.getActivoRetiros(), styleMoney);
                    totalRetiros += dc.getActivoRetiros();
                    createCell(row, col++, "", styleCenter); // Ajustes
                    createCell(row, col++, dc.getValorHistorico(), styleMoney);
                    totalValorHist += dc.getValorHistorico();
                    createCell(row, col++, "", styleCenter); // Ajuste Infl
                    createCell(row, col++, dc.getValorHistorico(), styleMoney);
                    totalValorAjust += dc.getValorHistorico();
                    createCell(row, col++, DateUtils.formatChange(activo.getFechaCompra(), "dd/MM/yyyy"), styleCenter);
                    createCell(row, col++, DateUtils.formatChange(activo.getFechaInicio(), "dd/MM/yyyy"), styleCenter);
                    createCell(row, col++, "", styleCenter); // Met
                    createCell(row, col++, "", styleCenter); // NDoc
                    createCell(row, col++, activo.getPorcentajeDepreciacionContable(), styleRight);
                    createCell(row, col++, dc.getInicial(), styleMoney);
                    totalAcumCier += dc.getInicial();
                    createCell(row, col++, dc.getTotal(), styleMoney);
                    totalEjercicio += dc.getTotal();
                    createCell(row, col++, dc.getRetiros(), styleMoney);
                    totalRelactRt += dc.getRetiros();
                    createCell(row, col++, "", styleCenter); // RelacOtr
                    createCell(row, col++, dc.getAcumuladoHistorico(), styleMoney);
                    totalAcumHist += dc.getAcumuladoHistorico();
                    createCell(row, col++, "", styleCenter); // AI
                    createCell(row, col++, dc.getAcumuladoHistorico(), styleMoney);
                    totalAcumAjust += dc.getAcumuladoHistorico();
                }
                rowNumF71++;
            }

            // TOTALES F71
            Row footF71 = hojaF71.createRow(rowNumF71);
            for (int i = 0; i < 6; i++)
                footF71.createCell(i).setCellStyle(fondoGreyStyle);
            footF71.getCell(2).setCellValue("TOTALES");
            createCell(footF71, 6, totalSaldoI, fondoGreyRightStyle);
            createCell(footF71, 7, totalAdq, fondoGreyRightStyle);
            footF71.createCell(8).setCellStyle(fondoGreyStyle);
            createCell(footF71, 9, totalRetiros, fondoGreyRightStyle);
            footF71.createCell(10).setCellStyle(fondoGreyStyle);
            createCell(footF71, 11, totalValorHist, fondoGreyRightStyle);
            footF71.createCell(12).setCellStyle(fondoGreyStyle);
            createCell(footF71, 13, totalValorAjust, fondoGreyRightStyle);
            for (int i = 14; i < 19; i++)
                footF71.createCell(i).setCellStyle(fondoGreyStyle);
            createCell(footF71, 19, totalAcumCier, fondoGreyRightStyle);
            createCell(footF71, 20, totalEjercicio, fondoGreyRightStyle);
            createCell(footF71, 21, totalRelactRt, fondoGreyRightStyle);
            footF71.createCell(22).setCellStyle(fondoGreyStyle);
            createCell(footF71, 23, totalAcumHist, fondoGreyRightStyle);
            footF71.createCell(24).setCellStyle(fondoGreyStyle);
            createCell(footF71, 25, totalAcumAjust, fondoGreyRightStyle);
            hojaF71.createFreezePane(0, 8);

            // HOJA F72
            XSSFSheet hojaF72 = wb.getSheetAt(1);
            fillCell(hojaF72, "C3", anio, styleLeft2);
            fillCell(hojaF72, "C4", ruc, styleLeft2);
            fillCell(hojaF72, "C5", razonSocial, styleLeft2);
            fillCell(hojaF72, "O8", "VALOR HISTÓRICO DEL A° FIJO AL 31.12." + anio2, styleCenter2);
            fillCell(hojaF72, "Q8", "VALOR AJUSTADO DEL Aº FIJO AL 31.12." + anio2, styleCenter2);

            int rowNumF72 = 9;
            double totalVoluntariaRev = 0, totalValorHistRev = 0, totalValorAjustRev = 0;
            for (ActivoDTO activoRT : listRV) {
                Row row = hojaF72.createRow(rowNumF72);
                int col = 0;
                createCell(row, col++, activoRT.getId(), styleCenter);
                createCell(row, col++, activoRT.getCuenta(), styleCenter);
                createCell(row, col++, activoRT.getDescripcion(), styleLeft);
                createCell(row, col++, activoRT.getMarca(), styleLeft);
                createCell(row, col++, activoRT.getModelo(), styleLeft);
                createCell(row, col++, activoRT.getSeriePlaca(), styleCenter);
                for (int i = 0; i < 5; i++)
                    createCell(row, col++, "", styleCenter);
                createCell(row, col++, activoRT.getCostoInicial(), styleMoney);
                totalVoluntariaRev += activoRT.getCostoInicial();
                createCell(row, col++, "", styleCenter);
                createCell(row, col++, "", styleCenter);
                createCell(row, col++, activoRT.getCostoInicial(), styleMoney);
                totalValorHistRev += activoRT.getCostoInicial();
                createCell(row, col++, "", styleCenter);
                createCell(row, col++, activoRT.getCostoInicial(), styleMoney);
                totalValorAjustRev += activoRT.getCostoInicial();
                createCell(row, col++, DateUtils.formatChange(activoRT.getFechaCompra(), "dd/MM/yyyy"), styleCenter);
                createCell(row, col++, DateUtils.formatChange(activoRT.getFechaInicio(), "dd/MM/yyyy"), styleCenter);
                for (int i = 0; i < 13; i++)
                    createCell(row, col++, "", styleCenter);
                rowNumF72++;
            }
            Row footF72 = hojaF72.createRow(rowNumF72);
            createCell(footF72, 5, "TOTALES", styleCenter2);
            for (int i = 6; i < 11; i++)
                createCell(footF72, i, "-", fondoGreyStyle);
            createCell(footF72, 11, totalVoluntariaRev, fondoGreyRightStyle);
            createCell(footF72, 12, "-", fondoGreyStyle);
            createCell(footF72, 13, "-", fondoGreyStyle);
            createCell(footF72, 14, totalValorHistRev, fondoGreyRightStyle);
            createCell(footF72, 15, "-", fondoGreyStyle);
            createCell(footF72, 16, totalValorAjustRev, fondoGreyRightStyle);
            for (int i = 17; i < 32; i++)
                createCell(footF72, i, i < 22 ? "" : "-", fondoGreyStyle);
            hojaF72.createFreezePane(0, 9);

            // HOJA F73
            XSSFSheet hojaF73 = wb.getSheetAt(2);
            fillCell(hojaF73, "B3", anio, styleLeft2);
            fillCell(hojaF73, "B4", ruc, styleLeft2);
            fillCell(hojaF73, "B5", razonSocial, styleLeft2);
            hojaF73.createFreezePane(0, 8);

            // HOJA F74
            XSSFSheet hojaF74 = wb.getSheetAt(3);
            fillCell(hojaF74, "B3", anio, styleLeft2);
            fillCell(hojaF74, "B4", ruc, styleLeft2);
            fillCell(hojaF74, "B5", razonSocial, styleLeft2);
            int rowNumF74 = 7;
            double totalMontoContrato = 0;
            for (ActivoDTO activoL : list) {
                if (activoL.getIdTipo() != null && activoL.getIdTipo().startsWith("L")) {
                    Row row = hojaF74.createRow(rowNumF74);
                    int col = 0;
                    createCell(row, col++, DateUtils.formatChange(activoL.getFechaCompra(), "dd/MM/yyyy"), styleCenter);
                    createCell(row, col++, activoL.getProveedor() + " " + activoL.getDocumento(), styleLeft);
                    createCell(row, col++, DateUtils.formatChange(activoL.getFechaInicio(), "dd/MM/yyyy"), styleCenter);
                    createCell(row, col++, activoL.getTipo(), styleLeft);
                    createCell(row, col++, activoL.getCostoInicial(), styleMoney);
                    totalMontoContrato += activoL.getCostoInicial();
                    rowNumF74++;
                }
            }
            Row footF74 = hojaF74.createRow(rowNumF74);
            createCell(footF74, 3, "TOTAL", styleCenter2);
            createCell(footF74, 4, totalMontoContrato, fondoGreyRightStyle);
            hojaF74.createFreezePane(0, 7);

            wb.write(out);
            return out.toByteArray();
        }
    }

    private void fillCell(Sheet sheet, String cellReference, Object value, CellStyle cs) {
        CellReference celda = new CellReference(cellReference);
        Row row = sheet.getRow(celda.getRow()) == null ? sheet.createRow(celda.getRow()) : sheet.getRow(celda.getRow());
        Cell cell = row.getCell(celda.getCol()) == null ? row.createCell(celda.getCol()) : row.getCell(celda.getCol());
        if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else {
            cell.setCellValue(String.valueOf(value));
        }
        cell.setCellStyle(cs);
    }

    private void createCell(Row row, int col, Object value, CellStyle cs) {
        Cell cell = row.createCell(col);
        if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value != null) {
            cell.setCellValue(String.valueOf(value));
        }
        cell.setCellStyle(cs);
    }
}
