package cn.lingmar.itolker.frags.media;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import cn.lingmar.common.tools.UITool;
import cn.lingmar.common.widget.GalleryView;
import cn.lingmar.itolker.R;

/**
 * 图片选择Fragment
 */
public class GalleryFragment extends BottomSheetDialogFragment
        implements GalleryView.SelectedChangeListener {

    private GalleryView mGallery;
    private OnSelectedListener mListener;

    public GalleryFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new TransStatusBottomSheetDialog(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        mGallery = (GalleryView) view.findViewById(R.id.galleryView);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mGallery.setup(getLoaderManager(), this);
    }

    @Override
    public void onSelectedCountChanged(int count) {
        if (count > 0) {
            dismiss();
            if (mListener != null) {
                String[] paths = mGallery.getSelectedPath();
                mListener.onSelectedImage(paths[0]);
                // 加快内存回收
                paths = null;
            }
        }
    }

    /**
     * 设置事件监听并返回自己
     *
     * @param listener
     * @return
     */
    public GalleryFragment setListener(OnSelectedListener listener) {
        mListener = listener;
        return this;
    }

    public interface OnSelectedListener {
        void onSelectedImage(String path);
    }

    /**
     * 复写的透明状态栏
     */
    public static class TransStatusBottomSheetDialog extends BottomSheetDialog {

        public TransStatusBottomSheetDialog(@NonNull Context context) {
            super(context);
        }

        public TransStatusBottomSheetDialog(@NonNull Context context, @StyleRes int theme) {
            super(context, theme);
        }

        protected TransStatusBottomSheetDialog(@NonNull Context context, boolean cancelable, OnCancelListener cancelListener) {
            super(context, cancelable, cancelListener);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            final Window window = getWindow();
            if (window == null)
                return;

            // 得到屏幕高度
            int screenHeight = UITool.getScreenHeight(getOwnerActivity());
            // 得到状态栏高度
            int statusHeight = UITool.getStatusBarHeight(getOwnerActivity());

            int dialogHeight = screenHeight - statusHeight;
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    dialogHeight <= 0 ? ViewGroup.LayoutParams.MATCH_PARENT : dialogHeight);
        }
    }
}
