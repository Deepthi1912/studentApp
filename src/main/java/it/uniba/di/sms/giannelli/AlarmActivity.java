package it.uniba.di.sms.giannelli;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AlarmActivity extends AppCompatActivity {

    Button btnAlarmSet;
    EditText editTextHours, editTextMinutes;
    Toolbar tlbAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        //Pulsanti e editText
        btnAlarmSet = (Button) findViewById(R.id.btnAlarmSet);
        editTextHours = (EditText) findViewById(R.id.editTextHours);
        editTextMinutes = (EditText) findViewById(R.id.editTextMinutes);
        tlbAlarm = (Toolbar) findViewById(R.id.tlbAlarm);
        tlbAlarm.setTitle(getString(R.string.txtAlarm));
        setSupportActionBar(tlbAlarm);

        btnAlarmSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hours = Integer.parseInt(editTextHours.getText().toString());
                int minutes = Integer.parseInt(editTextMinutes.getText().toString());
                createAlarm("Sveglia!", hours, minutes);

                //Formattazione per visualizzazione a due cifre
                String formattedHours = String.format("%01d", hours);
                String formattedMinutes = String.format("%01d", minutes);

                //Messaggio toast
                String toastMessage = getString(R.string.txtAlarmSet, formattedHours, formattedMinutes);
                Toast.makeText(AlarmActivity.this, toastMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    void createAlarm(String msg, int hours, int minutes) {
        //Intent implicito
        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
        //Informazioni per intent
        intent.putExtra(AlarmClock.EXTRA_MESSAGE, msg)
                .putExtra(AlarmClock.EXTRA_HOUR, hours)
                .putExtra(AlarmClock.EXTRA_MINUTES, minutes);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
        finish();
    }
}
