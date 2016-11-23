package com.jiaying.mediatablet.fragment.authentication;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jiaying.mediatablet.R;
import com.jiaying.mediatablet.graphics.font.AbstractTypeface;
import com.jiaying.mediatablet.graphics.font.AbstractTypefaceCreator;
import com.jiaying.mediatablet.graphics.font.XKTypefaceCreator;

/*
等待献浆员信息的界面
 */
public class WaitingForDonorFragment extends Fragment {
    private static final String ARG_PARAM1 = "slogan";
    private static final String ARG_PARAM2 = "nothing";

    // TODO: Rename and change types of parameters
    private String slogan;
    private String nothing;

    private AbstractTypeface XKface;
    private AbstractTypefaceCreator typefaceCreator;

    public static WaitingForDonorFragment newInstance(String slogan, String nothing) {
        WaitingForDonorFragment fragment = new WaitingForDonorFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, slogan);
        args.putString(ARG_PARAM2, nothing);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            slogan = getArguments().getString(ARG_PARAM1);
            nothing = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        Given the typeface, we should construct a factory pattern for these type face.
        typefaceCreator = new XKTypefaceCreator();
        XKface = typefaceCreator.createTypeface(getActivity());

//        Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_waiting_plasm, container, false);
        TextView textViewSlogan = (TextView) view.findViewById(R.id.tv_slogan);

//        设置标语字体
        textViewSlogan.setTypeface(XKface.getTypeface());

//        设置标语
        textViewSlogan.setText(slogan);

        return view;
    }
}
