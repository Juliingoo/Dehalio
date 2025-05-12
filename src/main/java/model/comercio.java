package model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "comercio")
public class comercio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idComercio")
    private int idComercio;

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
    @JoinColumn(name = "propietario", nullable = false)
    private usuario propietario;

    // Relación opcional con productos, valoraciones, etc., si se desea
    @OneToMany(mappedBy = "comercio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<producto> productos;

    @OneToMany(mappedBy = "comercio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<valoracion> valoraciones;

    // Getters y Setters

    public int getIdComercio() {
        return idComercio;
    }

    public void setIdComercio(int idComercio) {
        this.idComercio = idComercio;
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

    public usuario getPropietario() {
        return propietario;
    }

    public void setPropietario(usuario propietario) {
        this.propietario = propietario;
    }

    public List<producto> getProductos() {
        return productos;
    }

    public void setProductos(List<producto> productos) {
        this.productos = productos;
    }

    public List<valoracion> getValoraciones() {
        return valoraciones;
    }

    public void setValoraciones(List<valoracion> valoraciones) {
        this.valoraciones = valoraciones;
    }
}
