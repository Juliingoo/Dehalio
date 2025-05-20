package model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "valoracion")
public class valoracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idValoracion")
    private int idValoracion;

    @Column(name = "numeroValoracion", nullable = false)
    private int numeroValoracion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario", nullable = false)
    private usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comercio", nullable = false)
    private comercio comercio;

    // Getters y setters

    public int getIdValoracion() {
        return idValoracion;
    }

    public void setIdValoracion(int idValoracion) {
        this.idValoracion = idValoracion;
    }

    public usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(usuario usuario) {
        this.usuario = usuario;
    }

    public comercio getComercio() {
        return comercio;
    }

    public void setComercio(comercio comercio) {
        this.comercio = comercio;
    }


    public int getNumeroValoracion() {
        return numeroValoracion;
    }

    public void setNumeroValoracion(int numeroValoracion) {
        this.numeroValoracion = numeroValoracion;
    }
}
