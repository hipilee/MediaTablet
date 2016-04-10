package com.jiaying.mediatablet.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
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
        Bitmap cover_bitmap = getVideoThumbnail(mList.get(position).getPlay_url());
        if (cover_bitmap != null) {
            holder.cover_image.setImageBitmap(cover_bitmap);
        }
        holder.play_btn.setTag(position);
        holder.play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyLog.e(TAG, "path：" + mList.get(position).getPlay_url());
                View  title_bar_view = srMActivity.get().findViewById(R.id.title_bar_view);
                title_bar_view.setVisibility(View.GONE);

                View  title_bar_back_view = srMActivity.get().findViewById(R.id.title_bar_back_view);
                title_bar_back_view.setVisibility(View.VISIBLE);

                TextView title_bar_back_txt = (TextView) srMActivity.get().findViewById(R.id.title_text);
                title_bar_back_txt.setText(R.string.play_video);
                View  right_view = srMActivity.get().findViewById(R.id.left_view);
                right_view.setVisibility(View.VISIBLE);

                RadioGroup mGroup = (RadioGroup) srMActivity.get().findViewById(R.id.group);
                mGroup.setVisibility(View.GONE);
//                View  collection_container = srMActivity.get().findViewById(R.id.collection_container);
//                collection_container.setVisibility(View.GONE);
                View  fragment_container = srMActivity.get().findViewById(R.id.fragment_container);
                fragment_container.setVisibility(View.VISIBLE);


                srMActivity.get().getFragmentManager().beginTransaction().replace(R.id.fragment_container, PlayVideoFragment.newInstance(mList.get(position).getPlay_url(), "")).commit();
            }
        });

        return convertView;
    }

    private class MyHolder {
        ImageView cover_image;
        Button play_btn;
    }
    private  Bitmap getVideoThumbnail(String videoPath) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath,
                MediaStore.Images.Thumbnails.MICRO_KIND);
//        System.out.println("w" + bitmap.getWidth());
//        System.out.println("h" + bitmap.getHeight());
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, 80, 80,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }
}
