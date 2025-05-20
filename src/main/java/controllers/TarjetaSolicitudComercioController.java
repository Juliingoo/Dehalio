package controllers;

import model.solicitudComercio;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.ByteArrayInputStream;

public class TarjetaSolicitudComercioController {

    @FXML
    private Label nombreLabel;
    @FXML
    private Label nifLabel;
    @FXML
    private Label direccionLabel;
    @FXML
    private Label municipioLabel;
    @FXML
    private Label provinciaLabel;
    @FXML
    private Label codigoPostalLabel;
    @FXML
    private Label coordenadasLabel;
    @FXML
    private ImageView imagenView;
    @FXML
    private Button aceptarBtn;
    @FXML
    private Button rechazarBtn;

    private solicitudComercio solicitud;
    private ADMINISTRACIONController parentController;

    public void setDatos(solicitudComercio solicitud, ADMINISTRACIONController controller) {
        this.solicitud = solicitud;
        this.parentController = controller;

        // Mostrar datos
        nombreLabel.setText(solicitud.getNombre());
        nifLabel.setText(solicitud.getNif());
        direccionLabel.setText(solicitud.getDireccion());
        municipioLabel.setText(solicitud.getMunicipio());
        provinciaLabel.setText(solicitud.getProvincia());
        codigoPostalLabel.setText(solicitud.getCodigoPostal());
        coordenadasLabel.setText(solicitud.getCoordenadas());

        // Mostrar imagen
        if (solicitud.getImagen() != null && solicitud.getImagen().length > 0) {
            imagenView.setImage(new Image(new ByteArrayInputStream(solicitud.getImagen())));
        } else {
            imagenView.setImage(new Image(getClass().getResourceAsStream("/iconos/productoSinImagen.png")));
        }

        // Acciones botones
        aceptarBtn.setOnAction(e -> parentController.aceptarSolicitud(solicitud));
        rechazarBtn.setOnAction(e -> parentController.rechazarSolicitud(solicitud));
    }
}
