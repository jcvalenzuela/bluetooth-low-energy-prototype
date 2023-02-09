package com.example.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import java.util.UUID;

import io.reactivex.disposables.CompositeDisposable;

public class BleActivity extends AppCompatActivity {
    private final String TAG = "xxx " + this.getClass().getSimpleName();
    private Context context;

    private ImageView imgLock;
    private ProgressBar progressBar;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;
    private BluetoothManager bluetoothManager;
    private String deviceAddress = ("00:6F:F2:F1:00:A8");
    private BluetoothDevice device;

    public static final UUID TX_CHAR_UUID = UUID.fromString("5DD70002-4E4E-4E9C-BBD3-738DF1CAADAC");
    public static final UUID CLIENT_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    public static final UUID SERVICE_UUID = UUID.fromString("5DD74D52-4E4E-4E9C-BBD3-738DF1CAADAC");
    public static final UUID COMMAND_REQUEST_UUID = UUID.fromString("5DD70013-4E4E-4E9C-BBD3-738DF1CAADAC");
    public static final UUID COMMAND_REQUEST_RESPONSE_UUID = UUID.fromString("5DD70014-4E4E-4E9C-BBD3-738DF1CAADAC");
    public static final UUID CLIENT_CONFIG_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static final UUID DOOR_STATUS_NOTIFICATION_UUID = UUID.fromString("5DD70018-4E4E-4E9C-BBD3-738DF1CAADAC");
    public static final UUID SECURITY_RESPONSE_UUID = UUID.fromString("5DD70021-4E4E-4E9C-BBD3-738DF1CAADAC");

    // lock action
    public static final byte[] COMMAND_UNLOCK = hexToByte("0000");
    public static final byte[] COMMAND_LOCK = hexToByte("0100");
    // lock status
    public static final byte[] COMMAND_LOCK_STATUS = hexToByte("0C00");

    public BleActivity() {
        context = BleActivity.this;
    }

    private CompositeDisposable compositeDisposableButton = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);

        imgLock = findViewById(R.id.img_lock);
        progressBar = findViewById(R.id.progress_bar);

        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        connect();
    }

    private void connect() {
        device = bluetoothAdapter.getRemoteDevice(deviceAddress);
        bluetoothGatt = device.connectGatt(this, false, bluetoothGattCallback);
    }

    // Get callbacks when something changes
    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.e(TAG, "Connected to GATT server");
                // Attempts to discover services after successful connection.
                Log.e(TAG, "Attempting to start service discovery: " +
                        bluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.e(TAG, "Disconnected from GATT server");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.e(TAG, "onServicesDiscovered success");


                gatt.connect();
                BluetoothGattCharacteristic characteristic = gatt.getService(SERVICE_UUID).getCharacteristic(COMMAND_REQUEST_UUID);
                characteristic.setValue(COMMAND_LOCK_STATUS);


//                Log.e(TAG, "number of services: " + gatt.getServices().size());
//                for (BluetoothGattService x : gatt.getServices()) {
//                    Log.e(TAG, "     service uuid " + x.getUuid());
//                    for (BluetoothGattCharacteristic y : x.getCharacteristics()) {
//                        Log.e(TAG, "          characteristics uuid " + y.getUuid());
//                        for (BluetoothGattDescriptor z : y.getDescriptors()) {
//                            Log.e(TAG, "               descriptor uuid " + z.getUuid());
//                        }
//                    }
//                    Log.e(TAG, "---------------------------------------------------------------------");
//                }

                // Enable notification
//                BluetoothGattCharacteristic characteristic = gatt.getService(SERVICE_UUID).getCharacteristic(TX_CHAR_UUID);
//                gatt.setCharacteristicNotification(characteristic, true);
//                BluetoothGattDescriptor descriptor = characteristic.getDescriptor(CLIENT_CONFIG);
//                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//                gatt.writeDescriptor(descriptor);


            } else {
                Log.e(TAG, "onServicesDiscovered: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.e(TAG, "onCharacteristicRead: " + status);
            generateData(characteristic);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            Log.e(TAG, "onDescriptorWrite: " + status);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            Log.e(TAG, "onDescriptorRead: " + status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.e(TAG, "onCharacteristicWrite: " + status);

        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.e(TAG, "onCharacteristicChanged for: " + characteristic.getUuid());
            generateData(characteristic);

        }
    };

    // Get real data out of characteristic
    private void generateData(final BluetoothGattCharacteristic characteristic) {
        final byte[] data = characteristic.getValue();
        Log.e(TAG, "Data: " + data);
        Log.e(TAG, "Data size: " + data.length);

        String payload = "";
        for (byte b : data) {
            payload += String.format("%02x", b);
        }

        Log.e(TAG, "Decoded data: " + payload);

        // Image rendering
//        if (payload.equals("01")) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    imgLock.setBackgroundResource(R.drawable.lock_closed);
//                }
//            });
//        } else if (payload.equals("00")) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    imgLock.setBackgroundResource(R.drawable.lock_open);
//                }
//            });
//        } else {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    imgLock.setBackgroundResource(R.drawable.error);
//                }
//            });
//        }
    }

    // Command transformation
    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }

        return data;
    }

    public static byte[] hexToByte(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }
}