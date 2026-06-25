package com.joa.prexixionapi.services;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joa.prexixionapi.dto.ReunionDTO;
import com.joa.prexixionapi.dto.ReunionExcelDTO;
import com.joa.prexixionapi.dto.ReunionDataTablesRequest;
import com.joa.prexixionapi.repositories.ReunionRepository;
import com.joa.prexixionapi.utils.ExcelStyleManager;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

@Service
public class ReunionReportService {

    @Autowired
    private ReunionRepository reunionRepository;

    public byte[] generateExcel(ReunionDataTablesRequest req) throws Exception {
        List<ReunionExcelDTO> list = reunionRepository.listExcel(req);

        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("Reuniones");

            int contProgramada = 0;
            int contReprogramada = 0;
            int contCerrada = 0;

            ExcelStyleManager styleManager = new ExcelStyleManager((XSSFWorkbook) wb);

            // Cabeceras
            CellStyle cabeceraStyle = styleManager.getHeaderStyle(16);
            CellStyle subHeaderStyle = styleManager.getSubHeaderStyle(10);

            // Data Regular
            CellStyle dataLeftStyle = styleManager.getDataLeftStyle(10);
            CellStyle dataCenterStyle = styleManager.getDataCenterStyle(10);

            // Data Estados Especiales
            CellStyle dataGreenStyle = styleManager.getDataStatusStyle(ExcelStyleManager.GREEN_RGB, 10);
            CellStyle dataRedStyle = styleManager.getDataStatusStyle(ExcelStyleManager.RED_RGB, 10);
            CellStyle dataLightBlueStyle = styleManager.getDataStatusStyle(ExcelStyleManager.BLUE_RGB, 10);

            int rowNum = 0;
            int colNum = 0;

            // CABECERA RESUMEN
            Row cabeceraResumen = sheet.createRow(rowNum);
            colNum = CellReference.convertColStringToIndex("E");

            Cell resumenEstado = cabeceraResumen.createCell(colNum);
            resumenEstado.setCellValue("ESTADO");
            resumenEstado.setCellStyle(subHeaderStyle);
            colNum++;
            Cell resumenCantidad = cabeceraResumen.createCell(colNum);
            resumenCantidad.setCellValue("CANTIDAD");
            resumenCantidad.setCellStyle(subHeaderStyle);
            rowNum++;

            Row rowProgramada = sheet.createRow(rowNum);
            colNum = CellReference.convertColStringToIndex("E");
            Cell resumenProgramada = rowProgramada.createCell(colNum);
            resumenProgramada.setCellValue("PROGRAMADA");
            resumenProgramada.setCellStyle(subHeaderStyle);
            colNum++;
            Cell resumenProgramadaCantidad = rowProgramada.createCell(colNum);
            resumenProgramadaCantidad.setCellStyle(dataCenterStyle);
            rowNum++;

            Row rowReprogramada = sheet.createRow(rowNum);
            colNum = CellReference.convertColStringToIndex("E");
            Cell resumenReprogramada = rowReprogramada.createCell(colNum);
            resumenReprogramada.setCellValue("REPROGRAMADA");
            resumenReprogramada.setCellStyle(subHeaderStyle);
            colNum++;
            Cell resumenReprogramadaCantidad = rowReprogramada.createCell(colNum);
            resumenReprogramadaCantidad.setCellStyle(dataCenterStyle);
            rowNum++;

            Row rowCerrada = sheet.createRow(rowNum);
            colNum = CellReference.convertColStringToIndex("E");
            Cell resumenCerrada = rowCerrada.createCell(colNum);
            resumenCerrada.setCellValue("CERRADA");
            resumenCerrada.setCellStyle(subHeaderStyle);
            colNum++;
            Cell resumenCerradaCantidad = rowCerrada.createCell(colNum);
            resumenCerradaCantidad.setCellStyle(dataCenterStyle);
            rowNum++;

            Row rowTotal = sheet.createRow(rowNum);
            colNum = CellReference.convertColStringToIndex("E");
            Cell resumenTotal = rowTotal.createCell(colNum);
            resumenTotal.setCellValue("TOTAL");
            resumenTotal.setCellStyle(subHeaderStyle);
            colNum++;
            Cell resumenTotalCantidad = rowTotal.createCell(colNum);
            resumenTotalCantidad.setCellStyle(subHeaderStyle);
            rowNum += 2;

            // CABECERA
            colNum = 0;
            Row header = sheet.createRow(rowNum);
            header.setHeightInPoints((float) 45.00);

            Cell header0 = header.createCell(0);
            header0.setCellStyle(cabeceraStyle);
            header0.setCellValue(" REUNIONES ");
            sheet.addMergedRegion(CellRangeAddress.valueOf("A7:M7"));

            rowNum++;
            colNum = 0;
            Row subheader1 = sheet.createRow(rowNum);

            String[] headers = { "ESTADO", "RUC", "RAZÓN SOCIAL", "TEMAS", "TIPO", "FECHA", "HORA I.", "HORA F.",
                    "P. EXTERNOS.", "P. INTERNOS", "ÁREAS", "ACUERDOS", "OTROS" };
            for (String h : headers) {
                Cell cell = subheader1.createCell(colNum++);
                cell.setCellStyle(subHeaderStyle);
                cell.setCellValue(h);
            }

            rowNum++;
            Row filter = sheet.createRow(rowNum);
            filter.setHeightInPoints((float) 12.00);
            int inicioFilter = rowNum;
            rowNum++;

            // DATA
            for (ReunionExcelDTO obj : list) {
                Row data = sheet.createRow(rowNum);
                colNum = 0;

                Cell dataEstado = data.createCell(colNum++);
                dataEstado.setCellValue(obj.getEstadoDescripcion());
                switch (obj.getEstadoId()) {
                    case 1:
                        dataEstado.setCellStyle(dataGreenStyle);
                        contProgramada++;
                        break;
                    case 2:
                        dataEstado.setCellStyle(dataRedStyle);
                        contReprogramada++;
                        break;
                    case 3:
                        dataEstado.setCellStyle(dataLightBlueStyle);
                        contCerrada++;
                        break;
                    default:
                        dataEstado.setCellStyle(dataCenterStyle);
                        break;
                }

                Cell dataRuc = data.createCell(colNum++);
                dataRuc.setCellValue(obj.getClienteRuc());
                dataRuc.setCellStyle(dataCenterStyle);

                Cell dataRazonSocial = data.createCell(colNum++);
                dataRazonSocial.setCellValue(obj.getClienteRazonSocial());
                dataRazonSocial.setCellStyle(dataLeftStyle);

                Cell dataTemas = data.createCell(colNum++);
                String temas = obj.getTemas();
                dataTemas.setCellValue(temas);
                dataTemas.setCellStyle(dataLeftStyle);

                Cell dataTipo = data.createCell(colNum++);
                dataTipo.setCellValue(obj.getTipo());
                dataTipo.setCellStyle(dataLeftStyle);

                Cell dataFecha = data.createCell(colNum++);
                dataFecha.setCellValue(obj.getFecha());
                dataFecha.setCellStyle(dataCenterStyle);

                Cell dataHoraI = data.createCell(colNum++);
                dataHoraI.setCellValue(obj.getHoraI());
                dataHoraI.setCellStyle(dataCenterStyle);

                Cell dataHoraF = data.createCell(colNum++);
                dataHoraF.setCellValue(obj.getHoraF());
                dataHoraF.setCellStyle(dataCenterStyle);

                Cell dataParticipantesExternos = data.createCell(colNum++);
                String participantesExternos = obj.getParticipantesExternos();
                dataParticipantesExternos.setCellValue(participantesExternos);
                dataParticipantesExternos.setCellStyle(dataLeftStyle);

                Cell dataParticipantesInternos = data.createCell(colNum++);
                String participantesInternos = obj.getParticipantesInternos();
                dataParticipantesInternos.setCellValue(participantesInternos);
                dataParticipantesInternos.setCellStyle(dataLeftStyle);

                Cell dataAreas = data.createCell(colNum++);
                String areas = obj.getAreas();
                dataAreas.setCellValue(areas);
                dataAreas.setCellStyle(dataLeftStyle);

                Cell dataAcuerdos = data.createCell(colNum++);
                String acuerdos = obj.getAcuerdos();
                dataAcuerdos.setCellValue(acuerdos);
                dataAcuerdos.setCellStyle(dataLeftStyle);

                Cell dataOtros = data.createCell(colNum++);
                dataOtros.setCellValue(obj.getOtros());
                dataOtros.setCellStyle(dataLeftStyle);

                rowNum++;
            }

            resumenProgramadaCantidad.setCellValue(contProgramada);
            resumenReprogramadaCantidad.setCellValue(contReprogramada);
            resumenCerradaCantidad.setCellValue(contCerrada);
            resumenTotalCantidad.setCellValue(contProgramada + contReprogramada + contCerrada);

            int finFilter = rowNum;
            sheet.setAutoFilter(new CellRangeAddress(inicioFilter, finFilter - 1, 0, 12));
            sheet.createFreezePane(0, 9);

            for (int contCol = 0; contCol < 13; contCol++) {
                sheet.autoSizeColumn(contCol);
            }

            wb.write(out);
            return out.toByteArray();
        }
    }

    public byte[] generatePdf(int idReunion) throws Exception {
        ReunionDTO obj = reunionRepository.getById(idReunion);
        if (obj == null) {
            throw new IllegalArgumentException("Reunion not found");
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 30, 30, 30, 80);
            PdfWriter writer = PdfWriter.getInstance(document, out);

            // Simple header footer for now, the real one requires real files
            class HeaderFooter extends PdfPageEventHelper {
                public void onEndPage(PdfWriter writer, Document document) {
                    PdfContentByte cb = writer.getDirectContent();
                    Phrase footer = new Phrase("Generated by Prexixion API",
                            new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 8));
                    ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                            footer,
                            (document.right() - document.left()) / 2 + document.leftMargin(),
                            document.bottom() - 10, 0);
                }
            }
            writer.setPageEvent(new HeaderFooter());

            document.open();

            com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,
                    16, com.itextpdf.text.Font.BOLD);
            Paragraph title = new Paragraph("ADVICE REUNIÓN", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            document.add(new Paragraph(
                    "Cliente: " + obj.getCliente().getRazonSocial() + " (" + obj.getCliente().getRuc() + ")"));
            document.add(new Paragraph("Fecha: " + obj.getFecha()));
            document.add(new Paragraph("Hora: " + obj.getHoraI() + " - " + obj.getHoraF()));
            document.add(new Paragraph("Tipo: " + obj.getTipo()));
            document.add(new Paragraph("Estado: " + obj.getEstado().getDescripcion()));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Temas Tratados:", new com.itextpdf.text.Font(
                    com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD)));
            for (var t : obj.getTemas()) {
                document.add(new Paragraph("- " + t.getTema() + " (Acuerdo: " + t.getAcuerdoTema() + ")"));
            }

            document.add(new Paragraph("\nAcuerdos Generales:", new com.itextpdf.text.Font(
                    com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD)));
            for (var a : obj.getAcuerdos()) {
                document.add(new Paragraph("- " + a.getDescripcion()));
            }

            document.add(new Paragraph("\nOtros:", new com.itextpdf.text.Font(
                    com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD)));
            document.add(new Paragraph(obj.getOtros()));

            document.close();
            return out.toByteArray();
        }
    }
}
