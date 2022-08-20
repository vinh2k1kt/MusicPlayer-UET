package com.example.music_player_svmc.Activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.music_player_svmc.Application.MusicPlayerApp;
import com.example.music_player_svmc.Database.DBHelper;
import com.example.music_player_svmc.Fragment.PlayControlBottomSheetFragment;
import com.example.music_player_svmc.Model.Song;
import com.example.music_player_svmc.R;
import com.example.music_player_svmc.Service.BoundService;
import com.example.music_player_svmc.Utility.Constant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PlayControlActivity extends AppCompatActivity implements PlayControlBottomSheetFragment.IOnItemSelectedListener {

    private ImageButton playPauseBtn, previousBtn, nextBtn, loopBtn, shuffleBtn, showBtn, favoriteBtn, backBtn;
    private ImageView songPicture;
    private TextView songName, singerName;
    private TextView duration, runtime;
    private SeekBar seekBar;
    public MusicPlayerApp app;
    private List<Song> songList;
    private int position = 0;

    private PlayControlBottomSheetFragment bottomSheetFragment;
    private BoundService boundService;
    private boolean isBound = false;

    private Constant.Status favoriteStatus = Constant.Status.OFF;
    private DBHelper mDBHelper;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            BoundService.LocalBinder binder = (BoundService.LocalBinder) service;
            boundService = binder.getService();
            isBound = true;
            boundService.isConnectToPlayControl = true;

            if (getIntent().getBooleanExtra(Constant.SHUFFLE_KEY, false)) {
                if (boundService.shuffleStatus == Constant.Status.OFF)
                    shuffle();
                else {
                    shuffle();
                    shuffle();
                }
            } else if (boundService.shuffleStatus == Constant.Status.ON) {
                shuffle();
            }

            if (boundService.shuffleStatus == Constant.Status.ON) {
                songList = boundService.songList;
                shuffleBtn.setImageResource(R.drawable.ic_shuffle_on);

                if (bottomSheetFragment != null)
                    bottomSheetFragment.updateSongListAdapter(songList);
            }
            if (boundService.loopStatus == Constant.Status.SINGLE) {
                loopBtn.setImageResource(R.drawable.ic_repeat_one);
            }

            setTimeTotal();
            setInfoToLayout(boundService.getCurrentSong());
            updateTimeSong();
            playPauseBtn.setImageResource((boundService.playStatus == Constant.Status.ON) ? R.drawable.ic_pause : R.drawable.ic_play_arrow);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
            boundService.isConnectToPlayControl = false;
        }
    };

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constant.ACTION_CHANGED_LAYOUT.equals(intent.getAction())) {
                playPauseBtn.setImageResource((boundService.playStatus == Constant.Status.ON) ? R.drawable.ic_pause : R.drawable.ic_play_arrow);
                setInfoToLayout(boundService.getCurrentSong());
                setTimeTotal();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDBHelper = new DBHelper(this);

        Bundle bundle = getIntent().getExtras();
        songList = bundle.getParcelableArrayList(Constant.PLAYLIST_KEY);
        position = (int) bundle.get(Constant.POSITION_KEY);

        app = (MusicPlayerApp) getApplication();
        setContentView(R.layout.activity_play_control);
        bindingLayout();
        backBtn.setOnClickListener(view -> back());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                boundService.mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        Intent intent = new Intent(this, BoundService.class);
        intent.putParcelableArrayListExtra(Constant.PLAYLIST_KEY, (ArrayList<? extends Parcelable>) songList);
        intent.putExtra(Constant.POSITION_KEY, position);
        if (!getIntent().getBooleanExtra(Constant.FROM_HOME_TO_PLAYCONTROL, false)) {
            startForegroundService(intent);
        }
        bindService(intent, connection, BIND_AUTO_CREATE);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ACTION_CHANGED_LAYOUT);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            isBound = false;
            boundService.isConnectToPlayControl = false;
            unbindService(connection);
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        finish();
    }

    private void bindingLayout() {
        playPauseBtn = findViewById(R.id.playPauseBtn);
        nextBtn = findViewById(R.id.nextBtn);
        loopBtn = findViewById(R.id.loopBtn);
        previousBtn = findViewById(R.id.previousBtn);
        shuffleBtn = findViewById(R.id.shuffleBtn);
        showBtn = findViewById(R.id.showPlaylist);
        favoriteBtn = findViewById(R.id.favoriteBtn);
        songName = findViewById(R.id.songName);
        songName.setSelected(true);
        songName.setMovementMethod(new ScrollingMovementMethod());
        songName.setHorizontallyScrolling(true);
        singerName = findViewById(R.id.singerName);
        duration = findViewById(R.id.textViewTotalTime);
        runtime = findViewById(R.id.textViewRunTime);
        seekBar = findViewById(R.id.seekBartime);
        songPicture = findViewById(R.id.thumnail);
        backBtn = findViewById(R.id.backBtn);

        nextBtn.setOnClickListener(v -> next());
        previousBtn.setOnClickListener(v -> previous());
        playPauseBtn.setOnClickListener(v -> playPause());
        shuffleBtn.setOnClickListener(v -> shuffle());
        loopBtn.setOnClickListener(v -> loop());
        showBtn.setOnClickListener(v -> showBottomSheetPlaylist());
        favoriteBtn.setOnClickListener(view -> {
            addToFavorite(boundService.getCurrentSong());
        });
    }

    private void addToFavorite(Song song) {
        switch (favoriteStatus) {
            case OFF:
                favoriteBtn.setImageResource(R.drawable.ic_favorite);
                favoriteStatus = Constant.Status.ON;
                mDBHelper.addSongToPlaylist(song, "Favorites");
                Toast.makeText(this, "Đã thêm bài hát vào Favorite!", Toast.LENGTH_SHORT).show();
                break;
            case ON:
                favoriteBtn.setImageResource(R.drawable.ic_favorite_border);
                favoriteStatus = Constant.Status.OFF;
                mDBHelper.removeSongInPlaylist(song, "Favorites");
                Toast.makeText(this, "Đã xóa bài hát khỏi Favorite!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void showBottomSheetPlaylist() {
        bottomSheetFragment = new PlayControlBottomSheetFragment(boundService.songList, boundService.shuffleStatus, boundService.loopStatus);
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    private void loop() {
        boundService.loop();
        loopBtn.setImageResource(boundService.loopImageResource);
    }

    private void shuffle() {
        boundService.shuffle();
        shuffleBtn.setImageResource(boundService.shuffleImageResource);
        if (bottomSheetFragment != null) {
            bottomSheetFragment.updateSongListAdapter(new ArrayList<>(boundService.getSongList()));
        }
    }

    private void playPause() {
        if (boundService.mediaPlayer.isPlaying()) {
            playPauseBtn.setImageResource(R.drawable.ic_play_arrow);
            boundService.pause();
        } else {
            playPauseBtn.setImageResource(R.drawable.ic_pause);
            boundService.play();
        }
    }

    private void next() {
        boundService.next();
        if (boundService.mediaPlayer != null) {
            setTimeTotal();
            setInfoToLayout(boundService.getCurrentSong());
        }
        playPauseBtn.setImageResource(R.drawable.ic_pause);
    }

    private void previous() {
        boundService.previous();
        if (boundService.mediaPlayer != null) {
            setTimeTotal();
            setInfoToLayout(boundService.getCurrentSong());
        }
        playPauseBtn.setImageResource(R.drawable.ic_pause);
    }

    private void back() {
        finish();
        onBackPressed();
    }

    private void setTimeTotal() {
        SimpleDateFormat time = new SimpleDateFormat("mm:ss");
        duration.setText(time.format(boundService.mediaPlayer.getDuration()));
        seekBar.setMax(boundService.mediaPlayer.getDuration());
    }

    private void setInfoToLayout(Song song) {
        runOnUiThread(() -> {
            songName.setText(song.getSongName());
            songName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            singerName.setText(song.getSongSinger());
            if (song.isHasPic()) {
                song.loadEmbeddedPicture();
                songPicture.setImageBitmap(song.getSongEmbeddedPicture());
            } else {
                songPicture.setImageResource(song.getSongImage());
            }
            if (mDBHelper.checkSong(song, "Favorites")) {
                favoriteBtn.setImageResource(R.drawable.ic_favorite);
                favoriteStatus = Constant.Status.ON;
            } else {
                favoriteBtn.setImageResource(R.drawable.ic_favorite_border);
                favoriteStatus = Constant.Status.OFF;
            }
        });
    }

    private void updateTimeSong() {
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (boundService.mediaPlayer != null && isBound) {
                    boundService.mediaPlayer.setOnCompletionListener(mp -> next());
                    SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");
                    runtime.setText(timeFormat.format(boundService.mediaPlayer.getCurrentPosition()));
                    seekBar.setProgress(boundService.mediaPlayer.getCurrentPosition());
                }
                handler.postDelayed(this, 200);
            }
        }, 200);
    }

    @Override
    public void getSong(Song song) {
        Intent intent = new Intent(this, BoundService.class);
        for (Song s : boundService.songList) {
            if (s.getSongName().equals(song.getSongName())) {
                intent.putExtra(Constant.POSITION_KEY, boundService.songList.indexOf(s));
                break;
            }
        }
        startService(intent);
    }

    @Override
    public void getShuffleStatus() {
        shuffle();
    }

    @Override
    public void getLoopStatus() {
        loop();
    }
}