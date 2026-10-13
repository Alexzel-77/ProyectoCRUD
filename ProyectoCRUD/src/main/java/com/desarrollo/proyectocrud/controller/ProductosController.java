package com.desarrollo.proyectocrud.controller;

import com.desarrollo.proyectocrud.database.ConexionBD;
import com.desarrollo.proyectocrud.model.Categoria;
import com.desarrollo.proyectocrud.model.Marca;
import com.desarrollo.proyectocrud.model.Producto;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProductosController {

    @FXML
    private TextField txtCodigo;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtPrecio;

    @FXML
    private TextField txtStock;

    @FXML
    private TextField txtDescripcion;

    @FXML
    private ComboBox<Categoria> cmbCategoria;

    @FXML
    private ComboBox<Marca> cmbMarca;

    @FXML
    private DatePicker dpFecha;

    @FXML
    private TextField txtBuscar;

    @FXML
    private ComboBox<Categoria> cmbFiltroCategoria;

    @FXML
    private DatePicker dpDesde;

    @FXML
    private DatePicker dpHasta;

    @FXML
    private TextField txtPrecioMin;

    @FXML
    private TextField txtPrecioMax;

    @FXML
    private CheckBox chkConStock;

    @FXML
    private ComboBox<String> cmbOrdenar;

    @FXML
    private TableView<Producto> tablaProductos;

    @FXML
    private TableColumn<Producto, String> colCodigo;

    @FXML
    private TableColumn<Producto, String> colNombre;

    @FXML
    private TableColumn<Producto, String> colCategoria;

    @FXML
    private TableColumn<Producto, String> colMarca;

    @FXML
    private TableColumn<Producto, Double> colPrecio;

    @FXML
    private TableColumn<Producto, Integer> colStock;

    @FXML
    private TableColumn<Producto, LocalDate> colFecha;

    @FXML
    public void initialize() {
        configurarColumnas();
        cargarCategorias();
        cargarMarcas();
        cargarProductos();

        cmbOrdenar.setItems(FXCollections.observableArrayList(
                "Nombre (A-Z)", "Precio (Menor a Mayor)", "Precio (Mayor a Menor)", "Stock"
        ));

        tablaProductos.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, prod) -> {
            if (prod != null) {
                txtCodigo.setText(prod.getCodigo());
                txtNombre.setText(prod.getNombre());
                txtDescripcion.setText(prod.getDescripcion());
                txtPrecio.setText(String.valueOf(prod.getPrecio()));
                txtStock.setText(String.valueOf(prod.getStock()));
                dpFecha.setValue(prod.getFechaCreacion());

                for (Categoria c : cmbCategoria.getItems()) {
                    if (c.getIdCategoria() == prod.getIdCategoria()) {
                        cmbCategoria.setValue(c);
                        break;
                    }
                }
                for (Marca m : cmbMarca.getItems()) {
                    if (m.getIdMarca() == prod.getIdMarca()) {
                        cmbMarca.setValue(m);
                        break;
                    }
                }
            }
        });
    }

    private void configurarColumnas() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaCreacion"));
    }

    private void cargarCategorias() {
        ObservableList<Categoria> listaCategorias = FXCollections.observableArrayList();
        String sql = "SELECT id_categoria, nombre, descripcion, estado FROM catalogos.categorias WHERE estado = 1 ORDER BY nombre ASC";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Categoria cat = new Categoria(
                        rs.getInt("id_categoria"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getInt("estado")
                );
                listaCategorias.add(cat);
            }
            cmbCategoria.setItems(listaCategorias);
            cmbFiltroCategoria.setItems(listaCategorias);

        } catch (SQLException e) {
            System.out.println("Error al cargar categorías: " + e.getMessage());
        }
    }

    private void cargarMarcas() {
        ObservableList<Marca> listaMarcas = FXCollections.observableArrayList();
        String sql = "SELECT * FROM catalogos.marcas WHERE estado = 1 ORDER BY nombre ASC";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Marca marca = new Marca(
                        rs.getInt("id_marcas"),
                        rs.getString("nombre"),
                        rs.getString(3),
                        rs.getInt("estado")
                );
                listaMarcas.add(marca);
            }
            cmbMarca.setItems(listaMarcas);

        } catch (SQLException e) {
            System.out.println("Error al cargar marcas: " + e.getMessage());
        }
    }

    private void cargarProductos() {
        ObservableList<Producto> listaProductos = FXCollections.observableArrayList();
        String sql = "SELECT p.id_producto, p.codigo, p.descripcion, p.nombre, p.precio, p.stock, " +
                "p.fecha_creacion, p.id_categoria, c.nombre AS categoria, " +
                "p.id_marca, m.nombre AS marca, p.estado " +
                "FROM catalogos.productos p " +
                "INNER JOIN catalogos.categorias c ON p.id_categoria = c.id_categoria " +
                "INNER JOIN catalogos.marcas m ON p.id_marca = m.id_marcas " +
                "WHERE p.estado = 1 " +
                "ORDER BY p.id_producto ASC";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Date fechaSql = rs.getDate("fecha_creacion");
                LocalDate fecha = (fechaSql != null) ? fechaSql.toLocalDate() : null;

                Producto prod = new Producto(
                        rs.getInt("id_producto"),
                        rs.getString("codigo"),
                        rs.getString("descripcion"),
                        rs.getString("nombre"),
                        rs.getDouble("precio"),
                        rs.getInt("stock"),
                        fecha,
                        rs.getInt("id_categoria"),
                        rs.getString("categoria"),
                        rs.getInt("id_marca"),
                        rs.getString("marca"),
                        rs.getInt("estado")
                );
                listaProductos.add(prod);
            }
            tablaProductos.setItems(listaProductos);

        } catch (SQLException e) {
            System.out.println("Error al cargar productos: " + e.getMessage());
        }
    }

    @FXML
    private void nuevoProducto() {
        txtCodigo.clear();
        txtNombre.clear();
        txtPrecio.clear();
        txtStock.clear();
        txtDescripcion.clear();
        cmbCategoria.setValue(null);
        cmbMarca.setValue(null);
        dpFecha.setValue(null);
        tablaProductos.getSelectionModel().clearSelection();
    }

    @FXML
    private void guardarProducto() {
        if (cmbCategoria.getValue() == null || cmbMarca.getValue() == null) {
            mostrarAlerta("Seleccione una categoría y una marca.");
            return;
        }

        String sql = "INSERT INTO catalogos.productos (codigo, nombre, descripcion, precio, stock, fecha_creacion, id_categoria, id_marca, estado) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 1)";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, txtCodigo.getText().trim());
            ps.setString(2, txtNombre.getText().trim());
            ps.setString(3, txtDescripcion.getText().trim());
            ps.setDouble(4, Double.parseDouble(txtPrecio.getText().trim()));
            ps.setInt(5, Integer.parseInt(txtStock.getText().trim()));
            LocalDate fecha = (dpFecha.getValue() != null) ? dpFecha.getValue() : LocalDate.now();
            ps.setDate(6, Date.valueOf(fecha));
            ps.setInt(7, cmbCategoria.getValue().getIdCategoria());
            ps.setInt(8, cmbMarca.getValue().getIdMarca());

            ps.executeUpdate();
            cargarProductos();
            nuevoProducto();
            mostrarAlerta("Producto guardado correctamente.");

        } catch (Exception e) {
            mostrarAlerta("Error al guardar: " + e.getMessage());
        }
    }

    @FXML
    private void actualizarProducto() {
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Seleccione un producto de la tabla para actualizar.");
            return;
        }

        String sql = "UPDATE catalogos.productos SET codigo = ?, nombre = ?, descripcion = ?, " +
                "precio = ?, stock = ?, fecha_creacion = ?, id_categoria = ?, id_marca = ? " +
                "WHERE id_producto = ?";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, txtCodigo.getText().trim());
            ps.setString(2, txtNombre.getText().trim());
            ps.setString(3, txtDescripcion.getText().trim());
            ps.setDouble(4, Double.parseDouble(txtPrecio.getText().trim()));
            ps.setInt(5, Integer.parseInt(txtStock.getText().trim()));
            LocalDate fecha = (dpFecha.getValue() != null) ? dpFecha.getValue() : LocalDate.now();
            ps.setDate(6, Date.valueOf(fecha));
            ps.setInt(7, cmbCategoria.getValue().getIdCategoria());
            ps.setInt(8, cmbMarca.getValue().getIdMarca());
            ps.setInt(9, seleccionado.getIdProducto());

            ps.executeUpdate();
            cargarProductos();
            nuevoProducto();
            mostrarAlerta("Producto actualizado correctamente.");

        } catch (Exception e) {
            mostrarAlerta("Error al actualizar: " + e.getMessage());
        }
    }

    @FXML
    private void eliminarProducto() {
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Seleccione un producto de la tabla para eliminar.");
            return;
        }

        String sql = "DELETE FROM catalogos.productos WHERE id_producto = ?";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, seleccionado.getIdProducto());
            ps.executeUpdate();
            cargarProductos();
            nuevoProducto();
            mostrarAlerta("Producto eliminado correctamente.");

        } catch (SQLException e) {
            mostrarAlerta("Error al eliminar: " + e.getMessage());
        }
    }

    @FXML
    private void buscarProductos() {
        ObservableList<Producto> listaFiltrada = FXCollections.observableArrayList();

        StringBuilder sql = new StringBuilder(
                "SELECT p.id_producto, p.codigo, p.descripcion, p.nombre, p.precio, p.stock, " +
                        "p.fecha_creacion, p.id_categoria, c.nombre AS categoria, " +
                        "p.id_marca, m.nombre AS marca, p.estado " +
                        "FROM catalogos.productos p " +
                        "INNER JOIN catalogos.categorias c ON p.id_categoria = c.id_categoria " +
                        "INNER JOIN catalogos.marcas m ON p.id_marca = m.id_marcas " +
                        "WHERE p.estado = 1"
        );

        List<Object> parametros = new ArrayList<>();

        // 1. Filtro por Código o Nombre
        String texto = (txtBuscar.getText() != null) ? txtBuscar.getText().trim().toLowerCase() : "";
        if (!texto.isEmpty()) {
            sql.append(" AND (LOWER(p.nombre) LIKE ? OR LOWER(p.codigo) LIKE ?)");
            parametros.add("%" + texto + "%");
            parametros.add("%" + texto + "%");
        }

        // 2. Filtro por Categoría
        if (cmbFiltroCategoria.getValue() != null) {
            sql.append(" AND p.id_categoria = ?");
            parametros.add(cmbFiltroCategoria.getValue().getIdCategoria());
        }

        // 3. Filtro por Fecha Desde
        if (dpDesde.getValue() != null) {
            sql.append(" AND p.fecha_creacion >= ?");
            parametros.add(Date.valueOf(dpDesde.getValue()));
        }

        // 4. Filtro por Fecha Hasta
        if (dpHasta.getValue() != null) {
            sql.append(" AND p.fecha_creacion <= ?");
            parametros.add(Date.valueOf(dpHasta.getValue()));
        }

        // 5. Filtro por Precio Mínimo
        if (txtPrecioMin.getText() != null && !txtPrecioMin.getText().trim().isEmpty()) {
            try {
                double precioMin = Double.parseDouble(txtPrecioMin.getText().trim());
                sql.append(" AND p.precio >= ?");
                parametros.add(precioMin);
            } catch (NumberFormatException e) {
                mostrarAlerta("El precio mínimo debe ser un número válido.");
                return;
            }
        }

        // 6. Filtro por Precio Máximo
        if (txtPrecioMax.getText() != null && !txtPrecioMax.getText().trim().isEmpty()) {
            try {
                double precioMax = Double.parseDouble(txtPrecioMax.getText().trim());
                sql.append(" AND p.precio <= ?");
                parametros.add(precioMax);
            } catch (NumberFormatException e) {
                mostrarAlerta("El precio máximo debe ser un número válido.");
                return;
            }
        }

        // 7. Filtro de Solo productos con existencia (Stock > 0)
        if (chkConStock.isSelected()) {
            sql.append(" AND p.stock > 0");
        }

        // 8. Ordenar por
        String orden = cmbOrdenar.getValue();
        if (orden != null) {
            switch (orden) {
                case "Nombre (A-Z)":
                    sql.append(" ORDER BY p.nombre ASC");
                    break;
                case "Precio (Menor a Mayor)":
                    sql.append(" ORDER BY p.precio ASC");
                    break;
                case "Precio (Mayor a Menor)":
                    sql.append(" ORDER BY p.precio DESC");
                    break;
                case "Stock":
                    sql.append(" ORDER BY p.stock DESC");
                    break;
                default:
                    sql.append(" ORDER BY p.id_producto ASC");
                    break;
            }
        } else {
            sql.append(" ORDER BY p.id_producto ASC");
        }

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < parametros.size(); i++) {
                ps.setObject(i + 1, parametros.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Date fechaSql = rs.getDate("fecha_creacion");
                    LocalDate fecha = (fechaSql != null) ? fechaSql.toLocalDate() : null;

                    Producto prod = new Producto(
                            rs.getInt("id_producto"),
                            rs.getString("codigo"),
                            rs.getString("descripcion"),
                            rs.getString("nombre"),
                            rs.getDouble("precio"),
                            rs.getInt("stock"),
                            fecha,
                            rs.getInt("id_categoria"),
                            rs.getString("categoria"),
                            rs.getInt("id_marca"),
                            rs.getString("marca"),
                            rs.getInt("estado")
                    );
                    listaFiltrada.add(prod);
                }
            }
            tablaProductos.setItems(listaFiltrada);

        } catch (SQLException e) {
            System.out.println("Error al buscar: " + e.getMessage());
        }
    }

    @FXML
    private void limpiarFiltros() {
        txtBuscar.clear();
        cmbFiltroCategoria.setValue(null);
        dpDesde.setValue(null);
        dpHasta.setValue(null);
        txtPrecioMin.clear();
        txtPrecioMax.clear();
        chkConStock.setSelected(false);
        cmbOrdenar.setValue(null);
        cargarProductos();
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}