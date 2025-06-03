package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.administracion;
import org.hibernate.Query;
import org.hibernate.Session;
import utilities.Paths;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.List;

import static controllers.NavegacionController.mostrarError;
import static controllers.NavegacionController.navegar;
import static database.Sesion.newSession;
import static utilities.LogAdministrador.*;

public class PAS_ADMIN {

    public Button atrasBtn;
    public Button aceptarBtn;
    @FXML
    private TextField contraseniaCampo;

    Session session = newSession();

    /**
     * Navega a la pantalla de ajustes al recibir el evento correspondiente.
     * Muestra un error si ocurre un problema durante la navegación.
     *
     * @param event el evento de acción que dispara la navegación
     */
    public void atrasAction(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            NavegacionController.navegar(stage, Paths.PRI_AJU);
        } catch (IOException e) {
            System.out.println("Error al navegar a ajustes: " + e.getMessage());
            escribirLogError("Error al navegar a ajustes: " + e.getMessage());
        }
    }

    /**
     * Valida la contraseña ingresada descifrándola y comparándola con la clave esperada.
     * Si la autenticación es correcta, navega a la pantalla de administración; de lo contrario, muestra un error y desactiva los controles temporalmente.
     *
     * @param event el evento de acción que dispara la validación
     */
    public void aceptarAction(ActionEvent event) {
        String palabra = "admin";

        String clave = contraseniaCampo.getText();

        Query<administracion> qAdministracion = session.createQuery("FROM administracion");

        List<administracion> administracionList = qAdministracion.getResultList();

        String palabraCifrada = administracionList.getFirst().getContrasenia();

        try {
            SecretKeySpec secretKey = new SecretKeySpec(clave.getBytes("UTF-8"), "AES");
            Cipher descifrado = Cipher.getInstance("AES/ECB/PKCS5Padding");
            descifrado.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] descifradoDeNuevo = descifrado.doFinal(Base64.getDecoder().decode(palabraCifrada));

            String palabraDescifrada = new String(descifradoDeNuevo);

            if (palabraDescifrada.equals(palabra)){
                try {
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    NavegacionController.navegar(stage, Paths.ADMINISTRACION);
                } catch (IOException e) {
                    System.out.println("Error al navegar a ajustes: " + e.getMessage());
                    escribirLogError("Error al navegar a ajustes: " + e.getMessage());
                }
            } else {
                mostrarError("Contraseña incorrecta", "Clave erronea", "Ha introducido una clave erronea");
                desactivarTodoTiempo(5000);
            }

        } catch (Exception e) {
            escribirLogError(e.getMessage());
            mostrarError("Error", "Introduzca la clave de nuevo", e.getMessage());
            desactivarTodoTiempo(5000);
        }
    }

    /**
     * Desactiva los controles de la interfaz durante el tiempo especificado para evitar intentos repetidos.
     * Reactiva los controles una vez transcurrido el tiempo.
     *
     * @param cooldownMiliseg tiempo en milisegundos que los controles permanecerán desactivados
     */
    public void desactivarTodoTiempo(int cooldownMiliseg){
        new Thread(() -> {
            try {
                System.out.println(inicioInfoLogConsola() +
                        "Desactivando elementos de la pasarela durante " + cooldownMiliseg + " milisegundos");
                escribirLogInfo("Desactivando de la pasarela elementos durante " + cooldownMiliseg + " milisegundos");
                atrasBtn.setDisable(true);
                aceptarBtn.setDisable(true);
                contraseniaCampo.setDisable(true);
                Thread.sleep(cooldownMiliseg);
                System.out.println(inicioInfoLogConsola() +
                        "Activando de la pasarela elementos de nuevo");
                escribirLogInfo("Activando de la pasarela elementos de nuevo");
                atrasBtn.setDisable(false);
                aceptarBtn.setDisable(false);
                contraseniaCampo.setDisable(false);

            } catch (InterruptedException e) {
                escribirLogError("Error en el Thread: " + e.getMessage());
            }
        }).start();
    }

}
