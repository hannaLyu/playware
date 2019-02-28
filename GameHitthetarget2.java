package com.example.playware;


import android.os.Handler;


import com.livelife.motolibrary.AntData;
import com.livelife.motolibrary.Game;
import com.livelife.motolibrary.MotoConnection;

import java.util.Random;


import static com.livelife.motolibrary.AntData.EVENT_PRESS;
import static com.livelife.motolibrary.AntData.LED_COLOR_BLUE;
import static com.livelife.motolibrary.AntData.LED_COLOR_OFF;
import static com.livelife.motolibrary.AntData.LED_COLOR_RED;



public class GameHitthetarget2 extends Game {
    private MotoConnection connection = MotoConnection.getInstance();
    private int currenttile1;
    private int currenttile2;
    private int timeInterval = 1000;
    int timestep = 100;
    Handler handler = new Handler();
    private Runnable gameRunnable = new Runnable() {
        @Override
        public void run() {
            int tile1 = getRandomTile1();
            int tile2 = getRandomTile1();
            currenttile1 = tile1;
            currenttile2 = tile2;
            for (int t : connection.connectedTiles) {
                if (tile1 == t) {
                    connection.setTileColor(LED_COLOR_BLUE, tile1);

                } else if (tile2 == t) {
                    connection.setTileColor(LED_COLOR_RED, tile2);
                } else {
                    connection.setTileColor(LED_COLOR_OFF, t);
                }


            }
            handler.postDelayed(gameRunnable, timeInterval);
        }
    };
    /*@Override*/

        /*@Override
        public void run() {
            int tile2 = getRandomTile();
            for (int t : connection.connectedTiles) {
                if (tile2 == t) {
                    connection.setTileColor(LED_COLOR_RED, tile2);
                } else {
                    connection.setTileColor(LED_COLOR_OFF, t);
                }
                currenttile2 = tile2;
                handler.postDelayed(gameRunnable,timeInterval);
            }*/

    @Override
    public void onGameStart() {
        super.onGameStart();

        connection.setAllTilesIdle(LED_COLOR_OFF);
        currenttile1 = connection.randomIdleTile();
        currenttile2 = connection.randomIdleTile();
        connection.setTileColor(LED_COLOR_RED, currenttile1);
        connection.setTileColor(LED_COLOR_BLUE, currenttile2);
        handler.postDelayed(gameRunnable, timeInterval);
    }

    @Override
    public void onGameUpdate(byte[] message) {
        super.onGameUpdate(message);
        int event = AntData.getCommand(message);
        int tileID = AntData.getId(message);
        if (event == EVENT_PRESS) {
            int color = AntData.getColorFromPress(message);
            if (tileID == currenttile1 || tileID == currenttile2) {
                timeInterval -= timestep;
            } else {
                timeInterval += timestep;
            }
            if (timeInterval <= timestep) {
                timeInterval = timestep;
            }
        }
    }


//    public void onGameUpdate([byte]message) {
//        super.onGameUpdate(message);
//    }

    @Override
    public void onGameEnd() {
        super.onGameEnd();
    }


    int getRandomTile1() {
        Random random = new Random();
        for (int j = 0; j < connection.connectedTiles.size(); j++) {
            int tile = random.nextInt(connection.connectedTiles.size());
            if (connection.connectedTiles.get(tile) != currenttile1) {
                return connection.connectedTiles.get(tile);
        }
        }
        return -1;
    }
    int getRandomTile2() {
        Random random = new Random();
        for (int j = 0; j < connection.connectedTiles.size(); j++) {
            int tile = random.nextInt(connection.connectedTiles.size());
            if (connection.connectedTiles.get(tile) != currenttile2&&connection.connectedTiles.get(tile) != currenttile1) {
                return connection.connectedTiles.get(tile);
            }
        }
        return -1;
    }
    /*int getRandomTile2() {
        Random random = new Random();
        while (true) {
            int tile = random.nextInt(connection.connectedTiles.size());
            if (connection.connectedTiles.get(tile) != currenttile2&&connection.connectedTiles.get(tile) != currenttile1) {
                return connection.connectedTiles.get(tile);
            }
        }
    }*/
    }
