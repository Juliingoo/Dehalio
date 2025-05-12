package model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "categoriaTipoProducto")
public class categoriaTipoProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idCategoriaTipoProducto")
    private int idCategoriaTipoProducto;

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    // Relación uno-a-muchos con tipoProducto
    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<tipoProducto> tiposProducto;

    // Getters y setters
    public int getIdCategoriaTipoProducto() {
        return idCategoriaTipoProducto;
    }

    public void setIdCategoriaTipoProducto(int idCategoriaTipoProducto) {
        this.idCategoriaTipoProducto = idCategoriaTipoProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


}
