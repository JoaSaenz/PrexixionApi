package com.joa.prexixionapi.services;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joa.prexixionapi.entities.Cliente;
import com.joa.prexixionapi.entities.LoginCompra;
import com.joa.prexixionapi.entities.LoginVenta;
import com.joa.prexixionapi.repositories.LoginCompraRepository;
import com.joa.prexixionapi.utils.PoiUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LoginCompraExcelService {

    @Autowired
    private LoginCompraRepository loginCompraRepository;

    public byte[] exportarExcel(String anio, String mes, String estados, String grupos) {
        List<Cliente> list = loginCompraRepository.list(anio, mes, estados, grupos);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Workbook wb = new XSSFWorkbook();
            Sheet sheet = wb.createSheet("Compras");

            int contMasivaSunatSi = 0;
            int contMasivaSunatNo = 0;
            int contMasivaSunatNa = 0;
            
            int contValidacionVal = 0;
            int contValidacionSi = 0;
            int contValidacionNo = 0;
            int contValidacionNa = 0;
            
            int contConfirmacionSi = 0;
            int contConfirmacionNo = 0;

            DefaultIndexedColorMap colorMap = new DefaultIndexedColorMap();

            // <editor-fold defaultstate="collapsed" desc=" COLORES Y FUENTES ">
            XSSFColor GERENCIA_BLUE = new XSSFColor(new java.awt.Color(0, 51, 204), colorMap);
            XSSFColor GERENCIA_GREY = new XSSFColor(new java.awt.Color(214, 220, 228), colorMap);
            XSSFColor MATTE_BLACK = new XSSFColor(new java.awt.Color(43, 43, 43), colorMap);

            XSSFColor RED = new XSSFColor(new java.awt.Color(255, 0, 0), colorMap);
            XSSFColor GREEN = new XSSFColor(new java.awt.Color(0, 204, 0), colorMap);
            XSSFColor YELLOW = new XSSFColor(new java.awt.Color(255, 255, 0), colorMap);
            XSSFColor ORANGE = new XSSFColor(new java.awt.Color(255, 102, 0), colorMap);

            XSSFColor LIGHT_GREY = new XSSFColor(new java.awt.Color(240, 240, 240), colorMap);
            XSSFColor GREY = new XSSFColor(new java.awt.Color(153, 153, 153), colorMap);

            XSSFColor BLACK = new XSSFColor(new java.awt.Color(0, 0, 0), colorMap);
            XSSFColor WHITE = new XSSFColor(new java.awt.Color(255, 255, 255), colorMap);

            Boolean negrita = true;
            XSSFFont cabeceraFont = PoiUtils.fuente((XSSFWorkbook) wb, WHITE, 17, negrita);
            XSSFFont subHeaderFont = PoiUtils.fuente((XSSFWorkbook) wb, WHITE, 9, negrita);
            XSSFFont dataSimpleFont = PoiUtils.fuente((XSSFWorkbook) wb, MATTE_BLACK, 9, negrita);
            XSSFFont dataGreenFont = PoiUtils.fuente((XSSFWorkbook) wb, GREEN, 9, negrita);
            XSSFFont dataRedFont = PoiUtils.fuente((XSSFWorkbook) wb, RED, 9, negrita);
            XSSFFont dataGreyFont = PoiUtils.fuente((XSSFWorkbook) wb, GREY, 9, negrita);
            XSSFFont dataOrangeFont = PoiUtils.fuente((XSSFWorkbook) wb, ORANGE, 9, negrita);
            XSSFFont dataStarFont = PoiUtils.fuente((XSSFWorkbook) wb, MATTE_BLACK, 9, negrita);
            dataStarFont.setFontName("Wingdings");
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc=" CELL STYLES ">
            CellStyle cabeceraStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, GERENCIA_BLUE, cabeceraFont);
            CellStyle subHeaderStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, MATTE_BLACK, subHeaderFont);
            PoiUtils.addBorders(subHeaderStyle, BorderStyle.THIN, IndexedColors.WHITE);
            CellStyle cabeceraStyleRes = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, GERENCIA_BLUE, subHeaderFont);
            PoiUtils.addBorders(cabeceraStyleRes, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataStyleCellNegro = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, BLACK, subHeaderFont);
            PoiUtils.addBorders(dataStyleCellNegro, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, GERENCIA_GREY, dataSimpleFont);
            PoiUtils.addBorders(dataStyle, BorderStyle.THIN, IndexedColors.WHITE);
            CellStyle dataLeftStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.LEFT, GERENCIA_GREY, dataSimpleFont);
            PoiUtils.addBorders(dataLeftStyle, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataStyle2 = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, LIGHT_GREY, dataSimpleFont);
            PoiUtils.addBorders(dataStyle2, BorderStyle.THIN, IndexedColors.WHITE);
            CellStyle dataStyle2Neg = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, LIGHT_GREY, dataSimpleFont);
            PoiUtils.addBorders(dataStyle2Neg, BorderStyle.THIN, IndexedColors.WHITE);
            CellStyle dataStyleLeft2 = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.LEFT, LIGHT_GREY, dataSimpleFont);
            PoiUtils.addBorders(dataStyleLeft2, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataGreenStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, LIGHT_GREY, dataGreenFont);
            PoiUtils.addBorders(dataGreenStyle, BorderStyle.THIN, IndexedColors.WHITE);
            CellStyle dataRedStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, LIGHT_GREY, dataRedFont);
            PoiUtils.addBorders(dataRedStyle, BorderStyle.THIN, IndexedColors.WHITE);
            CellStyle dataOrangeStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, LIGHT_GREY, dataOrangeFont);
            PoiUtils.addBorders(dataOrangeStyle, BorderStyle.THIN, IndexedColors.WHITE);
            // </editor-fold>

            int rowNum = 0;
            int colNum = 0;

            // <editor-fold defaultstate="collapsed" desc="CABECERA RESUMEN">
            Row cabeceraResumen = sheet.createRow(rowNum);
            colNum = CellReference.convertColStringToIndex("M");

            Cell cellHeadResumen = cabeceraResumen.createCell(colNum);
            cellHeadResumen.setCellStyle(cabeceraStyleRes);
            cellHeadResumen.setCellValue("RESUMEN");
            colNum++;
            Cell cellHeadResumenMasivaSunat = cabeceraResumen.createCell(colNum);
            cellHeadResumenMasivaSunat.setCellStyle(cabeceraStyleRes);
            cellHeadResumenMasivaSunat.setCellValue("M SUNAT");
            colNum++;
            Cell cellHeadResumenValidacion = cabeceraResumen.createCell(colNum);
            cellHeadResumenValidacion.setCellStyle(cabeceraStyleRes);
            cellHeadResumenValidacion.setCellValue("VAL");
            colNum++;
            Cell cellHeadResumenConfirmacion = cabeceraResumen.createCell(colNum);
            cellHeadResumenConfirmacion.setCellStyle(cabeceraStyleRes);
            cellHeadResumenConfirmacion.setCellValue("CON");
            colNum++;
            rowNum++;

            Row rowOtr = sheet.createRow(rowNum);
            colNum = CellReference.convertColStringToIndex("M");

            Cell cellOtr = rowOtr.createCell(colNum);
            cellOtr.setCellStyle(cabeceraStyleRes);
            cellOtr.setCellValue("OTR");
            colNum++;
            Cell cellOtrMasivaSunat = rowOtr.createCell(colNum);
            cellOtrMasivaSunat.setCellStyle(dataOrangeStyle);
            cellOtrMasivaSunat.setCellValue("-");
            colNum++;
            Cell cellOtrValidacion = rowOtr.createCell(colNum);
            cellOtrValidacion.setCellStyle(dataOrangeStyle);
            colNum++;
            Cell cellOtrConfirmacion = rowOtr.createCell(colNum);
            cellOtrConfirmacion.setCellStyle(dataOrangeStyle);
            cellOtrConfirmacion.setCellValue("-");
            colNum++;
            rowNum++;

            Row rowTerminado = sheet.createRow(rowNum);
            colNum = CellReference.convertColStringToIndex("M");

            Cell cellTerminado = rowTerminado.createCell(colNum);
            cellTerminado.setCellStyle(cabeceraStyleRes);
            cellTerminado.setCellValue("TERMINADO");
            colNum++;
            Cell cellTerminadoMasivaSunat = rowTerminado.createCell(colNum);
            cellTerminadoMasivaSunat.setCellStyle(dataGreenStyle);
            colNum++;
            Cell cellTerminadoValidacion = rowTerminado.createCell(colNum);
            cellTerminadoValidacion.setCellStyle(dataGreenStyle);
            colNum++;
            Cell cellTerminadoConfirmacion = rowTerminado.createCell(colNum);
            cellTerminadoConfirmacion.setCellStyle(dataGreenStyle);
            colNum++;
            rowNum++;

            Row rowPendiente = sheet.createRow(rowNum);
            colNum = CellReference.convertColStringToIndex("M");

            Cell cellPendiente = rowPendiente.createCell(colNum);
            cellPendiente.setCellStyle(cabeceraStyleRes);
            cellPendiente.setCellValue("PENDIENTE");
            colNum++;
            Cell cellPendienteMasivaSunat = rowPendiente.createCell(colNum);
            cellPendienteMasivaSunat.setCellStyle(dataRedStyle);
            colNum++;
            Cell cellPendienteValidacion = rowPendiente.createCell(colNum);
            cellPendienteValidacion.setCellStyle(dataRedStyle);
            colNum++;
            Cell cellPendienteConfirmacion = rowPendiente.createCell(colNum);
            cellPendienteConfirmacion.setCellStyle(dataRedStyle);
            colNum++;
            rowNum++;

            Row rowNoAplica = sheet.createRow(rowNum);
            colNum = CellReference.convertColStringToIndex("M");

            Cell cellNoAplica = rowNoAplica.createCell(colNum);
            cellNoAplica.setCellStyle(cabeceraStyleRes);
            cellNoAplica.setCellValue("NO APLICA");
            colNum++;
            Cell cellNoAplicaMasivaSunat = rowNoAplica.createCell(colNum);
            cellNoAplicaMasivaSunat.setCellStyle(dataStyle2Neg);
            colNum++;
            Cell cellNoAplicaValidacion = rowNoAplica.createCell(colNum);
            cellNoAplicaValidacion.setCellStyle(dataStyle2Neg);
            colNum++;
            Cell cellNoAplicaConfirmacion = rowNoAplica.createCell(colNum);
            cellNoAplicaConfirmacion.setCellStyle(dataStyle2Neg);
            cellNoAplicaConfirmacion.setCellValue("-");
            colNum++;
            rowNum++;

            Row rowTotal = sheet.createRow(rowNum);
            colNum = CellReference.convertColStringToIndex("M");

            Cell cellTotal = rowTotal.createCell(colNum);
            cellTotal.setCellStyle(cabeceraStyleRes);
            cellTotal.setCellValue("TOTAL");
            colNum++;
            Cell cellTotalMasivaSunat = rowTotal.createCell(colNum);
            cellTotalMasivaSunat.setCellStyle(dataStyle);
            colNum++;
            Cell cellTotalValidacion = rowTotal.createCell(colNum);
            cellTotalValidacion.setCellStyle(dataStyle);
            colNum++;
            Cell cellTotalConfirmacion = rowTotal.createCell(colNum);
            cellTotalConfirmacion.setCellStyle(dataStyle);
            colNum++;
            rowNum++;
            rowNum++;
            // </editor-fold>

            colNum = 0;

            // <editor-fold defaultstate="collapsed" desc="CABECERA">
            Row header = sheet.createRow(rowNum);
            header.setHeightInPoints(sheet.getDefaultRowHeightInPoints() * 3);

            Cell header0 = header.createCell(colNum);
            header0.setCellStyle(cabeceraStyle);
            header0.setCellValue("L O G I N   C O M P R A S :   " + anio + "-" + mes);
            CellRangeAddress region = CellRangeAddress.valueOf("A8:Q8");
            sheet.addMergedRegion(region);

            rowNum++;
            colNum = 0;

            Row subheader1 = sheet.createRow(rowNum);

            Cell sub1Contribuyente = subheader1.createCell(colNum);
            sub1Contribuyente.setCellStyle(subHeaderStyle);
            sub1Contribuyente.setCellValue("CONTRIBUYENTE");
            region = CellRangeAddress.valueOf("A9:J9");
            sheet.addMergedRegion(region);
            PoiUtils.addBorders(region, BorderStyle.THIN, IndexedColors.WHITE, sheet);
            colNum = 10;

            Cell sub1ProcesoValidacion = subheader1.createCell(colNum);
            sub1ProcesoValidacion.setCellStyle(subHeaderStyle);
            sub1ProcesoValidacion.setCellValue("PROCESO VALIDACIÓN");
            region = CellRangeAddress.valueOf("K9:P9");
            sheet.addMergedRegion(region);
            PoiUtils.addBorders(region, BorderStyle.THIN, IndexedColors.WHITE, sheet);
            colNum = 16;

            Cell sub1Observacion = subheader1.createCell(colNum);
            sub1Observacion.setCellStyle(subHeaderStyle);
            sub1Observacion.setCellValue("OBSERVACION");
            region = CellRangeAddress.valueOf("Q9:Q10");
            sheet.addMergedRegion(region);
            PoiUtils.addBorders(region, BorderStyle.THIN, IndexedColors.WHITE, sheet);
            
            rowNum++;
            colNum = 0;

            Row subheader2 = sheet.createRow(rowNum);

            Cell sub2N = subheader2.createCell(colNum);
            sub2N.setCellStyle(subHeaderStyle);
            sub2N.setCellValue("N°");
            colNum++;
            
            Cell sub2Estado = subheader2.createCell(colNum);
            sub2Estado.setCellStyle(subHeaderStyle);
            sub2Estado.setCellValue("ESTADO");
            colNum++;
            
            Cell sub2CategoriaStore = subheader2.createCell(colNum);
            sub2CategoriaStore.setCellStyle(subHeaderStyle);
            sub2CategoriaStore.setCellValue("STO");
            colNum++;
            
            Cell sub2GrupoEconomico = subheader2.createCell(colNum);
            sub2GrupoEconomico.setCellStyle(subHeaderStyle);
            sub2GrupoEconomico.setCellValue("GE");
            colNum++;
            
            Cell sub2Y = subheader2.createCell(colNum);
            sub2Y.setCellStyle(subHeaderStyle);
            sub2Y.setCellValue("Y");
            colNum++;
            
            Cell sub2Signer = subheader2.createCell(colNum);
            sub2Signer.setCellStyle(subHeaderStyle);
            sub2Signer.setCellValue("SIGNER");
            colNum++;
            
            Cell sub2RegimenTributario = subheader2.createCell(colNum);
            sub2RegimenTributario.setCellStyle(subHeaderStyle);
            sub2RegimenTributario.setCellValue("RT");
            colNum++;

            Cell sub2Periodo = subheader2.createCell(colNum);
            sub2Periodo.setCellStyle(subHeaderStyle);
            sub2Periodo.setCellValue("PERIODO");
            colNum++;

            Cell sub2VencimientoDJ = subheader2.createCell(colNum);
            sub2VencimientoDJ.setCellStyle(subHeaderStyle);
            sub2VencimientoDJ.setCellValue("V DJ");
            colNum++;

            Cell sub2Movimiento = subheader2.createCell(colNum);
            sub2Movimiento.setCellStyle(subHeaderStyle);
            sub2Movimiento.setCellValue("MOV");
            colNum++;
            
            Cell sub2ComprasFilas = subheader2.createCell(colNum);
            sub2ComprasFilas.setCellStyle(subHeaderStyle);
            sub2ComprasFilas.setCellValue("C-FIL");
            colNum++;

            Cell sub2VentasConfirmacion = subheader2.createCell(colNum);
            sub2VentasConfirmacion.setCellStyle(subHeaderStyle);
            sub2VentasConfirmacion.setCellValue("V-CON");
            colNum++;

            Cell sub2MasivaSunat = subheader2.createCell(colNum);
            sub2MasivaSunat.setCellStyle(subHeaderStyle);
            sub2MasivaSunat.setCellValue("M SUNAT");
            colNum++;

            Cell sub2Validacion = subheader2.createCell(colNum);
            sub2Validacion.setCellStyle(subHeaderStyle);
            sub2Validacion.setCellValue("VAL");
            colNum++;

            Cell sub2ValidacionUsuario = subheader2.createCell(colNum);
            sub2ValidacionUsuario.setCellStyle(subHeaderStyle);
            sub2ValidacionUsuario.setCellValue("VAL-USUARIO");
            colNum++;

            Cell sub2Confirmacion = subheader2.createCell(colNum);
            sub2Confirmacion.setCellStyle(subHeaderStyle);
            sub2Confirmacion.setCellValue("CON");
            colNum++;

            Cell sub2Observacion = subheader2.createCell(colNum);
            sub2Observacion.setCellStyle(subHeaderStyle);
            colNum++;
            rowNum++;
            // </editor-fold>

            int inicioFilt = rowNum;
            rowNum++;

            int i = 1;

            // <editor-fold defaultstate="collapsed" desc="DATA">
            for (Cliente clie : list) {
                LoginCompra lc = clie.getLoginCompra() != null ? clie.getLoginCompra() : new LoginCompra();
                LoginVenta lv = clie.getLoginVenta() != null ? clie.getLoginVenta() : new LoginVenta();
                
                Row dataRow = sheet.createRow(rowNum);
                colNum = 0;

                Cell dataN = dataRow.createCell(colNum);
                dataN.setCellStyle(dataStyleCellNegro);
                dataN.setCellValue(i);
                colNum++;                

                Cell dataEstado = dataRow.createCell(colNum);
                dataEstado.setCellStyle(dataStyle2);
                dataEstado.setCellValue(clie.getEstado() != null ? clie.getEstado().getDescripcion() : "");
                colNum++;
                
                Cell dataCategoriaStore = dataRow.createCell(colNum);
                dataCategoriaStore.setCellStyle(dataStyle2);
                int catStore = (clie.getSignerNivel() != null) ? clie.getSignerNivel().getCategoriaStore() : 0;
                switch (catStore) {
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
                
                Cell dataGrupoEconomico = dataRow.createCell(colNum);
                dataGrupoEconomico.setCellStyle(dataStyle2);
                dataGrupoEconomico.setCellValue((clie.getGrupoEconomico() != null) ? clie.getGrupoEconomico().getDescripcion() : "");
                colNum++;
                
                Cell dataY = dataRow.createCell(colNum);
                dataY.setCellStyle(dataStyle);
                dataY.setCellValue(clie.getY() != null ? clie.getY() : "");
                colNum++;
                
                Cell dataSigner = dataRow.createCell(colNum);
                dataSigner.setCellStyle(dataLeftStyle);
                dataSigner.setCellValue(clie.getNombreCorto() != null ? clie.getNombreCorto() : "");
                colNum++;
                
                Cell dataRegimenTributario = dataRow.createCell(colNum);
                dataRegimenTributario.setCellStyle(dataStyle2);
                dataRegimenTributario.setCellValue(clie.getRegimenTributario() != null ? clie.getRegimenTributario() : "");
                colNum++;
                
                Cell dataPeriodo = dataRow.createCell(colNum);
                dataPeriodo.setCellStyle(dataStyle2);
                dataPeriodo.setCellValue((lc.getAnio() != null && lc.getMes() != null) ? lc.getAnio() + "-" + lc.getMes() : "");
                colNum++;

                Cell dataVencimientoDJ = dataRow.createCell(colNum);
                dataVencimientoDJ.setCellStyle(dataStyle2);
                dataVencimientoDJ.setCellValue(lc.getfVencimiento() != null ? lc.getfVencimiento() : "");
                colNum++;

                Cell dataMovimiento = dataRow.createCell(colNum);
                dataMovimiento.setCellValue(lc.getMovimiento() != null ? lc.getMovimiento() : "");
                dataMovimiento.setCellStyle(dataStyle2);
                colNum++;
                
                Cell dataComprasFilas = dataRow.createCell(colNum);
                dataComprasFilas.setCellValue(lc.getComprasFilas() != null ? lc.getComprasFilas() : 0);
                dataComprasFilas.setCellStyle(dataStyle2);
                colNum++;
                
                String valVentasConfirmacion = "";
                Cell dataVentasConfirmacion = dataRow.createCell(colNum);
                dataVentasConfirmacion.setCellStyle(dataStyle2);
                int conVenta = lv.getConfirmacion() != null ? lv.getConfirmacion() : 0;
                switch (conVenta) {
                    case 0:
                        dataVentasConfirmacion.setCellStyle(dataRedStyle);
                        valVentasConfirmacion = "X";
                        break;
                    case 1:
                        dataVentasConfirmacion.setCellStyle(dataGreenStyle);
                        valVentasConfirmacion = "✓";
                        break;
                }
                dataVentasConfirmacion.setCellValue(valVentasConfirmacion);
                colNum++;               

                String valValidacionSunat = "";
                Cell dataValidacionSunat = dataRow.createCell(colNum);
                dataValidacionSunat.setCellStyle(dataStyle2);
                int valSunat = lc.getValidacionSunat() != null ? lc.getValidacionSunat() : 0;
                switch (valSunat) {
                    case 0:
                        dataValidacionSunat.setCellStyle(dataRedStyle);
                        valValidacionSunat = "X";
                        contMasivaSunatNo++;
                        break;
                    case 1:
                        dataValidacionSunat.setCellStyle(dataGreenStyle);
                        valValidacionSunat = "✓";
                        contMasivaSunatSi++;
                        break;
                    case 2:
                        dataValidacionSunat.setCellStyle(dataStyle2Neg);
                        valValidacionSunat = "N/A";
                        contMasivaSunatNa++;
                        break;
                }
                dataValidacionSunat.setCellValue(valValidacionSunat);
                colNum++;
                
                String valValidacion = "";
                Cell dataValidacion = dataRow.createCell(colNum);
                dataValidacion.setCellStyle(dataStyle2);
                int valVal = lc.getValidacion() != null ? lc.getValidacion() : 0;
                switch (valVal) {
                    case 0:
                        dataValidacion.setCellStyle(dataRedStyle);
                        valValidacion = "X";
                        contValidacionNo++;
                        break;
                    case 1:
                        dataValidacion.setCellStyle(dataGreenStyle);
                        valValidacion = "✓";
                        contValidacionSi++;
                        break;
                    case 2:
                        dataValidacion.setCellStyle(dataStyle2Neg);
                        valValidacion = "N/A";
                        contValidacionNa++;
                        break;
                    case 3:
                        dataValidacion.setCellStyle(dataOrangeStyle);
                        valValidacion = "VAL";
                        contValidacionVal++;
                        break;
                }
                dataValidacion.setCellValue(valValidacion);
                colNum++;

                Cell dataValidacionUsuario = dataRow.createCell(colNum);
                dataValidacionUsuario.setCellStyle(dataStyleLeft2);
                // Derivar nombre corto en Java para evitar NPEs
                String nombreCortoUsuario = "";
                if (lc.getValidacionUsuario() != null && !lc.getValidacionUsuario().trim().isEmpty()) {
                    try {
                        String x = lc.getValidacionUsuario().trim().replaceAll("\\s+", " ");
                        String[] partsX = x.split(" ");
                        if (partsX.length >= 3) {
                            nombreCortoUsuario = partsX[2].substring(0, 1) + partsX[0];
                        } else {
                            nombreCortoUsuario = partsX[0];
                        }
                    } catch (Exception ex) {
                        nombreCortoUsuario = lc.getValidacionUsuario();
                    }
                }
                dataValidacionUsuario.setCellValue(nombreCortoUsuario);
                colNum++;

                String valConfirmacion = "";
                Cell dataConfirmacion = dataRow.createCell(colNum);
                dataConfirmacion.setCellStyle(dataStyle2);
                int conCompra = lc.getConfirmacion() != null ? lc.getConfirmacion() : 0;
                switch (conCompra) {
                    case 0:
                        dataConfirmacion.setCellStyle(dataRedStyle);
                        valConfirmacion = "X";
                        contConfirmacionNo++;
                        break;
                    case 1:
                        dataConfirmacion.setCellStyle(dataGreenStyle);
                        valConfirmacion = "✓";
                        contConfirmacionSi++;
                        break;
                }
                dataConfirmacion.setCellValue(valConfirmacion);
                colNum++;

                Cell dataObservacion = dataRow.createCell(colNum);
                dataObservacion.setCellStyle(dataStyleLeft2);
                dataObservacion.setCellValue(lc.getObservacion() != null ? lc.getObservacion() : "");
                colNum++;

                i++;
                rowNum++;
            }
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="DATA RESUMEN">
            cellOtrValidacion.setCellValue(contValidacionVal);

            cellTerminadoMasivaSunat.setCellValue(contMasivaSunatSi);
            cellTerminadoValidacion.setCellValue(contValidacionSi);
            cellTerminadoConfirmacion.setCellValue(contConfirmacionSi);
            
            cellPendienteMasivaSunat.setCellValue(contMasivaSunatNo);
            cellPendienteValidacion.setCellValue(contValidacionNo);
            cellPendienteConfirmacion.setCellValue(contConfirmacionNo);
            
            cellNoAplicaMasivaSunat.setCellValue(contMasivaSunatNa);
            cellNoAplicaValidacion.setCellValue(contValidacionNa);
            
            cellTotalMasivaSunat.setCellValue(contMasivaSunatSi + contMasivaSunatNo + contMasivaSunatNa);
            cellTotalValidacion.setCellValue(contValidacionVal + contValidacionSi + contValidacionNo + contValidacionNa);
            cellTotalConfirmacion.setCellValue(contConfirmacionSi + contConfirmacionNo);
            // </editor-fold>

            int finFilt = rowNum - 1;

            sheet.setAutoFilter(new CellRangeAddress(inicioFilt, finFilt, 0, 16));
            sheet.createFreezePane(0, 11);

            for (int contCol = 0; contCol < colNum; contCol++) {
                sheet.autoSizeColumn(contCol);
            }

            wb.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Error generating LoginCompras Excel: ", e);
            throw new RuntimeException(e);
        }
    }

    public byte[] exportarExcelDiario(String proceso, String fecha, String anio, String mes) {
        List<Cliente> list = loginCompraRepository.listExcelDiario(proceso, fecha, anio, mes);

        int acumulado = loginCompraRepository.countAcumulado(proceso, fecha, anio, mes);
        int realizado = 0;
        switch (proceso) {
            case "validacion":
                realizado = (int) (list.isEmpty() ? 0 : list.stream().filter(e -> e.getLoginCompra() != null && (e.getLoginCompra().getValidacion() == 1 || e.getLoginCompra().getValidacion() == 2)).count());
                break;
            case "confirmacion":
                realizado = (int) (list.isEmpty() ? 0 : list.stream().filter(e -> e.getLoginCompra() != null && e.getLoginCompra().getConfirmacion() == 1).count());
                break;
        }
        int total = loginCompraRepository.countTotal(anio, mes);
        int contadorDia = list.isEmpty() ? 0 : list.size();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Workbook wb = new XSSFWorkbook();
            Sheet sheet = wb.createSheet("Reporte");

            DefaultIndexedColorMap colorMap = new DefaultIndexedColorMap();

            // <editor-fold defaultstate="collapsed" desc="ESTILOS">
            XSSFColor GERENCIA_BLUE = new XSSFColor(new java.awt.Color(0, 51, 204), colorMap);
            XSSFColor GERENCIA_GREY = new XSSFColor(new java.awt.Color(214, 220, 228), colorMap);
            XSSFColor MATTE_BLACK = new XSSFColor(new java.awt.Color(43, 43, 43), colorMap);

            XSSFColor RED = new XSSFColor(new java.awt.Color(255, 0, 0), colorMap);
            XSSFColor SUPERLIGHT_RED = new XSSFColor(new java.awt.Color(253, 237, 236), colorMap);

            XSSFColor LIGHT_BLUE = new XSSFColor(new java.awt.Color(51, 153, 255), colorMap);
            XSSFColor LIGHT_SKYBLUE = new XSSFColor(new java.awt.Color(235, 245, 251), colorMap);

            XSSFColor GREEN = new XSSFColor(new java.awt.Color(0, 204, 0), colorMap);
            XSSFColor YELLOW = new XSSFColor(new java.awt.Color(255, 255, 0), colorMap);
            XSSFColor ORANGE = new XSSFColor(new java.awt.Color(255, 102, 0), colorMap);

            XSSFColor LIGHT_GREY = new XSSFColor(new java.awt.Color(240, 240, 240), colorMap);
            XSSFColor GREY = new XSSFColor(new java.awt.Color(153, 153, 153), colorMap);

            XSSFColor BLACK = new XSSFColor(new java.awt.Color(0, 0, 0), colorMap);
            XSSFColor WHITE = new XSSFColor(new java.awt.Color(255, 255, 255), colorMap);
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="FONTS">
            Boolean negrita = true;
            XSSFFont cabeceraFont = PoiUtils.fuente((XSSFWorkbook) wb, WHITE, 17, negrita);
            XSSFFont subHeaderFont = PoiUtils.fuente((XSSFWorkbook) wb, WHITE, 9, negrita);
            XSSFFont dataSimpleFont = PoiUtils.fuente((XSSFWorkbook) wb, MATTE_BLACK, 9, negrita);
            XSSFFont dataGreenFont = PoiUtils.fuente((XSSFWorkbook) wb, GREEN, 9, negrita);
            XSSFFont dataRedFont = PoiUtils.fuente((XSSFWorkbook) wb, RED, 9, negrita);
            XSSFFont dataGreyFont = PoiUtils.fuente((XSSFWorkbook) wb, GREY, 9, negrita);
            XSSFFont dataYellowFont = PoiUtils.fuente((XSSFWorkbook) wb, YELLOW, 9, negrita);
            XSSFFont dataOrangeFont = PoiUtils.fuente((XSSFWorkbook) wb, ORANGE, 9, negrita);
            XSSFFont dataLightBlueFont = PoiUtils.fuente((XSSFWorkbook) wb, LIGHT_BLUE, 9, negrita);
            XSSFFont dataStarFont = PoiUtils.fuente((XSSFWorkbook) wb, MATTE_BLACK, 9, negrita);
            dataStarFont.setFontName("Wingdings");
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="CELL STYLES">
            CellStyle cabeceraStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, GERENCIA_BLUE, cabeceraFont);
            CellStyle subHeaderStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, MATTE_BLACK, subHeaderFont);
            PoiUtils.addBorders(subHeaderStyle, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, GERENCIA_GREY, dataSimpleFont);
            PoiUtils.addBorders(dataStyle, BorderStyle.THIN, IndexedColors.WHITE);
            CellStyle dataLeftStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.LEFT, GERENCIA_GREY, dataSimpleFont);
            PoiUtils.addBorders(dataLeftStyle, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataStyle2 = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, LIGHT_GREY, dataSimpleFont);
            PoiUtils.addBorders(dataStyle2, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataGreenStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, LIGHT_GREY, dataGreenFont);
            PoiUtils.addBorders(dataGreenStyle, BorderStyle.THIN, IndexedColors.WHITE);
            CellStyle dataRedStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, LIGHT_GREY, dataRedFont);
            PoiUtils.addBorders(dataRedStyle, BorderStyle.THIN, IndexedColors.WHITE);
            CellStyle dataGreyStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, LIGHT_GREY, dataGreyFont);
            PoiUtils.addBorders(dataGreyStyle, BorderStyle.THIN, IndexedColors.WHITE);
            CellStyle dataYellowStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, LIGHT_GREY, dataYellowFont);
            PoiUtils.addBorders(dataYellowStyle, BorderStyle.THIN, IndexedColors.WHITE);
            CellStyle dataLightBlueStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, LIGHT_GREY, dataLightBlueFont);
            PoiUtils.addBorders(dataLightBlueStyle, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataSimpleRedStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, WHITE, dataRedFont);
            PoiUtils.addBorders(dataSimpleRedStyle, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle subCabeceraStyleBlue = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, GERENCIA_BLUE, subHeaderFont);
            PoiUtils.addBorders(subCabeceraStyleBlue, BorderStyle.THIN, IndexedColors.WHITE);
            CellStyle subCabeceraStyleRed = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, RED, subHeaderFont);
            PoiUtils.addBorders(subCabeceraStyleRed, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataAcumulado = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, WHITE, dataSimpleFont);
            PoiUtils.addBorders(dataAcumulado, BorderStyle.THIN, IndexedColors.WHITE);
            CellStyle dataRealizado = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, LIGHT_SKYBLUE, dataSimpleFont);
            PoiUtils.addBorders(dataRealizado, BorderStyle.THIN, IndexedColors.WHITE);
            CellStyle dataFaltantes = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, SUPERLIGHT_RED, dataSimpleFont);
            PoiUtils.addBorders(dataFaltantes, BorderStyle.THIN, IndexedColors.WHITE);
            // </editor-fold>

            int rowNum = 0;

            // <editor-fold defaultstate="collapsed" desc="RESUMEN">
            Row res0 = sheet.createRow(rowNum);
            int colNumRes = 6;

            Cell resumenTit = res0.createCell(colNumRes);
            resumenTit.setCellValue(" R E S U M E N ");
            resumenTit.setCellStyle(cabeceraStyle);
            CellRangeAddress regionRes = CellRangeAddress.valueOf("G1:J1");
            sheet.addMergedRegion(regionRes);
            PoiUtils.addBorders(regionRes, BorderStyle.THIN, IndexedColors.WHITE, sheet);

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
            double acumuladoPercent = total > 0 ? ((double) acumulado * 100) / total : 0;
            resAcumuladoPercent.setCellValue(Math.round(acumuladoPercent) + " %");
            resAcumuladoPercent.setCellStyle(dataAcumulado);
            colNumRes++;

            Cell resRealizadoPercent = res3.createCell(colNumRes);
            double realizadoPercent = total > 0 ? ((double) realizado * 100) / total : 0;
            resRealizadoPercent.setCellValue(Math.round(realizadoPercent) + " %");
            resRealizadoPercent.setCellStyle(dataRealizado);
            colNumRes++;

            Cell resFaltantePercent = res3.createCell(colNumRes);
            double faltantePercent = total > 0 ? ((double) faltantes * 100) / total : 0;
            resFaltantePercent.setCellValue(Math.round(faltantePercent) + " %");
            resFaltantePercent.setCellStyle(dataFaltantes);
            colNumRes++;

            Cell resTotalPercent = res3.createCell(colNumRes);
            double totalPercent = total > 0 ? ((double) total * 100) / total : 0;
            resTotalPercent.setCellValue(Math.round(totalPercent) + " %");
            resTotalPercent.setCellStyle(dataStyle2);
            colNumRes++;
            
            rowNum++;
            rowNum++;
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="CABECERA">
            Row cabecera = sheet.createRow(rowNum);
            cabecera.setHeightInPoints(sheet.getDefaultRowHeightInPoints() * 3);

            Cell cellCabecera = cabecera.createCell(0);
            cellCabecera.setCellStyle(cabeceraStyle);
            cellCabecera.setCellValue("REPORTE DIARIO - LOGIN COMPRAS");
            sheet.addMergedRegion(CellRangeAddress.valueOf("A6:L6"));

            rowNum++;
            int colNum = 0;

            Row title = sheet.createRow(rowNum);

            Cell cellTitle1 = title.createCell(colNum);
            cellTitle1.setCellStyle(subHeaderStyle);
            cellTitle1.setCellValue("CONTRIBUYENTE");
            CellRangeAddress region = CellRangeAddress.valueOf("A7:I7");
            sheet.addMergedRegion(region);
            PoiUtils.addBorders(region, BorderStyle.THIN, IndexedColors.WHITE, sheet);
            colNum = colNum + 9;

            Cell cellTitle2 = title.createCell(colNum);
            cellTitle2.setCellStyle(subHeaderStyle);
            cellTitle2.setCellValue("PROCESO");
            region = CellRangeAddress.valueOf("J7:K7");
            sheet.addMergedRegion(region);
            PoiUtils.addBorders(region, BorderStyle.THIN, IndexedColors.WHITE, sheet);
            colNum = colNum + 2;

            Cell cellEncargado = title.createCell(colNum);
            cellEncargado.setCellStyle(subHeaderStyle);
            cellEncargado.setCellValue("REGISTRADO POR");
            sheet.addMergedRegion(CellRangeAddress.valueOf("L7:L8"));
            PoiUtils.addBorders(CellRangeAddress.valueOf("L7:L8"), BorderStyle.THIN, IndexedColors.WHITE, sheet);

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
            int i = 1;

            // <editor-fold defaultstate="collapsed" desc=" L L E N A D O   D E   L A   D A T A ">
            for (Cliente clie : list) {
                LoginCompra lc = clie.getLoginCompra() != null ? clie.getLoginCompra() : new LoginCompra();
                
                colNum = 0;

                Row dataRow = sheet.createRow(rowNum);

                Cell yData = dataRow.createCell(colNum);
                yData.setCellValue(clie.getY() != null ? clie.getY() : "");
                yData.setCellStyle(dataStyle);
                colNum++;

                Cell rucData = dataRow.createCell(colNum);
                rucData.setCellValue(clie.getRuc() != null ? clie.getRuc() : "");
                rucData.setCellStyle(dataStyle);
                colNum++;

                Cell prioriData = dataRow.createCell(colNum);
                if (lc.getPrioridad() != null) {
                    switch (lc.getPrioridad()) {
                        case "ALTA - PRE":
                            prioriData.setCellValue("☆ - PRE");
                            break;
                        case "ALTA":
                            prioriData.setCellValue("☆");
                            break;
                        case "PRE":
                            prioriData.setCellValue("PRE");
                            break;
                        default:
                            prioriData.setCellValue("");
                            break;
                    }
                } else {
                    prioriData.setCellValue("");
                }
                prioriData.setCellStyle(dataStyle);
                colNum++;

                Cell rsData = dataRow.createCell(colNum);
                rsData.setCellValue(clie.getRazonSocial() != null ? clie.getRazonSocial() : "");
                rsData.setCellStyle(dataLeftStyle);
                colNum++;

                Cell estadoData = dataRow.createCell(colNum);
                estadoData.setCellStyle(dataStyle2);
                if (clie.getEstado() != null && clie.getEstado().getDescripcion() != null) {
                    estadoData.setCellValue(clie.getEstado().getDescripcion().toUpperCase());
                    switch (clie.getEstado().getId()) {
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
                } else {
                    estadoData.setCellValue("");
                }
                colNum++;

                Cell tipoServicioData = dataRow.createCell(colNum);
                tipoServicioData.setCellValue(clie.getServicio() != null ? clie.getServicio().getAbreviatura() : "");
                tipoServicioData.setCellStyle(dataStyle2);
                colNum++;

                Cell pleData = dataRow.createCell(colNum);
                pleData.setCellValue(lc.getPle() != null ? lc.getPle() : "");
                pleData.setCellStyle(dataStyle2);
                colNum++;

                Cell rtData = dataRow.createCell(colNum);
                rtData.setCellValue(clie.getRegimenTributario() != null ? clie.getRegimenTributario() : "");
                rtData.setCellStyle(dataStyle2);
                colNum++;

                Cell movData = dataRow.createCell(colNum);
                movData.setCellValue(lc.getMovimiento() != null ? lc.getMovimiento() : "");
                movData.setCellStyle(dataStyle2);
                colNum++;

                // PROCESO DATA
                Cell procesoData = dataRow.createCell(colNum);

                int procesoRegistrosValor = 0;
                int procesoValidacionValor = 0;
                int procesoConfirmacionValor = 0;
                String procesoFecha = "";
                String procesoRegistradoPor = "";
                switch (proceso) {
                    case "validacion":
                        procesoValidacionValor = lc.getValidacion() != null ? lc.getValidacion() : 0;
                        procesoFecha = lc.getValidacionFecha();
                        procesoRegistradoPor = lc.getValidacionUsuario();
                        break;
                    case "confirmacion":
                        procesoConfirmacionValor = lc.getConfirmacion() != null ? lc.getConfirmacion() : 0;
                        procesoFecha = lc.getConfirmacionFecha();
                        procesoRegistradoPor = lc.getConfirmacionUsuario();
                        break;
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
                            procesoData.setCellStyle(dataStyle2);
                            break;
                        case 3:
                            procesoData.setCellValue("VAL");
                            procesoData.setCellStyle(dataLightBlueStyle);
                            break;
                    }
                } else if (proceso.equals("confirmacion")) {
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

                Cell procesoFechaData = dataRow.createCell(colNum);
                procesoFechaData.setCellValue(procesoFecha != null ? procesoFecha : "");
                procesoFechaData.setCellStyle(dataStyle2);
                colNum++;

                Cell procesoUsuarioData = dataRow.createCell(colNum);
                procesoUsuarioData.setCellValue(procesoRegistradoPor != null ? procesoRegistradoPor : "");
                procesoUsuarioData.setCellStyle(dataLeftStyle);
                colNum++;

                i++;
                rowNum++;
            }
            // </editor-fold>

            int finFilt = rowNum - 1;
            sheet.setAutoFilter(new CellRangeAddress(inicioFilt - 1, finFilt, 0, 11));

            for (int contCol = 0; contCol < colNum; contCol++) {
                sheet.autoSizeColumn(contCol);
            }

            wb.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Error generating LoginCompras Excel Diario: ", e);
            throw new RuntimeException(e);
        }
    }
}
