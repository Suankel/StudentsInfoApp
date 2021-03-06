package MainPackage;

import MainPackage.Exams.AddNoteWindowController;
import MainPackage.Exams.ExamsListController;
import MainPackage.StudentOverView.AddStudentWindowController;
import MainPackage.StudentOverView.EditStudentInfoController;
import MainPackage.StudentOverView.StudentsOverViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.*;


public class Main extends Application {

    private BorderPane parentRoot;
    private Stage primaryStage;
    private StudentsOverViewController studentsOverViewController;
    private ExamsListController examsListController;

    public Main() {

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Выберите требуемую информацию");
        primaryStage.setResizable(false);
        initializeRootLayout();
        showAuthorizationWindow();
    }

    public void initializeRootLayout() throws IOException {

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("RootLayout.fxml"));
        parentRoot = loader.load();
        Scene scene = new Scene(parentRoot);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void showAuthorizationWindow() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("AuthorizationWindow.fxml"));
            AnchorPane authorizationPane = loader.load();
            parentRoot.setCenter(authorizationPane);
            AuthorizationWindowController controller = loader.getController();
            controller.setMainApp(this);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void showStudentsOverView() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Main.class.getResource("StudentOverView/StudentsOverView.fxml"));
            AnchorPane anchorPane = fxmlLoader.load();
            Stage dialogStage = new Stage();
            dialogStage.setResizable(false);
            dialogStage.setTitle("Информация о студентах");
            Scene scene = new Scene(anchorPane);
            dialogStage.setScene(scene);
            studentsOverViewController = fxmlLoader.getController();
            studentsOverViewController.setDialogStage(dialogStage);
            studentsOverViewController.setMainApp(this);
            dialogStage.showAndWait();

        } catch (Exception e) {
            System.out.println("Ааааааа!!!!! Не открылось основное окно со списком студентов");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void showAddStudentWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Main.class.getResource("StudentOverView/AddStudentWindow.fxml"));
            AnchorPane anchorPane = fxmlLoader.load();
            Stage dialogStage = new Stage();
            dialogStage.setResizable(false);
            dialogStage.setTitle("Добавить студента");
            Scene scene = new Scene(anchorPane);
            dialogStage.setScene(scene);
            AddStudentWindowController controller = fxmlLoader.getController();
            controller.setDialogStage(dialogStage);
            controller.setMainApp(this);
            dialogStage.showAndWait();

        } catch (Exception e) {
            System.out.println("Ааааааа!!!!! Не открылось окно добавления студента");
            System.out.println(e.getMessage());
        }
    }

    public void showEditStudentInfo() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Main.class.getResource("StudentOverView/EditStudentInfo.fxml"));
            AnchorPane anchorPane = fxmlLoader.load();
            Stage dialogStage = new Stage();
            dialogStage.setResizable(false);
            dialogStage.setTitle("Изменить информацию о студенте");
            Scene scene = new Scene(anchorPane);
            dialogStage.setScene(scene);
            EditStudentInfoController controller = fxmlLoader.getController();
            controller.setDialogStage(dialogStage);
            controller.setMainApp(this);
            dialogStage.showAndWait();

        } catch (Exception e) {
            System.out.println("Ааааааа!!!!! Не открылось окно редактирования данных о студенте");
            System.out.println(e.getMessage());
        }
    }

    public void showExamsWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Main.class.getResource("Exams/ExamsList.fxml"));
            AnchorPane anchorPane = fxmlLoader.load();
            Stage dialogStage = new Stage();
            dialogStage.setResizable(false);
            dialogStage.setTitle("Экзамены");
            Scene scene = new Scene(anchorPane);
            dialogStage.setScene(scene);
            examsListController = fxmlLoader.getController();
            examsListController.setDialogStage(dialogStage);
            examsListController.setMainApp(this);
            dialogStage.showAndWait();
        } catch (Exception e) {
            System.out.println("Ааааааа!!!!! Не открылось окно экзаменов");
            System.out.println(e.getMessage());

        }
    }

    public void showAddNoteWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Main.class.getResource("Exams/AddNoteWindow.fxml"));
            AnchorPane anchorPane = fxmlLoader.load();
            Stage dialogStage = new Stage();
            dialogStage.setResizable(false);
            dialogStage.setTitle("Добавить запись");
            Scene scene = new Scene(anchorPane);
            dialogStage.setScene(scene);
            AddNoteWindowController addNoteWindowController = fxmlLoader.getController();
            addNoteWindowController.setDialogStage(dialogStage);
            addNoteWindowController.setMainApp(this);
            dialogStage.showAndWait();
        } catch (Exception e) {
            System.out.println("Ааааааа!!!!! Не открылось окно добавления записи");
            System.out.println(e.getMessage());

        }
    }

    public StudentsOverViewController getStudentsOverViewController() {
        return studentsOverViewController;
    }

    public ExamsListController getExamsListController() {
        return examsListController;
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {

        launch(args);
    }
}
