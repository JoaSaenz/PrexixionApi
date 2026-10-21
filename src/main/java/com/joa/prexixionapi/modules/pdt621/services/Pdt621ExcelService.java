package com.joa.prexixionapi.modules.pdt621.services;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.joa.prexixionapi.modules.pdt621.dto.Pdt621DTO;
import com.joa.prexixionapi.modules.pdt621.dto.Pdt621FilterRequestDTO;
import com.joa.prexixionapi.modules.pdt621.dto.Pdt621ListDTO;
import com.joa.prexixionapi.modules.pdt621.dto.Pdt621RegistroDTO;
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
