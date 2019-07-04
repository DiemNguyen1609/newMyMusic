package com.example.mymusic;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    private SeekBar volumeBar;
    public static ImageButton btnPlay;
    public static ImageButton btnForward;
    public static ImageButton btnBackward;
    public static ImageButton btnNext;
    public static ImageButton btnPrevious;
    public static ImageButton btnPlaylist;
    public static ImageButton btnRepeat;
    public static ImageButton btnShuffle;
    public static SeekBar songProgressBar;
    public static TextView songTitleLabel;
    public static TextView songCurrentDurationLabel;
    public static TextView songTotalDurationLabel;
    public static ImageButton btnVolume;
    public static ImageButton btnVolumeMax;
    AudioManager audioManager;
    private boolean isFirst = false;
    private int currentSongIndex = 0;
    public static final int RUNTIME_PERMISSION_CODE = 7;
    ContentResolver contentResolver;
    Cursor cursor;
    Uri uri;
    Context context;
    private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
    public Intent intentSer = new Intent();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_2);
        // All player buttons
        btnVolume = (ImageButton) findViewById(R.id.btnVolume);
        btnVolumeMax = (ImageButton) findViewById(R.id.btnVolumeMax);
        volumeBar = (SeekBar) findViewById(R.id.volumeBar);
        btnPlay = (ImageButton) findViewById(R.id.btnPlay);
        btnForward = (ImageButton) findViewById(R.id.btnForward);
        btnBackward = (ImageButton) findViewById(R.id.btnBackward);
        btnNext = (ImageButton) findViewById(R.id.btnNext);
        btnPrevious = (ImageButton) findViewById(R.id.btnPrevious);
        btnPlaylist = (ImageButton) findViewById(R.id.btnPlaylist);
        btnRepeat = (ImageButton) findViewById(R.id.btnRepeat);
        btnShuffle = (ImageButton) findViewById(R.id.btnShuffle);
        songProgressBar = (SeekBar) findViewById(R.id.songProgressBar);
        songTitleLabel = (TextView) findViewById(R.id.songTitle);
        songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
        songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);


        context = getApplicationContext();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);



         //Request get all media in device
        AndroidRuntimePermission();
        GetAllMediaMp3Files();

        if(songsList.size()==0)
        {
            HashMap<String,String> songTemp=new HashMap<String,String>();
            songTemp.put("Anhchuatungbiet","R.raw.anhchuatungbiet");
            songsList.add(songTemp);
        }
        btnVolume.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (volumeBar.getVisibility() == View.INVISIBLE) {
                    volumeBar.setVisibility(View.VISIBLE);
                    btnVolumeMax.setVisibility(View.VISIBLE);

                } else {
                    volumeBar.setVisibility(View.INVISIBLE);
                    btnVolumeMax.setVisibility(View.INVISIBLE);
                }
            }
        });
        volumeBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volumeBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        volumeBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                        Toast.makeText(getApplicationContext(), "Volume: " + progress, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );
        btnVolumeMax.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volumeBar.getProgress() + 1, 0);
                volumeBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
                Toast.makeText(getApplicationContext(), "Volume: " + volumeBar.getProgress(), Toast.LENGTH_SHORT).show();

            }
        });

        /**
         * Click event btn Playlist
         *
         * */
        btnPlaylist.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                intent.putExtra("musiclist", songsList);
                startActivityForResult(intent, 1);

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        intentSer = new Intent(this, PlayerInService.class);
        intentSer.putExtra("musiclist", songsList);
        if (isFirst == false) {
            startService(intentSer);
        }


    }

    public void GetAllMediaMp3Files() {

        contentResolver = context.getContentResolver();

        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        cursor = contentResolver.query(
                uri, // Uri
                null,
                null,
                null,
                null
        );

        if (cursor == null) {

            Toast.makeText(MainActivity.this, "Something Went Wrong.", Toast.LENGTH_LONG);

        } else if (!cursor.moveToFirst()) {

            Toast.makeText(MainActivity.this, "No Music Found on SD Card.", Toast.LENGTH_LONG);

        } else {

            int Title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            do {
                String fullPath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String SongTitle = cursor.getString(Title);
                HashMap<String, String> ListElementsArrayList = new HashMap<String, String>();
                ListElementsArrayList.put("songTitle", SongTitle);
                ListElementsArrayList.put("songPath", fullPath);
                songsList.add(ListElementsArrayList);

            } while (cursor.moveToNext());
        }
    }

    // Creating Runtime permission function.
    public void AndroidRuntimePermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    android.app.AlertDialog.Builder alert_builder = new android.app.AlertDialog.Builder(MainActivity.this);
                    alert_builder.setMessage("External Storage Permission is Required.");
                    alert_builder.setTitle("Please Grant Permission.");
                    alert_builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            ActivityCompat.requestPermissions(
                                    MainActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    RUNTIME_PERMISSION_CODE
                            );
                        }
                    });

                    alert_builder.setNeutralButton("Cancel", null);

                    AlertDialog dialog = alert_builder.create();

                    dialog.show();

                } else {

                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            RUNTIME_PERMISSION_CODE
                    );
                }
            } else {
//
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case RUNTIME_PERMISSION_CODE: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
            }
        }
    }

    /**
     * Receiving song index from playlist view
     * and play the song
     */
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            currentSongIndex = data.getExtras().getInt("songIndex");
            intentSer = new Intent(getApplicationContext(), PlayerInService.class);
            intentSer.putExtra("musiclist", songsList);
            intentSer.putExtra("songIndex", currentSongIndex);
            isFirst = true;
            intentSer.putExtra("isFirst", isFirst);
            startService(intentSer);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!PlayerInService.mp.isPlaying()) {
            PlayerInService.mp.stop();
            stopService(intentSer);
        } else {
            btnPlay.setBackgroundResource(R.drawable.pause96);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (!PlayerInService.mp.isPlaying()) {
                btnPlay.setBackgroundResource(R.drawable.play961);
            } else {
                btnPlay.setBackgroundResource(R.drawable.pause96);
            }
        } catch (Exception e) {
            Log.e("Exception", "" + e.getMessage() + e.getStackTrace() + e.getCause());
        }
    }


}
























