package com.joa.prexixionapi.modules.depreciacion.services;

import com.joa.prexixionapi.common.repositories.TipoCambioRepository;
import com.joa.prexixionapi.common.dto.TipoCambioDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.joa.prexixionapi.modules.depreciacion.dto.ActivoTipoDTO;
import com.joa.prexixionapi.modules.depreciacion.services.ActivoService;
import com.joa.prexixionapi.modules.depreciacion.dto.ActivoDepreciacionDTO;
import com.joa.prexixionapi.modules.depreciacion.repositories.ActivoRepository;
import com.joa.prexixionapi.modules.depreciacion.dto.ActivoExcelDTO;
import com.joa.prexixionapi.modules.depreciacion.repositories.ActivoDepreciacionRepository;
import com.joa.prexixionapi.modules.depreciacion.dto.ActivoDTO;
import com.joa.prexixionapi.modules.depreciacion.repositories.ActivoTipoRepository;

@Service
@RequiredArgsConstructor
public class ActivoService {

    private final ActivoRepository repository;
    private final ActivoDepreciacionRepository depreciacionRepository;
    private final DepreciacionService depreciacionService;
    private final ActivoTipoRepository activoTipoRepository;
    private final TipoCambioRepository tipoCambioRepository;

    @Transactional
    public int saveActivo(ActivoDTO obj, boolean calcular) {
        long count = repository.countByClienteAndId(obj.getIdCliente(), obj.getId());
        int rpta;

        if (count > 0) {
            repository.updateActivo(obj.getIdCliente(), obj.getId(), obj.getIdProveedor(), obj.getProveedor(),
                    obj.getDescripcion(), obj.getMarca(), obj.getModelo(), obj.getSeriePlaca(), obj.getIdTipo(),
                    obj.getCuenta(), obj.getDocumento(), obj.getFechaInicio(), obj.getFechaFinContable(),
                    obj.getFechaFinTributaria(), obj.getIdMoneda(), obj.getPrecioUnitario(), obj.getCantidad(),
                    obj.getCostoInicial(), obj.getIdEstado(), obj.getFechaBaja(), obj.getFechaCompra());
            rpta = 2; // Update
        } else {
            repository.insertActivo(obj.getIdCliente(), obj.getId(), obj.getIdProveedor(), obj.getProveedor(),
                    obj.getDescripcion(), obj.getMarca(), obj.getModelo(), obj.getSeriePlaca(), obj.getIdTipo(),
                    obj.getCuenta(), obj.getDocumento(), obj.getFechaInicio(), obj.getFechaFinContable(),
                    obj.getFechaFinTributaria(), obj.getIdMoneda(), obj.getPrecioUnitario(), obj.getCantidad(),
                    obj.getCostoInicial(), obj.getIdEstado(), obj.getFechaBaja(), obj.getFechaCompra());
            rpta = 1; // Insert
        }

        if (calcular) {
            obj.setDepreciacionesContables(depreciacionService.calcularDepreciacion(obj, 1, null));
            obj.setDepreciacionesTributarias(depreciacionService.calcularDepreciacion(obj, 2, null));
        }

        // Batch replace depreciations
        depreciacionRepository.deleteByClienteAndBien(obj.getIdCliente(), obj.getId());

        depreciacionRepository.insertDepreciacionesBatch(obj.getIdCliente(), obj.getId(), 1, obj.getDepreciacionesContables());
        depreciacionRepository.insertDepreciacionesBatch(obj.getIdCliente(), obj.getId(), 2, obj.getDepreciacionesTributarias());

        return rpta;
    }


    @Transactional
    public int deleteActivo(String idCliente, String id) {
        depreciacionRepository.deleteByClienteAndBien(idCliente, id);
        repository.deleteByClienteAndId(idCliente, id);
        return 3;
    }

    @Transactional
    public int updateBloqueo(String idCliente, List<String> ids, int status) {
        return repository.updateBloqueo(idCliente, ids, status);
    }

    public DepreciacionService getDepreciacionService() {
        return this.depreciacionService;
    }

    public ActivoDTO getActivoDetalle(String idCliente, String id) {
        ActivoDTO activo = repository.findByClienteAndId(idCliente, id);
        if (activo == null)
            return null;

        activo.setDepreciacionesContables(mapDepreciaciones(
                depreciacionRepository.findByClienteAndBienAndTipoRaw(idCliente, id, 1)));
        activo.setDepreciacionesTributarias(mapDepreciaciones(
                depreciacionRepository.findByClienteAndBienAndTipoRaw(idCliente, id, 2)));

        return activo;
    }

    public List<ActivoDTO> getActivosByIdCliente(String idCliente) {
        return repository.findByIdCliente(idCliente);
    }

    public ActivoExcelDTO getActivosExcelData(String idCliente) {
        List<ActivoDTO> rawActivos = repository.findByIdCliente(idCliente);
        List<Object[]> rawDepreciaciones = depreciacionRepository.findAllByClienteRaw(idCliente);

        String minFechaCompra = repository.getMinFechaCompra(idCliente);
        String maxFecha = repository.getMaxFechaContableTributaria(idCliente);

        // Group existing depreciations by idBien and idTipo
        Map<String, Map<Integer, List<ActivoDepreciacionDTO>>> depMap = new LinkedHashMap<>();
        for (Object[] row : rawDepreciaciones) {
            String idBien = (String) row[1];
            Integer idTipo = toInteger(row[3]);
            depMap.computeIfAbsent(idBien, k -> new LinkedHashMap<>())
                    .computeIfAbsent(idTipo, k -> new ArrayList<>())
                    .add(constructDepDTO(row));
        }

        // Devolver los datos tal como están en BD. El relleno de años faltantes
        // lo realiza el ActivosController del legacy, que es quien genera el Excel.
        List<ActivoDTO> activos = rawActivos.stream().map(dto -> {
            Map<Integer, List<ActivoDepreciacionDTO>> bienDeps = depMap.getOrDefault(dto.getId(), Map.of());

            dto.setDepreciacionesContables(new ArrayList<>(bienDeps.getOrDefault(1, new ArrayList<>())));
            dto.setDepreciacionesTributarias(new ArrayList<>(bienDeps.getOrDefault(2, new ArrayList<>())));
            return dto;
        }).collect(Collectors.toList());

        return ActivoExcelDTO.builder()
                .activos(activos)
                .minFechaCompra(minFechaCompra)
                .maxFechaContableTributaria(maxFecha)
                .build();
    }

    @Transactional
    public int uploadActivos(List<ActivoDTO> list) {
        if (list == null || list.isEmpty())
            return 0;

        List<ActivoTipoDTO> tipos = activoTipoRepository.list();
        Map<String, ActivoTipoData> tiposMap = tipos.stream().collect(Collectors.toMap(
                ActivoTipoDTO::getId,
                dto -> new ActivoTipoData(dto.getDepreciacionContable(), dto.getDepreciacionTributaria())));

        for (ActivoDTO activo : list) {
            ActivoTipoData tipoData = tiposMap.getOrDefault(activo.getIdTipo(), new ActivoTipoData(0.0, 0.0));
            activo.setPorcentajeDepreciacionContable(tipoData.contable);
            activo.setPorcentajeDepreciacionTributaria(tipoData.tributaria);

            // Cálculos de Fecha Fin
            activo.setFechaFinContable(
                    calcularFechaFinal(activo.getFechaInicio(), activo.getPorcentajeDepreciacionContable()));
            activo.setFechaFinTributaria(
                    calcularFechaFinal(activo.getFechaInicio(), activo.getPorcentajeDepreciacionTributaria()));

            // Cálculo del costo inicial
            if ("USD".equals(activo.getIdMoneda())) {
                TipoCambioDTO tc = tipoCambioRepository.findByFecha(activo.getFechaCompra());
                double tCambio = (tc != null) ? tc.getTVenta() : 1.0;
                activo.setCostoInicial((activo.getPrecioUnitario() * tCambio) * activo.getCantidad());
            } else {
                activo.setCostoInicial(activo.getPrecioUnitario() * activo.getCantidad());
            }

            // Estado
            if (activo.getFechaBaja() != null && !activo.getFechaBaja().trim().isEmpty()) {
                activo.setIdEstado(2);
            } else {
                activo.setIdEstado(1);
            }

            saveActivo(activo, true);
        }

        return 1;
    }

    private String calcularFechaFinal(String fechaInicio, double porcentaje) {
        if (porcentaje == 0 || fechaInicio == null || fechaInicio.isEmpty())
            return fechaInicio;

        LocalDate date = LocalDate.parse(fechaInicio);
        double ratio = 100.0 / porcentaje;
        int years = (int) ratio;
        int months = (int) (12 * (ratio - years));
        int days = (int) Math.round(30 * ((12 * (ratio - years)) - months));

        date = date.plusYears(years).plusMonths(months).plusDays(days);
        return date.toString();
    }

    private static class ActivoTipoData {
        Double contable;
        Double tributaria;

        ActivoTipoData(Double c, Double t) {
            this.contable = c;
            this.tributaria = t;
        }
    }

    public List<ActivoDTO> getActivosFijosRVExcel(String idCliente) {
        return repository.findActivosFijosRVExcel(idCliente);
    }

    public List<ActivoDTO> getActivosFijosExcelWithDepreciations(String idCliente, String anio) {
        return repository.findActivosFijosExcelWithDepreciations(idCliente, anio);
    }

    private List<ActivoDepreciacionDTO> mapDepreciaciones(List<Object[]> rows) {
        return rows.stream().map(this::constructDepDTO).collect(Collectors.toList());
    }

    private ActivoDepreciacionDTO constructDepDTO(Object[] row) {
        return ActivoDepreciacionDTO.builder()
                .idCliente((String) row[0])
                .idBien((String) row[1])
                .anio((String) row[2])
                .idTipo(toInteger(row[3]))
                .activoSaldoInicial(toDouble(row[4]))
                .activoCompras(toDouble(row[5]))
                .activoRetiros(toDouble(row[6]))
                .activoSaldoFinal(toDouble(row[7]))
                .inicial(toDouble(row[8]))
                .ene(toDouble(row[9]))
                .feb(toDouble(row[10]))
                .mar(toDouble(row[11]))
                .abr(toDouble(row[12]))
                .may(toDouble(row[13]))
                .jun(toDouble(row[14]))
                .jul(toDouble(row[15]))
                .ago(toDouble(row[16]))
                .sep(toDouble(row[17]))
                .oct(toDouble(row[18]))
                .nov(toDouble(row[19]))
                .dec(toDouble(row[20]))
                .total(toDouble(row[21]))
                .retiros(toDouble(row[22]))
                .saldoFinal(toDouble(row[23]))
                .activoFijo(toDouble(row[24]))
                .build();
    }

    private Double toDouble(Object obj) {
        if (obj == null)
            return 0.0;
        if (obj instanceof Double)
            return (Double) obj;
        if (obj instanceof Number)
            return ((Number) obj).doubleValue();
        return 0.0;
    }

    private Integer toInteger(Object obj) {
        if (obj == null)
            return 0;
        if (obj instanceof Integer)
            return (Integer) obj;
        if (obj instanceof Number)
            return ((Number) obj).intValue();
        return 0;
    }
}
