package com.joa.prexixionapi.services;

import com.joa.prexixionapi.dto.JobStatusLogProjection;
import com.joa.prexixionapi.dto.NotificacionProjection;
import com.joa.prexixionapi.entities.JobStatus;
import com.joa.prexixionapi.repositories.JobStatusLogRepository;
import com.joa.prexixionapi.repositories.JobStatusRepository;
import com.joa.prexixionapi.repositories.NotificacionRepository;
import com.joa.prexixionapi.utils.ExcelStyleManager;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SunatBuzonExcelService {

    private final JobStatusRepository jobStatusRepository;
    private final JobStatusLogRepository jobStatusLogRepository;
    private final NotificacionRepository notificacionRepository;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public byte[] exportarDetalleJobStatus(Long jobStatusId) {
        JobStatus job = jobStatusRepository.findById(jobStatusId)
                .orElseThrow(() -> new RuntimeException("JobStatus no encontrado: " + jobStatusId));

        List<JobStatusLogProjection> logs = jobStatusLogRepository.findLogsWithEstadoByJobStatus(jobStatusId);
        List<NotificacionProjection> notificaciones = notificacionRepository.findNotificacionesWithEstadoByJobStatus(jobStatusId);

        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ExcelStyleManager styleManager = new ExcelStyleManager(wb);

            // --- ESTILOS ---
            XSSFCellStyle estiloCabecera = styleManager.getHeaderStyle();
            XSSFCellStyle estiloSubCabecera = styleManager.getSubHeaderStyleBlue();
            XSSFCellStyle estiloDatoCentro = styleManager.getFondoWhiteStyleCenter();
            XSSFCellStyle estiloDatoIzquierda = styleManager.getFondoWhiteStyleLeft();

            // --- ESTILOS ADICIONALES ---
            XSSFCellStyle dataLeftStyleGreen = styleManager.getCustomStyle(
                    ExcelStyleManager.WHITE_RGB, ExcelStyleManager.GREEN_RGB, 9, false, 
                    HorizontalAlignment.LEFT, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);
            XSSFCellStyle dataLeftStyleRed = styleManager.getCustomStyle(
                    ExcelStyleManager.WHITE_RGB, ExcelStyleManager.RED_RGB, 9, false, 
                    HorizontalAlignment.LEFT, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);
            XSSFCellStyle dataLeftStyleBlue = styleManager.getCustomStyle(
                    ExcelStyleManager.WHITE_RGB, ExcelStyleManager.BLUE_RGB, 9, false, 
                    HorizontalAlignment.LEFT, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT);

            // --- HOJA 1: LOGS ---
            Sheet sheetLogs = wb.createSheet("Logs de Ejecución");
            int rowNum = 0;

            // Fila 1: Cabecera Principal
            Row cabeceraRow = sheetLogs.createRow(rowNum++);
            Cell cellCabecera = cabeceraRow.createCell(0);
            cellCabecera.setCellStyle(estiloCabecera);
            cellCabecera.setCellValue("SINCRONIZACIÓN IBOX - DETALLE");
            sheetLogs.addMergedRegion(new CellRangeAddress(0, 0, 0, 9));

            // Fila 2: Detalle de Ejecución
            Row detalleRow = sheetLogs.createRow(rowNum++);
            Cell cellDetalle = detalleRow.createCell(0);
            cellDetalle.setCellStyle(styleManager.getFondoLightBlueStyle());
            String info = "Fecha: " + (job.getHoraInicio() != null ? job.getHoraInicio().format(DATE_TIME_FORMATTER) : "-") 
                    + " | Estado: " + job.getEstado() + " | Mensaje: " + job.getMensaje();
            cellDetalle.setCellValue(info);
            sheetLogs.addMergedRegion(new CellRangeAddress(1, 1, 0, 9));

            // Fila 3: Headers
            Row headerRow = sheetLogs.createRow(rowNum++);
            String[] headers = {"N°", "ESTADO", "RUC", "Y", "Razón Social", "RESULTADO", "MENSAJE", "DURACIÓN (s)", "FECHA REVISIÓN", "NUEVAS NOTIF."};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(estiloSubCabecera);
            }

            int inicioFilt1 = rowNum;

            // Datos
            int contador = 1;
            for (JobStatusLogProjection log : logs) {
                Row dataRow = sheetLogs.createRow(rowNum++);
                int colNum = 0;
                
                Cell cellNum = dataRow.createCell(colNum++);
                cellNum.setCellValue(contador++);
                cellNum.setCellStyle(estiloDatoCentro);
                
                Cell cellEstado = dataRow.createCell(colNum++);
                cellEstado.setCellValue(log.getEstadoCliente() != null ? log.getEstadoCliente() : "");
                int estadoId1 = log.getIdEstado() != null ? log.getIdEstado() : -1;
                if (estadoId1 == 1) cellEstado.setCellStyle(dataLeftStyleGreen);
                else if (estadoId1 == 2) cellEstado.setCellStyle(dataLeftStyleRed);
                else if (estadoId1 == 3) cellEstado.setCellStyle(dataLeftStyleBlue);
                else cellEstado.setCellStyle(estiloDatoIzquierda);
                
                Cell cellRuc = dataRow.createCell(colNum++);
                cellRuc.setCellValue(log.getRuc());
                cellRuc.setCellStyle(estiloDatoIzquierda);
                
                Cell cellY = dataRow.createCell(colNum++);
                cellY.setCellValue(log.getY() != null ? log.getY() : "");
                cellY.setCellStyle(estiloDatoIzquierda);

                Cell cellRazonSocial = dataRow.createCell(colNum++);
                cellRazonSocial.setCellValue(log.getRazonSocial() != null ? log.getRazonSocial() : "");
                cellRazonSocial.setCellStyle(estiloDatoIzquierda);
                
                Cell cellRes = dataRow.createCell(colNum++);
                cellRes.setCellValue(log.getResultado());
                cellRes.setCellStyle(estiloDatoIzquierda);
                
                Cell cellMsj = dataRow.createCell(colNum++);
                cellMsj.setCellValue(log.getMensaje());
                cellMsj.setCellStyle(estiloDatoIzquierda);
                
                Cell cellDur = dataRow.createCell(colNum++);
                cellDur.setCellValue(log.getDuracionMs() != null ? TimeUnit.MILLISECONDS.toSeconds(log.getDuracionMs()) : 0);
                cellDur.setCellStyle(estiloDatoCentro);
                
                Cell cellFec = dataRow.createCell(colNum++);
                cellFec.setCellValue(log.getFechaRegistro() != null ? log.getFechaRegistro().format(DATE_TIME_FORMATTER) : "");
                cellFec.setCellStyle(estiloDatoIzquierda);
                
                Cell cellNuevas = dataRow.createCell(colNum++);
                cellNuevas.setCellValue(log.getNuevasNotificaciones() != null ? log.getNuevasNotificaciones() : 0);
                cellNuevas.setCellStyle(estiloDatoCentro);
            }

            int finFilt1 = rowNum - 1;
            sheetLogs.setAutoFilter(new CellRangeAddress(inicioFilt1, finFilt1, 0, 9));
            for (int i = 0; i < headers.length; i++) sheetLogs.autoSizeColumn(i);
            sheetLogs.createFreezePane(0, 3);

            // --- HOJA 2: NOTIFICACIONES ---
            Sheet sheetNotif = wb.createSheet("Notificaciones");
            rowNum = 0;

            // Fila 1: Cabecera Notificaciones
            Row cabeceraNotifRow = sheetNotif.createRow(rowNum++);
            Cell cellCabeceraNotif = cabeceraNotifRow.createCell(0);
            cellCabeceraNotif.setCellStyle(estiloCabecera);
            cellCabeceraNotif.setCellValue("SINCRONIZACIÓN IBOX - NOTIFICACIONES");
            sheetNotif.addMergedRegion(new CellRangeAddress(0, 0, 0, 9));

            // Fila 2: Headers
            Row headerNotifRow = sheetNotif.createRow(rowNum++);
            String[] headersNotif = {"N°", "ESTADO", "RUC", "Y", "Razón Social", "ID SUNAT", "Tipo", "Nombre Corto", "Título", "Fecha"};
            for (int i = 0; i < headersNotif.length; i++) {
                Cell cell = headerNotifRow.createCell(i);
                cell.setCellValue(headersNotif[i]);
                cell.setCellStyle(estiloSubCabecera);
            }

            int inicioFilt2 = rowNum;

            // Datos
            contador = 1;
            for (NotificacionProjection notif : notificaciones) {
                Row dataRow = sheetNotif.createRow(rowNum++);
                int colNum = 0;
                
                Cell cellNum = dataRow.createCell(colNum++);
                cellNum.setCellValue(contador++);
                cellNum.setCellStyle(estiloDatoCentro);
                
                Cell cellEstado = dataRow.createCell(colNum++);
                cellEstado.setCellValue(notif.getEstadoCliente() != null ? notif.getEstadoCliente() : "");
                int estadoId2 = notif.getIdEstado() != null ? notif.getIdEstado() : -1;
                if (estadoId2 == 1) cellEstado.setCellStyle(dataLeftStyleGreen);
                else if (estadoId2 == 2) cellEstado.setCellStyle(dataLeftStyleRed);
                else if (estadoId2 == 3) cellEstado.setCellStyle(dataLeftStyleBlue);
                else cellEstado.setCellStyle(estiloDatoIzquierda);
                
                Cell cellRuc = dataRow.createCell(colNum++);
                cellRuc.setCellValue(notif.getRuc());
                cellRuc.setCellStyle(estiloDatoIzquierda);

                Cell cellY = dataRow.createCell(colNum++);
                cellY.setCellValue(notif.getY() != null ? notif.getY() : "");
                cellY.setCellStyle(estiloDatoIzquierda);
                
                Cell cellRazonSocial = dataRow.createCell(colNum++);
                cellRazonSocial.setCellValue(notif.getRazonSocial() != null ? notif.getRazonSocial() : "");
                cellRazonSocial.setCellStyle(estiloDatoIzquierda);

                Cell cellIdSunat = dataRow.createCell(colNum++);
                cellIdSunat.setCellValue(notif.getIdSunat() != null ? notif.getIdSunat() : "");
                cellIdSunat.setCellStyle(estiloDatoIzquierda);

                Cell cellTipo = dataRow.createCell(colNum++);
                String tipoVal = notif.getTipo() != null ? notif.getTipo() : "";
                String nombreCortoVal = notif.getNombreCorto() != null ? notif.getNombreCorto() : "";
                cellTipo.setCellValue(tipoVal);
                if (nombreCortoVal.toLowerCase().contains("coactiva")) {
                    cellTipo.setCellStyle(dataLeftStyleRed);
                } else if (tipoVal.equalsIgnoreCase("Fiscalizaciones")) {
                    cellTipo.setCellStyle(dataLeftStyleBlue);
                } else {
                    cellTipo.setCellStyle(estiloDatoIzquierda);
                }

                Cell cellNombreCorto = dataRow.createCell(colNum++);
                cellNombreCorto.setCellValue(notif.getNombreCorto() != null ? notif.getNombreCorto() : "");
                cellNombreCorto.setCellStyle(estiloDatoIzquierda);

                Cell cellTitulo = dataRow.createCell(colNum++);
                cellTitulo.setCellValue(notif.getTitulo() != null ? notif.getTitulo() : "");
                cellTitulo.setCellStyle(estiloDatoIzquierda);

                Cell cellFecha = dataRow.createCell(colNum++);
                cellFecha.setCellValue(notif.getFecha() != null ? notif.getFecha().format(DATE_TIME_FORMATTER) : "");
                cellFecha.setCellStyle(estiloDatoIzquierda);
            }

            int finFilt2 = rowNum - 1;
            sheetNotif.setAutoFilter(new CellRangeAddress(inicioFilt2, finFilt2, 0, 9));
            for (int i = 0; i < headersNotif.length; i++) sheetNotif.autoSizeColumn(i);
            sheetNotif.createFreezePane(0, 2);

            wb.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar Excel de Buzón Sunat", e);
        }
    }
}
