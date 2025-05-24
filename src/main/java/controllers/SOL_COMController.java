package controllers;

import database.Sesion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.solicitudComercio;
import model.usuario;
import org.hibernate.Session;
import utilities.Paths;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

import static controllers.NavegacionController.*;
import static database.Sesion.newSession;
import static utilities.LogAdministrador.escribirLogInfo;
import static utilities.LogAdministrador.inicioInfoLogConsola;


public class SOL_COMController {

    @FXML
    private TextField nombreComercioCampo;

    @FXML
    private TextField nifCampo;

    @FXML
    private TextField direccionCampo;

    @FXML
    private TextField codigoPostalCampo;

    @FXML
    private TextField municipioCampo;

    @FXML
    private TextField coordenadasCampo;

    @FXML
    private TextField provinciaCampo;

    @FXML
    private ImageView imagenComercioView;

    @FXML
    private Button cargarImagenBtn;

    @FXML
    private Button PresentarSolicitBtn;

    private byte[] imagenBuffer;

    Session session = newSession();

    private void limpiarFormulario() {
        nombreComercioCampo.clear();
        nifCampo.clear();
        direccionCampo.clear();
        codigoPostalCampo.clear();
        municipioCampo.clear();
        provinciaCampo.clear();
        imagenComercioView.setImage(null);
        imagenBuffer = null;
    }


    public void volverAtrasAction(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        try {
            navegar(stage, Paths.PRI_AJU);
        } catch (IOException e) {
            System.out.println(inicioInfoLogConsola() + "Navegando a ajustes");
            escribirLogInfo("Navegando a ajustes");
        }
    }

    @FXML
    private void cargarImagenAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());
        if (file != null) {
            try (FileInputStream inputStream = new FileInputStream(file)) {
                imagenBuffer = inputStream.readAllBytes();
                imagenComercioView.setImage(new Image(new ByteArrayInputStream(imagenBuffer)));
            } catch (IOException e) {
                mostrarError("Error al cargar imagen", "No se pudo cargar la imagen.", e.getMessage());
            }
        }
    }

    public void presentarSolicitud(ActionEvent event) {
        try {
            if (nombreComercioCampo.getText().isEmpty() || nifCampo.getText().isEmpty() ||
                    direccionCampo.getText().isEmpty() || coordenadasCampo.getText().isEmpty() ||
                    codigoPostalCampo.getText().isEmpty() || municipioCampo.getText().isEmpty() ||
                    provinciaCampo.getText().isEmpty()) {
                mostrarError("Campos obligatorios", "Se deben rellenar los campos", "Por favor, completa todos los campos obligatorios.");
                return;
            }

            solicitudComercio nuevaSolicitud = new solicitudComercio();
            nuevaSolicitud.setNombre(nombreComercioCampo.getText());
            nuevaSolicitud.setNif(nifCampo.getText());
            nuevaSolicitud.setDireccion(direccionCampo.getText());
            nuevaSolicitud.setCodigoPostal(codigoPostalCampo.getText());
            nuevaSolicitud.setCoordenadas(coordenadasCampo.getText());
            nuevaSolicitud.setMunicipio(municipioCampo.getText());
            nuevaSolicitud.setProvincia(provinciaCampo.getText());
            nuevaSolicitud.setImagen(imagenBuffer);

            usuario usuarioActual = Sesion.usuario;
            nuevaSolicitud.setSolicitante(usuarioActual);


            session.beginTransaction();
            session.save(nuevaSolicitud);
            session.getTransaction().commit();

            mostrarInformacion("Solicitud registrada", "Registro exitoso", "La solicitud del comercio ha sido registrada exitosamente.");
            limpiarFormulario();

        } catch (Exception e) {
            session.getTransaction().rollback();
            mostrarError("Error al registrar", "No se pudo registrar la solicitud.", e.getMessage());
        }
    }
}
