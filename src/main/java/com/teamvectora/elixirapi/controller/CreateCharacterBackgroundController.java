package com.teamvectora.elixirapi.controller;

import com.teamvectora.elixirapi.controller.abstractControllers.CreateCharacterSectionController;
import com.teamvectora.elixirapi.dao.*;
import com.teamvectora.elixirapi.manager.JsonManger;
import com.teamvectora.elixirapi.manager.ObjectSaveManager;
import com.teamvectora.elixirapi.manager.PaneManager;
import com.teamvectora.elixirapi.model.*;
import com.teamvectora.elixirapi.model.Attribute;
import com.teamvectora.elixirapi.model.CharacterMaster;
import com.teamvectora.elixirapi.model.Currency;
import com.teamvectora.elixirapi.model.Folder;
import com.teamvectora.elixirapi.model.Slots;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import static com.teamvectora.elixirapi.model.tables.TypeID.*;

public class CreateCharacterBackgroundController extends CreateCharacterSectionController {

    @FXML
    private TextArea backgroundField;

    @FXML
    private Button createCharacterButton;

    @FXML
    private Label errorLabel;

    private CharacterMaster character;

    private Attribute attribute;

    private Slots slots;


    @FXML
    public void initialize(){
        super.initialize();

        ObjectSaveManager reader = new ObjectSaveManager();
        character = (CharacterMaster) reader.getObject("character");

        if(character.getBackground() != null){
            backgroundField.setText(character.getBackground());
        }
        attribute = character.getAttribute();

    }
    @FXML
    void finishButtonAction(ActionEvent event){
        try {
            finishCharacter();
        } catch (SQLException | ParseException | IOException e) {
            errorLabel.setText("Ocorreu um erro inesperado");
            throw new RuntimeException(e);
        }
    }

    private void finishCharacter() throws SQLException, IOException, ParseException {

        long classXP = (long) JsonManger.get("class/" + getClass(character.getClassId()) + "/level:" + character.level + "/XP");
        double maxHeight = (double) JsonManger.get("race/race:" + character.getRaceId() + "/maxHeight");
        double minHeight = (double) JsonManger.get("race/race:" + character.getRaceId() + "/minHeight");
        long maxWeight = (long) JsonManger.get("race/race:" + character.getRaceId() + "/maxWeight");
        long minWeight = (long) JsonManger.get("race/race:" + character.getRaceId() + "/minWeight");
        String jsonDicePv = (String) JsonManger.get("class/" + getClass(character.getClassId()) + "/Dado de Vida");
        System.out.println(jsonDicePv);
        int dicePV = Integer.parseInt(String.valueOf(jsonDicePv).replace("d", ""));

        int totalBonusPV = 0;
        for (int i = 1; i <= character.level; i++) {
            String levelBonusPV = String.valueOf(JsonManger.get("class/" + getClass(character.getClassId()) + "/level:" + i + "/Dado de Vida"));
            if (levelBonusPV.contains("PV")) {
                totalBonusPV += Integer.parseInt(levelBonusPV.replace("PV", "").trim());
            } else {
                totalBonusPV += dicePV;
            }
        }
        System.out.println(totalBonusPV);

        ObjectSaveManager reader = new ObjectSaveManager();
        Map<Integer, Folder> folderMap = (Map<Integer, Folder>) reader.getObject("folders");

        int defaultId = folderMap.values()
                .stream()
                .filter(f -> f.getName().equals("default"))
                .findFirst()
                .map(Folder::getId)
                .orElse(-1);

        character.setFolderId(defaultId);
        character.setExperience((int) classXP);
        character.setHeight((int) ((maxHeight + minHeight) / 2));
        character.setWeight((int) ((maxWeight + minWeight) / 2));
        character.setMaxPv((int) (((Math.round((float) ((attribute.getConstitution() - 10) / 2) + 0.5) + dicePV) * character.level) + totalBonusPV));
        character.setImagePath("default");
        character.setBackground(backgroundField.getText());

        character.setFolder(folderMap.get(defaultId));

        System.out.println(character);

        Attribute backupAttribute = attribute;

        if (reader.getObject("bonus") != null) {
            Attribute bonusAttribute = (Attribute) reader.getObject("bonus");
            attribute.setStrength(attribute.getStrength() + bonusAttribute.getStrength());
            attribute.setDexterity(attribute.getDexterity() + bonusAttribute.getDexterity());
            attribute.setConstitution(attribute.getConstitution() + bonusAttribute.getConstitution());
            attribute.setIntelligence(attribute.getIntelligence() + bonusAttribute.getIntelligence());
            attribute.setWisdom(attribute.getWisdom() + bonusAttribute.getWisdom());
            attribute.setCharisma(attribute.getCharisma() + bonusAttribute.getCharisma());
        }

        if (attribute.getStrength() == 0){
            errorLabel.setText("Atributo zero para Força");
            attribute = backupAttribute;
        }
        if (attribute.getDexterity() == 0){
            errorLabel.setText("Atributo zero para Destreza");
            attribute = backupAttribute;
        }
        if (attribute.getConstitution() == 0){
            errorLabel.setText("Atributo zero para Constituição");
            attribute = backupAttribute;
        }
        if (attribute.getIntelligence() == 0){
            errorLabel.setText("Atributo zero para Inteligência");
            attribute = backupAttribute;
        } 
        if (attribute.getWisdom() == 0){
            errorLabel.setText("Atributo zero para Sabedoria");
            attribute = backupAttribute;
        }
        if (attribute.getCharisma() == 0){
            errorLabel.setText("Atributo zero para Carsima");
            attribute = backupAttribute;
        }

        if (attribute.getStrength() > 29)
            attribute.setStrength(29);
        if (attribute.getDexterity() > 29)
            attribute.setDexterity(29);
        if (attribute.getConstitution() > 29)
            attribute.setConstitution(29);
        if (attribute.getIntelligence() > 29)
            attribute.setIntelligence(29);
        if (attribute.getWisdom() > 29)
            attribute.setWisdom(29);
        if (attribute.getCharisma() > 29)
            attribute.setCharisma(29);
        
        switch (character.getClassId()) {
            case CLERIC -> {
                if (invalidateAttribute(12, 14, attribute.getConstitution(), attribute.getWisdom())) {
                    errorLabel.setText("Atributo menor permitido para a classe");
                    attribute = backupAttribute;
                    return;
                }
            }
            case WARRIOR -> {
                if (invalidateAttribute(12, 12, attribute.getConstitution(), attribute.getStrength())) {
                    errorLabel.setText("Atributo menor permitido para a classe");
                    attribute = backupAttribute;
                    return;
                }
            }
            case THIEF -> {
                if (invalidateAttribute(12, 0, attribute.getDexterity(), 1)) {
                    errorLabel.setText("Atributo menor permitido para a classe");
                    attribute = backupAttribute;
                    return;
                }
            }
            case WIZARD -> {
                if (invalidateAttribute(14, 0, attribute.getIntelligence(), 1)) {
                    errorLabel.setText("Atributo menor permitido para a classe");
                    attribute = backupAttribute;
                    return;
                }
            }
            default -> System.out.println("ID de classe inválido");
        }


        if (character.getClassId() == CLERIC){
            character.setSlots(new Slots(character.getId(),
                   (int) ((long) JsonManger.get("class/cleric/level:" + character.level + "/1o")),
                   (int) ((long) JsonManger.get("class/cleric/level:" + character.level + "/2o")),
                   (int) ((long) JsonManger.get("class/cleric/level:" + character.level + "/3o")),
                   (int) ((long) JsonManger.get("class/cleric/level:" + character.level + "/4o")),
                   (int) ((long) JsonManger.get("class/cleric/level:" + character.level + "/5o")),
                   (int) ((long) JsonManger.get("class/cleric/level:" + character.level + "/6o")),
                   (int) ((long) JsonManger.get("class/cleric/level:" + character.level + "/7o")),
                   0,
                   0
            ));
        }

        if (character.getClassId() == WIZARD){
            character.setSlots(new Slots(character.getId(),
                    (int) ((long) JsonManger.get("class/wizard/level:" + character.level + "/1o")),
                    (int) ((long) JsonManger.get("class/wizard/level:" + character.level + "/2o")),
                    (int) ((long) JsonManger.get("class/wizard/level:" + character.level + "/3o")),
                    (int) ((long) JsonManger.get("class/wizard/level:" + character.level + "/4o")),
                    (int) ((long) JsonManger.get("class/wizard/level:" + character.level + "/5o")),
                    (int) ((long) JsonManger.get("class/wizard/level:" + character.level + "/6o")),
                    (int) ((long) JsonManger.get("class/wizard/level:" + character.level + "/7o")),
                    (int) ((long) JsonManger.get("class/wizard/level:" + character.level + "/8o")),
                    (int) ((long) JsonManger.get("class/wizard/level:" + character.level + "/9o"))
            ));
        }

        character.setCurrency(new Currency());
        character.getCurrency().setGold((
                (int) (Math.random() * 6 + 1) +
                (int) (Math.random() * 6 + 1) +
                (int) (Math.random() * 6 + 1)) * 10);
        character.setSpeech(new ArrayList<>());
        for (Object idSpeech: (JSONArray) JsonManger.get("race/race:" + character.getRaceId() + "/languages")) {
            character.addSpeech(new Speech(
                    character.getId(),
                    (int) ((long) idSpeech)
            ));
        }


        SpeechDAO speechDAO = new SpeechDAO();
        CurrencyDAO currencyDAO = new CurrencyDAO();
        SlotsDAO slotsDAO = new SlotsDAO();
        CharacterDAO dao = new CharacterDAO();
        AttributeDAO attributeDAO = new AttributeDAO();

        try {
            int idAttribute = attributeDAO.create(character.getAttribute());
            character.setAttributeId(idAttribute);
            character.getAttribute().setId(idAttribute);

            int idCharacter = dao.create(character);
            character.setId(idCharacter);

            character.getCurrency().setCharacterId(idCharacter);
            character.getCurrency().setId(currencyDAO.create(character.getCurrency()));

            if (character.getClassId() == CLERIC || character.getClassId() == WIZARD) {
                character.getSlots().setCharacterId(idCharacter);
                character.getSlots().setId(slotsDAO.create(character.getSlots()));
            }

            for (Speech speech :
                    character.getSpeech()) {
                speech.setCharacterId(idCharacter);
                speech.setId(speechDAO.create(speech));
            }

        } catch (SQLException e){
            errorLabel.setText("Preencha todos os campos corretamente para criar o seu personagem");
            throw e;
        }

        Map<Integer, CharacterMaster> characters = (Map<Integer, CharacterMaster>) reader.getObject("characters");
        characters.put(character.getId(), character);

        reader.saveObject("characters", characters);

        PaneManager paneManager = new PaneManager();
        paneManager.openPane("myCharactersPane");


    }


    @Override
    protected void saveCharacter(String fxml){
        character.setBackground(backgroundField.getText());

        ObjectSaveManager saver = new ObjectSaveManager();
        saver.saveObject("character", character);

        PaneManager paneManager = new PaneManager();
        paneManager.openPane(fxml);

    }

    public static String getClass(int classId){
        switch (classId){
            case 1:
                return "warrior";
            case 4:
                return "wizard";
            case 3:
                return "thief";
            case 2:
                return "cleric";
            default:
                return "";
        }
    }

    private boolean invalidateAttribute(int minAttribute1, int minAttribute2, int valueAttribute1, int valueAttribute2){
        return valueAttribute1 < minAttribute1 || valueAttribute2 < minAttribute2;
    }

}
