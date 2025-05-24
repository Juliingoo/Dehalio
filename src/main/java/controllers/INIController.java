package controllers;

import database.Sesion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.comercio;
import model.usuario;
import org.hibernate.Query;
import org.hibernate.Session;
import utilities.LogAdministrador;
import utilities.Paths;

import java.util.List;

import static controllers.NavegacionController.mostrarError;
import static controllers.NavegacionController.mostrarInformacion;
import static database.Sesion.*;
import static utilities.Encriptacion.encriptarMD5;
import static utilities.LogAdministrador.*;


public class INIController {

    @FXML
    private TextField emailCampo;
    @FXML
    private TextField contraseniaCampo;

    Session session = newSession();

    @FXML
    void iniciarSesionAction(ActionEvent event){
        System.out.println(inicioInfoLogConsola() + "Iniciar sesión pulsado");
        escribirLogInfo("Iniciar sesion pulsado");
        String email = emailCampo.getText();
        String contrasenia = contraseniaCampo.getText();

        Query<usuario> qUsuario = session.createQuery("from usuario where email = :email and contrasenia = :contrasenia");
        qUsuario.setParameter("email", email);
        qUsuario.setParameter("contrasenia", encriptarMD5(contrasenia));

        List<usuario> usuarioBuscadoLista = qUsuario.getResultList();

        if (!usuarioBuscadoLista.isEmpty()){
            usuario = usuarioBuscadoLista.getFirst();

            if (usuario.isPropietario()) {
                Query<comercio> qComercio = session.createQuery("from comercio where propietario = :usuario");
                qComercio.setParameter("usuario", usuario);
                List<comercio> comercioLista = qComercio.getResultList();

                if (!comercioLista.isEmpty()) {
                    comercio = comercioLista.getFirst();
                    System.out.println(inicioInfoLogConsola() + "Comercio asociado encontrado: " + comercio.getNombre());
                    escribirLogInfo("Comercio asociado encontrado: " + comercio.getNombre());
                } else {
                    System.out.println(inicioInfoLogConsola() + "No se encontró un comercio asociado para el usuario propietario.");
                    escribirLogInfo("No se encontró un comercio asociado para el usuario propietario.");
                }
            }

            System.out.println(inicioInfoLogConsola() + "Usuario encontrado");
            System.out.println(inicioInfoLogConsola() + "Nombre: " + usuario.getNombre());
            escribirLogInfo("Usuario encontrado");
            escribirLogInfo("Nombre: " + usuario.getNombre());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            NavegacionController.navegarConCarga(event, stage, Paths.PRI_INI);


        } else {
            System.out.println(inicioInfoLogConsola() + "Lista vacia. Usuario no encontrado");
            mostrarError("Error", "Usuario o contraseña no encontrados", "Introduzca unas credenciales válidas");
        }

    }

    @FXML
    void registrarseAction(ActionEvent event) throws Exception{
        System.out.println(inicioInfoLogConsola() + "Registrarse pulsado");
        escribirLogInfo("Registrarse pulsado");
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        NavegacionController.navegarConCarga(event, stage, Paths.REG);
    }

}
