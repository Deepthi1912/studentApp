package it.uniba.di.sms.giannelli;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.DecimalFormat;

public class SecondActivity extends AppCompatActivity {

    private final String LOG = "Giannelli.SecondActivity";
    private Button buttonBack;
    private EditText editableTextWrTskGrade, editableTextOralTskGrade,
            editableTextExerTskGrade, editableTextFinalTskGrade;
    private TextView textSubjectGrade, textExerTsk;
    private SeekBar seekbarWrTskGrade, seekbarOralTskGrade, seekbarExerTskGrade;

    //Voti delle prove
    private int currentWrTskGrade, currentOralTskGrade;
    double currentExerTskGrade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        /*
         * Pulsanti e risorse
         */

        //TextViews
        textSubjectGrade = (TextView) findViewById(R.id.textViewSubjectGrade);
        textExerTsk = (TextView) findViewById(R.id.textViewExerTsk);

        //EditTexts
        editableTextWrTskGrade = (EditText) findViewById(R.id.editTextWrTskGrade);
        editableTextOralTskGrade = (EditText) findViewById(R.id.editTextOralTskGrade);
        editableTextExerTskGrade = (EditText) findViewById(R.id.editTextExerTskGrade);
        editableTextFinalTskGrade = (EditText) findViewById(R.id.editTextFinalTskGrade);

        //Seekbars
        seekbarWrTskGrade = (SeekBar) findViewById(R.id.skbWrTskGrade);
        seekbarOralTskGrade = (SeekBar) findViewById(R.id.skbOralTskGrade);
        seekbarExerTskGrade = (SeekBar) findViewById(R.id.skbExerTskGrade);

        //Pulsanti
        buttonBack = (Button) findViewById(R.id.btnBack);

        //intero corrispondente al numero di esercitazioni previste
        int exercisesNumber = 0;

        //Inizializzo voti a 0
        currentWrTskGrade = 0;
        currentOralTskGrade = 0;
        currentExerTskGrade = 0;

        /*
         * Esecuzione
         */

        //Parametri passati tramite Intent esplicito
        String subject = getIntent().getStringExtra("insertedSubject");
        String insertedExercisesNumber = getIntent().getStringExtra("insertedExercisesNumber");
        exercisesNumber = Integer.parseInt(insertedExercisesNumber);

        //Inizializzazione Resources per l'uso dei plurals
        Resources res = getResources();
        //Stringa modificata in virtu' del plural
        String exercisesPl = res.getQuantityString(R.plurals.numberOfExercises, exercisesNumber, exercisesNumber);

        //Costruzione HelloWorld
        String title = getString(R.string.txtSubjectGrade, subject);
        @SuppressLint("StringFormatMatches")
        String exercises = getString(R.string.txtExerTsk, insertedExercisesNumber, exercisesPl);

        //Settaggio testi personalizzati
        textSubjectGrade.setText(title);
        textExerTsk.setText(exercises);

        //Settaggio livelli seekbar:
        //la discretizzazione dei punti della seekbar
        //è relazionato dalla scelta dell'utente
        seekbarExerTskGrade.setMax(exercisesNumber);

        //Imposto pulsante indietro
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMainActivity();
            }
        });

        //Listener della seekbar del voto scritto
        SeekBar.OnSeekBarChangeListener customWrTskSkbListener =
                new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentWrTskGrade = progress;
                editableTextWrTskGrade.setText("" + seekBar.getProgress());

                //Aggiorno l'editText relativo al voto finale
                updateFinalVote();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        };

        //Listener della seekbar del voto orale
        SeekBar.OnSeekBarChangeListener customOralTskSkbListener =
                new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentOralTskGrade = progress;
                editableTextOralTskGrade.setText("" + seekBar.getProgress());

                //Aggiorno l'editText relativo al voto finale
                updateFinalVote();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        };

        //Listener della seekbar del voto delle esercitazioni
        final int finalExercisesNumber = exercisesNumber;
        SeekBar.OnSeekBarChangeListener customExerTskSkbListener =
                new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Calcolo voto esercitazioni
                double exNumber = (double) finalExercisesNumber;
                currentExerTskGrade = progress / exNumber * 3;
                currentExerTskGrade = Math.floor(currentExerTskGrade*10)/10;

                //Aggiorno l'editText relativo alle esercitazioni
                editableTextExerTskGrade.setText("" + currentExerTskGrade);

                //Aggiorno l'editText relativo al voto finale
                updateFinalVote();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}

        };

        //Assegnazione Listener alle Seekbar
        seekbarWrTskGrade.setOnSeekBarChangeListener(customWrTskSkbListener);
        seekbarOralTskGrade.setOnSeekBarChangeListener(customOralTskSkbListener);
        seekbarExerTskGrade.setOnSeekBarChangeListener(customExerTskSkbListener);

    }

    private void goToMainActivity() {
        Intent goToMain = new Intent(this, MainActivity.class);
        startActivity(goToMain);
    }

    void updateFinalVote() {
        int finalVote = (int) (currentWrTskGrade + currentOralTskGrade + currentExerTskGrade);
        editableTextFinalTskGrade.setText("" + finalVote);
    }

}
