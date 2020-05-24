package it.uniba.di.sms.giannelli;

import android.provider.BaseColumns;

public abstract class StudentContract {

    //Istanzio costruttore vuoto
    //In questo modo prevengo azioni accidentali come ad esempio
    //l'implementazione e l'uso di esso
    StudentContract() {}

    public static final class StudentEntry implements BaseColumns {
        //Tabella Studenti
        public static final String TABLE_NAME = "studente";
        public static final String COLUMN_MATRICOLA = "matricola";
        public static final String COLUMN_NAME = "nome";
        public static final String COLUMN_SURNAME = "cognome";
        public static final String COLUMN_BIRTH = "data_di_nascita";
        public static final String COLUMN_WEBSITE = "sito_web";
        public static final String COLUMN_PHONE = "cellulare";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_GENDER = "sesso";
        public static final String COLUMN_EXAMS_DONE = "esami_svolti";
        public static final String COLUMN_EXAMS_TODO = "esami_da_fare";
        public static final String COLUMN_EXAMS_AVERAGE = "media";
    }

    public static final class ExamEntry implements BaseColumns {
        //Tabella Esame
        public static final String TABLE_NAME = "esame";
        public static final String COLUMN_ID = "id_esame";
        public static final String COLUMN_NAME = "nome";
        public static final String COLUMN_YEAR = "anno";
        public static final String COLUMN_SEMESTER = "semestre";
        public static final String COLUMN_CFU = "cfu";
    }

    public static final class ExamDoneEntry implements BaseColumns {
        //Tabella Esami Svolti
        public static final String TABLE_NAME = "esami_sostenuti";
        public static final String COLUMN_ID = "id_esame_sostenuto";
        public static final String COLUMN_NAME = "nome";
        public static final String COLUMN_DATE = "data";
        public static final String COLUMN_GRADE = "voto";
    }
}
