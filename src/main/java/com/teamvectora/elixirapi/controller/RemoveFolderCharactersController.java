package com.teamvectora.elixirapi.controller;

import com.teamvectora.elixirapi.controller.abstractControllers.MenuController;
import com.teamvectora.elixirapi.dao.CharacterDAO;
import com.teamvectora.elixirapi.manager.ObjectSaveManager;
import com.teamvectora.elixirapi.manager.PaneManager;
import com.teamvectora.elixirapi.model.CharacterMaster;
import com.teamvectora.elixirapi.model.Folder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.sql.SQLException;
import java.util.Map;

public class RemoveFolderCharactersController extends MenuController {

    @FXML
    private AnchorPane anchorRoot;

    @FXML
    private MenuButton charactersMenuButton;

    @FXML
    private Button removeButton;

    @FXML
    private Button jumpButton;

    @FXML
    private Label folderNameLabel;

    private ObjectSaveManager saveManager;
    private Map<Integer, CharacterMaster> characterMap;
    private Map<Integer, Folder> folderMap;

    @FXML
    public void initialize() {
        super.initialize();

        saveManager = new ObjectSaveManager();
        characterMap = (Map<Integer, CharacterMaster>) saveManager.getObject("characters");
        Folder folder = (Folder) saveManager.getObject("folder");
        folderMap = (Map<Integer, Folder>) saveManager.getObject("folders");

        folderNameLabel.setText(folder.getName());

        for (CharacterMaster characterMaster :
                characterMap.values()) {
            System.out.println("sim?");
            if (characterMaster.getFolderId() == folder.getId()){
                System.out.println("sim");
                System.out.println(characterMaster.getFolderId());
                System.out.println(folder.getId());
                CheckMenuItem menuItem = new CheckMenuItem();
                menuItem.setText(characterMaster.getName());
                menuItem.setId(String.valueOf(characterMaster.getId()));
                menuItem.setOnAction(event -> {
                    charactersMenuButton.setText(characterMap.get(Integer.parseInt(menuItem.getId())).getName());
                });
                charactersMenuButton.getItems().add(menuItem);
            }
        }

    }

    @FXML
    void removeButtonAction(ActionEvent event) {
        CharacterDAO dao = new CharacterDAO();
        for (MenuItem menuItem :
                charactersMenuButton.getItems()) {
            CheckMenuItem checkMenuItem = (CheckMenuItem) menuItem;
            if (checkMenuItem.isSelected()){
                CharacterMaster character = characterMap.get(Integer.parseInt(checkMenuItem.getId()));

                int defaultId = folderMap.values()
                        .stream()
                        .filter(f -> f.getName().equals("default"))
                        .map(Folder::getId)
                        .findFirst()
                        .orElse(-1);

                character.setFolderId(defaultId);

                try {
                    dao.update(character);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                characterMap.put(character.getId(), character);
            }
        }
        saveManager.saveObject("characters", characterMap);
        saveManager.removeObject("folder");
        PaneManager paneManager = new PaneManager();
        paneManager.openPane("myCharactersPane");

    }

    @FXML
    void jumpButtonAction(ActionEvent event) {
        saveManager.removeObject("character");
        saveManager.removeObject("folder");
        saveManager.saveObject("characters", characterMap);
        PaneManager paneManager = new PaneManager();
        paneManager.openPane("myCharactersPane");
    }

}
