package com.example.music_player_svmc.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.music_player_svmc.Fragment.AlbumFragment;
import com.example.music_player_svmc.Fragment.PlaylistFragment;
import com.example.music_player_svmc.Fragment.SingerFragment;
import com.example.music_player_svmc.Fragment.SongFragment;

import java.util.HashMap;
import java.util.Objects;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private final HashMap<Integer, Fragment> mainFragmentHashmap = new HashMap<>();

    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        mainFragmentHashmap.put(0, new SongFragment());
        mainFragmentHashmap.put(1, new PlaylistFragment());
        mainFragmentHashmap.put(2, new AlbumFragment());
        mainFragmentHashmap.put(3, new SingerFragment());
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return Objects.requireNonNull(mainFragmentHashmap.get(position));
    }

    @Override
    public int getCount() {
        return mainFragmentHashmap.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
