package com.example.playware;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.livelife.motolibrary.MotoConnection;
import com.livelife.motolibrary.MotoSound;
import com.livelife.motolibrary.OnAntEventListener;

public class MainActivity extends AppCompatActivity implements OnAntEventListener {
    MotoConnection connection = MotoConnection.getInstance();
    MotoSound Sound = MotoSound.getInstance();
    TextView statusTextView;
    Button paringButton;
    Button startGameButton;
    boolean isParing = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        Sound.initializeSounds(this);
        connection=MotoConnection.getInstance();
        connection.startMotoConnection(MainActivity.this);
        connection.saveRfFrequency(66); //(Group No.)*10+6
        connection.setDeviceId(6); //Your group number

        statusTextView = findViewById(R.id.statusTextView);
        startGameButton = findViewById(R.id.startGameButton);
        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connection.unregisterListener(MainActivity.this);
                Intent i = new Intent(MainActivity.this, GameActivity.class);
                startActivity(i);
            }

        });
        connection.registerListener(MainActivity.this);
        super.onCreate(savedInstanceState);

        paringButton = findViewById(R.id.paringButton);
        paringButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isParing){
                    connection.pairTilesStart();
                    paringButton.setText("Stop Paring");
                } else {
                    connection.pairTilesStop();
                    paringButton.setText("Start Paring");
                }
                isParing = !isParing;
            }
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        connection.stopMotoConnection();
        connection.unregisterListener(MainActivity.this);
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        connection.startMotoConnection(MainActivity.this);
        connection.registerListener(MainActivity.this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        connection.stopMotoConnection();
        connection.unregisterListener(MainActivity.this);
    }
    public void onMessageReceived(byte[] bytes, long l) {
    }
    @Override
    public void onAntServiceConnected() {
        connection.setAllTilesToInit();
    }
    @Override
    public void onNumbersOfTilesConnected(final int i) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusTextView.setText(i+" connected tiles");
            }
        });
    }
}