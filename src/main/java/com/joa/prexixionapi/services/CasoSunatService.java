package com.joa.prexixionapi.services;

import com.joa.prexixionapi.dto.*;
import com.joa.prexixionapi.repositories.CasoSunatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CasoSunatService {

    private final CasoSunatRepository repository;

    public List<CasoSunatListDTO> list(CasoSunatRequest request) {
        return repository.listForDataTable(request);
    }

    public CasoSunatDTO getOne(Integer id) {
        return repository.getOne(id);
    }

    public boolean exist(Integer id) {
        return repository.exist(id);
    }

    private Integer parseRealId(Object idObj) {
        if (idObj == null) return null;
        try {
            if (idObj instanceof Number) {
                long val = ((Number) idObj).longValue();
                if (val > 0 && val <= Integer.MAX_VALUE) {
                    return (int) val;
                }
            } else {
                long val = Long.parseLong(idObj.toString().trim());
                if (val > 0 && val <= Integer.MAX_VALUE) {
                    return (int) val;
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    @Transactional
    public int insertUpdate(CasoSunatDTO dto) {
        boolean exists = repository.exist(dto.getId());
        int idCaso;
        int resultAction;

        List<Integer> existingAuditorIds = new ArrayList<>();
        List<Integer> existingDocumentoIds = new ArrayList<>();
        List<Integer> existingEventoIds = new ArrayList<>();
        List<Integer> existingRelacionIds = new ArrayList<>();

        if (exists) {
            idCaso = dto.getId();
            repository.updateCaso(dto);
            resultAction = 2; // Actualizado

            existingAuditorIds = repository.getAuditorIdsByCaso(idCaso);
            existingDocumentoIds = repository.getDocumentoIdsByCaso(idCaso);
            existingEventoIds = repository.getEventoIdsByCaso(idCaso);
            existingRelacionIds = repository.getRelacionIdsByCaso(idCaso);
        } else {
            idCaso = repository.insertCaso(dto);
            dto.setId(idCaso);
            resultAction = 1; // Registrado
        }

        // 1. Guardar o Actualizar Auditores
        Set<Integer> keptAuditorIds = new HashSet<>();
        if (dto.getAuditores() != null) {
            for (CasoSunatAuditorDTO auditor : dto.getAuditores()) {
                auditor.setIdCaso(idCaso);
                Integer realId = parseRealId(auditor.getId());
                if (realId != null && existingAuditorIds.contains(realId)) {
                    repository.updateAuditor(auditor, realId);
                    keptAuditorIds.add(realId);
                } else {
                    repository.insertAuditor(auditor);
                }
            }
        }
        existingAuditorIds.removeAll(keptAuditorIds);
        repository.deleteAuditoresByIds(existingAuditorIds);

        // 2. Guardar o Actualizar Documentos
        Map<Object, Integer> docIdMap = new HashMap<>();
        Set<Integer> keptDocumentoIds = new HashSet<>();

        if (dto.getDocumentos() != null) {
            for (CasoSunatDocumentoDTO doc : dto.getDocumentos()) {
                Object oldDocId = doc.getId();
                doc.setIdCaso(idCaso);
                Integer realId = parseRealId(oldDocId);
                int realDocId;

                if (realId != null && existingDocumentoIds.contains(realId)) {
                    repository.updateDocumento(doc, realId);
                    realDocId = realId;
                    keptDocumentoIds.add(realId);
                } else {
                    realDocId = repository.insertDocumento(doc);
                }

                doc.setId(realDocId);

                if (oldDocId != null) {
                    docIdMap.put(oldDocId, realDocId);
                    docIdMap.put(oldDocId.toString(), realDocId);
                }
                docIdMap.put(realDocId, realDocId);
                docIdMap.put(String.valueOf(realDocId), realDocId);
            }

            // Guardar o Actualizar Eventos por Documento
            Set<Integer> keptEventoIds = new HashSet<>();
            for (CasoSunatDocumentoDTO doc : dto.getDocumentos()) {
                if (doc.getEventos() != null) {
                    for (CasoSunatDocumentoEventoDTO evt : doc.getEventos()) {
                        evt.setIdCaso(idCaso);
                        evt.setIdDocumento((Integer) doc.getId());

                        if (evt.getIdDocumentoCarta() != null) {
                            Integer realCartaId = docIdMap.get(evt.getIdDocumentoCarta());
                            if (realCartaId == null) {
                                realCartaId = docIdMap.get(evt.getIdDocumentoCarta().toString());
                            }
                            if (realCartaId != null) {
                                evt.setIdDocumentoCarta(realCartaId);
                            }
                        }

                        Integer realEvtId = parseRealId(evt.getId());
                        if (realEvtId != null && existingEventoIds.contains(realEvtId)) {
                            repository.updateEvento(evt, realEvtId);
                            keptEventoIds.add(realEvtId);
                        } else {
                            repository.insertEvento(evt);
                        }
                    }
                }
            }
            existingEventoIds.removeAll(keptEventoIds);
            repository.deleteEventosByIds(existingEventoIds);
        } else {
            repository.deleteEventosByIds(existingEventoIds);
        }

        // 3. Guardar o Actualizar Relaciones
        Set<Integer> keptRelacionIds = new HashSet<>();
        if (dto.getRelaciones() != null) {
            for (CasoSunatDocumentoRelacionDTO rel : dto.getRelaciones()) {
                rel.setIdCaso(idCaso);
                Integer realOrigenId = docIdMap.get(rel.getIdDocumentoOrigen());
                if (realOrigenId == null && rel.getIdDocumentoOrigen() != null) {
                    realOrigenId = docIdMap.get(rel.getIdDocumentoOrigen().toString());
                }

                Integer realDestinoId = docIdMap.get(rel.getIdDocumentoDestino());
                if (realDestinoId == null && rel.getIdDocumentoDestino() != null) {
                    realDestinoId = docIdMap.get(rel.getIdDocumentoDestino().toString());
                }

                if (realOrigenId != null) rel.setIdDocumentoOrigen(realOrigenId);
                if (realDestinoId != null) rel.setIdDocumentoDestino(realDestinoId);

                Integer realRelId = parseRealId(rel.getId());
                if (realRelId != null && existingRelacionIds.contains(realRelId)) {
                    repository.updateRelacion(rel, realRelId);
                    keptRelacionIds.add(realRelId);
                } else {
                    repository.insertRelacion(rel);
                }
            }
        }
        existingRelacionIds.removeAll(keptRelacionIds);
        repository.deleteRelacionesByIds(existingRelacionIds);

        // 4. Eliminar Documentos retirados de la lista
        existingDocumentoIds.removeAll(keptDocumentoIds);
        repository.deleteDocumentosByIds(existingDocumentoIds);

        return resultAction;
    }

    @Transactional
    public int delete(Integer idCaso) {
        try {
            return repository.delete(idCaso);
        } catch (Exception e) {
            log.error("Error al eliminar Caso SUNAT ID: " + idCaso, e);
            return 0;
        }
    }
}
