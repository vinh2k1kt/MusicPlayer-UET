package com.example.music_player_svmc.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music_player_svmc.Activity.PlayControlActivity;
import com.example.music_player_svmc.Adapter.SongListAdapter;
import com.example.music_player_svmc.Application.MusicPlayerApp;
import com.example.music_player_svmc.Model.Song;
import com.example.music_player_svmc.R;
import com.example.music_player_svmc.Utility.Constant;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;


public class SongFragment extends Fragment {

    private View view;
    private RecyclerView songView;
    private List<Song> songList;
    private SongListAdapter songListAdapter;
    private MusicPlayerApp app;

    public SongFragment() {
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        app = (MusicPlayerApp) getActivity().getApplication();
        view = inflater.inflate(R.layout.fragment_song, container, false);
        songView = view.findViewById(R.id.songView);
        songListAdapter = new SongListAdapter(getContext(), song -> openPlayer(song));
        songList = app.songList;
        songListAdapter.setData(songList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        songView.setLayoutManager(linearLayoutManager);

        songView.setAdapter(songListAdapter);

        view.findViewById(R.id.sortBtn).setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(this.getContext(), view);
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                List<Song> temp = new ArrayList<>(songList);
                switch (menuItem.getItemId()) {
                    case R.id.ascending:
                        temp.sort(Comparator.comparing(song -> Constant.sorting.generator(song.getSongName())));
                        songListAdapter.setData(temp);
                        songList = temp;
                        return true;
                    case R.id.descending:
                        temp.sort(Comparator.comparing(song -> Constant.sorting.generator(song.getSongName())));
                        Collections.reverse(temp);
                        songList = temp;
                        songListAdapter.setData(temp);
                        return true;
                    case R.id.date_added:
                        temp.sort(Comparator.comparing(Song::getAddedDate));
                        Collections.reverse(temp);
                        songList = temp;
                        songListAdapter.setData(temp);
                        return true;
                    default:
                        return false;
                }
            });
            popupMenu.inflate(R.menu.sort);
            popupMenu.show();
        });

        ((MaterialButton) view.findViewById(R.id.playall)).setOnClickListener(v -> {
            openPlayer(songList.get(0));
        });

        ((MaterialButton) view.findViewById(R.id.randomBtn)).setOnClickListener(v -> {
            openPlayerWithShuffle(songList.get(new Random().nextInt((songList.size()))));
        });
        return view;
    }

    private void openPlayer(Song song) {
        Intent intent = new Intent(SongFragment.this.getActivity(), PlayControlActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(Constant.PLAYLIST_KEY, (ArrayList<? extends Parcelable>) songList);
        bundle.putInt(Constant.POSITION_KEY, songList.indexOf(song));
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void openPlayerWithShuffle(Song song) {
        Intent intent = new Intent(SongFragment.this.getActivity(), PlayControlActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(Constant.PLAYLIST_KEY, (ArrayList<? extends Parcelable>) songList);
        bundle.putInt(Constant.POSITION_KEY, songList.indexOf(song));
        intent.putExtra(Constant.SHUFFLE_KEY, true);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}