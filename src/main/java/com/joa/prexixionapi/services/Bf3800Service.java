package com.joa.prexixionapi.services;

import com.joa.prexixionapi.dto.*;
import com.joa.prexixionapi.entities.*;
import com.joa.prexixionapi.repositories.BeneficiarioRepository;
import com.joa.prexixionapi.repositories.Bf3800DataRepository;
import com.joa.prexixionapi.repositories.Bf3800RegistroRepository;
import com.joa.prexixionapi.utils.DateUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class Bf3800Service {

    @PersistenceContext
    private EntityManager em;

    private final Bf3800DataRepository dataRepository;
    private final BeneficiarioRepository beneficiarioRepository;
    private final Bf3800RegistroRepository registroRepository;
    private final CronogramaService cronogramaService;

    public List<Bf3800DTO> list(String periods, String estados, String grupos) {
        List<Bf3800DTO> allResults = new ArrayList<>();
        
        for (String period : periods.split(",")) {
            String anio = period.substring(0, 4);
            String mes = period.substring(4, 6);
            
            String sql = """
                    SELECT c.idEstado, ce.descripcion as estado, c.y, c.ruc, c.razonSocial, 
                    CAST(ct.stAnioDesde + ct.stMesDesde + '01' as date) as fechaI,
                    CAST(ct.stAnioHasta + ct.stMesHasta + '01' as date) as fechaF,
                    pd.observacion, pd.mail,
                    c.fPle, c.fPrico,
                    c.rtMypeTributario, c.rtRus, c.rtEspecial, c.rtGeneral, c.rtAmazonico, c.rtAgrario,
                    c.rt1ra, c.rt2da, c.rt3ra, c.rt4ta, c.rt5ta,
                    ts.id as idTipoServicio, ts.abreviatura as tipoServicioAbr, ts.descripcion as tipoServicio,
                    pR.idTipo as idTipoRegistro, pR.fecha as fechaRegistro, pR.nroOrden as nroOrdenRegistro,
                    CASE 
                        WHEN c.y = '0' THEN cr.fecha0 WHEN c.y = '1' THEN cr.fecha1 WHEN c.y = '2' THEN cr.fecha2 
                        WHEN c.y = '3' THEN cr.fecha3 WHEN c.y = '4' THEN cr.fecha4 WHEN c.y = '5' THEN cr.fecha5 
                        WHEN c.y = '6' THEN cr.fecha6 WHEN c.y = '7' THEN cr.fecha7 WHEN c.y = '8' THEN cr.fecha8 
                        WHEN c.y = '9' THEN cr.fecha9 WHEN c.y = 'b' THEN cr.fechab 
                    END as vencimiento
                    FROM cliente c
                    INNER JOIN clienteServiciosTributarios ct ON c.ruc = ct.idCliente AND ct.stIdServicioTributario = 10
                    LEFT JOIN bf3800_data pd ON c.ruc = pd.idCliente AND pd.anio = :anio AND pd.mes = :mes
                    INNER JOIN clientsEstados ce ON c.idEstado = ce.id
                    LEFT JOIN clientsTipoServicio ts ON c.idTipoServicio = ts.id
                    LEFT JOIN CRONOGRAMAPDT cr ON cr.anio = :anio AND cr.mes = :mes
                    LEFT JOIN bf3800_registros pR ON c.ruc = pR.idCliente AND pR.anio = :anio AND pR.mes = :mes
                    AND pR.id = (SELECT MAX(id) FROM bf3800_registros x WHERE x.idCliente = c.ruc AND x.anio = :anio AND x.mes = :mes)
                    WHERE CAST(ct.stAnioDesde + ct.stMesDesde + '01' as date) <= :periodoDate
                    AND (CAST(ct.stAnioHasta + ct.stMesHasta + '01' as date) >= :periodoDate OR ct.stAnioHasta IS NULL)
                    AND c.idEstado IN (%s) AND c.y IN (%s)
                    ORDER BY c.y
                    """.formatted(estados, grupos);

            log.debug("Executing native query for period: {}", period);
            var query = em.createNativeQuery(sql, Tuple.class);
            query.setParameter("anio", anio);
            query.setParameter("mes", mes);
            query.setParameter("periodoDate", anio + "-" + mes + "-01");

            @SuppressWarnings("unchecked")
            List<Tuple> results = query.getResultList();
            log.info("Period {} - Rows found: {}", period, results.size());

            allResults.addAll(results.stream().map(t -> {
                String ruc = t.get("ruc", String.class);
                String y = String.valueOf(t.get("y"));
                String venc = t.get("vencimiento") != null ? t.get("vencimiento").toString() : null;
                
                Bf3800DTO dto = Bf3800DTO.builder()
                        .idEstado(String.valueOf(t.get("idEstado")))
                        .estado(t.get("estado", String.class))
                        .idCliente(ruc)
                        .y(y)
                        .razonSocial(t.get("razonSocial", String.class))
                        .anio(anio)
                        .mes(mes)
                        .fInicio(t.get("fechaI") != null ? t.get("fechaI").toString() : null)
                        .fFin(t.get("fechaF") != null ? t.get("fechaF").toString() : null)
                        .observacion(t.get("observacion", String.class))
                        .mail(t.get("mail", Integer.class))
                        .idTipoServicio(t.get("idTipoServicio", Integer.class))
                        .tipoServicioAbr(t.get("tipoServicioAbr", String.class))
                        .tipoServicio(t.get("tipoServicio", String.class))
                        .fVencimiento(venc)
                        .build();

                // Régimen Tributario Logic
                dto.setRegimenTributario(calculateRegimenTributario(t));

                // PLE/PRICO
                dto.setPle(!"".equals(t.get("fPle", String.class)) ? "PLE" : "");
                dto.setPrico(!"".equals(t.get("fPrico", String.class)) ? "PRICO" : "");

                // Ultimo Registro
                if (t.get("idTipoRegistro") != null) {
                    Bf3800RegistroDTO reg = Bf3800RegistroDTO.builder()
                            .tipo(new Gclass(t.get("idTipoRegistro", Integer.class), ""))
                            .fecha(t.get("fechaRegistro", String.class))
                            .nroOrden(t.get("nroOrdenRegistro", String.class))
                            .build();
                    dto.setRegistros(List.of(reg));
                }

                // Vencimiento Decomposition
                if (venc != null && venc.length() >= 10) {
                    String[] parts = venc.split("-");
                    dto.setDiaVencimiento(parts[2]);
                    dto.setMesVencimiento(DateUtils.getNameStMonth(parts[1]));
                    dto.setDifference((int) DateUtils.getDifferenceInDays(venc));
                }

                return dto;
            }).collect(Collectors.toList()));
        }
        return allResults;
    }

    private String calculateRegimenTributario(Tuple t) {
        List<String> rTs = new ArrayList<>();
        if (t.get("rt3ra", Integer.class) != null && t.get("rt3ra", Integer.class) != 0) {
            if (t.get("rtMypeTributario", Integer.class) != null && t.get("rtMypeTributario", Integer.class) != 0) rTs.add("MYPE Tributario");
            if (t.get("rtRus", Integer.class) != null && t.get("rtRus", Integer.class) != 0) rTs.add("RUS");
            if (t.get("rtEspecial", Integer.class) != null && t.get("rtEspecial", Integer.class) != 0) rTs.add("Especial");
            if (t.get("rtGeneral", Integer.class) != null && t.get("rtGeneral", Integer.class) != 0) rTs.add("General");
            if (t.get("rtAmazonico", Integer.class) != null && t.get("rtAmazonico", Integer.class) != 0) rTs.add("Amazónico");
            if (t.get("rtAgrario", Integer.class) != null && t.get("rtAgrario", Integer.class) != 0) rTs.add("Agrario");
        } else {
            if (t.get("rt1ra", Integer.class) != null && t.get("rt1ra", Integer.class) != 0) rTs.add("1ra");
            else if (t.get("rt2da", Integer.class) != null && t.get("rt2da", Integer.class) != 0) rTs.add("2da");
            else if (t.get("rt4ta", Integer.class) != null && t.get("rt4ta", Integer.class) != 0) rTs.add("4ta");
            else if (t.get("rt5ta", Integer.class) != null && t.get("rt5ta", Integer.class) != 0) rTs.add("5ta");
        }
        return String.join(", ", rTs);
    }

    public Bf3800DTO getOne(String ruc, String anio, String mes) {
        String sql = """
                SELECT c.idEstado, ce.descripcion as estado, c.ruc, c.y, c.razonSocial, c.direccion,
                c.fPle, c.fPrico, c.rtMypeTributario, c.rtRus, c.rtEspecial, c.rtGeneral, c.rtAmazonico, c.rtAgrario,
                c.rt1ra, c.rt2da, c.rt3ra, c.rt4ta, c.rt5ta,
                ts.id as idTipoServicio, ts.abreviatura as tipoServicioAbr, ts.descripcion as tipoServicio,
                CAST(ct.stAnioDesde + ct.stMesDesde + '01' as date) as fechaI,
                CAST(ct.stAnioHasta + ct.stMesHasta + '01' as date) as fechaF,
                pd.observacion, pd.mail,
                CASE 
                    WHEN c.y = '0' THEN cr.fecha0 WHEN c.y = '1' THEN cr.fecha1 WHEN c.y = '2' THEN cr.fecha2 
                    WHEN c.y = '3' THEN cr.fecha3 WHEN c.y = '4' THEN cr.fecha4 WHEN c.y = '5' THEN cr.fecha5 
                    WHEN c.y = '6' THEN cr.fecha6 WHEN c.y = '7' THEN cr.fecha7 WHEN c.y = '8' THEN cr.fecha8 
                    WHEN c.y = '9' THEN cr.fecha9 WHEN c.y = 'b' THEN cr.fechab 
                END as vencimiento
                FROM cliente c
                INNER JOIN clienteServiciosTributarios ct ON c.ruc = ct.idCliente AND ct.stIdServicioTributario = 10
                LEFT JOIN bf3800_data pd ON c.ruc = pd.idCliente AND pd.anio = :anio AND pd.mes = :mes
                INNER JOIN clientsEstados ce ON c.idEstado = ce.id
                LEFT JOIN clientsTipoServicio ts ON c.idTipoServicio = ts.id
                LEFT JOIN CRONOGRAMAPDT cr ON cr.anio = :anio AND cr.mes = :mes
                WHERE c.ruc = :ruc
                AND CAST(ct.stAnioDesde + ct.stMesDesde + '01' as date) <= :periodoDate
                AND (CAST(ct.stAnioHasta + ct.stMesHasta + '01' as date) >= :periodoDate OR ct.stAnioHasta IS NULL)
                """;

        log.debug("Executing getOne native query for RUC: {} and Period: {}{}", ruc, anio, mes);
        var query = em.createNativeQuery(sql, Tuple.class);
        query.setParameter("ruc", ruc);
        query.setParameter("anio", anio);
        query.setParameter("mes", mes);
        query.setParameter("periodoDate", anio + "-" + mes + "-01");

        Tuple t;
        try {
            t = (Tuple) query.getSingleResult();
        } catch (Exception e) {
            log.warn("No data found for RUC: {} in period {}{}", ruc, anio, mes);
            return Bf3800DTO.builder().idCliente(ruc).anio(anio).mes(mes).build();
        }

        String venc = t.get("vencimiento") != null ? t.get("vencimiento").toString() : null;

        Bf3800DTO dto = Bf3800DTO.builder()
                .idEstado(String.valueOf(t.get("idEstado")))
                .estado(t.get("estado", String.class))
                .idCliente(ruc)
                .y(String.valueOf(t.get("y")))
                .razonSocial(t.get("razonSocial", String.class))
                .direccion(t.get("direccion", String.class))
                .anio(anio)
                .mes(mes)
                .fInicio(t.get("fechaI") != null ? t.get("fechaI").toString() : null)
                .fFin(t.get("fechaF") != null ? t.get("fechaF").toString() : null)
                .observacion(t.get("observacion", String.class))
                .mail(t.get("mail", Integer.class))
                .idTipoServicio(t.get("idTipoServicio", Integer.class))
                .tipoServicioAbr(t.get("tipoServicioAbr", String.class))
                .tipoServicio(t.get("tipoServicio", String.class))
                .fVencimiento(venc)
                .build();

        dto.setRegimenTributario(calculateRegimenTributario(t));
        dto.setPle(!"".equals(t.get("fPle", String.class)) ? "PLE" : "");
        dto.setPrico(!"".equals(t.get("fPrico", String.class)) ? "PRICO" : "");

        if (venc != null && venc.length() >= 10) {
            String[] parts = venc.split("-");
            dto.setDiaVencimiento(parts[2]);
            dto.setMesVencimiento(DateUtils.getNameStMonth(parts[1]));
            dto.setDifference((int) DateUtils.getDifferenceInDays(venc));
        }

        // Fetch nested lists
        List<Beneficiario> beneficiariosList = beneficiarioRepository.findByIdClienteAndAnioAndMes(ruc, anio, mes);
        List<Bf3800Registro> registrosList = registroRepository.findByIdClienteAndAnioAndMesOrderByIdDesc(ruc, anio, mes);
        
        dto.setBeneficiarios(mapBeneficiarios(beneficiariosList));
        dto.setRegistros(mapRegistros(registrosList));

        return dto;
    }

    public BeneficiarioDTO getBeneficiarioById(int id) {
        Beneficiario entity = beneficiarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Beneficiario no encontrado con ID: " + id));
        return mapBeneficiarios(List.of(entity)).get(0);
    }

    @Transactional
    public void save(Bf3800DTO dto) {
        // Save Master Data
        Bf3800Data data = Bf3800Data.builder()
                .idCliente(dto.getIdCliente())
                .anio(dto.getAnio())
                .mes(dto.getMes())
                .observacion(dto.getObservacion())
                .mail(dto.getMail())
                .build();
        dataRepository.save(data);

        // Save Beneficiarios (Cascades to nested lists)
        // First delete old ones for this period to avoid duplicates (standard in this legacy app)
        List<Beneficiario> old = beneficiarioRepository.findByIdClienteAndAnioAndMes(dto.getIdCliente(), dto.getAnio(), dto.getMes());
        beneficiarioRepository.deleteAll(old);

        if (dto.getBeneficiarios() != null) {
            List<Beneficiario> entities = dto.getBeneficiarios().stream().map(this::mapToEntity).collect(Collectors.toList());
            beneficiarioRepository.saveAll(entities);
        }

        // Save Registro if any
        if (dto.getRegistros() != null && !dto.getRegistros().isEmpty()) {
            Bf3800RegistroDTO regDto = dto.getRegistros().get(0);
            Bf3800Registro reg = Bf3800Registro.builder()
                    .idCliente(dto.getIdCliente())
                    .anio(dto.getAnio())
                    .mes(dto.getMes())
                    .idTipo(regDto.getTipo().getId())
                    .fecha(regDto.getFecha())
                    .nroOrden(regDto.getNroOrden())
                    .nroRectificacion(regDto.getNroRectificacion())
                    .build();
            registroRepository.save(reg);
        }
    }

    // Helper mappers
    private List<BeneficiarioDTO> mapBeneficiarios(List<Beneficiario> entities) {
        return entities.stream().map(e -> {
            BeneficiarioDTO dto = new BeneficiarioDTO();
            dto.setId(e.getId());
            dto.setIdCliente(e.getIdCliente());
            dto.setAnio(e.getAnio());
            dto.setMes(e.getMes());
            dto.setFechaFormato(e.getFechaFormato());
            dto.setCondicionBeneficiario(e.getCondicionBeneficiario());
            dto.setNombresBeneficiario(e.getNombresBeneficiario());
            dto.setPaisBeneficiario(e.getPaisBeneficiario());
            dto.setFechaNacimientoBeneficiario(e.getFechaNacimientoBeneficiario());
            dto.setNacionalidadBeneficiario(e.getNacionalidadBeneficiario());
            dto.setTipoDocumentoBeneficiario(e.getTipoDocumentoBeneficiario());
            dto.setNumeroDocumentoBeneficiario(e.getNumeroDocumentoBeneficiario());
            dto.setNitBeneficiario(e.getNitBeneficiario());
            dto.setRucBeneficiario(e.getRucBeneficiario());
            dto.setEstadoCivilBeneficiario(e.getEstadoCivilBeneficiario());
            dto.setNombresConyugeBeneficiario(e.getNombresConyugeBeneficiario());
            dto.setTipoDocumentoConyugeBeneficiario(e.getTipoDocumentoConyugeBeneficiario());
            dto.setNumeroDocumentoConyugeBeneficiario(e.getNumeroDocumentoConyugeBeneficiario());
            dto.setRegimenPatrimonialBeneficiario(e.getRegimenPatrimonialBeneficiario());
            dto.setFechaRegimenPatrimonialBeneficiario(e.getFechaRegimenPatrimonialBeneficiario());
            dto.setRelacionPersonaJuridicaBeneficiario(e.getRelacionPersonaJuridicaBeneficiario());
            dto.setCorreoElectronicoBeneficiario(e.getCorreoElectronicoBeneficiario());
            dto.setNumeroTelefonicoBeneficiario(e.getNumeroTelefonicoBeneficiario());
            dto.setDireccionBeneficiario(e.getDireccionBeneficiario());
            dto.setNumeroAccionesBeneficiario(e.getNumeroAccionesBeneficiario());
            dto.setParticipacionBeneficiario(e.getParticipacionBeneficiario());
            dto.setValorUnitarioBeneficiario(e.getValorUnitarioBeneficiario());
            dto.setTipoAccionesBeneficiario(e.getTipoAccionesBeneficiario());
            dto.setFechaCiertaBeneficiario(e.getFechaCiertaBeneficiario());
            dto.setLugarDepositoBeneficiario(e.getLugarDepositoBeneficiario());
            dto.setBeneficiarioIndirecto(e.getBeneficiarioIndirecto());
            dto.setPersPropiedadIndirecta(e.getPersPropiedadIndirecta());
            dto.setNombrePersEnteJuridico(e.getNombrePersEnteJuridico());
            dto.setPaisPersEnteJuridico(e.getPaisPersEnteJuridico());
            dto.setCreacionPersEnteJuridico(e.getCreacionPersEnteJuridico());
            dto.setConstitucionPersEnteJuridico(e.getConstitucionPersEnteJuridico());
            dto.setRegistroPersEnteJuridico(e.getRegistroPersEnteJuridico());
            dto.setPaisResidenciaPersEnteJuridico(e.getPaisResidenciaPersEnteJuridico());
            dto.setNitPersEnteJuridico(e.getNitPersEnteJuridico());
            dto.setRucPersEnteJuridico(e.getRucPersEnteJuridico());
            dto.setDireccionPersEnteJuridico(e.getDireccionPersEnteJuridico());
            dto.setBeneficiarioControl(e.getBeneficiarioControl());
            dto.setTextoEnteJuridico(e.getTextoEnteJuridico());
            dto.setCalidadEnteJuridico(e.getCalidadEnteJuridico());
            dto.setFechaBeneficiarioEnteJuridico(e.getFechaBeneficiarioEnteJuridico());
            
            if (e.getPersonaCapitalPPJJ() != null) {
                dto.setPersonaCapitalPPJJ(e.getPersonaCapitalPPJJ().stream().map(c -> {
                    PersonaCapitalPPJJDTO cDto = new PersonaCapitalPPJJDTO();
                    cDto.setIdBeneficiario(c.getIdBeneficiario());
                    cDto.setNroOrden(c.getNroOrden());
                    cDto.setFechaCierta(c.getFechaCierta());
                    cDto.setNombresApellidos(c.getNombresApellidos());
                    cDto.setValorNominal(c.getValorNominal());
                    cDto.setParticipacionDirecta(c.getParticipacionDirecta());
                    cDto.setBeneficiarioSiNo(c.getBeneficiarioSiNo());
                    cDto.setObservacion(c.getObservacion());
                    return cDto;
                }).collect(Collectors.toList()));
            }
            
            if (e.getPersonaCadenaTitularidad() != null) {
                dto.setPersonaCadenaTitularidad(e.getPersonaCadenaTitularidad().stream().map(t -> {
                    PersonaCadenaTitularidadDTO tDto = new PersonaCadenaTitularidadDTO();
                    tDto.setIdBeneficiario(t.getIdBeneficiario());
                    tDto.setNroOrden(t.getNroOrden());
                    tDto.setTipoPersona(t.getTipoPersona());
                    tDto.setNombresApellidos(t.getNombresApellidos());
                    tDto.setPersonaIntermediaria(t.getPersonaIntermediaria());
                    tDto.setParticipacionIntermediaria(t.getParticipacionIntermediaria());
                    tDto.setParticipacionIndirecta(t.getParticipacionIndirecta());
                    tDto.setBeneficiarioSiNo(t.getBeneficiarioSiNo());
                    tDto.setObservacion(t.getObservacion());
                    return tDto;
                }).collect(Collectors.toList()));
            }

            return dto;
        }).collect(Collectors.toList());
    }

    private List<Bf3800RegistroDTO> mapRegistros(List<Bf3800Registro> entities) {
        return entities.stream().map(e -> Bf3800RegistroDTO.builder()
                .id(e.getId())
                .tipo(new Gclass(e.getIdTipo(), ""))
                .fecha(e.getFecha())
                .nroOrden(e.getNroOrden())
                .nroRectificacion(e.getNroRectificacion())
                .build()).collect(Collectors.toList());
    }

    private Beneficiario mapToEntity(BeneficiarioDTO dto) {
        Beneficiario e = new Beneficiario();
        e.setIdCliente(dto.getIdCliente());
        e.setAnio(dto.getAnio());
        e.setMes(dto.getMes());
        e.setFechaFormato(dto.getFechaFormato());
        e.setCondicionBeneficiario(dto.getCondicionBeneficiario());
        e.setNombresBeneficiario(dto.getNombresBeneficiario());
        e.setPaisBeneficiario(dto.getPaisBeneficiario());
        e.setFechaNacimientoBeneficiario(dto.getFechaNacimientoBeneficiario());
        e.setNacionalidadBeneficiario(dto.getNacionalidadBeneficiario());
        e.setTipoDocumentoBeneficiario(dto.getTipoDocumentoBeneficiario());
        e.setNumeroDocumentoBeneficiario(dto.getNumeroDocumentoBeneficiario());
        e.setNitBeneficiario(dto.getNitBeneficiario());
        e.setRucBeneficiario(dto.getRucBeneficiario());
        e.setEstadoCivilBeneficiario(dto.getEstadoCivilBeneficiario());
        e.setNombresConyugeBeneficiario(dto.getNombresConyugeBeneficiario());
        e.setTipoDocumentoConyugeBeneficiario(dto.getTipoDocumentoConyugeBeneficiario());
        e.setNumeroDocumentoConyugeBeneficiario(dto.getNumeroDocumentoConyugeBeneficiario());
        e.setRegimenPatrimonialBeneficiario(dto.getRegimenPatrimonialBeneficiario());
        e.setFechaRegimenPatrimonialBeneficiario(dto.getFechaRegimenPatrimonialBeneficiario());
        e.setRelacionPersonaJuridicaBeneficiario(dto.getRelacionPersonaJuridicaBeneficiario());
        e.setCorreoElectronicoBeneficiario(dto.getCorreoElectronicoBeneficiario());
        e.setNumeroTelefonicoBeneficiario(dto.getNumeroTelefonicoBeneficiario());
        e.setDireccionBeneficiario(dto.getDireccionBeneficiario());
        e.setNumeroAccionesBeneficiario(dto.getNumeroAccionesBeneficiario());
        e.setParticipacionBeneficiario(dto.getParticipacionBeneficiario());
        e.setValorUnitarioBeneficiario(dto.getValorUnitarioBeneficiario());
        e.setTipoAccionesBeneficiario(dto.getTipoAccionesBeneficiario());
        e.setFechaCiertaBeneficiario(dto.getFechaCiertaBeneficiario());
        e.setLugarDepositoBeneficiario(dto.getLugarDepositoBeneficiario());
        e.setBeneficiarioIndirecto(dto.getBeneficiarioIndirecto());
        e.setPersPropiedadIndirecta(dto.getPersPropiedadIndirecta());
        e.setNombrePersEnteJuridico(dto.getNombrePersEnteJuridico());
        e.setPaisPersEnteJuridico(dto.getPaisPersEnteJuridico());
        e.setCreacionPersEnteJuridico(dto.getCreacionPersEnteJuridico());
        e.setConstitucionPersEnteJuridico(dto.getConstitucionPersEnteJuridico());
        e.setRegistroPersEnteJuridico(dto.getRegistroPersEnteJuridico());
        e.setPaisResidenciaPersEnteJuridico(dto.getPaisResidenciaPersEnteJuridico());
        e.setNitPersEnteJuridico(dto.getNitPersEnteJuridico());
        e.setRucPersEnteJuridico(dto.getRucPersEnteJuridico());
        e.setDireccionPersEnteJuridico(dto.getDireccionPersEnteJuridico());
        e.setBeneficiarioControl(dto.getBeneficiarioControl());
        e.setTextoEnteJuridico(dto.getTextoEnteJuridico());
        e.setCalidadEnteJuridico(dto.getCalidadEnteJuridico());
        e.setFechaBeneficiarioEnteJuridico(dto.getFechaBeneficiarioEnteJuridico());

        if (dto.getPersonaCapitalPPJJ() != null) {
            e.setPersonaCapitalPPJJ(dto.getPersonaCapitalPPJJ().stream().map(cDto -> {
                PersonaCapitalPPJJ c = new PersonaCapitalPPJJ();
                c.setNombresApellidos(cDto.getNombresApellidos());
                c.setNroOrden(cDto.getNroOrden());
                c.setFechaCierta(cDto.getFechaCierta());
                c.setValorNominal(cDto.getValorNominal());
                c.setParticipacionDirecta(cDto.getParticipacionDirecta());
                c.setBeneficiarioSiNo(cDto.getBeneficiarioSiNo());
                c.setObservacion(cDto.getObservacion());
                return c;
            }).collect(Collectors.toList()));
        }

        if (dto.getPersonaCadenaTitularidad() != null) {
            e.setPersonaCadenaTitularidad(dto.getPersonaCadenaTitularidad().stream().map(tDto -> {
                PersonaCadenaTitularidad t = new PersonaCadenaTitularidad();
                t.setNombresApellidos(tDto.getNombresApellidos());
                t.setNroOrden(tDto.getNroOrden());
                t.setTipoPersona(tDto.getTipoPersona());
                t.setPersonaIntermediaria(tDto.getPersonaIntermediaria());
                t.setParticipacionIntermediaria(tDto.getParticipacionIntermediaria());
                t.setParticipacionIndirecta(tDto.getParticipacionIndirecta());
                t.setBeneficiarioSiNo(tDto.getBeneficiarioSiNo());
                t.setObservacion(tDto.getObservacion());
                return t;
            }).collect(Collectors.toList()));
        }

        return e;
    }
}
