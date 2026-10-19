package com.joa.prexixionapi.modules.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import com.joa.prexixionapi.common.dto.FiscalizacionSummaryDTO;
import com.joa.prexixionapi.common.dto.TipoCambioDTO;
import com.joa.prexixionapi.common.dto.PaycomStatsDTO;
import com.joa.prexixionapi.modules.asistencia.dto.AsistenciaStatsDTO;
import com.joa.prexixionapi.common.dto.TareaDiariaDTO;
import com.joa.prexixionapi.common.dto.ProcesosSummaryDTO;
import com.joa.prexixionapi.common.dto.OperationStatsDTO;
import com.joa.prexixionapi.modules.cronograma.dto.CronogramaDetailDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryDTO {
    private TipoCambioDTO tipoCambio;
    private List<CronogramaDetailDTO> cronogramas;
    private List<FiscalizacionSummaryDTO> activeAuditsTax;
    private List<FiscalizacionSummaryDTO> activeAuditsPay;
    private List<FiscalizacionSummaryDTO> activeAuditsReclamos;

    public List<FiscalizacionSummaryDTO> getActiveAuditsTax() { return activeAuditsTax; }
    public void setActiveAuditsTax(List<FiscalizacionSummaryDTO> activeAuditsTax) { this.activeAuditsTax = activeAuditsTax; }

    public List<FiscalizacionSummaryDTO> getActiveAuditsPay() { return activeAuditsPay; }
    public void setActiveAuditsPay(List<FiscalizacionSummaryDTO> activeAuditsPay) { this.activeAuditsPay = activeAuditsPay; }

    public List<FiscalizacionSummaryDTO> getActiveAuditsReclamos() { return activeAuditsReclamos; }
    public void setActiveAuditsReclamos(List<FiscalizacionSummaryDTO> activeAuditsReclamos) { this.activeAuditsReclamos = activeAuditsReclamos; }

    private long totalTareasPendientes;
    private String mensajeBienvenida;
    private ProcesosSummaryDTO procesos;
    private ChartDataDTO clientStats;
    private ChartDataDTO taskStats;
    private ChartDataDTO serviceStats;
    private AsistenciaStatsDTO asistencia;
    private List<TareaDiariaDTO> tareas;
    private Double uit;
    private OperationStatsDTO operationStats;
    private PaycomStatsDTO paycomStats;
}
