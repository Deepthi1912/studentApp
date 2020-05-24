package it.uniba.di.sms.giannelli;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ButtonBarLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.SharedElementCallback;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import it.uniba.di.sms.giannelli.StudentContract.*;

public class StudentActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private TextView etName, etSurname, etBirth, etWebsite,
            etCellphone, etEmail, etExamsDone, etTodoExams, etAverage;
    private RadioGroup rgGender;
    private RadioButton rbMale, rbFemale;
    private ToggleButton tbModify;
    private Button btnSensors, btnSave, btnExams, btnExamsDone;
    private StudentOpenHelper studentOpenHelper;
    private Toolbar myStudentToolbar;

    //services constants
    private static final String PREFS_NAME = "Preferences";
    private static final String FILENAME = "Preferences.txt";
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 432;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.exam_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        //Verifico che sia stata cliccato l'action menu corrispondente
        if(id == R.id.exams) {
            goToExamsActivity();
        }

        //Verifico che sia stata cliccato l'action menu corrispondente
        if(id == R.id.exams_done) {
            goToExamsDoneActivity();
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        btnSensors = (Button) findViewById(R.id.btnSensors);
        tbModify = (ToggleButton) findViewById(R.id.tbModify);
        rbMale = (RadioButton) findViewById(R.id.rbMale);
        rbFemale = (RadioButton) findViewById(R.id.rbFemale);
        rgGender = (RadioGroup) findViewById(R.id.rgGender);
        etName = (TextView) findViewById(R.id.editTxtName);
        etSurname = (TextView) findViewById(R.id.editTxtSurname);
        etBirth = (TextView) findViewById(R.id.editTxtBirth);
        etWebsite = (TextView) findViewById(R.id.editTxtWebsite);
        etCellphone = (TextView) findViewById(R.id.editTxtCellphone);
        etEmail = (TextView) findViewById(R.id.editTxtEmail);
        etExamsDone = (TextView) findViewById(R.id.editTxtExamsDone);
        etTodoExams = (TextView) findViewById(R.id.editTxtTodoExams);
        etAverage = (TextView) findViewById(R.id.editTxtAverage);
        btnSave = (Button)findViewById(R.id.btnSave);
        btnExams = (Button) findViewById(R.id.btnExams);
        rgGender = (RadioGroup) findViewById (R.id.rgGender);
        myStudentToolbar = (Toolbar) findViewById(R.id.studentToolbar);
        myStudentToolbar.setTitle("Giannelli");
        myStudentToolbar.setSubtitle("672367");
        myStudentToolbar.setLogo(R.mipmap.ic_launcher_round);
        setSupportActionBar(myStudentToolbar);

        etName.setVisibility(View.INVISIBLE);
        etSurname.setVisibility(View.INVISIBLE);
        etBirth.setVisibility(View.INVISIBLE);
        etWebsite.setVisibility(View.INVISIBLE);
        etCellphone.setVisibility(View.INVISIBLE);
        etEmail.setVisibility(View.INVISIBLE);
        etExamsDone.setVisibility(View.INVISIBLE);
        etTodoExams.setVisibility(View.INVISIBLE);
        etAverage.setVisibility(View.INVISIBLE);
        rbMale.setVisibility(View.INVISIBLE);
        rbFemale.setVisibility(View.INVISIBLE);



        //data constants
         final String[] exams = {
                getString(R.string.txtArchitettura),
                getString(R.string.txtMatematicaDiscreta),
                getString(R.string.txtProgrammazione),
                getString(R.string.txtAnalisiMatematica),
                getString(R.string.txtLinguaggiDiProgrammazione),
                getString(R.string.txtLaboratorioDiInformatica),
                getString(R.string.txtLinguaInglese),
                getString(R.string.txtProgrammazione2),
                getString(R.string.txtProgettazioneDiBasiDiDati),
                getString(R.string.txtCalcoloNumerico),
                getString(R.string.txtRetiDiCalcolatori),
                getString(R.string.txtIngegneriaDelSoftware),
                getString(R.string.txtFisicaApplicataAllInformatica),
                getString(R.string.txtStatisticaPerLIngegneriaDelSoftware),
                getString(R.string.txtEconomiaEGestioneDImpresa),
                getString(R.string.txtModelliEMetodi),
                getString(R.string.txtIntegrazioneETest),
                getString(R.string.txtProgettazioneEInterazioneConLUtente),
                getString(R.string.txtSviluppoDiMobileSoftware),
                getString(R.string.txtDataMining),
                getString(R.string.txtSistemiInformativi),
                getString(R.string.txtDidattica)
        };

         final String[] cfu = {
                getString(R.string.txtCFUArchitettura),
                getString(R.string.txtCFUMatematicaDiscreta),
                getString(R.string.txtCFUProgrammazione),
                getString(R.string.txtCFUAnalisiMatematica),
                getString(R.string.txtCFULinguaggiDiProgrammazione),
                getString(R.string.txtCFULaboratorioDiInformatica),
                getString(R.string.txtCFULinguaInglese),
                getString(R.string.txtCFUProgrammazione2),
                getString(R.string.txtCFUProgettazioneDiBasiDiDati),
                getString(R.string.txtCFUCalcoloNumerico),
                getString(R.string.txtCFURetiDiCalcolatori),
                getString(R.string.txtCFUIngegneriaDelSoftware),
                getString(R.string.txtCFUFisicaApplicataAllInformatica),
                getString(R.string.txtCFUStatisticaPerLIngegneriaDelSoftware),
                getString(R.string.txtCFUEconomiaEGestioneDImpresa),
                getString(R.string.txtCFUModelliEMetodi),
                getString(R.string.txtCFUIntegrazioneETest),
                getString(R.string.txtCFUProgettazioneEInterazioneConLUtente),
                getString(R.string.txtCFUSviluppoDiMobileSoftware),
                getString(R.string.txtCFUDataMining),
                getString(R.string.txtCFUSistemiInformativi),
                getString(R.string.txtCFUDidattica)
        };

         final String[] year = {
                getString(R.string.txtAnnoArchitettura),
                getString(R.string.txtAnnoMatematicaDiscreta),
                getString(R.string.txtAnnoProgrammazione),
                getString(R.string.txtAnnoAnalisiMatematica),
                getString(R.string.txtAnnoLinguaggiDiProgrammazione),
                getString(R.string.txtAnnoLaboratorioDiInformatica),
                getString(R.string.txtAnnoLinguaInglese),
                getString(R.string.txtAnnoProgrammazione2),
                getString(R.string.txtAnnoProgettazioneDiBasiDiDati),
                getString(R.string.txtAnnoCalcoloNumerico),
                getString(R.string.txtAnnoRetiDiCalcolatori),
                getString(R.string.txtAnnoIngegneriaDelSoftware),
                getString(R.string.txtAnnoFisicaApplicataAllInformatica),
                getString(R.string.txtAnnoStatisticaPerLIngegneriaDelSoftware),
                getString(R.string.txtAnnoEconomiaEGestioneDImpresa),
                getString(R.string.txtAnnoModelliEMetodi),
                getString(R.string.txtAnnoIntegrazioneETest),
                getString(R.string.txtAnnoProgettazioneEInterazioneConLUtente),
                getString(R.string.txtAnnoSviluppoDiMobileSoftware),
                getString(R.string.txtAnnoDataMining),
                getString(R.string.txtAnnoSistemiInformativi),
                getString(R.string.txtAnnoDidattica)
        };

         final String[] semester = {
                getString(R.string.txtSemestreArchitettura),
                getString(R.string.txtSemestreMatematicaDiscreta),
                getString(R.string.txtSemestreProgrammazione),
                getString(R.string.txtSemestreAnalisiMatematica),
                getString(R.string.txtSemestreLinguaggiDiProgrammazione),
                getString(R.string.txtSemestreLaboratorioDiInformatica),
                getString(R.string.txtSemestreLinguaInglese),
                getString(R.string.txtSemestreProgrammazione2),
                getString(R.string.txtSemestreProgettazioneDiBasiDiDati),
                getString(R.string.txtSemestreCalcoloNumerico),
                getString(R.string.txtSemestreRetiDiCalcolatori),
                getString(R.string.txtSemestreIngegneriaDelSoftware),
                getString(R.string.txtSemestreFisicaApplicataAllInformatica),
                getString(R.string.txtSemestreStatisticaPerLIngegneriaDelSoftware),
                getString(R.string.txtSemestreEconomiaEGestioneDImpresa),
                getString(R.string.txtSemestreModelliEMetodi),
                getString(R.string.txtSemestreIntegrazioneETest),
                getString(R.string.txtSemestreProgettazioneEInterazioneConLUtente),
                getString(R.string.txtSemestreSviluppoDiMobileSoftware),
                getString(R.string.txtSemestreDataMining),
                getString(R.string.txtSemestreSistemiInformativi),
                getString(R.string.txtSemestreDidattica)
        };

        final String[] examsDone = {
                getString(R.string.txtDoneArchitettura),
                getString(R.string.txtDoneMatematicaDiscreta),
                getString(R.string.txtDoneProgrammazione),
                getString(R.string.txtDoneAnalisiMatematica)
        };

        final String[] examsDate = {
                getString(R.string.txtDateArchitettura),
                getString(R.string.txtDateMatematicaDiscreta),
                getString(R.string.txtDateProgrammazione),
                getString(R.string.txtDateAnalisiMatematica)
        };

        final String[] examsGrade = {
                getString(R.string.txtGradeArchitettura),
                getString(R.string.txtGradeMatematicaDiscreta),
                getString(R.string.txtGradeProgrammazione),
                getString(R.string.txtGradeAnalisiMatematica)
        };

        tbModify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    etName.setVisibility(View.VISIBLE);
                    etSurname.setVisibility(View.VISIBLE);
                    etBirth.setVisibility(View.VISIBLE);
                    etWebsite.setVisibility(View.VISIBLE);
                    etCellphone.setVisibility(View.VISIBLE);
                    etEmail.setVisibility(View.VISIBLE);
                    etExamsDone.setVisibility(View.VISIBLE);
                    etTodoExams.setVisibility(View.VISIBLE);
                    etAverage.setVisibility(View.VISIBLE);
                    rbMale.setVisibility(View.VISIBLE);
                    rbFemale.setVisibility(View.VISIBLE);
                } else {
                    etName.setVisibility(View.INVISIBLE);
                    etSurname.setVisibility(View.INVISIBLE);
                    etBirth.setVisibility(View.INVISIBLE);
                    etWebsite.setVisibility(View.INVISIBLE);
                    etCellphone.setVisibility(View.INVISIBLE);
                    etEmail.setVisibility(View.INVISIBLE);
                    etExamsDone.setVisibility(View.INVISIBLE);
                    etTodoExams.setVisibility(View.INVISIBLE);
                    etAverage.setVisibility(View.INVISIBLE);
                    rbMale.setVisibility(View.INVISIBLE);
                    rbFemale.setVisibility(View.INVISIBLE);
                    etBirth.setText("");
                }
            }
        });

        btnSensors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSensorActivity();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save(exams, cfu, year, semester, examsDone, examsDate, examsGrade);
            }
        });

        etBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "datePicker");
            }
        });

    }

    public void goToExamsActivity() {
        Intent goToExams = new Intent(this, ExamListActivity.class);
        startActivity(goToExams);
    }

    public void goToExamsDoneActivity() {
        Intent goToExamsDone = new Intent(this, ExamDoneListActivity.class);
        startActivity(goToExamsDone);
    }

    //Salvataggio dati
    public void save(String[] exams, String[] cfu, String[] year, String[] semester,
                     String[] examsDone, String[] date, String[] grade) {

        //Save on SharedPreferences
        saveOnPreferences();

        //Save on Internal Storage
        saveOnInternalStorage();

        //Save on External Storage
        saveOnExternalStorageWithPermissions();
        saveOnExternalStorage();

        //Save on Database
        saveOnDb(exams, cfu, year, semester, examsDone, date, grade);
    }

    public String getGenderToString() {
        if(rbFemale.isChecked())
            return getString(R.string.txtFemale);
        if(rbMale.isChecked())
            return getString(R.string.txtMale);
        else return getString(R.string.txtMale);
    }

    public void saveOnPreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(getString(R.string.txtName), etToString(etName));
        editor.putString(getString(R.string.txtSurname), etToString(etSurname));
        editor.putString(getString(R.string.txtBirth), etToString(etBirth));
        editor.putString(getString(R.string.txtWebsite), etToString(etWebsite));
        editor.putString(getString(R.string.txtCellphone), etToString(etCellphone));
        editor.putString(getString(R.string.txtEmail), etToString(etEmail));
        editor.putString(getString(R.string.txtExamsDone), etToString(etExamsDone));
        editor.putString(getString(R.string.txtTodoExams), etToString(etTodoExams));
        editor.putString(getString(R.string.txtAverage), etToString(etAverage));
        editor.putString(getString(R.string.txtMale), etToString(rbMale));
        editor.putString(getString(R.string.txtFemale), etToString(rbFemale));
        editor.apply();
    }

    public void saveOnExternalStorageWithPermissions() {
        //Controllo se il permesso sia stato già concordato
        if (ContextCompat.checkSelfPermission(StudentActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            //Nel caso in cui non sia stato dato mi occupo di mostrare un avviso all'utente
            if (ActivityCompat.shouldShowRequestPermissionRationale(StudentActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //Comunichiamo all'utente l'utilita' di questo permesso
                new AlertDialog.Builder(StudentActivity.this)
                        .setTitle("Permission needed")
                        .setMessage("We need to write a file on your external storage, " +
                                "for saving student information.")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(StudentActivity.this,
                                        new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        REQUEST_WRITE_EXTERNAL_STORAGE);
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
            } else {
                ActivityCompat.requestPermissions(StudentActivity.this,
                        new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        } else {
            saveOnExternalStorage();
        }
    }

    public void saveOnInternalStorage() {
        try {
            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(studentToString().getBytes());
            fos.close();
        } catch (Exception e) {
            Log.e("ERROR ", e.toString());
        }
    }

    //Save to external storage
    public void saveOnExternalStorage() {
        //root info
        String rootState = Environment.getExternalStorageState();
        //returns
        File root = Environment.getExternalStorageDirectory();
        //returns a File, the root directory of app if parameter is null, or
        //returns Music, Pictures if parameter equals Environment.DOCUMENTS, etc
        //File rootDirectory = this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);

        // verify if external storage is mounted
        if (!Environment.MEDIA_MOUNTED.equals(rootState)) {
            saveOnInternalStorage();
        } else {
            File file = new File(root, FILENAME);
            try {
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(studentToString().getBytes());
                fos.close();
                Toast.makeText(this, getString(R.string.txtSuccessSave, root.toString()),
                        Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e("Error: ", e.toString());
            }
        }
    }

    private void saveOnDb(String[] exams, String[] cfu, String[] year, String[] semester,
                          String[] examsDone, String[] date, String[] grade) {
        studentOpenHelper = new StudentOpenHelper(this);
        //ottenimento database in scrittura
        SQLiteDatabase db = studentOpenHelper.getWritableDatabase();
        db.insert(StudentEntry.TABLE_NAME, null, saveStudentEntries());

        saveExamsOnDb(exams, cfu, year, semester);
        saveExamsDoneOnDb(examsDone, date, grade);
    }

    private void saveExamsOnDb(String[] exams, String[] cfu, String[] year, String[] semester) {

        studentOpenHelper = new StudentOpenHelper(this);
        SQLiteDatabase db = studentOpenHelper.getWritableDatabase();

        //Esami
        List<ContentValues> cv = saveExamEntries(exams, cfu, year, semester);
        Iterator<ContentValues> pointer = cv.iterator();
        while(pointer.hasNext()){
            db.insert(ExamEntry.TABLE_NAME, null, pointer.next());
        }
    }

    private void saveExamsDoneOnDb(String[] examsDone, String[] date, String[] grade) {

        studentOpenHelper = new StudentOpenHelper(this);
        SQLiteDatabase db = studentOpenHelper.getWritableDatabase();

        //Esami Svolti
        List<ContentValues> cv = saveExamDoneEntries(examsDone, date, grade);
        Iterator<ContentValues> pointer = cv.iterator();
        while(pointer.hasNext()){
            db.insert(ExamDoneEntry.TABLE_NAME, null, pointer.next());
        }
    }

    private ContentValues saveStudentEntries() {
        ContentValues values = new ContentValues();
        values.put(StudentEntry.COLUMN_MATRICOLA, etToString(etName));
        values.put(StudentEntry.COLUMN_NAME, etToString(etName));
        values.put(StudentEntry.COLUMN_SURNAME, etToString(etSurname));
        values.put(StudentEntry.COLUMN_BIRTH, etToString(etBirth));
        values.put(StudentEntry.COLUMN_WEBSITE, etToString(etWebsite));
        values.put(StudentEntry.COLUMN_PHONE, etToString(etCellphone));
        values.put(StudentEntry.COLUMN_EMAIL, etToString(etEmail));
        values.put(StudentEntry.COLUMN_GENDER, getGenderToString());
        values.put(StudentEntry.COLUMN_EXAMS_DONE, etToString(etExamsDone));
        values.put(StudentEntry.COLUMN_EXAMS_TODO, etToString(etTodoExams));
        values.put(StudentEntry.COLUMN_EXAMS_AVERAGE, etToString(etAverage));
        return values;
    }

    private ArrayList<ContentValues> saveExamEntries(String[] localExams,
                                                     String[] localCfu, String[] localYear,
                                                     String[] localSemester) {
        //Lista di ContentValues, utilizzata per il salvataggio dei dati
        //di tutti gli esami
        ArrayList<ContentValues> values = new ArrayList<>();
        for(int count = 0; count < localExams.length; count++) {
            ContentValues cv = new ContentValues();
            cv.put(ExamEntry.COLUMN_NAME, localExams[count]);
            cv.put(ExamEntry.COLUMN_CFU, localCfu[count]);
            cv.put(ExamEntry.COLUMN_YEAR, localYear[count]);
            cv.put(ExamEntry.COLUMN_SEMESTER, localSemester[count]);
            values.add(cv);
        }
        return values;
    }

    private ArrayList<ContentValues> saveExamDoneEntries(String[] localExamsDone,
                                                         String[] localExamsDate,
                                                         String[] localExamsGrade) {
        //Lista di ContentValues, utilizzata per il salvataggio dei dati
        //di tutti gli esami SVOLTI
        ArrayList<ContentValues> values = new ArrayList<>();
        for(int count = 0; count < localExamsDone.length; count++) {
            ContentValues cv = new ContentValues();
            cv.put(ExamDoneEntry.COLUMN_NAME, localExamsDone[count]);
            cv.put(ExamDoneEntry.COLUMN_DATE, localExamsDate[count]);
            cv.put(ExamDoneEntry.COLUMN_GRADE, localExamsGrade[count]);
            values.add(cv);
        }
        return values;
    }

    //student info to string
    public String studentToString() {
        return etToString(etName) +
                etToString(etSurname) +
                etToString(etBirth) +
                etToString(etWebsite) +
                etToString(etCellphone) +
                etToString(etEmail) +
                etToString(etExamsDone) +
                etToString(etTodoExams) +
                etToString(etAverage) +
                getGenderToString();
    }

    //Retrieve string from TextView
    private String etToString(TextView textView) {
        return textView.getText().toString();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        updateBirth(c);
    }

    //Formattazione della data di nascita a seconda della lingua impostata sul dispositivo
    public void updateBirth(Calendar c) {
        String calendarString = DateFormat.getDateInstance().format(c.getTime());
        etBirth.setText(calendarString);
    }

    public void goToSensorActivity() {
        Intent goToSensor = new Intent(this, SensorListActivity.class);
        startActivity(goToSensor);
        finish();
    }
}
