package com.joa.prexixionapi.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.joa.prexixionapi.dto.ApiResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JobLauncherService {

    private final RestTemplate restTemplate;

    @Value("${jobs.api.url}")
    private String jobsApiUrl;

    public ApiResponse<?> lanzarSincronizacionSunat() {
        String url = jobsApiUrl + "/api/sunat/sincronizar";
        try {
            return restTemplate.postForObject(url, null, ApiResponse.class);
        } catch (Exception e) {
            return new ApiResponse<>(false, "No se pudo conectar con el servicio de jobs: " + e.getMessage(), null);
        }
    }
}
