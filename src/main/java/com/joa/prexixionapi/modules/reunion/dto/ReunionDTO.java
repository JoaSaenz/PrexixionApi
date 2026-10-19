package com.joa.prexixionapi.modules.reunion.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.joa.prexixionapi.common.dto.GclassDTO;
import com.joa.prexixionapi.modules.cliente.dto.ClienteDTO;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReunionDTO {
    private int id;
    private ClienteDTO cliente;
    private String tipo;
    private String fecha;
    private String horaI;
    private String horaF;
    private GclassDTO estado;
    private String otros;
    
    private List<ReunionTemaDTO> temas;
    private List<ReunionParticipanteExternoDTO> participantesExternos;
    private List<ReunionParticipanteInternoDTO> participantesInternos;
    private List<GclassDTO> areas;
    private List<GclassDTO> acuerdos;
}
