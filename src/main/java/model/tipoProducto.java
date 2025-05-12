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

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idCategoriaTipoProducto", nullable = false)
    private categoriaTipoProducto categoriaTipoProducto;

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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public categoriaTipoProducto getCategoriaTipoProducto() {
        return categoriaTipoProducto;
    }

    public void setCategoriaTipoProducto(categoriaTipoProducto categoriaTipoProducto) {
        this.categoriaTipoProducto = categoriaTipoProducto;
    }
}
