package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
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

    @FXML
    private TextField contraseniaCampo;

    Session session = newSession();

    public void atrasAction(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            NavegacionController.navegar(stage, Paths.PRI_AJU);
        } catch (IOException e) {
            System.out.println("Error al navegar a ajustes: " + e.getMessage());
            escribirLogError("Error al navegar a ajustes: " + e.getMessage());
        }
    }

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
                Thread.sleep(2000);
            }

        } catch (Exception e) {
            escribirLogError(e.getMessage());
        }


    }

}
