package com.atifnaseem.musicmusic;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    ImageView ivBackground, ivAlbum;
    ImageView btnPlay;

    TextView tvRemainingTime, tvPlayTime,tvSongName, tvSinger;

    Intent playingIntent;
    MusicService musicService;
    boolean isBound = false;

    SeekBar sb;
    int seekValue = 0;



    int songNum = 0;
    boolean playingMusic = false;
    String[] songName = {
            "Burn",
            "Love me like you do",
            "Something just like this"
    };

    String[] singer = {
            "Ellie Goulding", "Ellie Goulding", "Chainsmokers"
    };


    Resources res;
    TypedArray images;
    TypedArray albums;
    Drawable drawableImage, drawableAlbum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ivBackground = (ImageView) findViewById(R.id.iv_background);
        ivAlbum = (ImageView) findViewById(R.id.iv_album);

        btnPlay = (ImageView) findViewById(R.id.iv_play);

        tvRemainingTime = (TextView) findViewById(R.id.tv_remainingTime);
        tvPlayTime = (TextView) findViewById(R.id.tv_playTime);
        tvSongName = (TextView) findViewById(R.id.tv_songName);
        tvSinger = (TextView) findViewById(R.id.tv_singer);

        res = getResources();
        images = res.obtainTypedArray(R.array.backgrounds);
        albums= res.obtainTypedArray(R.array.albums);


        sb = (SeekBar) findViewById(R.id.sb_song);

        playingIntent = new Intent(MainActivity.this, MusicService.class);


        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekValue = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                musicService.setPlayerProgress(seekValue);
            }
        });
    }


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
            musicService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };




    public void NextSong(View view) {
        songNum++;
        if(songNum >= songName.length){
            songNum = 0;
        }
        if(playingMusic){
            //Stop service
            playingIntent = new Intent(MainActivity.this, MusicService.class);
            stopService(playingIntent);
        }
        PlaySong();
    }



    public void PreviousSong(View view) {
        songNum--;
        if(songNum < 0){
            songNum = (songName.length - 1);
        }
        if(playingMusic){
            //Stop service
            playingIntent = new Intent(MainActivity.this, MusicService.class);
            stopService(playingIntent);
        }

        PlaySong();
    }











    public void PlaySong(){
        //Start next song
        tvSongName.setText(songName[songNum]);
        tvSinger.setText("Loading...");
        btnPlay.setImageResource(R.drawable.mp_pause);
        playingIntent = new Intent(MainActivity.this, MusicService.class);
        playingIntent.putExtra("songNum", songNum);
        bindService(playingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        startService(playingIntent);
        playingMusic = true;
        tvSongName.setText(songName[songNum]);
        tvSinger.setText(singer[songNum]);

        drawableImage = images.getDrawable(songNum);
        drawableAlbum = albums.getDrawable(songNum);
        ivBackground.setImageDrawable(drawableImage);
        ivAlbum.setImageDrawable(drawableAlbum);

        SeekThread seekThread = new SeekThread();
        seekThread.start();
    }










    public void PlaySong(View view) {
        if(playingMusic) {
            btnPlay.setImageResource(R.drawable.mp_play);
            playingIntent = new Intent(MainActivity.this, MusicService.class);
            unbindService(serviceConnection);
            stopService(playingIntent);
            playingMusic = false;
        }else{
            PlaySong();
        }
    }









    class SeekThread extends Thread {
        public void run() {
            while(true){
                try{
                    Thread.sleep(1000);
                }catch (Exception e){
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sb.setProgress(musicService.getMusicPrgress());
                    }
                });
            }
        }
    }
}
