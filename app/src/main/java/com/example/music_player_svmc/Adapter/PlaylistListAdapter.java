package com.example.music_player_svmc.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music_player_svmc.Activity.PlaylistDetailActivity;
import com.example.music_player_svmc.Application.MusicPlayerApp;
import com.example.music_player_svmc.Model.Playlist;
import com.example.music_player_svmc.Model.Song;
import com.example.music_player_svmc.R;
import com.example.music_player_svmc.Utility.Constant;

import java.util.List;

public class PlaylistListAdapter extends RecyclerView.Adapter<PlaylistListAdapter.PlaylistViewHolder> {
    private Context context;
    private List<Playlist> playlists;
    private ItemClickListener itemClickListener;
    private ConstraintLayout layoutPlaylistFramgment;

    public PlaylistListAdapter(Context context) {
        this.context = context;
    }

    public PlaylistListAdapter(Context context, ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
        this.context = context;
    }

    public void setData(List<Playlist> playlists) {
        this.playlists = playlists;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_playlist, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Playlist playlist = playlists.get(position);
        if (playlist == null) return;
        holder.playListName.setText(playlist.getPlaylistName());
        if (position == 0) {
            holder.playListImg.setImageResource(R.drawable.favorites);
        } else {
            holder.playListImg.setImageResource(R.drawable.img_playlist_default);
        }

        List<Song> songList = MusicPlayerApp.dbHelper.getAllSong(playlists.get(position).getPlaylistName());
        holder.tvNumSong.setText(songList.size() + " songs");
        holder.ibMenuPlaylist.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(context, view);
            popupMenu.getMenuInflater().inflate(R.menu.playlist_option, popupMenu.getMenu());
            popupMenu.show();
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.deletePlaylist:
                        if (position != 0) {
                            deletePlaylist(position);
                        } else {
                            Toast.makeText(context, "Không thể xóa Favorite Playlist!", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
                return true;
            });
        });
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, PlaylistDetailActivity.class);
            intent.putExtra(Constant.POSITION_KEY, position);
            intent.putExtra(Constant.COVER_KEY, playlists.get(position).getPlaylistImage());
            ((Activity) context).startActivityForResult(intent, 1, ActivityOptions.makeSceneTransitionAnimation((Activity) context).toBundle());
        });

    }

    @Override
    public int getItemCount() {
        return playlists == null ? 0 : playlists.size();
    }

    public class PlaylistViewHolder extends RecyclerView.ViewHolder {

        private ImageView playListImg;
        private TextView playListName;
        private ImageButton ibMenuPlaylist;
        private TextView tvNumSong;

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            playListImg = itemView.findViewById(R.id.imageviewplaylist);
            playListName = itemView.findViewById(R.id.textviewplaylist);
            ibMenuPlaylist = itemView.findViewById(R.id.menuPlaylist);
            tvNumSong = itemView.findViewById(R.id.numSong);
            layoutPlaylistFramgment = itemView.findViewById(R.id.layoutPlaylistFragment);
        }
    }

    public void deletePlaylist(int positon) {
        List<Playlist> playlists = MusicPlayerApp.dbHelper.getAllPlaylist();
        Playlist playlist = playlists.get(positon);

        String name = playlist.getPlaylistName();
        int id = playlist.getPlaylistId();

        MusicPlayerApp.dbHelper.removePlaylist(name);
        MusicPlayerApp.dbHelper.removeAllSongInPlaylist(id);

        Toast.makeText(context, "Xóa thành công!", Toast.LENGTH_SHORT).show();
        update(playlists);
    }

    public void update(@NonNull List<Playlist> playlists) {
        playlists.clear();
        setData(MusicPlayerApp.dbHelper.getAllPlaylist());
    }

    public interface ItemClickListener {
        void onItemClick(Song song);
    }
}
