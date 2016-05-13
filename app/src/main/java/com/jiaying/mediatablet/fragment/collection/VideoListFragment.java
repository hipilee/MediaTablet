package com.jiaying.mediatablet.fragment.collection;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.jiaying.mediatablet.R;
import com.jiaying.mediatablet.activity.MainActivity;
import com.jiaying.mediatablet.adapter.VideoAdapter;
import com.jiaying.mediatablet.entity.VideoEntity;
import com.jiaying.mediatablet.utils.VideoReadUtils;
import com.jiaying.mediatablet.utils.VideoUtils;


import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

/*
视频列表
 */
public class VideoListFragment extends Fragment {
    private GridView collection_video_gridview;

    private List<VideoEntity> collection_video_list;
    private VideoAdapter collection_video_adapter;


    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, null);

        collection_video_gridview = (GridView) view.findViewById(R.id.gridview);
        collection_video_list = new ArrayList<>();
        MainActivity mainActivity = (MainActivity)getActivity();
        SoftReference<MainActivity> softReference = new SoftReference(mainActivity);
        collection_video_adapter = new VideoAdapter(mainActivity, collection_video_list,softReference);
        collection_video_gridview.setAdapter(collection_video_adapter);


        collection_video_adapter.notifyDataSetChanged();
        new Thread(new LocalVideoRunable()).start();
        return view;
    }
    private class LocalVideoRunable implements Runnable {

        @Override
        public void run() {
            List<VideoEntity> videoList  = VideoReadUtils.getLocalVideoList(VideoReadUtils.VIDEO_PATH);
            if(videoList != null){
                collection_video_list.addAll(videoList);
            }
        }
    }
}