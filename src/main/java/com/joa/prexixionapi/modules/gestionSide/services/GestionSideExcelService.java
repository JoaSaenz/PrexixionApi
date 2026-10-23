package com.joa.prexixionapi.modules.gestionSide.services;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.joa.prexixionapi.modules.gestionSide.dto.GestionSideListDTO;
import com.joa.prexixionapi.modules.gestionSide.dto.GestionSideRequest;
import com.joa.prexixionapi.modules.gestionSide.repositories.GestionSideRepository;
import com.joa.prexixionapi.utils.PoiUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class GestionSideExcelService {

    private final GestionSideRepository gestionSideRepository;

    public byte[] excel(GestionSideRequest req) {
        List<GestionSideListDTO> list = gestionSideRepository.list(req);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             XSSFWorkbook wb = new XSSFWorkbook()) {

            DefaultIndexedColorMap colorMap = new DefaultIndexedColorMap();

            // Colores basados en GestionNegocioController
            XSSFColor WHITE = new XSSFColor(new java.awt.Color(255, 255, 255), colorMap);
            XSSFColor BLACK = new XSSFColor(new java.awt.Color(0, 0, 0), colorMap);
            XSSFColor SKYBLUE_GREY = new XSSFColor(new java.awt.Color(217, 225, 242), colorMap);
            XSSFColor GREEN = new XSSFColor(new java.awt.Color(0, 204, 0), colorMap);
            XSSFColor RED = new XSSFColor(new java.awt.Color(192, 0, 0), colorMap);

            // Fuentes Aptos Narrow
            XSSFFont cabeceraFont = PoiUtils.fuenteAN(wb, BLACK, 11, true);
            XSSFFont subCabeceraFont = PoiUtils.fuenteAN(wb, BLACK, 8, true);
            XSSFFont dataFont = PoiUtils.fuenteAN(wb, BLACK, 8, false);
            XSSFFont dataFontRed = PoiUtils.fuenteAN(wb, RED, 8, false);
            XSSFFont dataFontGreen = PoiUtils.fuenteAN(wb, GREEN, 8, false);

            DataFormat format = wb.createDataFormat();

            // Estilos de celdas
            XSSFCellStyle cabeceraStyle = PoiUtils.createCellStyle(wb, HorizontalAlignment.CENTER, SKYBLUE_GREY, cabeceraFont);
            PoiUtils.addBorders(cabeceraStyle, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);

            XSSFCellStyle subCabeceraStyle = PoiUtils.createCellStyle(wb, HorizontalAlignment.CENTER, SKYBLUE_GREY, subCabeceraFont);
            PoiUtils.addBorders(subCabeceraStyle, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);

            XSSFCellStyle dataCenterStyle = PoiUtils.createCellStyle(wb, HorizontalAlignment.CENTER, WHITE, dataFont);
            PoiUtils.addBorders(dataCenterStyle, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);

            XSSFCellStyle dataLeftStyle = PoiUtils.createCellStyle(wb, HorizontalAlignment.LEFT, WHITE, dataFont);
            PoiUtils.addBorders(dataLeftStyle, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);

            XSSFCellStyle dataRightStyle = PoiUtils.createCellStyle(wb, HorizontalAlignment.RIGHT, WHITE, dataFont);
            PoiUtils.addBorders(dataRightStyle, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);
            dataRightStyle.setDataFormat(format.getFormat("#,##0.00"));

            XSSFCellStyle dataCenterStyleFontRed = PoiUtils.createCellStyle(wb, HorizontalAlignment.CENTER, WHITE, dataFontRed);
            PoiUtils.addBorders(dataCenterStyleFontRed, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);

            XSSFCellStyle dataCenterStyleFontGreen = PoiUtils.createCellStyle(wb, HorizontalAlignment.CENTER, WHITE, dataFontGreen);
            PoiUtils.addBorders(dataCenterStyleFontGreen, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);

            // Única hoja
            XSSFSheet sheet = wb.createSheet("REPORTE");

            int rowNum = 0;

            // CUADRO 1: RESUMEN DEL ESTADO DEL MÓDULO (Filas 0 a 4)
            int colNumRes = 4; // Columnas E y F

            Row resCabecera = sheet.createRow(rowNum++);
            Cell resumenEstado = resCabecera.createCell(colNumRes);
            resumenEstado.setCellValue("ESTADO");
            resumenEstado.setCellStyle(subCabeceraStyle);
            Cell resumenCantidad = resCabecera.createCell(colNumRes + 1);
            resumenCantidad.setCellValue("CANTIDAD");
            resumenCantidad.setCellStyle(subCabeceraStyle);

            Row resPendiente = sheet.createRow(rowNum++);
            Cell cellPend = resPendiente.createCell(colNumRes);
            cellPend.setCellValue("PENDIENTE");
            cellPend.setCellStyle(subCabeceraStyle);
            Cell resumenPendienteCantidad = resPendiente.createCell(colNumRes + 1);
            resumenPendienteCantidad.setCellStyle(dataCenterStyle);

            Row resProceso = sheet.createRow(rowNum++);
            Cell cellProc = resProceso.createCell(colNumRes);
            cellProc.setCellValue("PROCESO");
            cellProc.setCellStyle(subCabeceraStyle);
            Cell resumenProcesoCantidad = resProceso.createCell(colNumRes + 1);
            resumenProcesoCantidad.setCellStyle(dataCenterStyle);

            Row resCerrado = sheet.createRow(rowNum++);
            Cell cellCerr = resCerrado.createCell(colNumRes);
            cellCerr.setCellValue("CERRADO");
            cellCerr.setCellStyle(subCabeceraStyle);
            Cell resumenCerradoCantidad = resCerrado.createCell(colNumRes + 1);
            resumenCerradoCantidad.setCellStyle(dataCenterStyle);

            Row resTotal = sheet.createRow(rowNum++);
            Cell cellTot = resTotal.createCell(colNumRes);
            cellTot.setCellValue("TOTAL");
            cellTot.setCellStyle(subCabeceraStyle);
            Cell resumenTotalCantidad = resTotal.createCell(colNumRes + 1);
            resumenTotalCantidad.setCellStyle(subCabeceraStyle);

            rowNum++; // Fila 5 en blanco

            // CUADRO 2: CABECERA Y TABLA DE DATOS
            Row cabecera = sheet.createRow(rowNum++);
            Cell cellCabecera = cabecera.createCell(0);
            cellCabecera.setCellStyle(cabeceraStyle);
            cellCabecera.setCellValue("REPORTE GENERAL");
            sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 11));

            Row subCabecera = sheet.createRow(rowNum++);
            String[] headers = {
                "id", "GRUPO E", "ESTADO C", "RUC", "RAZÓN SOCIAL", 
                "ESTADO", "AREA", "SERVICIO", "DETALLE", "F. RECIBIDO", 
                "F. ENTREGA", "IMPORTE"
            };
            for (int i = 0; i < headers.length; i++) {
                Cell cell = subCabecera.createCell(i);
                cell.setCellStyle(subCabeceraStyle);
                cell.setCellValue(headers[i]);
            }

            // Fila en blanco (fila 9 en Excel) para alojar las flechas de filtro, idéntico a Gestión Negocios
            int inicioFilt = rowNum;
            sheet.createRow(inicioFilt);
            rowNum++;

            int cantPendiente = 0;
            int cantProceso = 0;
            int cantCerrado = 0;
            int contadorId = 1;

            for (GestionSideListDTO item : list) {
                Row row = sheet.createRow(rowNum++);
                int col = 0;

                // Conteo validando por idEstado (1: PENDIENTE, 2: PROCESO, 3: CERRADO)
                if (item.getIdEstado() != null) {
                    switch (item.getIdEstado()) {
                        case 1:
                            cantPendiente++;
                            break;
                        case 2:
                            cantProceso++;
                            break;
                        case 3:
                            cantCerrado++;
                            break;
                    }
                }

                // Col 0: id (contador secuencial con estilo subCabecera)
                Cell cellId = row.createCell(col++);
                cellId.setCellStyle(subCabeceraStyle);
                cellId.setCellValue(contadorId++);

                // Col 1: GRUPO E
                Cell cellGrupo = row.createCell(col++);
                cellGrupo.setCellStyle(dataLeftStyle);
                cellGrupo.setCellValue(item.getDescGrupoEconomico() != null ? item.getDescGrupoEconomico() : "");

                // Col 2: ESTADO C
                Cell cellEstCli = row.createCell(col++);
                cellEstCli.setCellStyle(dataLeftStyle);
                cellEstCli.setCellValue(item.getDescEstadoCliente() != null ? item.getDescEstadoCliente() : "");

                // Col 3: RUC
                Cell cellRuc = row.createCell(col++);
                cellRuc.setCellStyle(dataCenterStyle);
                cellRuc.setCellValue(item.getIdCliente() != null ? item.getIdCliente() : "");

                // Col 4: RAZÓN SOCIAL
                Cell cellRazon = row.createCell(col++);
                cellRazon.setCellStyle(dataLeftStyle);
                cellRazon.setCellValue(item.getRazonSocial() != null ? item.getRazonSocial() : "");

                // Col 5: ESTADO
                Cell cellEstado = row.createCell(col++);
                if (item.getIdEstado() != null && item.getIdEstado() == 1) {
                    cellEstado.setCellStyle(dataCenterStyleFontRed);
                } else if (item.getIdEstado() != null && item.getIdEstado() == 3) {
                    cellEstado.setCellStyle(dataCenterStyleFontGreen);
                } else {
                    cellEstado.setCellStyle(dataCenterStyle);
                }
                cellEstado.setCellValue(item.getDescEstado() != null ? item.getDescEstado() : "");

                // Col 6: AREA
                Cell cellArea = row.createCell(col++);
                cellArea.setCellStyle(dataLeftStyle);
                cellArea.setCellValue(item.getDescArea() != null ? item.getDescArea() : "");

                // Col 7: SERVICIO
                Cell cellServ = row.createCell(col++);
                cellServ.setCellStyle(dataLeftStyle);
                cellServ.setCellValue(item.getDescServicio() != null ? item.getDescServicio() : "");

                // Col 8: DETALLE
                Cell cellDet = row.createCell(col++);
                cellDet.setCellStyle(dataLeftStyle);
                cellDet.setCellValue(item.getDetalle() != null ? item.getDetalle() : "");

                // Col 9: F. RECIBIDO
                Cell cellFRec = row.createCell(col++);
                cellFRec.setCellStyle(dataCenterStyle);
                cellFRec.setCellValue(item.getFechaRecibido() != null ? item.getFechaRecibido() : "");

                // Col 10: F. ENTREGA
                Cell cellFEnt = row.createCell(col++);
                cellFEnt.setCellStyle(dataCenterStyle);
                cellFEnt.setCellValue(item.getFechaEntrega() != null ? item.getFechaEntrega() : "");

                // Col 11: IMPORTE
                Cell cellImp = row.createCell(col++);
                cellImp.setCellStyle(dataRightStyle);
                if (item.getImporte() != null) {
                    cellImp.setCellValue(item.getImporte().doubleValue());
                } else {
                    cellImp.setCellValue(0.00);
                }
            }

            // Establecer totales en Cuadro 1 (Resumen)
            resumenPendienteCantidad.setCellValue(cantPendiente);
            resumenProcesoCantidad.setCellValue(cantProceso);
            resumenCerradoCantidad.setCellValue(cantCerrado);
            resumenTotalCantidad.setCellValue(cantPendiente + cantProceso + cantCerrado);

            // AutoFilter
            int finFilt = Math.max(rowNum - 1, inicioFilt);
            if (finFilt >= inicioFilt) {
                sheet.setAutoFilter(new CellRangeAddress(inicioFilt, finFilt, 0, 11));
            }

            // Inmovilizar paneles (Freeze Pane) hasta la fila 9 (filas 1 a 9 fijas: resumen, títulos y fila de filtros)
            sheet.createFreezePane(0, 9);

            // Autoajustar ancho de columnas
            for (int c = 0; c < 12; c++) {
                sheet.autoSizeColumn(c);
            }

            wb.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Error al generar el reporte Excel de Gestión Side", e);
            throw new RuntimeException("Error al generar el reporte Excel de Gestión Side: " + e.getMessage(), e);
        }
    }
}
