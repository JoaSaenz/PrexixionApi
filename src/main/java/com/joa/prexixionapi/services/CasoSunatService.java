package com.joa.prexixionapi.services;

import com.joa.prexixionapi.dto.*;
import com.joa.prexixionapi.repositories.CasoSunatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Transactional
    public int insertUpdate(CasoSunatDTO dto) {
        boolean exists = repository.exist(dto.getId());
        int idCaso;
        int resultAction;

        if (exists) {
            idCaso = dto.getId();
            repository.updateCaso(dto);
            repository.deleteHijosByCaso(idCaso);
            resultAction = 2; // Actualizado
        } else {
            idCaso = repository.insertCaso(dto);
            dto.setId(idCaso);
            resultAction = 1; // Registrado
        }

        // 1. Guardar Auditores
        if (dto.getAuditores() != null) {
            for (CasoSunatAuditorDTO auditor : dto.getAuditores()) {
                auditor.setIdCaso(idCaso);
                repository.insertAuditor(auditor);
            }
        }

        // 2. Guardar Documentos y mapear IDs temporales a IDs reales generados por la BD
        Map<Object, Integer> docIdMap = new HashMap<>();

        if (dto.getDocumentos() != null) {
            for (CasoSunatDocumentoDTO doc : dto.getDocumentos()) {
                Object oldDocId = doc.getId();
                doc.setIdCaso(idCaso);
                int realDocId = repository.insertDocumento(doc);
                doc.setId(realDocId);

                if (oldDocId != null) {
                    docIdMap.put(oldDocId, realDocId);
                    docIdMap.put(oldDocId.toString(), realDocId);
                }
                docIdMap.put(realDocId, realDocId);
                docIdMap.put(String.valueOf(realDocId), realDocId);
            }

            // Guardar Eventos por Documento resguardando la relación con idDocumentoCarta
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
                        repository.insertEvento(evt);
                    }
                }
            }
        }

        // 3. Guardar Relaciones mapeando los IDs de origen y destino reales
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

                repository.insertRelacion(rel);
            }
        }

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
