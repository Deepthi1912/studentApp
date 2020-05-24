package it.uniba.di.sms.giannelli;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private final String LOG = "Giannelli.MainActivity";
    private Button buttonApply;
    private EditText editableSubject, editableExercisesNumber;
    private Toolbar tlbMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Creazione Activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(LOG, "- onCreate() - l'Activity è stata creata");

        //Pulsanti e risorse
        buttonApply = (Button) findViewById(R.id.btnApply);
        editableSubject = (EditText) findViewById(R.id.editTextChooseSubject);
        editableExercisesNumber = (EditText) findViewById(R.id.editTextExNumber);
        tlbMain = (Toolbar) findViewById(R.id.tlbMain);
        tlbMain.setTitle(getString(R.string.txtMainActivity));
        setSupportActionBar(tlbMain);

        buttonApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Inserisce il testo digitato nell'editText
                //all'interno della risorsa txtHelloWorld
                String insertedSubject = editableSubject.getText().toString();
                String insertedExercisesNumber = editableExercisesNumber.getText().toString();
                goToSecondActivity(insertedSubject, insertedExercisesNumber);
            }
        });
    }

    protected void goToSecondActivity(String insertedSubject, String insertedExercisesNumber) {
        Intent secondActivity = new Intent(this, SecondActivity.class);
        secondActivity.putExtra("insertedSubject", insertedSubject);
        secondActivity.putExtra("insertedExercisesNumber", insertedExercisesNumber);
        startActivity(secondActivity);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG, "- onStart() - l'Activity è in fase di avvio");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG, "- onResume() - l'Activity torna ad essere visibile");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG, "- onPause() - l'Activity va in background, ancora visibile");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LOG, "- onStop() - l'Activity non è più visibile");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(LOG, "- onRestart() - l'Activity sta per riprendere l'esecuzione");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LOG, "- onDestroy() - l'Activity è stata distrutta");
    }
}
