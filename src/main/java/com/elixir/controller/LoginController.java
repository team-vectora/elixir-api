package com.elixir.controller;

import com.elixir.controller.objects.ValidationButton;
import com.elixir.dao.*;
import com.elixir.factory.ConnectionFactory;
import com.elixir.manager.ObjectSaveManager;
import com.elixir.manager.PaneManager;
import com.elixir.model.*;

import com.elixir.model.Character;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static com.elixir.model.tables.TypeID.CLERIC;
import static com.elixir.model.tables.TypeID.WIZARD;


public class LoginController {

    @FXML
    private Button createAccountButton;

    @FXML
    private Label errorLabel;

    @FXML
    private Button loginButton;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField usernameField;

    @FXML
    private CheckBox viewPasswordCheckbox;

    @FXML
    private TextField textPassword;

    @FXML
    private VBox vboxBody;

    private String password;

    @FXML
    void createAccountButtonAction(ActionEvent event) {
        PaneManager manager = new PaneManager();
        manager.openPane("logon");
    }

    @FXML
    void loginButtonAction(ActionEvent event) throws SQLException {
        errorLabel.setMinHeight(20.0);
        errorLabel.setTextFill(Color.GREEN);
        errorLabel.setFont(Font.font("System Bold", FontWeight.BOLD, 14.0));
        errorLabel.setText("Carregando...");
        errorLabel.setText("Carregando...");

        UserDAO userDAO = new UserDAO();
        User filterUser = new User(true);
        filterUser.setUsername(usernameField.getText());
        System.out.println("Buscando: " + filterUser.getUsername());

        User user;

        try {
            user = (User) userDAO.read(filterUser).values().toArray()[0];
        } catch (SQLException exception){
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText(exception.getMessage());
            throw exception;
        } catch (NullPointerException exception){
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText("Verifique sua conexão");
            throw exception;
        } catch (ArrayIndexOutOfBoundsException e){
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText("Usuario ou Senha incorretos");
            throw e;
        }

        ObjectSaveManager saveManager = new ObjectSaveManager();
        saveManager.saveObject("user", user);

        if (!user.isVerify()){
            userNotVerifyRoutine();
            return;
        }

        password = viewPasswordCheckbox.isSelected() ? textPassword.getText() : passwordField.getText();

        System.out.println("Senha: " + password);
        System.out.println("Senha Hash: " + user.getPassword());

        if (BCrypt.checkpw(password, user.getPassword())){
            errorLabel.setTextFill(Color.GREEN);
            errorLabel.setText("Inciando Seção...");


            FolderDAO folderDAO = new FolderDAO();
            Folder folderFilter = new Folder();
            folderFilter.setUserId(user.getId());

            CharacterMasterDAO viewDAO = new CharacterMasterDAO();
            CharacterMaster characterMasterFilter = new CharacterMaster();
            characterMasterFilter.setFolder(folderFilter);

            Map<Integer, Folder> folderMap;
            Map<Integer, CharacterMaster> characterMasterMap;
            try {
                folderMap = folderDAO.read(folderFilter);
                characterMasterMap = viewDAO.read(characterMasterFilter);
            } catch (SQLException e){
                e.printStackTrace();
                throw e;
            }

            saveManager.saveObject("folders", folderMap);
            saveManager.saveObject("characters", characterMasterMap);

            PaneManager manager = new PaneManager();
            manager.openPane("startScreenPane");
        } else {
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText("Usuário ou Senha inválidos");
        }
    }

    private void userNotVerifyRoutine() {
        errorLabel.setTextFill(Color.RED);
        errorLabel.setText("Esse usuário ainda não foi confirmado");
        if (vboxBody.getChildren().size() < 5) {
            Insets insets = new Insets(
                    vboxBody.getInsets().getTop(),
                    vboxBody.getInsets().getRight(),
                    30.0,
                    vboxBody.getInsets().getLeft()
            );
            vboxBody.setPadding(insets);
            vboxBody.getChildren().add(new ValidationButton("Confimação"));
        }
    }

    @FXML
    void viewPasswordCheckboxAction(ActionEvent event) {

        if (viewPasswordCheckbox.isSelected()){
            password = passwordField.getText();
            textPassword.setText(password);
            textPassword.setMinHeight(39.0);
            passwordField.setMinHeight(0);
        } else {
            password = textPassword.getText();
            passwordField.setText(password);
            passwordField.setMinHeight(39.0);
            textPassword.setMinHeight(0);
        }
    }

    public void normalErrorLabel(){
    }


}