package com.example.music_player_svmc.Dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.music_player_svmc.Adapter.PlaylistDialogAdapter;
import com.example.music_player_svmc.Application.MusicPlayerApp;
import com.example.music_player_svmc.Model.Playlist;
import com.example.music_player_svmc.Model.Song;
import com.example.music_player_svmc.R;

import java.util.ArrayList;
import java.util.List;

public class AddToPlaylistDialog extends AppCompatDialogFragment {
    private Context context;
    private Song song;
    private List<Playlist> playlists;

    public AddToPlaylistDialog(Context context, Song song) {
        this.context = context;
        this.song = song;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_add_playlist, container, false);
        PlaylistDialogAdapter adapter = new PlaylistDialogAdapter(context);
        ListView lvPlaylistName = view.findViewById(R.id.lvPlaylistName);
        lvPlaylistName.setAdapter(adapter);
        LinearLayout newPlaylist = view.findViewById(R.id.newPlaylist);
        //Press newPlaylist
        newPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewPlaylist(view);
            }
        });
        lvPlaylistName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                addSongtoPlaylist(i);
            }
        });
        return view;
    }

    public void addSongtoPlaylist(int i) {
        playlists = MusicPlayerApp.dbHelper.getAllPlaylist();
        String name = playlists.get(i).getPlaylistName();

        List<Song> songList = MusicPlayerApp.dbHelper.getAllSong(name);
        if (checkSong(songList)) {
            Toast.makeText(context, "Bài hát này đã có trong " + name + " playlist!", Toast.LENGTH_SHORT).show();
        } else {
            MusicPlayerApp.dbHelper.addSongToPlaylist(song, name);
            playlists = MusicPlayerApp.dbHelper.getAllPlaylist();
            Toast.makeText(context, "Thêm bài hát vào playlist " + playlists.get(i).getPlaylistName() + " thành công!", Toast.LENGTH_SHORT).show();
        }
        dismiss();
        Log.v("song", "Add " + playlists.get(i).getPlaylistName() + " " + song.getSongName());
        playlists = MusicPlayerApp.dbHelper.getAllPlaylist();
        Log.v("song", "songList" + playlists.get(i).getSongList().size());
    }

    private boolean checkSong(List<Song> songList) {
        List<String> songUrl = new ArrayList<>();
        for (Song songItem : songList) {
            songUrl.add(songItem.getSongURL());
        }
        return songUrl.contains(song.getSongURL());
    }

    public void createNewPlaylist(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        dismiss();
        View view1 = getActivity().getLayoutInflater().inflate(R.layout.dialog_playlist_name, null);
        EditText playlistName = view1.findViewById(R.id.etPlaylistName);
        builder.setView(view1)
                .setTitle("Tạo Playlist")
                .setPositiveButton("Tạo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        List<Song> songList = new ArrayList<Song>();
                        songList.add(song);
                        Log.v("song", song.getSongName());

                        playlists = MusicPlayerApp.dbHelper.getAllPlaylist();
                        boolean isHavePlaylist = false;
                        for (Playlist playlist : playlists) {
                            if (playlistName.getText().toString().equals(playlist.getPlaylistName())) {
                                isHavePlaylist = true;
                                break;
                            }
                            Log.d("Debug", (playlistName.getText().toString().equals(playlist.getPlaylistName())) + "");
                        }
                        if (!isHavePlaylist) {
                            MusicPlayerApp.dbHelper.addPlaylist(playlistName.getText().toString());
                            MusicPlayerApp.dbHelper.addSongToPlaylist(song, playlistName.getText().toString());
                            Toast.makeText(context, "Tạo Playlist thành công!", Toast.LENGTH_SHORT).show();
                            Toast.makeText(context, "Thêm bài hát vào " + playlistName.getText() + " playlist thành công!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Đã có Playlist này !", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        builder.create().show();
    }
}
