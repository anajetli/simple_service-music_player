package com.atifnaseem.musicmusic;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;

/**
 * Created by AtifNaseem on 4/19/2018.
 */

public class MusicService extends Service {

    private final IBinder iBinder = new LocalBinder();

    public MusicService(){

    }

    String[] songs = {
            "http://the-sinner.net/download/music/%D1%8C%D0%B3%D1%8B%D1%88%D1%81/Ellie%20Goulding%20-%20Burn.mp3",
            "http://s1.mmdl.xyz/1396/11/20/Various%20Artists%20-%20Fifty%20Shades%20Freed%20%28Original%20Motion%20Picture%20Soundtrack%29/17%20Love%20Me%20Like%20You%20Do.mp3",
            "http://bitashop.org/Bita6/04.96/UK%20Top%2040%20Singles%20Chart/UK%20Top%2040%20Singles%20Chart/27.%20The%20Chainsmokers%20&%20Coldplay%20-%20Something%20Just%20Like%20This.mp3"
    };

    String[] songName = {
            "Burn",
            "Love me like you do",
            "Something just like this"
    };

    String[] singer = {
            "Ellie Goulding", "Ellie Goulding", "Chainsmokers"
    };


    public int songNum = 0;

    MediaPlayer player;




    public class LocalBinder extends Binder{
        MusicService getService(){
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return iBinder;
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        songNum = intent.getIntExtra("songNum", 0);
        //player = MediaPlayer.create(this, Uri.parse(songs[songNum]));
        player = new MediaPlayer();
        
        //player.setLooping(true); // Set looping
        try {
            player.setDataSource(songs[songNum]);
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.setVolume(100,100);
        player.start();
        return Service.START_STICKY;
    }


    @Override
    public void onDestroy() {
        player.stop();
        player.release();
    }


    public int getMusicPrgress(){
        return player.getCurrentPosition();
    }

    public int getMusicLength(){
        return player.getDuration();
    }


    public void setPlayerProgress(int progress){
        player.seekTo(progress);
    }

}
