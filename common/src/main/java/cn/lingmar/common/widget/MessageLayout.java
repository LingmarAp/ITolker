package cn.lingmar.common.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import net.qiujuer.widget.airpanel.AirPanelLinearLayout;

public class MessageLayout extends AirPanelLinearLayout {
    public MessageLayout(Context context) {
        super(context);
    }

    public MessageLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MessageLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected boolean fitSystemWindows(Rect insets) {
        insets.left = 0;
        insets.right = 0;
        insets.top = 0;

        return super.fitSystemWindows(insets);
    }
}
