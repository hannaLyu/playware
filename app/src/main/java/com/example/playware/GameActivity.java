package com.example.playware;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.playware.R;
import com.livelife.motolibrary.Game;
import com.livelife.motolibrary.GameType;
import com.livelife.motolibrary.MotoConnection;
import com.livelife.motolibrary.MotoSound;
import com.livelife.motolibrary.OnAntEventListener;

public class GameActivity extends AppCompatActivity implements OnAntEventListener {

    MotoConnection connection = MotoConnection.getInstance();
    MotoSound sound = MotoSound.getInstance();

    ColorRace colorRace;
    LinearLayout gameTypeContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        colorRace = new ColorRace(GameActivity.this);
        connection.registerListener(this);

        gameTypeContainer = findViewById(R.id.gameTypeContainer);

        for (final GameType gt: colorRace.getGameTypes()) {
            Button b = new Button(this);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    colorRace.selectedGameType = gt;
                    sound.playStart();
                    colorRace.startGame();
                }
            });
            b.setText(gt.getName());
            gameTypeContainer.addView(b);
        }

        colorRace.setOnGameEventListener(new Game.OnGameEventListener() {
            @Override
            public void onGameTimerEvent(int i) {

            }

            @Override
            public void onGameScoreEvent(int score, int playerIndex) {

            }

            @Override
            public void onGameStopEvent() {

            }

            @Override
            public void onSetupMessage(String s) {

            }

            @Override
            public void onGameMessage(String s) {

            }

            @Override
            public void onSetupEnd() {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        connection.unregisterListener(this);
    }

    @Override
    public void onMessageReceived(byte[] bytes, long l) {
        colorRace.addEvent(bytes);
    }

    @Override
    public void onAntServiceConnected() {

    }

    @Override
    public void onNumbersOfTilesConnected(int i) {

    }
}
