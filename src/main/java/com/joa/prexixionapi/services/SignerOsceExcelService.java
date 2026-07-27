package com.joa.prexixionapi.services;

import com.joa.prexixionapi.dto.SignerOsceListDTO;
import com.joa.prexixionapi.dto.SignerOsceRequest;
import com.joa.prexixionapi.utils.ExcelStyleManager;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class SignerOsceExcelService {

    private final SignerOsceService signerOsceService;

    public SignerOsceExcelService(SignerOsceService signerOsceService) {
        this.signerOsceService = signerOsceService;
    }

    public byte[] exportarExcel(SignerOsceRequest request) {
        List<SignerOsceListDTO> list = signerOsceService.listExcel(request);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             Workbook wb = new XSSFWorkbook()) {

            ExcelStyleManager styleManager = new ExcelStyleManager((XSSFWorkbook) wb);

            // Estilos equivalentes a los del legacy
            CellStyle fondoBlackStyle = styleManager.getFondoBlackStyle();
            CellStyle dataStyle2 = styleManager.getDataCenterBoldStyle();
            CellStyle fondoGreyStyleLeft = styleManager.getDataLeftStyle();
            CellStyle dataGreenStyle = styleManager.getDataStatusStyle(ExcelStyleManager.GREEN_RGB);
            CellStyle dataRedStyle = styleManager.getDataStatusStyle(ExcelStyleManager.RED_RGB);
            CellStyle dataLightBlueStyle = styleManager.getDataStatusStyle(ExcelStyleManager.LIGHT_BLUE_RGB);
            CellStyle subHeaderStyle = styleManager.getSubHeaderStyle();
            CellStyle cabeceraStyle = styleManager.getGenericStyle(ExcelStyleManager.GERENCIA_BLUE_RGB, ExcelStyleManager.WHITE_RGB, 17, true, HorizontalAlignment.CENTER);

            int contPendiente = 0;
            int contProceso = 0;
            int contTerminado = 0;

            String sheetName = "REPORTE";
            Sheet sheet = wb.createSheet(sheetName);

            int rowNum = 0;
            int colNum = 0;

            // RESUMEN
            Row cabeceraResumen = sheet.createRow(rowNum);
            colNum = CellReference.convertColStringToIndex("L");
            createCell(cabeceraResumen, colNum++, "ESTADO", fondoBlackStyle);
            createCell(cabeceraResumen, colNum, "CANTIDAD", fondoBlackStyle);
            rowNum++;

            Row rowPendiente = sheet.createRow(rowNum);
            colNum = CellReference.convertColStringToIndex("L");
            createCell(rowPendiente, colNum++, "PENDIENTE", fondoBlackStyle);
            Cell resumenPendienteCantidad = rowPendiente.createCell(colNum);
            resumenPendienteCantidad.setCellStyle(dataStyle2);
            rowNum++;

            Row rowProceso = sheet.createRow(rowNum);
            colNum = CellReference.convertColStringToIndex("L");
            createCell(rowProceso, colNum++, "PROCESO", fondoBlackStyle);
            Cell resumenProcesoCantidad = rowProceso.createCell(colNum);
            resumenProcesoCantidad.setCellStyle(dataStyle2);
            rowNum++;

            Row rowTerminado = sheet.createRow(rowNum);
            colNum = CellReference.convertColStringToIndex("L");
            createCell(rowTerminado, colNum++, "TERMINADO", fondoBlackStyle);
            Cell resumenTerminadoCantidad = rowTerminado.createCell(colNum);
            resumenTerminadoCantidad.setCellStyle(dataStyle2);
            rowNum++;

            Row rowTotal = sheet.createRow(rowNum);
            colNum = CellReference.convertColStringToIndex("L");
            createCell(rowTotal, colNum++, "TOTAL", fondoBlackStyle);
            Cell resumenTotalCantidad = rowTotal.createCell(colNum);
            resumenTotalCantidad.setCellStyle(fondoBlackStyle);
            rowNum++;
            rowNum += 2; // saltar unas filas

            // CABECERA PRINCIPAL
            colNum = 0;
            Row cabecera = sheet.createRow(rowNum);
            cabecera.setHeightInPoints(sheet.getDefaultRowHeightInPoints() * 3);
            Cell cellCabecera = cabecera.createCell(colNum);
            cellCabecera.setCellStyle(cabeceraStyle);
            cellCabecera.setCellValue("SIGNERS OSCE");
            sheet.addMergedRegion(CellRangeAddress.valueOf("A8:P8"));
            rowNum++;

            colNum = 0;
            Row subHeader1 = sheet.createRow(rowNum);
            String[] headers = {
                    "N°", "GRUPO E", "ESTADO", "RUC", "Y", "RAZÓN SOCIAL",
                    "MAIL", "USUARIO", "CLAVE", "RNP", "ALTA", "ESTADO O",
                    "BYS", "EYC", "TRÁMITE", "OBSERVACIÓN"
            };

            for (String header : headers) {
                createCell(subHeader1, colNum++, header, subHeaderStyle);
            }
            int inicioFilt = rowNum;
            rowNum++;

            // DATOS
            int i = 1;
            for (SignerOsceListDTO obj : list) {
                Row data = sheet.createRow(rowNum);
                colNum = 0;

                createCell(data, colNum++, String.valueOf(i), subHeaderStyle);
                createCell(data, colNum++, obj.getDescGrupoEconomico(), dataStyle2);
                createCell(data, colNum++, obj.getDescEstadoCliente(), dataStyle2);
                createCell(data, colNum++, obj.getIdCliente(), dataStyle2);
                createCell(data, colNum++, obj.getY(), dataStyle2);
                createCell(data, colNum++, obj.getRazonSocial(), fondoGreyStyleLeft);

                // MAIL
                Cell dataMail = data.createCell(colNum++);
                if (obj.getMail() != null && obj.getMail() == 1) {
                    dataMail.setCellStyle(dataGreenStyle);
                    dataMail.setCellValue("SI");
                } else {
                    dataMail.setCellStyle(dataRedStyle);
                    dataMail.setCellValue("NO");
                }

                createCell(data, colNum++, obj.getUsuario(), fondoGreyStyleLeft);
                createCell(data, colNum++, obj.getClave(), fondoGreyStyleLeft);
                createCell(data, colNum++, obj.getRnp(), fondoGreyStyleLeft);
                createCell(data, colNum++, obj.getFeAlta(), fondoGreyStyleLeft);

                // ESTADO O
                Cell dataEstadoOsce = data.createCell(colNum++);
                dataEstadoOsce.setCellValue(obj.getDescEstadoOsce() != null ? obj.getDescEstadoOsce() : "");
                if (obj.getIdEstadoOsce() != null) {
                    switch (obj.getIdEstadoOsce()) {
                        case 1:
                            dataEstadoOsce.setCellStyle(dataRedStyle);
                            contPendiente++;
                            break;
                        case 2:
                            dataEstadoOsce.setCellStyle(dataLightBlueStyle);
                            contProceso++;
                            break;
                        case 3:
                            dataEstadoOsce.setCellStyle(dataGreenStyle);
                            contTerminado++;
                            break;
                        default:
                            dataEstadoOsce.setCellStyle(dataStyle2);
                    }
                } else {
                    dataEstadoOsce.setCellStyle(dataStyle2);
                }

                // BYS
                Cell dataByS = data.createCell(colNum++);
                if (obj.getTipoByS() != null && obj.getTipoByS().equals("BYS")) {
                    dataByS.setCellStyle(dataGreenStyle);
                    dataByS.setCellValue("✓");
                } else {
                    dataByS.setCellStyle(dataRedStyle);
                    dataByS.setCellValue("");
                }

                // EYC
                Cell dataEyC = data.createCell(colNum++);
                if (obj.getTipoEyC() != null && obj.getTipoEyC().equals("EYC")) {
                    dataEyC.setCellStyle(dataGreenStyle);
                    dataEyC.setCellValue("✓ " + (obj.getCategoriaEyC() != null ? obj.getCategoriaEyC() : ""));
                } else {
                    dataEyC.setCellStyle(dataRedStyle);
                    dataEyC.setCellValue("");
                }

                createCell(data, colNum++, obj.getDescTramite(), fondoGreyStyleLeft);
                createCell(data, colNum++, obj.getObservacion(), fondoGreyStyleLeft);

                rowNum++;
                i++;
            }

            // TOTALES RESUMEN
            resumenPendienteCantidad.setCellValue(contPendiente);
            resumenProcesoCantidad.setCellValue(contProceso);
            resumenTerminadoCantidad.setCellValue(contTerminado);
            resumenTotalCantidad.setCellValue(contPendiente + contProceso + contTerminado);

            int finFilt = rowNum - 1;
            sheet.setAutoFilter(new CellRangeAddress(inicioFilt, finFilt, 0, 15));
            sheet.createFreezePane(0, inicioFilt + 1);

            // AUTO SIZE
            for (int j = 0; j < headers.length; j++) {
                sheet.autoSizeColumn(j);
            }

            wb.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el excel de Signer OSCE", e);
        }
    }

    private void createCell(Row row, int colIdx, String value, CellStyle style) {
        Cell cell = row.createCell(colIdx);
        cell.setCellValue(value != null ? value : "");
        cell.setCellStyle(style);
    }
}
