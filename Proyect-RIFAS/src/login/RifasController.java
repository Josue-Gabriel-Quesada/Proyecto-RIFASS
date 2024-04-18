package login;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RifasController implements Initializable {

    @FXML
    private Button btn_rifa;

    @FXML
    private TextField tf_addRifa;

    @FXML
    private TextField tf_num;

    @FXML
    private DatePicker FECHA;

    @FXML
    private TextField PREMIO;

    @FXML
    private TextField PAGO;  // Nuevo campo para método de pago

    @FXML
    private void agregarRifas(ActionEvent event) {
        String nombreRifa = tf_addRifa.getText().trim();
        String cantidadNumerosStr = tf_num.getText().trim();
        String premio = PREMIO.getText().trim();
        String metodoPago = PAGO.getText().trim().toLowerCase(); // Obtener el texto del campo PAGO en minúsculas

        if (nombreRifa.isEmpty() || cantidadNumerosStr.isEmpty() || premio.isEmpty() || metodoPago.isEmpty()) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.");
            return;
        }

        // Verificar si el método de pago es válido
        if (!metodoPago.equals("simpe") && !metodoPago.equals("efectivo")) {
            mostrarAlerta("Error", "El método de pago debe ser 'simpe' o 'efectivo'.");
            return;
        }

        try {
            int cantidadNumeros = Integer.parseInt(cantidadNumerosStr);

            if (cantidadNumeros > 100) {
                mostrarAlerta("Advertencia", "La cantidad de números debe ser menor o igual a 100.");
                return;
            }

            LocalDate fechaSeleccionada = FECHA.getValue();
            java.sql.Date fechaSQL = (fechaSeleccionada != null) ? java.sql.Date.valueOf(fechaSeleccionada) : null;

            try (Connection conn = DatabaseConnector.getConnection();
                 CallableStatement agregarRifa = conn.prepareCall("{call Insertar_Rifa(?, ?, ?, ?, ?)}")) {

                agregarRifa.setString(1, nombreRifa);
                agregarRifa.setInt(2, cantidadNumeros);
                agregarRifa.setDate(3, fechaSQL);
                agregarRifa.setString(4, premio);
                agregarRifa.setString(5, metodoPago); // Pasar el método de pago como quinto parámetro

                agregarRifa.execute();

                // Limpiar los campos después de agregar la rifa
                tf_addRifa.clear();
                tf_num.clear();
                FECHA.getEditor().clear();
                PREMIO.clear();
                PAGO.clear(); // Limpiar campo de método de pago

                mostrarAlerta("Éxito", "Rifa agregada correctamente.");

            } catch (SQLException e) {
                e.printStackTrace();
                mostrarAlerta("Error", "No se pudo crear la rifa. Error en la base de datos.");
            }

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "La cantidad de números debe ser un valor numérico válido.");
        }
    }

    @FXML
    private void irAMenu(ActionEvent event) {
        try {
            // Cargar MENU.fxml desde el mismo paquete (login)
            Parent root = FXMLLoader.load(getClass().getResource("MENU.fxml"));

            // Obtener el escenario actual
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();

            // Crear una nueva escena con la interfaz MENU.fxml
            Scene scene = new Scene(root);

            // Establecer la nueva escena en el escenario
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Manejar excepción de carga de la interfaz MENU.fxml
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Método de inicialización, puedes dejarlo vacío si no necesitas inicializar algo específico aquí
    }
}
