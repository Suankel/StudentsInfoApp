package MainPackage;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

public class AuthorizationWindowController {

    @FXML
    private Button enterBtn;
    @FXML
    private TextField loginField;
    @FXML
    private TextField passwordField;
    @FXML
    private RadioButton infoStudents;
    @FXML
    private RadioButton infoExams;

    private Main mainApp;

    public void initialize() {

        loginField.setPromptText("Логин");
        passwordField.setPromptText("Пароль");
        enterBtn.setDefaultButton(true);

        ToggleGroup toggleGroup = new ToggleGroup();
        infoStudents.setToggleGroup(toggleGroup);
        infoExams.setToggleGroup(toggleGroup);
        infoStudents.setSelected(true);

    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void loginAndChooseWindow() {

        if (loginField.getText().equals("Prof") && (passwordField.getText().equals("Q123"))) {
        if (infoStudents.isSelected())
            mainApp.showStudentsOverView();
        if (infoExams.isSelected())
            mainApp.showExamsWindow();
        }

    }

}
