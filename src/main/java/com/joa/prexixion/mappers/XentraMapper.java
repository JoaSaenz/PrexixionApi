package com.joa.prexixion.mappers;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import com.joa.prexixion.dto.XentraDataDTO;
import com.joa.prexixion.entities.XentraData;
import com.joa.prexixion.entities.XentraFecha;

@Mapper(componentModel = "spring") // así Spring lo detecta como bean
public interface XentraMapper {

    XentraMapper INSTANCE = Mappers.getMapper(XentraMapper.class);

    // DTO → Entity
    @Mapping(source = "diasSemana", target = "diasSemana", qualifiedByName = "listToString")
    @Mapping(source = "mesesPermitidos", target = "mesesPermitidos", qualifiedByName = "listToString")
    @Mapping(source = "fechas", target = "fechas", qualifiedByName = "mapFechasDtoToEntity")
    XentraData toEntity(XentraDataDTO dto);

    // Entity → DTO
    @Mapping(source = "diasSemana", target = "diasSemana", qualifiedByName = "stringToList")
    @Mapping(source = "mesesPermitidos", target = "mesesPermitidos", qualifiedByName = "stringToList")
    @Mapping(source = "fechas", target = "fechas", qualifiedByName = "mapFechasEntityToDto")
    XentraDataDTO toDTO(XentraData entity);

    // --- Métodos auxiliares ---
    @Named("mapFechasEntityToDto")
    static List<LocalDate> mapFechasEntityToDto(List<XentraFecha> fechas) {
        if (fechas == null)
            return List.of();
        return fechas.stream()
                .map(f -> LocalDate.parse(f.getFecha())) // yyyy-MM-dd
                .toList();
    }

    @Named("mapFechasDtoToEntity")
    static List<XentraFecha> mapFechasDtoToEntity(List<LocalDate> fechas) {
        if (fechas == null)
            return List.of();
        return fechas.stream()
                .map(localDate -> {
                    XentraFecha f = new XentraFecha();
                    f.setFecha(localDate.toString());
                    return f;
                })
                .toList();
    }

    @Named("listToString")
    static String listToString(List<Integer> list) {
        return (list == null || list.isEmpty())
                ? null
                : list.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    @Named("stringToList")
    static List<Integer> stringToList(String str) {
        return (str == null || str.isBlank())
                ? List.of()
                : Arrays.stream(str.split(","))
                        .map(String::trim)
                        .map(Integer::parseInt)
                        .toList();
    }
}