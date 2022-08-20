package com.example.music_player_svmc.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.music_player_svmc.Fragment.AlbumFragment;
import com.example.music_player_svmc.Fragment.SingerFragment;
import com.example.music_player_svmc.Fragment.SearchSongFragment;

import java.util.HashMap;

public class SearchViewPagerAdapter extends FragmentPagerAdapter {

    private final HashMap<Integer, Fragment> searchFragmentHashMap = new HashMap<>();

    public SearchViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        searchFragmentHashMap.put(0, new SearchSongFragment());
        searchFragmentHashMap.put(1, new AlbumFragment());
        searchFragmentHashMap.put(2, new SingerFragment());
    }

    @Override
    public Fragment getItem(int position) {
        return searchFragmentHashMap.get(position);
    }

    @Override
    public int getCount() {
        return 3;
    }
}
