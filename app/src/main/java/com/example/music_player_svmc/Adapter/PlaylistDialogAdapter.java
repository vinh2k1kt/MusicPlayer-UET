package com.example.music_player_svmc.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.music_player_svmc.Application.MusicPlayerApp;
import com.example.music_player_svmc.Model.Playlist;
import com.example.music_player_svmc.R;

import java.util.ArrayList;
import java.util.List;

public class PlaylistDialogAdapter extends BaseAdapter {
    private Context context;
    private List<Playlist> playlists = new ArrayList<>();

    public PlaylistDialogAdapter(Context context) {
        this.context = context;
        playlists = MusicPlayerApp.dbHelper.getAllPlaylist();
    }

    @Override
    public int getCount() {
        return playlists.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
        view = layoutInflater.inflate(R.layout.item_playlist_name, null);
        TextView tvPlaylistName = view.findViewById(R.id.playlistName);
        ImageView ivPlaylistCover = view.findViewById(R.id.playlistCover);
        String name = playlists.get(i).getPlaylistName();
        if (name.equals("Favorites")) {
            ivPlaylistCover.setImageResource(R.drawable.favorites);
        } else {
            ivPlaylistCover.setImageResource(R.drawable.img_playlist_default);
        }
        tvPlaylistName.setText(playlists.get(i).getPlaylistName());

        return view;
    }
}
