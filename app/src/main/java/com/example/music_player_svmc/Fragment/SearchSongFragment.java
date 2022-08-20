package com.example.music_player_svmc.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music_player_svmc.Activity.PlayControlActivity;
import com.example.music_player_svmc.Adapter.SongListAdapter;
import com.example.music_player_svmc.Application.MusicPlayerApp;
import com.example.music_player_svmc.Model.Song;
import com.example.music_player_svmc.R;
import com.example.music_player_svmc.Utility.Constant;

import java.util.ArrayList;
import java.util.List;

public class SearchSongFragment extends Fragment {

    private List<Song> songList = new ArrayList<>();
    private SongListAdapter songListAdapter;
    private RecyclerView songRecycleView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song, container, false);
        View toolbar = view.findViewById(R.id.tool_bar_song);
        ((ViewGroup) toolbar.getParent()).removeView(toolbar);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.v("SearchSongFrag", "First");
        songRecycleView = view.findViewById(R.id.songView);
        songListAdapter = new SongListAdapter(getContext(), song -> {
            Intent intent = new Intent(SearchSongFragment.this.getActivity(), PlayControlActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(Constant.PLAYLIST_KEY, (ArrayList<? extends Parcelable>) songList);
            bundle.putInt(Constant.POSITION_KEY, songList.indexOf(song));
            intent.putExtras(bundle);
            startActivity(intent);
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        songRecycleView.setLayoutManager(linearLayoutManager);

        updateSongListAdapter(((MusicPlayerApp) getActivity().getApplication()).songList);
        songRecycleView.setAdapter(songListAdapter);
    }

    public void updateSongListAdapter(List<Song> songList) {
        Log.v("SearchSongFrag", "Second");
        songListAdapter.setData(songList);
        this.songList = songList;
    }
}
