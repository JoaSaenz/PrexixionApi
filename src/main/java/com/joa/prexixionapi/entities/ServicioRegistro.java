package com.joa.prexixionapi.entities;

public class ServicioRegistro {
    private Integer id;
    private int nroRegistro;
    private Gclass tipo;
    private String fecha;
    private String nroRectificacion;
    private String nroOrden;

    public ServicioRegistro() {
    }

    public ServicioRegistro(Integer id, int nroRegistro, Gclass tipo, String fecha, String nroRectificacion, String nroOrden) {
        this.id = id;
        this.nroRegistro = nroRegistro;
        this.tipo = tipo;
        this.fecha = fecha;
        this.nroRectificacion = nroRectificacion;
        this.nroOrden = nroOrden;
    }

    public ServicioRegistro(int id, Gclass tipo, String fecha, String nroRectificacion, String nroOrden) {
        this.id = id;
        this.nroRegistro = id;
        this.tipo = tipo;
        this.fecha = fecha;
        this.nroRectificacion = nroRectificacion;
        this.nroOrden = nroOrden;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        if (id != null && this.nroRegistro == 0) {
            this.nroRegistro = id;
        }
    }

    public int getNroRegistro() {
        return nroRegistro;
    }

    public void setNroRegistro(int nroRegistro) {
        this.nroRegistro = nroRegistro;
    }

    public Gclass getTipo() {
        return tipo;
    }

    public void setTipo(Gclass tipo) {
        this.tipo = tipo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getNroRectificacion() {
        return nroRectificacion;
    }

    public void setNroRectificacion(String nroRectificacion) {
        this.nroRectificacion = nroRectificacion;
    }

    public String getNroOrden() {
        return nroOrden;
    }

    public void setNroOrden(String nroOrden) {
        this.nroOrden = nroOrden;
    }
}
