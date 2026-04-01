package com.joa.prexixionapi.mappers;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.joa.prexixionapi.dto.XentraDataDTO;
import com.joa.prexixionapi.entities.XentraData;
import com.joa.prexixionapi.entities.XentraFecha;

@Component
public class XentraMapper {

    /**
     * Convierte DTO a Entidad manualmente.
     */
    public XentraData toEntity(XentraDataDTO dto) {
        if (dto == null) {
            return null;
        }

        XentraData entity = new XentraData();

        entity.setId(dto.getId());
        entity.setIdArea(dto.getIdArea());
        entity.setIdSubArea(dto.getIdSubArea());
        entity.setAbreviatura(dto.getAbreviatura());
        entity.setNombre(dto.getNombre());
        entity.setColor(dto.getColor());
        entity.setResponsable(dto.getResponsable());
        entity.setFechaInicio(dto.getFechaInicio());
        entity.setFechaFin(dto.getFechaFin());
        entity.setTipoRepeticion(dto.getTipoRepeticion());
        entity.setIntervaloSemanas(dto.getIntervaloSemanas());
        entity.setDiaInicioMes(dto.getDiaInicioMes());
        entity.setDiaFinMes(dto.getDiaFinMes());
        entity.setEstado(dto.getEstado());

        // Mapeos con lógica especial
        entity.setDiasSemana(listToString(dto.getDiasSemana()));
        entity.setMesesPermitidos(listToString(dto.getMesesPermitidos()));
        entity.setFechas(mapFechasDtoToEntity(dto.getFechas()));

        return entity;
    }

    /**
     * Convierte Entidad a DTO manualmente.
     */
    public XentraDataDTO toDTO(XentraData entity) {
        if (entity == null) {
            return null;
        }

        XentraDataDTO dto = new XentraDataDTO();

        dto.setId(entity.getId());
        dto.setIdArea(entity.getIdArea());
        dto.setIdSubArea(entity.getIdSubArea());
        dto.setAbreviatura(entity.getAbreviatura());
        dto.setNombre(entity.getNombre());
        dto.setColor(entity.getColor());
        dto.setResponsable(entity.getResponsable());
        dto.setFechaInicio(entity.getFechaInicio());
        dto.setFechaFin(entity.getFechaFin());
        dto.setTipoRepeticion(entity.getTipoRepeticion());
        dto.setIntervaloSemanas(entity.getIntervaloSemanas());
        dto.setDiaInicioMes(entity.getDiaInicioMes());
        dto.setDiaFinMes(entity.getDiaFinMes());
        dto.setEstado(entity.getEstado());

        // Mapeos con lógica especial
        dto.setDiasSemana(stringToList(entity.getDiasSemana()));
        dto.setMesesPermitidos(stringToList(entity.getMesesPermitidos()));
        dto.setFechas(mapFechasEntityToDto(entity.getFechas()));

        return dto;
    }

    // --- Métodos auxiliares ---

    public List<LocalDate> mapFechasEntityToDto(List<XentraFecha> fechas) {
        if (fechas == null) {
            return List.of();
        }
        return fechas.stream()
                .map(f -> {
                    try {
                        return LocalDate.parse(f.getFecha());
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    public List<XentraFecha> mapFechasDtoToEntity(List<LocalDate> fechas) {
        if (fechas == null) {
            return List.of();
        }
        return fechas.stream()
                .map(localDate -> {
                    XentraFecha f = new XentraFecha();
                    f.setFecha(localDate.toString());
                    return f;
                })
                .toList();
    }

    public String listToString(List<Integer> list) {
        return (list == null || list.isEmpty())
                ? null
                : list.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    public List<Integer> stringToList(String str) {
        return (str == null || str.isBlank())
                ? List.of()
                : Arrays.stream(str.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .map(Integer::parseInt)
                        .toList();
    }
}