package com.elixir.controller;

import com.elixir.controller.abstractControllers.CreateCharacterSectionController;
import com.elixir.manager.ObjectSaveManager;
import com.elixir.manager.PaneManager;
import com.elixir.model.Attribute;
import com.elixir.model.Character;
import com.elixir.dao.AttributeDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.util.Map;

import java.sql.SQLException;
import java.util.logging.Filter;


public class CreateCharacterRaceController extends CreateCharacterSectionController {
    @FXML
    private Label chosenRaceLabel;
    @FXML
    private TextField errorLabel;
    @FXML
    private TextField conField;
    private Character character;
    private Attribute attribute;


    @FXML
    private void initialize(){
        ObjectSaveManager reader = new ObjectSaveManager();
        character = (Character) reader.getObject("character");

        try{
            if(character.getRaceId() > 0){
                switch (character.getRaceId()){
                    case 1:
                        chosenRaceLabel.setText("Humano");
                        break;
                    case 2:
                        chosenRaceLabel.setText("Elfo");
                        break;
                    case 3:
                        chosenRaceLabel.setText("Anão");
                        break;
                    case 4:
                        chosenRaceLabel.setText("Halfing");
                        break;
                    default:
                        chosenRaceLabel.setText("");
                        break;
                }
            }
        } catch (NullPointerException e){
            character = new Character();
        }
    }

    @FXML
    void choiseDwarfButtonAction(ActionEvent event) {
        ObjectSaveManager reader = new ObjectSaveManager();
        Attribute dwarfAtt = (Attribute) reader.getObject("attribute");
        dwarfAtt.setCharisma(dwarfAtt.getCharisma()-2);
        dwarfAtt.setConstitution(dwarfAtt.getConstitution()+2);
        System.out.println(dwarfAtt.getConstitution());
        chosenRaceLabel.setText("Anão");
        character.setRaceId(3);
    }



    @FXML
    void choiseElfButtonAction(ActionEvent event) {
        ObjectSaveManager reader = new ObjectSaveManager();
        Attribute elfAtt = (Attribute) reader.getObject("attribute");
        elfAtt.setDexterity(elfAtt.getDexterity()+2);
        elfAtt.setConstitution(elfAtt.getConstitution()-2);
        chosenRaceLabel.setText("Elfo");
        character.setRaceId(2);
    }
    @FXML
    void choiseHalflingButtonAction(ActionEvent event) {
        ObjectSaveManager reader = new ObjectSaveManager();
        Attribute halAtt = (Attribute) reader.getObject("attribute");
        halAtt.setDexterity(halAtt.getDexterity()+2);
        halAtt.setStrength(halAtt.getStrength()-2);
        reader.saveObject("attribute", halAtt);
        chosenRaceLabel.setText("Halfing");
        character.setRaceId(4);
    }
    @FXML
    void choiseHumanButtonAction(ActionEvent event) {
        chosenRaceLabel.setText("Humano");
        character.setRaceId(1);
    }

    @Override
    protected void saveCharacter(String fxml){
        ObjectSaveManager saver = new ObjectSaveManager();
        saver.saveObject("character", character);

        PaneManager paneManager = new PaneManager((Stage) chosenRaceLabel.getScene().getWindow());
        paneManager.openPane(fxml);
    }

}
