package com.example.music_player_svmc.Fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music_player_svmc.Activity.AlbumDetailActivity;
import com.example.music_player_svmc.Adapter.SingerAdapter;
import com.example.music_player_svmc.Application.MusicPlayerApp;
import com.example.music_player_svmc.Model.Singer;
import com.example.music_player_svmc.Model.Song;
import com.example.music_player_svmc.R;
import com.example.music_player_svmc.Utility.Constant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class SingerFragment extends Fragment {

    private List<Singer> singerList;
    private SingerAdapter singerAdapter;
    private RecyclerView singerRecycleView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_singer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        singerRecycleView = view.findViewById(R.id.singerView);
        singerAdapter = new SingerAdapter(getContext(), singer -> {
            Intent intent = new Intent(getContext(), AlbumDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(Constant.PLAYLIST_KEY, (ArrayList<? extends Parcelable>) singer.getSingerSong());
            bundle.putString(Constant.NAME_KEY, singer.getSingerName());
            intent.putExtras(bundle);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        singerRecycleView.setLayoutManager(linearLayoutManager);

        singerRecycleView.setAdapter(singerAdapter);
    }

    public void updateSingerList(List<Singer> singerList) {
        singerAdapter.setData(singerList);

        this.singerList = singerList;
    }

    public void getFullSinger() {
        if (singerList == null) {
            singerList = new ArrayList<>();
            for (Map.Entry<String, ArrayList<Song>> entry : ((MusicPlayerApp) getActivity().getApplication()).singer.entrySet()) {
                singerList.add(new Singer(entry.getKey(), entry.getValue()));
            }
            Collections.sort(singerList, Constant.singerComparator);
            singerAdapter.setData(singerList);
        }
    }
}
