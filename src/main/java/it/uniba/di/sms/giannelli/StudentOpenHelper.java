package it.uniba.di.sms.giannelli;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import it.uniba.di.sms.giannelli.StudentContract.*;

import androidx.annotation.Nullable;

public class StudentOpenHelper extends SQLiteOpenHelper {

    //Nome Database
    public static final String DATABASE_NAME = "esami_giannelli.db";

    /*
     * Definizione Tabelle
     */

    //Utilita'
    private static final int DATABASE_VERSION = 1;

    /*
     * Creazione delle tabelle
     */

    private static final String STUDENT_TABLE_CREATE = createStudentTable();
    private static final String EXAM_TABLE_CREATE = createExamsTable();
    private static final String EXAM_DONE_TABLE_CREATE = createExamsDoneTable();

    StudentOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(STUDENT_TABLE_CREATE);
        db.execSQL(EXAM_TABLE_CREATE);
        db.execSQL(EXAM_DONE_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //metodo che restituisce una stringa
    //relativa al nome colonna e tipo valore
    private static String printColumnName(String name, String type, boolean finale) {
        if (!finale)
            return name + " " + type + ", ";
        else return name + " " + type + ");";
    }

    private static String createStudentTable() {
        return "CREATE TABLE " +
                StudentEntry.TABLE_NAME + " ("
                + printColumnName(StudentEntry.COLUMN_MATRICOLA, "INTEGER PRIMARY KEY AUTOINCREMENT", false)
                + printColumnName(StudentEntry.COLUMN_NAME, "TEXT", false)
                + printColumnName(StudentEntry.COLUMN_SURNAME, "TEXT", false)
                + printColumnName(StudentEntry.COLUMN_BIRTH, "DATE", false)
                + printColumnName(StudentEntry.COLUMN_WEBSITE, "TEXT", false)
                + printColumnName(StudentEntry.COLUMN_PHONE, "TEXT", false)
                + printColumnName(StudentEntry.COLUMN_EMAIL, "TEXT", false)
                + printColumnName(StudentEntry.COLUMN_GENDER, "TEXT", false)
                + printColumnName(StudentEntry.COLUMN_EXAMS_DONE, "TEXT", false)
                + printColumnName(StudentEntry.COLUMN_EXAMS_TODO, "TEXT", false)
                + printColumnName(StudentEntry.COLUMN_EXAMS_AVERAGE, "INTEGER", true)
                + ");";
    }

    private static String createExamsTable() {
        return "CREATE TABLE " +
                ExamEntry.TABLE_NAME + " ("
                + printColumnName(ExamEntry.COLUMN_ID, "INTEGER PRIMARY KEY AUTOINCREMENT", false)
                + printColumnName(ExamEntry.COLUMN_NAME, "TEXT", false)
                + printColumnName(ExamEntry.COLUMN_YEAR, "TEXT", false)
                + printColumnName(ExamEntry.COLUMN_SEMESTER, "TEXT", false)
                + printColumnName(ExamEntry.COLUMN_CFU, "TEXT", true)
                + ");";
    }

    private static String createExamsDoneTable() {
        return "CREATE TABLE " +
                ExamDoneEntry.TABLE_NAME + " ("
                + printColumnName(ExamDoneEntry.COLUMN_ID, "INTEGER PRIMARY KEY AUTOINCREMENT", false)
                + printColumnName(ExamDoneEntry.COLUMN_NAME, "TEXT", false)
                + printColumnName(ExamDoneEntry.COLUMN_DATE, "TEXT", false)
                + printColumnName(ExamDoneEntry.COLUMN_GRADE, "TEXT", true)
                + ");";
    }
}