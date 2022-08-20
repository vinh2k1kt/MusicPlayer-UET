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
import com.example.music_player_svmc.Adapter.AlbumAdapter;
import com.example.music_player_svmc.Application.MusicPlayerApp;
import com.example.music_player_svmc.Model.Album;
import com.example.music_player_svmc.Model.Song;
import com.example.music_player_svmc.R;
import com.example.music_player_svmc.Utility.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AlbumFragment extends Fragment {

    private List<Album> albumList;
    private AlbumAdapter albumListAdapter;
    private RecyclerView albumRecycleView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_album, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        albumRecycleView = view.findViewById(R.id.albumView);
        albumListAdapter = new AlbumAdapter(getContext(), album -> {
            Intent intent = new Intent(getContext(), AlbumDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(Constant.PLAYLIST_KEY, (ArrayList<? extends Parcelable>) album.getAlbumData());
            bundle.putString(Constant.NAME_KEY, album.getAlbumName());
            intent.putExtras(bundle);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        albumRecycleView.setLayoutManager(linearLayoutManager);

        albumRecycleView.setAdapter(albumListAdapter);
    }

    public void updateAlbumList(List<Album> albumList) {
        albumListAdapter.setData(albumList);

        this.albumList = albumList;
    }

    public void getFullAlbum() {
        if (albumList == null) {
            albumList = new ArrayList<>();
            for (Map.Entry<String, ArrayList<Song>> entry : ((MusicPlayerApp) getActivity().getApplication()).album.entrySet()) {
                albumList.add(new Album(entry.getKey(), entry.getValue()));
            }

            albumList.sort(Constant.albumComparator);
            albumListAdapter.setData(albumList);
        }
    }
}
