package cn.lingmar.itolker.frags.panel;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;

import butterknife.BindView;
import cn.lingmar.common.widget.recycler.RecyclerAdapter;
import cn.lingmar.face.Face;
import cn.lingmar.itolker.R;

public class FaceHolder extends RecyclerAdapter.ViewHolder<Face.Bean> {
    @BindView(R.id.im_face)
    ImageView mFace;

    public FaceHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void onBind(Face.Bean bean) {
        if (bean != null &&
                // drawable 资源 id
                ((bean.preview instanceof Integer)
                        // face zip 包资源路径
                        || bean.preview instanceof String)) {
            Glide.with(mFace.getContext())
                    .load(bean.preview)
                    .asBitmap()
                    .format(DecodeFormat.PREFER_ARGB_8888)
                    .into(mFace);
        }
    }
}
