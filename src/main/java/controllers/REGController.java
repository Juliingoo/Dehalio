package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.usuario;
import org.hibernate.Session;

import org.hibernate.Query;
import utilities.LogAdministrador;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static controllers.NavegacionController.mostrarError;
import static controllers.NavegacionController.mostrarInformacion;
import static database.Sesion.newSession;
import static utilities.Encriptacion.encriptarMD5;

public class REGController {
    @FXML
    private TextField nombreCampo;

    @FXML
    private TextField apellidosCampo;

    @FXML
    private TextField emailCampo;

    @FXML
    private TextField contraseniaCampo;

    @FXML
    private TextField repetirContraseniaCampo;

    Session session = newSession();

    @FXML
    void volverAtrasAction(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        NavegacionController.irAtras(stage);
    }

    @FXML
    void registrarseAction(ActionEvent event){
        String nombre = nombreCampo.getText();
        String apellidos = apellidosCampo.getText();
        String email = emailCampo.getText();
        String contrasenia = contraseniaCampo.getText();
        String repetirContrasenia = repetirContraseniaCampo.getText();

        if(nombre.isEmpty() || apellidos.isEmpty() || email.isEmpty() || contrasenia.isEmpty() || repetirContrasenia.isEmpty()){
            System.out.println("Campos vacios. Rellena todos los campos");
            LogAdministrador.escribirLogInfo("Se ha intentado registrar con los campos vacios");
            NavegacionController.mostrarError("Error", "Error al crear usuario", "Rellena todos los campos antes de continuar");
            return;
        }

        usuario usuarioNuevo = new usuario();

        usuarioNuevo.setEmail(email);


        if(existeUsuario(usuarioNuevo)){
            System.out.println("El usuario ya existe. El correo electrónico ya está en uso");
            LogAdministrador.escribirLogInfo("Error en la creación del usuario, el usuario ya existe. Correo en uso.");
            NavegacionController.mostrarError("Error", "Error al crear usuario", "El usuario ya existe. El correo electrónico ya está en uso");
            return;
        }

        if(!contrasenia.equals(repetirContrasenia)){
            System.out.println("Las contraseñas no coinciden");
            LogAdministrador.escribirLogInfo("Error en la creación del usuario. Las contraseñas no coinciden.");
            NavegacionController.mostrarError("Error", "Error al crear usuario","Las contraseñas no coinciden");
            contraseniaCampo.clear();
            repetirContraseniaCampo.clear();
            return;
        }

        usuarioNuevo.setNombre(nombre);
        usuarioNuevo.setApellidos(apellidos);
        usuarioNuevo.setContrasenia(encriptarMD5(contrasenia));
        usuarioNuevo.setPropietario(false);

        try {
            session.beginTransaction();
            session.save(usuarioNuevo);
            session.getTransaction().commit();

            if(existeUsuario(usuarioNuevo)){
                mostrarInformacion("Registro exitoso", "Se ha registrado su usuario correctamente",
                        "Gracias por unirse a Dehalio 😀. Ya puede iniciar sesión. ");
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                NavegacionController.irAtras(stage);
            } else {
                mostrarError("Error al registrarse", "Se ha producido un error al registrarse",
                        "No se pudo guardar el usuario nuevo");
            }


        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage() );
        }

    }

    boolean existeUsuario(usuario usuario){
        Query<usuario> qUsuarioComprobacionEmail = newSession().createQuery("from usuario where email = :email");
        qUsuarioComprobacionEmail.setParameter("email", usuario.getEmail());

        List<usuario> usuarioComprobacionEmailLista = qUsuarioComprobacionEmail.getResultList();

        return !usuarioComprobacionEmailLista.isEmpty();
    }

}
