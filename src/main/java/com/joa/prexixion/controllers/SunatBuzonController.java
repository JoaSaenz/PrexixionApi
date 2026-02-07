package com.joa.prexixion.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.io.ByteArrayOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import org.springframework.web.bind.annotation.RequestParam;

import com.joa.prexixion.dto.ApiResponse;
import com.joa.prexixion.entities.Notificacion;
import com.joa.prexixion.entities.NotificacionAdjunto;
import com.joa.prexixion.repositories.NotificacionRepository;
import com.joa.prexixion.services.JobLauncherService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sunat")
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class SunatBuzonController {

    private final JobLauncherService jobLauncherService;
    private final NotificacionRepository notificacionRepository;

    @PostMapping("/sincronizar")
    public ResponseEntity<ApiResponse> sincronizarManualmente() {
        ApiResponse response = jobLauncherService.lanzarSincronizacionSunat();
        if ("ERROR".equals(response.getStatus())) {
            return ResponseEntity.internalServerError().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/notificacion/{id}/descargar")
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<byte[]> descargarAdjuntos(@PathVariable Long id) {
        Notificacion notif = notificacionRepository.findById(id).orElse(null);
        if (notif == null || notif.getAdjuntos().isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<NotificacionAdjunto> adjuntos = notif.getAdjuntos();

        try {
            if (adjuntos.size() == 1) {
                NotificacionAdjunto adj = adjuntos.get(0);
                String filename = adj.getNombre();
                if (filename != null && !filename.toLowerCase().endsWith(".pdf")
                        && !filename.toLowerCase().endsWith(".zip")) {
                    filename += ".pdf";
                }

                notif.setRevisado(true);
                notificacionRepository.save(notif);

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(adj.getContenido());
            } else {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ZipOutputStream zos = new ZipOutputStream(baos);

                for (NotificacionAdjunto adj : adjuntos) {
                    ZipEntry entry = new ZipEntry(adj.getNombre());
                    zos.putNextEntry(entry);
                    zos.write(adj.getContenido());
                    zos.closeEntry();
                }
                zos.close();

                notif.setRevisado(true);
                notificacionRepository.save(notif);

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"notificacion_" + notif.getIdSunat() + ".zip\"")
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(baos.toByteArray());
            }
        } catch (Exception e) {
            log.error("Error al descargar adjunto para id: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/descargar-lote")
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<byte[]> descargarLote(@RequestParam("ruc") String ruc,
            @RequestParam("fecha") String fecha) {
        if (ruc == null || fecha == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            LocalDate date = LocalDate.parse(fecha);
            LocalDateTime inicio = date.atStartOfDay();
            LocalDateTime fin = date.atTime(LocalTime.MAX);

            List<Notificacion> notificaciones = notificacionRepository.findByRucAndFechaBetween(ruc, inicio, fin);
            List<NotificacionAdjunto> allAdjuntos = new ArrayList<>();
            for (Notificacion notif : notificaciones) {
                allAdjuntos.addAll(notif.getAdjuntos());
            }

            if (allAdjuntos.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // Mark as reviewed first to ensure it's saved even if there's only 1 attachment
            for (Notificacion notif : notificaciones) {
                notif.setRevisado(true);
            }
            notificacionRepository.saveAllAndFlush(notificaciones);

            if (allAdjuntos.size() == 1) {
                NotificacionAdjunto adj = allAdjuntos.get(0);
                String filename = adj.getNombre();
                if (filename != null && !filename.toLowerCase().endsWith(".pdf")
                        && !filename.toLowerCase().endsWith(".zip")) {
                    filename += ".pdf";
                }
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(adj.getContenido());
            } else {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ZipOutputStream zos = new ZipOutputStream(baos);

                for (NotificacionAdjunto adj : allAdjuntos) {
                    ZipEntry entry = new ZipEntry(adj.getNotificacion().getIdSunat() + "_" + adj.getNombre());
                    zos.putNextEntry(entry);
                    zos.write(adj.getContenido());
                    zos.closeEntry();
                }
                zos.close();

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"lote_" + ruc + "_" + fecha + ".zip\"")
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(baos.toByteArray());
            }
        } catch (Exception e) {
            log.error("Error al descargar lote para RUC: {} y fecha: {}", ruc, fecha, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
