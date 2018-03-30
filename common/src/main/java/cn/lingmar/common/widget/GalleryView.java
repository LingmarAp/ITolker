package cn.lingmar.common.widget;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cn.lingmar.common.R;
import cn.lingmar.common.widget.recycler.RecyclerAdapter;

public class GalleryView extends RecyclerView {

    private static final int LOADER_ID = 0x0100;
    private static final int MAX_IMAGE_COUNT = 3; // 最大选中图片数量
    private static final int MIN_IMAGE_FILE_SIZE = 10 * 1024; // 最小图片大小

    private LoaderManager.LoaderCallbacks mLoaderCallback = new LoaderCallback();
    private Adapter mAdapter = new Adapter();
    private List<Image> mSelectedImages = new LinkedList<>();
    private SelectedChangeListener mListener;

    public GalleryView(Context context) {
        super(context);
        init();
    }

    public GalleryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GalleryView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setLayoutManager(new GridLayoutManager(getContext(), 4));
        setAdapter(mAdapter);
        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<Image>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, Image image) {
                if (onItemSelectClick(image)) {
                    holder.updateData(image);
                }
            }
        });
    }

    /**
     * 初始化方法
     *
     * @return
     */
    public int setup(LoaderManager loaderManager, SelectedChangeListener listener) {
        mListener = listener;
        loaderManager.initLoader(LOADER_ID, null, mLoaderCallback);

        return LOADER_ID;
    }

    /**
     * Cell点击的逻辑
     *
     * @param image
     * @return True 需要更新数据
     */
    private boolean onItemSelectClick(Image image) {
        boolean notifyRefresh;
        // 如果存在则移除
        if (mSelectedImages.contains(image)) {
            mSelectedImages.remove(image);
            image.isSelect = false;
            // 需要进行更新
            notifyRefresh = true;
        } else {
            if (mSelectedImages.size() < MAX_IMAGE_COUNT) {
                mSelectedImages.add(image);
                image.isSelect = true;
                notifyRefresh = true;
            } else {
                // 得到提示文字
                String str = getResources().getString(R.string.label_gallery_select_max_size);
                str = String.format(str, MAX_IMAGE_COUNT);
                Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
                notifyRefresh = false;
            }
        }

        // 如果数据有改动
        // 则通知监听者数据选中改变了
        if (notifyRefresh) {
            notifySelectChanged();
            return true;
        }

        return false;
    }

    /**
     * 通知选中状态进行改变
     */
    private void notifySelectChanged() {
        SelectedChangeListener listener = mListener;
        if (listener != null) {
            listener.onSelectedCountChanged(mSelectedImages.size());
        }
    }

    /**
     * 通知Adapter数据更改
     *
     * @param images 新的数据
     */
    private void updateSource(List<Image> images) {
        mAdapter.replace(images);
    }

    /**
     * 得到选中的图片的全部地址
     *
     * @return 选中的数组
     */
    public String[] getSelectedPath() {
        String[] paths = new String[mSelectedImages.size()];
        int index = 0;
        for (Image img : mSelectedImages) {
            paths[index++] = img.path;
        }

        return paths;
    }

    /**
     * 清空选中的图片
     */
    public void clean() {
        for (Image img : mSelectedImages) {
            img.isSelect = false;
        }
        mSelectedImages.clear();
        // 通知更新
        mAdapter.notifyDataSetChanged();
    }

    private class LoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {

        private final String[] IMAGE_PROJECTION = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,   // 图片路径
                MediaStore.Images.Media.DATE_ADDED // 图片创建时间
        };

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            // 创建一个Loader
            // 如果是我们定义的Loader则可以开始初始化
            if (id == LOADER_ID) {
                return new CursorLoader(getContext(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        IMAGE_PROJECTION,
                        null,
                        null,
                        IMAGE_PROJECTION[2] + " DESC");
            }

            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            // Loader加载完成时
            List<Image> images = new ArrayList<>();

            if (data != null) {
                int count = data.getCount();
                if (count > 0) {
                    // 游标移动到开始位置
                    data.moveToFirst();

                    int indexId = data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]);
                    int indexPath = data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]);
                    int indexDate = data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]);
                    do {
                        int id = data.getInt(indexId);
                        String path = data.getString(indexPath);
                        long dateTime = data.getLong(indexDate);

                        File file = new File(path);
                        if (!file.exists() || file.length() < MIN_IMAGE_FILE_SIZE) {
                            continue;
                        }

                        // 添加一条新的数据
                        Image image = new Image();
                        image.id = id;
                        image.path = path;
                        image.date = dateTime;
                        images.add(image);
                    } while (data.moveToNext());
                }
            }

            updateSource(images);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            updateSource(null);
        }
    }

    private static class Image {
        int id;
        String path;
        long date;
        boolean isSelect;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Image image = (Image) o;

            return path != null ? path.equals(image.path) : image.path == null;

        }

        @Override
        public int hashCode() {
            return path != null ? path.hashCode() : 0;
        }

        @Override
        public String toString() {
            return "Image{" +
                    "id=" + id +
                    ", path='" + path + '\'' +
                    ", date=" + date +
                    ", isSelect=" + isSelect +
                    '}';
        }
    }

    private class Adapter extends RecyclerAdapter<Image> {

        @Override
        protected int getItemViewType(int position, Image image) {
            return R.layout.cell_galley;
        }

        @Override
        protected ViewHolder<Image> onCreateViewHolder(View root, int viewType) {
            return new GalleryView.ViewHolder(root);
        }
    }

    private class ViewHolder extends RecyclerAdapter.ViewHolder<Image> {
        private ImageView mPic;
        private View mShade;
        private CheckBox mSelected;

        public ViewHolder(View itemView) {
            super(itemView);

            mPic = (ImageView) itemView.findViewById(R.id.img_image);
            mShade = itemView.findViewById(R.id.view_shade);
            mSelected = (CheckBox) itemView.findViewById(R.id.cb_select);
        }

        @Override
        protected void onBind(Image image) {
            Glide.with(getContext())
                    .load(image.path)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)  // 不使用缓存，直接从原图加载
                    .centerCrop()
                    .placeholder(R.color.grey_200)
                    .into(mPic);

            mShade.setVisibility(image.isSelect ? VISIBLE : INVISIBLE);
            mSelected.setChecked(image.isSelect);
            mSelected.setVisibility(VISIBLE);
        }
    }

    /**
     * 对外的监听器
     */
    public interface SelectedChangeListener {
        void onSelectedCountChanged(int count);
    }
}
