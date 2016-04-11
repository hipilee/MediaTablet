package com.jiaying.mediatablet.fragment;

import android.app.Activity;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SynthesizerListener;
import com.jiaying.mediatablet.R;

import com.jiaying.mediatablet.entity.Donor;
import com.jiaying.mediatablet.graphics.font.AbstractTypeface;
import com.jiaying.mediatablet.graphics.font.AbstractTypefaceCreator;
import com.jiaying.mediatablet.graphics.font.XKTypefaceCreator;


/*
结束欢送页面
 */
public class OverFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private View view;
    private TextView sloganTextView, thanksTextView;
    private Donor donor = Donor.getInstance();
    private String slogan;
    private String thanks;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OverFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OverFragment newInstance(String param1, String param2) {
        OverFragment fragment = new OverFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public OverFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_over, container, false);

        donor = Donor.getInstance();
        slogan = getActivity().getString(R.string.slogantwoabove);
        thanks = donor.getUserName() + ", " + getActivity().getString(R.string.slogantwoabelow);

        // Generate the typeface
        AbstractTypefaceCreator abstractTypefaceCreator = new XKTypefaceCreator();
        AbstractTypeface abstractTypeface = abstractTypefaceCreator.createTypeface(getActivity());

        // Set these text views
        sloganTextView = (TextView) view.findViewById(R.id.end_slogan_text_view);
        sloganTextView.setText(slogan);
        sloganTextView.setTypeface(abstractTypeface.getTypeface());

        thanksTextView = (TextView) view.findViewById(R.id.end_thanks_text_view);
        thanksTextView.setText(thanks);
        thanksTextView.setTypeface(abstractTypeface.getTypeface());

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                play(thanks + slogan, mTtsListener);
            }
        }).start();

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
//            showTip("暂停播放");
        }

        @Override
        public void onSpeakResumed() {
//            showTip("继续播放");
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
            // 合成进度
//            mPercentForBuffering = percent;
//            showTip(String.format(getString(R.string.tts_toast_format),
//                    mPercentForBuffering, mPercentForPlaying));
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度
//            mPercentForPlaying = percent;
//            showTip(String.format(getString(R.string.tts_toast_format),
//                    mPercentForBuffering, mPercentForPlaying));
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
//                showTip("播放完成");
            } else if (error != null) {
//                showTip(error.getPlainDescription(true));
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnEvaluationFragmentListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnEvaluationFragmentListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
