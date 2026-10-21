package com.joa.prexixionapi.modules.pdt621.services;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToDoubleFunction;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.joa.prexixionapi.modules.pdt621.dto.Pdt621DTO;
import com.joa.prexixionapi.modules.pdt621.dto.Pdt621FilterRequestDTO;
import com.joa.prexixionapi.modules.pdt621.dto.Pdt621ListDTO;
import com.joa.prexixionapi.modules.pdt621.dto.Pdt621RegistroDTO;
import com.joa.prexixionapi.modules.pdt621.dto.Pdt621ReportAnualDTO;
import com.joa.prexixionapi.utils.ExcelStyleManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class Pdt621ExcelService {

    private final Pdt621Service pdt621Service;

    public byte[] excel(Pdt621FilterRequestDTO req) {
        List<Pdt621ListDTO> list = pdt621Service.getPdt621List(req);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            XSSFWorkbook wb = new XSSFWorkbook();
            Sheet sheet = wb.createSheet("PDT 621");
            ExcelStyleManager sm = new ExcelStyleManager(wb);

            // Styles
            XSSFCellStyle headerStyle = sm.getHeaderStyle(16);
            XSSFCellStyle subHeaderStyle = sm.getSubHeaderStyle(9);
            XSSFCellStyle subHeaderBlueStyle = sm.getSubHeaderStyleBlue(9);

            XSSFCellStyle dataCenterStyle = sm.getDataCenterStyle(9);
            XSSFCellStyle dataCenterBoldStyle = sm.getDataCenterBoldStyle(9);
            XSSFCellStyle dataLeftStyle = sm.getDataLeftStyle(9);

            XSSFCellStyle dataGreenStyle = sm.getFondoGreenStyle(9);
            XSSFCellStyle dataRedStyle = sm.getFondoRedStyle(9);
            XSSFCellStyle dataLightBlueStyle = sm.getFondoLightBlueStyle(9);
            XSSFCellStyle dataLightRedStyle = sm.getFondoLightRedStyle(9);

            // Number formats
            XSSFCellStyle numberStyle = sm.getDataCenterStyle(9);
            numberStyle.setAlignment(HorizontalAlignment.RIGHT);
            numberStyle.setDataFormat(wb.createDataFormat().getFormat("#,##0"));

            XSSFCellStyle numberRedStyle = sm.getCustomStyle(
                    ExcelStyleManager.GERENCIA_GREY_RGB,
                    ExcelStyleManager.RED_RGB,
                    9, true, HorizontalAlignment.RIGHT, BorderStyle.THIN, IndexedColors.WHITE);
            numberRedStyle.setDataFormat(wb.createDataFormat().getFormat("#,##0"));

            // Summary counters by RUC last digit 0-9
            int[] declaradas = new int[10];
            int[] pendientes = new int[10];
            int[] rus = new int[10];

            int rowNum = 0;
            int colNum = 0;

            // --- RESUMEN BLOCK ---
            Row resumenHead = sheet.createRow(rowNum++);
            colNum = CellReference.convertColStringToIndex("K");
            Cell cellResumen = resumenHead.createCell(colNum);
            cellResumen.setCellStyle(subHeaderBlueStyle);
            cellResumen.setCellValue("R E S U M E N");
            sheet.addMergedRegion(new CellRangeAddress(0, 0, colNum, colNum + 4));

            Row resumenHead2 = sheet.createRow(rowNum++);
            colNum = CellReference.convertColStringToIndex("K");
            String[] resHeaders = { "RUC", "D", "P", "R", "TOTAL" };
            for (String h : resHeaders) {
                Cell c = resumenHead2.createCell(colNum++);
                c.setCellStyle(subHeaderStyle);
                c.setCellValue(h);
            }

            Row[] rucRows = new Row[10];
            for (int d = 0; d < 10; d++) {
                rucRows[d] = sheet.createRow(rowNum++);
            }

            Row totalRow = sheet.createRow(rowNum++);

            rowNum += 2; // Blank lines

            // --- TABLE TITLE ---
            Row cabecera = sheet.createRow(rowNum++);
            cabecera.setHeightInPoints(sheet.getDefaultRowHeightInPoints() * 2.5f);
            Cell cellCabecera = cabecera.createCell(0);
            cellCabecera.setCellStyle(headerStyle);
            cellCabecera.setCellValue(
                    "C  O  R  P  O  R  A  C  I  O  N      G  E  R  E  N  C  I  A . C O M      S . A . C .");
            sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 31));

            // --- SUPER HEADERS ---
            Row superHead = sheet.createRow(rowNum++);
            superHead.setHeightInPoints(20f);

            Cell cContrib = superHead.createCell(0);
            cContrib.setCellStyle(subHeaderStyle);
            cContrib.setCellValue("C O N T R I B U Y E N T E");
            sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 12));

            Cell cDeclar = superHead.createCell(13);
            cDeclar.setCellStyle(subHeaderStyle);
            cDeclar.setCellValue("D E C L A R A C I Ó N");
            sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 13, 23));

            Cell cVentas = superHead.createCell(24);
            cVentas.setCellStyle(subHeaderBlueStyle);
            cVentas.setCellValue("V E N T A S");
            sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 24, 26));

            Cell cCompras = superHead.createCell(27);
            cCompras.setCellStyle(subHeaderBlueStyle);
            cCompras.setCellValue("C O M P R A S");
            sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 27, 29));

            Cell cImpuestos = superHead.createCell(30);
            cImpuestos.setCellStyle(subHeaderBlueStyle);
            cImpuestos.setCellValue("I M P U E S T O S");
            sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 30, 31));

            // --- COLUMN HEADERS ---
            String[] colTitles = {
                    "N°", "CAT", "CGE", "STO", "GRUPO E", "ESTADO", "T. SERV.", "RUC", "ID", "RAZÓN SOCIAL", "RÉGIMEN",
                    "PLE", "SIRE", "V. PLE", "PLE", "V. SIRE", "SIRE", "V. PDT", "PDT", "CP", "F. PDT",
                    "MAIL", "SUS", "P IMP.", "VENTAS", "VENTAS NG", "EXPORTACIONES", "COMPRAS", "COMPRAS NG E",
                    "COMPRAS NG",
                    "IGV", "RENTA"
            };

            Row colHeadRow = sheet.createRow(rowNum++);
            colHeadRow.setHeightInPoints(22f);
            for (int c = 0; c < colTitles.length; c++) {
                Cell cell = colHeadRow.createCell(c);
                cell.setCellStyle(c >= 24 ? subHeaderBlueStyle : subHeaderStyle);
                cell.setCellValue(colTitles[c]);
            }

            int inicioFilt = rowNum;
            int itemIndex = 1;

            // --- DATA ROWS ---
            for (Pdt621ListDTO item : list) {
                Row row = sheet.createRow(rowNum++);
                colNum = 0;

                // N°
                Cell cNro = row.createCell(colNum++);
                cNro.setCellStyle(dataCenterBoldStyle);
                cNro.setCellValue(itemIndex++);

                // CAT
                Cell cCat = row.createCell(colNum++);
                cCat.setCellStyle(dataCenterBoldStyle);
                cCat.setCellValue(item.getAbrCategoria() != null ? item.getAbrCategoria() : "");

                // CGE
                Cell cCge = row.createCell(colNum++);
                if (item.getCategoriaGrupoE() != null && item.getCategoriaGrupoE() == 1) {
                    cCge.setCellStyle(dataLightRedStyle);
                    cCge.setCellValue("SI");
                } else {
                    cCge.setCellStyle(dataCenterStyle);
                    cCge.setCellValue("");
                }

                // STO
                Cell cSto = row.createCell(colNum++);
                if (item.getCategoriaStore() != null && item.getCategoriaStore() == 1) {
                    cSto.setCellStyle(dataLightRedStyle);
                    cSto.setCellValue("SI");
                } else {
                    cSto.setCellStyle(dataCenterStyle);
                    cSto.setCellValue("");
                }

                // GRUPO E
                Cell cGe = row.createCell(colNum++);
                cGe.setCellStyle(dataCenterBoldStyle);
                cGe.setCellValue(item.getDescGrupoEconomico() != null ? item.getDescGrupoEconomico() : "");

                // ESTADO
                Cell cEstado = row.createCell(colNum++);
                String est = item.getEstado() != null ? item.getEstado().toUpperCase() : "";
                cEstado.setCellValue(est);
                if ("ACTIVO".equals(est))
                    cEstado.setCellStyle(dataGreenStyle);
                else if ("SUSPENDIDO".equals(est))
                    cEstado.setCellStyle(dataRedStyle);
                else if ("BAJA DEFINITIVA".equals(est))
                    cEstado.setCellStyle(dataLightBlueStyle);
                else
                    cEstado.setCellStyle(dataCenterStyle);

                // T. SERV.
                Cell cTserv = row.createCell(colNum++);
                cTserv.setCellStyle(dataCenterStyle);
                cTserv.setCellValue(item.getTipoServicioAbr() != null ? item.getTipoServicioAbr() : "");

                // RUC
                Cell cRuc = row.createCell(colNum++);
                cRuc.setCellStyle(dataCenterBoldStyle);
                cRuc.setCellValue(item.getRuc() != null ? item.getRuc() : "");

                // ID (y)
                Cell cY = row.createCell(colNum++);
                cY.setCellStyle(dataCenterBoldStyle);
                cY.setCellValue(item.getY() != null ? item.getY() : "");

                // RAZÓN SOCIAL
                Cell cRs = row.createCell(colNum++);
                cRs.setCellStyle(dataLeftStyle);
                cRs.setCellValue(item.getRazonSocial() != null ? item.getRazonSocial() : "");

                // RÉGIMEN
                Cell cReg = row.createCell(colNum++);
                cReg.setCellStyle(dataCenterStyle);
                cReg.setCellValue(
                        item.getAbrGestionRegimenTributario() != null ? item.getAbrGestionRegimenTributario() : "");

                // PLE
                Cell cEsPle = row.createCell(colNum++);
                cEsPle.setCellStyle(dataCenterStyle);
                cEsPle.setCellValue(item.getPle() != null ? item.getPle() : "");

                // SIRE
                Cell cEsSire = row.createCell(colNum++);
                cEsSire.setCellStyle(dataCenterStyle);
                cEsSire.setCellValue(item.getRucSire() != null ? item.getRucSire() : "");

                // V. PLE
                Cell cVPle = row.createCell(colNum++);
                cVPle.setCellStyle(dataCenterStyle);
                cVPle.setCellValue(item.getFechaVencimientoPle() != null ? item.getFechaVencimientoPle() : "");

                // REG PLE
                Cell cRegPle = row.createCell(colNum++);
                cRegPle.setCellStyle(dataCenterStyle);
                cRegPle.setCellValue(item.getSituacionPle() != null ? item.getSituacionPle() : "");

                // V. SIRE
                Cell cVSire = row.createCell(colNum++);
                cVSire.setCellStyle(dataCenterStyle);
                cVSire.setCellValue(item.getFechaVencimientoSire() != null ? item.getFechaVencimientoSire() : "");

                // SIRE CV
                Cell cSireCv = row.createCell(colNum++);
                if (item.getSireCV() != null) {
                    if (item.getSireCV() == 1) {
                        cSireCv.setCellStyle(dataGreenStyle);
                        cSireCv.setCellValue("SI");
                    } else if (item.getSireCV() == 0) {
                        cSireCv.setCellStyle(dataRedStyle);
                        cSireCv.setCellValue("NO");
                    } else {
                        cSireCv.setCellStyle(dataCenterStyle);
                        cSireCv.setCellValue("N/A");
                    }
                } else {
                    cSireCv.setCellStyle(dataCenterStyle);
                    cSireCv.setCellValue("N/A");
                }

                // V. PDT
                Cell cVPdt = row.createCell(colNum++);
                cVPdt.setCellStyle(dataCenterStyle);
                cVPdt.setCellValue(item.getFechaVencimiento() != null ? item.getFechaVencimiento() : "");

                // PDT REGISTRADO / CP / F. PDT
                Cell cPdtReg = row.createCell(colNum++);
                Cell cPdtCp = row.createCell(colNum++);
                Cell cPdtFec = row.createCell(colNum++);

                boolean hasPdt = false;
                String cpVal = "";
                String fecVal = "";

                if (item.getRegistros() != null && !item.getRegistros().isEmpty()) {
                    Pdt621RegistroDTO lastReg = item.getRegistros().get(item.getRegistros().size() - 1);
                    if (lastReg.getNroOrden() != null && !lastReg.getNroOrden().trim().isEmpty()) {
                        hasPdt = true;
                        cpVal = lastReg.getNroOrden();
                        fecVal = lastReg.getFecha() != null ? lastReg.getFecha() : "";
                    }
                }

                if (hasPdt) {
                    cPdtReg.setCellStyle(dataGreenStyle);
                    cPdtReg.setCellValue("SI");
                    cPdtCp.setCellStyle(dataCenterStyle);
                    cPdtCp.setCellValue(cpVal);
                    cPdtFec.setCellStyle(dataCenterStyle);
                    cPdtFec.setCellValue(fecVal);
                } else {
                    cPdtReg.setCellStyle(dataRedStyle);
                    cPdtReg.setCellValue("NO");
                    cPdtCp.setCellStyle(dataCenterStyle);
                    cPdtCp.setCellValue("");
                    cPdtFec.setCellStyle(dataCenterStyle);
                    cPdtFec.setCellValue("");
                }

                // MAIL
                Cell cMail = row.createCell(colNum++);
                cMail.setCellStyle(dataCenterStyle);
                cMail.setCellValue(item.getMail() != null && item.getMail() == 1 ? "SI" : "NO");

                // SUS
                Cell cSus = row.createCell(colNum++);
                cSus.setCellStyle(dataCenterStyle);
                cSus.setCellValue(item.getSus() != null && item.getSus() == 1 ? "SI" : "NO");

                // P IMP.
                Cell cPImp = row.createCell(colNum++);
                cPImp.setCellStyle(dataCenterStyle);
                cPImp.setCellValue(
                        item.getPagarImpuestos() != null && item.getPagarImpuestos() == 1 ? "CLIENTE" : "G.COM");

                // VENTAS
                Cell cVentasVal = row.createCell(colNum++);
                writeNumber(cVentasVal, item.getVentasG(), numberStyle, numberRedStyle);

                // VENTAS NG
                Cell cVentasNgVal = row.createCell(colNum++);
                writeNumber(cVentasNgVal, item.getVentasNg(), numberStyle, numberRedStyle);

                // EXPORTACIONES
                Cell cExpVal = row.createCell(colNum++);
                writeNumber(cExpVal, item.getExpFactPer(), numberStyle, numberRedStyle);

                // COMPRAS
                Cell cComprasVal = row.createCell(colNum++);
                writeNumber(cComprasVal, item.getComprasG(), numberStyle, numberRedStyle);

                // COMPRAS NG E
                Cell cComprasNgEVal = row.createCell(colNum++);
                writeNumber(cComprasNgEVal, item.getComprasNgE(), numberStyle, numberRedStyle);

                // COMPRAS NG
                Cell cComprasNgVal = row.createCell(colNum++);
                writeNumber(cComprasNgVal, item.getComprasNg(), numberStyle, numberRedStyle);

                // IGV
                Cell cIgvVal = row.createCell(colNum++);
                Double igvAmount = (item.getIgvPorPagar() != null && item.getIgvPorPagar() != 0)
                        ? item.getIgvPorPagar()
                        : item.getResultadoIgv();
                writeNumber(cIgvVal, igvAmount, numberStyle, numberRedStyle);

                // RENTA
                Cell cRentaVal = row.createCell(colNum++);
                Double rentaAmount = (item.getRentaPorPagar() != null && item.getRentaPorPagar() != 0)
                        ? item.getRentaPorPagar()
                        : item.getTotalDeudaTributariaRenta();
                writeNumber(cRentaVal, rentaAmount, numberStyle, numberRedStyle);

                // Resumen counter update
                if (item.getY() != null && item.getY().matches("\\d")) {
                    int digit = Integer.parseInt(item.getY());
                    if (item.getAbrGestionRegimenTributario() != null
                            && "NRUS".equalsIgnoreCase(item.getAbrGestionRegimenTributario())) {
                        rus[digit]++;
                    } else if (hasPdt) {
                        declaradas[digit]++;
                    } else {
                        pendientes[digit]++;
                    }
                }
            }

            int finFilt = Math.max(inicioFilt, rowNum - 1);
            sheet.setAutoFilter(new CellRangeAddress(inicioFilt - 1, finFilt, 0, 31));

            // --- POPULATE RESUMEN TABLE ---
            int totalD = 0, totalP = 0, totalR = 0;
            for (int d = 0; d < 10; d++) {
                Row rRow = rucRows[d];
                colNum = CellReference.convertColStringToIndex("K");

                Cell cDig = rRow.createCell(colNum++);
                cDig.setCellStyle(subHeaderStyle);
                cDig.setCellValue(String.valueOf(d));

                Cell cD = rRow.createCell(colNum++);
                cD.setCellStyle(dataCenterStyle);
                cD.setCellValue(declaradas[d]);
                totalD += declaradas[d];

                Cell cP = rRow.createCell(colNum++);
                cP.setCellStyle(dataCenterStyle);
                cP.setCellValue(pendientes[d]);
                totalP += pendientes[d];

                Cell cR = rRow.createCell(colNum++);
                cR.setCellStyle(dataCenterStyle);
                cR.setCellValue(rus[d]);
                totalR += rus[d];

                Cell cT = rRow.createCell(colNum++);
                cT.setCellStyle(subHeaderStyle);
                cT.setCellValue(declaradas[d] + pendientes[d] + rus[d]);
            }

            colNum = CellReference.convertColStringToIndex("K");
            Cell cTotLbl = totalRow.createCell(colNum++);
            cTotLbl.setCellStyle(subHeaderStyle);
            cTotLbl.setCellValue("TOTAL");

            Cell cTotD = totalRow.createCell(colNum++);
            cTotD.setCellStyle(subHeaderStyle);
            cTotD.setCellValue(totalD);

            Cell cTotP = totalRow.createCell(colNum++);
            cTotP.setCellStyle(subHeaderStyle);
            cTotP.setCellValue(totalP);

            Cell cTotR = totalRow.createCell(colNum++);
            cTotR.setCellStyle(subHeaderStyle);
            cTotR.setCellValue(totalR);

            Cell cTotAll = totalRow.createCell(colNum++);
            cTotAll.setCellStyle(subHeaderStyle);
            cTotAll.setCellValue(totalD + totalP + totalR);

            // Column width adjustments
            sheet.setColumnWidth(0, 5 * 256); // N°
            sheet.setColumnWidth(1, 7 * 256); // CAT
            sheet.setColumnWidth(2, 6 * 256); // CGE
            sheet.setColumnWidth(3, 6 * 256); // STO
            sheet.setColumnWidth(4, 15 * 256); // GRUPO E
            sheet.setColumnWidth(5, 14 * 256); // ESTADO
            sheet.setColumnWidth(6, 10 * 256); // T. SERV
            sheet.setColumnWidth(7, 14 * 256); // RUC
            sheet.setColumnWidth(8, 5 * 256); // ID
            sheet.setColumnWidth(9, 36 * 256); // RAZÓN SOCIAL
            sheet.setColumnWidth(10, 10 * 256); // RÉGIMEN
            sheet.setColumnWidth(11, 6 * 256); // PLE
            sheet.setColumnWidth(12, 6 * 256); // SIRE
            sheet.setColumnWidth(13, 12 * 256); // V. PLE
            sheet.setColumnWidth(14, 8 * 256); // REG PLE
            sheet.setColumnWidth(15, 12 * 256); // V. SIRE
            sheet.setColumnWidth(16, 8 * 256); // SIRE CV
            sheet.setColumnWidth(17, 12 * 256); // V. PDT
            sheet.setColumnWidth(18, 8 * 256); // PDT
            sheet.setColumnWidth(19, 12 * 256); // CP
            sheet.setColumnWidth(20, 12 * 256); // F. PDT
            sheet.setColumnWidth(21, 7 * 256); // MAIL
            sheet.setColumnWidth(22, 7 * 256); // SUS
            sheet.setColumnWidth(23, 10 * 256); // P IMP
            sheet.setColumnWidth(24, 14 * 256); // VENTAS
            sheet.setColumnWidth(25, 14 * 256); // VENTAS NG
            sheet.setColumnWidth(26, 14 * 256); // EXP
            sheet.setColumnWidth(27, 14 * 256); // COMPRAS
            sheet.setColumnWidth(28, 14 * 256); // COMPRAS NG E
            sheet.setColumnWidth(29, 14 * 256); // COMPRAS NG
            sheet.setColumnWidth(30, 14 * 256); // IGV
            sheet.setColumnWidth(31, 14 * 256); // RENTA

            wb.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Error generating PDT 621 Excel", e);
            throw new RuntimeException("Error generando reporte Excel de PDT 621", e);
        }
    }

    public byte[] excelAcumulado(Pdt621FilterRequestDTO req) {
        String pI = req.getPeriodoI();
        String pF = req.getPeriodoF();

        String anioI = pI.substring(0, 4);
        String mesI = pI.substring(5, 7);
        String anioF = pF.substring(0, 4);
        String mesF = pF.substring(5, 7);

        Map<String, List<Pdt621ListDTO>> dataMap = new LinkedHashMap<>();
        List<String> periodos = new ArrayList<>();
        List<Pdt621ListDTO> allClients = new ArrayList<>();

        int start = Integer.parseInt(anioI + mesI);
        int end = Integer.parseInt(anioF + mesF);

        while (start <= end) {
            String pKey = anioI + "-" + mesI;
            periodos.add(pKey);

            Pdt621FilterRequestDTO subReq = new Pdt621FilterRequestDTO();
            subReq.setPeriodoI(pKey);
            subReq.setPeriodoF(pKey);
            subReq.setEstadoFiltro(req.getEstadoFiltro());
            subReq.setGrupoFiltro(req.getGrupoFiltro());
            subReq.setCategoriaFiltro(req.getCategoriaFiltro());
            subReq.setCategoriaGrupoEFiltro(req.getCategoriaGrupoEFiltro());
            subReq.setCategoriaStoreFiltro(req.getCategoriaStoreFiltro());
            subReq.setGrupoEconomicoFiltro(req.getGrupoEconomicoFiltro());

            List<Pdt621ListDTO> subList = pdt621Service.getPdt621List(subReq);
            dataMap.put(pKey, subList);

            for (Pdt621ListDTO c : subList) {
                if (allClients.stream().noneMatch(existing -> existing.getRuc().equals(c.getRuc()))) {
                    allClients.add(c);
                }
            }

            if (mesI.equals("12")) {
                anioI = String.valueOf(Integer.parseInt(anioI) + 1);
                mesI = "01";
            } else {
                int nextM = Integer.parseInt(mesI) + 1;
                mesI = nextM < 10 ? "0" + nextM : String.valueOf(nextM);
            }
            start = Integer.parseInt(anioI + mesI);
        }

        allClients.sort(Comparator.comparing(Pdt621ListDTO::getY, Comparator.nullsLast(String::compareTo))
                .thenComparing(Pdt621ListDTO::getRazonSocial, Comparator.nullsLast(String::compareTo)));

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            XSSFWorkbook wb = new XSSFWorkbook();
            Sheet sheet = wb.createSheet("PDT 621 Acumulado");
            ExcelStyleManager sm = new ExcelStyleManager(wb);

            XSSFCellStyle headerStyle = sm.getHeaderStyle(16);
            XSSFCellStyle subHeaderStyle = sm.getSubHeaderStyle(9);
            XSSFCellStyle subHeaderBlueStyle = sm.getSubHeaderStyleBlue(9);
            XSSFCellStyle dataCenterStyle = sm.getDataCenterStyle(9);
            XSSFCellStyle dataCenterBoldStyle = sm.getDataCenterBoldStyle(9);
            XSSFCellStyle dataLeftStyle = sm.getDataLeftStyle(9);
            XSSFCellStyle dataGreenStyle = sm.getFondoGreenStyle(9);
            XSSFCellStyle dataRedStyle = sm.getFondoRedStyle(9);

            XSSFCellStyle numberStyle = sm.getDataCenterStyle(9);
            numberStyle.setAlignment(HorizontalAlignment.RIGHT);
            numberStyle.setDataFormat(wb.createDataFormat().getFormat("#,##0"));

            int rowNum = 0;

            // Title
            Row cabecera = sheet.createRow(rowNum++);
            cabecera.setHeightInPoints(sheet.getDefaultRowHeightInPoints() * 2f);
            Cell cellCabecera = cabecera.createCell(0);
            cellCabecera.setCellStyle(headerStyle);
            cellCabecera.setCellValue("PDT 621 - REPORTE ACUMULADO (" + pI + " AL " + pF + ")");
            sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 7 + (periodos.size() * 4)));

            rowNum++;

            // Period Headers (Row 1)
            Row superHead = sheet.createRow(rowNum++);
            superHead.setHeightInPoints(20f);
            Cell cInfo = superHead.createCell(0);
            cInfo.setCellStyle(subHeaderStyle);
            cInfo.setCellValue("DATOS CLIENTE");
            sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 6));

            int colCursor = 7;
            for (String p : periodos) {
                Cell cP = superHead.createCell(colCursor);
                cP.setCellStyle(subHeaderBlueStyle);
                cP.setCellValue(p);
                sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, colCursor, colCursor + 3));
                colCursor += 4;
            }

            // Columns Header (Row 2)
            Row colHead = sheet.createRow(rowNum++);
            colHead.setHeightInPoints(20f);
            String[] baseCols = { "N°", "CAT", "GRUPO E", "ESTADO", "RUC", "ID", "RAZÓN SOCIAL" };
            for (int i = 0; i < baseCols.length; i++) {
                Cell c = colHead.createCell(i);
                c.setCellStyle(subHeaderStyle);
                c.setCellValue(baseCols[i]);
            }

            colCursor = 7;
            for (int pIdx = 0; pIdx < periodos.size(); pIdx++) {
                String[] pSubCols = { "PDT", "VENTAS", "IGV", "RENTA" };
                for (String sc : pSubCols) {
                    Cell c = colHead.createCell(colCursor++);
                    c.setCellStyle(subHeaderBlueStyle);
                    c.setCellValue(sc);
                }
            }

            int inicioFilt = rowNum;
            int itemIndex = 1;

            // Fill rows
            for (Pdt621ListDTO clie : allClients) {
                Row row = sheet.createRow(rowNum++);
                int cCol = 0;

                Cell cNro = row.createCell(cCol++);
                cNro.setCellStyle(dataCenterBoldStyle);
                cNro.setCellValue(itemIndex++);

                Cell cCat = row.createCell(cCol++);
                cCat.setCellStyle(dataCenterBoldStyle);
                cCat.setCellValue(clie.getAbrCategoria() != null ? clie.getAbrCategoria() : "");

                Cell cGe = row.createCell(cCol++);
                cGe.setCellStyle(dataCenterStyle);
                cGe.setCellValue(clie.getDescGrupoEconomico() != null ? clie.getDescGrupoEconomico() : "");

                Cell cEst = row.createCell(cCol++);
                cEst.setCellStyle(dataCenterStyle);
                cEst.setCellValue(clie.getEstado() != null ? clie.getEstado() : "");

                Cell cRuc = row.createCell(cCol++);
                cRuc.setCellStyle(dataCenterBoldStyle);
                cRuc.setCellValue(clie.getRuc());

                Cell cY = row.createCell(cCol++);
                cY.setCellStyle(dataCenterBoldStyle);
                cY.setCellValue(clie.getY() != null ? clie.getY() : "");

                Cell cRs = row.createCell(cCol++);
                cRs.setCellStyle(dataLeftStyle);
                cRs.setCellValue(clie.getRazonSocial() != null ? clie.getRazonSocial() : "");

                // Period data
                for (String pKey : periodos) {
                    List<Pdt621ListDTO> pList = dataMap.get(pKey);
                    Pdt621ListDTO itemMonth = (pList != null) ? pList.stream()
                            .filter(x -> x.getRuc().equals(clie.getRuc()))
                            .findFirst().orElse(null) : null;

                    Cell cPdt = row.createCell(cCol++);
                    Cell cV = row.createCell(cCol++);
                    Cell cIgv = row.createCell(cCol++);
                    Cell cRta = row.createCell(cCol++);

                    if (itemMonth != null) {
                        boolean hasPdt = itemMonth.getRegistros() != null && !itemMonth.getRegistros().isEmpty()
                                && itemMonth.getRegistros().get(itemMonth.getRegistros().size() - 1)
                                        .getNroOrden() != null
                                && !itemMonth.getRegistros().get(itemMonth.getRegistros().size() - 1).getNroOrden()
                                        .trim().isEmpty();

                        cPdt.setCellValue(hasPdt ? "SI" : "NO");
                        cPdt.setCellStyle(hasPdt ? dataGreenStyle : dataRedStyle);

                        writeNumber(cV, itemMonth.getVentasG(), numberStyle, numberStyle);
                        Double igvVal = (itemMonth.getIgvPorPagar() != null && itemMonth.getIgvPorPagar() != 0)
                                ? itemMonth.getIgvPorPagar()
                                : itemMonth.getResultadoIgv();
                        writeNumber(cIgv, igvVal, numberStyle, numberStyle);

                        Double rtaVal = (itemMonth.getRentaPorPagar() != null && itemMonth.getRentaPorPagar() != 0)
                                ? itemMonth.getRentaPorPagar()
                                : itemMonth.getTotalDeudaTributariaRenta();
                        writeNumber(cRta, rtaVal, numberStyle, numberStyle);
                    } else {
                        cPdt.setCellStyle(dataCenterStyle);
                        cPdt.setCellValue("-");
                        cV.setCellStyle(dataCenterStyle);
                        cV.setCellValue("-");
                        cIgv.setCellStyle(dataCenterStyle);
                        cIgv.setCellValue("-");
                        cRta.setCellStyle(dataCenterStyle);
                        cRta.setCellValue("-");
                    }
                }
            }

            int finFilt = Math.max(inicioFilt, rowNum - 1);
            sheet.setAutoFilter(new CellRangeAddress(inicioFilt - 1, finFilt, 0, colCursor - 1));

            // Auto-size or set width
            sheet.setColumnWidth(0, 5 * 256);
            sheet.setColumnWidth(1, 7 * 256);
            sheet.setColumnWidth(2, 14 * 256);
            sheet.setColumnWidth(3, 12 * 256);
            sheet.setColumnWidth(4, 14 * 256);
            sheet.setColumnWidth(5, 5 * 256);
            sheet.setColumnWidth(6, 35 * 256);

            for (int c = 7; c < colCursor; c++) {
                sheet.setColumnWidth(c, 11 * 256);
            }

            wb.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Error generating PDT 621 Acumulado Excel", e);
            throw new RuntimeException("Error generando reporte Excel acumulado de PDT 621", e);
        }
    }

    public byte[] excelCliente(String ruc, String anio, String mes) {
        Pdt621DTO dto = pdt621Service.getOne(ruc, anio, mes);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            XSSFWorkbook wb = new XSSFWorkbook();
            Sheet sheet = wb.createSheet("Liquidación PDT 621");
            ExcelStyleManager sm = new ExcelStyleManager(wb);

            XSSFCellStyle headerStyle = sm.getHeaderStyle(14);
            XSSFCellStyle subHeaderBlueStyle = sm.getSubHeaderStyleBlue(10);
            XSSFCellStyle labelStyle = sm.getDataLeftBoldStyle(9);
            XSSFCellStyle valueStyle = sm.getDataLeftStyle(9);
            XSSFCellStyle numberStyle = sm.getDataCenterStyle(9);
            numberStyle.setAlignment(HorizontalAlignment.RIGHT);
            numberStyle.setDataFormat(wb.createDataFormat().getFormat("#,##0.00"));

            int rowNum = 0;

            // Title
            Row rTitle = sheet.createRow(rowNum++);
            Cell cT = rTitle.createCell(0);
            cT.setCellStyle(headerStyle);
            cT.setCellValue("LIQUIDACIÓN DE IMPUESTOS PDT 621");
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));

            rowNum++;

            // Client Info
            addInfoRow(sheet, rowNum++, "RUC:", dto.getRuc() != null ? dto.getRuc() : "", labelStyle, valueStyle);
            addInfoRow(sheet, rowNum++, "Razón Social:", dto.getRazonSocial() != null ? dto.getRazonSocial() : "",
                    labelStyle, valueStyle);
            addInfoRow(sheet, rowNum++, "Periodo:", anio + " - " + mes, labelStyle, valueStyle);
            addInfoRow(sheet, rowNum++, "Régimen:",
                    dto.getAbrGestionRegimenTributario() != null ? dto.getAbrGestionRegimenTributario() : "",
                    labelStyle, valueStyle);
            addInfoRow(sheet, rowNum++, "Vencimiento:",
                    dto.getFechaVencimiento() != null ? dto.getFechaVencimiento() : "", labelStyle, valueStyle);

            rowNum++;

            // Sections Table
            Row rSecHead = sheet.createRow(rowNum++);
            Cell cSh1 = rSecHead.createCell(0);
            cSh1.setCellStyle(subHeaderBlueStyle);
            cSh1.setCellValue("CONCEPTO");
            sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 2));

            Cell cSh2 = rSecHead.createCell(3);
            cSh2.setCellStyle(subHeaderBlueStyle);
            cSh2.setCellValue("BASE IMPONIBLE");

            Cell cSh3 = rSecHead.createCell(4);
            cSh3.setCellStyle(subHeaderBlueStyle);
            cSh3.setCellValue("TRIBUTO / SALDO");

            // IGV
            addCalcRow(sheet, rowNum++, "Ventas Netas Gravadas (18%)", dto.getVentasG(), dto.getMesIgv(), labelStyle,
                    numberStyle);
            addCalcRow(sheet, rowNum++, "Ventas Gravadas (10%)", dto.getVentasNetas10(), dto.getVentasNetasIgv10(),
                    labelStyle, numberStyle);
            addCalcRow(sheet, rowNum++, "Ventas No Gravadas", dto.getVentasNg(), 0.0, labelStyle, numberStyle);
            addCalcRow(sheet, rowNum++, "Compras Netas Gravadas", dto.getComprasG(), dto.getImpComprasGigv(),
                    labelStyle, numberStyle);
            addCalcRow(sheet, rowNum++, "Saldo a Favor Mes Anterior IGV", null, dto.getMesAnteriorIgv(), labelStyle,
                    numberStyle);
            addCalcRow(sheet, rowNum++, "Resultado IGV", null, dto.getResultadoIgv(), labelStyle, numberStyle);
            addCalcRow(sheet, rowNum++, "IGV a Pagar", null, dto.getIgvPorPagar(), labelStyle, numberStyle);

            rowNum++;

            // Renta
            addCalcRow(sheet, rowNum++, "Ingresos Netos (Base Renta)", dto.getBaseRenta(), dto.getMesRenta(),
                    labelStyle, numberStyle);
            addCalcRow(sheet, rowNum++, "Saldo a Favor Renta Mes Anterior", null, dto.getMesAnteriorRenta(), labelStyle,
                    numberStyle);
            addCalcRow(sheet, rowNum++, "Total Deuda Tributaria Renta", null, dto.getTotalDeudaTributariaRenta(),
                    labelStyle, numberStyle);
            addCalcRow(sheet, rowNum++, "Renta a Pagar", null, dto.getRentaPorPagar(), labelStyle, numberStyle);

            sheet.setColumnWidth(0, 15 * 256);
            sheet.setColumnWidth(1, 15 * 256);
            sheet.setColumnWidth(2, 20 * 256);
            sheet.setColumnWidth(3, 18 * 256);
            sheet.setColumnWidth(4, 18 * 256);

            wb.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Error generating PDT 621 Cliente Excel", e);
            throw new RuntimeException("Error generando reporte individual de PDT 621", e);
        }
    }

    // ======================= REPORTE ANUAL (PDT 621) =======================

    private record AnualCol(ToDoubleFunction<Pdt621ReportAnualDTO> getter, boolean strong, boolean tasa) {
    }

    private static final String[] MESES_ABR = { "Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Set", "Oct",
            "Nov", "Dic" };

    /** Primera columna numérica (H) y última columna (AR) del reporte anual. */
    private static final int ANUAL_FIRST_NUM_COL = 7;
    private static final int ANUAL_LAST_COL = 43;

    /** Columnas numéricas del reporte, en el orden en que se dibujan desde la columna H. */
    private List<AnualCol> anualColumns() {
        List<AnualCol> c = new ArrayList<>();
        // Operaciones gravadas (H..M) + tasa (N)
        c.add(new AnualCol(Pdt621ReportAnualDTO::getVentasG, false, false));
        c.add(new AnualCol(Pdt621ReportAnualDTO::getComprasG, false, false));
        c.add(new AnualCol(Pdt621ReportAnualDTO::getVentasNetas10, false, false));
        c.add(new AnualCol(Pdt621ReportAnualDTO::getComprasNetas10, false, false));
        c.add(new AnualCol(r -> r.getVentasG() + r.getVentasNetas10(), false, false));
        c.add(new AnualCol(r -> r.getComprasG() + r.getComprasNetas10(), false, false));
        c.add(new AnualCol(Pdt621ReportAnualDTO::getTasa, false, true));
        // IGV
        c.add(new AnualCol(Pdt621ReportAnualDTO::getMesIgv, true, false));
        c.add(new AnualCol(Pdt621ReportAnualDTO::getMesAnteriorIgv, false, false));
        c.add(new AnualCol(Pdt621ReportAnualDTO::getAjusteIgv, false, false));
        c.add(new AnualCol(Pdt621ReportAnualDTO::getResultadoIgv, true, false));
        // Percepciones
        c.add(new AnualCol(Pdt621ReportAnualDTO::getMesPer, true, false));
        c.add(new AnualCol(Pdt621ReportAnualDTO::getMesAnteriorPer, false, false));
        c.add(new AnualCol(Pdt621ReportAnualDTO::getCompensacionPer, false, false));
        c.add(new AnualCol(Pdt621ReportAnualDTO::getAjustePer, false, false));
        c.add(new AnualCol(Pdt621ReportAnualDTO::getResultadoPer, true, false));
        // Retenciones
        c.add(new AnualCol(Pdt621ReportAnualDTO::getMesRet, true, false));
        c.add(new AnualCol(Pdt621ReportAnualDTO::getMesAnteriorRet, false, false));
        c.add(new AnualCol(Pdt621ReportAnualDTO::getCompensacionRet, false, false));
        c.add(new AnualCol(Pdt621ReportAnualDTO::getAjusteRet, false, false));
        c.add(new AnualCol(Pdt621ReportAnualDTO::getResultadoRet, true, false));
        // Total IGV
        c.add(new AnualCol(Pdt621ReportAnualDTO::getIgvPorPagar, true, false));
        // Operaciones no gravadas
        c.add(new AnualCol(Pdt621ReportAnualDTO::getExpFactPer, false, false));
        c.add(new AnualCol(Pdt621ReportAnualDTO::getVentasNg, false, false));
        c.add(new AnualCol(Pdt621ReportAnualDTO::getComprasNgE, false, false));
        c.add(new AnualCol(Pdt621ReportAnualDTO::getComprasNg, false, false));
        // IVAP
        c.add(new AnualCol(Pdt621ReportAnualDTO::getIvapVentasGravadas, false, false));
        c.add(new AnualCol(Pdt621ReportAnualDTO::getIvapTributo, false, false));
        // Renta de 3ra
        c.add(new AnualCol(Pdt621ReportAnualDTO::getBaseRenta, true, false));
        c.add(new AnualCol(Pdt621ReportAnualDTO::getMesRenta, true, false));
        c.add(new AnualCol(Pdt621ReportAnualDTO::getMesAnteriorRenta, false, false));
        c.add(new AnualCol(Pdt621ReportAnualDTO::getAnualRenta, false, false));
        c.add(new AnualCol(Pdt621ReportAnualDTO::getSaldoFavorExportador, false, false));
        c.add(new AnualCol(Pdt621ReportAnualDTO::getCreditoItanRenta, false, false));
        c.add(new AnualCol(Pdt621ReportAnualDTO::getAjusteRenta, false, false));
        c.add(new AnualCol(Pdt621ReportAnualDTO::getTotalDeudaTributariaRenta, false, false));
        c.add(new AnualCol(Pdt621ReportAnualDTO::getRentaPorPagar, true, false));
        return c;
    }

    public byte[] excelAnual(String ruc, List<String> anios) {
        com.joa.prexixionapi.modules.pdt621.dto.Pdt621AnualResponseDTO data = pdt621Service.getPdt621ReportAnual(ruc,
                anios);
        String[] cli = pdt621Service.getClienteBasico(ruc);
        List<Pdt621ReportAnualDTO> list = data.getList() != null ? data.getList() : new ArrayList<>();
        List<AnualCol> cols = anualColumns();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            XSSFWorkbook wb = new XSSFWorkbook();
            Sheet sheet = wb.createSheet("PDT621 ANUAL");
            ExcelStyleManager sm = new ExcelStyleManager(wb);

            XSSFColor black = sm.getColor(ExcelStyleManager.BLACK_RGB);
            XSSFColor red = sm.getColor(ExcelStyleManager.DARK_RED_TEXT_RGB);
            XSSFColor white = sm.getColor(ExcelStyleManager.WHITE_RGB);
            XSSFColor lightGrey = sm.getColor(ExcelStyleManager.LIGHT_GREY_RGB);
            XSSFColor skyBlueGrey = sm.getColor(ExcelStyleManager.SKYBLUE_GREY_RGB);

            XSSFFont fCab = anualFont(wb, black, 11, true);
            XSSFFont fSub = anualFont(wb, black, 8, true);
            XSSFFont fData = anualFont(wb, black, 8, false);
            XSSFFont fRed = anualFont(wb, red, 8, false);

            XSSFCellStyle cabeceraStyle = anualStyle(wb, HorizontalAlignment.CENTER, skyBlueGrey, fCab, null);
            XSSFCellStyle subStyle = anualStyle(wb, HorizontalAlignment.CENTER, skyBlueGrey, fSub, null);
            XSSFCellStyle centerStyle = anualStyle(wb, HorizontalAlignment.CENTER, white, fData, null);
            XSSFCellStyle tasaStyle = anualStyle(wb, HorizontalAlignment.CENTER, white, fData, null);

            XSSFCellStyle[] generalPos = anualNumStyles(wb, white, fData);
            XSSFCellStyle[] generalNeg = anualNumStyles(wb, white, fRed);
            XSSFCellStyle[] strongPos = anualNumStyles(wb, lightGrey, fData);
            XSSFCellStyle[] strongNeg = anualNumStyles(wb, lightGrey, fRed);
            XSSFCellStyle[] totalStyles = anualNumStyles(wb, skyBlueGrey, fSub);

            // Etiquetas de la 3ra fila de cabecera, indexadas por columna
            String[] labels3 = new String[ANUAL_LAST_COL + 1];
            String[] l3 = { "Rect.", "N°", "Fecha", "Constancia", "Ventas", "Compras", "Ventas", "Compras", "Ventas",
                    "Compras", null, "M E S", "A Favor", "Ajuste", "Resultado", "M E S", "A Favor", "Compensa", "Ajuste",
                    "Resultado", "M E S", "A Favor", "Compensa", "Ajuste", "Resultado", "IGV | Por Pagar",
                    "Exportaciones", "Ventas Ng.", "Compras Ng. E", "Compras Ng.", "V GRAVADAS", "TRIB IVAP", "Base",
                    "M E S", "A favor", "Anual", "Del Export.", "c.ITAN", "Ajuste", "Saldo", "RENTA|Por Pagar" };
            System.arraycopy(l3, 0, labels3, 3, l3.length);

            // Encabezado de empresa
            Row empresa = sheet.createRow(0);
            empresa.setHeightInPoints(sheet.getDefaultRowHeightInPoints() * 2);
            anualMerged(sheet, 0, 0, 0, ANUAL_LAST_COL, "R U C : " + ruc + " | " + cli[0], cabeceraStyle);
            anualMerged(sheet, 1, 1, 0, ANUAL_LAST_COL, cli[1], cabeceraStyle);

            int rowNum = 3;
            double[] grand = new double[cols.size()];

            for (String anio : anios) {
                int h = rowNum;

                // ---- Cabecera fila 1 ----
                anualMerged(sheet, h, h + 2, 0, 0, "AÑO", subStyle);
                anualMerged(sheet, h, h + 2, 1, 1, anio, subStyle);
                anualMerged(sheet, h, h + 2, 2, 2, "RT", subStyle);
                anualMerged(sheet, h, h + 1, 3, 6, "PDT | DECLARACIÓN", subStyle);
                anualMerged(sheet, h, h, 7, 13, "OPERACIONES GRAVADAS", subStyle);
                anualMerged(sheet, h, h + 1, 14, 17, "I G V", subStyle);
                anualMerged(sheet, h, h + 1, 18, 22, "P E R C E P C I O N E S", subStyle);
                anualMerged(sheet, h, h + 1, 23, 27, "R E T E N C I O N E S", subStyle);
                anualMerged(sheet, h, h + 1, 28, 28, "T O T A L", subStyle);
                anualMerged(sheet, h, h + 1, 29, 32, "OP. NO GRAVADAS", subStyle);
                anualMerged(sheet, h, h + 1, 33, 34, "IVAP", subStyle);
                anualMerged(sheet, h, h + 1, 35, 43, "R E N T A  D E  3 R A", subStyle);

                // ---- Cabecera fila 2 ----
                anualMerged(sheet, h + 1, h + 1, 7, 8, "PDT | VALOR GRAVADO", subStyle);
                anualMerged(sheet, h + 1, h + 1, 9, 10, "PDT | VALOR IGV 10 %", subStyle);
                anualMerged(sheet, h + 1, h + 1, 11, 12, "TOTAL CALCULABLE", subStyle);
                anualMerged(sheet, h + 1, h + 2, 13, 13, "Tasa", subStyle);

                // ---- Cabecera fila 3 ----
                Row r3 = sheet.getRow(h + 2) != null ? sheet.getRow(h + 2) : sheet.createRow(h + 2);
                for (int c = 3; c <= ANUAL_LAST_COL; c++) {
                    if (labels3[c] == null) {
                        continue;
                    }
                    Cell cell = r3.getCell(c) != null ? r3.getCell(c) : r3.createCell(c);
                    cell.setCellStyle(subStyle);
                    cell.setCellValue(labels3[c]);
                }

                rowNum = h + 3;

                // ---- Filas de datos ----
                double[] sums = new double[cols.size()];
                for (Pdt621ReportAnualDTO d : list) {
                    if (!anio.equals(d.getAnio()) || "13".equals(d.getMes())) {
                        continue;
                    }
                    Row row = sheet.createRow(rowNum++);

                    int mesIdx = Integer.parseInt(d.getMes()) - 1;
                    Cell cMes = row.createCell(0);
                    cMes.setCellStyle(subStyle);
                    cMes.setCellValue(mesIdx >= 0 && mesIdx < 12 ? MESES_ABR[mesIdx] : d.getMes());

                    Cell cPer = row.createCell(1);
                    cPer.setCellStyle(centerStyle);
                    cPer.setCellValue(d.getAnio() + d.getMes());

                    Cell cRt = row.createCell(2);
                    cRt.setCellStyle(centerStyle);
                    cRt.setCellValue(d.getAbrGestionRegimenTributario() != null ? d.getAbrGestionRegimenTributario() : "");

                    String nroRect = "";
                    String fechaRect = "";
                    String constancia = "";
                    if (d.getRegistros() != null && !d.getRegistros().isEmpty()) {
                        Pdt621RegistroDTO reg = d.getRegistros().get(d.getRegistros().size() - 1);
                        nroRect = reg.getNroRectificacion() != null ? reg.getNroRectificacion() : "";
                        fechaRect = reg.getFecha() != null ? reg.getFecha() : "";
                        constancia = reg.getNroOrden() != null ? reg.getNroOrden() : "";
                    }
                    String[] declaracion = { "", nroRect, fechaRect, constancia };
                    for (int i = 0; i < declaracion.length; i++) {
                        Cell cd = row.createCell(3 + i);
                        cd.setCellStyle(centerStyle);
                        cd.setCellValue(declaracion[i]);
                    }

                    for (int i = 0; i < cols.size(); i++) {
                        AnualCol col = cols.get(i);
                        double v = col.getter().applyAsDouble(d);
                        Cell cell = row.createCell(ANUAL_FIRST_NUM_COL + i);
                        if (col.tasa()) {
                            cell.setCellStyle(tasaStyle);
                            cell.setCellValue(v);
                            continue;
                        }
                        XSSFCellStyle[] st = col.strong() ? (v < 0 ? strongNeg : strongPos)
                                : (v < 0 ? generalNeg : generalPos);
                        writeAnualNumber(cell, v, st);
                        sums[i] += v;
                    }
                }

                // ---- Fila de totales del año ----
                String titulo = anios.size() > 1 ? "SUB TOTAL AÑO " + anio : "T O T A L E S";
                writeAnualFooter(sheet, rowNum, titulo, sums, cols, subStyle, totalStyles);
                for (int i = 0; i < sums.length; i++) {
                    grand[i] += sums[i];
                }
                rowNum += 2; // fila en blanco antes del siguiente año
            }

            if (anios.size() > 1) {
                writeAnualFooter(sheet, rowNum, "T O T A L E S", grand, cols, subStyle, totalStyles);
            }

            sheet.setColumnWidth(0, 12 * 256);
            sheet.setColumnWidth(1, 10 * 256);
            sheet.setColumnWidth(2, 8 * 256);
            sheet.setColumnWidth(3, 7 * 256);
            sheet.setColumnWidth(4, 7 * 256);
            sheet.setColumnWidth(5, 12 * 256);
            sheet.setColumnWidth(6, 12 * 256);
            for (int c = ANUAL_FIRST_NUM_COL; c <= ANUAL_LAST_COL; c++) {
                sheet.setColumnWidth(c, 13 * 256);
            }

            wb.write(out);
            wb.close();
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Error generating PDT 621 Anual Excel", e);
            throw new RuntimeException("Error generando reporte anual de PDT 621", e);
        }
    }

    private void writeAnualFooter(Sheet sheet, int rowNum, String titulo, double[] values, List<AnualCol> cols,
            XSSFCellStyle titleStyle, XSSFCellStyle[] totalStyles) {
        Row footer = sheet.createRow(rowNum);
        anualMerged(sheet, rowNum, rowNum, 0, 6, titulo, titleStyle);
        for (int i = 0; i < cols.size(); i++) {
            Cell cell = footer.createCell(ANUAL_FIRST_NUM_COL + i);
            if (cols.get(i).tasa()) {
                cell.setCellStyle(titleStyle);
                cell.setCellValue("");
            } else {
                writeAnualNumber(cell, values[i], totalStyles);
            }
        }
    }

    /** Escribe un número eligiendo el formato igual que el reporte legacy (entero, decimal, o menor a 1). */
    private void writeAnualNumber(Cell cell, double v, XSSFCellStyle[] st) {
        if (v % 1 == 0) {
            cell.setCellStyle(st[0]);
        } else if (v < 1) {
            cell.setCellStyle(st[2]);
        } else {
            cell.setCellStyle(st[1]);
        }
        cell.setCellValue(v);
    }

    private XSSFFont anualFont(XSSFWorkbook wb, XSSFColor color, int size, boolean bold) {
        XSSFFont f = wb.createFont();
        f.setFontName("Arial");
        f.setFontHeightInPoints((short) size);
        f.setBold(bold);
        f.setColor(color);
        return f;
    }

    private XSSFCellStyle anualStyle(XSSFWorkbook wb, HorizontalAlignment align, XSSFColor bg, XSSFFont font,
            String format) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setAlignment(align);
        s.setFillForegroundColor(bg);
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setFont(font);
        s.setBorderTop(BorderStyle.THIN);
        s.setBorderBottom(BorderStyle.THIN);
        s.setBorderLeft(BorderStyle.THIN);
        s.setBorderRight(BorderStyle.THIN);
        short grey = IndexedColors.GREY_25_PERCENT.getIndex();
        s.setTopBorderColor(grey);
        s.setBottomBorderColor(grey);
        s.setLeftBorderColor(grey);
        s.setRightBorderColor(grey);
        if (format != null) {
            s.setDataFormat(wb.createDataFormat().getFormat(format));
        }
        return s;
    }

    /** Estilos numéricos [entero, decimal, menor-a-1] para un fondo y fuente dados. */
    private XSSFCellStyle[] anualNumStyles(XSSFWorkbook wb, XSSFColor bg, XSSFFont font) {
        return new XSSFCellStyle[] {
                anualStyle(wb, HorizontalAlignment.RIGHT, bg, font, "#,###,###"),
                anualStyle(wb, HorizontalAlignment.RIGHT, bg, font, "#,###,###.##"),
                anualStyle(wb, HorizontalAlignment.RIGHT, bg, font, "0.##") };
    }

    /** Combina un rango, escribe el texto en su primera celda y aplica el estilo (con bordes) a todo el rango. */
    private void anualMerged(Sheet sheet, int r1, int r2, int c1, int c2, String text, XSSFCellStyle style) {
        for (int r = r1; r <= r2; r++) {
            Row row = sheet.getRow(r) != null ? sheet.getRow(r) : sheet.createRow(r);
            for (int c = c1; c <= c2; c++) {
                Cell cell = row.getCell(c) != null ? row.getCell(c) : row.createCell(c);
                cell.setCellStyle(style);
            }
        }
        sheet.getRow(r1).getCell(c1).setCellValue(text != null ? text : "");
        if (r1 != r2 || c1 != c2) {
            sheet.addMergedRegion(new CellRangeAddress(r1, r2, c1, c2));
        }
    }

    private void addInfoRow(Sheet sheet, int rowNum, String label, String val, CellStyle lblStyle, CellStyle valStyle) {
        Row row = sheet.createRow(rowNum);
        Cell cL = row.createCell(0);
        cL.setCellStyle(lblStyle);
        cL.setCellValue(label);

        Cell cV = row.createCell(1);
        cV.setCellStyle(valStyle);
        cV.setCellValue(val);
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 1, 3));
    }

    private void addCalcRow(Sheet sheet, int rowNum, String label, Double base, Double tributo, CellStyle lblStyle,
            CellStyle numStyle) {
        Row row = sheet.createRow(rowNum);
        Cell cL = row.createCell(0);
        cL.setCellStyle(lblStyle);
        cL.setCellValue(label);
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 2));

        Cell cB = row.createCell(3);
        cB.setCellStyle(numStyle);
        if (base != null) {
            cB.setCellValue(base);
        } else {
            cB.setCellValue("");
        }

        Cell cT = row.createCell(4);
        cT.setCellStyle(numStyle);
        if (tributo != null) {
            cT.setCellValue(tributo);
        } else {
            cT.setCellValue("");
        }
    }

    private void writeNumber(Cell cell, Double val, CellStyle posStyle, CellStyle negStyle) {
        if (val == null || val == 0.0) {
            cell.setCellStyle(posStyle);
            cell.setCellValue(0);
        } else if (val < 0) {
            cell.setCellStyle(negStyle);
            cell.setCellValue(Math.round(val));
        } else {
            cell.setCellStyle(posStyle);
            cell.setCellValue(Math.round(val));
        }
    }
}
