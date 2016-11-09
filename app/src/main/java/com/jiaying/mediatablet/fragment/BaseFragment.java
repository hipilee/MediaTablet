package com.jiaying.mediatablet.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.jiaying.mediatablet.utils.MyLog;

public class BaseFragment extends Fragment {
    private static String TAG = "BaseFragment";

    // 语音合成对象
    private SpeechSynthesizer speechSynthesizer;

    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_LOCAL;

    // 默认发音人
    private String voicer = "xiaoyan";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        InitListener speechSynthInitListener = new InitListener() {
            @Override
            public void onInit(int code) {

                if (code != ErrorCode.SUCCESS) {
                    MyLog.e("yy", "初始化语音合成器失败,错误码：" + code);
                } else {
                    MyLog.e("yy", "初始化语音合成器成功");
                }
            }
        };

        // 初始化语音合成对象
        speechSynthesizer = SpeechSynthesizer.createSynthesizer(getActivity(), speechSynthInitListener);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public int playSpeech(String text, SynthesizerListener synthesizerListener) {

        //设置语音播报的相关参数
        setParameter();

        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        int code = speechSynthesizer.startSpeaking(text, synthesizerListener);

        return code;
    }

    public void stop() {
        speechSynthesizer.stopSpeaking();
        speechSynthesizer.destroy();
    }

    private void setParameter() {
        // 清空参数
        speechSynthesizer.setParameter(SpeechConstant.PARAMS, null);
        // 根据合成引擎设置相应参数
        if (mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
            speechSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            // 设置在线合成发音人
            speechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, voicer);
            //设置合成语速
            speechSynthesizer.setParameter(SpeechConstant.SPEED, "50");
            //设置合成音调
            speechSynthesizer.setParameter(SpeechConstant.PITCH, "70");
            //设置合成音量
            speechSynthesizer.setParameter(SpeechConstant.VOLUME, "50");
        } else {
            speechSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
            // 设置本地合成发音人 voicer为空，默认通过语记界面指定发音人。
            speechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, "");
            /**
             * TODO 本地合成不设置语速、音调、音量，默认使用语记设置
             * 开发者如需自定义参数，请参考在线合成参数设置
             */
        }
        //设置播放器音频流类型
        speechSynthesizer.setParameter(SpeechConstant.STREAM_TYPE, "3");
        // 设置播放合成音频打断音乐播放，默认为true
        speechSynthesizer.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        speechSynthesizer.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        speechSynthesizer.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/tts.wav");
    }
}
