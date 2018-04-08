package com.example.myfirstapplication;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import static android.content.ContentValues.TAG;

public class BluetoothService extends Service {

    Handler mHandler;
    DatabaseReference databaseReference;

    @Override
    public void onCreate() {
        super.onCreate();

        databaseReference = FirebaseDatabase.getInstance().getReference("root");

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                String readMessage = (String)msg.obj;
                Log.i("Bt_data",readMessage);
                databaseReference.setValue(readMessage);
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private class ConnectThread extends Thread {

        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        private String device_address;
        private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        ConnectThread() {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = mBluetoothAdapter.getRemoteDevice(device_address);

            Log.i("BTstatus", "Inside Connect Thread");
            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                //tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(id));
                tmp = mmDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e("SocketError", "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();

            } catch (IOException connectException) {
                Log.i("Lol", connectException.getMessage());
                // Unable to connect; close the socket and return.
                try {
                    Log.i("BTstatus", "Close socket");
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e("SocketFailed", "Could not close the client socket", closeException);
                }
                Log.i("BTstatus", "Inside run method failed");
                return;
            }
            Log.i("BTConnectionStatus", "Connected to " + mmDevice.getName());
            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            DataThread data = new DataThread(mmSocket);
            data.start();
            //data.write("");
        }

        // Closes the client socket and causes the thread to finish.
        void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e("SocketError", "Could not close the client socket", e);
            }
        }
    }

    public class DataThread extends Thread {
        final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer;            // mmBuffer store for the stream
        private StringBuilder msg = new StringBuilder();

        DataThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;

        }

        int MESSAGE_READ = 0;
        int MESSAGE_WRITE = 1;
        int MESSAGE_TOAST = 2;


        public void run() {
            mmBuffer = new byte[1024];
            int numBytes; // bytes returned from read()

            Log.d("Inside run", "Inside data run successful");
            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    numBytes = mmInStream.read(mmBuffer);
                    String readMessage = new String(mmBuffer, 0, numBytes);
                    if(readMessage.startsWith("+") || !readMessage.endsWith("+"))
                        msg.append(readMessage);
                    else if(readMessage.endsWith("+")){
                        msg.append(readMessage);
                        // Send the obtained bytes to the UI activity.
                        Log.i("Buf_data", readMessage);
                        Message readMsg = mHandler.obtainMessage(MESSAGE_READ, msg.length(), -1, msg.toString());
                        readMsg.sendToTarget();
                        msg.delete(0,msg.length());
                    }
                } catch (IOException e) {
                    Log.d(TAG, "Input stream was disconnected", e);
                    break;
                }
            }
        }

        void write(String data) {
            try {
                //mmOutStream.write(bytes);
                String msg = "Testing";
                mmOutStream.write(msg.getBytes());
                Log.i("write_data" , data);
                // Share the sent message with the UI activity.
                Message writtenMsg = mHandler.obtainMessage(MESSAGE_WRITE, -1, -1, mmBuffer);
                writtenMsg.sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data", e);

                // Send a failure message back to the activity.
                Message writeErrorMsg = mHandler.obtainMessage(MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast", "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                mHandler.sendMessage(writeErrorMsg);
            }
        }
    }
}


