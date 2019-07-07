package com.example.musicbox;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private MediaPlayer mediaPlayer;
    private ImageView imageViewCover;
    private TextView startTimeTV, endTimeTV;
    private SeekBar seekBar;
    private Button prevBtn, playBtn, nextBtn;
    private Thread thread;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find View By Id
        setUpUI();

        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    mediaPlayer.seekTo(progress);
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
                int currentPosition = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();

                startTimeTV.setText(dateFormat.format(new Date(currentPosition)));
                endTimeTV.setText(dateFormat.format(new Date(duration - currentPosition)));


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    public void setUpUI(){

        mediaPlayer = new MediaPlayer();
        mediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.maula);

        imageViewCover = findViewById(R.id.imageViewId);
        startTimeTV = findViewById(R.id.startTimeId);
        endTimeTV = findViewById(R.id.endTimeId);
        seekBar = findViewById(R.id.seekBarId);
        prevBtn = findViewById(R.id.prevBtnId);
        playBtn = findViewById(R.id.playBtnId);
        nextBtn = findViewById(R.id.nextBtnId);

        prevBtn.setOnClickListener(this);
        playBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
    }


    // Button Click Listener
    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.prevBtnId:
                backMusic();
                break;

            case R.id.playBtnId:
                if (mediaPlayer.isPlaying()){
                    pauseMusic();
                }else {
                    startMusic();
                }
                break;

            case R.id.nextBtnId:
                nextMusic();
                break;

        }

    }

    // Pause Music OR Play Button
    public void pauseMusic(){
        if (mediaPlayer != null){
            mediaPlayer.pause();
            playBtn.setBackgroundResource(android.R.drawable.ic_media_play);
        }

    }

    // Music Start OR Play Button
    public void startMusic(){
        if (mediaPlayer != null){
            mediaPlayer.start();
            updateThread();
            playBtn.setBackgroundResource(android.R.drawable.ic_media_pause);
        }
    }

    // Back Button
    public void backMusic(){

        if (mediaPlayer.isPlaying()){
            mediaPlayer.seekTo(0);
        }

    }

    public void nextMusic(){
        if (mediaPlayer.isPlaying()){
            mediaPlayer.seekTo(mediaPlayer.getDuration());
        }
    }



    // SeekBar With Time
    public void updateThread(){

        thread = new Thread(){
            @Override
            public void run() {
                try {

                    while (mediaPlayer != null && mediaPlayer.isPlaying()){
                        Thread.sleep(50);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int newPosition = mediaPlayer.getCurrentPosition();
                                int newMax = mediaPlayer.getDuration();
                                seekBar.setMax(newMax);
                                seekBar.setProgress(newPosition);

                                startTimeTV.setText(String
                                        .valueOf(new java.text.SimpleDateFormat("mm:ss")
                                        .format(new Date(mediaPlayer.getCurrentPosition()))));

                                endTimeTV.setText(String
                                        .valueOf(new java.text.SimpleDateFormat("mm:ss")
                                        .format(new Date(mediaPlayer.getDuration()- mediaPlayer.getCurrentPosition()))));

                            }
                        });
                    }
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    @Override
    protected void onDestroy() {

        if (mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        thread.interrupt();
        thread = null;

        super.onDestroy();
    }
}
