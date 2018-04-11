package com.example.myfirstapplication;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;
import android.support.constraint.solver.Goal;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    String recognizedevent;
    ArrayList arrayList = new ArrayList();
    RecyclerView recyclerView;
    RecyclerAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    private static final int REQUEST_ENABLE_BT = 1;
    BluetoothAdapter mBluetoothAdapter;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    TextToSpeech t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //arrayList.add("LiftCup");
        //arrayList.add("LiftCoffeeContainer");

        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.US);
                }
            }
        });

        recyclerView = findViewById(R.id.recyle);
        textView = findViewById(R.id.textview);
        final DatabaseReference event = myRef.child("Sensor").child("SensorEvent");

        //checkBluetoothAdapter();

        event.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                textView.setText(value);
                recognizedevent = value;

                String toSpeak = "Recognized action is"+value;
                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);

                get_assist();

                Log.d("data", "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        adapter = new RecyclerAdapter(arrayList, this, recognizedevent);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void get_assist() {
        myRef.child(recognizedevent).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot tmp : dataSnapshot.getChildren()) {
                    arrayList.add(tmp.getKey());
                    Log.d("Array", tmp.getKey());
                }

                recyclerView.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void checkBluetoothAdapter() {

        if (mBluetoothAdapter == null) {
            //Show a message that the device has no bluetooth adapter
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();
            //finish apk
            finish();
        } else if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }
}
