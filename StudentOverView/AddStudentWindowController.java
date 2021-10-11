package MainPackage.StudentOverView;

import MainPackage.DataBase.DataBaseController;
import MainPackage.Human.Gender;
import MainPackage.Human.Student;
import MainPackage.Main;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AddStudentWindowController {

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
    private Label courseField;
    @FXML
    private DatePicker birthdayField;
    @FXML
    private ComboBox<String> genderComboBox;
    @FXML
    private Button addStudentBtn;

    private Main mainApp;
    private Stage dialogStage;
    private DataBaseController dataBaseController;

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void initialize() throws SQLException {
        lastNameField.setPromptText("Фамилия");
        firstNameField.setPromptText("Имя");
        patronymicNameField.setPromptText("Отчество");
        birthdayField.setPromptText("Дата рождения");

        addStudentBtn.setDefaultButton(true);

        dataBaseController = new DataBaseController();

        facultyField.setItems(FXCollections.observableArrayList(dataBaseController.selectForComboBox("Name", "Faculties", "")));
        facultyField.getSelectionModel().selectFirst();

        specialityField.setItems(FXCollections.observableArrayList(dataBaseController.selectForComboBox(
                "Name", "Specialities", "WHERE Name_Of_Faculty = '" + facultyField.getSelectionModel().getSelectedItem() + "'")));
        specialityField.getSelectionModel().selectFirst();

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
        groupIdField.getSelectionModel().selectFirst();


        specialityField.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    groupIdField.setItems(FXCollections.observableArrayList(dataBaseController.selectForComboBox(
                            "Groups.ID", "Groups", "JOIN Specialities ON Groups.Speciality_ID = Specialities.ID " +
                                    "WHERE Specialities.Name = " + "'" + newValue + "'")
                    ));
                    groupIdField.getSelectionModel().selectFirst();
                });

        courseField.setText(dataBaseController.printSmthByParameter("Course", "Groups", "WHERE Groups.ID = '",
                groupIdField.getSelectionModel().getSelectedItem() + "'"));

        groupIdField.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {

                    if (groupIdField.getSelectionModel().selectedItemProperty().get() != null && !groupIdField.getSelectionModel().selectedItemProperty().get().equals("")) {
                        courseField.setText(dataBaseController.printSmthByParameter(
                                "Course", "Groups", "WHERE Groups.ID = '", newValue + "'")
                        );

                        courseField.setTextFill(Paint.valueOf("#FFFFFF"));
                        addStudentBtn.disableProperty().setValue(false);
                    } else {
                        courseField.setTextFill(Paint.valueOf("#64112B"));
                        courseField.setText("На выбранной специальности отсутствуют группы");
                        addStudentBtn.disableProperty().setValue(true);
                    }
                });

        List<String> enumValues = new ArrayList<>();
        for (Gender gender: Gender.values())
            enumValues.add(gender.getType());

        genderComboBox.setItems(FXCollections.observableArrayList(enumValues));
        genderComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    public void addStudentToDB() {

        if (isInputValid()) {
            String lastName = lastNameField.getText().substring(0, 1).toUpperCase().replace(" ", "") +
                    lastNameField.getText().substring(1).toLowerCase().replace(" ", "");
            String firstName = firstNameField.getText().substring(0, 1).toUpperCase().replace(" ", "") +
                    firstNameField.getText().substring(1).toLowerCase().replace(" ", "");
            String patronymicName = patronymicNameField.getText().substring(0, 1).toUpperCase().replace(" ", "") +
                    patronymicNameField.getText().substring(1).toLowerCase().replace(" ", "");

            Student student = new Student(lastName, firstName, patronymicName);
            student.setGroupID(Integer.parseInt(groupIdField.getSelectionModel().selectedItemProperty().get()));

            if (genderComboBox.getSelectionModel().selectedItemProperty().get().equalsIgnoreCase("мужской"))
                student.setGender(Gender.MALE);
            else if (genderComboBox.getSelectionModel().selectedItemProperty().get().equalsIgnoreCase("женский"))
                student.setGender(Gender.FEMALE);
            student.setDateOfBirth(birthdayField.getValue());

            label: try {

              for (Student s : StudentsOverViewController.getListOfStudents()) {
                    if (student.compareStudents(s)) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Найдено совпадение");
                        alert.setTitle("");
                        alert.setContentText("В группе " + student.getGroupID() + " уже есть студент " +
                                student.getFIO() + ". Вы уверены, что хотите добавить студента?");
                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.get() == ButtonType.OK) {
                            break;
                        } else {
                            break label;
                        }
                    }
                }

                    try {
                        dataBaseController = new DataBaseController();
                        dataBaseController.addStudentToDB(student);
                        mainApp.getStudentsOverViewController().setListOfStudentsFromDB();
                        mainApp.getStudentsOverViewController().setFilteredListForTable();
                        Alert alertAdditionInfo = new Alert(Alert.AlertType.INFORMATION);
                        alertAdditionInfo.setTitle("Добавлен новый студент");
                        alertAdditionInfo.setHeaderText("");
                        alertAdditionInfo.setContentText("Студент " +
                                student.getFIO() + " добавлен(а) в группу " + groupIdField.getSelectionModel().selectedItemProperty().get());
                        alertAdditionInfo.showAndWait();
                        dialogStage.close();
                    } catch (SQLException e) {
                        Alert alertInfo = new Alert(Alert.AlertType.INFORMATION);
                        alertInfo.setTitle("");
                        alertInfo.setHeaderText("");
                        alertInfo.setContentText(e.getMessage());
                        alertInfo.showAndWait();
                    }

            } catch(Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

   @FXML
    public void closeDialogStage() {
        dialogStage.close();
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
            alert.setHeaderText("Внесите информацию о студенте");
            alert.setTitle("");
            alert.setContentText(String.valueOf(error_message));
            alert.showAndWait();
            return false;
        }
    }
}
