package com.example.music_player_svmc.Application;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import com.example.music_player_svmc.Database.DBHelper;
import com.example.music_player_svmc.Model.Song;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MusicPlayerApp extends Application {

    public Map<String, ArrayList<Song>> album = new TreeMap<>();
    public Map<String, ArrayList<Song>> singer = new TreeMap<>();
    public Map<String, Boolean> status = new HashMap<>();
    public static final String CHANNEL_ID = "CHANNEL_MUSIC_APP";
    public List<Song> songList = new ArrayList<>();
    public static DBHelper dbHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();

        dbHelper = new DBHelper(this);
        createFavoritePlaylist();
    }

    private void createFavoritePlaylist() {
        if (dbHelper.getAllPlaylist().size() == 0) {
            dbHelper.addPlaylist("Favorites");
        }
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Channel music",
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setSound(null, null);
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }
    }
}
