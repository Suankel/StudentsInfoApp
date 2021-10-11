package MainPackage.StudentOverView;

import MainPackage.DataBase.DataBaseController;
import MainPackage.Human.Gender;
import MainPackage.Human.Student;
import MainPackage.Main;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EditStudentInfoController {

    @FXML
    private TextField lastNameField;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField patronymicNameField;
    @FXML
    private ComboBox<String> facultyField;
    @FXML
    private ComboBox<String> specialityField;
    @FXML
    private ComboBox<String> groupIdField;
    @FXML
    private ComboBox<String> courseField;
    @FXML
    private DatePicker birthdayField;
    @FXML
    private ComboBox<String> genderComboBox;
    @FXML
    private Button saveEditionBtn = new Button();

    private DataBaseController dataBaseController;
    private static volatile Student editableStudent;
    private Stage dialogStage;
    private Main mainApp;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    public void initialize() throws SQLException {

        editableStudent = StudentsOverViewController.getChosenStudent();

        saveEditionBtn.setDefaultButton(true);

        lastNameField.setPromptText("Фамилия");
        firstNameField.setPromptText("Имя");
        patronymicNameField.setPromptText("Отчество");
        birthdayField.setPromptText("Дата рождения");

        lastNameField.setText(editableStudent.getLastName());
        firstNameField.setText(editableStudent.getFirstName());
        patronymicNameField.setText(editableStudent.getPatronymicName());

        dataBaseController = new DataBaseController();

        List<String> faculties = dataBaseController.selectForComboBox("Name", "Faculties", "");
        facultyField.setItems(FXCollections.observableArrayList(faculties));
        facultyField.getSelectionModel().select(editableStudent.getFaculty());

        specialityField.setItems(FXCollections.observableArrayList(dataBaseController.selectForComboBox(
                "Name", "Specialities", "WHERE Name_Of_Faculty = '" + facultyField.getSelectionModel().getSelectedItem() + "'")));
        specialityField.getSelectionModel().select(editableStudent.getSpeciality());

        facultyField.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    specialityField.setItems(FXCollections.observableArrayList(dataBaseController.selectForComboBox(
                            "Name", "Specialities", "WHERE Name_Of_Faculty = '" + newValue + "'")
                    ));
                    specialityField.getSelectionModel().selectFirst();
                });

        groupIdField.setItems(FXCollections.observableArrayList(dataBaseController.selectForComboBox(
                "Groups.ID", "Groups", "JOIN Specialities ON Groups.Speciality_ID = Specialities.ID " +
                        "WHERE Specialities.Name = " + "'" + specialityField.getSelectionModel().getSelectedItem() + "'")));
        groupIdField.getSelectionModel().select(String.valueOf(editableStudent.getGroupID()));


        specialityField.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    groupIdField.setItems(FXCollections.observableArrayList(dataBaseController.selectForComboBox(
                            "Groups.ID", "Groups", "JOIN Specialities ON Groups.Speciality_ID = Specialities.ID " +
                                    "WHERE Specialities.Name = " + "'" + newValue + "'")
                    ));
                    groupIdField.getSelectionModel().selectFirst();
                });

        courseField.setItems(FXCollections.observableArrayList(dataBaseController.selectForComboBox(
                "DISTINCT Course", "Groups", "")));
        courseField.getSelectionModel().select(String.valueOf(editableStudent.getCourse()));

        groupIdField.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {

                    if (groupIdField.getSelectionModel().selectedItemProperty().get() != null && !groupIdField.getSelectionModel().selectedItemProperty().get().equals("")) {
                        courseField.setItems(dataBaseController.selectForComboBox(
                                "DISTINCT Course", "Groups", "")
                        );
                        courseField.disableProperty().setValue(false);
                        saveEditionBtn.disableProperty().setValue(false);
                    } else {
                        courseField.setValue("");
                        courseField.disableProperty().setValue(true);
                        saveEditionBtn.disableProperty().setValue(true);
                    }
                });

        //Должно работать так, что кнопка "Сохранить становится активной, только при изменении элементов Scene (пока не работает)"
/*        EventHandler<Event> mouseEventHandler = event -> System.out.println("Мышь!");
        dialogStage.getScene().getRoot().addEventHandler(MouseEvent.MOUSE_ENTERED, mouseEventHandler);*/

        List<String> enumValues = new ArrayList<>();
        for (Gender gender: Gender.values())
            enumValues.add(gender.getType());

        genderComboBox.setItems(FXCollections.observableArrayList(enumValues));
        genderComboBox.getSelectionModel().select(editableStudent.getGender().toString());

        birthdayField.setValue(editableStudent.getDateOfBirth());

    }

    @FXML
    public void saveChanges() {
        if (isInputValid()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("");
            alert.setHeaderText("");

            try {
                String lastName = lastNameField.getText().substring(0, 1).toUpperCase().replace(" ", "") +
                        lastNameField.getText().substring(1).toLowerCase().replace(" ", "");
                String firstName = firstNameField.getText().substring(0, 1).toUpperCase().replace(" ", "") +
                        firstNameField.getText().substring(1).toLowerCase().replace(" ", "");
                String patronymicName = patronymicNameField.getText().substring(0, 1).toUpperCase().replace(" ", "") +
                        patronymicNameField.getText().substring(1).toLowerCase().replace(" ", "");

                dataBaseController = new DataBaseController();
                dataBaseController.updateStudentInDB(
                        lastName,
                        firstName,
                        patronymicName,
                        Integer.parseInt(groupIdField.getSelectionModel().selectedItemProperty().get()),
                        genderComboBox.getSelectionModel().selectedItemProperty().get(),
                        birthdayField.getValue(),
                        editableStudent.getStudentID());
                mainApp.getStudentsOverViewController().setListOfStudentsFromDB();
                mainApp.getStudentsOverViewController().setFilteredListForTable();
                alert.setContentText("Данные о студенте изменены");
                closeDialogStage();
                alert.showAndWait();

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private boolean isInputValid() {
        StringBuffer error_message = new StringBuffer("");
        LocalDate currentDate = LocalDate.now();
        if (lastNameField.getText() == null || lastNameField.getText().isEmpty())
            error_message.append("Введите корректное значение в поле 'Фамилия'!\n");
        if (firstNameField.getText() == null || firstNameField.getText().isEmpty())
            error_message.append("Введите корректное значение в поле 'Имя'!\n");
        if (patronymicNameField.getText() == null || patronymicNameField.getText().isEmpty())
            error_message.append("Введите корректное значение в поле 'Отчество'!\n");
        if (birthdayField.getValue() == null)
            error_message.append("Выберите дату рождения");
        else if (birthdayField.getValue().isAfter(currentDate))
            error_message.append("Значение в поле 'Дата рождения' не может быть позже текущей даты!\n");
        else if ((currentDate.getYear() - birthdayField.getValue().getYear()) < 17)
            error_message.append("Этому студентику поступать рановато");

        if (error_message.length() == 0)
            return true;
        else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Внесите обновленную информацию о студенте");
            alert.setTitle("");
            alert.setContentText(String.valueOf(error_message));
            alert.showAndWait();
            return false;
        }
    }

    public void closeDialogStage() {
        dialogStage.close();
    }
}
