package controllers;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.comercio;
import model.solicitudComercio;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.hibernate.Session;
import org.hibernate.Transaction;
import utilities.Paths;

import javax.persistence.Query;
import java.io.IOException;
import java.util.List;

import static controllers.NavegacionController.navegar;
import static database.Sesion.newSession;
import static database.Sesion.usuario;
import static utilities.LogAdministrador.escribirLogInfo;
import static utilities.LogAdministrador.inicioInfoLogConsola;

public class ADMINISTRACIONController {

    @FXML
    private VBox contenedorSolicitudes;

    Session session = newSession();


    @FXML
    public void initialize() {
        cargarSolicitudes();
    }

    private void cargarSolicitudes() {
        try {
            Query qSolicitudes = session.createQuery("from solicitudComercio");
            List<solicitudComercio> lista = qSolicitudes.getResultList();

            if (lista.isEmpty()) {
                mostrarAlerta("Información", "No hay solicitudes para mostrar.");
                return;
            }

            for (solicitudComercio solicitud : lista) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(Paths.FXML_TARJETA_SOLICITUD_COMERCIO));
                    HBox tarjeta = loader.load();

                    TarjetaSolicitudComercioController controller = loader.getController();
                    controller.setDatos(solicitud, this);

                    contenedorSolicitudes.getChildren().add(tarjeta);
                } catch (IOException e) {
                    mostrarAlerta("Error cargando tarjeta", "No se pudo cargar una tarjeta: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            mostrarAlerta("Error cargando solicitudes", "Ocurrió un error al cargar las solicitudes: " + e.getMessage());
        }
    }

    public void aceptarSolicitud(solicitudComercio solicitud) {
        Transaction tx = session.beginTransaction();

        comercio nuevo = new comercio();
        nuevo.setNombre(solicitud.getNombre());
        nuevo.setNif(solicitud.getNif());
        nuevo.setImagen(solicitud.getImagen());
        nuevo.setDireccion(solicitud.getDireccion());
        nuevo.setCodigoPostal(solicitud.getCodigoPostal());
        nuevo.setMunicipio(solicitud.getMunicipio());
        nuevo.setProvincia(solicitud.getProvincia());
        nuevo.setCoordenadas(solicitud.getCoordenadas());
        nuevo.setPropietario(solicitud.getSolicitante());

        usuario.setPropietario(true);
        session.update(usuario);
        session.save(nuevo);
        session.remove(solicitud);
        tx.commit();

        recargarSolicitudes();
    }

    public void rechazarSolicitud(solicitudComercio solicitud) {
        Transaction tx = session.beginTransaction();
        session.remove(solicitud);
        tx.commit();

        recargarSolicitudes();
    }

    private void recargarSolicitudes() {
        contenedorSolicitudes.getChildren().clear();
        cargarSolicitudes();
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
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

}
