package com.elixir.controller;

import com.elixir.controller.abstractControllers.MenuController;
import com.elixir.dao.FolderDAO;
import com.elixir.manager.ObjectSaveManager;
import com.elixir.manager.PaneManager;
import com.elixir.model.Folder;
import com.elixir.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.sql.SQLException;
import java.util.Map;

public class newFolderNameController extends MenuController {

    @FXML
    private AnchorPane anchorRoot;
    private User user;
    private ObjectSaveManager saveManager;
    private Map<Integer, Folder> folderMap;

    @FXML
    public void initialize() {
        super.initialize();
        saveManager = new ObjectSaveManager();
        user = (User) saveManager.getObject("user");
        folderMap = (Map<Integer, Folder>) saveManager.getObject("folders");
    }

    @FXML
    private Button createFolderButton;

    @FXML
    private TextField folderNameField;

    @FXML
    public void createFolderButtonAction(ActionEvent event) {
        Folder folder;
        if (!folderNameField.getText().isBlank() || !folderNameField.getText().isEmpty()) {
            folder = new Folder(user.getId(), folderNameField.getText());
        } else {
            folder = new Folder(user.getId(), "Pasta sem nome");
        }
            FolderDAO dao = new FolderDAO();
            try {
                folder.setId(dao.create(folder));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            folderMap.put(folder.getId(), folder);

            saveManager.saveObject("folder", folder);
            saveManager.saveObject("folders", folderMap);
            PaneManager manager = new PaneManager();
            manager.openPane("newFolderCharacters");
    }
}
