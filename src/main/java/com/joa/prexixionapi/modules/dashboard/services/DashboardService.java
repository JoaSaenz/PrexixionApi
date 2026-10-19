package com.joa.prexixionapi.modules.dashboard.services;

import com.joa.prexixionapi.modules.dashboard.dto.DashboardSummaryDTO;
import com.joa.prexixionapi.common.dto.TipoCambioDTO;
import com.joa.prexixionapi.modules.dashboard.repositories.DashboardRepository;
import com.joa.prexixionapi.common.repositories.TipoCambioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DashboardRepository dashboardRepository;
    private final TipoCambioRepository tipoCambioRepository;

    public DashboardSummaryDTO getSummary(String dni) {
        String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        String currentPeriod = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        
        // El resumen de procesos suele ser del mes anterior (lo que se está declarando ahora)
        LocalDate lastMonthDate = LocalDate.now().minusMonths(1);
        String lastMonthAnio = String.valueOf(lastMonthDate.getYear());
        String lastMonthMes = String.format("%02d", lastMonthDate.getMonthValue());

        TipoCambioDTO tc = tipoCambioRepository.findByFecha(today);
        
        return DashboardSummaryDTO.builder()
                .tipoCambio(tc)
                .cronogramas(dashboardRepository.findUpcomingSchedules(currentPeriod))
                .activeAuditsTax(dashboardRepository.findActiveAuditsTax())
                .activeAuditsPay(dashboardRepository.findActiveAuditsPay())
                .activeAuditsReclamos(dashboardRepository.findActiveAuditsReclamos())
                .totalTareasPendientes(dashboardRepository.countPendingTasks())
                .procesos(dashboardRepository.getProcesosSummary(lastMonthAnio, lastMonthMes))
                .asistencia(dashboardRepository.getAsistenciaStats())
                .tareas(dashboardRepository.findTasksForUser(dni))
                .uit(dashboardRepository.getUIT())
                .operationStats(dashboardRepository.getOperationStats(lastMonthAnio, lastMonthMes))
                .paycomStats(dashboardRepository.getPaycomStats(lastMonthAnio, lastMonthMes))
                .mensajeBienvenida("¡Hola! Aquí tienes el resumen de tus actividades hoy.")
                .build();
    }
}
