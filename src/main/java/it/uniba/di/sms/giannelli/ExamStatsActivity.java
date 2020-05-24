package it.uniba.di.sms.giannelli;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

import it.uniba.di.sms.giannelli.StudentContract.*;

public class ExamStatsActivity extends AppCompatActivity {

    Toolbar statsToolbar;
    TextView mathAverage, pondAverage, finalGrade;
    private static final String DOT = ".";
    private static final String COLON = ", ";
    private static final String SELECT = "SELECT ";
    private static final String AS = " AS ";
    private static final String TABLE_PESO_ESAMI = "peso_esami";
    private static final String FROM = " FROM ";
    private static final String INNER_JOIN = " INNER JOIN ";
    private static final String ON = " ON ";
    private static final String QUERY = SELECT +
            ExamDoneEntry.COLUMN_GRADE +
            COLON +
            ExamEntry.COLUMN_CFU +
            FROM +
            ExamEntry.TABLE_NAME + INNER_JOIN + ExamDoneEntry.TABLE_NAME +
            ON +
            ExamEntry.TABLE_NAME + DOT + ExamEntry.COLUMN_NAME
            + "=" +
            ExamDoneEntry.TABLE_NAME + DOT + ExamDoneEntry.COLUMN_NAME;

    String mediaPonderata, mediaAritmetica, votoFinale;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_stats);

        //TextViews
        mathAverage = (TextView) findViewById(R.id.tvMediaAritmetica);
        pondAverage = (TextView) findViewById(R.id.tvMediaPonderata);
        finalGrade = (TextView) findViewById(R.id.tvVotoDiLaurea);

        //Toolbar
        statsToolbar = (Toolbar) findViewById(R.id.toolbarStats);
        statsToolbar.setTitle(getString(R.string.txtStats));
        statsToolbar.setSubtitle("Giannelli Fabio");
        statsToolbar.setLogo(R.mipmap.ic_launcher_round);
        setSupportActionBar(statsToolbar);

        //Ottenimento dati da DB
        Cursor c = getDataFromDb();

        //creo liste voti e cfu
        ArrayList<Integer> gradesList = getListFromColumn(ExamDoneEntry.COLUMN_GRADE, c);
        ArrayList<Integer> cfuList = getListFromColumn(ExamEntry.COLUMN_CFU, c);

        //calcolo medie
        String mediaPonderata = calculateMediaPonderata(cfuList, gradesList);
        String mediaAritmetica = calculateMediaAritmetica(gradesList);
        String votoFinale = calculateVotoFinale(mediaPonderata, mediaAritmetica);

        //assegno valori alle textview
        mathAverage.setText(getString(R.string.txtTvMediaAritmetica, mediaAritmetica));
        pondAverage.setText(getString(R.string.txtTvMediaPonderata, mediaPonderata));
        finalGrade.setText(getString(R.string.txtTvVotoDiLaurea, votoFinale));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bluetooth_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        //Verifico che sia stata cliccato l'action menu corrispondente
        if(id == R.id.bluetooth_menu) {
            goToBluetoothActivity();
        }
        return false;
    }

    private void goToBluetoothActivity() {
        Intent goToBluetooth = new Intent(this, BluetoothActivity.class);
        goToBluetooth.putExtra("Average", getMediaPonderata());
        startActivity(goToBluetooth);
    }

    private Cursor getDataFromDb() {

        //database in lettura
        StudentOpenHelper studentOpenHelper = new StudentOpenHelper(this);
        SQLiteDatabase db = studentOpenHelper.getReadableDatabase();

        Cursor c = db.rawQuery(QUERY, null);

        return c;
    }

    public String calculateMediaPonderata(ArrayList<Integer> cfuList, ArrayList<Integer> gradesList) {

        /*
        La media ponderata è ottenibile mediante la divisione di due quantita':
        1) la somma delle moltiplicazioni dei voti coi rispettivi cfu
        2) la somma dei cfu
        Questo metodo si occupa di restituire la media ponderata
         */

        //Iterators
        Iterator cfuIterator = cfuList.iterator();
        Iterator gradesIterator = gradesList.iterator();

        //Somma delle moltiplicazioni fra voti e cfu corrispondenti
        Integer sum = 0;
        //Somma dei cfu
        Integer sumCfu = 0;

        while(gradesIterator.hasNext()) {

            //ottengo voto e cfu singolo
            int grade = (int) gradesIterator.next();
            int cfu = (int) cfuIterator.next();

            //costruisco l'addendo che sara'
            //il dividendo dell'operazione finale
            int addend = (grade * cfu);
            sum += addend;

            //costruisco l'addendo che sara'
            //divisore dell'operazione finale
            sumCfu += cfu;
        }

        //salvo il valore temporaneo restituito
        double mediaPonderata = (double) (sum / sumCfu);

        return Double.valueOf(mediaPonderata).toString();

    }

    public ArrayList<Integer> getListFromColumn(String columnName, Cursor c) {

        c.moveToFirst();
        ArrayList<Integer> columnData = new ArrayList<>();
        do {
            columnData.add(Integer.parseInt(c.getString(c.getColumnIndex(columnName))));
        } while(c.moveToNext());
        return columnData;
    }

    public String calculateMediaAritmetica(ArrayList<Integer> grades) {
        Iterator i = grades.iterator();
        Integer sum = 0;
        while(i.hasNext()) {
            sum += (Integer) i.next();
        }
        Double mediaAritmetica = (double) (sum / grades.size());
        return mediaAritmetica.toString();
    }

    public String calculateVotoFinale(String mediaPonderata, String mediaAritmetica) {

        Double mPond = Double.parseDouble(mediaPonderata);
        Double mArit = Double.parseDouble(mediaAritmetica);

        //Per il troncamento delle cifre decimali
        DecimalFormat df = new DecimalFormat("#.##");
        double finalGrade = 0;

        //calcolo voto di laurea
        if(mPond > mArit) {
            finalGrade = (mPond / 3) * 11;
            //Stringa corrispondente al voto finale
            return df.format(finalGrade);
        } else {
            finalGrade = (mArit / 3) * 11;
            //Stringa corrispondente al voto finale
            return df.format(finalGrade);
        }
    }

    public String getMediaPonderata() {
        return this.mediaPonderata;
    }

    public String getMediaAritmetica() {
        return this.mediaPonderata;
    }

    public String getVotoFinale() {
        return this.mediaPonderata;
    }
}