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

    public LoginProcesosDataTablesResponse listServerSide(LoginProcesosDataTablesRequest req) {
        LoginProcesosDataTablesResponse response = new LoginProcesosDataTablesResponse();
        response.setDraw(req.getDraw());

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

            while (currAnio * 100 + currMes <= endAnio * 100 + endMes) {
                String mesStr = currMes < 10 ? "0" + currMes : String.valueOf(currMes);
                String anioStr = String.valueOf(currAnio);

                all.addAll(loginProcesosRepository.list(anioStr, mesStr, null, null, null));

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
        
        int confirmacionVentasSi = 0;
        int confirmacionVentasNo = 0;
        int confirmacionVentasF = 0;

        int confirmacionComprasSi = 0;
        int confirmacionComprasNo = 0;
        int confirmacionComprasF = 0;

        int validerSi = 0;

        int preLiquidacionSi = 0;
        int preLiquidacionNo = 0;
        int preLiquidacionNa = 0;
        int preLiquidacionF = 0;

        int confirmacionSi = 0;
        int confirmacionNo = 0;
        int confirmacionNa = 0;
        int confirmacionF = 0;

        int sireCvSi = 0;
        int sireCvNo = 0;
        int sireCvNa = 0;
        int sireCvF = 0;

        int pdtSi = 0;
        int pdtNo = 0;
        int pdtNa = 0;
        int pdtF = 0;

        for (LoginProcesos c : all) {
            if (matchesFilter(c, req)) {
                filtered.add(c);

                // 1. V-CON (confirmacionVentas)
                if (c.getConfirmacionVentas() != null) {
                    if (c.getConfirmacionVentas() == 1) {
                        confirmacionVentasSi++;
                    } else if (c.getConfirmacionVentas() == 0) {
                        confirmacionVentasNo++;
                    }
                } else {
                    confirmacionVentasNo++;
                }

                // 2. C-CON (confirmacionCompras)
                if (c.getConfirmacionCompras() != null) {
                    if (c.getConfirmacionCompras() == 1) {
                        confirmacionComprasSi++;
                    } else if (c.getConfirmacionCompras() == 0) {
                        confirmacionComprasNo++;
                    }
                } else {
                    confirmacionComprasNo++;
                }

                // validerSi
                int cvVal = c.getConfirmacionVentas() != null ? c.getConfirmacionVentas() : 0;
                int ccVal = c.getConfirmacionCompras() != null ? c.getConfirmacionCompras() : 0;
                if (cvVal == 1 && ccVal == 1) {
                    validerSi++;
                }

                // 3. TAKE B (preLiquidacion)
                if (c.getPreLiquidacion() != null) {
                    if (c.getPreLiquidacion() == 1) {
                        preLiquidacionSi++;
                    } else if (c.getPreLiquidacion() == 0) {
                        preLiquidacionNo++;
                    } else if (c.getPreLiquidacion() == 2) {
                        preLiquidacionNa++;
                    }
                } else {
                    preLiquidacionNo++;
                }

                // 4. TAKE (confirmacion)
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

                // 5. SIRE (sireCV)
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

                // 6. PDT
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

        // Calculos F
        preLiquidacionF = validerSi - preLiquidacionSi - preLiquidacionNa;
        confirmacionF = preLiquidacionSi + preLiquidacionNa - confirmacionSi - confirmacionNa;
        pdtF = confirmacionSi + confirmacionNa - pdtSi - pdtNa;

        response.setRecordsTotal(totalCount);
        response.setRecordsFiltered(filtered.size());
        response.setData(filtered);

        response.setConfirmacionVentasSi(confirmacionVentasSi);
        response.setConfirmacionVentasNo(confirmacionVentasNo);
        response.setConfirmacionVentasF(confirmacionVentasF);

        response.setConfirmacionComprasSi(confirmacionComprasSi);
        response.setConfirmacionComprasNo(confirmacionComprasNo);
        response.setConfirmacionComprasF(confirmacionComprasF);

        response.setPreLiquidacionSi(preLiquidacionSi);
        response.setPreLiquidacionNo(preLiquidacionNo);
        response.setPreLiquidacionNa(preLiquidacionNa);
        response.setPreLiquidacionF(preLiquidacionF);

        response.setConfirmacionSi(confirmacionSi);
        response.setConfirmacionNo(confirmacionNo);
        response.setConfirmacionNa(confirmacionNa);
        response.setConfirmacionF(confirmacionF);

        response.setSireCvSi(sireCvSi);
        response.setSireCvNo(sireCvNo);
        response.setSireCvNa(sireCvNa);
        response.setSireCvF(sireCvF);

        response.setPdtSi(pdtSi);
        response.setPdtNo(pdtNo);
        response.setPdtNa(pdtNa);
        response.setPdtF(pdtF);

        return response;
    }

    private boolean matchesFilter(LoginProcesos c, LoginProcesosDataTablesRequest req) {
        // 1. Search
        if (req.getSearch() != null && !req.getSearch().trim().isEmpty()) {
            String q = req.getSearch().toLowerCase().trim();
            boolean match = (c.getRuc() != null && c.getRuc().toLowerCase().contains(q))
                    || (c.getRazonSocial() != null && c.getRazonSocial().toLowerCase().contains(q))
                    || (c.getNombreCortoSigner() != null && c.getNombreCortoSigner().toLowerCase().contains(q))
                    || (c.getGrupoEconomico() != null && c.getGrupoEconomico().getDescripcion() != null && c.getGrupoEconomico().getDescripcion().toLowerCase().contains(q));
            if (!match) return false;
        }

        // 2. Estados
        if (req.getEstadosString() != null && !req.getEstadosString().trim().isEmpty()) {
            List<String> list = parseCsv(req.getEstadosString());
            String val = c.getIdEstado() != null ? String.valueOf(c.getIdEstado()) : "0";
            if (!list.contains(val)) return false;
        }

        // 3. Categorias (from signerNivel.categoria.id)
        if (req.getCategoriasString() != null && !req.getCategoriasString().trim().isEmpty()) {
            List<String> list = parseCsv(req.getCategoriasString());
            String val = (c.getSignerNivel() != null && c.getSignerNivel().getCategoria() != null)
                    ? String.valueOf(c.getSignerNivel().getCategoria().getId()) : "0";
            if (!list.contains(val)) return false;
        }

        // 4. CategoriaStore
        if (req.getCategoriaStoreString() != null && !req.getCategoriaStoreString().trim().isEmpty()) {
            List<String> list = parseCsv(req.getCategoriaStoreString());
            String val = (c.getSignerNivel() != null)
                    ? String.valueOf(c.getSignerNivel().getCategoriaStore()) : "0";
            if (!list.contains(val)) return false;
        }

        // 5. GrupoEconomico
        if (req.getGrupoEconomicoString() != null && !req.getGrupoEconomicoString().trim().isEmpty()) {
            List<String> list = parseCsv(req.getGrupoEconomicoString());
            String val = (c.getGrupoEconomico() != null && c.getGrupoEconomico().getId() != 0)
                    ? String.valueOf(c.getGrupoEconomico().getId()) : "0";
            if (!list.contains(val)) return false;
        }

        // 6. Grupos
        if (req.getGruposString() != null && !req.getGruposString().trim().isEmpty()) {
            List<String> list = parseCsv(req.getGruposString());
            String val = c.getY() != null ? c.getY() : "";
            if (!list.contains(val)) return false;
        }

        // 7. Equipo2 (DniResponsable2RTB)
        if (req.getEquipo2String() != null && !req.getEquipo2String().trim().isEmpty()) {
            List<String> list = parseCsv(req.getEquipo2String());
            String val = (c.getDniResponsable2RTB() != null && !c.getDniResponsable2RTB().trim().isEmpty())
                    ? c.getDniResponsable2RTB().trim() : "0";
            if (!list.contains(val)) return false;
        }

        // 8. ConfirmacionVentas
        if (req.getConfirmacionVentasString() != null && !req.getConfirmacionVentasString().trim().isEmpty()) {
            List<String> list = parseCsv(req.getConfirmacionVentasString());
            String val = c.getConfirmacionVentas() != null ? String.valueOf(c.getConfirmacionVentas()) : "0";
            if (!list.contains(val)) return false;
        }

        // 9. ConfirmacionCompras
        if (req.getConfirmacionComprasString() != null && !req.getConfirmacionComprasString().trim().isEmpty()) {
            List<String> list = parseCsv(req.getConfirmacionComprasString());
            String val = c.getConfirmacionCompras() != null ? String.valueOf(c.getConfirmacionCompras()) : "0";
            if (!list.contains(val)) return false;
        }

        // 10. PreLiquidacion
        if (req.getPreLiquidacionString() != null && !req.getPreLiquidacionString().trim().isEmpty()) {
            List<String> list = parseCsv(req.getPreLiquidacionString());
            String val = c.getPreLiquidacion() != null ? String.valueOf(c.getPreLiquidacion()) : "0";
            if (!list.contains(val)) return false;
        }

        // 11. Confirmacion
        if (req.getConfirmacionString() != null && !req.getConfirmacionString().trim().isEmpty()) {
            List<String> list = parseCsv(req.getConfirmacionString());
            String val = c.getConfirmacion() != null ? String.valueOf(c.getConfirmacion()) : "0";
            if (!list.contains(val)) return false;
        }

        // 12. SireCv
        if (req.getSireCvString() != null && !req.getSireCvString().trim().isEmpty()) {
            List<String> list = parseCsv(req.getSireCvString());
            String val = c.getSireCV() != null ? String.valueOf(c.getSireCV()) : "0";
            if (!list.contains(val)) return false;
        }

        // 13. Pdt
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
            if (!list.contains(val)) return false;
        }

        // 14. fVencimiento range
        if (c.getfVencimiento() != null) {
            String fVenc = c.getfVencimiento();
            if (req.getFVencimientoMin() != null && !req.getFVencimientoMin().isEmpty()) {
                if (fVenc.compareTo(req.getFVencimientoMin()) < 0) return false;
            }
            if (req.getFVencimientoMax() != null && !req.getFVencimientoMax().isEmpty()) {
                if (fVenc.compareTo(req.getFVencimientoMax()) > 0) return false;
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

    public List<LoginProcesos> list(String anio, String mes, String estados, String grupos, String equipo2) {
        return loginProcesosRepository.list(anio, mes, estados, grupos, equipo2);
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
