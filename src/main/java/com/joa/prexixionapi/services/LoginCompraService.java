package com.joa.prexixionapi.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joa.prexixionapi.entities.Cliente;
import com.joa.prexixionapi.entities.LoginCompra;
import com.joa.prexixionapi.entities.LoginCompraId;
import com.joa.prexixionapi.repositories.LoginCompraJpaRepository;
import com.joa.prexixionapi.repositories.LoginCompraRepository;
import com.joa.prexixionapi.dto.LoginCompraDataTablesRequest;
import com.joa.prexixionapi.dto.LoginCompraDataTablesResponse;

import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LoginCompraService {

    @Autowired
    private LoginCompraRepository loginCompraRepository;

    @Autowired
    private LoginCompraJpaRepository loginCompraJpaRepository;

    public LoginCompraDataTablesResponse listServerSide(LoginCompraDataTablesRequest req) {
        LoginCompraDataTablesResponse response = new LoginCompraDataTablesResponse();
        response.setDraw(req.getDraw());

        List<Cliente> all = new ArrayList<>();

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

                all.addAll(loginCompraRepository.list(anioStr, mesStr, null, null));

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
        List<Cliente> filtered = new ArrayList<>();
        int sireSi = 0, sireNo = 0, sireNa = 0;
        int validacionSunatSi = 0, validacionSunatNo = 0, validacionSunatNa = 0;
        int validacionSi = 0, validacionNo = 0, validacionNa = 0, validacionVal = 0;
        int confirmacionSi = 0, confirmacionNo = 0;

        for (Cliente c : all) {
            if (matchesFilter(c, req)) {
                filtered.add(c);

                // Calcular contadores
                LoginCompra lc = c.getLoginCompra();
                if (lc != null) {
                    // Sire
                    if (lc.getSire() != null) {
                        switch (lc.getSire()) {
                            case 0: sireNo++; break;
                            case 1: sireSi++; break;
                            case 2: sireNa++; break;
                        }
                    } else {
                        sireNo++;
                    }

                    // Validacion Sunat
                    if (lc.getValidacionSunat() != null) {
                        switch (lc.getValidacionSunat()) {
                            case 0: validacionSunatNo++; break;
                            case 1: validacionSunatSi++; break;
                            case 2: validacionSunatNa++; break;
                        }
                    } else {
                        validacionSunatNo++;
                    }

                    // Validacion
                    if (lc.getValidacion() != null) {
                        switch (lc.getValidacion()) {
                            case 0: validacionNo++; break;
                            case 1: validacionSi++; break;
                            case 2: validacionNa++; break;
                            case 3: validacionVal++; break;
                        }
                    } else {
                        validacionNo++;
                    }

                    // Confirmacion
                    if (lc.getConfirmacion() != null) {
                        switch (lc.getConfirmacion()) {
                            case 0: confirmacionNo++; break;
                            case 1: confirmacionSi++; break;
                        }
                    } else {
                        confirmacionNo++;
                    }
                } else {
                    sireNo++;
                    validacionSunatNo++;
                    validacionNo++;
                    confirmacionNo++;
                }
            }
        }

        response.setRecordsTotal(totalCount);
        response.setRecordsFiltered(filtered.size());
        response.setData(filtered);

        response.setSireSi(sireSi);
        response.setSireNo(sireNo);
        response.setSireNa(sireNa);

        response.setValidacionSunatSi(validacionSunatSi);
        response.setValidacionSunatNo(validacionSunatNo);
        response.setValidacionSunatNa(validacionSunatNa);

        response.setValidacionSi(validacionSi);
        response.setValidacionNo(validacionNo);
        response.setValidacionNa(validacionNa);
        response.setValidacionVal(validacionVal);

        response.setConfirmacionSi(confirmacionSi);
        response.setConfirmacionNo(confirmacionNo);

        return response;
    }

    private boolean matchesFilter(Cliente c, LoginCompraDataTablesRequest req) {
        // 1. Search
        if (req.getSearch() != null && !req.getSearch().trim().isEmpty()) {
            String q = req.getSearch().toLowerCase().trim();
            boolean match = (c.getRuc() != null && c.getRuc().toLowerCase().contains(q))
                    || (c.getRazonSocial() != null && c.getRazonSocial().toLowerCase().contains(q))
                    || (c.getNombreCorto() != null && c.getNombreCorto().toLowerCase().contains(q))
                    || (c.getGrupoEconomico() != null && c.getGrupoEconomico().getDescripcion() != null && c.getGrupoEconomico().getDescripcion().toLowerCase().contains(q));
            if (!match) return false;
        }

        // 2. Estados
        if (req.getEstadosString() != null && !req.getEstadosString().trim().isEmpty()) {
            List<String> list = parseCsv(req.getEstadosString());
            String val = c.getEstado() != null ? String.valueOf(c.getEstado().getId()) : "0";
            if (!list.contains(val)) return false;
        }

        // 3. CategoriaStore
        if (req.getCategoriaStoreString() != null && !req.getCategoriaStoreString().trim().isEmpty()) {
            List<String> list = parseCsv(req.getCategoriaStoreString());
            String val = c.getSignerNivel() != null ? String.valueOf(c.getSignerNivel().getCategoriaStore()) : "0";
            if (!list.contains(val)) return false;
        }

        // 4. GrupoEconomico
        if (req.getGrupoEconomicoString() != null && !req.getGrupoEconomicoString().trim().isEmpty()) {
            List<String> list = parseCsv(req.getGrupoEconomicoString());
            String val = (c.getGrupoEconomico() != null && c.getGrupoEconomico().getId() != 0) ? String.valueOf(c.getGrupoEconomico().getId()) : "0";
            if (!list.contains(val)) return false;
        }

        // 5. Grupos
        if (req.getGruposString() != null && !req.getGruposString().trim().isEmpty()) {
            List<String> list = parseCsv(req.getGruposString());
            String val = c.getY() != null ? c.getY() : "";
            if (!list.contains(val)) return false;
        }

        // 6. Ventas Confirmacion (v-con)
        if (req.getVentasConfirmacionString() != null && !req.getVentasConfirmacionString().trim().isEmpty()) {
            List<String> list = parseCsv(req.getVentasConfirmacionString());
            String val = (c.getLoginVenta() != null && c.getLoginVenta().getConfirmacion() != null) ? String.valueOf(c.getLoginVenta().getConfirmacion()) : "0";
            if (!list.contains(val)) return false;
        }

        // 7. Sire
        if (req.getSireString() != null && !req.getSireString().trim().isEmpty()) {
            List<String> list = parseCsv(req.getSireString());
            String val = (c.getLoginCompra() != null && c.getLoginCompra().getSire() != null) ? String.valueOf(c.getLoginCompra().getSire()) : "0";
            if (!list.contains(val)) return false;
        }

        // 8. Validacion Sunat
        if (req.getValidacionSunatString() != null && !req.getValidacionSunatString().trim().isEmpty()) {
            List<String> list = parseCsv(req.getValidacionSunatString());
            String val = (c.getLoginCompra() != null && c.getLoginCompra().getValidacionSunat() != null) ? String.valueOf(c.getLoginCompra().getValidacionSunat()) : "0";
            if (!list.contains(val)) return false;
        }

        // 9. Validacion
        if (req.getValidacionString() != null && !req.getValidacionString().trim().isEmpty()) {
            List<String> list = parseCsv(req.getValidacionString());
            String val = (c.getLoginCompra() != null && c.getLoginCompra().getValidacion() != null) ? String.valueOf(c.getLoginCompra().getValidacion()) : "0";
            if (!list.contains(val)) return false;
        }

        // 10. Confirmacion
        if (req.getConfirmacionString() != null && !req.getConfirmacionString().trim().isEmpty()) {
            List<String> list = parseCsv(req.getConfirmacionString());
            String val = (c.getLoginCompra() != null && c.getLoginCompra().getConfirmacion() != null) ? String.valueOf(c.getLoginCompra().getConfirmacion()) : "0";
            if (!list.contains(val)) return false;
        }

        // 11. fVencimiento range
        if (c.getLoginCompra() != null && c.getLoginCompra().getfVencimiento() != null) {
            String fVenc = c.getLoginCompra().getfVencimiento();
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

    public List<Cliente> list(String anio, String mes, String estados, String grupos) {
        return loginCompraRepository.list(anio, mes, estados, grupos);
    }

    public Cliente getOne(String ruc, String anio, String mes) {
        Cliente clie = loginCompraRepository.getOne(ruc, anio, mes);
        
        if (clie == null) {
            clie = new Cliente();
            clie.setRuc(ruc);
        }

        if (clie.getLoginCompra() == null) {
            LoginCompra emptyLc = new LoginCompra();
            emptyLc.setVersion(0);
            clie.setLoginCompra(emptyLc);
        } else if (clie.getLoginCompra().getVersion() == null) {
            clie.getLoginCompra().setVersion(0);
        }
        
        return clie;
    }

    @Transactional
    public int insertUpdate(Cliente clie) {
        try {
            LoginCompra lcInput = clie.getLoginCompra();
            LoginCompraId id = new LoginCompraId(clie.getRuc(), lcInput.getAnio(), lcInput.getMes());
            lcInput.setId(id);

            boolean exists = loginCompraJpaRepository.existsById(id);

            if (exists) {
                loginCompraJpaRepository.save(lcInput);
                return 2; // update
            } else {
                lcInput.setVersion(1);
                loginCompraJpaRepository.save(lcInput);
                return 1; // insert
            }
        } catch (OptimisticLockException e) {
            log.warn("Optimistic lock exception on LoginCompra: {}", clie.getRuc());
            return 10;
        } catch (Exception e) {
            log.error("Error inserting/updating LoginCompra", e);
            throw e;
        }
    }
}
