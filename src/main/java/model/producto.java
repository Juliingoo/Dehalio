package model;

import jakarta.persistence.*;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "producto")
public class producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idProducto")
    private int idProducto;

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @Lob
    @Column(name = "imagen", columnDefinition="BLOB")
    private byte[] imagen;

    @Column(name = "precio")
    private double precio;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "idComercio", nullable = false)
    private comercio comercio;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "idTipoProducto")
    private tipoProducto tipo;

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }


    public comercio getComercio() {
        return comercio;
    }

    public void setComercio(comercio comercio) {
        this.comercio = comercio;
    }

    public tipoProducto getTipo() {
        return tipo;
    }

    public void setTipo(tipoProducto tipo) {
        this.tipo = tipo;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }
}
