package login;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class EliminarController implements Initializable {

    @FXML
    private MenuButton men_rifaaa;

    private Connection conn;
    private int idRifaSeleccionada; // Almacena el ID de la rifa seleccionada

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            conn = DatabaseConnector.getConnection(); // Obtener conexión a la base de datos
            cargarRifas();
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error de conexión", e.getMessage());
        }
    }

    private void cargarRifas() {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT ID_RIFA, NOMBRE FROM RIFAS");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                MenuItem menuItem = new MenuItem(rs.getString("NOMBRE"));
                int idRifa = rs.getInt("ID_RIFA");
                menuItem.setOnAction(e -> {
                    idRifaSeleccionada = idRifa; // Almacenar el ID de la rifa seleccionada
                });
                men_rifaaa.getItems().add(menuItem);
            }

            stmt.close();
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar las rifas", e.getMessage());
        }
    }

    @FXML
    private void eliminarRifa(ActionEvent event) {
        try {
            if (idRifaSeleccionada == 0) {
                mostrarAlerta("Advertencia", "Por favor selecciona una rifa", "");
                return;
            }

            // Iniciar la transacción explícitamente
            conn.setAutoCommit(false);

            // Eliminar registros relacionados con la rifa seleccionada
            try (PreparedStatement stmtEliminarNum = conn.prepareStatement("DELETE FROM NUMEROS_RIFA WHERE ID_RIFA = ?")) {
                stmtEliminarNum.setInt(1, idRifaSeleccionada);
                stmtEliminarNum.executeUpdate();
            }

            try (PreparedStatement stmtEliminarPers = conn.prepareStatement("DELETE FROM PERSONAS WHERE ID_PERSONA IN (SELECT ID_PERSONA_COMPRA FROM NUMEROS_RIFA WHERE ID_RIFA = ?)")) {
                stmtEliminarPers.setInt(1, idRifaSeleccionada);
                stmtEliminarPers.executeUpdate();
            }

            try (PreparedStatement stmtEliminarRifa = conn.prepareStatement("DELETE FROM RIFAS WHERE ID_RIFA = ?")) {
                stmtEliminarRifa.setInt(1, idRifaSeleccionada);
                stmtEliminarRifa.executeUpdate();
            }

            // Confirmar la transacción
            conn.commit();

            // Mostrar mensaje de éxito
            mostrarAlerta("Eliminación exitosa", "La rifa ha sido eliminada correctamente", "");
        } catch (SQLException e) {
            // Deshacer la transacción en caso de error
            try {
                conn.rollback();
            } catch (SQLException ex) {
                mostrarAlerta("Error", "Error al deshacer la transacción", ex.getMessage());
            }
            mostrarAlerta("Error", "Error al eliminar la rifa", e.getMessage());
        } finally {
            // Restaurar el modo de auto-commit
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                mostrarAlerta("Error", "Error al restaurar el modo de auto-commit", ex.getMessage());
            }
        }
    }

    @FXML
    private void irAMenu(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("MENU.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) men_rifaaa.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            mostrarAlerta("Error", "Error al cargar la interfaz MENU.fxml", e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, String detalles) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(mensaje);
        alert.setContentText(detalles);
        alert.showAndWait();
    }
}
