package com.example.music_player_svmc.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.music_player_svmc.Model.Playlist;
import com.example.music_player_svmc.Model.Song;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    public static final String SONG_TABLE = "Songs";
    public static final String SONG_ID = "SongID";
    public static final String SONG_NAME = "SongName";
    public static final String SONG_ALBUM = "SongAlbum";
    public static final String SONG_IMG = "SongImage";
    public static final String SONG_SINGER = "SongSinger";
    public static final String SONG_URL = "SongURL";

    public static final String PLAYLIST_TABLE = "Playlists";
    public static final String PLAYLIST_ID = "PlaylistID";
    public static final String PLAYLIST_NAME = "PlaylistName";
    public static final String PLAYLIST_IMG = "PlaylistImage";

    public static final String DATABASE_NAME = "musicPlayer.db";
    public static final int VERSION = 3;

    // Song: ID, Name, Album, img, singer, url, playlistID
    private static final String DATABASE_CREATE_TABLE_SONG =
            String.format("CREATE TABLE IF NOT EXISTS %s" +
                    "(%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "%s TEXT NOT NULL," +
                    "%s TEXT NOT NULL," +
                    "%s INTEGER NOT NULL," +
                    "%s TEXT NOT NULL," +
                    "%s TEXT NOT NULL," +
                    "%s INTEGER);", SONG_TABLE, SONG_ID, SONG_NAME, SONG_ALBUM, SONG_IMG, SONG_SINGER, SONG_URL, PLAYLIST_ID);

    // Playlists(ID,Name)
    private static final String DATABASE_CREATE_TABLE_PLAYLIST =
            String.format("CREATE TABLE IF NOT EXISTS %s " +
                    "(%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "%s TEXT);", PLAYLIST_TABLE, PLAYLIST_ID, PLAYLIST_NAME);

    private SQLiteDatabase database = getWritableDatabase();

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    //Function for CREATE, DELETE, UPDATE,...
    public void query(String sql) {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }

    public void open() {
        database = this.getWritableDatabase();
    }

    public void close() {
        database.close();
    }

    //Get Data:Select
    public Cursor getData(String sql) {
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql, null);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE_TABLE_SONG);
        sqLiteDatabase.execSQL(DATABASE_CREATE_TABLE_PLAYLIST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    // get all songs from playlist
    public List<Song> getAllSong(String PlaylistName) {
        List<Song> songList = new ArrayList<>();
        Cursor cursor = this.getData(String.format("SELECT * FROM %s INNER JOIN %s ON %s.%s = %s.%s WHERE %s = '%s'",
                DBHelper.SONG_TABLE, DBHelper.PLAYLIST_TABLE, DBHelper.SONG_TABLE,
                DBHelper.PLAYLIST_ID, DBHelper.PLAYLIST_TABLE, DBHelper.PLAYLIST_ID,
                DBHelper.PLAYLIST_NAME, PlaylistName));
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            songList.add(getSongFromCursor(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return songList;
    }

    public boolean checkSong(Song song, String playlistName) {
        final String SELECT_PLAYLIST_ID = String.format(
                "SELECT * FROM %s WHERE %s = '%s' AND %s = '%s'",
                DBHelper.SONG_TABLE, DBHelper.SONG_NAME, song.getSongName(),
                DBHelper.PLAYLIST_ID, getPlaylistID(playlistName));
        Cursor cursor = this.getData(SELECT_PLAYLIST_ID);
        return cursor.getCount() > 0;
    }

    public int getPlaylistID(String playlistName) {
        final String SELECT_PLAYLIST_ID = String.format(
                "SELECT %s FROM %s WHERE %s = '%s'",
                DBHelper.PLAYLIST_ID, DBHelper.PLAYLIST_TABLE,
                DBHelper.PLAYLIST_NAME, playlistName);
        Cursor cursor = this.getData(SELECT_PLAYLIST_ID);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    // get all Playlist
    public List<Playlist> getAllPlaylist() {
        List<Playlist> playlistList = new ArrayList<>();
        Cursor cursor = this.getData(String.format("SELECT * FROM %s", DBHelper.PLAYLIST_TABLE));
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Playlist playlist = new Playlist();
            playlist.setPlaylistId(cursor.getInt(0));
            playlist.setPlaylistName(cursor.getString(1));
            playlistList.add(playlist);
            cursor.moveToNext();
        }
        cursor.close();
        return playlistList;
    }

    // add 1 song to playlist
    public void addSongToPlaylist(Song song, String playlistName) {
        open();
        ContentValues values = new ContentValues();
        values.put(DBHelper.SONG_NAME, song.getSongName());
        values.put(DBHelper.SONG_ALBUM, song.getSongAlbum());
        values.put(DBHelper.SONG_IMG, song.getSongImage());
        values.put(DBHelper.SONG_SINGER, song.getSongSinger());
        values.put(DBHelper.SONG_URL, song.getSongURL());
        values.put(DBHelper.PLAYLIST_ID, getPlaylistID(playlistName));
        database.insert(DBHelper.SONG_TABLE, null, values);
        close();
    }

    // remove 1 song in playlist
    public void removeSongInPlaylist(Song song, String playlistName) {
        open();
        final String REMOVE_SONG = String.format(
                "DELETE FROM %s WHERE %s = '%s' AND %s = '%s'",
                DBHelper.SONG_TABLE, DBHelper.SONG_NAME, song.getSongName(),
                DBHelper.PLAYLIST_ID, getPlaylistID(playlistName));
        query(REMOVE_SONG);
        close();
    }

    // remove 1 playlist
    public void removePlaylist(String playlistName) {
        open();
        final String REMOVE_PLAYLIST = String.format("DELETE FROM %s WHERE %s = '%s'",
                DBHelper.PLAYLIST_TABLE, DBHelper.PLAYLIST_ID, getPlaylistID(playlistName));
        query(REMOVE_PLAYLIST);
        close();
    }

    // remove all song in playlist
    public void removeAllSongInPlaylist(int playlistId) {
        open();
        final String REMOVE_All_SONG_IN_PLAYLIST = String.format(
                "DELETE FROM %s WHERE %s = '%s'",
                DBHelper.SONG_TABLE, DBHelper.PLAYLIST_ID, playlistId);
        query(REMOVE_All_SONG_IN_PLAYLIST);
        close();
    }

    /**
     * add a playlist to database
     *
     * @param playlistName
     */
    public void addPlaylist(String playlistName) {
        open();
        ContentValues values = new ContentValues();
        values.put(DBHelper.PLAYLIST_NAME, playlistName);
        database.insert(DBHelper.PLAYLIST_TABLE, null, values);
        close();
    }

    public Song getSongFromCursor(Cursor cursor) {
        Song song = new Song();
        song.setSongId(cursor.getInt(0));
        song.setSongName(cursor.getString(1));
        song.setSongAlbum(cursor.getString(2));
        song.setSongImage(cursor.getInt(3));
        song.setSongSinger(cursor.getString(4));
        song.setSongURL(cursor.getString(5));
        return song;
    }

    public void reset() {
        database.delete(this.SONG_TABLE, null, null);
        database.delete(this.PLAYLIST_TABLE, null, null);
    }

}

