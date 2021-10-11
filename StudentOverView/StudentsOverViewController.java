package MainPackage.StudentOverView;

import MainPackage.DataBase.DataBaseController;
import MainPackage.Human.Gender;
import MainPackage.Human.Student;
import MainPackage.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class StudentsOverViewController {

    @FXML
    private TableView<Student> studentTableView;
    @FXML
    private TableColumn<Student, String> lastNameColumn;
    @FXML
    private TableColumn<Student, String> firstNameColumn;
    @FXML
    private TableColumn<Student, String> patronymicNameColumn;
    @FXML
    private Label lastNameLabel;
    @FXML
    private Label firstNameLabel;
    @FXML
    private Label patronycNameLabel;
    @FXML
    private Label facultyLabel;
    @FXML
    private Label specialityLabel;
    @FXML
    private Label groupLabel;
    @FXML
    private Label courseLabel;
    @FXML
    private Label sexLabel;
    @FXML
    private Label ageLabel;
    @FXML
    private Label sliderLabel;
    @FXML
    private CheckBox deleteFromDBcheck;
    @FXML
    private Button editBtn;
    @FXML
    private Button deleteBtn;
    @FXML
    private TextField studentFIOField;
    @FXML
    private ComboBox<String> facultyField;
    @FXML
    private ComboBox<String> specialityField;
    @FXML
    private ComboBox<String> groupIdField;
    @FXML
    private CheckBox checkCourse1;
    @FXML
    private CheckBox checkCourse2;
    @FXML
    private CheckBox checkCourse3;
    @FXML
    private CheckBox checkCourse4;
    @FXML
    private CheckBox maleCheck;
    @FXML
    private CheckBox femaleCheck;
    @FXML
    private CheckBox checkAge = new CheckBox();
    @FXML
    private Slider slider = new Slider();


    private Main mainApp;
    private Stage dialogStage;
    private DataBaseController dataBaseController;
    private static volatile ObservableList<Student> listOfStudents;
    private static volatile FilteredList<Student> filteredList;
    private static volatile Student chosenStudent;

    public StudentsOverViewController() {

    }

    public void initialize() throws SQLException {

        dataBaseController = new DataBaseController();
        studentTableView.setRowFactory(t -> {
            TableRow<Student> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) { //Показ окна при двойном клике на строку таблицы
                    editStudentInfo();
                }
            });
            return row;
        });
        lastNameColumn.setCellValueFactory(cellData ->
                cellData.getValue().lastNameProperty());
        firstNameColumn.setCellValueFactory(cellData ->
                cellData.getValue().firstNameProperty());
        patronymicNameColumn.setCellValueFactory(cellData ->
                cellData.getValue().patronymicNameProperty());

        editBtn.disableProperty().setValue(true);
        deleteBtn.disableProperty().setValue(true);
        studentTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    showStudentInfo(newValue);
                    if (newValue != null) {
                        editBtn.disableProperty().setValue(false);
                        deleteBtn.disableProperty().setValue(false);
                    } else {
                        editBtn.disableProperty().setValue(true);
                        deleteBtn.disableProperty().setValue(true);
                    }

                });
        setListOfStudentsFromDB();
        setFilteredListForTable();

        slider.setDisable(true);
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            sliderLabel.textProperty().setValue(String.valueOf(newValue.intValue()));
        });

        checkAge.setOnAction(action -> {
            if (checkAge.isSelected()) {
                slider.setDisable(false);
                sliderLabel.textProperty().setValue(String.valueOf((int)slider.getValue()));
            } else {
                slider.setDisable(true);
                sliderLabel.textProperty().set("");
            }

        });

        specialityField.setValue("Не выбрано");
        groupIdField.setValue("Не выбрано");

        ObservableList<String> facultList = FXCollections.observableArrayList(dataBaseController.selectForComboBox("Name", "Faculties", ""));
        facultList.add(0, "Не выбрано");
        facultyField.setItems(facultList);
        facultyField.getSelectionModel().selectFirst();

        facultyField.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                        ObservableList<String> specList = FXCollections.observableArrayList(dataBaseController.selectForComboBox(
                                "Name", "Specialities", "WHERE Name_Of_Faculty = '" + newValue + "'"));
                        specList.add(0, "Не выбрано");
                        specialityField.setItems(specList);
                        specialityField.getSelectionModel().selectFirst();
                });

        specialityField.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                        ObservableList<String> groupList = FXCollections.observableArrayList(dataBaseController.selectForComboBox(
                                "Groups.ID", "Groups", "JOIN Specialities ON Groups.Speciality_ID = Specialities.ID " +
                                        "WHERE Specialities.Name = " + "'" + newValue + "'"));
                        groupList.add(0, "Не выбрано");
                        groupIdField.setItems(groupList);
                        groupIdField.getSelectionModel().selectFirst();
                });
        searchStudents();
    }

    public void refreshTable() {
        setFilteredListForTable();
        studentTableView.setItems(filteredList);
    }

    public void setListOfStudentsFromDB() {
        listOfStudents = dataBaseController.selectListOfStudents("*", "Students", "ORDER BY Last_Name");
    }
    public void setFilteredListForTable() {

        filteredList = new FilteredList<>(listOfStudents, p -> true);

        studentFIOField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(student -> {
                if (newValue.isEmpty())
                    return true;
                else {
                    if (student.getFIO().toLowerCase().contains(newValue.toLowerCase()))
                        return true;
                    return false;
                }
            });
        });
        studentTableView.setItems(filteredList);
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void showStudentInfo(Student student) {

            if (student != null) {
                lastNameLabel.setText(student.getLastName());
                firstNameLabel.setText(student.getFirstName());
                patronycNameLabel.setText(student.getPatronymicName());
                facultyLabel.setText(student.getFaculty());
                specialityLabel.setText(student.getSpeciality());
                groupLabel.setText(String.valueOf(student.getGroupID()));
                courseLabel.setText(String.valueOf(student.getCourse()));
                for (Gender gender : Gender.values()) {
                    if (gender == student.getGender())
                        sexLabel.setText(gender.getType());
                }
                ageLabel.setText(String.valueOf(student.getAge()));
            } else {
                lastNameLabel.setText("");
                firstNameLabel.setText("");
                patronycNameLabel.setText("");
                facultyLabel.setText("");
                specialityLabel.setText("");
                groupLabel.setText("");
                courseLabel.setText("");
                sexLabel.setText("");
                ageLabel.setText("");
            }
    }

    @FXML
    public void deleteStudentFromDB() throws SQLException {
        int selectedIndex = studentTableView.getSelectionModel().getSelectedIndex();
        String nameOfStudent = studentTableView.getSelectionModel().getSelectedItem().getFIO();

        if (selectedIndex >= 0) {
            if (deleteFromDBcheck.isSelected()) {
                dataBaseController = new DataBaseController();
                Alert alertOfDeleting = new Alert(Alert.AlertType.CONFIRMATION);
                alertOfDeleting.setTitle("Удаление");
                alertOfDeleting.setHeaderText("Удаление информации о студенте из БАЗЫ ДАННЫХ");
                alertOfDeleting.setContentText("Внимание! Удалить студента \"" + nameOfStudent + "\" из базы данных?");
                Optional<ButtonType> result = alertOfDeleting.showAndWait();
                if (result.get() == ButtonType.OK) {
                    Alert alertInfo = new Alert(Alert.AlertType.INFORMATION);
                    try {
                        dataBaseController.deleteFromDB("Students", "ID", studentTableView.getItems().get(selectedIndex).getStudentID());
                        deleteFromDBcheck.setSelected(false);
                        setListOfStudentsFromDB();
                        alertInfo.setTitle("Информация удалена");
                        alertInfo.setHeaderText("");
                        alertInfo.setContentText("Информация о студенте \"" + nameOfStudent + "\" удалена из базы данных");
                        alertInfo.showAndWait();
                    } catch (Exception e) {
                        alertInfo.setTitle("");
                        alertInfo.setHeaderText("");
                        alertInfo.setContentText(e.getMessage());
                        alertInfo.showAndWait();
                    }
                }
            } else {
                listOfStudents.remove(studentTableView.getSelectionModel().getSelectedItem());
            }
            refreshTable();
        }
    }

    public static ObservableList<Student> getListOfStudents() {
        return listOfStudents;
    }

    public static Student getChosenStudent() {
        return chosenStudent;
    }

    public synchronized void setChosenStudent() {
        chosenStudent = studentTableView.getSelectionModel().selectedItemProperty().get();
    }

    @FXML
    public void addStudent() {
        mainApp.showAddStudentWindow();
    }

    @FXML
    public void editStudentInfo() {
        setChosenStudent();
        mainApp.showEditStudentInfo();
    }

    @FXML
    public void searchStudents() {
        studentFIOField.clear();
        CopyOnWriteArrayList<Student> listForSearch = new CopyOnWriteArrayList<>(listOfStudents);

        List<Integer> checkBoxArrayCourse = new ArrayList<>();
        checkBoxArrayCourse.add(0);
        checkBoxArrayCourse.add(0);
        checkBoxArrayCourse.add(0);
        checkBoxArrayCourse.add(0);
        List<Integer> checkBoxArrayGender = new ArrayList<>();
        checkBoxArrayGender.add(0);
        checkBoxArrayGender.add(0);

        String[] genderTypes = new String[] {"Мужской", "Женский"};

        if (checkCourse1.isSelected())
            checkBoxArrayCourse.set(0, 1);
        if (checkCourse2.isSelected())
            checkBoxArrayCourse.set(1, 1);
        if (checkCourse3.isSelected())
            checkBoxArrayCourse.set(2, 1);
        if (checkCourse4.isSelected())
            checkBoxArrayCourse.set(3, 1);

        if (maleCheck.isSelected())
            checkBoxArrayGender.set(0, 1);
        if (femaleCheck.isSelected())
            checkBoxArrayGender.set(1, 1);

        for (Student st : listForSearch
             ) {
            if (checkBoxArrayCourse.contains(1)) {
                for (int i = 0; i < checkBoxArrayCourse.size(); i++
                ) {
                    if (checkBoxArrayCourse.get(i) == 0 && st.getCourse() == i + 1)
                        listForSearch.remove(st);
                }
            }

            if (checkBoxArrayGender.contains(1)) {
                for (int i = 0; i < checkBoxArrayGender.size(); i++
                ) {
                    if (checkBoxArrayGender.get(i) == 0 && st.getGender().toString().equals(genderTypes[i]))
                        listForSearch.remove(st);
                }
            }

            if (!(facultyField.getSelectionModel().selectedItemProperty().get().equals("Не выбрано"))) {
                if (!st.getFaculty().equals(facultyField.getSelectionModel().selectedItemProperty().get()))
                    listForSearch.remove(st);
            }

            if (!(specialityField.getSelectionModel().selectedItemProperty().get().equals("Не выбрано"))) {
                if (!st.getSpeciality().equals(specialityField.getSelectionModel().selectedItemProperty().get()))
                    listForSearch.remove(st);
            }

            if (!(groupIdField.getSelectionModel().selectedItemProperty().get().equals("Не выбрано"))) {
                if (!(st.getGroupID() == (Integer.parseInt(groupIdField.getSelectionModel().selectedItemProperty().get()))))
                    listForSearch.remove(st);
            }

            if (checkAge.isSelected() && st.getAge() != (int)slider.getValue())
                listForSearch.remove(st);
        }
        ObservableList<Student> newListForSearch = FXCollections.observableArrayList(listForSearch);
        FilteredList<Student> filteredListForSearch = new FilteredList<>(newListForSearch, p -> true);
        studentFIOField.textProperty().addListener((observable, oldValue, newValue) -> filteredListForSearch.setPredicate(student -> {
            if (newValue.isEmpty())
                return true;
            else {

                return student.getFIO().toLowerCase().contains(newValue.toLowerCase());
            }
        }));

        studentTableView.setItems(filteredListForSearch);
    }
    @FXML
    public void refreshSearch() {
        studentFIOField.clear();
        setListOfStudentsFromDB();
        refreshTable();

        checkAge.setSelected(false);
        slider.setDisable(true);
        sliderLabel.textProperty().set("");
        checkCourse1.setSelected(false);
        checkCourse2.setSelected(false);
        checkCourse3.setSelected(false);
        checkCourse4.setSelected(false);

        maleCheck.setSelected(false);
        femaleCheck.setSelected(false);

    }
}
