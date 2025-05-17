package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.categoriaTipoProducto;
import model.comercio;
import model.producto;
import org.hibernate.Session;
import utilities.LogAdministrador;
import utilities.Paths;

import javax.management.Query;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static controllers.NavegacionController.mostrarError;
import static database.Sesion.newSession;

public class TIEN_PERFController implements Initializable {

    @FXML
    private TextField barraBusqueda;

    @FXML
    private ComboBox<String> filtroComboBox;

    @FXML
    private ScrollPane scrollPaneProductos;

    @FXML
    private FlowPane productosContainer;

    @FXML
    private ImageView bannerTienda;

    @FXML
    private ImageView imagenProducto;

    @FXML
    private Text nombreProducto;

    @FXML
    private Text tipoProducto;

    @FXML
    private Text categoriaProducto;

    @FXML
    private Text nombreTienda;

    @FXML
    private Text direccionTienda;

    private final int limiteResultados = 1000;

    private comercio comercioActual;

    Session session = newSession();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LogAdministrador.escribirLogInfo("Inicializando pantalla de perfil de tienda.");

        filtroComboBox.setOnAction(event -> buscar());
        barraBusqueda.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) {
                buscar();
            }
        });

        cargarProductos();
    }

    public void btnInicioAction(ActionEvent event) {
        navegar(event, Paths.PRI_INI);
    }

    public void btnTiendasAction(ActionEvent event) {
        navegar(event, Paths.PRI_TIEN);
    }

    public void btnFavoritosAction(ActionEvent event) {
        navegar(event, Paths.PRI_FAV);
    }

    public void btnAjustesAction(ActionEvent event) {
        navegar(event, Paths.PRI_INI);
    }

    @FXML
    public void btnBuscarAction(ActionEvent event) {
        buscar();
    }

    public void setComercio(comercio comercio) {
        this.comercioActual = comercio;

        // Configurar el nombre y la dirección de la tienda
        nombreTienda.setText(comercio.getNombre());
        direccionTienda.setText(comercio.getDireccion());

        // Configurar el banner de la tienda
        if (bannerTienda != null) {
            if (comercio.getImagen() != null && comercio.getImagen().length > 0) {
                bannerTienda.setImage(new Image(new ByteArrayInputStream(comercio.getImagen())));
            } else {
                bannerTienda.setImage(new Image(getClass().getResourceAsStream("/images/Fondo_carritos.png")));
            }
        } else {
            LogAdministrador.escribirLogError("El componente bannerTienda no está inicializado.");
        }

        // Cargar los productos de la tienda seleccionada
        cargarProductos();
    }

    private void cargarProductos() {
        // Simulación de consulta de productos
        List<producto> listaProductos = obtenerProductosDeTienda();

        productosContainer.getChildren().clear();

        for (producto producto : listaProductos) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/TarjetaProducto.fxml"));
                Node tarjetaProducto = loader.load();

                TarjetaProductoController controller = loader.getController();
                controller.setProducto(producto);

                tarjetaProducto.setOnMouseClicked(event -> mostrarProducto(producto));

                productosContainer.getChildren().add(tarjetaProducto);
            } catch (IOException e) {
                LogAdministrador.escribirLogError("Error al cargar la tarjeta del producto: " + e.getMessage());
                mostrarError("Error al cargar producto", "No se pudo cargar la tarjeta del producto.", e.getMessage());
            }
        }
    }

    private void buscar() {
        String filtroSeleccionado = filtroComboBox.getValue();
        String criterioBusqueda = barraBusqueda.getText();

        List<producto> listaProductos = obtenerProductosDeTienda();
        List<producto> productosFiltrados = new ArrayList<>();

        for (producto producto : listaProductos) {
            if (producto.getNombre().toLowerCase().contains(criterioBusqueda.toLowerCase())) {
                productosFiltrados.add(producto);
            }
        }

        if (filtroSeleccionado != null) {
            switch (filtroSeleccionado) {
                case "Precio Ascendente":
                    productosFiltrados.sort((p1, p2) -> Double.compare(p1.getPrecio(), p2.getPrecio()));
                    break;
                case "Precio Descendente":
                    productosFiltrados.sort((p1, p2) -> Double.compare(p2.getPrecio(), p1.getPrecio()));
                    break;
                case "Nombre":
                    productosFiltrados.sort((p1, p2) -> p1.getNombre().compareToIgnoreCase(p2.getNombre()));
                    break;
            }
        }

        cargarProductosFiltrados(productosFiltrados);
    }

    private void cargarProductosFiltrados(List<producto> productosFiltrados) {
        productosContainer.getChildren().clear();

        for (producto producto : productosFiltrados) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/TarjetaProducto.fxml"));
                Node tarjetaProducto = loader.load();

                TarjetaProductoController controller = loader.getController();
                controller.setProducto(producto);

                tarjetaProducto.setOnMouseClicked(event -> mostrarProducto(producto));

                productosContainer.getChildren().add(tarjetaProducto);
            } catch (IOException e) {
                LogAdministrador.escribirLogError("Error al cargar la tarjeta del producto: " + e.getMessage());
                mostrarError("Error al cargar producto", "No se pudo cargar la tarjeta del producto.", e.getMessage());
            }
        }
    }

    private void mostrarProducto(producto producto) {
        if (producto.getImagen() != null && producto.getImagen().length > 0) {
            imagenProducto.setImage(new Image(new ByteArrayInputStream(producto.getImagen())));
        } else {
            imagenProducto.setImage(new Image(getClass().getResourceAsStream("/iconos/productoSinImagen.png")));
        }
        nombreProducto.setText(producto.getNombre());
        tipoProducto.setText(producto.getTipo().getNombre());
        categoriaProducto.setText(producto.getTipo().getCategoria().getNombre());
    }

    private List<producto> obtenerProductosDeTienda() {
        if (comercioActual == null) {
            return new ArrayList<>();
        }

        return newSession().createQuery("FROM producto WHERE comercio.id = :idTienda", producto.class)
                .setParameter("idTienda", comercioActual.getIdComercio())
                .setMaxResults(limiteResultados)
                .getResultList();
    }

    private void navegar(ActionEvent event, String path) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            NavegacionController.navegar(stage, path);
        } catch (IOException e) {
            LogAdministrador.escribirLogError("Error al navegar: " + e.getMessage());
            mostrarError("Error de navegación", "No se pudo navegar a la pantalla solicitada.", e.getMessage());
        }
    }
}