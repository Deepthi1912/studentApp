package it.uniba.di.sms.giannelli;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BluetoothActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    public static final int REQUEST_ENABLE_BT = 1;
    private static final String TAG = ".BluetoothActivity";
    Button btnScan, btnShow, btnSend;
    Switch swtcEnabled, swtcVisible;
    TextView tvEnabled, tvVisible, tvFriendAverage;
    EditText etYourAverage;
    BluetoothAdapter btAdapter;
    BluetoothDevice btDevice;
    ListView pairedListView, bluetoothListView;
    ArrayList<BluetoothDevice> btDevices;
    ArrayAdapter bluetoothFoundArrayAdapter;
    BluetoothShareService bss = new BluetoothShareService(this);

    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("6efe21d3-9a9d-43c6-8e7f-998158d97b3b");

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        btnScan = (Button) findViewById(R.id.btnScan);
        swtcEnabled = (Switch) findViewById(R.id.swtcEnabled);
        swtcVisible = (Switch) findViewById(R.id.swtcVisible);
        tvEnabled = (TextView) findViewById(R.id.tvIsEnabled);
        tvVisible = (TextView) findViewById(R.id.tvIsVisible);
        btnShow = (Button) findViewById(R.id.btnShow);
        btnSend = (Button) findViewById(R.id.btnSend);
        pairedListView = (ListView) findViewById(R.id.pairedListView);
        bluetoothListView = (ListView) findViewById(R.id.bluetoothListView);

        //TextView che cattura il valore fornito dal secondo dispositivo bluetooth
        tvFriendAverage = findViewById(R.id.tvFriendAverage);

        //EditText con la propria media
        etYourAverage = findViewById(R.id.etSendViaBluetooth);

        //Accensione Bluetooth
        setEnabledSwitch();

        //Discoverability
        btDiscoberable();

        //Scansione dispositivi accoppiati
        scanBtPairedDevices();

        //Istanzio la lista di stringhe contenenti i nomi dei dispositivi ottenuti
        //tramite discover
        btDevices = new ArrayList<>();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(bondingStateReceiver, filter);

        btnScan.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                scanBTDevices();

            }
        });

        pairedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startConnection();
            }
        });

        bluetoothListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                btAdapter.cancelDiscovery();

                Log.d(TAG, "Clicked on a device");
                String deviceName = btDevices.get(position).getName();
                String deviceAddress = btDevices.get(position).getAddress();

                Log.d(TAG, "onItemClick: Device name: " + deviceName);
                Log.d(TAG, "onItemClick: Device address: " + deviceAddress);

                /**
                 * Creating the bond
                 */
                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    Log.d(TAG, "Trying to pair with " + deviceName);
                    btDevices.get(position).createBond();
                    btDevice = btDevices.get(position);
                    bss = new BluetoothShareService(BluetoothActivity.this);
                }
            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(averageReceiver,
                new IntentFilter("incomingMessage"));

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] bytes = etYourAverage.getText().toString().getBytes(Charset.defaultCharset());
                bss.write(bytes);

            }
        });

    }

    private void startConnection() {
        startBTConnection(btDevice, MY_UUID_INSECURE);
    }

    public void startBTConnection(BluetoothDevice device, UUID uuid) {
        Log.d(TAG, "startBTConnection: Initializing RFCOM Bluetooth Connection.");

        bss.startClient(device, uuid);

    }

    BroadcastReceiver averageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String text = intent.getStringExtra("average");
            tvFriendAverage.setText(getString(R.string.txtYourFriend, text));
        }
    };

    /**
     * Broadcast Receiver that detects bond state changes (Pairing status changes)
     */
    private BroadcastReceiver bondingStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                switch (device.getBondState()) {

                    case BluetoothDevice.BOND_BONDED:
                        Log.d(TAG, "BOND_BONDED: Bluetooth device already bonded");
                        btDevice = device;
                    case BluetoothDevice.BOND_BONDING:
                        Log.d(TAG, "BOND_BONDING: Bluetooth device bonding...");
                    case BluetoothDevice.BOND_NONE:
                        Log.d(TAG, "BOND_NONE: Bluetooth device not bonded");
                }
            }
        }
    };

    //Creazione di un BroadcastReceiver per ACTION_FOUND,
    //occupandosi quindi di ricevere la lista dei
    // dispositivi scansionabili nel range di scansione
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "scanBTDevices: onReceive() callback method called from the system");
            String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION_FOUND");

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //La ricerca ha trovato un device
                //ora otteniamo il dispositivo dall'intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                //Verifica dispositivo trovato
                Log.d(TAG, "onReceive: trovato " + device.getName() + "; indirizzo: " + device.getAddress());

                btDevices.add(device);

                bluetoothFoundArrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.device_row, btDevices);
                bluetoothListView.setAdapter(bluetoothFoundArrayAdapter);
            }
        }
    };

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (action.equals(btAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, btAdapter.ERROR);

                switch(state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }
        }
    };

    /**
     * Broadcast Receiver for changes made to bluetooth states such as:
     * 1) Discoverability mode on/off or expire.
     */
    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {

                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (mode) {
                    //Device is in Discoverable Mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Enabled.");
                        break;
                    //Device not in discoverable mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Able to receive connections.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Not able to receive connections.");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "mBroadcastReceiver2: Connecting....");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "mBroadcastReceiver2: Connected.");
                        break;
                }

            }
        }
    };

    public void btDiscoberable() {
        swtcVisible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!btAdapter.isDiscovering()) {
                    startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE), 1);
                    Toast.makeText(getApplicationContext(), "Making device discoverable", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void scanBTDevices() {
        Log.d(TAG, "scanBTDevices: scansione dispositivi bluetooth non appaiati");

        if(btAdapter.isDiscovering()) {
            btAdapter.cancelDiscovery();
            Log.d(TAG, "scanBTDevices: scansione cancellata poiche' gia' attiva");

            //Controlla i permessi del bluetooth nel manifest
            checkBTPermission();

            btAdapter.startDiscovery();
            Log.d(TAG, "scanBTDevices: inizio scansione");

            //Intent filter allo scopo di mostrarsi
            //"interessato" alla ricezione di info
            //sulla scoperta di nuovi dispositivi
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            Log.d(TAG, "scanBTDevices: creazione intentFilter");
            registerReceiver(receiver, filter);

        }

        if (!btAdapter.isDiscovering()) {

            //Controlla i permessi del bluetooth nel manifest
            checkBTPermission();
            Log.d(TAG, "scanBTDevices: scansione NON cancellata poiche' NON gia' attiva");

            btAdapter.startDiscovery();
            Log.d(TAG, "scanBTDevices: inizio scansione");

            //Intent filter allo scopo di mostrarsi
            //"interessato" alla ricezione di info
            //sulla scoperta di nuovi dispositivi
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            Log.d(TAG, "scanBTDevices: creazione intentFilter");
            registerReceiver(receiver, filter);

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkBTPermission() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
            } else {
                Log.d(TAG, "checkBTPermission: No need to check permissions. SDK version < LOLLIPOP");
            }
        }
    }



    private void setEnabledSwitch() {
        //ottenimento adapter dispositivo
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        swtcEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setBluetoothOn();
                } else {
                    if (btAdapter.isEnabled()) {
                        btAdapter.disable();
                    }
                }

            }
        });
    }


    private void setBluetoothOn() {

        //controlliamo se sia integrato nel dispositivo
        if(btAdapter == null) {

            /*
            Il dispositivo non supporta il Bluetooth, pertanto
            verra' mostrato un dialog che, alla pressione
            del pulsante "ok", riportera'
            all'activity Studente
             */
            dialogBTNotSupported();

        } else {

            //Verifichiamo che il bluetooth sia abilitato

            if(!btAdapter.isEnabled()) {

                //Nel caso non sia abilitato, lo abilita
                enableBluetooth();

            }
        }
    }

    private void dialogBTNotSupported() {

        //Creazione Dialog
        new AlertDialog.Builder(BluetoothActivity.this)
                .setTitle(getString(R.string.txtBluetoothNotSupported))
                .setMessage(getString(R.string.txtBNSDescription))
                .setPositiveButton(getString(R.string.txtOk), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goToStudentActivity();
                    }
                }).create().show();
    }


    private void enableBluetooth() {
        Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BT);
    }

    //Scan bluetooth paired devices
    public void scanBtPairedDevices() {
        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Set<BluetoothDevice> bt = btAdapter.getBondedDevices();
                String[] strings = new String[bt.size()];
                int index = 0;

                if (bt.size() > 0) {
                    for (BluetoothDevice device:bt) {
                        strings[index] = device.getName();
                        index++;
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),R.layout.device_row, strings);
                    pairedListView.setAdapter(adapter);
                }
            }
        });
    }

    private void goToStudentActivity() {
        Intent goToStudent = new Intent(this, StudentActivity.class);
        startActivity(goToStudent);
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        //Verifichiamo che l'intero relativo alla richiesta
        //sia effettivamente il medesimo

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {

            //Una volta verificato, verifichiamo che
            //l'operazione sia andata a buon fine
            if (resultCode == RESULT_OK) {

                //Il bluetooth è abilitato
                Toast.makeText(this, "Bluetooth Enabled", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Bluetooth Enabling Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void startBTConnection() {
        Log.d("Giannelli", "startBTConnection: Initializing RFCOM Bluetooth Connection");
        Toast.makeText(this, "Bluetooth Connection started!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {



    }
}
