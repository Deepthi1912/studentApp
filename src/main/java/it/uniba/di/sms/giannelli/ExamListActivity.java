package it.uniba.di.sms.giannelli;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import it.uniba.di.sms.giannelli.StudentContract.*;

public class ExamListActivity extends AppCompatActivity {

    private Toolbar examListToolbar;

    private final String ANNO = " anno";
    private final String SEMESTRE = " semestre";
    private final String CFU = " CFU";
    private final String CARDINALITY = "°";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_list);

        examListToolbar = (Toolbar) findViewById(R.id.toolbarExamList);
        examListToolbar.setTitle(getString(R.string.txtExamsList));
        examListToolbar.setSubtitle(getString(R.string.txtCorso));
        examListToolbar.setLogo(R.mipmap.ic_launcher_round);
        setSupportActionBar(examListToolbar);

        String[] exams = readExamsFromDb();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.exam_row, exams);
        ListView listView = (ListView) findViewById(R.id.examList);
        listView.setAdapter(adapter);
    }

    private String[] readExamsFromDb() {
        SQLiteOpenHelper examsOpenHelper = new StudentOpenHelper(this);
        SQLiteDatabase db = examsOpenHelper.getReadableDatabase();

        Cursor cursor = db.query(ExamEntry.TABLE_NAME,
                new String[] {ExamEntry.COLUMN_NAME,
                        ExamEntry.COLUMN_CFU,
                        ExamEntry.COLUMN_YEAR,
                        ExamEntry.COLUMN_SEMESTER},
                null,
                null,
                null,
                null,
                null);

        //Porto il cursor in prima posizione
        cursor.moveToFirst();

        //Creo e riempio la lista di esami con
        //elementi stringa acquisiti dal cursor
        ArrayList<String> exams = new ArrayList<>();
        do {
            String tuple = getTuple(cursor);
            exams.add(tuple);
        } while(cursor.moveToNext());

        //converto la lista in array
        String[] examsArray = new String[exams.size()];
        examsArray = exams.toArray(examsArray);

        return examsArray;
    }

    private String getTuple(Cursor c) {

        //Estrazione dati
        String examName = c.getString(c.getColumnIndex(ExamEntry.COLUMN_NAME));
        String examCFU = c.getString(c.getColumnIndex(ExamEntry.COLUMN_CFU));
        String examYear = c.getString(c.getColumnIndex(ExamEntry.COLUMN_YEAR));
        String examSemester = c.getString(c.getColumnIndex(ExamEntry.COLUMN_SEMESTER));

        //Costruisco la stringa
        StringBuilder sb = new StringBuilder();
        sb.append(examName);
        sb.append("\n");
        sb.append(examCFU);
        sb.append(CFU);
        sb.append("\n");
        sb.append(examYear);
        sb.append(CARDINALITY);
        sb.append(ANNO);
        sb.append("\n");
        sb.append(examSemester);
        sb.append(CARDINALITY);
        sb.append(SEMESTRE);

        //tupla
        return sb.toString();
    }
}
