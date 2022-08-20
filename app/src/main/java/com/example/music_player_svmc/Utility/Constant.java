package com.example.music_player_svmc.Utility;

import com.example.music_player_svmc.Model.Album;
import com.example.music_player_svmc.Model.Singer;
import com.example.music_player_svmc.Model.Song;

import java.util.Comparator;

public class Constant {
    public enum Status {OFF, SINGLE, WHOLE, ON, NONE}

    public static int PAUSE_REQUEST_CODE = 1;
    public static int NOTIFICATION_ID = 128;
    public static String POSITION_KEY = "position";
    public static String PLAYLIST_KEY = "playlist";
    public static String ORIGINAL_PLAYLIST_KEY = "original";
    public static String CONNECTED_STATUS_KEY = "isConnected";
    public static String COVER_KEY = "cover";
    public static String SONG_KEY = "song";
    public static String SHUFFLE_KEY = "shuffle";
    public static String NAME_KEY = "name";
    public static String NEXT_KEY = "next";
    public static String PREV_KEY = "previous";
    public static String PLAY_KEY = "play status";
    public static String RANDOM_KEY = "random";
    public static String SEND_TO_PLAY_CONTROL_ACTIVITY = "toPlayControl";
    public static final int REQUEST_CODE_NEXT = 1000;
    public static final int REQUEST_CODE_PREV = 1001;
    public static final int REQUEST_CODE_PAUSE = 1002;
    public static final int REQUEST_CODE_BACK = 1003;
    public static final String SEND_TO_BROADCAST = "To BroadcastReceiver";
    public static final String FROM_HOME_TO_PLAYCONTROL = "Home to playcontrol";
    public static final String FROM_BOUND_SERVICE = "From BoundService";
    public static final String ACTION_NEXT = "com.example.music_player_svmc.Utility.Notification-next";
    public static final String ACTION_PREV = "com.example.music_player_svmc.Utility.Notification-prev";
    public static final String ACTION_PAUSE = "com.example.music_player_svmc.Utility.Notification-pause";
    public static final String ACTION_CLOSE = "com.example.music_player_svmc.Utility.Notification-close";
    public static final String ACTION_BACK = "com.example.music_player_svmc.Utility.Notification-back";
    public static final String ACTION_CHANGED_LAYOUT = "com.example.music_player_svmc.Utility.Notification-change";

    public static Sorting sorting = new Sorting();

    public static Comparator<Song> songComparator = new Comparator<Song>() {
        @Override
        public int compare(Song song, Song t1) {
            return Constant.sorting.generator(song.getSongName()).compareTo(
                    Constant.sorting.generator(t1.getSongName()));
        }
    };

    public static Comparator<Singer> singerComparator = new Comparator<Singer>() {
        @Override
        public int compare(Singer singer, Singer t1) {
            return Constant.sorting.generator(singer.getSingerName()).compareTo(
                    Constant.sorting.generator(t1.getSingerName()));
        }
    };

    public static Comparator<Album> albumComparator = new Comparator<Album>() {
        @Override
        public int compare(Album album, Album t1) {
            return Constant.sorting.generator(album.getAlbumName()).compareTo(
                    Constant.sorting.generator(t1.getAlbumName()));
        }
    };
}
