package com.jiaying.mediatablet.fragment.collection;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SynthesizerListener;
import com.jiaying.mediatablet.R;
import com.jiaying.mediatablet.activity.MainActivity;
import com.jiaying.mediatablet.fragment.BaseFragment;
import com.jiaying.mediatablet.net.signal.RecSignal;

/*
采集提示页面
 */
public class CollectionFragment extends BaseFragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View fragment_collection_view = inflater.inflate(R.layout.fragment_collection, null);
        TextView content_txt = (TextView) fragment_collection_view.findViewById(R.id.content_txt);
        SpannableString ss = new SpannableString(getString(R.string.fragment_collect_content));
        ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.orange)), 4, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        content_txt.setText(ss);
        return fragment_collection_view;
    }

    @Override
    public void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                startSpeech(getString(R.string.fragment_collect_content), mTtsListener);
            }
        }).start();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
//            showTip("开始播放");
        }

        @Override
        public void onSpeakPaused() {
            MainActivity mainActivity = (MainActivity) CollectionFragment.this.getActivity();
            if (mainActivity != null) {
                mainActivity.getTabletStateContext().handleMessge(mainActivity.getRecordState(), mainActivity.getObservableZXDCSignalListenerThread(), null, null, RecSignal.STARTCOLLECTIONVIDEO);
            }
        }

        @Override
        public void onSpeakResumed() {
//            showTip("继续播放");
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {

        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
//                showTip("播放完成");
                Log.e("error", "采集播放完毕null");
                MainActivity mainActivity = (MainActivity) CollectionFragment.this.getActivity();
                if (mainActivity != null) {
                    mainActivity.getTabletStateContext().handleMessge(mainActivity.getRecordState(), mainActivity.getObservableZXDCSignalListenerThread(), null, null, RecSignal.STARTCOLLECTIONVIDEO);
                }

            } else if (error != null) {
                Log.e("error", "采集播放完毕is not null");
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {

        }
    };


}
