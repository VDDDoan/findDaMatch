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
    //单例模式
    public static SoundPoolUtil getInstance(Context context) {
        if (soundPoolUtil == null)
            soundPoolUtil = new SoundPoolUtil(context);
        return soundPoolUtil;
    }

    private SoundPoolUtil(Context context) {
        SoundPool.Builder builder = new SoundPool.Builder();
        //传入最多播放音频数量,
        builder.setMaxStreams(4);
        //AudioAttributes是一个封装音频各种属性的方法
        AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
        //设置音频流的合适的属性
        attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
        //加载一个AudioAttributes
        builder.setAudioAttributes(attrBuilder.build());
        soundPool = builder.build();
        //加载音频文件
        soundmap.put(0,soundPool.load(context,R.raw.win,1));
        soundmap.put(1,soundPool.load(context,R.raw.correct,1));
        soundmap.put(2,soundPool.load(context,R.raw.error,1));
        soundmap.put(3,soundPool.load(context,R.raw.intro2,1));
    }

    public void play(int num) {
        /**
         * 播放音频
         * params说明：
         * //左耳道音量【0~1】
         * //右耳道音量【0~1】
         * //播放优先级【0表示最低优先级】
         * //循环模式【0表示循环一次，-1表示一直循环，其他表示数字+1表示当前数字对应的循环次数】
         * //播放速度【1是正常，范围从0~2】
         */



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