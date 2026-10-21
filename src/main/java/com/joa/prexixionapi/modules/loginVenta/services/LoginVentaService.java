package com.joa.prexixionapi.modules.loginVenta.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import com.joa.prexixionapi.modules.loginVenta.repositories.LoginVentaRepository;
import com.joa.prexixionapi.modules.loginVenta.dto.LoginVentaDTO;
import com.joa.prexixionapi.modules.loginVenta.dto.LoginVentaListDTO;
import com.joa.prexixionapi.modules.loginVenta.dto.LoginVentaRequest;
import com.joa.prexixionapi.common.dto.DataTablesResponseDTO;
import com.joa.prexixionapi.common.dto.IndicadorDTO;
import com.joa.prexixionapi.modules.loginVenta.services.LoginVentaService;

@Service
@Slf4j
public class LoginVentaService {

    @Autowired
    private LoginVentaRepository loginVentaRepository;

    public DataTablesResponseDTO<LoginVentaListDTO> list(LoginVentaRequest req) {
        DataTablesResponseDTO<LoginVentaListDTO> response = new DataTablesResponseDTO<>();

        List<LoginVentaListDTO> all = new ArrayList<>();

        if (req.getPeriodoI() != null && req.getPeriodoF() != null) {
            String periodoI = req.getPeriodoI();
            String periodoF = req.getPeriodoF();

            int startAnio = Integer.parseInt(periodoI.substring(0, 4));
            int startMes = Integer.parseInt(periodoI.substring(5, 7));
            int endAnio = Integer.parseInt(periodoF.substring(0, 4));
            int endMes = Integer.parseInt(periodoF.substring(5, 7));

            int currAnio = startAnio;
            int currMes = startMes;

            while (currAnio * 100 + currMes <= endAnio * 100 + endMes) {
                String mesStr = currMes < 10 ? "0" + currMes : String.valueOf(currMes);
                String anioStr = String.valueOf(currAnio);

                all.addAll(loginVentaRepository.list(anioStr, mesStr, req));

                if (currMes == 12) {
                    currAnio++;
                    currMes = 1;
                } else {
                    currMes++;
                }
            }
        }

        int registroSi = 0, registroNo = 0, registroNa = 0;
        int revisionSunatSi = 0, revisionSunatNo = 0, revisionSunatNa = 0;
        int validacionSi = 0, validacionNo = 0, validacionNa = 0, validacionVal = 0;
        int confirmacionSi = 0, confirmacionNo = 0;

        for (LoginVentaListDTO c : all) {
            // Calcular conteos de resumen
            if (c.getRegistro() != null) {
                switch (c.getRegistro()) {
                    case 0:
                        registroNo++;
                        break;
                    case 1:
                        registroSi++;
                        break;
                    case 2:
                        registroNa++;
                        break;
                }
            } else {
                registroNo++;
            }

            if (c.getRevisionSunat() != null) {
                switch (c.getRevisionSunat()) {
                    case 0:
                        revisionSunatNo++;
                        break;
                    case 1:
                        revisionSunatSi++;
                        break;
                    case 2:
                        revisionSunatNa++;
                        break;
                }
            } else {
                revisionSunatNo++;
            }

            if (c.getValidacion() != null) {
                switch (c.getValidacion()) {
                    case 0:
                        validacionNo++;
                        break;
                    case 1:
                        validacionSi++;
                        break;
                    case 2:
                        validacionNa++;
                        break;
                    case 3:
                        validacionVal++;
                        break;
                }
            } else {
                validacionNo++;
            }

            if (c.getConfirmacion() != null) {
                switch (c.getConfirmacion()) {
                    case 0:
                        confirmacionNo++;
                        break;
                    case 1:
                        confirmacionSi++;
                        break;
                }
            } else {
                confirmacionNo++;
            }
        }

        response.setData(all);

        List<IndicadorDTO> indicadores = new ArrayList<>();
        indicadores.add(new IndicadorDTO("REGISTRO_SI", registroSi));
        indicadores.add(new IndicadorDTO("REGISTRO_NO", registroNo));
        indicadores.add(new IndicadorDTO("REGISTRO_NA", registroNa));

        indicadores.add(new IndicadorDTO("REVISION_SUNAT_SI", revisionSunatSi));
        indicadores.add(new IndicadorDTO("REVISION_SUNAT_NO", revisionSunatNo));
        indicadores.add(new IndicadorDTO("REVISION_SUNAT_NA", revisionSunatNa));

        indicadores.add(new IndicadorDTO("VALIDACION_SI", validacionSi));
        indicadores.add(new IndicadorDTO("VALIDACION_NO", validacionNo));
        indicadores.add(new IndicadorDTO("VALIDACION_NA", validacionNa));
        indicadores.add(new IndicadorDTO("VALIDACION_VAL", validacionVal));

        indicadores.add(new IndicadorDTO("CONFIRMACION_SI", confirmacionSi));
        indicadores.add(new IndicadorDTO("CONFIRMACION_NO", confirmacionNo));

        response.setIndicadores(indicadores);

        return response;
    }

    public LoginVentaDTO getOne(String ruc, String anio, String mes) {
        LoginVentaDTO dto = loginVentaRepository.getOne(ruc, anio, mes);

        if (dto == null) {
            dto = new LoginVentaDTO();
            dto.setRuc(ruc);
            dto.setAnio(anio);
            dto.setMes(mes);
        }

        if (dto.getVersion() == null) {
            dto.setVersion(0);
        }

        return dto;
    }

    @Transactional
    public int insertUpdate(LoginVentaDTO dto) {
        try {
            return loginVentaRepository.insertUpdate(dto);
        } catch (org.springframework.dao.OptimisticLockingFailureException e) {
            log.warn("Optimistic lock exception on LoginVenta: {}", dto.getRuc());
            return 10;
        } catch (Exception e) {
            log.error("Error inserting/updating LoginVenta", e);
            throw e;
        }
    }

    public String[] getVencimientoLimits(String periodoI, String periodoF) {
        int startAnio = Integer.parseInt(periodoI.substring(0, 4));
        int startMes = Integer.parseInt(periodoI.substring(5, 7));
        int endAnio = Integer.parseInt(periodoF.substring(0, 4));
        int endMes = Integer.parseInt(periodoF.substring(5, 7));

        int currAnio = startAnio;
        int currMes = startMes;

        String globalMin = null;
        String globalMax = null;

        while (currAnio * 100 + currMes <= endAnio * 100 + endMes) {
            String mesStr = currMes < 10 ? "0" + currMes : String.valueOf(currMes);
            String anioStr = String.valueOf(currAnio);

            String[] limits = loginVentaRepository.getVencimientoLimits(anioStr, mesStr);

            if (limits[0] != null && !limits[0].isEmpty()) {
                if (globalMin == null || limits[0].compareTo(globalMin) < 0) {
                    globalMin = limits[0];
                }
            }
            if (limits[1] != null && !limits[1].isEmpty()) {
                if (globalMax == null || limits[1].compareTo(globalMax) > 0) {
                    globalMax = limits[1];
                }
            }

            if (currMes == 12) {
                currAnio++;
                currMes = 1;
            } else {
                currMes++;
            }
        }

        return new String[] { globalMin != null ? globalMin : "", globalMax != null ? globalMax : "" };
    }
}
