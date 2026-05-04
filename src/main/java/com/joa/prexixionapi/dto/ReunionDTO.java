package com.joa.prexixionapi.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.joa.prexixionapi.entities.Cliente;
import com.joa.prexixionapi.entities.Gclass;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReunionDTO {
    private int id;
    private Cliente cliente;
    private String tipo;
    private String fecha;
    private String horaI;
    private String horaF;
    private Gclass estado;
    private String otros;
    
    private List<ReunionTemaDTO> temas;
    private List<ReunionParticipanteExternoDTO> participantesExternos;
    private List<ReunionParticipanteInternoDTO> participantesInternos;
    private List<Gclass> areas;
    private List<Gclass> acuerdos;
}
