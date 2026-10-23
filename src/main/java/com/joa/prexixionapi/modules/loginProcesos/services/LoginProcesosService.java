package com.joa.prexixionapi.modules.loginProcesos.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joa.prexixionapi.common.dto.DataTablesResponseDTO;
import com.joa.prexixionapi.common.dto.IndicadorDTO;

import lombok.extern.slf4j.Slf4j;
import com.joa.prexixionapi.modules.loginProcesos.dto.LoginProcesosDTO;
import com.joa.prexixionapi.modules.loginProcesos.dto.LoginProcesosListDTO;
import com.joa.prexixionapi.modules.loginProcesos.services.LoginProcesosService;
import com.joa.prexixionapi.modules.loginProcesos.dto.LoginProcesosRequest;
import com.joa.prexixionapi.modules.loginProcesos.repositories.LoginProcesosRepository;

@Service
@Slf4j
public class LoginProcesosService {

    @Autowired
    private LoginProcesosRepository loginProcesosRepository;

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

            String[] limits = loginProcesosRepository.getVencimientoLimits(anioStr, mesStr);

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

    public DataTablesResponseDTO<LoginProcesosListDTO> list(LoginProcesosRequest req) {
        List<LoginProcesosListDTO> all = new ArrayList<>();

        if (req.getPeriodoI() != null && req.getPeriodoF() != null) {
            String periodoI = req.getPeriodoI();
            String periodoF = req.getPeriodoF();

            int startAnio = Integer.parseInt(periodoI.substring(0, 4));
            int startMes = Integer.parseInt(periodoI.substring(5, 7));
            int endAnio = Integer.parseInt(periodoF.substring(0, 4));
            int endMes = Integer.parseInt(periodoF.substring(5, 7));

            int currAnio = startAnio;
            int currMes = startMes;

            // Bucle en el LoginProcesosService de la API para acumular registros de varios
            // meses
            while (currAnio * 100 + currMes <= endAnio * 100 + endMes) {
                String mesStr = currMes < 10 ? "0" + currMes : String.valueOf(currMes);
                String anioStr = String.valueOf(currAnio);

                all.addAll(loginProcesosRepository.list(anioStr, mesStr, req));

                if (currMes == 12) {
                    currAnio++;
                    currMes = 1;
                } else {
                    currMes++;
                }
            }
        }

        int confirmacionSi = 0;
        int confirmacionNo = 0;
        int confirmacionNa = 0;

        int sireCvSi = 0;
        int sireCvNo = 0;
        int sireCvNa = 0;

        int pdtSi = 0;
        int pdtNo = 0;
        int pdtNa = 0;

        for (LoginProcesosListDTO c : all) {
            // 1. TAKE (confirmacion)
            if (c.getConfirmacion() != null) {
                if (c.getConfirmacion() == 1) {
                    confirmacionSi++;
                } else if (c.getConfirmacion() == 0) {
                    confirmacionNo++;
                } else if (c.getConfirmacion() == 2) {
                    confirmacionNa++;
                }
            } else {
                confirmacionNo++;
            }

            // 2. SIRE (sireCV)
            if (c.getSireCV() != null) {
                if (c.getSireCV() == 1) {
                    sireCvSi++;
                } else if (c.getSireCV() == 0) {
                    sireCvNo++;
                } else if (c.getSireCV() == 2) {
                    sireCvNa++;
                }
            } else {
                sireCvNo++;
            }

            // 3. PDT
            boolean hasPdt = false;
            if (c.getPdtRegistrado() != null && c.getPdtRegistrado() == 1) {
                hasPdt = true;
            }
            if (hasPdt) {
                pdtSi++;
            } else {
                pdtNo++;
            }
        }

        List<IndicadorDTO> indicadores = new ArrayList<>();
        indicadores.add(new IndicadorDTO("TAKE_CONFIRMED", confirmacionSi + confirmacionNa));
        indicadores.add(new IndicadorDTO("TAKE_PENDING", confirmacionNo));
        indicadores.add(new IndicadorDTO("SIRE_PRESENTED", sireCvSi + sireCvNa));
        indicadores.add(new IndicadorDTO("SIRE_PENDING", sireCvNo));
        indicadores.add(new IndicadorDTO("PDT_PRESENTED", pdtSi + pdtNa));
        indicadores.add(new IndicadorDTO("PDT_PENDING", pdtNo));
        indicadores.add(new IndicadorDTO("TOTAL", all.size()));

        return DataTablesResponseDTO.<LoginProcesosListDTO>builder()
                .data(all)
                .indicadores(indicadores)
                .build();
    }

    public LoginProcesosDTO getOne(String ruc, String anio, String mes) {
        LoginProcesosDTO c = loginProcesosRepository.getOne(ruc, anio, mes);

        if (c == null) {
            c = new LoginProcesosDTO();
            c.setRuc(ruc);
            c.setAnio(anio);
            c.setMes(mes);
        }

        if (c.getVersion() == null) {
            c.setVersion(0);
        }

        return c;
    }

    @Transactional
    public int insertUpdate(LoginProcesosDTO lp) {
        try {
            return loginProcesosRepository.insertUpdate(lp);
        } catch (org.springframework.dao.OptimisticLockingFailureException e) {
            log.warn("Optimistic lock exception on LoginProcesosDTO: {}", lp.getRuc());
            return 10;
        } catch (Exception e) {
            log.error("Error inserting/updating LoginProcesosDTO", e);
            throw e;
        }
    }
}
