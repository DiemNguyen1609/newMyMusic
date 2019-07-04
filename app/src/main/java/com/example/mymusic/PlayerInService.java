package com.example.mymusic;


import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class PlayerInService extends Service implements OnClickListener, MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {

    private static WeakReference<ImageButton> btnPlay;
    private static WeakReference<ImageButton> btnForward;
    private static WeakReference<ImageButton> btnBackward;
    private static WeakReference<ImageButton> btnNext;
    private static WeakReference<ImageButton> btnPrevious;
    private static WeakReference<ImageButton> btnRepeat;
    private static WeakReference<ImageButton> btnShuffle;
    private static WeakReference<SeekBar> songProgressBar;
    private static WeakReference<TextView> songTitleLabel;
    private static WeakReference<TextView> songCurrentDurationLabel;
    private static WeakReference<TextView> songTotalDurationLabel;
    public static MediaPlayer mp;

    // Handler to update UI timer, progress bar etc,.
    private static Handler mHandler = new Handler();
    private int seekForwardTime = 5000; // 5000 milliseconds
    private int seekBackwardTime = 5000; // 5000 milliseconds
    private int currentSongIndex = 0;
    private boolean isShuffle = false;
    private boolean isRepeat = false;


    private static ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

    private static boolean isFirst = false;


    @Override
    public void onCreate() {
        super.onCreate();
        mp = new MediaPlayer();
        mp.reset();
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);

    }

    @SuppressWarnings("deprecation")
    @Override
    public int onStartCommand(Intent intent, int flags, int starId) {
        super.onStart(intent, starId);
        super.onStartCommand(intent, flags, starId);
        initUI();
        songsList = (ArrayList<HashMap<String, String>>) intent.getSerializableExtra("musiclist");
        isFirst = intent.getBooleanExtra("isFirst", false);
        int head = intent.getIntExtra("head", 0);
        if (head == 1) {
            btnPlay.get().setImageResource(R.drawable.play961);
        } else if (isFirst == true) {
            currentSongIndex = intent.getIntExtra("songIndex", 0);
            playSong(currentSongIndex);
        }
        if (mp.isPlaying() && isFirst == false) {
            btnPlay.get().setImageResource(R.drawable.pause96);
            songTitleLabel.get().setText(songsList.get(currentSongIndex).get("songTitle"));
        } else if (!mp.isPlaying() && isFirst == false) {
            btnPlay.get().setImageResource(R.drawable.play961);
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // reference to id of buttons
    private void initUI() {
        btnPlay = new WeakReference<>(MainActivity.btnPlay);
        btnForward = new WeakReference<>(MainActivity.btnForward);
        btnBackward = new WeakReference<>(MainActivity.btnBackward);
        btnNext = new WeakReference<>(MainActivity.btnNext);
        btnPrevious = new WeakReference<>(MainActivity.btnPrevious);
        btnRepeat = new WeakReference<>(MainActivity.btnRepeat);
        btnShuffle = new WeakReference<>(MainActivity.btnShuffle);
        songProgressBar = new WeakReference<>(MainActivity.songProgressBar);
        songTitleLabel = new WeakReference<>(MainActivity.songTitleLabel);
        songCurrentDurationLabel = new WeakReference<>(MainActivity.songCurrentDurationLabel);
        songTotalDurationLabel = new WeakReference<>(MainActivity.songTotalDurationLabel);
        songProgressBar.get().setOnSeekBarChangeListener(this);
        btnPlay.get().setOnClickListener(this);
        btnForward.get().setOnClickListener(this);
        btnBackward.get().setOnClickListener(this);
        btnNext.get().setOnClickListener(this);
        btnPrevious.get().setOnClickListener(this);
        btnRepeat.get().setOnClickListener(this);
        btnShuffle.get().setOnClickListener(this);

        mp.setOnCompletionListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /**
             * Forward button click event
             * Forwards song specified seconds
             * */
            case R.id.btnForward:
                int currentPosition = mp.getCurrentPosition();
                // check if seekForward time is lesser than song duration
                if (currentPosition + seekForwardTime <= mp.getDuration()) {
                    // forward song
                    mp.seekTo(currentPosition + seekForwardTime);
                } else {
                    // forward to end position
                    mp.seekTo(mp.getDuration());
                }

                break;
            /**
             * Backward button click event
             * Backward song to specified seconds
             * */
            case R.id.btnBackward:

                int currentPosition1 = mp.getCurrentPosition();
                // check if seekBackward time is greater than 0 sec
                if (currentPosition1 - seekBackwardTime >= 0) {
                    // forward song
                    mp.seekTo(currentPosition1 - seekBackwardTime);
                } else {
                    // backward to starting position
                    mp.seekTo(0);
                }

                break;
            /**
             * Next button click event
             * Plays next song by taking currentSongIndex + 1
             * */
            case R.id.btnNext:

                if (currentSongIndex < (songsList.size() - 1)) {
                    playSong(currentSongIndex + 1);
                    currentSongIndex = currentSongIndex + 1;
                } else {
                    // play first song
                    playSong(0);
                    currentSongIndex = 0;
                }

                break;
            /**
             * Back button click event
             * Plays previous song by currentSongIndex - 1
             * */
            case R.id.btnPrevious:

                if (currentSongIndex > 0) {
                    playSong(currentSongIndex - 1);
                    currentSongIndex = currentSongIndex - 1;
                } else {
                    // play last song
                    playSong(songsList.size() - 1);
                    currentSongIndex = songsList.size() - 1;
                }

                break;
            /**
             * Button Click event for Repeat button
             * Enables repeat flag to true
             * */
            case R.id.btnRepeat:

                if (isRepeat) {
                    isRepeat = false;
                    Toast.makeText(getApplicationContext(), "Repeat is OFF", Toast.LENGTH_SHORT).show();
                    btnRepeat.get().setImageResource(R.drawable.btn_repeat);
                } else {
                    // make repeat to true
                    isRepeat = true;
                    Toast.makeText(getApplicationContext(), "Repeat is ON", Toast.LENGTH_SHORT).show();
                    // make shuffle to false
                    isShuffle = false;
                    btnRepeat.get().setImageResource(R.drawable.btn_repeat_focused);
                    btnShuffle.get().setImageResource(R.drawable.btn_shuffle);
                }

                break;
            /**
             * Button Click event for Shuffle button
             * Enables shuffle flag to true
             * */
            case R.id.btnShuffle:

                if (isShuffle) {
                    isShuffle = false;
                    Toast.makeText(getApplicationContext(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();
                    btnShuffle.get().setImageResource(R.drawable.btn_shuffle);
                } else {
                    // make repeat to true
                    isShuffle = true;
                    Toast.makeText(getApplicationContext(), "Shuffle is ON", Toast.LENGTH_SHORT).show();
                    // make shuffle to false
                    isRepeat = false;
                    btnShuffle.get().setImageResource(R.drawable.btn_shuffle_focused);
                    btnRepeat.get().setImageResource(R.drawable.btn_repeat);
                }

                break;
            /**
             * Button Click event for play/pause button
             *
             * */
            case R.id.btnPlay:

                if (isFirst) {
                    if (mp.isPlaying() && isFirst) {
                        mp.pause();
                        btnPlay.get().setImageResource(R.drawable.play961);
                    } else if (mp.isPlaying() == false && isFirst) {
                        mp.start();
                        btnPlay.get().setImageResource(R.drawable.pause96);
                    }
                } else {
                    if (!mp.isPlaying()) {
                        playSong(0);
                        isFirst = true;
                    } else {
                        mp.pause();
                        btnPlay.get().setImageResource(R.drawable.play961);
                        isFirst = true;
                    }
                }

                break;
        }
    }

    /**
     * Function to play a song
     *
     * @param songIndex - index of song
     */
    public void playSong(int songIndex) {
        // Play song
        try {

            mp.reset();
            mp.setDataSource(songsList.get(songIndex).get("songPath"));
            mp.prepare();
            mp.start();
            // Displaying Song title
            String songTitle = songsList.get(songIndex).get("songTitle");
            songTitleLabel.get().setText(songTitle);

            // Changing Button Image to pause image
            btnPlay.get().setImageResource(R.drawable.pause96);

            // set Progress bar values
            songProgressBar.get().setProgress(0);
            songProgressBar.get().setMax(100);

            // Updating progress bar
            updateProgressBar();

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update timer on seekbar
     */
    public static void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     * Background Runnable thread
     */
    static Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mp.getDuration();
            long currentDuration = mp.getCurrentPosition();

            // Displaying Total Duration time
            songTotalDurationLabel.get().setText("" + Utilities.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            songCurrentDurationLabel.get().setText("" + Utilities.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            int progress = (int) (Utilities.getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            songProgressBar.get().setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };

    /**
     *
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

    }

    /**
     * When user starts moving the progress handler
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    /**
     * When user stops moving the progress hanlder
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mp.getDuration();
        int currentPosition = Utilities.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        mp.seekTo(currentPosition);

        // update timer progress again
        updateProgressBar();
    }

    /**
     * On Song Playing completed
     * if repeat is ON play same song again
     * if shuffle is ON play random song
     */
    @Override
    public void onCompletion(MediaPlayer arg0) {

        // check for repeat is ON or OFF
        if (isRepeat) {
            // repeat is on play same song again
            playSong(currentSongIndex);
        } else if (isShuffle) {
            // shuffle is on - play a random song
            Random rand = new Random();
            currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
            playSong(currentSongIndex);
        } else {
            // no repeat or shuffle ON - play next song
            if (currentSongIndex < (songsList.size() - 1)) {
                playSong(currentSongIndex + 1);
                currentSongIndex = currentSongIndex + 1;
            } else {
                // play first song
                playSong(0);
                currentSongIndex = 0;
            }
        }
    }

    @Override
    public void onDestroy() {
    }


}


