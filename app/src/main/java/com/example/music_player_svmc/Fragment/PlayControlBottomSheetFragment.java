package com.example.music_player_svmc.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music_player_svmc.Adapter.SongListAdapter;
import com.example.music_player_svmc.Model.Song;
import com.example.music_player_svmc.R;
import com.example.music_player_svmc.Utility.Constant;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class PlayControlBottomSheetFragment extends BottomSheetDialogFragment {

    private ImageButton shuffleBtn, loopBtn;
    private List<Song> songList;
    private IOnItemSelectedListener iOnItemSelectedListener;
    private Constant.Status shuffleStatus = Constant.Status.OFF;
    private Constant.Status loopStatus = Constant.Status.OFF;
    private SongListAdapter songListAdapter;
    private RecyclerView recyclerViewData;

    public interface IOnItemSelectedListener {
        void getSong(Song song);

        void getShuffleStatus();

        void getLoopStatus();
    }

    public PlayControlBottomSheetFragment(List<Song> songList, Constant.Status shuffleStatus,
                                          Constant.Status loopStatus) {
        this.songList = new ArrayList<>(songList);
        this.shuffleStatus = shuffleStatus;
        this.loopStatus = loopStatus;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        iOnItemSelectedListener = (IOnItemSelectedListener) getActivity();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_fragment_play_control, null);
        bottomSheetDialog.setContentView(view);

        recyclerViewData = view.findViewById(R.id.songView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewData.setLayoutManager(linearLayoutManager);

        songListAdapter = new SongListAdapter(getContext(), song -> {
            iOnItemSelectedListener.getSong(song);
        });

        shuffleBtn = view.findViewById(R.id.shuffleBtn);
        shuffleBtn.setOnClickListener(view1 -> {
            shuffle();
            iOnItemSelectedListener.getShuffleStatus();
        });

        loopBtn = view.findViewById(R.id.loopBtn);

        loopBtn.setOnClickListener(view1 -> {
            loop();
            iOnItemSelectedListener.getLoopStatus();
        });

        setButtonImage();

        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
        bottomSheetBehavior.setDraggable(false);
        songListAdapter.setData(songList);

        recyclerViewData.setAdapter(songListAdapter);

        return bottomSheetDialog;
    }

    private void loop() {
        if (loopStatus == Constant.Status.WHOLE) {
            loopStatus = Constant.Status.SINGLE;
            loopBtn.setImageResource(R.drawable.ic_repeat_one);
        } else {
            loopStatus = Constant.Status.WHOLE;
            loopBtn.setImageResource(R.drawable.ic_repeat_whole);
        }
    }

    private void shuffle() {
        if (shuffleStatus == Constant.Status.OFF) {
            shuffleStatus = Constant.Status.ON;
            shuffleBtn.setImageResource(R.drawable.ic_shuffle_on);
        } else {
            shuffleStatus = Constant.Status.OFF;
            shuffleBtn.setImageResource(R.drawable.ic_shuffle_off);
        }
    }

    public void updateSongListAdapter(List<Song> songList) {
        Log.e("PlayControlBottomSheet", String.valueOf(songList));
        this.songList = songList;
        songListAdapter.setData(songList);
    }

    private void setButtonImage() {
        if (shuffleStatus == Constant.Status.OFF) {
            shuffleBtn.setImageResource(R.drawable.ic_shuffle_off);
        } else {
            shuffleBtn.setImageResource(R.drawable.ic_shuffle_on);
        }

        if (loopStatus == Constant.Status.WHOLE) {
            loopBtn.setImageResource(R.drawable.ic_repeat_whole);
        } else {
            loopBtn.setImageResource(R.drawable.ic_repeat_one);
        }
    }
}
