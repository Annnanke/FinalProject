package org.example;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.example.Requesters.SignInRequest.doSignInRequest;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


public class AuthorizationController {


    @FXML
    private TextField loginTextField;

    @FXML
    private TextField passwordTextField;

    @FXML
    private Label errorLabel;

    @FXML
    private void login() throws IOException {
        try {
            if(doSignInRequest(loginTextField.getText(), passwordTextField.getText()) == 200){
                App.goToWarehouse();
            }
            else {
                errorLabel.setVisible(true);
            }
        } catch (URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
