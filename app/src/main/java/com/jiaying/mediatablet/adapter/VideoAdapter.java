package com.jiaying.mediatablet.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jiaying.mediatablet.R;
import com.jiaying.mediatablet.activity.MainActivity;
import com.jiaying.mediatablet.constants.Status;
import com.jiaying.mediatablet.entity.VideoEntity;
import com.jiaying.mediatablet.fragment.PlayVideoFragment;
import com.jiaying.mediatablet.utils.MyLog;

import java.lang.ref.SoftReference;
import java.util.List;

/**
 * 作者：lenovo on 2016/3/19 19:54
 * 邮箱：353510746@qq.com
 * 功能：
 */
public class VideoAdapter extends BaseAdapter {
    private static final String TAG = "VideoAdapter";
    private Context mContext;
    private List<VideoEntity> mList;
    private SoftReference<MainActivity> srMActivity;

    public VideoAdapter(MainActivity mContext, List<VideoEntity> mList, SoftReference<MainActivity> srMActivity) {
        this.mContext = mContext;
        this.mList = mList;
        this.srMActivity = srMActivity;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        MyHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.video_item, null);
            holder = new MyHolder();
            holder.play_btn = (Button) convertView.findViewById(R.id.play_btn);
            holder.cover_image = (ImageView) convertView.findViewById(R.id.cover_image);
            convertView.setTag(holder);
        } else {
            holder = (MyHolder) convertView.getTag();
        }

        holder.cover_image.setImageBitmap(mList.get(position).getCover_bitmap());

        holder.play_btn.setTag(position);
        holder.play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyLog.e(TAG, "path：" + mList.get(position).getPlay_url());

                srMActivity.get().dealSignalStartCollcetionVideo(mList.get(position).getPlay_url());

            }
        });

        return convertView;
    }

    private class MyHolder {
        ImageView cover_image;
        Button play_btn;
    }
}
