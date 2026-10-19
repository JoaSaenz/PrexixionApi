package com.joa.prexixionapi.modules.notificacion.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class NotificacionDTO {

    private Long id;

    private String ruc;

    private String idSunat;

    private String titulo;
    private LocalDateTime fecha;
    private String tipo;
    private String nombreCorto;

    private String estadoCliente;
    private String y;
    private String razonSocial;
    private Integer idEstado;

    private Long jobStatusId;

    private Boolean revisado = false;

    private List<NotificacionAdjuntoDTO> adjuntos = new ArrayList<>();

    public NotificacionDTO() {
    }

    public String getEstadoCliente() {
        return estadoCliente;
    }

    public void setEstadoCliente(String estadoCliente) {
        this.estadoCliente = estadoCliente;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public Integer getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(Integer idEstado) {
        this.idEstado = idEstado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getIdSunat() {
        return idSunat;
    }

    public void setIdSunat(String idSunat) {
        this.idSunat = idSunat;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Long getJobStatusId() {
        return jobStatusId;
    }

    public void setJobStatusId(Long jobStatusId) {
        this.jobStatusId = jobStatusId;
    }

    public List<NotificacionAdjuntoDTO> getAdjuntos() {
        return adjuntos;
    }

    public void setAdjuntos(List<NotificacionAdjuntoDTO> adjuntos) {
        this.adjuntos = adjuntos;
    }

    public boolean isRevisado() {
        return revisado != null && revisado;
    }

    public void setRevisado(boolean revisado) {
        this.revisado = revisado;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNombreCorto() {
        return nombreCorto;
    }

    public void setNombreCorto(String nombreCorto) {
        this.nombreCorto = nombreCorto;
    }

}
