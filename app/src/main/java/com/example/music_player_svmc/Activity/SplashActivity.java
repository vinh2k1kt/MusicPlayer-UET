package com.example.music_player_svmc.Activity;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.music_player_svmc.Application.MusicPlayerApp;
import com.example.music_player_svmc.Model.Song;
import com.example.music_player_svmc.R;
import com.example.music_player_svmc.Utility.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class SplashActivity extends AppCompatActivity {
    private List<Song> songList;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private Map<String, ArrayList<Song>> temp_album = new HashMap<>();
    private Map<String, ArrayList<Song>> temp_singer = new HashMap<>();
    private Map<String, Boolean> temp_status = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        loadSongFromSharedStorage();
                        new Handler().postDelayed(() -> {
                            Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                            finish();
                            startActivity(intent);
                        }, 2000);
                    } else {
                        onRequestPermissionResult();
                    }
                });

        requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);

    }

    public void loadSongFromSharedStorage() {
        if (((MusicPlayerApp) getApplication()).songList != null &&
                !(((MusicPlayerApp) getApplication())).songList.isEmpty()) {
            return;
        }

        songList = new ArrayList<>();
        temp_album = new HashMap<>();
        temp_singer = new HashMap<>();

        Thread loadAlbumPicThread = new Thread(() -> {
            for (Song song : songList) {
                song.loadEmbeddedPicture();
                temp_status.put(song.getSongName(), song.isHasPic());
                ((MusicPlayerApp) getApplication()).status = new HashMap<>(temp_status);
            }
        });

        Thread loadAlbum = new Thread(() -> {
            for (Song song : songList) {
                if (!temp_album.containsKey(song.getSongAlbum())) {
                    temp_album.put(song.getSongAlbum(), new ArrayList<>());
                }
                temp_album.get(song.getSongAlbum()).add(song);

                if (!temp_singer.containsKey(song.getSongSinger())) {
                    temp_singer.put(song.getSongSinger(), new ArrayList<>());
                }
                temp_singer.get(song.getSongSinger()).add(song);
            }
            ((MusicPlayerApp) getApplication()).album = new TreeMap<>(temp_album);
            ((MusicPlayerApp) getApplication()).singer = new TreeMap<>(temp_singer);

        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

            String[] projection = new String[]{
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.DATE_ADDED,
            };
            String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";

            Thread loadingThread = new Thread(() -> {
                Cursor cursor = getApplicationContext()
                        .getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                projection, null, null, sortOrder);
                while (cursor.moveToNext()) {
                    int songId = cursor.getInt(0);
                    String songName = cursor.getString(1);
                    String songAlbum = cursor.getString(2);
                    String songSinger = cursor.getString(3);
                    String songURL = cursor.getString(4);
                    String addedDate = cursor.getString(5);
                    Song song = new Song(songId, songName, songAlbum, R.drawable.ic_default_music, songSinger, songURL, addedDate);
                    songList.add(song);
                }
                cursor.close();
                songList.sort(Constant.songComparator);
                ((MusicPlayerApp) getApplication()).songList = new ArrayList<>(songList);
                loadAlbumPicThread.start();
                loadAlbum.start();
            });

            loadingThread.start();
        }
    }

    private void onRequestPermissionResult() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            loadSongFromSharedStorage();
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // In an educational UI, explain to the user why your app requires this
            // permission for a specific feature to behave as expected.
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Requesting Permission");
            alertDialog.setMessage("Allow us to fetch songs form your device");

            alertDialog.setPositiveButton("Allow", (dialogInterface, i) -> {
                //request permission again
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            });

            alertDialog.setNegativeButton("Not Allow", (dialogInterface, i) -> {
                //request permission again
                Toast.makeText(this, "You denied to fetch songs", Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
            });

            alertDialog.show();
        } else {
            Toast.makeText(this, "You denied to fetch songs", Toast.LENGTH_SHORT).show();
            //We can close our application here
        }
    }
}