package com.example.music_player_svmc.Service;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.music_player_svmc.Activity.HomeActivity;
import com.example.music_player_svmc.Activity.PlayControlActivity;
import com.example.music_player_svmc.Application.MusicPlayerApp;
import com.example.music_player_svmc.Model.Song;
import com.example.music_player_svmc.R;
import com.example.music_player_svmc.Utility.Constant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BoundService extends Service {

    public MediaPlayer mediaPlayer;
    public List<Song> songList;
    public List<Song> originalSongList;
    public int randomSeed = 0;
    public int position = 0;
    private final IBinder binder = new LocalBinder();
    public Constant.Status shuffleStatus = Constant.Status.OFF;
    public Constant.Status loopStatus = Constant.Status.WHOLE;
    public Constant.Status favoriteStatus = Constant.Status.OFF;
    public Constant.Status playStatus = Constant.Status.ON;
    public int loopImageResource, shuffleImageResource;
    public boolean isConnectToPlayControl = false;
    public boolean isConnectToHome = false;

    public class LocalBinder extends Binder {
        public BoundService getService() {
            return BoundService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("BoundService", "On Bind");
        sendNotificationMedia(songList.get(position));
        return binder;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.e("BoundService", "On ReBind");
        sendNotificationMedia(songList.get(position));
        super.onRebind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("BoundService", "On StartCommand ");
        if (intent.hasExtra(Constant.PLAYLIST_KEY)) {
            songList = intent.getParcelableArrayListExtra(Constant.PLAYLIST_KEY);
            originalSongList = new ArrayList<>(songList);
        }

        if (intent.hasExtra(Constant.POSITION_KEY)) {
            position = intent.getIntExtra(Constant.POSITION_KEY, 0);
            changedSong();
        }

        initMediaPlayer(songList.get(position).getSongURL());

        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case Constant.ACTION_NEXT:
                    next();
                    break;
                case Constant.ACTION_PAUSE:
                    if (playStatus == Constant.Status.OFF) play();
                    else pause();
                    break;
                case Constant.ACTION_PREV:
                    previous();
                    break;
            }
        }
        sendNotificationMedia(songList.get(position));
        return START_NOT_STICKY;
    }

    private boolean CompareList(List<Song> songList, List<Song> songList_2) {
        if (songList.size() != songList_2.size()) return false;
        else {
            for (int index = 0; index < songList.size(); index++) {
                if (songList.get(index).getSongName() != songList_2.get(index).getSongName())
                    return false;
            }
        }
        return true;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e("BoundService", "On UnBind " + isConnectToPlayControl);
        sendNotificationMedia(songList.get(position));
        return true;
    }

    @Override
    public void onDestroy() {
        Log.e("BoundService", "onDestroy");
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel("tag", Constant.NOTIFICATION_ID);
        super.onDestroy();
    }

    public void sendNotificationMedia(Song song) {
        MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(this, "tag");

        int pauseImageId;
        if (playStatus == Constant.Status.OFF) {
            pauseImageId = R.drawable.ic_play_arrow;
        } else {
            pauseImageId = R.drawable.ic_pause;
        }
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, MusicPlayerApp.CHANNEL_ID)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSubText("MusicPlayer")
                .setContentTitle(song.getSongName())
                .setSubText(song.getSongSinger())
                .setSmallIcon(R.drawable.ic_music)
                .addAction(R.drawable.ic_skip_previous, "Previous", sendPrevCommand()) // #0
                .addAction(pauseImageId, "Pause", sendPlayStatus())  // #1
                .addAction(R.drawable.ic_skip_next, "Next", sendNextCommand())  // #2
                .setDeleteIntent(sendCloseCommand())
                .setContentIntent(sendPopBackCommand())
                .setSound(null);

        notification.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(0, 1, 2 /* #1: pause button */)
        );

        mediaSessionCompat.setPlaybackState(new PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PLAYING, 0, 1f)
                .build());
        if (!song.isHasPic()) {
            notification.setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.ic_default_music));
        } else {
            song.loadEmbeddedPicture();
            notification.setLargeIcon(song.getSongEmbeddedPicture());
        }

        startForeground(Constant.NOTIFICATION_ID, notification.build());
        if (playStatus == Constant.Status.OFF && !isConnectToPlayControl && !isConnectToHome) {
            stopForeground(false);
        }
    }

    private PendingIntent sendPopBackCommand() {
        PendingIntent pendingIntent;
        if (!isConnectToHome && !isConnectToPlayControl) {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(Constant.ACTION_BACK, true);
            intent.putExtra(Constant.FROM_BOUND_SERVICE, true);
            intent.putParcelableArrayListExtra(Constant.PLAYLIST_KEY, (ArrayList<? extends Parcelable>) originalSongList);
            intent.putExtra(Constant.POSITION_KEY, originalSongList.indexOf(getCurrentSong()));
            pendingIntent = PendingIntent.getActivity(this, Constant.REQUEST_CODE_BACK
                    , intent, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            pendingIntent = PendingIntent.getActivity(this, Constant.REQUEST_CODE_BACK
                    , new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
        }
        return pendingIntent;
    }

    private PendingIntent sendCloseCommand() {
        Intent close_intent = new Intent(this, ActionBroadcastReceiver.class);
        close_intent.setAction(Constant.ACTION_CLOSE);
        return PendingIntent.getBroadcast(getApplicationContext(), Constant.REQUEST_CODE_NEXT,
                close_intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent sendNextCommand() {
        Intent next_intent = new Intent(this, ActionBroadcastReceiver.class);
        next_intent.setAction(Constant.ACTION_NEXT);
        return PendingIntent.getBroadcast(getApplicationContext(), Constant.REQUEST_CODE_NEXT, next_intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent sendPlayStatus() {
        Intent pause_intent = new Intent(this, ActionBroadcastReceiver.class);
        pause_intent.setAction(Constant.ACTION_PAUSE);
        return PendingIntent.getBroadcast(getApplicationContext(), Constant.REQUEST_CODE_PAUSE, pause_intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent sendPrevCommand() {
        Intent prev_intent = new Intent(this, ActionBroadcastReceiver.class);
        prev_intent.setAction(Constant.ACTION_PREV);
        return PendingIntent.getBroadcast(getApplicationContext(), Constant.REQUEST_CODE_PREV, prev_intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void sendChangedCommand() {
        sendNotificationMedia(songList.get(position));
        Intent intent = new Intent(Constant.ACTION_CHANGED_LAYOUT);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public Song getCurrentSong() {
        return songList.get(position);
    }

    public void initMediaPlayer(String URL) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(URL));
            mediaPlayer.start();
        } else if (!mediaPlayer.isPlaying() && playStatus != Constant.Status.OFF) {
            mediaPlayer.start();
        }
    }

    public void next() {
        if (loopStatus == Constant.Status.WHOLE)
            position = position < songList.size() - 1 ? ++position : 0;
        changedSong();
    }

    public void previous() {
        if (loopStatus == Constant.Status.WHOLE)
            position = position > 0 ? --position : songList.size() - 1;
        changedSong();
    }

    public void changedSong() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
            }
            mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(songList.get(position).getSongURL()));
            mediaPlayer.start();
            playStatus = Constant.Status.ON;
            sendChangedCommand();
        }
    }

    public void pause() {
        mediaPlayer.pause();
        playStatus = Constant.Status.OFF;
        sendNotificationMedia(songList.get(position));
        sendChangedCommand();
    }

    public void play() {
        mediaPlayer.start();
        playStatus = Constant.Status.ON;
        sendNotificationMedia(songList.get(position));
        sendChangedCommand();
    }

    public void loop() {
        if (loopStatus == Constant.Status.WHOLE) {
            loopStatus = Constant.Status.SINGLE;
            loopImageResource = R.drawable.ic_repeat_one;
        } else {
            loopStatus = Constant.Status.WHOLE;
            loopImageResource = R.drawable.ic_repeat_whole;
        }
        sendChangedCommand();
    }

    public void shuffle() {
        if (shuffleStatus == Constant.Status.OFF) {
            Song currentSong = songList.get(position);
            Collections.shuffle(songList);
            shuffleStatus = Constant.Status.ON;
            shuffleImageResource = R.drawable.ic_shuffle_on;
            position = songList.indexOf(currentSong);
        } else {
            shuffleStatus = Constant.Status.OFF;
            shuffleImageResource = R.drawable.ic_shuffle_off;
            Song currentSong = songList.get(position);
            songList = new ArrayList<>(originalSongList);
            position = songList.indexOf(currentSong);
        }
        sendChangedCommand();
    }

    public List<Song> getSongList() {
        return songList;
    }
}
