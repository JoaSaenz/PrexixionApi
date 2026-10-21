package com.joa.prexixionapi.modules.beneficiarioFinal.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.joa.prexixionapi.modules.beneficiarioFinal.repositories.BeneficiarioFinalReporteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import com.joa.prexixionapi.modules.beneficiarioFinal.dto.BeneficiarioFinalDTO;
import com.joa.prexixionapi.modules.beneficiarioFinal.dto.BeneficiarioFinalReporteDTO;
import com.joa.prexixionapi.modules.beneficiarioFinal.dto.BeneficiarioFinalDataDTO;
import com.joa.prexixionapi.modules.beneficiarioFinal.repositories.BeneficiarioFinalDataRepository;
import com.joa.prexixionapi.modules.beneficiarioFinal.repositories.BeneficiarioFinalRegistroRepository;
import com.joa.prexixionapi.modules.beneficiarioFinal.services.BeneficiarioFinalService;
import com.joa.prexixionapi.modules.beneficiarioFinal.dto.BeneficiarioFinalRegistroDTO;
import com.joa.prexixionapi.modules.beneficiarioFinal.repositories.BeneficiarioFinalRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class BeneficiarioFinalService {

    private final BeneficiarioFinalReporteRepository reporteRepository;
    private final BeneficiarioFinalDataRepository dataRepository;
    private final BeneficiarioFinalRepository beneficiarioRepository;
    private final BeneficiarioFinalRegistroRepository registroRepository;

    public List<BeneficiarioFinalReporteDTO> list(String periods, String estados, String grupos) {
        List<BeneficiarioFinalReporteDTO> allResults = new ArrayList<>();

        for (String period : periods.split(",")) {
            String anio = period.substring(0, 4);
            String mes = period.substring(4, 6);

            log.debug("Executing query for period: {}", period);
            List<BeneficiarioFinalReporteDTO> results = reporteRepository.list(anio, mes, estados, grupos);

            log.info("Period {} - Rows found: {}", period, results.size());
            allResults.addAll(results);
        }
        return allResults;
    }

    public BeneficiarioFinalReporteDTO getOne(String ruc, String anio, String mes) {
        log.debug("Executing getOne query for RUC: {} and Period: {}{}", ruc, anio, mes);
        List<BeneficiarioFinalReporteDTO> list = reporteRepository.getOneList(ruc, anio, mes);

        if (list.isEmpty()) {
            log.warn("No data found for RUC: {} in period {}{}", ruc, anio, mes);
            return BeneficiarioFinalReporteDTO.builder().idCliente(ruc).anio(anio).mes(mes).build();
        }

        BeneficiarioFinalReporteDTO dto = list.get(0);

        // Fetch nested lists
        List<BeneficiarioFinalDTO> beneficiariosList = beneficiarioRepository.findByIdClienteAndAnioAndMes(ruc, anio,
                mes);
        List<BeneficiarioFinalRegistroDTO> registrosList = registroRepository
                .findByIdClienteAndAnioAndMesOrderByNroRegistroAsc(ruc, anio, mes);

        dto.setBeneficiarios(beneficiariosList);
        dto.setRegistros(registrosList);

        return dto;
    }

    public BeneficiarioFinalDTO getBeneficiarioById(int id) {
        return beneficiarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Beneficiario no encontrado con ID: " + id));
    }

    @Transactional
    public void save(BeneficiarioFinalReporteDTO dto) {
        log.info("Saving BF3800 for Client: {} - Period: {}-{}", dto.getIdCliente(), dto.getAnio(), dto.getMes());
        log.info("Number of registries to process: {}", dto.getRegistros() != null ? dto.getRegistros().size() : 0);

        // Save Master Data
        BeneficiarioFinalDataDTO data = BeneficiarioFinalDataDTO.builder()
                .idCliente(dto.getIdCliente())
                .anio(dto.getAnio())
                .mes(dto.getMes())
                .observacion(dto.getObservacion())
                .mail(dto.getMail())
                .build();
        dataRepository.upsert(data);

        // Save Beneficiarios (Cascades to nested lists)
        beneficiarioRepository.deleteAllByPeriod(dto.getIdCliente(), dto.getAnio(), dto.getMes());

        if (dto.getBeneficiarios() != null) {
            for (BeneficiarioFinalDTO b : dto.getBeneficiarios()) {
                b.setIdCliente(dto.getIdCliente());
                b.setAnio(dto.getAnio());
                b.setMes(dto.getMes());
                beneficiarioRepository.insert(b);
            }
        }

        // Save Registros (all of them)
        if (dto.getRegistros() != null && !dto.getRegistros().isEmpty()) {
            for (BeneficiarioFinalRegistroDTO regDto : dto.getRegistros()) {
                log.info("Processing Registry - Technical ID: {}, Logical Nro: {}", regDto.getId(),
                        regDto.getNroRegistro());
                registroRepository.save(regDto, dto.getIdCliente(), dto.getAnio(), dto.getMes());
            }
        }
    }

}
