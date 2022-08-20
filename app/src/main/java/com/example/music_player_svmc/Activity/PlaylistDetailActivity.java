package com.example.music_player_svmc.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music_player_svmc.Adapter.SongListAdapter;
import com.example.music_player_svmc.Application.MusicPlayerApp;
import com.example.music_player_svmc.Database.DBHelper;
import com.example.music_player_svmc.Model.Playlist;
import com.example.music_player_svmc.Model.Song;
import com.example.music_player_svmc.R;
import com.example.music_player_svmc.Utility.Constant;

import java.util.ArrayList;
import java.util.List;

public class PlaylistDetailActivity extends AppCompatActivity {
    RecyclerView rvSongList;
    ImageView ivPlaylistCover;
    ImageButton ibBackBtn;
    int playlistCover;
    int index;
    TextView tvPlaylistName;
    List<Song> songList = new ArrayList<>();
    List<Playlist> playlists = new ArrayList<>();
    SongListAdapter adapter;
    DBHelper mDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDBHelper = new DBHelper(this);
        playlists = mDBHelper.getAllPlaylist();

        Intent intent = getIntent();
        index = intent.getExtras().getInt(Constant.POSITION_KEY);

        String name = playlists.get(index).getPlaylistName();

        songList = mDBHelper.getAllSong(name);

        playlistCover = intent.getExtras().getInt(Constant.COVER_KEY);
        setContentView(R.layout.activity_playlist_detail);
        rvSongList = findViewById(R.id.playlistDetailRecycleView);
        ivPlaylistCover = findViewById(R.id.playlistCover);

        if (name.equals("Favorites")) {
            ivPlaylistCover.setImageResource(R.drawable.favorites);
        } else {
            ivPlaylistCover.setImageResource(R.drawable.img_playlist_default);
        }

        ibBackBtn = findViewById(R.id.backBtnPlaylistDetail);
        ibBackBtn.setOnClickListener(view -> {
            onBackPressed();
        });
        tvPlaylistName = findViewById(R.id.playlistNamePlaylistDetail);
        tvPlaylistName.setText(name);

        adapter = new SongListAdapter(PlaylistDetailActivity.this, new SongListAdapter.ItemClickListener() {
            @Override
            public void onItemClick(Song song) {
                Intent intent = new Intent(PlaylistDetailActivity.this, PlayControlActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(Constant.PLAYLIST_KEY, (ArrayList<? extends Parcelable>) songList);
                bundle.putInt(Constant.POSITION_KEY, songList.indexOf(song));
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
            }
        }, index);

        for (Song song : songList) {
            if (Boolean.TRUE.equals(((MusicPlayerApp) getApplication()).status.get(song.getSongName()))) {
                song.loadEmbeddedPicture();
            }
        }

        adapter.setData(songList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvSongList.setLayoutManager(linearLayoutManager);
        rvSongList.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        String name = mDBHelper.getAllPlaylist().get(index).getPlaylistName();
        Log.e("PlaylistDetail", name);
        songList = mDBHelper.getAllSong(name);

        for (Song song : songList) {
            if (Boolean.TRUE.equals(((MusicPlayerApp) getApplication()).status.get(song.getSongName()))) {
                song.loadEmbeddedPicture();
            }
        }

        adapter.setData(songList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


}