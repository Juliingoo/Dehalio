package controllers;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.usuario;
import utilities.LogAdministrador;
import utilities.Paths;
import utilities.LogAdministrador.*;

import java.io.IOException;
import java.util.Stack;

import static utilities.LogAdministrador.escribirLogError;
import static utilities.LogAdministrador.inicioErrorLogConsola;

public class NavegacionController {
    private static Stack<Scene> historialEscenas = new Stack<>();

    /**
     * Cambia la escena actual del `Stage` al FXML especificado, manteniendo el historial de escenas para navegación hacia atrás.
     *
     * @param stage el escenario principal donde se mostrará la nueva escena
     * @param fxmlPath la ruta del archivo FXML a cargar
     * @throws IOException si ocurre un error al cargar el archivo FXML
     */
    public static void navegar(Stage stage, String fxmlPath) throws IOException {
        // Guarda escena actual
        if (stage.getScene() != null) {
            historialEscenas.push(stage.getScene());
        }

        // Guarda tamaño actual
        double ancho = stage.getWidth();
        double alto = stage.getHeight();

        // Carga nueva escena
        Parent root = FXMLLoader.load(NavegacionController.class.getResource(fxmlPath));
        Scene nuevaEscena = new Scene(root);
        stage.setScene(nuevaEscena);

        // Restaura tamaño
        stage.setWidth(ancho);
        stage.setHeight(alto);

        // Asegura que la ventana no esté minimizada
        stage.setIconified(false);

        // Muestra ventana
        stage.show();
    }


    /**
     * Restaura la escena anterior desde el historial, permitiendo volver atrás en la navegación.
     *
     * @param stage el escenario principal donde se restaurará la escena previa
     */
    public static void irAtras(Stage stage) {
        if (!historialEscenas.isEmpty()) {
            stage.setScene(historialEscenas.pop());
            stage.show();
        }
    }

    /**
     * Cambia la escena mostrando un indicador de progreso mientras se carga la nueva vista en segundo plano.
     * Útil para transiciones que pueden tardar debido a la carga de recursos.
     *
     * @param event el evento de acción que dispara la navegación
     * @param stage el escenario principal donde se mostrará la nueva escena
     * @param fxmlPath la ruta del archivo FXML a cargar
     */
    public static void navegarConCarga(ActionEvent event, Stage stage, String fxmlPath) {
        // Crear un ProgressIndicator
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxSize(50, 50);

        // Mostrar el ProgressIndicator en el centro de la escena
        AnchorPane root = (AnchorPane) ((Node) event.getSource()).getScene().getRoot();
        root.getChildren().add(progressIndicator);
        AnchorPane.setTopAnchor(progressIndicator, root.getHeight() / 2 - 25);
        AnchorPane.setLeftAnchor(progressIndicator, root.getWidth() / 2 - 25);

        // Crear la tarea para cargar la nueva escena
        Task<Parent> task = new Task<>() {
            @Override
            protected Parent call() throws Exception {
                // Cargar la nueva vista (esto puede tardar si hay muchos elementos)
                return FXMLLoader.load(NavegacionController.class.getResource(fxmlPath));
            }
        };

        // Cuando se completa exitosamente
        task.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                try {
                    Parent newRoot = task.get();
                    Scene newScene = new Scene(newRoot);
                    stage.setScene(newScene);
                    stage.setIconified(false); // Por si acaso estaba minimizada
                    stage.centerOnScreen();    // <-- Esto centra la ventana en pantalla
                    stage.show();
                } catch (Exception ex) {
                    System.out.println("Error cargando escena: " + ex.getMessage());
                    escribirLogError("Error: " + ex.getMessage());
                } finally {
                    root.getChildren().remove(progressIndicator);
                }
            });
        });

        // En caso de error
        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                root.getChildren().remove(progressIndicator);
                System.out.println("Error: " + task.getException().getMessage());
                escribirLogError("Error: " + task.getException().getMessage());
            });
        });

        // Ejecutar en un hilo en segundo plano
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Muestra una alerta de error con el título, cabecera y mensaje especificados.
     *
     * @param titulo el título de la alerta
     * @param cabecera el texto de cabecera de la alerta
     * @param mensaje el mensaje detallado del error
     */
    public static void mostrarError(String titulo, String cabecera, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(cabecera);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    /**
     * Muestra una alerta informativa con el título, cabecera y mensaje especificados.
     *
     * @param titulo el título de la alerta
     * @param cabecera el texto de cabecera de la alerta
     * @param mensaje el mensaje informativo a mostrar
     */
    public static void mostrarInformacion(String titulo, String cabecera, String mensaje){
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(cabecera);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

}
