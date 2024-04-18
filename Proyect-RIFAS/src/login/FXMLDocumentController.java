/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXML2.java to edit this template
 */
package login;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 *
 * @author sgtom
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private Button button;
    @FXML
    private Label label;
    @FXML
    private TextField tf_contra;
    @FXML
    private TextField tf_user;
    @FXML
    private StackPane contenedorPadre;
    @FXML
    private AnchorPane raiz;

    @FXML
    private void link(ActionEvent event) { //Cambiar pantalla
        if ("Josue".equals(tf_user.getText()) && "12345".equals(tf_contra.getText())) {
            Parent root;
            try {
                root = FXMLLoader.load(getClass().getResource("MENU.fxml"));
                Scene scene = button.getScene();
                root.translateYProperty().set(scene.getHeight());
                contenedorPadre.getChildren().add(root);
                Timeline timeline = new Timeline();
                KeyValue kv = new KeyValue(root.translateYProperty(), 0, Interpolator.EASE_IN);
                KeyFrame kf = new KeyFrame(Duration.seconds(0.001), kv);
                timeline.getKeyFrames().add(kf);
                timeline.setOnFinished(t -> {
                    contenedorPadre.getChildren().remove(raiz);
                });
                timeline.play();
            } catch (IOException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

}
