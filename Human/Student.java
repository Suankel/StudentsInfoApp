package MainPackage.Human;
import javafx.beans.property.*;

import java.time.LocalDate;

public class Student {


    private StringProperty firstName;
    private StringProperty lastName;
    private StringProperty patronymicName; // отчество
    private IntegerProperty age = new SimpleIntegerProperty();
    private Enum<Gender> gender;
    private IntegerProperty course = new SimpleIntegerProperty();
    private StringProperty speciality = new SimpleStringProperty();
    private IntegerProperty groupID = new SimpleIntegerProperty();
    private StringProperty faculty = new SimpleStringProperty();
    private IntegerProperty studentID = new SimpleIntegerProperty();
    private LocalDate dateOfBirth;

    public Student(String lastName, String firstName, String patronymicName) {
        this.lastName = new SimpleStringProperty(lastName);
        this.firstName = new SimpleStringProperty(firstName);
        this.patronymicName = new SimpleStringProperty(patronymicName);
    }

    public String getFirstName() {
        return firstName.get();
    }

    public StringProperty firstNameProperty() {
        return firstName;
    }

    public String getLastName() {
        return lastName.get();
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public Integer getAge() {
        return age.get();
    }


    public IntegerProperty ageProperty() {
        return age;
    }

    public int getCourse() {
        return course.get();
    }

    public IntegerProperty courseProperty() {
        return course;
    }

    public String getSpeciality() {
        return speciality.get();
    }

    public StringProperty specialityProperty() {
        return speciality;
    }

    public Integer getGroupID() {
        return groupID.get();
    }

    public IntegerProperty groupIDProperty() {
        return groupID;
    }

    public String getPatronymicName() {
        return patronymicName.get();
    }

    public StringProperty patronymicNameProperty() {
        return patronymicName;
    }

    public Enum<Gender> getGender() {
        return gender;
    }

    public String getFaculty() {
        return faculty.get();
    }

    public StringProperty facultyProperty() {
        return faculty;
    }

    public int getStudentID() {
        return studentID.get();
    }

    public IntegerProperty studentIDProperty() {
        return studentID;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getFIO() {
        return getLastName() + " " + getFirstName() + " " + getPatronymicName();
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public void setPatronymicName(String patronymicName) {
        this.patronymicName.set(patronymicName);
    }

    public void setAge(int age) {
        this.age.set(age);
    }

    public void setCourse(int course) {
        this.course.set(course);
    }

    public void setSpeciality(String speciality) {
        this.speciality.set(speciality);
    }

    public void setGender(Enum<Gender> gender) {
        this.gender = gender;
    }

    public void setFaculty(String faculty) {
        this.faculty.set(faculty);
    }

    public void setGroupID(int groupID) {
        this.groupID.set(groupID);
    }

    public void setStudentID(int studentID) {
        this.studentID.set(studentID);
    }

    public void setDateOfBirth(LocalDate birthDate) {
        this.dateOfBirth = birthDate;
    }

    public boolean compareStudents(Student student) {
        boolean comparison = false;

        if (getFIO().equalsIgnoreCase(student.getFIO()) && String.valueOf(groupID).equalsIgnoreCase(String.valueOf(student.groupID))) {
            comparison = true;
        }

        return comparison;
    }

}
