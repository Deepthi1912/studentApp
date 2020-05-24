package it.uniba.di.sms.giannelli;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import it.uniba.di.sms.giannelli.flagquiz.*;
import it.uniba.di.sms.giannelli.favouriteresearch.*;
import it.uniba.di.sms.giannelli.cannonapp.*;
import it.uniba.di.sms.giannelli.snake.*;

public class LauncherActivity extends AppCompatActivity {

    private Button btnMain, btnSecond, btnAlarm, btnContacts, btnStudent;
    private Button btnCannon, btnFavouriteResearch, btnFlagQuiz, btnSnake;
    Toolbar myToolbar;
    private final int REQUEST_READ_CONTACTS = 581;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        btnMain = (Button) findViewById(R.id.btnMainActivity);
        btnAlarm = (Button) findViewById(R.id.btnAlarm);
        btnContacts = (Button) findViewById(R.id.btnContacts);
        btnStudent = (Button) findViewById(R.id.btnStudent);
        btnCannon = (Button) findViewById(R.id.btnCannonApp);
        btnFavouriteResearch = (Button) findViewById(R.id.btnFavouriteResearch);
        btnFlagQuiz = (Button) findViewById(R.id.btnFlagQuiz);
        btnSnake = (Button) findViewById(R.id.btnSnake);

        myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitle("Giannelli");
        myToolbar.setSubtitle("672367");
        myToolbar.setLogo(R.mipmap.ic_launcher_round);
        setSupportActionBar(myToolbar);

        //Listener Button Main Activity
        btnMain.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMainActivity();
            }
        });

        btnAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAlarmActivity();
            }
        });

        btnContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Controllo se il permesso sia stato già concordato
                if (ContextCompat.checkSelfPermission(LauncherActivity.this,
                        Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {

                    //Nel caso in cui non sia stato dato mi occupo di mostrare un avviso all'utente
                    if (ActivityCompat.shouldShowRequestPermissionRationale(LauncherActivity.this,
                            Manifest.permission.READ_CONTACTS)) {
                        //Comunichiamo all'utente l'utilita' di questo permesso
                        new AlertDialog.Builder(LauncherActivity.this)
                                .setTitle("Permission needed")
                                .setMessage("It's a big deal. We need to access your contacts to continue!")
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ActivityCompat.requestPermissions(LauncherActivity.this,
                                                new String[] {Manifest.permission.READ_CONTACTS},
                                                REQUEST_READ_CONTACTS);
                                    }
                                })
                                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create().show();
                    } else {
                        ActivityCompat.requestPermissions(LauncherActivity.this,
                                new String[] {Manifest.permission.READ_CONTACTS},
                                REQUEST_READ_CONTACTS);
                    }
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW, ContactsContract.Contacts.CONTENT_URI);
                    startActivity(intent);
                }
            }
        });

        btnStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToStudentActivity();
            }
        });

        btnCannon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCannonActivity();
            }
        });

        btnFavouriteResearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFavouriteResearchActivity();
            }
        });

        btnFlagQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFlagQuizActivity();
            }
        });

        btnSnake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSnakeActivity();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        //Verifico che sia stata cliccato l'action menu corrispondente
        if(id == R.id.motto) {
            Toast.makeText(LauncherActivity.this, "The bigger they are, the harder they fall.", Toast.LENGTH_LONG).show();
        }
        return true;
    }

    //Verifichiamo che l'utente abbia concesso il permesso di lettura dei contatti
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(LauncherActivity.this, "Permission Granted!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(LauncherActivity.this, "Permission Denied.", Toast.LENGTH_LONG).show();
            }
        }
    }

    void goToMainActivity() {
        Intent goToMain = new Intent(this, MainActivity.class);
        startActivity(goToMain);
    }

    void goToAlarmActivity() {
        Intent goToAlarm = new Intent(this, AlarmActivity.class);
        startActivity(goToAlarm);
    }

    void goToStudentActivity() {
        Intent goToStudent = new Intent(this, StudentActivity.class);
        startActivity(goToStudent);
    }

    private void goToCannonActivity() {
        Intent goToStudent = new Intent(this, CannonApp.class);
        startActivity(goToStudent);
    }

    private void goToFavouriteResearchActivity() {
        Intent goToStudent = new Intent(this, MainFavoriteSearches.class);
        startActivity(goToStudent);
    }

    private void goToFlagQuizActivity() {
        Intent goToStudent = new Intent(this, FlagQuizGame.class);
        startActivity(goToStudent);
    }

    private void goToSnakeActivity() {
        Intent goToStudent = new Intent(this, Snake.class);
        startActivity(goToStudent);
    }
}
