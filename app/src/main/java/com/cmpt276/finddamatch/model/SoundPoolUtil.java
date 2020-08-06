package com.cmpt276.finddamatch.model;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import com.cmpt276.finddamatch.R;

import java.util.HashMap;

public class SoundPoolUtil {

    private static SoundPoolUtil soundPoolUtil;
    private SoundPool soundPool;
    HashMap<Integer, Integer > soundmap = new HashMap<Integer,Integer >();
    //instance setting
    public static SoundPoolUtil getInstance(Context context) {
        if (soundPoolUtil == null)
            soundPoolUtil = new SoundPoolUtil(context);
        return soundPoolUtil;
    }

    private SoundPoolUtil(Context context) {
        SoundPool.Builder builder = new SoundPool.Builder();
        //Max Stream number
        builder.setMaxStreams(4);
        AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
        attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
        builder.setAudioAttributes(attrBuilder.build());
        soundPool = builder.build();
        //sound effect load
        //|0: win  |1:correct_click |2:error_click |3:intro
        soundmap.put(0,soundPool.load(context,R.raw.win,1));
        soundmap.put(1,soundPool.load(context,R.raw.correct,1));
        soundmap.put(2,soundPool.load(context,R.raw.error,1));
        soundmap.put(3,soundPool.load(context,R.raw.intro2,1));
    }

    public void play(int num) {
        // This is for load check, but seems useless
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId,
                                       int status) {
                boolean loaded = true;
            }
        });
        soundPool.play(soundmap.get(num), 1, 1, 0, 0, 1);
    }
}