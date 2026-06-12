package com.joa.prexixionapi.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joa.prexixionapi.entities.LoginProcesos;
import com.joa.prexixionapi.entities.LoginProcesosId;
import com.joa.prexixionapi.entities.ServicioRegistro;
import com.joa.prexixionapi.repositories.LoginProcesosJpaRepository;
import com.joa.prexixionapi.repositories.LoginProcesosRepository;
import com.joa.prexixionapi.dto.LoginProcesosDataTablesRequest;
import com.joa.prexixionapi.dto.LoginProcesosDataTablesResponse;

import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LoginProcesosService {

    @Autowired
    private LoginProcesosRepository loginProcesosRepository;

    @Autowired
    private LoginProcesosJpaRepository loginProcesosJpaRepository;

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
        
        return new String[]{globalMin != null ? globalMin : "", globalMax != null ? globalMax : ""};
    }

    public LoginProcesosDataTablesResponse listServerSide(LoginProcesosDataTablesRequest req) {
        LoginProcesosDataTablesResponse response = new LoginProcesosDataTablesResponse();

        List<LoginProcesos> all = new ArrayList<>();

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

                all.addAll(loginProcesosRepository.list(anioStr, mesStr, null, null));

                // ... incremento del mes/año ...
                if (currMes == 12) {
                    currAnio++;
                    currMes = 1;
                } else {
                    currMes++;
                }
            }
        }

        int totalCount = all.size();

        // Aplicar filtros en memoria
        List<LoginProcesos> filtered = new ArrayList<>();

        int confirmacionSi = 0;
        int confirmacionNo = 0;
        int confirmacionNa = 0;

        int sireCvSi = 0;
        int sireCvNo = 0;
        int sireCvNa = 0;

        int pdtSi = 0;
        int pdtNo = 0;
        int pdtNa = 0;

        for (LoginProcesos c : all) {
            if (matchesFilter(c, req)) {
                filtered.add(c);

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
                if (c.getRegistros() != null && !c.getRegistros().isEmpty()) {
                    ServicioRegistro reg = c.getRegistros().get(0);
                    if (reg != null && reg.getNroOrden() != null && !reg.getNroOrden().trim().isEmpty()) {
                        hasPdt = true;
                    }
                }
                if (hasPdt) {
                    pdtSi++;
                } else {
                    pdtNo++;
                }
            }
        }

        response.setRecordsTotal(totalCount);
        response.setRecordsFiltered(filtered.size());
        response.setData(filtered);

        response.setConfirmacionSi(confirmacionSi);
        response.setConfirmacionNo(confirmacionNo);
        response.setConfirmacionNa(confirmacionNa);

        response.setSireCvSi(sireCvSi);
        response.setSireCvNo(sireCvNo);
        response.setSireCvNa(sireCvNa);

        response.setPdtSi(pdtSi);
        response.setPdtNo(pdtNo);
        response.setPdtNa(pdtNa);

        return response;
    }

    private boolean matchesFilter(LoginProcesos c, LoginProcesosDataTablesRequest req) {
        // 1. Search
        if (req.getSearch() != null && !req.getSearch().trim().isEmpty()) {
            String q = req.getSearch().toLowerCase().trim();
            boolean match = (c.getRuc() != null && c.getRuc().toLowerCase().contains(q))
                    || (c.getRazonSocial() != null && c.getRazonSocial().toLowerCase().contains(q))
                    || (c.getNombreCortoSigner() != null && c.getNombreCortoSigner().toLowerCase().contains(q))
                    || (c.getGrupoEconomico() != null && c.getGrupoEconomico().getDescripcion() != null
                            && c.getGrupoEconomico().getDescripcion().toLowerCase().contains(q));
            if (!match)
                return false;
        }

        // 2. Estados
        if (req.getEstadosString() != null && !req.getEstadosString().trim().isEmpty()) {
            List<String> list = parseCsv(req.getEstadosString());
            String val = c.getIdEstado() != null ? String.valueOf(c.getIdEstado()) : "0";
            if (!list.contains(val))
                return false;
        }

        // 3. Categorias (from signerNivel.categoria.id)
        if (req.getCategoriasString() != null && !req.getCategoriasString().trim().isEmpty()) {
            List<String> list = parseCsv(req.getCategoriasString());
            String val = (c.getSignerNivel() != null && c.getSignerNivel().getCategoria() != null)
                    ? String.valueOf(c.getSignerNivel().getCategoria().getId())
                    : "0";
            if (!list.contains(val))
                return false;
        }

        // 4. CategoriaStore
        if (req.getCategoriaStoreString() != null && !req.getCategoriaStoreString().trim().isEmpty()) {
            List<String> list = parseCsv(req.getCategoriaStoreString());
            String val = (c.getSignerNivel() != null)
                    ? String.valueOf(c.getSignerNivel().getCategoriaStore())
                    : "0";
            if (!list.contains(val))
                return false;
        }

        // 5. GrupoEconomico
        if (req.getGrupoEconomicoString() != null && !req.getGrupoEconomicoString().trim().isEmpty()) {
            List<String> list = parseCsv(req.getGrupoEconomicoString());
            String val = (c.getGrupoEconomico() != null && c.getGrupoEconomico().getId() != 0)
                    ? String.valueOf(c.getGrupoEconomico().getId())
                    : "0";
            if (!list.contains(val))
                return false;
        }

        // 6. Grupos
        if (req.getGruposString() != null && !req.getGruposString().trim().isEmpty()) {
            List<String> list = parseCsv(req.getGruposString());
            String val = c.getY() != null ? c.getY() : "";
            if (!list.contains(val))
                return false;
        }

        // 7. ConfirmacionVentas
        if (req.getConfirmacionVentasString() != null && !req.getConfirmacionVentasString().trim().isEmpty()) {
            List<String> list = parseCsv(req.getConfirmacionVentasString());
            String val = c.getConfirmacionVentas() != null ? String.valueOf(c.getConfirmacionVentas()) : "0";
            if (!list.contains(val))
                return false;
        }

        // 8. ConfirmacionCompras
        if (req.getConfirmacionComprasString() != null && !req.getConfirmacionComprasString().trim().isEmpty()) {
            List<String> list = parseCsv(req.getConfirmacionComprasString());
            String val = c.getConfirmacionCompras() != null ? String.valueOf(c.getConfirmacionCompras()) : "0";
            if (!list.contains(val))
                return false;
        }

        // 9. PreLiquidacion
        if (req.getPreLiquidacionString() != null && !req.getPreLiquidacionString().trim().isEmpty()) {
            List<String> list = parseCsv(req.getPreLiquidacionString());
            String val = c.getPreLiquidacion() != null ? String.valueOf(c.getPreLiquidacion()) : "0";
            if (!list.contains(val))
                return false;
        }

        // 10. Confirmacion
        if (req.getConfirmacionString() != null && !req.getConfirmacionString().trim().isEmpty()) {
            List<String> list = parseCsv(req.getConfirmacionString());
            String val = c.getConfirmacion() != null ? String.valueOf(c.getConfirmacion()) : "0";
            if (!list.contains(val))
                return false;
        }

        // 11. SireCv
        if (req.getSireCvString() != null && !req.getSireCvString().trim().isEmpty()) {
            List<String> list = parseCsv(req.getSireCvString());
            String val = c.getSireCV() != null ? String.valueOf(c.getSireCV()) : "0";
            if (!list.contains(val))
                return false;
        }

        // 12. Pdt
        if (req.getPdtString() != null && !req.getPdtString().trim().isEmpty()) {
            List<String> list = parseCsv(req.getPdtString());
            boolean hasOrder = false;
            if (c.getRegistros() != null && !c.getRegistros().isEmpty()) {
                ServicioRegistro reg = c.getRegistros().get(0);
                if (reg != null && reg.getNroOrden() != null && !reg.getNroOrden().trim().isEmpty()) {
                    hasOrder = true;
                }
            }
            String val = hasOrder ? "1" : "0";
            if (!list.contains(val))
                return false;
        }

        // 13. fVencimiento range
        if (c.getfVencimiento() != null) {
            String fVenc = c.getfVencimiento();
            if (req.getFVencimientoMin() != null && !req.getFVencimientoMin().isEmpty()) {
                if (fVenc.compareTo(req.getFVencimientoMin()) < 0)
                    return false;
            }
            if (req.getFVencimientoMax() != null && !req.getFVencimientoMax().isEmpty()) {
                if (fVenc.compareTo(req.getFVencimientoMax()) > 0)
                    return false;
            }
        }

        return true;
    }

    private List<String> parseCsv(String csv) {
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    public List<LoginProcesos> list(String anio, String mes, String estados, String grupos) {
        return loginProcesosRepository.list(anio, mes, estados, grupos);
    }

    public LoginProcesos getOne(String ruc, String anio, String mes) {
        LoginProcesos c = loginProcesosRepository.getOne(ruc, anio, mes);

        if (c == null) {
            c = new LoginProcesos();
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
    public int insertUpdate(LoginProcesos lp) {
        try {
            LoginProcesosId id = new LoginProcesosId(lp.getRuc(), lp.getAnio(), lp.getMes());
            lp.setId(id);

            boolean exists = loginProcesosJpaRepository.existsById(id);

            if (exists) {
                loginProcesosJpaRepository.save(lp);
                return 2; // update
            } else {
                lp.setVersion(1);
                loginProcesosJpaRepository.save(lp);
                return 1; // insert
            }
        } catch (OptimisticLockException e) {
            log.warn("Optimistic lock exception on LoginProcesos: {}", lp.getRuc());
            return 10;
        } catch (Exception e) {
            log.error("Error inserting/updating LoginProcesos", e);
            throw e;
        }
    }
}
