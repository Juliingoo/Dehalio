package model;

import jakarta.persistence.*;

@Entity
@Table(name = "tipoPrecio")
public class tipoPrecio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idTipoPrecio")
    private int idTipoPrecio;

    @Column(name = "tipo", nullable = false, length = 20)
    private String tipo;


    // Getters y setters

    public int getIdTipoPrecio() {
        return idTipoPrecio;
    }

    public void setIdTipoPrecio(int idTipoPrecio) {
        this.idTipoPrecio = idTipoPrecio;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
