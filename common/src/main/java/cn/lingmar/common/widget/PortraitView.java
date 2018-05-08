package cn.lingmar.common.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.bumptech.glide.RequestManager;

import cn.lingmar.factory.model.Author;
import de.hdodenhof.circleimageview.CircleImageView;

import cn.lingmar.lang.R;

/**
 * 头像控件
 * Created by Lingmar on 2017/10/28.
 */

public class PortraitView extends CircleImageView {
    public PortraitView(Context context) {
        super(context);
    }

    public PortraitView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PortraitView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setup(RequestManager manager, Author author) {
        setup(manager, R.drawable.default_portrait, author.getPortrait());
    }

    public void setup(RequestManager manager, String url) {
        setup(manager, url);
    }

    public void setup(RequestManager manager, int resourceId, String url) {
        if(url == null) {
            url = "";
        }

        manager.load(url)
                .placeholder(resourceId)
                .centerCrop()
                .dontAnimate() // CircleImageView 控件中不能使用渐变动画，会导致延迟
                .into(this);
    }

}
