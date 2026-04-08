package com.joa.prexixionapi.services;

import com.joa.prexixionapi.dto.ActivoDepreciacionDTO;
import com.joa.prexixionapi.dto.ActivoExcelDTO;
import com.joa.prexixionapi.dto.ActivoDTO;
import com.joa.prexixionapi.repositories.ActivoDepreciacionRepository;
import com.joa.prexixionapi.repositories.ActivoRepository;
import com.joa.prexixionapi.repositories.ActivoTipoRepository;
import com.joa.prexixionapi.repositories.TipoCambioRepository;
import com.joa.prexixionapi.dto.TipoCambioDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivoService {

    private final ActivoRepository repository;
    private final ActivoDepreciacionRepository depreciacionRepository;
    private final DepreciacionService depreciacionService;
    private final ActivoTipoRepository activoTipoRepository;
    private final TipoCambioRepository tipoCambioRepository;
    private final JdbcTemplate jdbcTemplate;

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

        insertDepreciacionesBatch(obj.getIdCliente(), obj.getId(), 1, obj.getDepreciacionesContables());
        insertDepreciacionesBatch(obj.getIdCliente(), obj.getId(), 2, obj.getDepreciacionesTributarias());

        return rpta;
    }

    private void insertDepreciacionesBatch(String idCliente, String idBien, int idTipo,
            List<ActivoDepreciacionDTO> list) {
        if (list == null || list.isEmpty())
            return;

        String sql = """
                    INSERT INTO activosDepreciaciones
                    (idCliente, idBien, anio, idTipo, activoSaldoInicial, activoCompras, activoRetiros, activoSaldoFinal,
                     inicial, ene, feb, mar, abr, may, jun, jul, ago, sep, oct, nov, [dec], total, retiros, saldoFinal, activoFijo)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.batchUpdate(sql, list, 1000, (ps, ad) -> {
            ps.setString(1, idCliente);
            ps.setString(2, idBien);
            ps.setString(3, ad.getAnio());
            ps.setInt(4, idTipo);
            ps.setDouble(5, ad.getActivoSaldoInicial());
            ps.setDouble(6, ad.getActivoCompras());
            ps.setDouble(7, ad.getActivoRetiros());
            ps.setDouble(8, ad.getActivoSaldoFinal());
            ps.setDouble(9, ad.getInicial());
            ps.setDouble(10, ad.getEne());
            ps.setDouble(11, ad.getFeb());
            ps.setDouble(12, ad.getMar());
            ps.setDouble(13, ad.getAbr());
            ps.setDouble(14, ad.getMay());
            ps.setDouble(15, ad.getJun());
            ps.setDouble(16, ad.getJul());
            ps.setDouble(17, ad.getAgo());
            ps.setDouble(18, ad.getSep());
            ps.setDouble(19, ad.getOct());
            ps.setDouble(20, ad.getNov());
            ps.setDouble(21, ad.getDec());
            ps.setDouble(22, ad.getTotal());
            ps.setDouble(23, ad.getRetiros());
            ps.setDouble(24, ad.getSaldoFinal());
            ps.setDouble(25, ad.getActivoFijo());
        });
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
        Object[] row = repository.findByClienteAndIdRaw(idCliente, id);
        if (row == null)
            return null;

        ActivoDTO activo = mapToActivoDTO(row);

        activo.setDepreciacionesContables(mapDepreciaciones(
                depreciacionRepository.findByClienteAndBienAndTipoRaw(idCliente, id, 1)));
        activo.setDepreciacionesTributarias(mapDepreciaciones(
                depreciacionRepository.findByClienteAndBienAndTipoRaw(idCliente, id, 2)));

        return activo;
    }

    public List<ActivoDTO> getActivosByIdCliente(String idCliente) {
        List<Object[]> rawList = repository.findByIdClienteRaw(idCliente);
        return rawList.stream().map(this::mapToActivoDTO).collect(Collectors.toList());
    }

    public ActivoExcelDTO getActivosExcelData(String idCliente) {
        List<Object[]> rawActivos = repository.findByIdClienteRaw(idCliente);
        List<Object[]> rawDepreciaciones = depreciacionRepository.findAllByClienteRaw(idCliente);

        String minFechaCompra = (String) jdbcTemplate.queryForObject(
                "SELECT MIN(fechaCompra) FROM activos WHERE idCliente = ?",
                String.class, idCliente);

        String maxFecha = (String) jdbcTemplate.queryForObject(
                "SELECT MAX(v) FROM (SELECT MAX(fechaFinContable) as v FROM activos WHERE idCliente = ? " +
                        "UNION SELECT MAX(fechaFinTributaria) FROM activos WHERE idCliente = ?) t",
                String.class, idCliente, idCliente);

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
        List<ActivoDTO> activos = rawActivos.stream().map(row -> {
            ActivoDTO dto = mapToActivoDTO(row);
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

        List<Object[]> tiposBienesRaw = activoTipoRepository.findAllRaw();
        Map<String, ActivoTipoData> tiposMap = tiposBienesRaw.stream().collect(Collectors.toMap(
                row -> (String) row[0],
                row -> new ActivoTipoData(toDouble(row[2]), toDouble(row[3]))));

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
        return repository.findActivosFijosRVExcel(idCliente).stream()
                .map(this::mapToActivoDTO)
                .collect(Collectors.toList());
    }

    public List<ActivoDTO> getActivosFijosExcelWithDepreciations(String idCliente, String anio) {
        List<Object[]> raw = repository.findActivosFijosExcelWithDepreciations(idCliente, anio);
        Map<String, ActivoDTO> map = new LinkedHashMap<>();

        for (Object[] row : raw) {
            String id = (String) row[0];
            ActivoDTO dto = map.computeIfAbsent(id, k -> mapToActivoDTO(row));

            if (dto.getDepreciacionesContables() == null)
                dto.setDepreciacionesContables(new ArrayList<>());
            if (dto.getDepreciacionesTributarias() == null)
                dto.setDepreciacionesTributarias(new ArrayList<>());

            if (row[29] != null) { // idCliente from Depreciacion (Shifted to 29)
                ActivoDepreciacionDTO ad = ActivoDepreciacionDTO.builder()
                        .idCliente((String) row[29])
                        .idBien((String) row[30])
                        .anio((String) row[31])
                        .idTipo(toInteger(row[32]))
                        .activoSaldoInicial(toDouble(row[33]))
                        .activoCompras(toDouble(row[34]))
                        .activoRetiros(toDouble(row[35]))
                        .activoSaldoFinal(toDouble(row[36]))
                        .inicial(toDouble(row[37]))
                        .ene(toDouble(row[38]))
                        .feb(toDouble(row[39]))
                        .mar(toDouble(row[40]))
                        .abr(toDouble(row[41]))
                        .may(toDouble(row[42]))
                        .jun(toDouble(row[43]))
                        .jul(toDouble(row[44]))
                        .ago(toDouble(row[45]))
                        .sep(toDouble(row[46]))
                        .oct(toDouble(row[47]))
                        .nov(toDouble(row[48]))
                        .dec(toDouble(row[49]))
                        .total(toDouble(row[50]))
                        .retiros(toDouble(row[51]))
                        .saldoFinal(toDouble(row[52]))
                        .activoFijo(toDouble(row[53]))
                        .valorHistorico(toDouble(row[54]))
                        .acumuladoHistorico(toDouble(row[55]))
                        .build();

                if (ad.getIdTipo() == 1)
                    dto.getDepreciacionesContables().add(ad);
                else if (ad.getIdTipo() == 2)
                    dto.getDepreciacionesTributarias().add(ad);
            }
        }
        return new ArrayList<>(map.values());
    }

    private ActivoDTO mapToActivoDTO(Object[] row) {
        Integer bloqueado = toInteger(row[26]);
        String bloqueadoDesc = (bloqueado != null && bloqueado == 1) ? "SÍ" : "";

        Double costInit = toDouble(row[21]);
        String mon = (String) row[16];
        Double tCambio = toDouble(row[18]);
        Double pUni = toDouble(row[19]);
        Integer cant = toInteger(row[20]);

        // Fallback para costoInicial si no está presente en BD (como en el logic legacy)
        if (costInit <= 0.0 && pUni > 0 && cant > 0) {
            if ("USD".equals(mon)) {
                // Si es USD y no hay TC, usamos 1.0 como fallback para evitar pérdida de data
                double tcRaw = (tCambio > 0) ? tCambio : 1.0;
                costInit = pUni * cant * tcRaw;
            } else {
                costInit = pUni * cant;
            }
        }

        return ActivoDTO.builder()
                .id((String) row[0])
                .idCliente((String) row[27])
                .idProveedor((String) row[1])
                .proveedor((String) row[2])
                .descripcion((String) row[3])
                .marca((String) row[4])
                .modelo((String) row[5])
                .seriePlaca((String) row[6])
                .idTipo(row[7] != null ? row[7].toString() : null)
                .tipo((String) row[8])
                .porcentajeDepreciacionContable(toDouble(row[9]))
                .porcentajeDepreciacionTributaria(toDouble(row[10]))
                .cuenta((String) row[11])
                .documento((String) row[12])
                .fechaInicio((String) row[13])
                .fechaFinContable((String) row[14])
                .fechaFinTributaria((String) row[15])
                .idMoneda(mon)
                .moneda((String) row[17])
                .tipoCambio(tCambio)
                .precioUnitario(pUni)
                .cantidad(cant)
                .costoInicial(costInit)
                .idEstado(toInteger(row[22]))
                .estado((String) row[23])
                .fechaBaja((String) row[24])
                .fechaCompra((String) row[25])
                .bloqueado(bloqueado)
                .bloqueadoDesc(bloqueadoDesc)
                .build();
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
