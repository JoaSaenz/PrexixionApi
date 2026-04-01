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

            // --- HOJA 1: LOGS ---
            Sheet sheetLogs = wb.createSheet("Logs de Ejecución");
            int rowNum = 0;

            // Fila 1: Cabecera Principal
            Row cabeceraRow = sheetLogs.createRow(rowNum++);
            Cell cellCabecera = cabeceraRow.createCell(0);
            cellCabecera.setCellStyle(estiloCabecera);
            cellCabecera.setCellValue("SINCRONIZACIÓN IBOX - DETALLE");
            sheetLogs.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));

            // Fila 2: Detalle de Ejecución
            Row detalleRow = sheetLogs.createRow(rowNum++);
            Cell cellDetalle = detalleRow.createCell(0);
            cellDetalle.setCellStyle(styleManager.getFondoLightBlueStyle());
            String info = "Fecha: " + (job.getHoraInicio() != null ? job.getHoraInicio().format(DATE_TIME_FORMATTER) : "-") 
                    + " | Estado: " + job.getEstado() + " | Mensaje: " + job.getMensaje();
            cellDetalle.setCellValue(info);
            sheetLogs.addMergedRegion(new CellRangeAddress(1, 1, 0, 8));

            // Fila 3: Headers
            Row headerRow = sheetLogs.createRow(rowNum++);
            String[] headers = {"N°", "ESTADO", "RUC", "Y", "RESULTADO", "MENSAJE", "DURACIÓN (s)", "FECHA REVISIÓN", "NUEVAS NOTIF."};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(estiloSubCabecera);
            }

            // Datos
            int contador = 1;
            for (JobStatusLogProjection log : logs) {
                Row dataRow = sheetLogs.createRow(rowNum++);
                int col = 0;
                
                dataRow.createCell(col++).setCellValue(contador++);
                dataRow.createCell(col++).setCellValue(log.getEstadoCliente() != null ? log.getEstadoCliente() : "");
                dataRow.createCell(col++).setCellValue(log.getRuc());
                dataRow.createCell(col++).setCellValue(log.getY() != null ? log.getY() : "");
                dataRow.createCell(col++).setCellValue(log.getResultado());
                dataRow.createCell(col++).setCellValue(log.getMensaje());
                dataRow.createCell(col++).setCellValue(log.getDuracionMs() != null ? TimeUnit.MILLISECONDS.toSeconds(log.getDuracionMs()) : 0);
                dataRow.createCell(col++).setCellValue(log.getFechaRegistro() != null ? log.getFechaRegistro().format(DATE_TIME_FORMATTER) : "");
                dataRow.createCell(col++).setCellValue(log.getNuevasNotificaciones() != null ? log.getNuevasNotificaciones() : 0);

                // Aplicar estilos a las celdas de datos
                for (int j = 0; j < col; j++) {
                    dataRow.getCell(j).setCellStyle(j == 0 || j == 6 || j == 8 ? estiloDatoCentro : estiloDatoIzquierda);
                }
            }

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
            sheetNotif.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));

            // Fila 2: Headers
            Row headerNotifRow = sheetNotif.createRow(rowNum++);
            String[] headersNotif = {"N°", "ESTADO", "RUC", "ID SUNAT", "TÍTULO", "FECHA"};
            for (int i = 0; i < headersNotif.length; i++) {
                Cell cell = headerNotifRow.createCell(i);
                cell.setCellValue(headersNotif[i]);
                cell.setCellStyle(estiloSubCabecera);
            }

            // Datos
            contador = 1;
            for (NotificacionProjection notif : notificaciones) {
                Row dataRow = sheetNotif.createRow(rowNum++);
                int col = 0;
                
                dataRow.createCell(col++).setCellValue(contador++);
                dataRow.createCell(col++).setCellValue(notif.getEstadoCliente() != null ? notif.getEstadoCliente() : "");
                dataRow.createCell(col++).setCellValue(notif.getRuc());
                dataRow.createCell(col++).setCellValue(notif.getIdSunat());
                dataRow.createCell(col++).setCellValue(notif.getTitulo());
                dataRow.createCell(col++).setCellValue(notif.getFecha() != null ? notif.getFecha().format(DATE_TIME_FORMATTER) : "");

                for (int j = 0; j < col; j++) {
                    dataRow.getCell(j).setCellStyle(j == 0 || j == 3 || j == 5 ? estiloDatoCentro : estiloDatoIzquierda);
                }
            }

            for (int i = 0; i < headersNotif.length; i++) sheetNotif.autoSizeColumn(i);
            sheetNotif.createFreezePane(0, 2);

            wb.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar Excel de Buzón Sunat", e);
        }
    }
}
