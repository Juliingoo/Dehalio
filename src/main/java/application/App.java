package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import utilities.Paths;
import utilities.LogAdministrador;


public class App extends Application{

    /**
     * Punto de entrada principal de la aplicación. Inicializa el sistema de logs y lanza la aplicación JavaFX.
     *
     * @param args argumentos de línea de comandos
     */
    public static void main(String[] args) {
        System.out.println(LogAdministrador.inicioInfoLogConsola() + "Aplicación iniciada");
        LogAdministrador.comprobarDirectorioLogs();
        LogAdministrador.escribirLogInfo("Aplicación iniciada");
        launch();
    }

    /**
     * Inicializa y muestra la ventana principal de la aplicación cargando la escena inicial desde el archivo FXML.
     *
     * @param stage el escenario principal proporcionado por JavaFX
     * @throws Exception si ocurre un error al cargar la interfaz
     */
    @Override
    public void start(Stage stage) throws Exception {
        System.out.println(LogAdministrador.inicioInfoLogConsola() + "Arrancando ventana");
        LogAdministrador.escribirLogInfo("Arrancando ventana");
        AnchorPane load = FXMLLoader.load(getClass().getResource(Paths.INI));
        Scene scene = new Scene(load);
        stage.setScene(scene);
        stage.getIcons().add(new Image("/images/Logo_Dehalio_Icono_Rojo.png"));
        stage.show();
    }
}
