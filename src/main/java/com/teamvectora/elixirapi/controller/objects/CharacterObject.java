package com.teamvectora.elixirapi.controller.objects;
import com.teamvectora.elixirapi.controller.MyCharactersController;
import com.teamvectora.elixirapi.manager.ObjectSaveManager;
import com.teamvectora.elixirapi.manager.PaneManager;
import com.teamvectora.elixirapi.model.Attribute;
import com.teamvectora.elixirapi.model.CharacterMaster;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.*;
import javafx.scene.image.*;

import static javafx.scene.layout.VBox.setVgrow;

public class CharacterObject extends HBox {

    public CharacterObject(CharacterMaster character) {
        // Crie um ImageView com a imagem desejada
        ImageView imageView = new ImageView(new Image("/media/emptyImage.png"));
        imageView.setFitHeight(108.0);
        imageView.setFitWidth(118.0);

        // Crie um VBox para os textos
        VBox textVBox = new VBox(5);
        textVBox.setPrefHeight(200.0);
        textVBox.setPrefWidth(100.0);
        textVBox.setSpacing(5.0);

        // Adicione os textos
        Text nomeText = createText(character.getName());
        Text classeText = createText(MyCharactersController.getClassId(character.getClassId()));
        Text racaText = createText(MyCharactersController.getRaceId(character.getRaceId()));

        // Adicione os textos ao VBox
        textVBox.getChildren().addAll(nomeText, classeText, racaText);

        VBox.setMargin(this, new Insets(10, 0, 0, 0));

        // Adicione o ImageView e o VBox ao HBox
        getChildren().addAll(imageView, textVBox);

        // Configure o espaçamento, alinhamento e crescimento do HBox
        setSpacing(5.0);
        setAlignment(Pos.TOP_LEFT);
        setHgrow(this, Priority.ALWAYS);
        setVgrow(this, Priority.ALWAYS);

        // Configure o preenchimento (padding) do VBox
        VBox.setMargin(textVBox, new Insets(20.0, 0, 20.0, 0));

        setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                System.out.println("clicou");
                ObjectSaveManager saver = new ObjectSaveManager();
                saver.saveObject("character", character);

                PaneManager manager = new PaneManager();
                manager.openPane("viewCharacterPage1");
            } else if (mouseEvent.getButton() == MouseButton.SECONDARY){

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("DELETAR PERSONAGEM");
                alert.setContentText("Você tem certeza que deseja deletar o personagem: " + character.getName() + "\n" +
                        "Essa ação não pode ser desfeita");

                alert.showAndWait().ifPresent(response -> {
                    if (response.getText().equals("OK")){
                        alert.setHeaderText("Nao foi possível deletar por que não");
                        alert.setContentText("Nao");
                        alert.showAndWait();
                    }
                });
            }
        });
    }

    private Text createText(String text) {
        Text labelText = new Text(text);
        labelText.setFill(Color.web("#4b5c6b"));
        labelText.setStrokeType(StrokeType.OUTSIDE);
        labelText.setStrokeWidth(0.0);
        labelText.setFont(Font.font("System Bold", FontWeight.BOLD, 16.0));
        return labelText;
    }
}