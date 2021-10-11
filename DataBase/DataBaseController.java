package MainPackage.DataBase;

import MainPackage.Human.Gender;
import MainPackage.Human.Student;
import MainPackage.Exams.NoteOfExam;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DataBaseController {
    private Connection dbConnection;

    public DataBaseController () throws SQLException {

        try {

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            System.out.println("Подключаемся...");
            String url = "jdbc:sqlserver://localhost:1433;databaseName=Education_System";
            String user = "sa";
            String password = "password";
            dbConnection = DriverManager.getConnection(url,user,password);
            System.out.println("Подключение успешно!");

        } catch (SQLException throwables) {
            System.out.println("Отсутствует контакт с базой");
            dbConnection.close();
            throwables.printStackTrace();
        } catch (ClassNotFoundException throwables) {
            System.out.println("Класс не найден");
            dbConnection.close();
            throwables.printStackTrace();
        }

    }

    public ObservableList<String> selectForComboBox(String column, String table, String additionToQuery) {
        Statement statement;
        ObservableList<String> list = FXCollections.observableArrayList();

        try {
            statement = dbConnection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT " + column + " FROM " + table + " " + additionToQuery);

            while(resultSet.next()) {
                list.add(resultSet.getString(1));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return list;
    }

    public ObservableList<Student> selectListOfStudents(String column, String table, String additionToQuery) {
        Statement statement;
        ObservableList<Student> list = FXCollections.observableArrayList();
        Student student = null;
        try {
            statement = dbConnection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT " + column + " FROM " + table + " " + additionToQuery);

            while(resultSet.next()) {

                String lastName = resultSet.getString("Last_Name");
                String firstName = resultSet.getString("First_Name");
                String patronymicName = resultSet.getString("Patronymic_Name");
                student = new Student(lastName, firstName, patronymicName);
                int studentID = Integer.parseInt(resultSet.getString("ID"));
                student.setStudentID(studentID);
                int age = Integer.parseInt(resultSet.getString("Age"));
                student.setAge(age);

                String speciality = this.printSmthByParameter("DISTINCT Name", "Specialities",
                        "JOIN Students ON Specialities.ID = Students.Speciality_ID WHERE Specialities.ID = ", resultSet.getString("Speciality_ID"));
                student.setSpeciality(speciality);

                String faculty = this.printSmthByParameter("DISTINCT Name_Of_Faculty", "Specialities",
                        "JOIN Students ON Specialities.ID = Students.Speciality_ID WHERE Specialities.ID = ", resultSet.getString("Speciality_ID"));
                student.setFaculty(faculty);

                int groupID = Integer.parseInt(resultSet.getString("Group_ID"));
                student.setGroupID(groupID);
                int course = Integer.parseInt(resultSet.getString("Course"));
                student.setCourse(course);

                switch (resultSet.getString("Sex")) {
                    case "м":
                        student.setGender(Gender.MALE);
                        break;
                    case "ж":
                        student.setGender(Gender.FEMALE);
                        break;
                }

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate dateOfBirth = LocalDate.parse(resultSet.getString("Date_of_birth"), formatter);
                student.setDateOfBirth(dateOfBirth);

                list.add(student);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return list;
    }

    public ObservableList<NoteOfExam> selectListOfNotesOfExams(String column, String table, String additionToQuery) {
        Statement statement;
        ObservableList<NoteOfExam> list = FXCollections.observableArrayList();
        NoteOfExam noteOfExam;
        try {
            statement = dbConnection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT " + column + " FROM " + table + " " + additionToQuery);

            while(resultSet.next()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate dateOfExam = LocalDate.parse(resultSet.getString("Date_Of_Exam"), formatter);
                LocalTime time_in =  LocalTime.parse((resultSet.getString("Time_IN")));
                LocalTime time_out = LocalTime.parse((resultSet.getString("Time_OUT")));
                noteOfExam = new NoteOfExam(dateOfExam, time_in, time_out);

                int noteID = Integer.parseInt(resultSet.getString("ID"));
                int groupID = Integer.parseInt(resultSet.getString("Group_ID"));

                String subject = this.printSmthByParameter("DISTINCT Name", "Subjects",
                        "WHERE ID = ", resultSet.getString("Subject_ID"));
                String professorFIO = this.printSmthByParameter("DISTINCT FIO", "Professors",
                        "WHERE ID = ", resultSet.getString("Professor_ID"));
                noteOfExam.setNoteID(noteID);
                noteOfExam.setSubject(subject);
                noteOfExam.setProfessorsFIO(professorFIO);
                noteOfExam.setGroupID(groupID);
                list.add(noteOfExam);

            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return list;
    }

    public String printSmthByParameter(String column, String table, String additionToQuery, String parameter) {
        Statement statement;
        String name = null;
        try {
            statement = dbConnection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT " + column + " FROM " + table + " " + additionToQuery + parameter);
            while(resultSet.next()) {
                name = resultSet.getString(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (NullPointerException throwables) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Данная информация отсутствует");
            alert.setContentText("Отсутствует информация " + name + " по данному запросу ");
        }

        return name;
    }

    public void deleteFromDB(String table, String parameter, int value) {
        Statement statement;
        try {
            statement = dbConnection.createStatement();
            statement.executeUpdate("DELETE FROM " + table + " WHERE " + parameter + " = " + value);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void addStudentToDB(Student student) {

        char sexChar = '1';
        switch (student.getGender().toString()) {
            case "Мужской":
                sexChar = 'м';
                break;
            case "Женский":
                sexChar = 'ж';
                break;
        }

        try(CallableStatement cstmt = dbConnection.prepareCall("{call addStudents(?,?,?,?,?,?)}")) {

            cstmt.setString(1, student.getLastName());
            cstmt.setString(2, student.getFirstName());
            cstmt.setString(3, student.getPatronymicName());
            cstmt.setInt(4, student.getGroupID());
            cstmt.setString(5, String.valueOf(sexChar));
            cstmt.setDate(6, Date.valueOf(student.getDateOfBirth()));
            cstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addNoteToDB(NoteOfExam note) throws SQLException {

        try(CallableStatement cstmt = dbConnection.prepareCall("{call add_new_note_exams(?,?,?,?,?,?)}")) {
            int subject = Integer.parseInt(this.printSmthByParameter(
                    "ID", "Subjects", "WHERE Name = '", note.getSubject() + "'"));
            int professorFIO = Integer.parseInt(this.printSmthByParameter(
                    "ID", "Professors", "WHERE FIO = '", note.getProfessorsFIO() + "'"));

            cstmt.setInt(1, subject);
            cstmt.setInt(2, professorFIO);
            cstmt.setDate(3, Date.valueOf(note.getDateOfExam()));
            cstmt.setTime(4, Time.valueOf(note.getTime_in()));
            cstmt.setTime(5, Time.valueOf(note.getTime_out()));
            cstmt.setInt(6, note.getGroupID());
            cstmt.executeUpdate();

        } catch (SQLException e) {
            throw e;
        }
    }

    public void updateStudentInDB(String lastName, String firstName, String patronymicName, int group_ID, String sex, LocalDate date, int Student_ID) {
        char sexChar = '1';
        switch (sex) {
            case "Мужской":
                sexChar = 'м';
                break;
            case "Женский":
                sexChar = 'ж';
                break;
        }

        try(CallableStatement cstmt = dbConnection.prepareCall("{call updateStudents(?,?,?,?,?,?,?)}")) {

            cstmt.setString(1, lastName);
            cstmt.setString(2, firstName);
            cstmt.setString(3, patronymicName);
            cstmt.setInt(4, group_ID);
            cstmt.setString(5, String.valueOf(sexChar));
            cstmt.setDate(6, Date.valueOf(date));
            cstmt.setInt(7, Student_ID);
            cstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTimeInExams(NoteOfExam note, LocalTime time_in) throws SQLException {

        try(CallableStatement cstmt = dbConnection.prepareCall("{call update_timeIN_in_note(?,?,?,?,?,?)}")) {

            int professorFIO = Integer.parseInt(this.printSmthByParameter(
                    "ID", "Professors", "WHERE FIO = '", note.getProfessorsFIO() + "'"));

            cstmt.setInt(1, note.getNoteID());
            cstmt.setInt(2, professorFIO);
            cstmt.setDate(3, Date.valueOf(note.getDateOfExam()));
            cstmt.setTime(4, Time.valueOf(time_in));
            cstmt.setTime(5, Time.valueOf(note.getTime_out()));
            cstmt.setInt(6, note.getGroupID());
            cstmt.executeUpdate();

        } catch (SQLException e) {
            throw e;
        }

    }

    public void updateTimeOutExams(NoteOfExam note, LocalTime time_out) throws SQLException {

        try(CallableStatement cstmt = dbConnection.prepareCall("{call update_timeOUT_in_note(?,?,?,?,?,?)}")) {

            int professorFIO = Integer.parseInt(this.printSmthByParameter(
                    "ID", "Professors", "WHERE FIO = '", note.getProfessorsFIO() + "'"));

            cstmt.setInt(1, note.getNoteID());
            cstmt.setInt(2, professorFIO);
            cstmt.setDate(3, Date.valueOf(note.getDateOfExam()));
            cstmt.setTime(4, Time.valueOf(note.getTime_in()));
            cstmt.setTime(5, Time.valueOf(time_out));
            cstmt.setInt(6, note.getGroupID());
            cstmt.executeUpdate();

        } catch (SQLException e) {
            throw e;
        }

    }

}
