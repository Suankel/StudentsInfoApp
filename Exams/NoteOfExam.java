package MainPackage.Exams;

import javafx.beans.property.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class NoteOfExam {

    private IntegerProperty NoteID = new SimpleIntegerProperty();
    private StringProperty subject = new SimpleStringProperty();
    private StringProperty professorsFIO = new SimpleStringProperty();
    private ObjectProperty<LocalDate> DateOfExam;
    private ObjectProperty<LocalTime> time_in;
    private ObjectProperty<LocalTime> time_out;
    private IntegerProperty groupID = new SimpleIntegerProperty();

    public NoteOfExam(LocalDate date, LocalTime time1, LocalTime time2) {
        DateOfExam = new SimpleObjectProperty<>(date);
        time_in = new SimpleObjectProperty<>(time1);
        time_out = new SimpleObjectProperty<>(time2);
    }

    public int getNoteID() {
        return NoteID.get();
    }

    public IntegerProperty noteIDProperty() {
        return NoteID;
    }

    public String getSubject() {
        return subject.get();
    }

    public StringProperty subjectProperty() {
        return subject;
    }

    public String getProfessorsFIO() {
        return professorsFIO.get();
    }

    public StringProperty professorsFIOProperty() {
        return professorsFIO;
    }

    public LocalDate getDateOfExam() {
        return DateOfExam.get();
    }

    public ObjectProperty<LocalDate> dateOfExamProperty() {
        return DateOfExam;
    }

    public ObjectProperty<LocalTime> time_inProperty() {
        return time_in;
    }

    public LocalTime getTime_in(){
        return time_in.get();
    }

    public ObjectProperty<LocalTime> time_outProperty() {
        return time_out;
    }

    public LocalTime getTime_out() {
        return time_out.get();
    }

    public int getGroupID() {
        return groupID.get();
    }

    public IntegerProperty groupIDProperty() {
        return groupID;
    }

    public void setNoteID(int noteID) {
        this.NoteID.set(noteID);
    }

    public void setGroupID(int groupID) {
        this.groupID.set(groupID);
    }

    public void setSubject(String subject) {
        this.subject.set(subject);
    }

    public void setProfessorsFIO(String professorsFIO) {
        this.professorsFIO.set(professorsFIO);
    }

    public void setTime_in(LocalTime time1) {
        time_in.set(time1);
    }

    public void setTime_out(LocalTime time2) {
        time_out.set(time2);
    }

    public void setDateOfExam(LocalDate date) {
        DateOfExam.set(date);
    }
}
