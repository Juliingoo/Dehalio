package model;

import jakarta.persistence.*;

@Entity
@Table(name = "tipoProducto")
public class tipoProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idTipoProducto")
    private int idTipoProducto;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "imagenBase")
    private byte[] imagenBase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idCategoriaTipoProducto", nullable = false)
    private categoriaTipoProducto categoria;

    // Getters y setters

    public int getIdTipoProducto() {
        return idTipoProducto;
    }

    public void setIdTipoProducto(int idTipoProducto) {
        this.idTipoProducto = idTipoProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public categoriaTipoProducto getCategoria() {
        return categoria;
    }

    public void setCategoria(categoriaTipoProducto categoria) {
        this.categoria = categoria;
    }

    public byte[] getImagenBase() {
        return imagenBase;
    }

    public void setImagenBase(byte[] imagenBase) {
        this.imagenBase = imagenBase;
    }
}
