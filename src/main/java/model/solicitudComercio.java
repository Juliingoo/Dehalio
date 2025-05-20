package model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "comercio")
public class solicitudComercio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idSolicitudComercio")
    private int idSolicitudComercio;

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @Column(name = "NIF", nullable = false, unique = true, length = 9)
    private String nif;

    @Lob
    @Column(name = "imagen")
    private byte[] imagen;

    @Column(name = "direccion", nullable = false, length = 100)
    private String direccion;

    @Column(name = "codigoPostal", nullable = false, length = 5)
    private String codigoPostal;

    @Column(name = "municipio", nullable = false, length = 50)
    private String municipio;

    @Column(name = "provincia", nullable = false, length = 50)
    private String provincia;

    @Column(name = "coordenadas", nullable = false, length = 50)
    private String coordenadas;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitante", nullable = false)
    private usuario solicitante;

    // Getters y Setters

    public int getIdSolicitudComercio() {
        return idSolicitudComercio;
    }

    public void setIdSolicitudComercio(int idSolicitudComercio) {
        this.idSolicitudComercio = idSolicitudComercio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getCoordenadas() {
        return coordenadas;
    }

    public void setCoordenadas(String coordenadas) {
        this.coordenadas = coordenadas;
    }


    public usuario getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(usuario solicitante) {
        this.solicitante = solicitante;
    }
}
