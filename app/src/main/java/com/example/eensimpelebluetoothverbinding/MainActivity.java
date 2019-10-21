package com.example.eensimpelebluetoothverbinding;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static com.example.eensimpelebluetoothverbinding.BroadcastRecieverRinkelt.erIsOpgehangen;
import static java.nio.charset.Charset.defaultCharset;

//--------------------------------MainActivity----------------------------------
public class MainActivity extends AppCompatActivity {

    private ScheduledExecutorService scheduleTaskExecutor;
    private static final int PERMISSION_REQUEST_READ_PHONE_STATE = 0;
    public DeviceListAdapter mDeviceListAdapter;
    public BluetoothConnectionService mBluetoothConnection;

    private String TAG = "testflow";
    private static final UUID MY_UUID_INSECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();

    public ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_DTMF, 95);

    ListView lvNewDevices;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mBTDevice;
    TextView incomingMessages;
    StringBuilder messages;
    Button btnEnableDisable_Discoverable;
    Button btnSend;
    int teller = 0;
    byte[] bytes;
    Context mContext;
    Context context;
    final long timeInterval = 2000;
    private Button Test_knop;

    //----------------------------------------------------------------------------------------------------------------------------
// Handler methode
    Handler hdler=new Handler(new Handler.Callback(){
        @Override
        public boolean handleMessage(Message msg) {
            Log.d("testflow", "bellen nu terug " + erIsOpgehangen);
            {
//                Intent callIntent = new Intent(Intent.ACTION_CALL);
//                callIntent.setData(Uri.parse("tel:" + "06149"));
//                startActivity(callIntent);
            }
            // if (erIsOpgehangen > 0)
            hdler.removeCallbacks(r);
            return false;
        }
    });

    //-------------------------------- onCreate ------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println("In Main");
        messages = new StringBuilder();
        incomingMessages =  findViewById(R.id.incomingMessage);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("incomingMessage"));

        //------------ Enable Default adapter ------------------------------
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivity(enableBTIntent);

        //------- DeviceList Discover BUTTON ------------------------------
        lvNewDevices = findViewById(R.id.lvNewDevices);
        mBTDevices = new ArrayList<>();
        Log.d(TAG, "btnDiscover: Looking for unpaired devices.");

        //checkBTPermissions();
        mBluetoothAdapter.startDiscovery();

        IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);

//        IntentFilter broadevt = new IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
//        registerReceiver(mBroadcastReceiver6, broadevt);

        btnSend =  findViewById(R.id.btnSendxml);
        btnEnableDisable_Discoverable = findViewById(R.id.btnDiscoverable_on_off);

//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED) {
//                String[] permissions = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE};
//                requestPermissions(permissions, PERMISSION_REQUEST_READ_PHONE_STATE);
//            }
//        }

        Thread wil = new Thread(r);
        wil.start();
    }
//-------------------------------- end off onCreate ------------------------------------

//----------------------------------------------------------------------------------------------------------------------------

    // Runnable Thread runs in background
    Runnable r = new Runnable() {
        @Override
        public void run() {

            while (true) {
                Log.d(TAG, "Timer erIsOpgehangen " + erIsOpgehangen);
                if (erIsOpgehangen==1) {

                    hdler.sendEmptyMessage(0);
                    erIsOpgehangen=0;
                }
                try {
                    Thread.sleep(timeInterval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };


//----------------------------------------------------------------------------------------------------------------------------
    //Button Onclick event
    public void btnSend(View view) {
        erIsOpgehangen++;
    }


//----------------------------------------------------------------------------------------------------------------------------
    //Button Onclick event
    public void Unpair(View view) {
        bytes="r".getBytes(defaultCharset());
        mBluetoothConnection.write(bytes);
    }


//----------------------------------------------------------------------------------------------------------------------------

    //Button Onclick event
    public void btnEnableDisable_Discoverable(View view) {
//        anwwerCall();
        bytes="r".getBytes(defaultCharset());
        mBluetoothConnection.write(bytes);
    }


//----------------------------------------------------------------------------------------------------------------------------


    public void startConnection() {
        mBluetoothConnection.startClient(mBTDevice, MY_UUID_INSECURE);
    }

//----------------------------------------------------------------------------------------------------------------------------


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onReceive(Context context, Intent intent) {
            String text = intent.getStringExtra("theMessage");
            messages.append(text);
            incomingMessages.setText(messages);
            String str = messages.toString();
            teller++;
            Log.d(TAG, Integer.toString(teller));
        }
    };


//-------------------- BroadcastReciever zoekt naar devices die nog niet gepaired zijn ---------------------
    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra (BluetoothDevice.EXTRA_DEVICE);
                mBTDevices.add(device);
                Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress() + " Gevonden met ACTION_FOUND in de mBroadcastReceiver3 !");
                mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTDevices);
                lvNewDevices.setAdapter(mDeviceListAdapter);

                if (device.getAddress().equals("00:18:E4:40:00:06")){
                    int r =mDeviceListAdapter.getCount()-1;
                    Log.d(TAG, "Probeer te verbinden met : " + r);
                    mBTDevices.get(r).createBond();
                    mBTDevice = mBTDevices.get(r);
                    mBluetoothConnection = new BluetoothConnectionService(MainActivity.this);
                    mBluetoothAdapter.cancelDiscovery();
                    startConnection();

                    //mBluetoothConnection.write(bytes);
                }
            }
        }
    };

//----------------------------------------------------------------------------------------------------------------------------

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver3);
        //unregisterReceiver(BroadcastRecieverRinkelt);
    }

//----------------------------------------------------------------------------------------------------------------------------


    /**
     * This method is required for all devices running API23+
     * Android must programmatically check the permissions for bluetooth. Putting the proper permissions
     * in the manifest is not enough.
     *
     * NOTE: This will only execute on versions > LOLLIPOP because it is not needed otherwise.
     */
    private void checkBTPermissions() {
//        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
//            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
//            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
//            if (permissionCheck != 0) {
//
//                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
//            }
//        }
//        else{
//            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
//        }
    }


//----------------------------------------------------------------------------------------------------------------------------

    //Testing
    public void anwwerCall()  {
        //Runtime.getRuntime().exec("input keyevent"+Integer.toString(KeyEvent.KEYCODE_HEADSETHOOK));


//        try {
//            Runtime.getRuntime().exec("input keyevent"+Integer.toString(KeyEvent.KEYCODE_HEADSETHOOK));
//        } catch (IOException e) {
//            //handle error here
//        }

//        Intent i = new Intent(Intent.ACTION_MEDIA_BUTTON);
//        KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_HEADSETHOOK);
//        i.putExtra(Intent.EXTRA_KEY_EVENT, event );
//        context.sendOrderedBroadcast(i, null);

    }

//-------------------- BroadcastRecieverRinkelt ---------------------
//private BroadcastReceiver mBroadcastReceiver6 = new BroadcastReceiver() {
//    @Override
//    public void onReceive(Context context, Intent intent) {
//
//        if(intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)){
//
//            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
//            //Toast.makeText(context,"Call from: " + incomingNumber,Toast.LENGTH_LONG).show();
//            //Log.d(TAG, "willem1");
//            // bytes="r".getBytes(defaultCharset());
//            mBluetoothConnection.write("r".getBytes(defaultCharset()));
//
//        }
//        else if(intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_IDLE) || intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
//
//            //Toast.makeText(context,"Detectes call hangup event",Toast.LENGTH_LONG).show();
//            //Log.d(TAG, "willem2");
//        }
//
//    }
//};

}