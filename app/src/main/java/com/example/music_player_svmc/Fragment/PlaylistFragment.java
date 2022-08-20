package com.example.music_player_svmc.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music_player_svmc.Adapter.PlaylistListAdapter;
import com.example.music_player_svmc.Application.MusicPlayerApp;
import com.example.music_player_svmc.Model.Playlist;
import com.example.music_player_svmc.R;

import java.util.ArrayList;
import java.util.List;

public class PlaylistFragment extends Fragment {

    private RecyclerView playlistReCycleView;
    private PlaylistListAdapter playlistListAdapter;
    private List<Playlist> playlists = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_playlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        playlistReCycleView = view.findViewById(R.id.playlistRecycleView);
        playlistListAdapter = new PlaylistListAdapter(this.getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getContext(), 2);
        playlistReCycleView.setLayoutManager(gridLayoutManager);
        playlistReCycleView.setAdapter(playlistListAdapter);
    }

    public void updatePlaylist() {
        if (playlistListAdapter != null) {
            playlists = MusicPlayerApp.dbHelper.getAllPlaylist();
            playlistListAdapter.setData(playlists);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updatePlaylist();
    }

}

