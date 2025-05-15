package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import utilities.Paths;
import utilities.LogAdministrador;
import utilities.LogAdministrador.*;

public class App extends Application{
    public static void main(String[] args) {
        System.out.println(LogAdministrador.inicioInfoLogConsola() + "Aplicación iniciada");
        LogAdministrador.comprobarDirectorioLogs();
        LogAdministrador.escribirLogInfo("Aplicación iniciada");
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        System.out.println(LogAdministrador.inicioInfoLogConsola() + "Arrancando ventana");
        LogAdministrador.escribirLogInfo("Arrancando ventana");
        AnchorPane load = FXMLLoader.load(getClass().getResource(Paths.INI));
        Scene scene = new Scene(load);
        stage.setScene(scene);
        stage.show();
    }
}
