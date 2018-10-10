package cn.lingmar.itolker.frags.panel;

import android.view.View;

import java.util.List;

import cn.lingmar.common.widget.recycler.RecyclerAdapter;
import cn.lingmar.face.Face;
import cn.lingmar.itolker.R;

public class FaceAdapter extends RecyclerAdapter<Face.Bean> {

    public FaceAdapter(List<Face.Bean> beans, AdapterListener<Face.Bean> listener) {
        super(beans, listener);
    }

    @Override
    protected int getItemViewType(int position, Face.Bean bean) {
        return R.layout.cell_face;
    }

    @Override
    protected ViewHolder<Face.Bean> onCreateViewHolder(View root, int viewType) {
        return new FaceHolder(root);
    }
}
