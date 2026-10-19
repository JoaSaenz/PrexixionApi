package com.joa.prexixionapi.modules.beneficiarioFinal.repositories;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import com.joa.prexixionapi.modules.beneficiarioFinal.dto.BeneficiarioFinalDTO;
import com.joa.prexixionapi.modules.beneficiarioFinal.dto.BeneficiarioFinalCapitalDTO;
import com.joa.prexixionapi.modules.beneficiarioFinal.dto.BeneficiarioFinalCadenaTitularidadDTO;
import com.joa.prexixionapi.modules.beneficiarioFinal.repositories.BeneficiarioFinalRepository;

@Repository
@RequiredArgsConstructor
public class BeneficiarioFinalRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final RowMapper<BeneficiarioFinalDTO> rowMapper = (rs, rowNum) -> {
        BeneficiarioFinalDTO dto = new BeneficiarioFinalDTO();
        dto.setId(rs.getInt("id"));
        dto.setIdCliente(rs.getString("idCliente"));
        dto.setAnio(rs.getString("anio"));
        dto.setMes(rs.getString("mes"));
        dto.setFechaFormato(rs.getString("fechaFormato"));
        dto.setCondicionBeneficiario(rs.getString("condicionBeneficiario"));
        dto.setNombresBeneficiario(rs.getString("nombresBeneficiario"));
        dto.setPaisBeneficiario(rs.getString("paisBeneficiario"));
        dto.setFechaNacimientoBeneficiario(rs.getString("fechaNacimientoBeneficiario"));
        dto.setNacionalidadBeneficiario(rs.getString("nacionalidadBeneficiario"));
        dto.setTipoDocumentoBeneficiario(rs.getString("tipoDocumentoBeneficiario"));
        dto.setNumeroDocumentoBeneficiario(rs.getString("numeroDocumentoBeneficiario"));
        dto.setNitBeneficiario(rs.getString("nitBeneficiario"));
        dto.setRucBeneficiario(rs.getString("rucBeneficiario"));
        dto.setEstadoCivilBeneficiario(rs.getString("estadoCivilBeneficiario"));
        dto.setNombresConyugeBeneficiario(rs.getString("nombresConyugeBeneficiario"));
        dto.setTipoDocumentoConyugeBeneficiario(rs.getString("tipoDocumentoConyugeBeneficiario"));
        dto.setNumeroDocumentoConyugeBeneficiario(rs.getString("numeroDocumentoConyugeBeneficiario"));
        dto.setRegimenPatrimonialBeneficiario(rs.getString("regimenPatrimonialBeneficiario"));
        dto.setFechaRegimenPatrimonialBeneficiario(rs.getString("fechaRegimenPatrimonialBeneficiario"));
        dto.setRelacionPersonaJuridicaBeneficiario(rs.getString("relacionPersonaJuridicaBeneficiario"));
        dto.setCorreoElectronicoBeneficiario(rs.getString("correoElectronicoBeneficiario"));
        dto.setNumeroTelefonicoBeneficiario(rs.getString("numeroTelefonicoBeneficiario"));
        dto.setDireccionBeneficiario(rs.getString("direccionBeneficiario"));
        dto.setNumeroAccionesBeneficiario(rs.getString("numeroAccionesBeneficiario"));
        dto.setParticipacionBeneficiario(rs.getString("participacionBeneficiario"));
        dto.setValorUnitarioBeneficiario(rs.getString("valorUnitarioBeneficiario"));
        dto.setTipoAccionesBeneficiario(rs.getString("tipoAccionesBeneficiario"));
        dto.setFechaCiertaBeneficiario(rs.getString("fechaCiertaBeneficiario"));
        dto.setLugarDepositoBeneficiario(rs.getString("lugarDepositoBeneficiario"));
        dto.setBeneficiarioIndirecto(rs.getString("beneficiarioIndirecto"));
        dto.setPersPropiedadIndirecta(rs.getString("persPropiedadIndirecta"));
        dto.setNombrePersEnteJuridico(rs.getString("nombrePersEnteJuridico"));
        dto.setPaisPersEnteJuridico(rs.getString("paisPersEnteJuridico"));
        dto.setCreacionPersEnteJuridico(rs.getString("creacionPersEnteJuridico"));
        dto.setConstitucionPersEnteJuridico(rs.getString("constitucionPersEnteJuridico"));
        dto.setRegistroPersEnteJuridico(rs.getString("registroPersEnteJuridico"));
        dto.setPaisResidenciaPersEnteJuridico(rs.getString("paisResidenciaPersEnteJuridico"));
        dto.setNitPersEnteJuridico(rs.getString("nitPersEnteJuridico"));
        dto.setRucPersEnteJuridico(rs.getString("rucPersEnteJuridico"));
        dto.setDireccionPersEnteJuridico(rs.getString("direccionPersEnteJuridico"));
        dto.setBeneficiarioControl(rs.getString("beneficiarioControl"));
        dto.setTextoEnteJuridico(rs.getString("textoEnteJuridico"));
        dto.setCalidadEnteJuridico(rs.getString("calidadEnteJuridico"));
        dto.setFechaBeneficiarioEnteJuridico(rs.getString("fechaBeneficiarioEnteJuridico"));
        return dto;
    };

    private final RowMapper<BeneficiarioFinalCapitalDTO> capitalRowMapper = (rs, rowNum) -> {
        BeneficiarioFinalCapitalDTO dto = new BeneficiarioFinalCapitalDTO();
        dto.setIdBeneficiario(rs.getInt("idBeneficiario"));
        dto.setNroOrden(rs.getString("nroOrden"));
        dto.setFechaCierta(rs.getString("fechaCierta"));
        dto.setNombresApellidos(rs.getString("nombresApellidos"));
        dto.setValorNominal(rs.getString("valorNominal"));
        dto.setParticipacionDirecta(rs.getString("participacionDirecta"));
        dto.setBeneficiarioSiNo(rs.getString("beneficiarioSiNo"));
        dto.setObservacion(rs.getString("observacion"));
        return dto;
    };

    private final RowMapper<BeneficiarioFinalCadenaTitularidadDTO> cadenaRowMapper = (rs, rowNum) -> {
        BeneficiarioFinalCadenaTitularidadDTO dto = new BeneficiarioFinalCadenaTitularidadDTO();
        dto.setIdBeneficiario(rs.getInt("idBeneficiario"));
        dto.setNroOrden(rs.getString("nroOrden"));
        dto.setTipoPersona(rs.getString("tipoPersona"));
        dto.setNombresApellidos(rs.getString("nombresApellidos"));
        dto.setPersonaIntermediaria(rs.getString("personaIntermediaria"));
        dto.setParticipacionIntermediaria(rs.getString("participacionIntermediaria"));
        dto.setParticipacionIndirecta(rs.getString("participacionIndirecta"));
        dto.setBeneficiarioSiNo(rs.getString("beneficiarioSiNo"));
        dto.setObservacion(rs.getString("observacion"));
        return dto;
    };

    public Optional<BeneficiarioFinalDTO> findById(int id) {
        String sql = "SELECT * FROM bf3800_beneficiarios WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
        .addValue("id", id);
        List<BeneficiarioFinalDTO> list = jdbcTemplate.query(sql, params, rowMapper);
        
        for (BeneficiarioFinalDTO b : list) {
            String capSql = "SELECT * FROM persona_capital_ppjj WHERE idBeneficiario = :idB";
            MapSqlParameterSource params1 = new MapSqlParameterSource()
                .addValue("idB", b.getId());
            b.setPersonaCapitalPPJJ(jdbcTemplate.query(capSql, params1, capitalRowMapper));
            
            String cadSql = "SELECT * FROM persona_cadena_titularidad WHERE idBeneficiario = :idB";
            MapSqlParameterSource params2 = new MapSqlParameterSource()
                .addValue("idB", b.getId());
            b.setPersonaCadenaTitularidad(jdbcTemplate.query(cadSql, params2, cadenaRowMapper));
        }
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public List<BeneficiarioFinalDTO> findByIdClienteAndAnioAndMes(String idCliente, String anio, String mes) {
        String sql = "SELECT * FROM bf3800_beneficiarios WHERE idCliente = :idCliente AND anio = :anio AND mes = :mes";
        MapSqlParameterSource params = new MapSqlParameterSource()
        .addValue("idCliente", idCliente)
        .addValue("anio", anio)
        .addValue("mes", mes);
        List<BeneficiarioFinalDTO> list = jdbcTemplate.query(sql, params, rowMapper);
        
        for (BeneficiarioFinalDTO b : list) {
            String capSql = "SELECT * FROM persona_capital_ppjj WHERE idBeneficiario = :idB";
            MapSqlParameterSource params1 = new MapSqlParameterSource()
                .addValue("idB", b.getId());
            b.setPersonaCapitalPPJJ(jdbcTemplate.query(capSql, params1, capitalRowMapper));
            
            String cadSql = "SELECT * FROM persona_cadena_titularidad WHERE idBeneficiario = :idB";
            MapSqlParameterSource params2 = new MapSqlParameterSource()
                .addValue("idB", b.getId());
            b.setPersonaCadenaTitularidad(jdbcTemplate.query(cadSql, params2, cadenaRowMapper));
        }
        return list;
    }

    public void deleteAllByPeriod(String idCliente, String anio, String mes) {
        List<BeneficiarioFinalDTO> list = findByIdClienteAndAnioAndMes(idCliente, anio, mes);
        for (BeneficiarioFinalDTO b : list) {
            MapSqlParameterSource params1 = new MapSqlParameterSource().addValue("idB", b.getId());
            jdbcTemplate.update("DELETE FROM persona_capital_ppjj WHERE idBeneficiario = :idB", params1);
            MapSqlParameterSource params2 = new MapSqlParameterSource().addValue("idB", b.getId());
            jdbcTemplate.update("DELETE FROM persona_cadena_titularidad WHERE idBeneficiario = :idB", params2);
            MapSqlParameterSource params3 = new MapSqlParameterSource().addValue("id", b.getId());
            jdbcTemplate.update("DELETE FROM bf3800_beneficiarios WHERE id = :id", params3);
        }
    }

    public void insert(BeneficiarioFinalDTO dto) {
        String sql = "INSERT INTO bf3800_beneficiarios (idCliente, anio, mes, fechaFormato, condicionBeneficiario, nombresBeneficiario, paisBeneficiario, fechaNacimientoBeneficiario, nacionalidadBeneficiario, tipoDocumentoBeneficiario, numeroDocumentoBeneficiario, nitBeneficiario, rucBeneficiario, estadoCivilBeneficiario, nombresConyugeBeneficiario, tipoDocumentoConyugeBeneficiario, numeroDocumentoConyugeBeneficiario, regimenPatrimonialBeneficiario, fechaRegimenPatrimonialBeneficiario, relacionPersonaJuridicaBeneficiario, correoElectronicoBeneficiario, numeroTelefonicoBeneficiario, direccionBeneficiario, numeroAccionesBeneficiario, participacionBeneficiario, valorUnitarioBeneficiario, tipoAccionesBeneficiario, fechaCiertaBeneficiario, lugarDepositoBeneficiario, beneficiarioIndirecto, persPropiedadIndirecta, nombrePersEnteJuridico, paisPersEnteJuridico, creacionPersEnteJuridico, constitucionPersEnteJuridico, registroPersEnteJuridico, paisResidenciaPersEnteJuridico, nitPersEnteJuridico, rucPersEnteJuridico, direccionPersEnteJuridico, beneficiarioControl, textoEnteJuridico, calidadEnteJuridico, fechaBeneficiarioEnteJuridico) VALUES (:idCliente, :anio, :mes, :fechaFormato, :condicionBeneficiario, :nombresBeneficiario, :paisBeneficiario, :fechaNacimientoBeneficiario, :nacionalidadBeneficiario, :tipoDocumentoBeneficiario, :numeroDocumentoBeneficiario, :nitBeneficiario, :rucBeneficiario, :estadoCivilBeneficiario, :nombresConyugeBeneficiario, :tipoDocumentoConyugeBeneficiario, :numeroDocumentoConyugeBeneficiario, :regimenPatrimonialBeneficiario, :fechaRegimenPatrimonialBeneficiario, :relacionPersonaJuridicaBeneficiario, :correoElectronicoBeneficiario, :numeroTelefonicoBeneficiario, :direccionBeneficiario, :numeroAccionesBeneficiario, :participacionBeneficiario, :valorUnitarioBeneficiario, :tipoAccionesBeneficiario, :fechaCiertaBeneficiario, :lugarDepositoBeneficiario, :beneficiarioIndirecto, :persPropiedadIndirecta, :nombrePersEnteJuridico, :paisPersEnteJuridico, :creacionPersEnteJuridico, :constitucionPersEnteJuridico, :registroPersEnteJuridico, :paisResidenciaPersEnteJuridico, :nitPersEnteJuridico, :rucPersEnteJuridico, :direccionPersEnteJuridico, :beneficiarioControl, :textoEnteJuridico, :calidadEnteJuridico, :fechaBeneficiarioEnteJuridico)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("idCliente", dto.getIdCliente());
        params.addValue("anio", dto.getAnio());
        params.addValue("mes", dto.getMes());
        params.addValue("fechaFormato", dto.getFechaFormato());
        params.addValue("condicionBeneficiario", dto.getCondicionBeneficiario());
        params.addValue("nombresBeneficiario", dto.getNombresBeneficiario());
        params.addValue("paisBeneficiario", dto.getPaisBeneficiario());
        params.addValue("fechaNacimientoBeneficiario", dto.getFechaNacimientoBeneficiario());
        params.addValue("nacionalidadBeneficiario", dto.getNacionalidadBeneficiario());
        params.addValue("tipoDocumentoBeneficiario", dto.getTipoDocumentoBeneficiario());
        params.addValue("numeroDocumentoBeneficiario", dto.getNumeroDocumentoBeneficiario());
        params.addValue("nitBeneficiario", dto.getNitBeneficiario());
        params.addValue("rucBeneficiario", dto.getRucBeneficiario());
        params.addValue("estadoCivilBeneficiario", dto.getEstadoCivilBeneficiario());
        params.addValue("nombresConyugeBeneficiario", dto.getNombresConyugeBeneficiario());
        params.addValue("tipoDocumentoConyugeBeneficiario", dto.getTipoDocumentoConyugeBeneficiario());
        params.addValue("numeroDocumentoConyugeBeneficiario", dto.getNumeroDocumentoConyugeBeneficiario());
        params.addValue("regimenPatrimonialBeneficiario", dto.getRegimenPatrimonialBeneficiario());
        params.addValue("fechaRegimenPatrimonialBeneficiario", dto.getFechaRegimenPatrimonialBeneficiario());
        params.addValue("relacionPersonaJuridicaBeneficiario", dto.getRelacionPersonaJuridicaBeneficiario());
        params.addValue("correoElectronicoBeneficiario", dto.getCorreoElectronicoBeneficiario());
        params.addValue("numeroTelefonicoBeneficiario", dto.getNumeroTelefonicoBeneficiario());
        params.addValue("direccionBeneficiario", dto.getDireccionBeneficiario());
        params.addValue("numeroAccionesBeneficiario", dto.getNumeroAccionesBeneficiario());
        params.addValue("participacionBeneficiario", dto.getParticipacionBeneficiario());
        params.addValue("valorUnitarioBeneficiario", dto.getValorUnitarioBeneficiario());
        params.addValue("tipoAccionesBeneficiario", dto.getTipoAccionesBeneficiario());
        params.addValue("fechaCiertaBeneficiario", dto.getFechaCiertaBeneficiario());
        params.addValue("lugarDepositoBeneficiario", dto.getLugarDepositoBeneficiario());
        params.addValue("beneficiarioIndirecto", dto.getBeneficiarioIndirecto());
        params.addValue("persPropiedadIndirecta", dto.getPersPropiedadIndirecta());
        params.addValue("nombrePersEnteJuridico", dto.getNombrePersEnteJuridico());
        params.addValue("paisPersEnteJuridico", dto.getPaisPersEnteJuridico());
        params.addValue("creacionPersEnteJuridico", dto.getCreacionPersEnteJuridico());
        params.addValue("constitucionPersEnteJuridico", dto.getConstitucionPersEnteJuridico());
        params.addValue("registroPersEnteJuridico", dto.getRegistroPersEnteJuridico());
        params.addValue("paisResidenciaPersEnteJuridico", dto.getPaisResidenciaPersEnteJuridico());
        params.addValue("nitPersEnteJuridico", dto.getNitPersEnteJuridico());
        params.addValue("rucPersEnteJuridico", dto.getRucPersEnteJuridico());
        params.addValue("direccionPersEnteJuridico", dto.getDireccionPersEnteJuridico());
        params.addValue("beneficiarioControl", dto.getBeneficiarioControl());
        params.addValue("textoEnteJuridico", dto.getTextoEnteJuridico());
        params.addValue("calidadEnteJuridico", dto.getCalidadEnteJuridico());
        params.addValue("fechaBeneficiarioEnteJuridico", dto.getFechaBeneficiarioEnteJuridico());
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, params, keyHolder);
        int generatedId = keyHolder.getKey().intValue();
        dto.setId(generatedId);

        if (dto.getPersonaCapitalPPJJ() != null) {
            String cSql = "INSERT INTO persona_capital_ppjj (idBeneficiario, nroOrden, fechaCierta, nombresApellidos, valorNominal, participacionDirecta, beneficiarioSiNo, observacion) VALUES (:idB, :nro, :fecha, :nom, :val, :part, :ben, :obs)";
            for (BeneficiarioFinalCapitalDTO c : dto.getPersonaCapitalPPJJ()) {
                MapSqlParameterSource capitalParams = new MapSqlParameterSource()
                    .addValue("idB", generatedId)
                    .addValue("nro", c.getNroOrden() != null ? c.getNroOrden() : "")
                    .addValue("fecha", c.getFechaCierta() != null ? c.getFechaCierta() : "")
                    .addValue("nom", c.getNombresApellidos() != null ? c.getNombresApellidos() : "")
                    .addValue("val", c.getValorNominal() != null ? c.getValorNominal() : "")
                    .addValue("part", c.getParticipacionDirecta() != null ? c.getParticipacionDirecta() : "")
                    .addValue("ben", c.getBeneficiarioSiNo() != null ? c.getBeneficiarioSiNo() : "")
                    .addValue("obs", c.getObservacion() != null ? c.getObservacion() : "");
                jdbcTemplate.update(cSql, capitalParams);
            }
        }
        
        if (dto.getPersonaCadenaTitularidad() != null) {
            String tSql = "INSERT INTO persona_cadena_titularidad (idBeneficiario, nroOrden, tipoPersona, nombresApellidos, personaIntermediaria, participacionIntermediaria, participacionIndirecta, beneficiarioSiNo, observacion) VALUES (:idB, :nro, :tipo, :nom, :pInt, :pInt2, :pInd, :ben, :obs)";
            for (BeneficiarioFinalCadenaTitularidadDTO t : dto.getPersonaCadenaTitularidad()) {
                MapSqlParameterSource cadenaParams = new MapSqlParameterSource()
                    .addValue("idB", generatedId)
                    .addValue("nro", t.getNroOrden() != null ? t.getNroOrden() : "")
                    .addValue("tipo", t.getTipoPersona() != null ? t.getTipoPersona() : "")
                    .addValue("nom", t.getNombresApellidos() != null ? t.getNombresApellidos() : "")
                    .addValue("pInt", t.getPersonaIntermediaria() != null ? t.getPersonaIntermediaria() : "")
                    .addValue("pInt2", t.getParticipacionIntermediaria() != null ? t.getParticipacionIntermediaria() : "")
                    .addValue("pInd", t.getParticipacionIndirecta() != null ? t.getParticipacionIndirecta() : "")
                    .addValue("ben", t.getBeneficiarioSiNo() != null ? t.getBeneficiarioSiNo() : "")
                    .addValue("obs", t.getObservacion() != null ? t.getObservacion() : "");
                jdbcTemplate.update(tSql, cadenaParams);
            }
        }
    }
}
