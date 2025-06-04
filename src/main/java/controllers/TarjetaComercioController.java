package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.comercio;
import utilities.Paths;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static controllers.NavegacionController.navegar;

public class TarjetaComercioController {

    @FXML
    private ImageView imageView;

    @FXML
    private Label labelNombre;

    @FXML
    private Label labelDireccion;

    private comercio comercioActual;

    /**
     * Configura la tarjeta visualizando la imagen, nombre y dirección del comercio recibido.
     * Si el comercio no tiene imagen, se muestra una imagen por defecto.
     *
     * @param comercio comercio cuyos datos se mostrarán en la tarjeta
     */
    public void setComercio(comercio comercio) {
        this.comercioActual = comercio;

        // Configurar la imagen
        if (comercio.getImagen() != null && comercio.getImagen().length > 0) {
            imageView.setImage(new Image(new ByteArrayInputStream(comercio.getImagen())));
        } else {
            imageView.setImage(new Image(getClass().getResourceAsStream("/iconos/comercioSinImagen.png")));
        }

        // Configurar el nombre y la dirección
        labelNombre.setText(comercio.getNombre());
        labelDireccion.setText(comercio.getDireccion());
    }

    /**
     * Navega al perfil detallado del comercio seleccionado al pulsar el botón correspondiente.
     * Pasa la información del comercio al nuevo controlador.
     *
     * @param event el evento de acción que dispara la navegación
     */
    @FXML
    private void visitarTiendaAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Paths.TIEN_PERF));
            Parent root = loader.load();

            // Obtener el controlador del perfil de la tienda
            TIEN_PERFController controller = loader.getController();
            controller.setComercio(comercioActual); // Pasar la tienda seleccionada

            // Navegar a la pantalla del perfil de la tienda
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}