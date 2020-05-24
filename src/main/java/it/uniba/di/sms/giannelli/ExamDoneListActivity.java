package it.uniba.di.sms.giannelli;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import it.uniba.di.sms.giannelli.StudentContract.*;

public class ExamDoneListActivity extends AppCompatActivity {

    private final String DATA = "Data: ";
    private final String VOTO = " Voto conseguito: ";
    Toolbar myExamDoneListToolbar;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.exam_stats_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        //Verifico che sia stata cliccato l'action menu corrispondente
        if(id == R.id.stats) {
            goToExamStatsActivity();
        }
        return false;
    }

    private void goToExamStatsActivity() {
        Intent goToExamsStats = new Intent(this, ExamStatsActivity.class);
        startActivity(goToExamsStats);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_done_list);
        myExamDoneListToolbar = (Toolbar) findViewById(R.id.toolbarExamDoneList);
        myExamDoneListToolbar.setTitle(getString(R.string.txtEsamiSvolti));
        myExamDoneListToolbar.setSubtitle("Giannelli Fabio");
        myExamDoneListToolbar.setLogo(R.mipmap.ic_launcher_round);
        setSupportActionBar(myExamDoneListToolbar);
        String[] examsDone = readExamsFromDb();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.exam_row, examsDone);
        ListView listView = (ListView) findViewById(R.id.examDoneList);
        listView.setAdapter(adapter);
    }

    private String[] readExamsFromDb() {

        SQLiteOpenHelper examsDoneOpenHelper = new StudentOpenHelper(this);
        SQLiteDatabase db = examsDoneOpenHelper.getReadableDatabase();

        Cursor cursor = db.query(ExamDoneEntry.TABLE_NAME,
                new String[] {ExamDoneEntry.COLUMN_NAME,
                        ExamDoneEntry.COLUMN_DATE,
                        ExamDoneEntry.COLUMN_GRADE},
                null,
                null,
                null,
                null,
                null);

        //Porto il cursor in prima posizione
        cursor.moveToFirst();

        //Creo e riempio la lista di esami con
        //elementi stringa acquisiti dal cursor
        ArrayList<String> examsDone = new ArrayList<>();
        do {
            String tuple = getTuple(cursor);
            examsDone.add(tuple);
        } while(cursor.moveToNext());

        //converto la lista in array
        String[] examsDoneArray = new String[examsDone.size()];
        examsDoneArray = examsDone.toArray(examsDoneArray);

        return examsDoneArray;
    }

    private String getTuple(Cursor c) {

        //Estrazione dati
        String examName = c.getString(c.getColumnIndex(ExamDoneEntry.COLUMN_NAME));
        String examDate = c.getString(c.getColumnIndex(ExamDoneEntry.COLUMN_DATE));
        String examGrade = c.getString(c.getColumnIndex(ExamDoneEntry.COLUMN_GRADE));

        //Costruisco la stringa
        StringBuilder sb = new StringBuilder();
        sb.append(examName);
        sb.append("\n");
        sb.append(DATA);
        sb.append(examDate);
        sb.append("\n");
        sb.append(VOTO);
        sb.append(examGrade);

        //tupla
        return sb.toString();
    }
}

