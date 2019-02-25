package com.example.playware;


import android.content.Context;

import com.livelife.motolibrary.AntData;
import com.livelife.motolibrary.Game;
import com.livelife.motolibrary.GameType;
import com.livelife.motolibrary.LocalizationApi;
import com.livelife.motolibrary.MotoConnection;
import com.livelife.motolibrary.MotoSound;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by Max on 04/05/2018.
 */

public class ColorRace extends Game {

    MotoConnection connection = MotoConnection.getInstance();
    MotoSound sound = MotoSound.getInstance();
    LocalizationApi loc = LocalizationApi.getInstance();

    //HashMap<Integer,Integer> lastTilePress = new HashMap<>();

    public ColorRace(Context c) {
        setName("Color Race");
        setMaxPlayers(1);
        setGameId(1);
        for (int i = 1; i <= getMaxPlayers(); i++) {
            GameType gt1 = new GameType(i, GameType.GAME_TYPE_TIME,30,i+" "+loc.getLocText(c, R.string.Player)+" 30 "+loc.getLocText(c, R.string.sec),i);
            addGameType(gt1);
        }

        selectedGameType = getGameTypes().get(0);
    }

    @Override
    public void onGameStart() {
        super.onGameStart();

        connection.setAllTilesIdle(AntData.LED_COLOR_OFF);
        int color = 0;
        for(int p=0;p<selectedGameType.getNumPlayers();p++) {
            int tileId = connection.randomIdleTile();
            connection.setTileColor(MotoConnection.playerColor[color],tileId);
            color++;
        }
    }

    @Override
    public void onGameEnd() {

        super.onGameEnd();

        ArrayList<Integer> winners = getMaxScorePlayers();

        if(winners.size() == 1) {
            int playerIndex = winners.get(0);
            connection.setAllTilesBlink(4, MotoConnection.playerColor[playerIndex]);
        }
        else {
            int winnerIndex = 0;
            for (int tile: connection.connectedTiles) {
                int playerIndex = getMaxScorePlayers().get(winnerIndex);
                connection.setTileBlink(4, MotoConnection.playerColor[winnerIndex],tile);
                winnerIndex++;
                if (winnerIndex>=getMaxScorePlayers().size()) winnerIndex = 0;
            }
        }

        sound.playStop();
    }

    int lastTileId = 0;
    @Override
    public void onGameUpdate(byte[] message) {
        super.onGameUpdate(message);

        int tileId = AntData.getId(message);
        int event = AntData.getCommand(message);

        if (event == AntData.EVENT_PRESS) {

            if (tileId == lastTileId) return;

            lastTileId = tileId;
            int color = AntData.getColorFromPress(message);

            int pIndex = 0;
            switch (color) {
                case 1:
                    pIndex = 0;
                    sound.playPress1();
                    break;
                case 2:
                    pIndex = 1;
                    sound.playPress2();
                    break;
                case 3:
                    pIndex = 2;
                    sound.playPress3();
                    break;
                case 7:
                    pIndex = 3;
                    sound.playPress4();
                    break;
            }

            /*
            if (!lastTilePress.containsKey(pIndex)) {
                lastTilePress.put(pIndex,tileId);
            } else {
                int lastTile = lastTilePress.get(pIndex);
                if (lastTile == tileId) {
                    return;
                }
            }
            */

            int nextTileId = connection.randomIdleTile();
            connection.setTileIdle(color,tileId);
            connection.setTileColor(color,nextTileId);


            int score = incrementPlayerScore(1,pIndex);

            if (selectedGameType.getType() == GameType.GAME_TYPE_SCORE) {
                if (score == selectedGameType.getGoal()) {
                    stopGame();
                }
            }


        }
    }


}
