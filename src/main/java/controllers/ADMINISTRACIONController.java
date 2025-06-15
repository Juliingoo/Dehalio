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

import static controllers.NavegacionController.mostrarInformacion;
import static controllers.NavegacionController.navegar;
import static database.Sesion.newSession;
import static database.Sesion.usuario;
import static utilities.LogAdministrador.escribirLogInfo;
import static utilities.LogAdministrador.inicioInfoLogConsola;

public class ADMINISTRACIONController {

    @FXML
    private VBox contenedorSolicitudes;

    Session session = newSession();

    /**
     * Inicializa el controlador cargando las solicitudes pendientes al iniciar la vista.
     */
    @FXML
    public void initialize() {
        cargarSolicitudes();
    }

    /**
     * Recupera las solicitudes de comercio desde la base de datos y las muestra en la interfaz.
     * Muestra una alerta si no hay solicitudes o si ocurre un error al cargarlas.
     */
    private void cargarSolicitudes() {
        try {
            Query qSolicitudes = session.createQuery("from solicitudComercio");
            List<solicitudComercio> lista = qSolicitudes.getResultList();

            if (lista.isEmpty()) {
                mostrarInformacion("Información", "No hay solicitudes para mostrar.","");
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
                    mostrarInformacion("Error","Error cargando tarjeta", "No se pudo cargar una tarjeta: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            mostrarInformacion("Error", "Error cargando solicitudes", "Ocurrió un error al cargar las solicitudes: " + e.getMessage());
        }
    }

    /**
     * Acepta una solicitud de comercio, crea un nuevo comercio con los datos de la solicitud,
     * actualiza el usuario como propietario y elimina la solicitud de la base de datos.
     * Recarga la lista de solicitudes tras la operación.
     *
     * @param solicitud la solicitud de comercio a aceptar
     */
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

    /**
     * Elimina una solicitud de comercio de la base de datos y recarga la lista de solicitudes.
     *
     * @param solicitud la solicitud de comercio a rechazar
     */
    public void rechazarSolicitud(solicitudComercio solicitud) {
        Transaction tx = session.beginTransaction();
        session.remove(solicitud);
        tx.commit();

        recargarSolicitudes();
    }

    /**
     * Limpia el contenedor de solicitudes y vuelve a cargar las solicitudes desde la base de datos.
     */
    private void recargarSolicitudes() {
        contenedorSolicitudes.getChildren().clear();
        cargarSolicitudes();
    }

    /**
     * Navega a la pantalla anterior (ajustes) al recibir el evento correspondiente.
     *
     * @param event el evento de acción que dispara la navegación
     */
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
