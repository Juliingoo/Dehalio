package controllers;

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

    public static void navegar(Stage stage, String fxmlPath) throws IOException {
        //Se guarda la escena actual en el historial
        if (stage.getScene() != null) {
            historialEscenas.push(stage.getScene());
        }

        //Carga nueva escena
        Parent root = FXMLLoader.load(NavegacionController.class.getResource(fxmlPath));
        stage.setScene(new Scene(root));

        stage.show();
    }

    public static void irAtras(Stage stage) {
        if (!historialEscenas.isEmpty()) {
            stage.setScene(historialEscenas.pop());
            stage.show();
        }
    }

    public static void navegarConCarga(ActionEvent event, Stage stage, String fxmlPath){

        // Crear un ProgressIndicator
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxSize(50, 50);

        // Mostrar el ProgressIndicator en el centro de la escena
        AnchorPane root = (AnchorPane) ((Node) event.getSource()).getScene().getRoot();
        root.getChildren().add(progressIndicator);
        AnchorPane.setTopAnchor(progressIndicator, root.getHeight() / 2 - 25);
        AnchorPane.setLeftAnchor(progressIndicator, root.getWidth() / 2 - 25);

        // Crear un Task para simular la carga
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                // Simular un tiempo de carga
                Thread.sleep(10);
                return null;
            }
        };

        // Al completar la tarea, navegar a la pantalla de registro
        task.setOnSucceeded(e -> {
            root.getChildren().remove(progressIndicator); // Ocultar el ProgressIndicator
            try {
                NavegacionController.navegar(stage, fxmlPath);
            } catch (IOException ex) {
                System.out.println(inicioErrorLogConsola() + "Error: " + ex.getMessage());
                escribirLogError("Error: " + ex.getMessage());
            }
        });

        // Ejecutar la tarea en un hilo separado
        new Thread(task).start();
    }

    public static void mostrarError(String titulo, String cabecera, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(cabecera);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    public static void mostrarInformacion(String titulo, String cabecera, String mensaje){
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(cabecera);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

}
