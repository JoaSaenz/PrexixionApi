package com.joa.prexixionapi.modules.loginCompra.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import com.joa.prexixionapi.modules.loginCompra.repositories.LoginCompraRepository;
import com.joa.prexixionapi.common.dto.DataTablesResponseDTO;
import com.joa.prexixionapi.common.dto.IndicadorDTO;
import com.joa.prexixionapi.modules.loginCompra.dto.LoginCompraRequest;
import com.joa.prexixionapi.modules.loginCompra.dto.LoginCompraListDTO;
import com.joa.prexixionapi.modules.loginCompra.dto.LoginCompraDTO;
import com.joa.prexixionapi.modules.loginCompra.services.LoginCompraService;

@Service
@Slf4j
public class LoginCompraService {

    @Autowired
    private LoginCompraRepository loginCompraRepository;

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

            String[] limits = loginCompraRepository.getVencimientoLimits(anioStr, mesStr);

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

    public DataTablesResponseDTO<LoginCompraListDTO> list(LoginCompraRequest req) {
        DataTablesResponseDTO<LoginCompraListDTO> response = new DataTablesResponseDTO<>();

        List<LoginCompraListDTO> all = new ArrayList<>();

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

                all.addAll(loginCompraRepository.list(anioStr, mesStr, req));

                if (currMes == 12) {
                    currAnio++;
                    currMes = 1;
                } else {
                    currMes++;
                }
            }
        }

        int sireSi = 0, sireNo = 0, sireNa = 0;
        int validacionSunatSi = 0, validacionSunatNo = 0, validacionSunatNa = 0;
        int validacionSi = 0, validacionNo = 0, validacionNa = 0, validacionVal = 0;
        int confirmacionSi = 0, confirmacionNo = 0;

        for (LoginCompraListDTO c : all) {
            // Sire
            if (c.getSireCompras() != null) {
                switch (c.getSireCompras()) {
                    case 0:
                        sireNo++;
                        break;
                    case 1:
                        sireSi++;
                        break;
                    case 2:
                        sireNa++;
                        break;
                }
            } else {
                sireNo++;
            }

            // Validacion Sunat
            if (c.getValidacionSunat() != null) {
                switch (c.getValidacionSunat()) {
                    case 0:
                        validacionSunatNo++;
                        break;
                    case 1:
                        validacionSunatSi++;
                        break;
                    case 2:
                        validacionSunatNa++;
                        break;
                }
            } else {
                validacionSunatNo++;
            }

            // Validacion
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

            // Confirmacion
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
        indicadores.add(new IndicadorDTO("SIRE_SI", sireSi));
        indicadores.add(new IndicadorDTO("SIRE_NO", sireNo));
        indicadores.add(new IndicadorDTO("SIRE_NA", sireNa));

        indicadores.add(new IndicadorDTO("VALIDACION_SUNAT_SI", validacionSunatSi));
        indicadores.add(new IndicadorDTO("VALIDACION_SUNAT_NO", validacionSunatNo));
        indicadores.add(new IndicadorDTO("VALIDACION_SUNAT_NA", validacionSunatNa));

        indicadores.add(new IndicadorDTO("VALIDACION_SI", validacionSi));
        indicadores.add(new IndicadorDTO("VALIDACION_NO", validacionNo));
        indicadores.add(new IndicadorDTO("VALIDACION_NA", validacionNa));
        indicadores.add(new IndicadorDTO("VALIDACION_VAL", validacionVal));

        indicadores.add(new IndicadorDTO("CONFIRMACION_SI", confirmacionSi));
        indicadores.add(new IndicadorDTO("CONFIRMACION_NO", confirmacionNo));

        response.setIndicadores(indicadores);

        return response;
    }

    public LoginCompraDTO getOne(String ruc, String anio, String mes) {
        LoginCompraDTO dto = loginCompraRepository.getOne(ruc, anio, mes);

        if (dto == null) {
            dto = new LoginCompraDTO();
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
    public int insertUpdate(LoginCompraDTO dto) {
        try {
            return loginCompraRepository.insertUpdate(dto);
        } catch (org.springframework.dao.OptimisticLockingFailureException e) {
            log.warn("Optimistic lock exception on LoginCompra: {}", dto.getRuc());
            return 10;
        } catch (Exception e) {
            log.error("Error inserting/updating LoginCompra", e);
            throw e;
        }
    }
}
