package MainPackage.Exams;

import MainPackage.DataBase.DataBaseController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import MainPackage.Main;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public class ExamsListController {
    @FXML
    private TableView<NoteOfExam> notesTableView;
    @FXML
    private Pagination pagination;
    @FXML
    private Button deleteBtn;
    @FXML
    private Button addBtn;
    @FXML
    private Button returnBtn;
    @FXML
    private Label infoLabel;

    private TableColumn<NoteOfExam, LocalTime> timeINColumn;
    private TableColumn<NoteOfExam,LocalTime> timeOUTColumn;

    private Stage dialogStage;
    private Main mainApp;
    private static volatile ObservableList<NoteOfExam> listOfNotes;
    private DataBaseController dataBaseController;
    private static final int ROWS_PER_PAGE = 10;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    public void initialize() throws SQLException {

        dataBaseController = new DataBaseController();
        setListOfNotes();

        int pageCount;

        if (listOfNotes.size() % ROWS_PER_PAGE > 0)
            pageCount = listOfNotes.size() / ROWS_PER_PAGE + 1;
        else
            pageCount = listOfNotes.size() / ROWS_PER_PAGE;

        pagination.setPageCount(pageCount);
        pagination.setCurrentPageIndex(0);
        notesTableView = createTable();
        pagination.setPageFactory(this::createElementsOnPage);

        deleteBtn.disableProperty().setValue(true);

        notesTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        deleteBtn.disableProperty().setValue(false);
                    } else {
                        deleteBtn.disableProperty().setValue(true);
                    }

                });

        timeINColumn.setOnEditCommit(event -> {
            try {
                dataBaseController.updateTimeInExams(notesTableView.getSelectionModel().getSelectedItem(), event.getNewValue());
                infoLabel.setText("");
                initialize();
            } catch (SQLException e) {
                infoLabel.setTextFill(Paint.valueOf("#64112B"));
                infoLabel.setText(e.getMessage());
            }
        });

        timeOUTColumn.setOnEditCommit(event -> {
            try {
                dataBaseController.updateTimeOutExams(notesTableView.getSelectionModel().getSelectedItem(), event.getNewValue());
                infoLabel.setText("");
                initialize();
            } catch (SQLException e) {
                infoLabel.setTextFill(Paint.valueOf("#64112B"));
                infoLabel.setText(e.getMessage());
            }
        });

        Image addImg = new Image(getClass().getResourceAsStream("..\\img\\plus_small.png"));
        addBtn.graphicProperty().setValue(new ImageView(addImg));

        Image deleteImg = new Image(getClass().getResourceAsStream("..\\img\\garbage_small.png"));
        deleteBtn.graphicProperty().setValue(new ImageView(deleteImg));

        Image returnImg = new Image(getClass().getResourceAsStream("..\\img\\return_small.png"));
        returnBtn.graphicProperty().setValue(new ImageView(returnImg));

    }

    public void setListOfNotes() {
        listOfNotes = dataBaseController.selectListOfNotesOfExams("*", "Exams_List", "ORDER BY Date_Of_Exam, Time_IN");
    }

    public TableView<NoteOfExam> createTable() {

        TableView<NoteOfExam> tableView = new TableView<>();

        TableColumn<NoteOfExam, String> subjectColumn = new TableColumn<>("Дисциплина");
        TableColumn<NoteOfExam, String> professorFIOColumn = new TableColumn<>("Преподаватель");
        TableColumn<NoteOfExam, LocalDate> dateColumn = new TableColumn<>("Дата");
        timeINColumn = new TableColumn<>("Время начала");
        timeOUTColumn = new TableColumn<>("Время завершения");
        TableColumn<NoteOfExam, Integer> groupIDColumn = new TableColumn<>("Группа");

        subjectColumn.setPrefWidth(220);
        professorFIOColumn.setPrefWidth(220);
        dateColumn.setPrefWidth(140);
        timeINColumn.setPrefWidth(200);
        timeOUTColumn.setPrefWidth(200);
        groupIDColumn.setPrefWidth(120);

        subjectColumn.setCellValueFactory(c ->
                c.getValue().subjectProperty());
        professorFIOColumn.setCellValueFactory(c ->
                c.getValue().professorsFIOProperty());
        dateColumn.setCellValueFactory(c ->
                c.getValue().dateOfExamProperty());
        timeINColumn.setCellValueFactory(c ->
                c.getValue().time_inProperty());
        timeOUTColumn.setCellValueFactory(c ->
                c.getValue().time_outProperty());
        groupIDColumn.setCellValueFactory(c ->
                c.getValue().groupIDProperty().asObject());

        tableView.setEditable(true);
        LocalTime time = LocalTime.of(8, 00);
        ObservableList<LocalTime> timeList = FXCollections.observableArrayList();
        while (time.getHour() != 18) {
            time = time.plusMinutes(15);
            timeList.add(time);
        }

        timeINColumn.setCellFactory(ComboBoxTableCell.forTableColumn(timeList));
        timeINColumn.setEditable(true);
        timeOUTColumn.setCellFactory(ComboBoxTableCell.forTableColumn(timeList));
        timeOUTColumn.setEditable(true);

        tableView.getColumns().addAll(subjectColumn, professorFIOColumn, dateColumn, timeINColumn, timeOUTColumn, groupIDColumn);
        return tableView;
    }

    public Node createElementsOnPage(int pageIndex) {
        infoLabel.setText("");
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, listOfNotes.size());

        notesTableView.setItems(FXCollections.observableArrayList(listOfNotes.subList(fromIndex, toIndex)));

        return notesTableView;
    }

    @FXML
    public void deleteNoteFromDB() throws SQLException {
        int selectedIndex = notesTableView.getSelectionModel().getSelectedIndex();
        System.out.println(selectedIndex);
        if (selectedIndex >= 0) {

            dataBaseController = new DataBaseController();
            Alert alertOfDeleting = new Alert(Alert.AlertType.CONFIRMATION);
            alertOfDeleting.setTitle("Удаление");
            alertOfDeleting.setHeaderText("Удаление информации из БАЗЫ ДАННЫХ");
            alertOfDeleting.setContentText("Внимание! Удалить запись из базы данных?");
            Optional<ButtonType> result = alertOfDeleting.showAndWait();
            if (result.get() == ButtonType.OK) {
                Alert alertInfo = new Alert(Alert.AlertType.INFORMATION);
                try {
                    dataBaseController.deleteFromDB("Exams_List", "ID", notesTableView.getItems().get(selectedIndex).getNoteID());
                    initialize();
                    alertInfo.setTitle("Информация удалена");
                    alertInfo.setHeaderText("");
                    alertInfo.setContentText("Запись удалена из базы данных");
                    alertInfo.showAndWait();
                    pagination.setPageFactory(this::createElementsOnPage);
                } catch (Exception e) {
                    alertInfo.setTitle("");
                    alertInfo.setHeaderText("");
                    alertInfo.setContentText(e.getMessage());
                    alertInfo.showAndWait();
                }
            }
        }
    }

    @FXML
    public void addNote() {
        try {
            mainApp.showAddNoteWindow();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public int getPageIndex() {
        return pagination.getCurrentPageIndex();
    }

    @FXML
    public void closeDialogStage() {
        dialogStage.close();
    }
}
