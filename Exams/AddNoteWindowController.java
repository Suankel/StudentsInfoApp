package MainPackage.Exams;

import MainPackage.DataBase.DataBaseController;
import MainPackage.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class AddNoteWindowController {

    @FXML
    private ComboBox<String> subjectBox;
    @FXML
    private ComboBox<String> professorBox;
    @FXML
    private ComboBox<String> specialityBox;
    @FXML
    private DatePicker dateOfExam;
    @FXML
    private ComboBox<LocalTime> time_in_Box;
    @FXML
    private ComboBox<LocalTime> time_out_Box;
    @FXML
    private ComboBox<String> groupBox;
    @FXML
    private Button addBtn;


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

        addBtn.setDefaultButton(true);
        dataBaseController = new DataBaseController();
        subjectBox.setItems(FXCollections.observableArrayList(dataBaseController.selectForComboBox("Name", "Subjects", "")));
        subjectBox.getSelectionModel().selectFirst();


        professorBox.setItems(FXCollections.observableArrayList(dataBaseController.selectForComboBox(
                "FIO", "Professors", "JOIN Subjects_Professors ON Subjects_Professors.Professor_ID = Professors.ID " +
                        "JOIN Subjects ON Subjects.ID = Subjects_Professors.Subject_ID " +
                        "WHERE Subjects.Name = '" + subjectBox.getSelectionModel().getSelectedItem() + "'")));
        professorBox.getSelectionModel().selectFirst();

        subjectBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    professorBox.setItems(FXCollections.observableArrayList(dataBaseController.selectForComboBox(
                            "FIO", "Professors", "JOIN Subjects_Professors ON Subjects_Professors.Professor_ID = Professors.ID " +
                                    "JOIN Subjects ON Subjects.ID = Subjects_Professors.Subject_ID " +
                                    "WHERE Subjects.Name = '" + subjectBox.getSelectionModel().getSelectedItem() + "'")));
                    professorBox.getSelectionModel().selectFirst();
                });

        specialityBox.setItems(FXCollections.observableArrayList(dataBaseController.selectForComboBox(
                "Name", "Specialities", "")));
        specialityBox.getSelectionModel().selectFirst();

        groupBox.setItems(FXCollections.observableArrayList(dataBaseController.selectForComboBox(
                "Groups.ID", "Groups", "JOIN Specialities ON Groups.Speciality_ID = Specialities.ID " +
                        "WHERE Specialities.Name = " + "'" + specialityBox.getSelectionModel().getSelectedItem() + "'")));
        groupBox.getSelectionModel().selectFirst();


        specialityBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    groupBox.setItems(FXCollections.observableArrayList(dataBaseController.selectForComboBox(
                            "Groups.ID", "Groups", "JOIN Specialities ON Groups.Speciality_ID = Specialities.ID " +
                                    "WHERE Specialities.Name = " + "'" + newValue + "'")
                    ));
                    groupBox.getSelectionModel().selectFirst();
                });

        LocalTime time = LocalTime.of(8, 00);
        ObservableList<LocalTime> timeList = FXCollections.observableArrayList();
        while (time.getHour() != 18) {
            time = time.plusMinutes(15);
            timeList.add(time);
        }

        time_in_Box.setItems(timeList);
        time_out_Box.setItems(timeList);
    }

    @FXML
    public void addNoteToDB() {

        if (isInputValid()) {

            LocalDate date = dateOfExam.getValue();
            LocalTime time_in = time_in_Box.getValue();
            LocalTime time_out = time_out_Box.getValue();

            NoteOfExam noteOfExam = new NoteOfExam(date, time_in, time_out);
            noteOfExam.setSubject(subjectBox.getSelectionModel().selectedItemProperty().get());
            noteOfExam.setProfessorsFIO(professorBox.getSelectionModel().selectedItemProperty().get());
            noteOfExam.setGroupID(Integer.parseInt(groupBox.getSelectionModel().selectedItemProperty().get()));

                try {
                    dataBaseController = new DataBaseController();
                    dataBaseController.addNoteToDB(noteOfExam);
                    mainApp.getExamsListController().setListOfNotes();
                    mainApp.getExamsListController().createElementsOnPage(mainApp.getExamsListController().getPageIndex());
                    Alert alertAdditionInfo = new Alert(Alert.AlertType.INFORMATION);
                    alertAdditionInfo.setTitle("Добавлена запись");
                    alertAdditionInfo.setHeaderText("");
                    alertAdditionInfo.setContentText("Добавлена новая запись");
                    alertAdditionInfo.showAndWait();
                    dialogStage.close();
                } catch (SQLException e) {
                    Alert alertInfo = new Alert(Alert.AlertType.INFORMATION);
                    alertInfo.setTitle("");
                    alertInfo.setHeaderText("");
                    alertInfo.setContentText(e.getMessage());
                    alertInfo.showAndWait();
                }
        }
    }

    private boolean isInputValid() {
        StringBuffer error_message = new StringBuffer("");
        LocalDate currentDate = LocalDate.now();

        if (dateOfExam.getValue() == null)
            error_message.append("Выберите дату проведения экзамена\n");
        else if (dateOfExam.getValue().isBefore(currentDate))
            error_message.append("Значение в поле 'Дата проведения экзамена' не может быть ранее текущей даты!\n");

        if (time_in_Box.getValue() == null)
            error_message.append("Выберите время начала экзамена\n");
        if (time_out_Box.getValue() == null)
            error_message.append("Выберите время окончания экзамена\n");

        if (time_in_Box.getValue() != null && time_out_Box.getValue() != null) {
            if (ChronoUnit.MINUTES.between(time_in_Box.getValue(), time_out_Box.getValue()) < 60)
                error_message.append("Минимальная продолжительность экзамена должна составлять 60 минут\n");
        }

        if (error_message.length() == 0)
            return true;
        else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Заполните корректно все поля");
            alert.setTitle("");
            alert.setContentText(String.valueOf(error_message));
            alert.showAndWait();
            return false;
        }
    }

    @FXML
    public void closeDialogStage() {
        dialogStage.close();
    }
}

