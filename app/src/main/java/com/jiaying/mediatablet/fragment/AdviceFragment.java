package com.jiaying.mediatablet.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.jiaying.mediatablet.R;
import com.jiaying.mediatablet.net.signal.RecSignal;


/*
投诉与建议
 */
public class AdviceFragment extends Fragment {
    private Button advice_btn;
    private Button evaluate_btn;
    private AdviceFragmentInteractionListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listener = (AdviceFragmentInteractionListener) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_advice, null);
        advice_btn = (Button) view.findViewById(R.id.advice_btn);
        advice_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAdviceFragmentInteraction(RecSignal.INPUTSUGGESTION);
            }
        });
        evaluate_btn = (Button) view.findViewById(R.id.evaluate_btn);
        evaluate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAdviceFragmentInteraction(RecSignal.INPUTEVALUATION);
            }
        });
        return view;
    }
    public interface AdviceFragmentInteractionListener{
        public void onAdviceFragmentInteraction(RecSignal recSignal);
    }
}
