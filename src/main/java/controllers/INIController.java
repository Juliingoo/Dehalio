package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.usuario;
import org.hibernate.Query;
import org.hibernate.Session;
import utilities.Paths;

import java.util.List;

import static database.Sesion.newSession;
import static utilities.Encriptacion.encriptarMD5;


public class INIController {

    @FXML
    private TextField emailCampo;
    @FXML
    private TextField contraseniaCampo;
    @FXML
    private Button iniciarSesionBtn;
    @FXML
    private Button registarseBtn;


    Session session = newSession();

    @FXML
    void iniciarSesionAction(ActionEvent event){
        System.out.println("Iniciar sesión pulsado");
        String email = emailCampo.getText();
        String contrasenia = contraseniaCampo.getText();

        Query<usuario> qUsuario = session.createQuery("from usuario where email = :email and contrasenia = :contrasenia");
        qUsuario.setParameter("email", email);
        qUsuario.setParameter("contrasenia", encriptarMD5(contrasenia));

        List<usuario> usuarioBuscadoLista = qUsuario.getResultList();

        if (!usuarioBuscadoLista.isEmpty()){
            System.out.println("Usuario encontrado");
            System.out.println("Nombre: " + usuarioBuscadoLista.getFirst().getNombre());

            if(usuarioBuscadoLista.getFirst().isPropietario()){
                System.out.println("En construcción");
            } else {
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                NavegacionController.navegarConCarga(event, stage, Paths.PRI_INI);
            }

        } else {
            System.out.println("Lista vacia");
        }

    }

    @FXML
    void registrarseAction(ActionEvent event) throws Exception{
        System.out.println("Registrarse pulsado");
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
//        NavegacionController.navegar(stage, Paths.REG);
        NavegacionController.navegarConCarga(event, stage, Paths.REG);
    }




}
