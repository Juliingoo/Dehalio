package controllers;

import database.Sesion;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.producto;
import model.productoFavorito;
import org.hibernate.Session;

import java.io.ByteArrayInputStream;

import static database.Sesion.newSession;

public class TarjetaProductoController {

    @FXML
    private ImageView imageView;

    @FXML
    private Label labelNombre;

    @FXML
    private Label labelPrecio;

    @FXML
    private Label labelComercio;

    @FXML
    private Button btnFavorito;

    @FXML
    private ImageView iconoCorazon;

    Session session = newSession();

    /**
     * Configura la tarjeta mostrando la imagen, nombre, precio y comercio del producto recibido.
     * Permite añadir o quitar el producto de favoritos y actualiza el icono correspondiente.
     *
     * @param producto producto cuyos datos se mostrarán en la tarjeta
     */
    public void setProducto(producto producto) {
        if (producto.getImagen() != null && producto.getImagen().length > 0) {
            imageView.setImage(new Image(new ByteArrayInputStream(producto.getImagen())));
        } else {
            imageView.setImage(new Image(getClass().getResourceAsStream("/iconos/productoSinImagen.png")));
        }

        labelNombre.setText(producto.getNombre());
        labelPrecio.setText(String.format("%.2f €", producto.getPrecio()));
        labelComercio.setText("Comercio: " + (producto.getComercio() != null ? producto.getComercio().getNombre() : "Sin comercio"));

        // Método auxiliar para comprobar si es favorito y actualizar icono
        actualizarIconoFavorito(producto);

        // Acción del botón
        btnFavorito.setOnAction(e -> {
            Session sessionAccion = newSession();
            sessionAccion.beginTransaction();

            // Comprobar si ya está en favoritos
            productoFavorito existente = sessionAccion.createQuery(
                            "FROM productoFavorito WHERE producto = :producto AND usuario = :usuario", productoFavorito.class)
                    .setParameter("producto", producto)
                    .setParameter("usuario", Sesion.usuario)
                    .uniqueResult();

            if (existente != null) {
                // Eliminar de favoritos
                sessionAccion.delete(existente);
            } else {
                // Añadir a favoritos
                productoFavorito nuevo = new productoFavorito();
                nuevo.setProducto(producto);
                nuevo.setUsuario(Sesion.usuario);
                sessionAccion.save(nuevo);
            }

            sessionAccion.getTransaction().commit();
            sessionAccion.close();

            // Actualizar el icono tras la acción
            actualizarIconoFavorito(producto);
        });
    }

    /**
     * Comprueba si el producto está marcado como favorito por el usuario actual
     * y actualiza el icono del corazón según corresponda.
     *
     * @param producto producto a comprobar en la lista de favoritos
     */
    private void actualizarIconoFavorito(producto producto) {
        Session sessionAux = newSession();
        productoFavorito favorito = sessionAux.createQuery(
                        "FROM productoFavorito WHERE producto = :producto AND usuario = :usuario", productoFavorito.class)
                .setParameter("producto", producto)
                .setParameter("usuario", Sesion.usuario)
                .uniqueResult();
        sessionAux.close();

        if (favorito != null) {
            iconoCorazon.setImage(new Image(getClass().getResourceAsStream("/iconos/corazonEliminar.png")));
        } else {
            iconoCorazon.setImage(new Image(getClass().getResourceAsStream("/iconos/corazonAniadir.png")));
        }
    }



}