package com.example.music_player_svmc.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.music_player_svmc.Activity.PlayControlActivity;
import com.example.music_player_svmc.Utility.Constant;

public class ActionBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case Constant.ACTION_NEXT:
            case Constant.ACTION_PAUSE:
            case Constant.ACTION_PREV:
                intent.setClass(context, BoundService.class);
                context.startService(intent);
                break;
        }
        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(Constant.ACTION_CHANGED_LAYOUT);
        LocalBroadcastManager.getInstance(context).sendBroadcast(broadCastIntent);
        Log.e("ActionBroadcastReceiver", "on Receive " + intent.getAction());
    }
}
