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

    /**
     * Inicializa la pantalla de ajustes cargando los datos de usuario, productos y configurando los controles.
     *
     * @param location ubicación utilizada para resolver rutas relativas
     * @param resources recursos utilizados para la internacionalización
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        escribirLogInfo("Inicializando pantalla de ajustes.");

        //Refrescar usuario y comercio
        try (Session s = newSession()) {
            if (Sesion.usuario != null) {
                Sesion.usuario = s.get(model.usuario.class, Sesion.usuario.getIdUsuario());
                if (Sesion.usuario.isPropietario()) {
                    // Obtener comercio asociado solo si es propietario
                    List<model.comercio> comercios = s.createQuery(
                                    "FROM comercio WHERE propietario = :usuario", model.comercio.class)
                            .setParameter("usuario", Sesion.usuario)
                            .list();
                    if (!comercios.isEmpty()) {
                        Sesion.comercio = comercios.getFirst();
                    } else {
                        Sesion.comercio = null;
                    }
                } else {
                    Sesion.comercio = null;
                }
            }
        } catch (Exception e) {
            escribirLogError("Error refrescando usuario/comercio: " + e);
        }

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

    /**
     * Configura la visualización de nombres en los listados y combos de productos y categorías.
     */
    public void establecerListadosNombre(){
        lvProductos.setCellFactory(lv -> new ListCell<producto>() {
            @Override
            protected void updateItem(producto item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNombre());
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

    /**
     * Navega a la pantalla de inicio al pulsar el botón correspondiente.
     *
     * @param event el evento de acción que dispara la navegación
     */
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

    /**
     * Navega a la pantalla de tiendas al pulsar el botón correspondiente.
     *
     * @param event el evento de acción que dispara la navegación
     */
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

    /**
     * Navega a la pantalla de favoritos al pulsar el botón correspondiente.
     *
     * @param event el evento de acción que dispara la navegación
     */
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

    /**
     * Recarga la pantalla de ajustes al pulsar el botón correspondiente.
     *
     * @param event el evento de acción que dispara la navegación
     */
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

    /**
     * Carga los datos del usuario actual y su comercio asociado, mostrando los productos si es propietario.
     */
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

    /**
     * Recupera y muestra los productos del comercio del usuario en la lista.
     * Muestra un error si ocurre algún problema durante la carga.
     */
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


    /**
     * Guarda los cambios realizados en los datos del usuario actual en la base de datos.
     *
     * @param ev el evento de acción que dispara el guardado
     */
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

    /**
     * Permite al usuario solicitar ser propietario de un comercio si no tiene una solicitud pendiente.
     * Muestra información si ya existe una solicitud registrada.
     *
     * @param event el evento de acción que dispara la solicitud
     */
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

    /**
     * Inicia el proceso para agregar un nuevo producto mostrando el editor correspondiente.
     *
     * @param ev el evento de acción que dispara la adición
     */
    @FXML
    private void onAddProducto(ActionEvent ev) {
        productoEnEdicion = null;
        abrirEditorNuevo();
    }

    /**
     * Carga los datos del producto seleccionado en el editor para su modificación.
     *
     * @param ev el evento de acción que dispara la edición (puede ser null si se llama desde el listener)
     */
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

    /**
     * Elimina el producto seleccionado de la base de datos y actualiza la lista de productos.
     *
     * @param ev el evento de acción que dispara la eliminación
     */
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

    /**
     * Muestra el panel de edición de producto para crear o modificar un producto.
     */
    private void abrirEditorNuevo() {
        paneEditorProducto.setVisible(true);
        paneEditorProducto.setManaged(true);
    }

    /**
     * Permite seleccionar una imagen desde el sistema de archivos y la asigna al producto en edición.
     *
     * @param ev el evento de acción que dispara el cambio de imagen
     */
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

    /**
     * Guarda o actualiza el producto en la base de datos con los datos ingresados en el editor.
     * Valida los campos obligatorios antes de guardar.
     *
     * @param ev el evento de acción que dispara el guardado
     */
    @FXML
    private void onGuardarProducto(ActionEvent ev) {
        if (tfProdNombre.getText().isBlank()) {
            mostrarError("Validación", "El nombre es obligatorio.", "");
            return;
        }
        if (Sesion.comercio == null) {
            mostrarError("Validación", "No se ha encontrado el comercio asociado.", "No es posible guardar el producto sin un comercio.");
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

    /**
     * Cancela la edición del producto, limpia los campos y oculta el panel de edición.
     *
     * @param ev el evento de acción que dispara la cancelación
     */
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

    /**
     * Navega a la pantalla de administración al pulsar el botón correspondiente.
     *
     * @param event el evento de acción que dispara la navegación
     */
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
