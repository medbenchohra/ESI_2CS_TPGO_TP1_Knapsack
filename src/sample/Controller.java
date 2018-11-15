package sample;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXScrollPane;
import com.jfoenix.controls.JFXTextField;
import com.sun.scenario.effect.ImageData;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class Controller {

    static int objectsNumber = 0;

    @FXML
    private JFXButton button_insert;

    @FXML
    private VBox list_objects;

    @FXML
    private ScrollPane scrollable_list;

    @FXML
    private JFXTextField capacite;

    @FXML
    private JFXTextField resultat_gain_max;

    @FXML
    private JFXTextField resultat_poids_max;

    @FXML
    private JFXTextField resultat_poids_wasted;


    @FXML
    private ImageView button_return;

    Stage stage;
    Scene scene;


    @FXML
    public void addObject() {
        ++objectsNumber;

        HBox line = new HBox();
        line.setId("object_" + objectsNumber);
        line.setAlignment(Pos.CENTER_LEFT);
        line.setPadding(new Insets(5,5,5,5));

        int a = 20;
        Label labelWeight = new Label(" Object N° " + objectsNumber + ",    Weight : ");
        labelWeight.setFont(new Font("Amaranth", a));
        labelWeight.setStyle("-fx-font-weight: bold ; -fx-font-family: Amaranth ;");

        Label labelGain = new Label(" Kg,    Gain : ");
        labelGain.setFont(new Font("Amaranth", a));
        labelGain.setStyle("-fx-font-weight: bold ; -fx-font-family: Amaranth ;");

        JFXTextField weight = new JFXTextField();
        weight.setMaxWidth(80);
        weight.setStyle("-fx-font-weight: bold ; -fx-font-family: Amaranth ; -fx-font-size: 20 ;");
        weight.setId("weight_" + objectsNumber);
        weight.setAlignment(Pos.CENTER);

        JFXTextField gain = new JFXTextField();
        gain.setMaxWidth(80);
        gain.setStyle("-fx-font-weight: bold ; -fx-font-family: Amaranth ; -fx-font-size: 20 ;");
        gain.setId("gain_" + objectsNumber);
        gain.setAlignment(Pos.CENTER);


        line.getChildren().addAll(labelWeight, weight, labelGain, gain);
        list_objects.getChildren().add(line);
        scrollable_list.setContent(list_objects);
        scrollable_list.setPannable(true);
        stage = new Stage();
        scene = new Scene(scrollable_list.getParent());
        stage.setScene(scene);
        stage.show();

    }

    public void calculer() {

        // Maximum weight supported by the knapsack
        int w = Integer.valueOf(capacite.getText().trim());

        Thing[] things = new Thing[objectsNumber + 1];
        things[0] = null;  // Putting first case to null (for readability)

        int tmpWeight = 0, tmpGain = 0;
        String tmpWeightString, tmpGainString;
        Scene scene = button_insert.getScene();
        for (int i = 1; i <= objectsNumber; i++) {
            tmpWeightString = ((JFXTextField) scene.lookup("#weight_" + i)).getText().trim();
            tmpWeight = Integer.valueOf(tmpWeightString);
            tmpGainString = ((JFXTextField) scene.lookup("#gain_" + i)).getText().trim();
            tmpGain = Integer.valueOf(tmpGainString);
            things[i] = new Thing(tmpWeight, tmpGain);
        }


        int n = objectsNumber; // Number of things to choose from

        LinkedList<Integer> chosenThings = new LinkedList<>();
        int p[][] = new int[n+1][w+1];

        int gainWith, gainWithout;

        // Calculating the maximum possible gain
        for (int j = 1; j <= w; j++) {
            for (int i = 1; i <= n; i++) {
                if(i*j == 0) p[i][j] = 0; // Initializing first column & line with 0
                else if(j < things[i].weight) p[i][j] = p[i-1][j];
                else {
                    gainWithout = p[i-1][j];
                    gainWith = p[i-1][j- things[i].weight] + things[i].value;

                    if(gainWithout < gainWith) {
                        p[i][j] = gainWith;
                    }else {
                        p[i][j] = gainWithout;
                    }
                }
            }
        }

        // Extracting the corresponding series of things (chosen)
        int col = w;
        for (int i = n; i > 0; i--) {
            if(p[i][col] != p[i-1][col]) {
                chosenThings.addFirst(i);
                col -= things[i].weight;
            }
        }

        // Finding the maximum weight to be carried in the knapsack
        int maxWeight = 0;
        for (int i = w; i > 0; i--) {
            if(p[n][i-1] != p[n][i]) {
                maxWeight = i;
                break;
            }
        }

        int wastedWeight = w - maxWeight;

        resultat_gain_max.setText("" + p[n][w]);
        resultat_poids_max.setText("" + maxWeight);
        resultat_poids_wasted.setText("" + wastedWeight);


        // Coloring chosen and non-chosen objects
        scene = button_insert.getScene();
        for (int i = 1; i <= objectsNumber; i++) {
            if(chosenThings.contains(i)) {
                scene.lookup("#object_" + i).setStyle("-fx-background-color: green");
            }else{
                scene.lookup("#object_" + i).setStyle("-fx-background-color: red");
            }
        }


        // Clearing (restarting)
        button_return.setOnMouseClicked(event -> {
            capacite.setText("");
            resultat_poids_max.setText("");
            resultat_gain_max.setText("");
            resultat_poids_wasted.setText("");
            list_objects.getChildren().clear();
            objectsNumber = 0;
        });
    }

    class Thing {
        int weight;
        int value;

        Thing(int weight, int value) {
            this.weight = weight;
            this.value = value;
        }
    }
}
