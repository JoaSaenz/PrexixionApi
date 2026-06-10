package com.joa.prexixionapi.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joa.prexixionapi.entities.Cliente;
import com.joa.prexixionapi.entities.LoginVenta;
import com.joa.prexixionapi.entities.LoginVentaId;
import com.joa.prexixionapi.repositories.LoginVentaJpaRepository;
import com.joa.prexixionapi.repositories.LoginVentaRepository;
import com.joa.prexixionapi.dto.LoginVentaDataTablesRequest;
import com.joa.prexixionapi.dto.LoginVentaDataTablesResponse;

import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LoginVentaService {

    @Autowired
    private LoginVentaRepository loginVentaRepository;

    @Autowired
    private LoginVentaJpaRepository loginVentaJpaRepository;

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
        
        return new String[]{globalMin != null ? globalMin : "", globalMax != null ? globalMax : ""};
    }

    public LoginVentaDataTablesResponse listServerSide(LoginVentaDataTablesRequest req) {
        LoginVentaDataTablesResponse response = new LoginVentaDataTablesResponse();

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

                all.addAll(loginVentaRepository.list(anioStr, mesStr, null, null));

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
        int registroSi = 0, registroNo = 0, registroNa = 0;
        int revisionSunatSi = 0, revisionSunatNo = 0, revisionSunatNa = 0;
        int validacionSi = 0, validacionNo = 0, validacionNa = 0, validacionVal = 0;
        int confirmacionSi = 0, confirmacionNo = 0;

        for (Cliente c : all) {
            if (matchesFilter(c, req)) {
                filtered.add(c);

                // Calcular conteos de resumen
                LoginVenta lv = c.getLoginVenta();
                if (lv != null) {
                    // Registro
                    if (lv.getRegistro() != null) {
                        switch (lv.getRegistro()) {
                            case 0: registroNo++; break;
                            case 1: registroSi++; break;
                            case 2: registroNa++; break;
                        }
                    } else {
                        registroNo++;
                    }

                    // Revision Sunat
                    if (lv.getRevisionSunat() != null) {
                        switch (lv.getRevisionSunat()) {
                            case 0: revisionSunatNo++; break;
                            case 1: revisionSunatSi++; break;
                            case 2: revisionSunatNa++; break;
                        }
                    } else {
                        revisionSunatNo++;
                    }

                    // Validacion
                    if (lv.getValidacion() != null) {
                        switch (lv.getValidacion()) {
                            case 0: validacionNo++; break;
                            case 1: validacionSi++; break;
                            case 2: validacionNa++; break;
                            case 3: validacionVal++; break;
                        }
                    } else {
                        validacionNo++;
                    }

                    // Confirmacion
                    if (lv.getConfirmacion() != null) {
                        switch (lv.getConfirmacion()) {
                            case 0: confirmacionNo++; break;
                            case 1: confirmacionSi++; break;
                        }
                    } else {
                        confirmacionNo++;
                    }
                } else {
                    registroNo++;
                    revisionSunatNo++;
                    validacionNo++;
                    confirmacionNo++;
                }
            }
        }

        response.setRecordsTotal(totalCount);
        response.setRecordsFiltered(filtered.size());
        response.setData(filtered);

        response.setRegistroSi(registroSi);
        response.setRegistroNo(registroNo);
        response.setRegistroNa(registroNa);

        response.setRevisionSunatSi(revisionSunatSi);
        response.setRevisionSunatNo(revisionSunatNo);
        response.setRevisionSunatNa(revisionSunatNa);

        response.setValidacionSi(validacionSi);
        response.setValidacionNo(validacionNo);
        response.setValidacionNa(validacionNa);
        response.setValidacionVal(validacionVal);

        response.setConfirmacionSi(confirmacionSi);
        response.setConfirmacionNo(confirmacionNo);

        return response;
    }

    private boolean matchesFilter(Cliente c, LoginVentaDataTablesRequest req) {
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

        // 6. Responsable
        if (req.getResponsableString() != null && !req.getResponsableString().trim().isEmpty()) {
            List<String> list = parseCsv(req.getResponsableString());
            String val = (c.getLoginVenta() != null && c.getLoginVenta().getResponsable() != null && !c.getLoginVenta().getResponsable().trim().isEmpty()) ? c.getLoginVenta().getResponsable() : "0";
            if (!list.contains(val)) return false;
        }

        // 7. Registro
        if (req.getRegistroString() != null && !req.getRegistroString().trim().isEmpty()) {
            List<String> list = parseCsv(req.getRegistroString());
            String val = (c.getLoginVenta() != null && c.getLoginVenta().getRegistro() != null) ? String.valueOf(c.getLoginVenta().getRegistro()) : "0";
            if (!list.contains(val)) return false;
        }

        // 8. RevisionSunat
        if (req.getRevisionSunatString() != null && !req.getRevisionSunatString().trim().isEmpty()) {
            List<String> list = parseCsv(req.getRevisionSunatString());
            String val = (c.getLoginVenta() != null && c.getLoginVenta().getRevisionSunat() != null) ? String.valueOf(c.getLoginVenta().getRevisionSunat()) : "0";
            if (!list.contains(val)) return false;
        }

        // 9. Validacion
        if (req.getValidacionString() != null && !req.getValidacionString().trim().isEmpty()) {
            List<String> list = parseCsv(req.getValidacionString());
            String val = (c.getLoginVenta() != null && c.getLoginVenta().getValidacion() != null) ? String.valueOf(c.getLoginVenta().getValidacion()) : "0";
            if (!list.contains(val)) return false;
        }

        // 10. Confirmacion
        if (req.getConfirmacionString() != null && !req.getConfirmacionString().trim().isEmpty()) {
            List<String> list = parseCsv(req.getConfirmacionString());
            String val = (c.getLoginVenta() != null && c.getLoginVenta().getConfirmacion() != null) ? String.valueOf(c.getLoginVenta().getConfirmacion()) : "0";
            if (!list.contains(val)) return false;
        }

        // 11. fVencimiento range
        if (c.getLoginVenta() != null && c.getLoginVenta().getfVencimiento() != null) {
            String fVenc = c.getLoginVenta().getfVencimiento();
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
        return loginVentaRepository.list(anio, mes, estados, grupos);
    }

    public Cliente getOne(String ruc, String anio, String mes) {
        Cliente clie = loginVentaRepository.getOne(ruc, anio, mes);
        
        if (clie == null) {
            clie = new Cliente();
            clie.setRuc(ruc);
        }

        if (clie.getLoginVenta() == null) {
            LoginVenta emptyLv = new LoginVenta();
            emptyLv.setVersion(0);
            clie.setLoginVenta(emptyLv);
        } else if (clie.getLoginVenta().getVersion() == 0) {
            clie.getLoginVenta().setVersion(0);
        }
        
        return clie;
    }

    @Transactional
    public int insertUpdate(Cliente clie) {
        try {
            LoginVenta lvInput = clie.getLoginVenta();
            LoginVentaId id = new LoginVentaId(clie.getRuc(), lvInput.getAnio(), lvInput.getMes());
            lvInput.setId(id);

            // Legacy returns 1 for insert, 2 for update, 10 for optimistic lock fail
            boolean exists = loginVentaJpaRepository.existsById(id);

            if (exists) {
                // JPA will handle the versioning update
                loginVentaJpaRepository.save(lvInput);
                return 2; // update
            } else {
                // Force version to 1 for new records as legacy did
                lvInput.setVersion(1);
                loginVentaJpaRepository.save(lvInput);
                return 1; // insert
            }
        } catch (OptimisticLockException e) {
            log.warn("Optimistic lock exception on LoginVenta: {}", clie.getRuc());
            return 10;
        } catch (Exception e) {
            log.error("Error inserting/updating LoginVenta", e);
            throw e;
        }
    }
}
