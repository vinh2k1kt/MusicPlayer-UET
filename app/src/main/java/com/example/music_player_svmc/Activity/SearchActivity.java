package com.example.music_player_svmc.Activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.music_player_svmc.Adapter.SearchViewPagerAdapter;
import com.example.music_player_svmc.Application.MusicPlayerApp;
import com.example.music_player_svmc.Fragment.AlbumFragment;
import com.example.music_player_svmc.Fragment.SingerFragment;
import com.example.music_player_svmc.Fragment.SearchSongFragment;
import com.example.music_player_svmc.Model.Album;
import com.example.music_player_svmc.Model.Singer;
import com.example.music_player_svmc.Model.Song;
import com.example.music_player_svmc.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class SearchActivity extends AppCompatActivity {

    private TabLayout sTabLayout;
    private ViewPager sViewPager;
    private EditText sEditText;
    private int currentTab;

    private List<Song> songList = new ArrayList<>();
    private List<Album> albumList = new ArrayList<>();
    private List<Singer> singerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        sTabLayout = findViewById(R.id.sTabLayout);
        sViewPager = findViewById(R.id.sViewPager);
        sEditText = findViewById(R.id.sTextView);
        sEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                searching();
            }
        });


        SearchViewPagerAdapter searchViewPagerAdapter = new SearchViewPagerAdapter(getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        sViewPager.setAdapter(searchViewPagerAdapter);
        sTabLayout.setupWithViewPager(sViewPager);
        sTabLayout.getTabAt(0).setText("Song");
        sTabLayout.getTabAt(1).setText("Album");
        sTabLayout.getTabAt(2).setText("Singer");
        sTabLayout.getTabAt(0).setIcon(R.drawable.ic_music);
        sTabLayout.getTabAt(1).setIcon(R.drawable.ic_album);
        sTabLayout.getTabAt(2).setIcon(R.drawable.ic_artist);
        sTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getPosition();
                searching();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        sViewPager.setOffscreenPageLimit(searchViewPagerAdapter.getCount());
    }

    private void searching() {
        songList.clear();
        albumList.clear();
        singerList.clear();

        Thread searchingSongThread = new Thread(() -> {
            for (Song song : ((MusicPlayerApp) getApplication()).songList) {
                if (song.getSongName().toLowerCase().contains(String.valueOf(sEditText.getText()))) {
                    songList.add(song);
                }
            }
        });

        Thread searchingAlbumThread = new Thread(() -> {
            for (Map.Entry<String, ArrayList<Song>> entry : ((MusicPlayerApp) getApplication()).album.entrySet()) {
                if (entry.getKey().toLowerCase().contains(sEditText.getText())) {
                    albumList.add(new Album(entry.getKey(), entry.getValue()));
                }
            }
        });

        Thread searchingSingerThread = new Thread(() -> {
            for (Map.Entry<String, ArrayList<Song>> entry : ((MusicPlayerApp) getApplication()).singer.entrySet()) {
                if (entry.getKey().toLowerCase().contains(sEditText.getText())) {
                    singerList.add(new Singer(entry.getKey(), entry.getValue()));
                }
            }
        });

        searchingSingerThread.start();
        searchingAlbumThread.start();
        searchingSongThread.start();

        switch (currentTab) {
            case 0:
                updateSongFragment();
                break;
            case 1:
                updateAlbumFragment();
                break;
            case 2:
                updateSingerFragment();
                break;
        }
    }

    public void updateSongFragment() {
        SearchSongFragment searchSongFragment = (SearchSongFragment) ((SearchViewPagerAdapter) Objects.requireNonNull(
                sViewPager.getAdapter())).getItem(0);
        searchSongFragment.updateSongListAdapter(songList);
    }

    public void updateAlbumFragment() {
        AlbumFragment searchAlbumFragment = (AlbumFragment) ((SearchViewPagerAdapter)
                sViewPager.getAdapter()).getItem(1);
        searchAlbumFragment.updateAlbumList(albumList);
    }

    public void updateSingerFragment() {
        SingerFragment singerFragment = (SingerFragment) ((SearchViewPagerAdapter)
                sViewPager.getAdapter()).getItem(2);
        singerFragment.updateSingerList(singerList);
    }
}
