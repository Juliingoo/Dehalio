package model;

import jakarta.persistence.*;

@Entity
@Table(name = "productoFavorito")
public class productoFavorito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idProductoFavorito")
    private int idProductoFavorito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario", nullable = false)
    private usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto", nullable = false)
    private producto producto;

    // Getters y setters

    public int getIdProductoFavorito() {
        return idProductoFavorito;
    }

    public void setIdProductoFavorito(int idProductoFavorito) {
        this.idProductoFavorito = idProductoFavorito;
    }

    public usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(usuario usuario) {
        this.usuario = usuario;
    }

    public producto getProducto() {
        return producto;
    }

    public void setProducto(producto producto) {
        this.producto = producto;
    }
}
