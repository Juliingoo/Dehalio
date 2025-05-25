package controllers;

import database.Sesion;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.*;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import utilities.LogAdministrador;
import utilities.Paths;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static controllers.NavegacionController.*;
import static database.Sesion.newSession;
import static utilities.LogAdministrador.*;
import static utilities.LogAdministrador.inicioInfoLogConsola;

public class PRI_AJUController implements Initializable {

    @FXML private TextField tfNombre, tfApellidos, tfEmail, tfDireccion;
    @FXML private TextField tfCodigoPostal, tfMunicipio, tfProvincia;
    @FXML private Button btnGuardarUsuario;

    @FXML private VBox paneNoPropietario, panePropietario;
    @FXML private Button btnSolicitarPropietario;
    @FXML private ListView<producto> lvProductos;
    @FXML private Button btnAddProd, btnEditProd, btnDeleteProd;

    @FXML private VBox paneEditorProducto;
    @FXML private ImageView ivProductoEd;
    @FXML private Button btnChangeImage, btnSaveProd, btnCancelProd;
    @FXML private TextField tfProdNombre, tfProdPrecio;
    @FXML private ComboBox<tipoProducto> cbTipoProducto;
    @FXML private ComboBox<categoriaTipoProducto> cbCategoriaProd;

    private usuario usuarioActual;
    private producto productoEnEdicion;
    private byte[] imagenBuffer;

    private Session sesion = newSession();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        escribirLogInfo("Inicializando pantalla de ajustes.");

        // cargar datos de comboBoxes
        try (Session s = newSession()) {
            List<tipoProducto> tipos = s.createQuery("FROM tipoProducto", tipoProducto.class).list();
            cbTipoProducto.getItems().setAll(tipos);
            List<categoriaTipoProducto> cats = s.createQuery("FROM categoriaTipoProducto", categoriaTipoProducto.class).list();
            cbCategoriaProd.getItems().setAll(cats);
        } catch (Exception e) {
            escribirLogError("Error cargando tipos/categorías: " + e);
        }

        paneEditorProducto.setVisible(false);
        paneEditorProducto.setManaged(false);

        // evento selección producto
        lvProductos.getSelectionModel().selectedItemProperty().addListener((obs,o,n) -> {
            if (n != null) onEditProducto(null);
        });


        establecerListadosNombre();

        cargarUsuarioYComercio();
    }

    public void establecerListadosNombre(){
        lvProductos.setCellFactory(lv -> new ListCell<producto>() {
            @Override
            protected void updateItem(producto item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNombre()); // Aquí defines lo que se muestra
                }
            }
        });

        cbTipoProducto.setConverter(new StringConverter<tipoProducto>() {
            @Override
            public String toString(tipoProducto tp) {
                return tp != null ? tp.getNombre() : "";
            }

            @Override
            public tipoProducto fromString(String s) {
                return null;
            }
        });

        cbCategoriaProd.setConverter(new StringConverter<categoriaTipoProducto>() {
            @Override
            public String toString(categoriaTipoProducto cat) {
                return cat != null ? cat.getNombre() : "";
            }

            @Override
            public categoriaTipoProducto fromString(String s) {
                return null;
            }
        });
    }

    public void btnInicioAction(ActionEvent event) {
        System.out.println(inicioInfoLogConsola() + "Boton inicio pulsado");
        escribirLogInfo("Boton inicio pulsado");
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            navegar(stage, Paths.PRI_INI);
        } catch (IOException e) {
            System.out.println("Error al navegar a inicio: " + e.getMessage());
            escribirLogError("Error al navegar a inicio: " + e.getMessage());
        }
    }

    public void btnTiendasAction(ActionEvent event) {
        System.out.println(inicioInfoLogConsola() + "Boton tiendas pulsado");
        escribirLogInfo("Boton tiendas pulsado");
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            navegar(stage, Paths.PRI_TIEN);
        } catch (IOException e) {
            System.out.println("Error al navegar a tiendas: " + e.getMessage());
            escribirLogError("Error al navegar a tiendas: " + e.getMessage());
        }
    }

    public void btnFavoritosAction(ActionEvent event) {
        System.out.println(inicioInfoLogConsola() + "Boton favoritos pulsado");
        escribirLogInfo("Boton favoritos pulsado");
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            navegar(stage, Paths.PRI_FAV);
        } catch (IOException e) {
            System.out.println("Error al navegar a favoritos: " + e.getMessage());
            escribirLogError("Error al navegar a favoritos: " + e.getMessage());
        }
    }

    public void btnAjustesAction(ActionEvent event) {
        System.out.println(inicioInfoLogConsola() + "Boton ajustes pulsado");
        escribirLogInfo("Boton ajustes pulsado");
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            navegar(stage, Paths.PRI_AJU);
        } catch (IOException e) {
            System.out.println("Error al navegar a ajustes: " + e.getMessage());
            escribirLogError("Error al navegar a ajustes: " + e.getMessage());
        }
    }

    private void cargarUsuarioYComercio() {
        // asumimos que usuarioActual se obtuvo en login y se pasa por NavegacionController
        usuarioActual = Sesion.usuario;
        tfNombre.setText(usuarioActual.getNombre());
        tfApellidos.setText(usuarioActual.getApellidos());
        tfEmail.setText(usuarioActual.getEmail());
        tfDireccion.setText(usuarioActual.getDireccion());
        tfCodigoPostal.setText(usuarioActual.getCodigoPostal());
        tfMunicipio.setText(usuarioActual.getMunicipio());
        tfProvincia.setText(usuarioActual.getProvincia());

        boolean esProp = usuarioActual.isPropietario();

        paneNoPropietario.setVisible(!esProp);
        paneNoPropietario.setManaged(!esProp);
        panePropietario.setVisible(esProp);
        panePropietario.setManaged(esProp);

        if (esProp) {
            cargarProductosEnList();
        }
    }

    private void cargarProductosEnList() {
        lvProductos.getItems().clear();
        try (Session s = newSession()) {
            List<producto> lista = s.createQuery(
                            "FROM producto p JOIN FETCH p.tipo t JOIN FETCH t.categoria WHERE p.comercio.idComercio = :id",
                            producto.class)
                    .setParameter("id", Sesion.comercio.getIdComercio())
                    .list();
            lvProductos.getItems().setAll(lista);
        } catch (Exception e) {
            escribirLogError("Error cargando productos: " + e);
            mostrarError("Carga productos", "No fue posible cargar los productos.", e.getMessage());
        }
    }


    // —— ACCIONES USUARIO ————————————————————————————————————————————————————
    @FXML
    private void onGuardarUsuario(ActionEvent ev) {
        try (Session s = newSession()) {
            Transaction tx = s.beginTransaction();
            usuarioActual.setNombre(tfNombre.getText());
            usuarioActual.setApellidos(tfApellidos.getText());
            usuarioActual.setEmail(tfEmail.getText());
            usuarioActual.setDireccion(tfDireccion.getText());
            usuarioActual.setCodigoPostal(tfCodigoPostal.getText());
            usuarioActual.setMunicipio(tfMunicipio.getText());
            usuarioActual.setProvincia(tfProvincia.getText());
            s.update(usuarioActual);
            tx.commit();
            escribirLogInfo("Datos de usuario actualizados.");
        } catch (Exception e) {
            escribirLogError("Error guardando usuario: " + e);
            mostrarError("Guardar usuario", "No se pudo actualizar tus datos.", e.getMessage());
        }
    }

    // —— SOLICITAR PROPIETARIO —————————————————————————————————————————————
    @FXML
    private void onSolicitarPropietario(ActionEvent event) {
        Query<solicitudComercio> qSolicitud = sesion.createQuery("FROM solicitudComercio WHERE solicitante = :solicitanteId");
        qSolicitud.setParameter("solicitanteId", Sesion.usuario);
        List<solicitudComercio> solicitudList = qSolicitud.getResultList();

        if(solicitudList.isEmpty()) {
            System.out.println(inicioInfoLogConsola() + "Abriendo formulario de solicitud de propietario");
            escribirLogInfo("Abriendo formulario de solicitud de propietario");

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            try {
                navegar(stage, Paths.SOL_COM);
            } catch (IOException e) {
                System.out.println(inicioInfoLogConsola() + "Error al navegar al formulario de solicitud de propietario");
                escribirLogInfo("Error al navegar al formulario de solicitud de propietario");
            }
        }else{
            mostrarInformacion("Solitud registrada", "Existe una solicitud de comercio registrada",
                    "Su solicitud está pendiente de aceptación");
        }
    }

    // —— CRUD PRODUCTOS ——————————————————————————————————————————————————————
    @FXML
    private void onAddProducto(ActionEvent ev) {
        productoEnEdicion = null;
        abrirEditorNuevo();
    }

    @FXML
    private void onEditProducto(ActionEvent ev) {
        productoEnEdicion = lvProductos.getSelectionModel().getSelectedItem();
        if (productoEnEdicion == null) return;
        tfProdNombre.setText(productoEnEdicion.getNombre());
        tfProdPrecio.setText(String.valueOf(productoEnEdicion.getPrecio()));
        cbTipoProducto.setValue(productoEnEdicion.getTipo());
        cbCategoriaProd.setValue(productoEnEdicion.getTipo().getCategoria());
        imagenBuffer = productoEnEdicion.getImagen();
        if (imagenBuffer != null && imagenBuffer.length>0) {
            ivProductoEd.setImage(new Image(new ByteArrayInputStream(imagenBuffer)));
        } else {
            ivProductoEd.setImage(null);
        }
        abrirEditorNuevo();
    }

    @FXML
    private void onDeleteProducto(ActionEvent ev) {
        producto sel = lvProductos.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        try (Session s = newSession()) {
            Transaction tx = s.beginTransaction();
            s.delete(sel);
            tx.commit();
            escribirLogInfo("Producto eliminado: " + sel.getNombre());
            cargarProductosEnList();
        } catch (Exception e) {
            escribirLogError("Error eliminando producto: " + e);
            mostrarError("Eliminar producto", "No se pudo eliminar el producto.", e.getMessage());
        }
    }

    private void abrirEditorNuevo() {
        paneEditorProducto.setVisible(true);
        paneEditorProducto.setManaged(true);
    }

    @FXML
    private void onCambiarImagen(ActionEvent ev) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Seleccionar imagen");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes","*.png","*.jpg","*.jpeg"));
        File f = chooser.showOpenDialog(ivProductoEd.getScene().getWindow());
        if (f != null) {
            try (FileInputStream in = new FileInputStream(f)) {
                imagenBuffer = in.readAllBytes();
                ivProductoEd.setImage(new Image(new ByteArrayInputStream(imagenBuffer)));
            } catch (IOException e) {
                escribirLogError("Error cargando imagen: " + e);
                mostrarError("Imagen", "No se pudo leer la imagen.", e.getMessage());
            }
        }
    }

    @FXML
    private void onGuardarProducto(ActionEvent ev) {
        if (tfProdNombre.getText().isBlank()) {
            mostrarError("Validación", "El nombre es obligatorio.", "");
            return;
        }
        try (Session s = newSession()) {
            Transaction tx = s.beginTransaction();
            producto p = productoEnEdicion == null
                    ? new producto()
                    : s.get(producto.class, productoEnEdicion.getIdProducto());

            p.setNombre(tfProdNombre.getText());
            p.setPrecio(Double.parseDouble(tfProdPrecio.getText()));
            p.setTipo(cbTipoProducto.getValue());
            p.setComercio(Sesion.comercio);
            if (imagenBuffer != null) p.setImagen(imagenBuffer);

            if (productoEnEdicion == null) {
                s.persist(p);
                escribirLogInfo("Producto creado: " + p.getNombre());
            } else {
                s.update(p);
                escribirLogInfo("Producto actualizado: " + p.getNombre());
            }
            tx.commit();
            cargarProductosEnList();
            onCancelarEdicion(null);
        } catch (Exception e) {
            escribirLogError("Error guardando producto: " + e);
            mostrarError("Guardar producto", "No se pudo guardar el producto.", e.getMessage());
        }
    }

    @FXML
    private void onCancelarEdicion(ActionEvent ev) {
        paneEditorProducto.setVisible(false);
        paneEditorProducto.setManaged(false);
        productoEnEdicion = null;
        imagenBuffer = null;
        ivProductoEd.setImage(null);
        tfProdNombre.clear();
        tfProdPrecio.clear();
        cbTipoProducto.getSelectionModel().clearSelection();
        cbCategoriaProd.getSelectionModel().clearSelection();
    }

    public void lanzarAdministracion(ActionEvent event) {
        System.out.println(inicioInfoLogConsola() + "Boton administración pulsado");
        escribirLogInfo("Boton administración pulsado");
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            navegar(stage, Paths.PAS_ADMIN);
        } catch (IOException e) {
            System.out.println("Error al navegar a administración: " + e.getMessage());
            escribirLogError("Error al navegar a administración: " + e.getMessage());
        }
    }
}
