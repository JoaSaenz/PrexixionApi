package com.joa.prexixionapi.modules.loginProcesos.services;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joa.prexixionapi.utils.DateUtils;
import com.joa.prexixionapi.utils.PoiUtils;
import com.joa.prexixionapi.utils.StringUtils;

import lombok.extern.slf4j.Slf4j;
import com.joa.prexixionapi.modules.loginProcesos.dto.LoginProcesosRequest;
import com.joa.prexixionapi.modules.loginProcesos.dto.LoginProcesosListDTO;
import com.joa.prexixionapi.modules.loginProcesos.dto.LoginProcesosAvanceDTO;
import com.joa.prexixionapi.modules.loginProcesos.services.LoginProcesosExcelService;
import com.joa.prexixionapi.modules.loginProcesos.repositories.LoginProcesosRepository;

@Service
@Slf4j
public class LoginProcesosExcelService {

    @Autowired
    private LoginProcesosRepository loginProcesosRepository;

    public byte[] excel(String anio, String mes, String estados, String grupos) {
        LoginProcesosRequest req = new LoginProcesosRequest();
        req.setEstadoFiltro(StringUtils.parseCsvInt(estados));
        req.setGrupoFiltro(StringUtils.parseCsv(grupos));
        return excel(req, anio, mes);
    }

    public byte[] excel(LoginProcesosRequest req) {
        String p = req.getPeriodoI() != null && !req.getPeriodoI().isEmpty() ? req.getPeriodoI() : req.getPeriodoF();
        String anio = (p != null && p.length() >= 4) ? p.substring(0, 4) : "";
        String mes = (p != null && p.length() >= 7) ? p.substring(5, 7) : "";
        return excel(req, anio, mes);
    }

    public byte[] excel(LoginProcesosRequest req, String anio, String mes) {
        List<LoginProcesosListDTO> list = loginProcesosRepository.list(anio, mes, req);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Workbook wb = new XSSFWorkbook();
            Sheet sheet = wb.createSheet("Reporte");

            int contConfirmacionVentasSi = 0;
            int contConfirmacionVentasNo = 0;
            int contConfirmacionComprasSi = 0;
            int contConfirmacionComprasNo = 0;
            int contPreSi = 0;
            int contPreNo = 0;
            int contPreNa = 0;
            int contConfirmacionSi = 0;
            int contConfirmacionNo = 0;
            int contConfirmacionNa = 0;
            int contPdtSi = 0;
            int contPdtNo = 0;
            int contPdtNa = 0;
            int contPleSi = 0;
            int contPleNo = 0;
            int contPleNa = 0;
            int contSireSi = 0;
            int contSireNo = 0;
            int contSireNa = 0;

            // Colores
            XSSFColor GERENCIA_BLUE = new XSSFColor(new byte[] { (byte) 0, (byte) 51, (byte) 204 }, null);
            XSSFColor GERENCIA_GREY = new XSSFColor(new byte[] { (byte) 214, (byte) 220, (byte) 228 }, null);
            XSSFColor MATTE_BLACK = new XSSFColor(new byte[] { (byte) 43, (byte) 43, (byte) 43 }, null);
            XSSFColor RED = new XSSFColor(new byte[] { (byte) 255, (byte) 0, (byte) 0 }, null);
            XSSFColor GREEN = new XSSFColor(new byte[] { (byte) 0, (byte) 204, (byte) 0 }, null);
            XSSFColor YELLOW = new XSSFColor(new byte[] { (byte) 255, (byte) 255, (byte) 0 }, null);
            XSSFColor LIGHT_BLUE = new XSSFColor(new byte[] { (byte) 51, (byte) 153, (byte) 255 }, null);
            XSSFColor ORANGE = new XSSFColor(new byte[] { (byte) 255, (byte) 102, (byte) 0 }, null);
            XSSFColor GREY = new XSSFColor(new byte[] { (byte) 153, (byte) 153, (byte) 153 }, null);
            XSSFColor LIGHT_GREY = new XSSFColor(new byte[] { (byte) 240, (byte) 240, (byte) 240 }, null);
            XSSFColor WHITE = new XSSFColor(new byte[] { (byte) 255, (byte) 255, (byte) 255 }, null);

            // Fuentes
            XSSFFont cabeceraFont = PoiUtils.fuente((XSSFWorkbook) wb, WHITE, 17, true);
            XSSFFont subHeaderFont = PoiUtils.fuente((XSSFWorkbook) wb, WHITE, 9, true);
            XSSFFont dataSimpleFont = PoiUtils.fuente((XSSFWorkbook) wb, MATTE_BLACK, 9, true);
            XSSFFont dataGreenFont = PoiUtils.fuente((XSSFWorkbook) wb, GREEN, 9, true);
            XSSFFont dataRedFont = PoiUtils.fuente((XSSFWorkbook) wb, RED, 9, true);
            XSSFFont dataGreyFont = PoiUtils.fuente((XSSFWorkbook) wb, GREY, 9, true);
            XSSFFont dataYellowFont = PoiUtils.fuente((XSSFWorkbook) wb, YELLOW, 9, true);
            XSSFFont dataLightBlueFont = PoiUtils.fuente((XSSFWorkbook) wb, LIGHT_BLUE, 9, true);

            // Estilos
            CellStyle cabeceraStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER,
                    GERENCIA_BLUE, cabeceraFont);
            CellStyle subHeaderStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER,
                    MATTE_BLACK, subHeaderFont);
            PoiUtils.addBorders(subHeaderStyle, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataSimpleStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.LEFT, WHITE,
                    dataSimpleFont);
            PoiUtils.addBorders(dataSimpleStyle, BorderStyle.THIN, IndexedColors.WHITE);
            dataSimpleStyle.setRightBorderColor(GREY.getIndex());

            CellStyle dataStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, GERENCIA_GREY,
                    dataSimpleFont);
            PoiUtils.addBorders(dataStyle, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataLeftStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.LEFT,
                    GERENCIA_GREY, dataSimpleFont);
            PoiUtils.addBorders(dataLeftStyle, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataStyle2 = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, LIGHT_GREY,
                    dataSimpleFont);
            PoiUtils.addBorders(dataStyle2, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataGreenStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER,
                    LIGHT_GREY, dataGreenFont);
            PoiUtils.addBorders(dataGreenStyle, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataRedStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, LIGHT_GREY,
                    dataRedFont);
            PoiUtils.addBorders(dataRedStyle, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataGreyStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER,
                    LIGHT_GREY, dataGreyFont);
            PoiUtils.addBorders(dataGreyStyle, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataYellowStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER,
                    LIGHT_GREY, dataYellowFont);
            PoiUtils.addBorders(dataYellowStyle, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataLightBlueStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER,
                    LIGHT_GREY, dataLightBlueFont);
            PoiUtils.addBorders(dataLightBlueStyle, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataGreenStyleNeg = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER,
                    GERENCIA_GREY, dataGreenFont);
            PoiUtils.addBorders(dataGreenStyleNeg, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataRedStyleNeg = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER,
                    GERENCIA_GREY, dataRedFont);
            PoiUtils.addBorders(dataRedStyleNeg, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataStyleNeg = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER,
                    GERENCIA_GREY, dataSimpleFont);
            PoiUtils.addBorders(dataStyleNeg, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataOrangeStyleNeg = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER,
                    LIGHT_GREY, PoiUtils.fuente((XSSFWorkbook) wb, ORANGE, 9, true));
            PoiUtils.addBorders(dataOrangeStyleNeg, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataSimpleRedStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER,
                    WHITE, dataRedFont);
            PoiUtils.addBorders(dataSimpleRedStyle, BorderStyle.THIN, IndexedColors.WHITE);

            int rowNum = 0;
            int colNum = 0;

            // CABECERA RESUMEN
            Row cabeceraResumen = sheet.createRow(rowNum);
            colNum = CellReference.convertColStringToIndex("I");

            Cell cellHeadResumen = cabeceraResumen.createCell(colNum++);
            cellHeadResumen.setCellStyle(subHeaderStyle);
            cellHeadResumen.setCellValue("RESUMEN");

            Cell cellHeadResumenConfirmacionVentas = cabeceraResumen.createCell(colNum++);
            cellHeadResumenConfirmacionVentas.setCellStyle(subHeaderStyle);
            cellHeadResumenConfirmacionVentas.setCellValue("V-CON");

            Cell cellHeadResumenConfirmacionCompras = cabeceraResumen.createCell(colNum++);
            cellHeadResumenConfirmacionCompras.setCellStyle(subHeaderStyle);
            cellHeadResumenConfirmacionCompras.setCellValue("C-CON");

            Cell cellHeadResumenPreLiquidacion = cabeceraResumen.createCell(colNum++);
            cellHeadResumenPreLiquidacion.setCellStyle(subHeaderStyle);
            cellHeadResumenPreLiquidacion.setCellValue("PRE");

            Cell cellHeadResumenConfirmacion = cabeceraResumen.createCell(colNum++);
            cellHeadResumenConfirmacion.setCellStyle(subHeaderStyle);
            cellHeadResumenConfirmacion.setCellValue("CONF");

            Cell cellHeadResumenPDT = cabeceraResumen.createCell(colNum++);
            cellHeadResumenPDT.setCellStyle(subHeaderStyle);
            cellHeadResumenPDT.setCellValue("PDT");

            Cell cellHeadResumenPLE = cabeceraResumen.createCell(colNum++);
            cellHeadResumenPLE.setCellStyle(subHeaderStyle);
            cellHeadResumenPLE.setCellValue("PLE");

            Cell cellHeadResumenSIRE = cabeceraResumen.createCell(colNum++);
            cellHeadResumenSIRE.setCellStyle(subHeaderStyle);
            cellHeadResumenSIRE.setCellValue("SIRE");
            rowNum++;

            Row rowTerminado = sheet.createRow(rowNum);
            colNum = CellReference.convertColStringToIndex("I");

            Cell cellTerminado = rowTerminado.createCell(colNum++);
            cellTerminado.setCellStyle(subHeaderStyle);
            cellTerminado.setCellValue("TERMINADO");

            Cell cellTerminadoResumenConfirmacionVentas = rowTerminado.createCell(colNum++);
            cellTerminadoResumenConfirmacionVentas.setCellStyle(dataGreenStyle);

            Cell cellTerminadoResumenConfirmacionCompras = rowTerminado.createCell(colNum++);
            cellTerminadoResumenConfirmacionCompras.setCellStyle(dataGreenStyle);

            Cell cellTerminadoResumenPreLiquidacion = rowTerminado.createCell(colNum++);
            cellTerminadoResumenPreLiquidacion.setCellStyle(dataGreenStyle);

            Cell cellTerminadoResumenConfirmacion = rowTerminado.createCell(colNum++);
            cellTerminadoResumenConfirmacion.setCellStyle(dataGreenStyle);

            Cell cellTerminadoResumenPDT = rowTerminado.createCell(colNum++);
            cellTerminadoResumenPDT.setCellStyle(dataGreenStyle);

            Cell cellTerminadoResumenPLE = rowTerminado.createCell(colNum++);
            cellTerminadoResumenPLE.setCellStyle(dataGreenStyle);

            Cell cellTerminadoResumenSIRE = rowTerminado.createCell(colNum++);
            cellTerminadoResumenSIRE.setCellStyle(dataGreenStyle);
            rowNum++;

            Row rowPendiente = sheet.createRow(rowNum);
            colNum = CellReference.convertColStringToIndex("I");

            Cell cellPendiente = rowPendiente.createCell(colNum++);
            cellPendiente.setCellStyle(subHeaderStyle);
            cellPendiente.setCellValue("PENDIENTE");

            Cell cellPendienteResumenConfirmacionVentas = rowPendiente.createCell(colNum++);
            cellPendienteResumenConfirmacionVentas.setCellStyle(dataSimpleRedStyle);

            Cell cellPendienteResumenConfirmacionCompras = rowPendiente.createCell(colNum++);
            cellPendienteResumenConfirmacionCompras.setCellStyle(dataSimpleRedStyle);

            Cell cellPendienteResumenPreLiquidacion = rowPendiente.createCell(colNum++);
            cellPendienteResumenPreLiquidacion.setCellStyle(dataSimpleRedStyle);

            Cell cellPendienteResumenConfirmacion = rowPendiente.createCell(colNum++);
            cellPendienteResumenConfirmacion.setCellStyle(dataSimpleRedStyle);

            Cell cellPendienteResumenPDT = rowPendiente.createCell(colNum++);
            cellPendienteResumenPDT.setCellStyle(dataSimpleRedStyle);

            Cell cellPendienteResumenPLE = rowPendiente.createCell(colNum++);
            cellPendienteResumenPLE.setCellStyle(dataSimpleRedStyle);

            Cell cellPendienteResumenSIRE = rowPendiente.createCell(colNum++);
            cellPendienteResumenSIRE.setCellStyle(dataSimpleRedStyle);
            rowNum++;

            Row rowNoAplica = sheet.createRow(rowNum);
            colNum = CellReference.convertColStringToIndex("I");

            Cell cellNoAplica = rowNoAplica.createCell(colNum++);
            cellNoAplica.setCellStyle(subHeaderStyle);
            cellNoAplica.setCellValue("NO APLICA");

            Cell cellNoAplicaResumenConfirmacionVentas = rowNoAplica.createCell(colNum++);
            cellNoAplicaResumenConfirmacionVentas.setCellStyle(dataStyle2);

            Cell cellNoAplicaResumenConfirmacionCompras = rowNoAplica.createCell(colNum++);
            cellNoAplicaResumenConfirmacionCompras.setCellStyle(dataStyle2);

            Cell cellNoAplicaResumenPreLiquidacion = rowNoAplica.createCell(colNum++);
            cellNoAplicaResumenPreLiquidacion.setCellStyle(dataStyle2);

            Cell cellNoAplicaResumenConfirmacion = rowNoAplica.createCell(colNum++);
            cellNoAplicaResumenConfirmacion.setCellStyle(dataStyle2);

            Cell cellNoAplicaResumenPDT = rowNoAplica.createCell(colNum++);
            cellNoAplicaResumenPDT.setCellStyle(dataStyle2);

            Cell cellNoAplicaResumenPLE = rowNoAplica.createCell(colNum++);
            cellNoAplicaResumenPLE.setCellStyle(dataStyle2);

            Cell cellNoAplicaResumenSIRE = rowNoAplica.createCell(colNum++);
            cellNoAplicaResumenSIRE.setCellStyle(dataStyle2);
            rowNum++;

            Row rowTotal = sheet.createRow(rowNum);
            colNum = CellReference.convertColStringToIndex("I");

            Cell cellTotal = rowTotal.createCell(colNum++);
            cellTotal.setCellStyle(subHeaderStyle);
            cellTotal.setCellValue("TOTAL");

            Cell cellTotalResumenConfirmacionVentas = rowTotal.createCell(colNum++);
            cellTotalResumenConfirmacionVentas.setCellStyle(dataStyle);

            Cell cellTotalResumenConfirmacionCompras = rowTotal.createCell(colNum++);
            cellTotalResumenConfirmacionCompras.setCellStyle(dataStyle);

            Cell cellTotalResumenPreLiquidacion = rowTotal.createCell(colNum++);
            cellTotalResumenPreLiquidacion.setCellStyle(dataStyle);

            Cell cellTotalResumenConfirmacion = rowTotal.createCell(colNum++);
            cellTotalResumenConfirmacion.setCellStyle(dataStyle);

            Cell cellTotalResumenPDT = rowTotal.createCell(colNum++);
            cellTotalResumenPDT.setCellStyle(dataStyle);

            Cell cellTotalResumenPLE = rowTotal.createCell(colNum++);
            cellTotalResumenPLE.setCellStyle(dataStyle);

            Cell cellTotalResumenSIRE = rowTotal.createCell(colNum++);
            cellTotalResumenSIRE.setCellStyle(dataStyle);
            rowNum++;
            rowNum++;

            colNum = 0;

            // CABECERA TABLA PRINCIPAL
            Row cabecera = sheet.createRow(rowNum);
            cabecera.setHeightInPoints(sheet.getDefaultRowHeightInPoints() * 3);

            Cell cellCabecera = cabecera.createCell(colNum);
            cellCabecera.setCellStyle(cabeceraStyle);
            cellCabecera.setCellValue("M A E S T R O   -  L O G I N    P R O C E S O S");
            sheet.addMergedRegion(CellRangeAddress.valueOf("A7:Q7"));
            rowNum++;

            Row title = sheet.createRow(rowNum);
            colNum = 0;

            Cell cellTitle1 = title.createCell(colNum);
            cellTitle1.setCellStyle(subHeaderStyle);
            cellTitle1.setCellValue("C  O  N  T  R  I  B  U  Y  E  N  T  E");
            CellRangeAddress region = CellRangeAddress.valueOf("A8:I8");
            sheet.addMergedRegion(region);
            PoiUtils.addBorders(region, BorderStyle.THIN, IndexedColors.WHITE, sheet);
            colNum = 9;

            Cell cellTitle2 = title.createCell(colNum);
            cellTitle2.setCellStyle(subHeaderStyle);
            cellTitle2.setCellValue("P  R  O  C  E  S  O");
            region = CellRangeAddress.valueOf("J8:Q8");
            sheet.addMergedRegion(region);
            PoiUtils.addBorders(region, BorderStyle.THIN, IndexedColors.WHITE, sheet);
            rowNum++;

            Row subHeader = sheet.createRow(rowNum);
            colNum = 0;
            String[] headers = {
                    "N°", "ESTADO", "CAT", "STO", "GRUPO E.", "Y", "SIGNER", "RT", "MOV",
                    "V-CON", "C-CON", "PRE", "CONF", "PDT", "PLE CV", "SIRE CV",
                    "OBSERVACION"
            };
            for (String h : headers) {
                Cell c = subHeader.createCell(colNum++);
                c.setCellStyle(subHeaderStyle);
                c.setCellValue(h);
            }

            rowNum++;
            int inicioFilt = rowNum;
            rowNum++;

            int i = 1;

            // ORDENAR ARRAY LIST (orden descendente)
            // list sorted by query

            // Llenado de data
            for (LoginProcesosListDTO obj : list) {
                Row dataRow = sheet.createRow(rowNum);
                colNum = 0;

                Cell nData = dataRow.createCell(colNum++);
                nData.setCellValue(i);
                nData.setCellStyle(dataStyle);

                Cell estadoData = dataRow.createCell(colNum++);
                estadoData.setCellValue(obj.getEstado() != null ? obj.getEstado().toUpperCase() : "");
                CellStyle estStyle = dataStyle2;
                if (obj.getIdEstado() != null) {
                    switch (obj.getIdEstado()) {
                        case 1:
                            estStyle = dataGreenStyle;
                            break;
                        case 2:
                            estStyle = dataRedStyle;
                            break;
                        case 3:
                            estStyle = dataLightBlueStyle;
                            break;
                        case 4:
                            estStyle = dataYellowStyle;
                            break;
                        case 5:
                            estStyle = dataGreyStyle;
                            break;
                    }
                }
                estadoData.setCellStyle(estStyle);

                Cell dataCategoria = dataRow.createCell(colNum++);
                dataCategoria.setCellStyle(dataStyle2);
                dataCategoria.setCellValue(obj.getCategoriaAbreviatura());

                Cell dataCategoriaStore = dataRow.createCell(colNum++);
                int storeVal = obj.getCategoriaStore() != null ? obj.getCategoriaStore() : 0;
                if (storeVal == 1) {
                    dataCategoriaStore.setCellStyle(dataRedStyle);
                    dataCategoriaStore.setCellValue("SI");
                } else {
                    dataCategoriaStore.setCellStyle(dataStyle2);
                    dataCategoriaStore.setCellValue("");
                }

                Cell dataGrupoE = dataRow.createCell(colNum++);
                dataGrupoE
                        .setCellValue(obj.getGrupoEconomico() != null ? obj.getGrupoEconomico() : "");
                dataGrupoE.setCellStyle(dataStyle2);

                Cell yData = dataRow.createCell(colNum++);
                yData.setCellValue(obj.getY());
                yData.setCellStyle(dataStyle);

                Cell rsData = dataRow.createCell(colNum++);
                rsData.setCellValue(obj.getNombreCorto());
                rsData.setCellStyle(dataLeftStyle);

                Cell rtData = dataRow.createCell(colNum++);
                rtData.setCellValue(obj.getAbrGestionRegimenTributario());
                rtData.setCellStyle(dataStyle2);

                Cell movData = dataRow.createCell(colNum++);
                String movVal = (obj.getMovimiento() != null && !obj.getMovimiento().trim().isEmpty())
                        ? obj.getMovimiento()
                        : "-";
                movData.setCellValue(movVal);
                movData.setCellStyle(dataStyle2);

                // V-CON DATA
                Cell confirmacionVentasData = dataRow.createCell(colNum++);
                int cv = obj.getConfirmacionVentas() != null ? obj.getConfirmacionVentas() : 0;
                if (cv == 1) {
                    confirmacionVentasData.setCellValue("✓");
                    confirmacionVentasData.setCellStyle(dataGreenStyle);
                    contConfirmacionVentasSi++;
                } else {
                    confirmacionVentasData.setCellValue("X");
                    confirmacionVentasData.setCellStyle(dataSimpleRedStyle);
                    contConfirmacionVentasNo++;
                }

                // C-CON DATA
                Cell confirmacionComprasData = dataRow.createCell(colNum++);
                int cc = obj.getConfirmacionCompras() != null ? obj.getConfirmacionCompras() : 0;
                if (cc == 1) {
                    confirmacionComprasData.setCellValue("✓");
                    confirmacionComprasData.setCellStyle(dataGreenStyle);
                    contConfirmacionComprasSi++;
                } else {
                    confirmacionComprasData.setCellValue("X");
                    confirmacionComprasData.setCellStyle(dataSimpleRedStyle);
                    contConfirmacionComprasNo++;
                }

                // PRE-LIQUIDACIÓN DATA
                Cell preLiquidacionData = dataRow.createCell(colNum++);
                int pre = obj.getPreLiquidacion() != null ? obj.getPreLiquidacion() : 0;
                if (pre == 1) {
                    preLiquidacionData.setCellValue("✓");
                    preLiquidacionData.setCellStyle(dataGreenStyle);
                    contPreSi++;
                } else if (pre == 2) {
                    preLiquidacionData.setCellValue("-");
                    preLiquidacionData.setCellStyle(dataStyle2);
                    contPreNa++;
                } else {
                    preLiquidacionData.setCellValue("X");
                    preLiquidacionData.setCellStyle(dataSimpleRedStyle);
                    contPreNo++;
                }

                // CONFIRMACIÓN DATA
                Cell confirmacionData = dataRow.createCell(colNum++);
                int conf = obj.getConfirmacion() != null ? obj.getConfirmacion() : 0;
                if (conf == 1) {
                    confirmacionData.setCellValue("✓");
                    confirmacionData.setCellStyle(dataGreenStyle);
                    contConfirmacionSi++;
                } else if (conf == 2) {
                    confirmacionData.setCellValue("-");
                    confirmacionData.setCellStyle(dataStyle2);
                    contConfirmacionNa++;
                } else {
                    confirmacionData.setCellValue("X");
                    confirmacionData.setCellStyle(dataSimpleRedStyle);
                    contConfirmacionNo++;
                }

                // PDT DATA
                Cell pdtData = dataRow.createCell(colNum++);
                boolean hasPdt = false;
                if (obj.getPdtRegistrado() != null && obj.getPdtRegistrado() == 1) {
                    hasPdt = true;
                }
                if (hasPdt) {
                    pdtData.setCellValue("✓");
                    pdtData.setCellStyle(dataGreenStyleNeg);
                    contPdtSi++;
                } else {
                    pdtData.setCellValue("X");
                    pdtData.setCellStyle(dataRedStyleNeg);
                    contPdtNo++;
                }

                // PLE CV DATA
                Cell pleCVData = dataRow.createCell(colNum++);
                int pleVal = obj.getPleCV() != null ? obj.getPleCV() : 0;
                if (pleVal == 2) {
                    pleCVData.setCellValue("-");
                    pleCVData.setCellStyle(dataStyle);
                    contPleNa++;
                } else {
                    boolean hasPdtReg = (obj.getPdtRegistrado() != null && obj.getPdtRegistrado() == 1);
                    if (hasPdtReg) {
                        pleCVData.setCellValue("✓");
                        pleCVData.setCellStyle(dataGreenStyleNeg);
                        contPleSi++;
                    } else {
                        pleCVData.setCellValue("X");
                        pleCVData.setCellStyle(dataRedStyleNeg);
                        contPleNo++;
                    }
                }

                // SIRE CV DATA
                Cell sireCVData = dataRow.createCell(colNum++);
                int sireVal = obj.getSireCV() != null ? obj.getSireCV() : 0;
                if (sireVal == 1) {
                    sireCVData.setCellValue("✓");
                    sireCVData.setCellStyle(dataGreenStyleNeg);
                    contSireSi++;
                } else if (sireVal == 2) {
                    sireCVData.setCellValue("-");
                    sireCVData.setCellStyle(dataStyle);
                    contSireNa++;
                } else {
                    sireCVData.setCellValue("X");
                    sireCVData.setCellStyle(dataRedStyleNeg);
                    contSireNo++;
                }

                Cell obsData = dataRow.createCell(colNum++);
                obsData.setCellValue(obj.getObservacion());
                obsData.setCellStyle(dataSimpleStyle);

                i++;
                rowNum++;
            }

            // Actualizar resumen
            cellTerminadoResumenConfirmacionVentas.setCellValue(contConfirmacionVentasSi);
            cellTerminadoResumenConfirmacionCompras.setCellValue(contConfirmacionComprasSi);
            cellTerminadoResumenPreLiquidacion.setCellValue(contPreSi);
            cellTerminadoResumenConfirmacion.setCellValue(contConfirmacionSi);
            cellTerminadoResumenPDT.setCellValue(contPdtSi);
            cellTerminadoResumenPLE.setCellValue(contPleSi);
            cellTerminadoResumenSIRE.setCellValue(contSireSi);

            cellPendienteResumenConfirmacionVentas.setCellValue(contConfirmacionVentasNo);
            cellPendienteResumenConfirmacionCompras.setCellValue(contConfirmacionComprasNo);
            cellPendienteResumenPreLiquidacion.setCellValue(contPreNo);
            cellPendienteResumenConfirmacion.setCellValue(contConfirmacionNo);
            cellPendienteResumenPDT.setCellValue(contPdtNo);
            cellPendienteResumenPLE.setCellValue(contPleNo);
            cellPendienteResumenSIRE.setCellValue(contSireNo);

            cellNoAplicaResumenConfirmacionVentas.setCellValue(0);
            cellNoAplicaResumenConfirmacionCompras.setCellValue(0);
            cellNoAplicaResumenPreLiquidacion.setCellValue(contPreNa);
            cellNoAplicaResumenConfirmacion.setCellValue(contConfirmacionNa);
            cellNoAplicaResumenPDT.setCellValue(contPdtNa);
            cellNoAplicaResumenPLE.setCellValue(contPleNa);
            cellNoAplicaResumenSIRE.setCellValue(contSireNa);

            cellTotalResumenConfirmacionVentas.setCellValue(contConfirmacionVentasSi + contConfirmacionVentasNo);
            cellTotalResumenConfirmacionCompras.setCellValue(contConfirmacionComprasSi + contConfirmacionComprasNo);
            cellTotalResumenPreLiquidacion.setCellValue(contPreSi + contPreNo + contPreNa);
            cellTotalResumenConfirmacion.setCellValue(contConfirmacionSi + contConfirmacionNo + contConfirmacionNa);
            cellTotalResumenPDT.setCellValue(contPdtSi + contPdtNo + contPdtNa);
            cellTotalResumenPLE.setCellValue(contPleSi + contPleNo + contPleNa);
            cellTotalResumenSIRE.setCellValue(contSireSi + contSireNo + contSireNa);

            int finFilt = rowNum - 1;
            sheet.setAutoFilter(new CellRangeAddress(inicioFilt, finFilt, 0, 16));
            sheet.createFreezePane(0, 10);

            for (int contCol = 0; contCol < colNum; contCol++) {
                sheet.autoSizeColumn(contCol);
            }

            wb.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Error generating monthly processes Excel", e);
            return new byte[0];
        }
    }

    public byte[] excelDiario(LoginProcesosRequest req) {
        String proceso = req.getProceso();
        String fechaF = req.getFechaF();
        String fechaI = req.getFechaI() != null && !req.getFechaI().isEmpty() ? req.getFechaI() : fechaF;

        String periodo = req.getPeriodoI();
        String anio = "";
        String mes = "";
        if (periodo != null && periodo.length() >= 7) {
            anio = periodo.substring(0, 4);
            mes = periodo.substring(5, 7);
        }
        return excelDiario(proceso, fechaI, fechaF, anio, mes);
    }

    public byte[] excelDiario(String proceso, String fechaI, String fechaF, String anio, String mes) {
        if (fechaI == null || fechaI.isEmpty()) {
            fechaI = fechaF;
        }
        if (fechaF == null || fechaF.isEmpty()) {
            fechaF = DateUtils.getToday();
            fechaI = fechaF;
        }
        List<LoginProcesosListDTO> list = loginProcesosRepository.listExcelDiario(proceso, fechaI, fechaF, anio, mes);
        int acumulado = loginProcesosRepository.countAcumulado(proceso, fechaF, anio, mes);
        int total = loginProcesosRepository.countTotal(anio, mes);
        int realizado = 0;

        for (LoginProcesosListDTO lp : list) {
            switch (proceso) {
                case "validerCompras":
                    if (lp.getValidacionCompras() != null && lp.getValidacionCompras() == 1)
                        realizado++;
                    break;
                case "validerVentas":
                    if (lp.getValidacionVentas() != null && lp.getValidacionVentas() == 1)
                        realizado++;
                    break;
                case "preLiquidacion":
                    if (lp.getPreLiquidacion() != null && (lp.getPreLiquidacion() == 1 || lp.getPreLiquidacion() == 2))
                        realizado++;
                    break;
                case "confirmacion":
                    if (lp.getConfirmacion() != null && (lp.getConfirmacion() == 1 || lp.getConfirmacion() == 2))
                        realizado++;
                    break;
            }
        }
        int contadorDia = list.size();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Workbook wb = new XSSFWorkbook();
            Sheet sheet = wb.createSheet("Reporte");

            // Colores
            XSSFColor GERENCIA_BLUE = new XSSFColor(new byte[] { (byte) 0, (byte) 51, (byte) 204 }, null);
            XSSFColor GERENCIA_GREY = new XSSFColor(new byte[] { (byte) 214, (byte) 220, (byte) 228 }, null);
            XSSFColor MATTE_BLACK = new XSSFColor(new byte[] { (byte) 43, (byte) 43, (byte) 43 }, null);
            XSSFColor RED = new XSSFColor(new byte[] { (byte) 255, (byte) 0, (byte) 0 }, null);
            XSSFColor GREEN = new XSSFColor(new byte[] { (byte) 0, (byte) 204, (byte) 0 }, null);
            XSSFColor YELLOW = new XSSFColor(new byte[] { (byte) 255, (byte) 255, (byte) 0 }, null);
            XSSFColor LIGHT_BLUE = new XSSFColor(new byte[] { (byte) 51, (byte) 153, (byte) 255 }, null);
            XSSFColor GREY = new XSSFColor(new byte[] { (byte) 153, (byte) 153, (byte) 153 }, null);
            XSSFColor LIGHT_GREY = new XSSFColor(new byte[] { (byte) 240, (byte) 240, (byte) 240 }, null);
            XSSFColor WHITE = new XSSFColor(new byte[] { (byte) 255, (byte) 255, (byte) 255 }, null);
            XSSFColor ORANGE = new XSSFColor(new byte[] { (byte) 255, (byte) 102, (byte) 0 }, null);
            XSSFColor SUPERLIGHT_RED = new XSSFColor(new byte[] { (byte) 253, (byte) 237, (byte) 236 }, null);
            XSSFColor LIGHT_SKYBLUE = new XSSFColor(new byte[] { (byte) 235, (byte) 245, (byte) 251 }, null);

            // Fonts
            XSSFFont cabeceraFont = PoiUtils.fuente((XSSFWorkbook) wb, WHITE, 17, true);
            XSSFFont subHeaderFont = PoiUtils.fuente((XSSFWorkbook) wb, WHITE, 9, true);
            XSSFFont dataSimpleFont = PoiUtils.fuente((XSSFWorkbook) wb, MATTE_BLACK, 9, true);
            XSSFFont dataGreenFont = PoiUtils.fuente((XSSFWorkbook) wb, GREEN, 9, true);
            XSSFFont dataRedFont = PoiUtils.fuente((XSSFWorkbook) wb, RED, 9, true);
            XSSFFont dataGreyFont = PoiUtils.fuente((XSSFWorkbook) wb, GREY, 9, true);
            XSSFFont dataYellowFont = PoiUtils.fuente((XSSFWorkbook) wb, YELLOW, 9, true);
            XSSFFont dataLightBlueFont = PoiUtils.fuente((XSSFWorkbook) wb, LIGHT_BLUE, 9, true);

            // Styles
            CellStyle cabeceraStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER,
                    GERENCIA_BLUE, cabeceraFont);
            CellStyle subHeaderStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER,
                    MATTE_BLACK, subHeaderFont);
            PoiUtils.addBorders(subHeaderStyle, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataSimpleStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.LEFT, WHITE,
                    dataSimpleFont);
            PoiUtils.addBorders(dataSimpleStyle, BorderStyle.THIN, IndexedColors.WHITE);
            dataSimpleStyle.setRightBorderColor(GREY.getIndex());

            CellStyle dataStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, GERENCIA_GREY,
                    dataSimpleFont);
            PoiUtils.addBorders(dataStyle, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataLeftStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.LEFT,
                    GERENCIA_GREY, dataSimpleFont);
            PoiUtils.addBorders(dataLeftStyle, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataStyle2 = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, LIGHT_GREY,
                    dataSimpleFont);
            PoiUtils.addBorders(dataStyle2, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataGreenStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER,
                    LIGHT_GREY, dataGreenFont);
            PoiUtils.addBorders(dataGreenStyle, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataRedStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, LIGHT_GREY,
                    dataRedFont);
            PoiUtils.addBorders(dataRedStyle, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataGreyStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER,
                    LIGHT_GREY, dataGreyFont);
            PoiUtils.addBorders(dataGreyStyle, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataYellowStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER,
                    LIGHT_GREY, dataYellowFont);
            PoiUtils.addBorders(dataYellowStyle, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataLightBlueStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER,
                    LIGHT_GREY, dataLightBlueFont);
            PoiUtils.addBorders(dataLightBlueStyle, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataGreenStyleNeg = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER,
                    GERENCIA_GREY, dataGreenFont);
            PoiUtils.addBorders(dataGreenStyleNeg, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataRedStyleNeg = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER,
                    GERENCIA_GREY, dataRedFont);
            PoiUtils.addBorders(dataRedStyleNeg, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataStyleNeg = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER,
                    GERENCIA_GREY, dataSimpleFont);
            PoiUtils.addBorders(dataStyleNeg, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataOrangeStyleNeg = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER,
                    LIGHT_GREY, PoiUtils.fuente((XSSFWorkbook) wb, ORANGE, 9, true));
            PoiUtils.addBorders(dataOrangeStyleNeg, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataSimpleRedStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER,
                    WHITE, dataRedFont);
            PoiUtils.addBorders(dataSimpleRedStyle, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle subCabeceraStyleBlue = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER,
                    GERENCIA_BLUE, subHeaderFont);
            PoiUtils.addBorders(subCabeceraStyleBlue, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle subCabeceraStyleRed = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, RED,
                    subHeaderFont);
            PoiUtils.addBorders(subCabeceraStyleRed, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataAcumulado = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, WHITE,
                    dataSimpleFont);
            PoiUtils.addBorders(dataAcumulado, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataRealizado = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER,
                    LIGHT_SKYBLUE, dataSimpleFont);
            PoiUtils.addBorders(dataRealizado, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataFaltantes = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER,
                    SUPERLIGHT_RED, dataSimpleFont);
            PoiUtils.addBorders(dataFaltantes, BorderStyle.THIN, IndexedColors.WHITE);

            int rowNum = 0;

            // RESUMEN
            Row res0 = sheet.createRow(rowNum);
            int colNumRes = 5;

            Cell resumenTit = res0.createCell(colNumRes);
            resumenTit.setCellValue(" R E S U M E N ");
            resumenTit.setCellStyle(cabeceraStyle);
            CellRangeAddress regionRes = CellRangeAddress.valueOf("F1:I1");
            sheet.addMergedRegion(regionRes);
            PoiUtils.addBorders(regionRes, BorderStyle.THIN, IndexedColors.WHITE, sheet);
            rowNum++;

            colNumRes = 5;
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
            colNumRes = 5;

            Row res2 = sheet.createRow(rowNum);

            Cell resAcum = res2.createCell(colNumRes);
            resAcum.setCellValue(acumulado);
            resAcum.setCellStyle(dataAcumulado);
            colNumRes++;

            Cell resReal = res2.createCell(colNumRes);
            resReal.setCellValue(realizado);
            resReal.setCellStyle(dataRealizado);
            colNumRes++;

            Cell resFalt = res2.createCell(colNumRes);
            int faltantes = total - acumulado - realizado;
            resFalt.setCellValue(faltantes);
            resFalt.setCellStyle(dataFaltantes);
            colNumRes++;

            Cell resTot = res2.createCell(colNumRes);
            resTot.setCellValue(total);
            resTot.setCellStyle(dataStyle2);
            colNumRes++;

            Cell resCont = res2.createCell(colNumRes);
            resCont.setCellValue(contadorDia);
            resCont.setCellStyle(dataStyle2);
            colNumRes++;

            rowNum++;
            colNumRes = 5;

            Row res3 = sheet.createRow(rowNum);
            Cell resAcumPct = res3.createCell(colNumRes);
            double acPct = total > 0 ? ((double) acumulado * 100.0) / total : 0;
            resAcumPct.setCellValue(Math.round(acPct) + " %");
            resAcumPct.setCellStyle(dataAcumulado);
            colNumRes++;

            Cell resRealPct = res3.createCell(colNumRes);
            double rePct = total > 0 ? ((double) realizado * 100.0) / total : 0;
            resRealPct.setCellValue(Math.round(rePct) + " %");
            resRealPct.setCellStyle(dataRealizado);
            colNumRes++;

            Cell resFaltPct = res3.createCell(colNumRes);
            double faPct = total > 0 ? ((double) faltantes * 100.0) / total : 0;
            resFaltPct.setCellValue(Math.round(faPct) + " %");
            resFaltPct.setCellStyle(dataFaltantes);
            colNumRes++;

            Cell resTotPct = res3.createCell(colNumRes);
            resTotPct.setCellValue("100 %");
            resTotPct.setCellStyle(dataStyle2);
            colNumRes++;

            rowNum++;
            rowNum++;

            // CABECERA
            Row cabeceraRow = sheet.createRow(rowNum);
            cabeceraRow.setHeightInPoints(sheet.getDefaultRowHeightInPoints() * 3);
            Cell cCab = cabeceraRow.createCell(0);
            cCab.setCellStyle(cabeceraStyle);
            cCab.setCellValue("M A E S T R O   -  L O G I N    P R O C E S O S");
            sheet.addMergedRegion(CellRangeAddress.valueOf("A" + (rowNum + 1) + ":K" + (rowNum + 1)));

            rowNum++;
            int colNum = 0;

            Row titleRow = sheet.createRow(rowNum);

            Cell cellTitle1 = titleRow.createCell(colNum);
            cellTitle1.setCellStyle(subHeaderStyle);
            cellTitle1.setCellValue("C  O  N  T  R  I  B  U  Y  E  N  T  E");
            CellRangeAddress region = CellRangeAddress.valueOf("A" + (rowNum + 1) + ":H" + (rowNum + 1));
            sheet.addMergedRegion(region);
            PoiUtils.addBorders(region, BorderStyle.THIN, IndexedColors.WHITE, sheet);
            colNum = colNum + 8;

            Cell cellTitle2 = titleRow.createCell(colNum);
            cellTitle2.setCellStyle(subHeaderStyle);
            cellTitle2.setCellValue("P  R  O  C  E  S  O");
            region = CellRangeAddress.valueOf("I" + (rowNum + 1) + ":J" + (rowNum + 1));
            sheet.addMergedRegion(region);
            PoiUtils.addBorders(region, BorderStyle.THIN, IndexedColors.WHITE, sheet);
            colNum = colNum + 2;

            Cell cellEncargado = titleRow.createCell(colNum);
            cellEncargado.setCellStyle(subHeaderStyle);
            cellEncargado.setCellValue("REGISTRADO POR");
            sheet.addMergedRegion(CellRangeAddress.valueOf("K" + (rowNum + 1) + ":K" + (rowNum + 2)));
            PoiUtils.addBorders(CellRangeAddress.valueOf("K" + (rowNum + 1) + ":K" + (rowNum + 2)), BorderStyle.THIN,
                    IndexedColors.WHITE, sheet);

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

            Cell cellHeaderSigner = subHeader.createCell(colNum);
            cellHeaderSigner.setCellStyle(subHeaderStyle);
            cellHeaderSigner.setCellValue("SIGNER");
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

            Cell cellHeaderMovimiento = subHeader.createCell(colNum);
            cellHeaderMovimiento.setCellStyle(subHeaderStyle);
            cellHeaderMovimiento.setCellValue("MOV");
            colNum++;

            Cell cellHeaderProceso = subHeader.createCell(colNum);
            cellHeaderProceso.setCellStyle(subHeaderStyle);
            cellHeaderProceso.setCellValue(proceso.toUpperCase());
            colNum++;

            Cell cellHeaderProcesoFecha = subHeader.createCell(colNum);
            cellHeaderProcesoFecha.setCellStyle(subHeaderStyle);
            cellHeaderProcesoFecha.setCellValue("FECHA");
            colNum++;

            rowNum++;
            int inicioFilt = rowNum;
            rowNum++;

            // Data
            for (LoginProcesosListDTO obj : list) {
                Row data = sheet.createRow(rowNum);
                colNum = 0;

                Cell yData = data.createCell(colNum);
                yData.setCellValue(obj.getY());
                yData.setCellStyle(dataStyle);
                colNum++;

                Cell rucData = data.createCell(colNum);
                rucData.setCellValue(obj.getRuc());
                rucData.setCellStyle(dataStyle);
                colNum++;

                Cell prioriData = data.createCell(colNum);
                String priVal = "";
                if (obj.getPrioridad() != null) {
                    switch (obj.getPrioridad()) {
                        case "ALTA - PRE":
                            priVal = "☆ - PRE";
                            break;
                        case "ALTA":
                            priVal = "☆";
                            break;
                        case "PRE":
                            priVal = "PRE";
                            break;
                    }
                }
                prioriData.setCellValue(priVal);
                prioriData.setCellStyle(dataStyle);
                colNum++;

                Cell rsData = data.createCell(colNum);
                rsData.setCellValue(obj.getNombreCorto());
                rsData.setCellStyle(dataLeftStyle);
                colNum++;

                Cell estadoData = data.createCell(colNum);
                estadoData.setCellValue(obj.getEstado() != null ? obj.getEstado().toUpperCase() : "");
                CellStyle estSt = dataStyle2;
                if (obj.getIdEstado() != null) {
                    switch (obj.getIdEstado()) {
                        case 1:
                            estSt = dataGreenStyle;
                            break;
                        case 2:
                            estSt = dataRedStyle;
                            break;
                        case 3:
                            estSt = dataLightBlueStyle;
                            break;
                        case 4:
                            estSt = dataYellowStyle;
                            break;
                        case 5:
                            estSt = dataGreyStyle;
                            break;
                    }
                }
                estadoData.setCellStyle(estSt);
                colNum++;

                Cell tipoServicioData = data.createCell(colNum);
                tipoServicioData.setCellValue(obj.getTipoServicio());
                tipoServicioData.setCellStyle(dataStyle2);
                colNum++;

                Cell pleData = data.createCell(colNum);
                pleData.setCellValue(obj.getPle());
                pleData.setCellStyle(dataStyle2);
                colNum++;

                Cell movData = data.createCell(colNum);
                movData.setCellValue(obj.getMovimiento());
                movData.setCellStyle(dataStyle2);
                colNum++;

                Cell procesoData = data.createCell(colNum);
                String fch = "";
                String user = "";
                int pVal = 0;
                int pValiderComprasVal = 0;
                switch (proceso) {
                    case "validerCompras":
                        pValiderComprasVal = obj.getValidacionCompras() != null ? obj.getValidacionCompras() : 0;
                        fch = obj.getValidacionFechaCompras();
                        user = obj.getValidacionUsuarioCompras();
                        break;
                    case "validerVentas":
                        pVal = obj.getValidacionVentas() != null ? obj.getValidacionVentas() : 0;
                        fch = obj.getValidacionFechaVentas();
                        user = obj.getValidacionUsuarioVentas();
                        break;
                    case "preLiquidacion":
                        pVal = obj.getPreLiquidacion() != null ? obj.getPreLiquidacion() : 0;
                        fch = obj.getPreLiquidacionFecha();
                        user = obj.getPreLiquidacionUsuario();
                        break;
                    case "confirmacion":
                        pVal = obj.getConfirmacion() != null ? obj.getConfirmacion() : 0;
                        fch = obj.getConfirmacionFecha();
                        user = obj.getConfirmacionUsuario();
                        break;
                }

                if (proceso.equals("validerCompras")) {
                    switch (pValiderComprasVal) {
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
                } else {
                    switch (pVal) {
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
                colNum++;

                Cell procesoFecha = data.createCell(colNum);
                procesoFecha.setCellValue(fch);
                procesoFecha.setCellStyle(dataStyle2);
                colNum++;

                Cell dataEncargado = data.createCell(colNum);
                dataEncargado.setCellValue(user);
                dataEncargado.setCellStyle(dataStyle2);
                colNum++;
                rowNum++;
            }

            sheet.createFreezePane(0, 9);

            int finFilt = rowNum - 1;
            sheet.setAutoFilter(new CellRangeAddress(inicioFilt, finFilt, 0, 10));

            // Col widths
            for (int contCol = 0; contCol < colNum; contCol++) {
                sheet.autoSizeColumn(contCol);
            }

            wb.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Error generating daily process Excel", e);
            return new byte[0];
        }
    }

    public byte[] excelRange(String pI, String pF, String estados, String grupos) {
        LoginProcesosRequest req = new LoginProcesosRequest();
        req.setPeriodoI(pI);
        req.setPeriodoF(pF);
        req.setEstadoFiltro(StringUtils.parseCsvInt(estados));
        req.setGrupoFiltro(StringUtils.parseCsv(grupos));
        return excelRange(req);
    }

    public byte[] excelRange(LoginProcesosRequest req) {
        String pI = req.getPeriodoI();
        String pF = req.getPeriodoF();

        String anioI = pI.substring(0, 4);
        String mesI = pI.substring(5, 7);

        String anioF = pF.substring(0, 4);
        String mesF = pF.substring(5, 7);

        ArrayList<LoginProcesosListDTO> list = new ArrayList<>();
        Map<String, List<LoginProcesosListDTO>> dataMap = new HashMap<>();
        List<String> periodos = new ArrayList<>();

        while (Integer.parseInt(anioI + "" + mesI) <= Integer.parseInt(anioF + "" + mesF)) {
            String pKey = anioI + "" + mesI;
            periodos.add(pKey);
            LoginProcesosRequest subReq = new LoginProcesosRequest();
            subReq.setEstadoFiltro(req.getEstadoFiltro());
            subReq.setGrupoFiltro(req.getGrupoFiltro());
            subReq.setCategoriaFiltro(req.getCategoriaFiltro());
            subReq.setCategoriaStoreFiltro(req.getCategoriaStoreFiltro());
            subReq.setGrupoEconomicoFiltro(req.getGrupoEconomicoFiltro());
            List<LoginProcesosListDTO> subList = loginProcesosRepository.list(anioI, mesI, subReq);
            dataMap.put(pKey, subList);

            for (LoginProcesosListDTO e : subList) {
                boolean found = false;
                for (LoginProcesosListDTO existing : list) {
                    if (existing.getRuc().equals(e.getRuc())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    list.add(e);
                }
            }

            if (mesI.equals("12")) {
                anioI = String.valueOf(Integer.parseInt(anioI) + 1);
                mesI = "01";
            } else {
                int nextMes = Integer.parseInt(mesI) + 1;
                mesI = nextMes < 10 ? "0" + nextMes : String.valueOf(nextMes);
            }
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Workbook wb = new XSSFWorkbook();
            Sheet sheet = wb.createSheet("Reporte");

            // Colores
            XSSFColor GERENCIA_BLUE = new XSSFColor(new byte[] { (byte) 0, (byte) 51, (byte) 204 }, null);
            XSSFColor GERENCIA_GREY = new XSSFColor(new byte[] { (byte) 214, (byte) 220, (byte) 228 }, null);
            XSSFColor MATTE_BLACK = new XSSFColor(new byte[] { (byte) 43, (byte) 43, (byte) 43 }, null);
            XSSFColor RED = new XSSFColor(new byte[] { (byte) 255, (byte) 0, (byte) 0 }, null);
            XSSFColor GREEN = new XSSFColor(new byte[] { (byte) 0, (byte) 204, (byte) 0 }, null);
            XSSFColor YELLOW = new XSSFColor(new byte[] { (byte) 255, (byte) 255, (byte) 0 }, null);
            XSSFColor LIGHT_BLUE = new XSSFColor(new byte[] { (byte) 51, (byte) 153, (byte) 255 }, null);
            XSSFColor GREY = new XSSFColor(new byte[] { (byte) 153, (byte) 153, (byte) 153 }, null);
            XSSFColor LIGHT_GREY = new XSSFColor(new byte[] { (byte) 240, (byte) 240, (byte) 240 }, null);
            XSSFColor WHITE = new XSSFColor(new byte[] { (byte) 255, (byte) 255, (byte) 255 }, null);

            // Fonts
            XSSFFont periodosFont = PoiUtils.fuente((XSSFWorkbook) wb, WHITE, 9, true);
            XSSFFont subHeaderFont = PoiUtils.fuente((XSSFWorkbook) wb, WHITE, 9, true);
            XSSFFont dataSimpleFont = PoiUtils.fuente((XSSFWorkbook) wb, MATTE_BLACK, 9, true);
            XSSFFont dataGreenFont = PoiUtils.fuente((XSSFWorkbook) wb, GREEN, 9, true);
            XSSFFont dataRedFont = PoiUtils.fuente((XSSFWorkbook) wb, RED, 9, true);
            XSSFFont dataGreyFont = PoiUtils.fuente((XSSFWorkbook) wb, GREY, 9, true);
            XSSFFont dataYellowFont = PoiUtils.fuente((XSSFWorkbook) wb, YELLOW, 9, true);
            XSSFFont dataLightBlueFont = PoiUtils.fuente((XSSFWorkbook) wb, LIGHT_BLUE, 9, true);

            // Styles
            CellStyle periodosStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER,
                    GERENCIA_BLUE, periodosFont);
            CellStyle subHeaderStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER,
                    MATTE_BLACK, subHeaderFont);
            PoiUtils.addBorders(subHeaderStyle, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataSimpleStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.LEFT, WHITE,
                    dataSimpleFont);
            PoiUtils.addBorders(dataSimpleStyle, BorderStyle.THIN, IndexedColors.WHITE);
            dataSimpleStyle.setRightBorderColor(GREY.getIndex());

            CellStyle dataStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, GERENCIA_GREY,
                    dataSimpleFont);
            PoiUtils.addBorders(dataStyle, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataLeftStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.LEFT,
                    GERENCIA_GREY, dataSimpleFont);
            PoiUtils.addBorders(dataLeftStyle, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataStyle2 = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, LIGHT_GREY,
                    dataSimpleFont);
            PoiUtils.addBorders(dataStyle2, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataGreenStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER,
                    LIGHT_GREY, dataGreenFont);
            PoiUtils.addBorders(dataGreenStyle, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataRedStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER, LIGHT_GREY,
                    dataRedFont);
            PoiUtils.addBorders(dataRedStyle, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataGreyStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER,
                    LIGHT_GREY, dataGreyFont);
            PoiUtils.addBorders(dataGreyStyle, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataYellowStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER,
                    LIGHT_GREY, dataYellowFont);
            PoiUtils.addBorders(dataYellowStyle, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataLightBlueStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER,
                    LIGHT_GREY, dataLightBlueFont);
            PoiUtils.addBorders(dataLightBlueStyle, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataSimpleRedStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER,
                    WHITE, dataRedFont);
            PoiUtils.addBorders(dataSimpleRedStyle, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataRedStyleNeg = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER,
                    GERENCIA_GREY, dataRedFont);
            PoiUtils.addBorders(dataRedStyleNeg, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataGreenStyleNeg = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER,
                    GERENCIA_GREY, dataGreenFont);
            PoiUtils.addBorders(dataGreenStyleNeg, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataStyleNeg = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER,
                    GERENCIA_GREY, dataSimpleFont);
            PoiUtils.addBorders(dataStyleNeg, BorderStyle.THIN, IndexedColors.WHITE);

            // Resumen rows
            int rowNum = 1;
            Row rowTerminado = sheet.createRow(rowNum);
            int colNum = CellReference.convertColStringToIndex("D");
            Cell cellCheck = rowTerminado.createCell(colNum++);
            cellCheck.setCellStyle(dataGreenStyle);
            cellCheck.setCellValue("✓");
            Cell cellTerminado = rowTerminado.createCell(colNum);
            cellTerminado.setCellStyle(subHeaderStyle);
            cellTerminado.setCellValue("TERMINADO");

            rowNum = 2;
            Row rowPendiente = sheet.createRow(rowNum);
            colNum = CellReference.convertColStringToIndex("D");
            Cell cellCross = rowPendiente.createCell(colNum++);
            cellCross.setCellStyle(dataRedStyle);
            cellCross.setCellValue("X");
            Cell cellPendiente = rowPendiente.createCell(colNum);
            cellPendiente.setCellStyle(subHeaderStyle);
            cellPendiente.setCellValue("PENDIENTE");

            rowNum = 3;
            Row rowNoAplica = sheet.createRow(rowNum);
            colNum = CellReference.convertColStringToIndex("D");
            Cell cellMinus = rowNoAplica.createCell(colNum++);
            cellMinus.setCellStyle(dataStyle);
            cellMinus.setCellValue("-");
            Cell cellNoAplica = rowNoAplica.createCell(colNum);
            cellNoAplica.setCellStyle(subHeaderStyle);
            cellNoAplica.setCellValue("NO APLICA");

            rowNum = 0;
            Row rowResumen = sheet.createRow(rowNum);
            colNum = CellReference.convertColStringToIndex("H");

            for (String periodo : periodos) {
                List<LoginProcesosListDTO> datosPeriodo = dataMap.get(periodo);

                // MOV
                int conMov = (int) datosPeriodo.stream()
                        .filter(e -> e.getMovimiento() != null && e.getMovimiento().equals("C/M")).count();
                int sinMov = (int) datosPeriodo.stream()
                        .filter(e -> e.getMovimiento() != null && e.getMovimiento().equals("S/M")).count();
                int naMov = list.size() - (conMov + sinMov);

                Cell cellHeadMov = rowResumen.createCell(colNum);
                cellHeadMov.setCellStyle(subHeaderStyle);
                cellHeadMov.setCellValue("MOV");

                Cell movTerm = rowTerminado.createCell(colNum);
                movTerm.setCellValue(conMov);
                movTerm.setCellStyle(dataStyle2);

                Cell movPend = rowPendiente.createCell(colNum);
                movPend.setCellValue(sinMov);
                movPend.setCellStyle(dataStyle2);

                Cell movNa = rowNoAplica.createCell(colNum);
                movNa.setCellValue(naMov);
                movNa.setCellStyle(dataStyle2);
                colNum++;

                // V-CON
                int conVentas = (int) datosPeriodo.stream()
                        .filter(e -> e.getConfirmacionVentas() != null && e.getConfirmacionVentas() == 1).count();
                int sinVentas = datosPeriodo.size() - conVentas;
                int naVentas = list.size() - datosPeriodo.size();

                Cell cellHeadVCon = rowResumen.createCell(colNum);
                cellHeadVCon.setCellStyle(subHeaderStyle);
                cellHeadVCon.setCellValue("V-CON");

                Cell vconTerm = rowTerminado.createCell(colNum);
                vconTerm.setCellValue(conVentas);
                vconTerm.setCellStyle(dataStyle2);

                Cell vconPend = rowPendiente.createCell(colNum);
                vconPend.setCellValue(sinVentas);
                vconPend.setCellStyle(dataStyle2);

                Cell vconNa = rowNoAplica.createCell(colNum);
                vconNa.setCellValue(naVentas);
                vconNa.setCellStyle(dataStyle2);
                colNum++;

                // C-CON
                int conCompras = (int) datosPeriodo.stream()
                        .filter(e -> e.getConfirmacionCompras() != null && e.getConfirmacionCompras() == 1).count();
                int sinCompras = datosPeriodo.size() - conCompras;
                int naCompras = list.size() - datosPeriodo.size();

                Cell cellHeadCCon = rowResumen.createCell(colNum);
                cellHeadCCon.setCellStyle(subHeaderStyle);
                cellHeadCCon.setCellValue("C-CON");

                Cell cconTerm = rowTerminado.createCell(colNum);
                cconTerm.setCellValue(conCompras);
                cconTerm.setCellStyle(dataStyle2);

                Cell cconPend = rowPendiente.createCell(colNum);
                cconPend.setCellValue(sinCompras);
                cconPend.setCellStyle(dataStyle2);

                Cell cconNa = rowNoAplica.createCell(colNum);
                cconNa.setCellValue(naCompras);
                cconNa.setCellStyle(dataStyle2);
                colNum++;

                // PRE
                int conPre = (int) datosPeriodo.stream()
                        .filter(e -> e.getPreLiquidacion() != null && e.getPreLiquidacion() == 1).count();
                int naPreInPeriod = (int) datosPeriodo.stream()
                        .filter(e -> e.getPreLiquidacion() != null && e.getPreLiquidacion() == 2).count();
                int sinPre = datosPeriodo.size() - (conPre + naPreInPeriod);
                int naPre = (list.size() - datosPeriodo.size()) + naPreInPeriod;

                Cell cellHeadPre = rowResumen.createCell(colNum);
                cellHeadPre.setCellStyle(subHeaderStyle);
                cellHeadPre.setCellValue("PRE");

                Cell preTerm = rowTerminado.createCell(colNum);
                preTerm.setCellValue(conPre);
                preTerm.setCellStyle(dataStyle2);

                Cell prePend = rowPendiente.createCell(colNum);
                prePend.setCellValue(sinPre);
                prePend.setCellStyle(dataStyle2);

                Cell preNa = rowNoAplica.createCell(colNum);
                preNa.setCellValue(naPre);
                preNa.setCellStyle(dataStyle2);
                colNum++;

                // CONF
                int conConf = (int) datosPeriodo.stream()
                        .filter(e -> e.getConfirmacion() != null && e.getConfirmacion() == 1).count();
                int naConfInPeriod = (int) datosPeriodo.stream()
                        .filter(e -> e.getConfirmacion() != null && e.getConfirmacion() == 2).count();
                int sinConf = datosPeriodo.size() - (conConf + naConfInPeriod);
                int naConf = (list.size() - datosPeriodo.size()) + naConfInPeriod;

                Cell cellHeadConf = rowResumen.createCell(colNum);
                cellHeadConf.setCellStyle(subHeaderStyle);
                cellHeadConf.setCellValue("CONF");

                Cell confTerm = rowTerminado.createCell(colNum);
                confTerm.setCellValue(conConf);
                confTerm.setCellStyle(dataStyle2);

                Cell confPend = rowPendiente.createCell(colNum);
                confPend.setCellValue(sinConf);
                confPend.setCellStyle(dataStyle2);

                Cell confNa = rowNoAplica.createCell(colNum);
                confNa.setCellValue(naConf);
                confNa.setCellStyle(dataStyle2);
                colNum++;

                // PDT
                int conPdt = (int) datosPeriodo.stream()
                        .filter(e -> e.getPdtRegistrado() != null && e.getPdtRegistrado() == 1).count();
                int sinPdt = datosPeriodo.size() - conPdt;
                int naPdt = list.size() - datosPeriodo.size();

                Cell cellHeadPdt = rowResumen.createCell(colNum);
                cellHeadPdt.setCellStyle(subHeaderStyle);
                cellHeadPdt.setCellValue("PDT");

                Cell pdtTerm = rowTerminado.createCell(colNum);
                pdtTerm.setCellValue(conPdt);
                pdtTerm.setCellStyle(dataStyle2);

                Cell pdtPend = rowPendiente.createCell(colNum);
                pdtPend.setCellValue(sinPdt);
                pdtPend.setCellStyle(dataStyle2);

                Cell pdtNa = rowNoAplica.createCell(colNum);
                pdtNa.setCellValue(naPdt);
                pdtNa.setCellStyle(dataStyle2);
                colNum++;

                // PLE
                int conPle = 0;
                int sinPle = 0;
                int naPleInPeriod = 0;
                for (LoginProcesosListDTO x : datosPeriodo) {
                    if (x.getPleCV() != null && x.getPleCV() == 2) {
                        naPleInPeriod++;
                    } else {
                        boolean hasPdt = false;
                        if (x.getPdtRegistrado() != null && x.getPdtRegistrado() == 1) {
                            hasPdt = true;
                        }
                        if (hasPdt)
                            conPle++;
                        else
                            sinPle++;
                    }
                }
                int naPle = (list.size() - datosPeriodo.size()) + naPleInPeriod;

                Cell cellHeadPle = rowResumen.createCell(colNum);
                cellHeadPle.setCellStyle(subHeaderStyle);
                cellHeadPle.setCellValue("PLE");

                Cell pleTerm = rowTerminado.createCell(colNum);
                pleTerm.setCellValue(conPle);
                pleTerm.setCellStyle(dataStyle2);

                Cell plePend = rowPendiente.createCell(colNum);
                plePend.setCellValue(sinPle);
                plePend.setCellStyle(dataStyle2);

                Cell pleNa = rowNoAplica.createCell(colNum);
                pleNa.setCellValue(naPle);
                pleNa.setCellStyle(dataStyle2);
                colNum++;

                // SIRE
                int conSire = (int) datosPeriodo.stream()
                        .filter(x -> x.getSireCV() != null && x.getSireCV() == 1).count();
                int naSireInPeriod = (int) datosPeriodo.stream()
                        .filter(x -> x.getSireCV() != null && x.getSireCV() == 2).count();
                int sinSire = datosPeriodo.size() - (conSire + naSireInPeriod);
                int naSire = (list.size() - datosPeriodo.size()) + naSireInPeriod;

                Cell cellHeadSire = rowResumen.createCell(colNum);
                cellHeadSire.setCellStyle(subHeaderStyle);
                cellHeadSire.setCellValue("SIRE");

                Cell sireTerm = rowTerminado.createCell(colNum);
                sireTerm.setCellValue(conSire);
                sireTerm.setCellStyle(dataStyle2);

                Cell sirePend = rowPendiente.createCell(colNum);
                sirePend.setCellValue(sinSire);
                sirePend.setCellStyle(dataStyle2);

                Cell sireNa = rowNoAplica.createCell(colNum);
                sireNa.setCellValue(naSire);
                sireNa.setCellStyle(dataStyle2);
                colNum++;

                colNum++; // Space column between periods
            }

            // Headers row 6 (Periodos) and row 7 (Subheaders)
            rowNum = 6;
            Row rowPeriodos = sheet.createRow(rowNum);
            rowNum = 7;
            Row subHeader = sheet.createRow(rowNum);

            colNum = 0;
            Cell cId = subHeader.createCell(colNum++);
            cId.setCellStyle(subHeaderStyle);
            cId.setCellValue("ID");

            Cell cRuc = subHeader.createCell(colNum++);
            cRuc.setCellStyle(subHeaderStyle);
            cRuc.setCellValue("RUC");

            Cell cSigner = subHeader.createCell(colNum++);
            cSigner.setCellStyle(subHeaderStyle);
            cSigner.setCellValue("SIGNER");

            Cell cEst = subHeader.createCell(colNum++);
            cEst.setCellStyle(subHeaderStyle);
            cEst.setCellValue("ESTADO");

            Cell cRt = subHeader.createCell(colNum++);
            cRt.setCellStyle(subHeaderStyle);
            cRt.setCellValue("RT");
            colNum += 2; // match conversion index

            int colHeadPeriodos = CellReference.convertColStringToIndex("H");
            for (String periodo : periodos) {
                Cell cellPeriodo = rowPeriodos.createCell(colHeadPeriodos);
                cellPeriodo.setCellStyle(periodosStyle);
                cellPeriodo.setCellValue(periodo);

                CellRangeAddress range = new CellRangeAddress(6, 6, colHeadPeriodos, colHeadPeriodos + 7);
                sheet.addMergedRegion(range);

                String[] subHeaderTitles = { "MOV", "V-CON", "C-CON", "PRE", "CONF", "PDT", "PLE", "SIRE" };
                for (int t = 0; t < subHeaderTitles.length; t++) {
                    Cell c = subHeader.createCell(colHeadPeriodos + t);
                    c.setCellStyle(subHeaderStyle);
                    c.setCellValue(subHeaderTitles[t]);
                }
                colHeadPeriodos += 9;
            }

            rowNum = 8;
            int inicioFilt = rowNum + 1;
            rowNum++;

            // Llenado de data
            for (LoginProcesosListDTO obj : list) {
                Row dataRow = sheet.createRow(rowNum);
                colNum = 0;

                Cell yData = dataRow.createCell(colNum++);
                yData.setCellValue(obj.getY());
                yData.setCellStyle(dataStyle);

                Cell rucData = dataRow.createCell(colNum++);
                rucData.setCellValue(obj.getRuc());
                rucData.setCellStyle(dataStyle);

                Cell rsData = dataRow.createCell(colNum++);
                rsData.setCellValue(obj.getNombreCorto());
                rsData.setCellStyle(dataLeftStyle);

                Cell estadoData = dataRow.createCell(colNum++);
                estadoData.setCellValue(obj.getEstado() != null ? obj.getEstado().toUpperCase() : "");
                CellStyle estSt = dataStyle2;
                if (obj.getIdEstado() != null) {
                    switch (obj.getIdEstado()) {
                        case 1:
                            estSt = dataGreenStyle;
                            break;
                        case 2:
                            estSt = dataRedStyle;
                            break;
                        case 3:
                            estSt = dataLightBlueStyle;
                            break;
                        case 4:
                            estSt = dataYellowStyle;
                            break;
                        case 5:
                            estSt = dataGreyStyle;
                            break;
                    }
                }
                estadoData.setCellStyle(estSt);

                Cell rtData = dataRow.createCell(colNum++);
                rtData.setCellValue(obj.getAbrGestionRegimenTributario());
                rtData.setCellStyle(dataStyle2);
                colNum += 2; // Skip helper spacer indices

                for (String periodo : periodos) {
                    List<LoginProcesosListDTO> datosPeriodo = dataMap.get(periodo);
                    LoginProcesosListDTO x = datosPeriodo.stream().filter(e -> e.getRuc().equals(obj.getRuc()))
                            .findFirst()
                            .orElse(null);

                    if (x != null) {
                        Cell mD = dataRow.createCell(colNum++);
                        String movVal = (x.getMovimiento() != null && !x.getMovimiento().trim().isEmpty())
                                ? x.getMovimiento()
                                : "-";
                        mD.setCellValue(movVal);
                        mD.setCellStyle(dataStyle2);

                        Cell vD = dataRow.createCell(colNum++);
                        int cvV = x.getConfirmacionVentas() != null ? x.getConfirmacionVentas() : 0;
                        vD.setCellValue(cvV == 1 ? "✓" : "X");
                        vD.setCellStyle(cvV == 1 ? dataGreenStyle : dataSimpleRedStyle);

                        Cell cD = dataRow.createCell(colNum++);
                        int ccV = x.getConfirmacionCompras() != null ? x.getConfirmacionCompras() : 0;
                        cD.setCellValue(ccV == 1 ? "✓" : "X");
                        cD.setCellStyle(ccV == 1 ? dataGreenStyle : dataSimpleRedStyle);

                        Cell pD = dataRow.createCell(colNum++);
                        int preV = x.getPreLiquidacion() != null ? x.getPreLiquidacion() : 0;
                        if (preV == 1) {
                            pD.setCellValue("✓");
                            pD.setCellStyle(dataGreenStyle);
                        } else if (preV == 2) {
                            pD.setCellValue("-");
                            pD.setCellStyle(dataStyleNeg);
                        } else {
                            pD.setCellValue("X");
                            pD.setCellStyle(dataSimpleRedStyle);
                        }

                        Cell coD = dataRow.createCell(colNum++);
                        int confV = x.getConfirmacion() != null ? x.getConfirmacion() : 0;
                        if (confV == 1) {
                            coD.setCellValue("✓");
                            coD.setCellStyle(dataGreenStyle);
                        } else if (confV == 2) {
                            coD.setCellValue("-");
                            coD.setCellStyle(dataStyleNeg);
                        } else {
                            coD.setCellValue("X");
                            coD.setCellStyle(dataSimpleRedStyle);
                        }

                        Cell pdtD = dataRow.createCell(colNum++);
                        boolean hasPdt = false;
                        if (x.getPdtRegistrado() != null && x.getPdtRegistrado() == 1) {
                            hasPdt = true;
                        }
                        pdtD.setCellValue(hasPdt ? "✓" : "X");
                        pdtD.setCellStyle(hasPdt ? dataGreenStyleNeg : dataRedStyleNeg);

                        Cell pleD = dataRow.createCell(colNum++);
                        int pleV = x.getPleCV() != null ? x.getPleCV() : 0;
                        if (pleV == 2) {
                            pleD.setCellValue("-");
                            pleD.setCellStyle(dataRedStyleNeg);
                        } else {
                            boolean hasPdtReg = false;
                            if (x.getPdtRegistrado() != null && x.getPdtRegistrado() == 1) {
                                hasPdtReg = true;
                            }
                            pleD.setCellValue(hasPdtReg ? "✓" : "X");
                            pleD.setCellStyle(hasPdtReg ? dataGreenStyleNeg : dataRedStyleNeg);
                        }

                        Cell sireD = dataRow.createCell(colNum++);
                        int sireV = x.getSireCV() != null ? x.getSireCV() : 0;
                        if (sireV == 1) {
                            sireD.setCellValue("✓");
                            sireD.setCellStyle(dataGreenStyleNeg);
                        } else {
                            sireD.setCellValue(sireV == 2 ? "-" : "X");
                            sireD.setCellStyle(dataRedStyleNeg);
                        }
                    } else {
                        for (int k = 0; k < 8; k++) {
                            Cell emptyCell = dataRow.createCell(colNum++);
                            emptyCell.setCellValue("-");
                            emptyCell.setCellStyle(dataStyle2);
                        }
                    }
                    colNum++; // spacing column
                }
                rowNum++;
            }

            sheet.createFreezePane(0, 8);
            sheet.setAutoFilter(new CellRangeAddress(inicioFilt - 1, rowNum - 1, 0, colNum - 2));

            for (int contCol = 0; contCol < colNum; contCol++) {
                sheet.autoSizeColumn(contCol);
            }

            wb.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Error generating ranged process Excel", e);
            return new byte[0];
        }
    }

    public byte[] excelAvance(String pI, String pF) {
        LoginProcesosRequest req = new LoginProcesosRequest();
        req.setPeriodoI(pI);
        req.setPeriodoF(pF);
        return excelAvance(req);
    }

    public byte[] excelAvance(LoginProcesosRequest req) {
        String periodoDesdeElemento = req.getPeriodoI();
        String periodoHastaElemento = req.getPeriodoF() != null && !req.getPeriodoF().isEmpty()
                ? req.getPeriodoF()
                : periodoDesdeElemento;

        if (periodoDesdeElemento == null || periodoDesdeElemento.isEmpty()) {
            return new byte[0];
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // Convierte a mes operativo (periodo de PDT + 1)
            LocalDate desde = LocalDate.parse(periodoDesdeElemento + "-01").plusMonths(1);
            LocalDate hasta = LocalDate.parse(periodoHastaElemento + "-01").plusMonths(1);

            Workbook wb = new XSSFWorkbook();

            // Colores
            XSSFColor ROYAL_BLUE_NAVY = new XSSFColor(new byte[] { (byte) 0, (byte) 0, (byte) 128 }, null);
            XSSFColor MATTE_BLACK = new XSSFColor(new byte[] { (byte) 43, (byte) 43, (byte) 43 }, null);
            XSSFColor LIGHT_SKYBLUE = new XSSFColor(new byte[] { (byte) 235, (byte) 245, (byte) 251 }, null);
            XSSFColor LIGHT_GREY = new XSSFColor(new byte[] { (byte) 242, (byte) 242, (byte) 242 }, null);
            XSSFColor BLACK = new XSSFColor(new byte[] { (byte) 0, (byte) 0, (byte) 0 }, null);
            XSSFColor WHITE = new XSSFColor(new byte[] { (byte) 255, (byte) 255, (byte) 255 }, null);

            // Fuentes
            XSSFFont cabeceraFont = PoiUtils.fuente((XSSFWorkbook) wb, WHITE, 17, true);
            XSSFFont fontWhite9b = PoiUtils.fuente((XSSFWorkbook) wb, WHITE, 9, true);
            XSSFFont fontWhite9 = PoiUtils.fuente((XSSFWorkbook) wb, WHITE, 9, false);
            XSSFFont fontBlack9b = PoiUtils.fuente((XSSFWorkbook) wb, MATTE_BLACK, 9, true);
            XSSFFont fontBlack9 = PoiUtils.fuente((XSSFWorkbook) wb, MATTE_BLACK, 9, false);

            // Estilos
            CellStyle cabeceraStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER,
                    ROYAL_BLUE_NAVY, cabeceraFont);
            CellStyle subHeaderStyle = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER,
                    MATTE_BLACK, fontWhite9b);
            PoiUtils.addBorders(subHeaderStyle, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle fondoBlackStyleSinNegrita = PoiUtils.createCellStyle((XSSFWorkbook) wb,
                    HorizontalAlignment.CENTER, BLACK, fontWhite9);
            PoiUtils.addBorders(fondoBlackStyleSinNegrita, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataStyleSkyBlueCenter = PoiUtils.createCellStyle((XSSFWorkbook) wb,
                    HorizontalAlignment.CENTER, LIGHT_SKYBLUE, fontBlack9b);
            PoiUtils.addBorders(dataStyleSkyBlueCenter, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle styleBlackFontBlackLeft = PoiUtils.createCellStyle((XSSFWorkbook) wb,
                    HorizontalAlignment.LEFT, WHITE, fontBlack9b);
            PoiUtils.addBorders(styleBlackFontBlackLeft, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);

            CellStyle styleBlackFontBlackRight = PoiUtils.createCellStyle((XSSFWorkbook) wb,
                    HorizontalAlignment.RIGHT, WHITE, fontBlack9b);
            PoiUtils.addBorders(styleBlackFontBlackRight, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);

            CellStyle dataStyle2SinNegrita = PoiUtils.createCellStyle((XSSFWorkbook) wb,
                    HorizontalAlignment.RIGHT, LIGHT_GREY, fontBlack9);
            dataStyle2SinNegrita.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat("#,##0"));
            PoiUtils.addBorders(dataStyle2SinNegrita, BorderStyle.THIN, IndexedColors.WHITE);

            // HOJA 1: POR PERIODO
            String sheetName = "POR PERIODO";
            Sheet sheet = wb.createSheet(sheetName);

            int rowNum = 0;
            LocalDate curMes = desde;

            while (!curMes.isAfter(hasta)) {
                String anioDesde = String.format("%04d", curMes.getYear());
                String mesDesde = String.format("%02d", curMes.getMonthValue());

                String fechaI = anioDesde + "-" + mesDesde + "-01";
                LocalDate nextMes = curMes.plusMonths(1);
                String fechaF = String.format("%04d-%02d-01", nextMes.getYear(), nextMes.getMonthValue());

                // Periodo PDT trabajado
                LocalDate pdtPeriod = curMes.minusMonths(1);
                String anioPdt = String.format("%04d", pdtPeriod.getYear());
                String mesPdt = String.format("%02d", pdtPeriod.getMonthValue());

                List<LoginProcesosAvanceDTO> list = loginProcesosRepository.listAvanceMensual(anioPdt, mesPdt, fechaI,
                        fechaF);

                // Días entre fechaI y fechaF
                List<String> fechas = new ArrayList<>();
                LocalDate d = curMes;
                while (d.isBefore(nextMes)) {
                    fechas.add(d.toString());
                    d = d.plusDays(1);
                }

                int nroLista = fechas.size();
                int colEnd = CellReference.convertColStringToIndex("A") + (nroLista + 4);
                String colEndRef = CellReference.convertNumToColString(colEnd);

                // Cabecera periodo
                Row cabecera = sheet.createRow(rowNum);
                Cell cellCabecera = cabecera.createCell(0);
                cellCabecera.setCellStyle(cabeceraStyle);
                cellCabecera.setCellValue("AVANCE    " + anioPdt + " - " + mesPdt);
                sheet.addMergedRegion(CellRangeAddress.valueOf("A" + (rowNum + 1) + ":" + colEndRef + (rowNum + 1)));
                rowNum++;

                // Cabecera columnas
                Row title = sheet.createRow(rowNum);
                int colNum = 0;

                Cell cellTitleN = title.createCell(colNum++);
                cellTitleN.setCellStyle(subHeaderStyle);
                cellTitleN.setCellValue("N°");

                Cell cellTitleProceso = title.createCell(colNum++);
                cellTitleProceso.setCellStyle(subHeaderStyle);
                cellTitleProceso.setCellValue("PROCESO");

                Cell cellTitleDiasTrabajados = title.createCell(colNum++);
                cellTitleDiasTrabajados.setCellStyle(subHeaderStyle);
                cellTitleDiasTrabajados.setCellValue("D T");

                Cell cellTitlePorcentajeDiasTrabajados = title.createCell(colNum++);
                cellTitlePorcentajeDiasTrabajados.setCellStyle(subHeaderStyle);
                cellTitlePorcentajeDiasTrabajados.setCellValue("%");

                Cell cellTitleConteoProceso = title.createCell(colNum++);
                cellTitleConteoProceso.setCellStyle(subHeaderStyle);
                cellTitleConteoProceso.setCellValue("C");

                for (String f : fechas) {
                    Cell cellTitleDia = title.createCell(colNum++);
                    cellTitleDia.setCellStyle(subHeaderStyle);
                    cellTitleDia.setCellValue(f.substring(8, 10));
                }
                rowNum++;

                // Filas de datos
                int n = 1;
                LoginProcesosAvanceDTO procesoAux = null;
                int idProceso = 0;
                for (int i = 0; i < list.size(); i++) {
                    LoginProcesosAvanceDTO procesoActual = list.get(i);
                    if (procesoAux == null || procesoActual.getIdProceso() != procesoAux.getIdProceso()) {
                        procesoAux = procesoActual;
                        idProceso = procesoActual.getIdProceso();

                        Row data = sheet.createRow(rowNum);
                        colNum = 0;

                        Cell nData = data.createCell(colNum++);
                        nData.setCellValue(n);
                        nData.setCellStyle(fondoBlackStyleSinNegrita);

                        Cell descProcesoData = data.createCell(colNum++);
                        descProcesoData.setCellValue(procesoAux.getDescProceso());
                        descProcesoData.setCellStyle(styleBlackFontBlackLeft);

                        Cell diasTrabajadosData = data.createCell(colNum++);
                        long diasTrabajados = diferenciaDias(list, idProceso);
                        diasTrabajadosData.setCellValue(diasTrabajados);
                        diasTrabajadosData.setCellStyle(styleBlackFontBlackRight);

                        Cell porcentajeDiasTrabajadosData = data.createCell(colNum++);
                        long porcentajeDiasTrabajados = ((diasTrabajados * 100) / 30);
                        porcentajeDiasTrabajadosData.setCellValue(porcentajeDiasTrabajados + " %");
                        porcentajeDiasTrabajadosData.setCellStyle(styleBlackFontBlackRight);

                        int conteoProceso = 0;
                        Cell conteoProcesoData = data.createCell(colNum++);
                        conteoProcesoData.setCellStyle(styleBlackFontBlackRight);

                        for (String f : fechas) {
                            final int idProcesoFinal = idProceso;
                            LoginProcesosAvanceDTO procesoX = list.stream()
                                    .filter(e -> e.getIdProceso() == idProcesoFinal && f.equals(e.getFechaAvance()))
                                    .findFirst().orElse(null);

                            int avanceInt = 0;
                            if (procesoX != null) {
                                int avanceTmInt = procesoX.getAvanceTM();
                                int avanceTtInt = procesoX.getAvanceTT();
                                avanceInt = avanceTmInt + avanceTtInt;
                                conteoProceso += avanceInt;
                            }

                            Cell avanceData = data.createCell(colNum++);
                            if (avanceInt > 0) {
                                avanceData.setCellValue(avanceInt);
                            } else {
                                avanceData.setCellValue("");
                            }
                            avanceData.setCellStyle(dataStyle2SinNegrita);
                        }

                        conteoProcesoData.setCellValue(conteoProceso);

                        rowNum++;
                        n++;
                    }
                }

                rowNum++;
                curMes = curMes.plusMonths(1);
            }

            for (int contCol = 0; contCol < 36; contCol++) {
                sheet.autoSizeColumn(contCol);
            }

            // HOJA 2: POR PROCESO
            String sheetName2 = "POR PROCESO";
            Sheet sheet2 = wb.createSheet(sheetName2);
            Row title2 = sheet2.createRow(0);
            int colNum2 = 0;

            Cell cellTitleProceso_2 = title2.createCell(colNum2++);
            cellTitleProceso_2.setCellStyle(dataStyleSkyBlueCenter);
            cellTitleProceso_2.setCellValue("PROCESO");

            Cell cellTitlePeriodo_2 = title2.createCell(colNum2++);
            cellTitlePeriodo_2.setCellStyle(dataStyleSkyBlueCenter);
            cellTitlePeriodo_2.setCellValue("PERIODO");

            for (int day = 1; day <= 31; day++) {
                Cell cellDay = title2.createCell(colNum2++);
                cellDay.setCellStyle(dataStyleSkyBlueCenter);
                cellDay.setCellValue(String.valueOf(day));
            }

            Cell cellTitleDiasTrabajados_2 = title2.createCell(colNum2++);
            cellTitleDiasTrabajados_2.setCellStyle(dataStyleSkyBlueCenter);
            cellTitleDiasTrabajados_2.setCellValue("D T");

            Cell cellTitlePorcentajeDiasTrabajados_2 = title2.createCell(colNum2++);
            cellTitlePorcentajeDiasTrabajados_2.setCellStyle(dataStyleSkyBlueCenter);
            cellTitlePorcentajeDiasTrabajados_2.setCellValue("%");

            Cell cellTitleConteoProceso_2 = title2.createCell(colNum2++);
            cellTitleConteoProceso_2.setCellStyle(dataStyleSkyBlueCenter);
            cellTitleConteoProceso_2.setCellValue("C");

            for (int contCol = 0; contCol < 36; contCol++) {
                sheet2.autoSizeColumn(contCol);
            }

            wb.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Error generating monthly process progress Excel", e);
            return new byte[0];
        }
    }

    private long diferenciaDias(List<LoginProcesosAvanceDTO> list, int idProceso) {
        LocalDate minDate = list.stream()
                .filter(e -> e.getIdProceso() == idProceso && e.getFechaAvance() != null
                        && !e.getFechaAvance().isEmpty())
                .map(e -> LocalDate.parse(e.getFechaAvance()))
                .min(LocalDate::compareTo)
                .orElse(null);

        LocalDate maxDate = list.stream()
                .filter(e -> e.getIdProceso() == idProceso && e.getFechaAvance() != null
                        && !e.getFechaAvance().isEmpty())
                .map(e -> LocalDate.parse(e.getFechaAvance()))
                .max(LocalDate::compareTo)
                .orElse(null);

        if (minDate == null || maxDate == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(minDate, maxDate) + 1;
    }
}
