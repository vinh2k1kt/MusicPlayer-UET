package com.example.music_player_svmc.Activity;

import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.example.music_player_svmc.Adapter.ViewPagerAdapter;
import com.example.music_player_svmc.Database.DBHelper;
import com.example.music_player_svmc.Fragment.AlbumFragment;
import com.example.music_player_svmc.Fragment.PlaylistFragment;
import com.example.music_player_svmc.Fragment.SingerFragment;
import com.example.music_player_svmc.R;
import com.example.music_player_svmc.Service.BoundService;
import com.example.music_player_svmc.Utility.Constant;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private int tabPosition = 0;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ImageButton ibSearchBtn;
    private LinearLayout miniControl;
    private boolean isConnected = false;
    private BoundService boundService;

    @RequiresApi(api = Build.VERSION_CODES.M)
    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BoundService.LocalBinder binder = (BoundService.LocalBinder) service;
            boundService = binder.getService();
            isConnected = true;
            boundService.isConnectToHome = true;

            ((TextView) miniControl.findViewById(R.id.tv_mini_songName)).setText(boundService.getCurrentSong().getSongName());
            ((ImageButton) miniControl.findViewById(R.id.mini_pauseBtn)).setImageResource(boundService.playStatus == Constant.Status.OFF ?
                    R.drawable.ic_play_arrow : R.drawable.ic_pause);
            miniControl.findViewById(R.id.mini_pauseBtn).setOnClickListener(v -> {
                if (boundService.playStatus == Constant.Status.OFF) boundService.play();
                else boundService.pause();
            });
            miniControl.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, PlayControlActivity.class);
                intent.putExtra(Constant.ACTION_BACK, true);
                intent.putParcelableArrayListExtra(Constant.PLAYLIST_KEY, (ArrayList<? extends Parcelable>) boundService.originalSongList);
                intent.putExtra(Constant.POSITION_KEY, boundService.originalSongList.indexOf(boundService.getCurrentSong()));
                intent.putExtra(Constant.FROM_HOME_TO_PLAYCONTROL, true);
                startActivity(intent);
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isConnected = false;
            boundService.isConnectToHome = false;
        }
    };

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constant.ACTION_CHANGED_LAYOUT.equals(intent.getAction()) && isConnected) {
                ((TextView) miniControl.findViewById(R.id.tv_mini_songName)).setText(boundService.getCurrentSong().getSongName());
                ((ImageButton) miniControl.findViewById(R.id.mini_pauseBtn)).setImageResource(boundService.playStatus == Constant.Status.OFF ?
                        R.drawable.ic_play_arrow : R.drawable.ic_pause);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> taskList = activityManager.getRunningTasks(10);

        Log.e(TAG, String.valueOf(taskList.get(0).numActivities));

        if (getIntent().getBooleanExtra(Constant.FROM_BOUND_SERVICE, false) && taskList.get(0).numActivities > 1) {
            finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTabLayout = findViewById(R.id.tabLayout);
        mViewPager = findViewById(R.id.viewPager);
        ibSearchBtn = findViewById(R.id.search_btn);
        miniControl = findViewById(R.id.mini_layout);

        ibSearchBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        });

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setText("Songs");
        mTabLayout.getTabAt(1).setText("Playlists");
        mTabLayout.getTabAt(2).setText("Albums");
        mTabLayout.getTabAt(3).setText("Artists");
        mTabLayout.getTabAt(0).setIcon(R.drawable.ic_music);
        mTabLayout.getTabAt(1).setIcon(R.drawable.ic_playlist);
        mTabLayout.getTabAt(2).setIcon(R.drawable.ic_album);
        mTabLayout.getTabAt(3).setIcon(R.drawable.ic_artist);
        mViewPager.setOffscreenPageLimit(adapter.getCount());

        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabPosition = tab.getPosition();
                updateTab();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ACTION_CHANGED_LAYOUT);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isMyServiceRunning(BoundService.class)) {
            miniControl.setVisibility(View.INVISIBLE);
        } else {
            Intent intent = new Intent(this, BoundService.class);
            bindService(intent, serviceConnection, BIND_AUTO_CREATE);
            miniControl.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPause() {
        if (isConnected) {
            boundService.isConnectToHome = false;
            unbindService(serviceConnection);
            isConnected = false;
        }

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    private void updateTab() {
        ViewPagerAdapter tempAdapter = (ViewPagerAdapter) mViewPager.getAdapter();
        Fragment tempFragment = tempAdapter.getItem(tabPosition);
        switch (tabPosition) {
            case 1:
                ((PlaylistFragment) tempFragment).updatePlaylist();
                break;
            case 2:
                ((AlbumFragment) tempFragment).getFullAlbum();
                break;
            case 3:
                ((SingerFragment) tempFragment).getFullSinger();
                break;
            default:

        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}