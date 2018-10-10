package cn.lingmar.itolker.frags.panel;


import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import net.qiujuer.genius.ui.Ui;

import java.util.List;

import cn.lingmar.common.app.Fragment;
import cn.lingmar.common.tools.UITool;
import cn.lingmar.common.widget.recycler.RecyclerAdapter;
import cn.lingmar.face.Face;
import cn.lingmar.itolker.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PanelFragment extends Fragment {
    private PanelCallback mCallback;

    public PanelFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_panel;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        initFace(root);
        initRecord(root);
        initGallery(root);
    }

    // callback初始化方法
    public void setup(PanelCallback callback) {
        this.mCallback = callback;
    }

    private void initFace(View root) {
        View facePanel = root.findViewById(R.id.lay_panel_face);

        View backspace = facePanel.findViewById(R.id.im_backspace);
        backspace.setOnClickListener(v -> {
            // 删除逻辑
            PanelCallback callback = mCallback;
            if (callback == null)
                return;

            // 模拟键盘点击删除键
            KeyEvent keyEvent = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL,
                    0, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
            callback.getInputEditText().dispatchKeyEvent(keyEvent);
        });

        TabLayout tabLayout = (TabLayout) facePanel.findViewById(R.id.tab);
        ViewPager viewPager = (ViewPager) facePanel.findViewById(R.id.pager);

        tabLayout.setupWithViewPager(viewPager);

        // 每个表情占48dp
        final int minFaceSize = (int) Ui.dipToPx(getResources(), 48);
        final int totalScreen = UITool.getScreenWidth(getActivity());
        final int spanCount = totalScreen / minFaceSize;

        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return Face.all(getContext()).size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                RecyclerView recyclerView = (RecyclerView) LayoutInflater.from(getContext())
                        .inflate(R.layout.lay_face_content, container, false);
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), spanCount));

                List<Face.Bean> faces = Face.all(getContext()).get(position).faces;
                FaceAdapter adapter = new FaceAdapter(faces, new RecyclerAdapter.AdapterListenerImpl<Face.Bean>() {
                    @Override
                    public void onItemClick(RecyclerAdapter.ViewHolder holder, Face.Bean bean) {
                        if (mCallback == null)
                            return;
                        // 点击后表情填写到输入框
                        EditText editText = mCallback.getInputEditText();
                        Face.inputFace(getContext(), editText.getText(), bean,
                                (int) (editText.getTextSize() + Ui.dipToPx(getResources(), 2)));
                    }
                });
                recyclerView.setAdapter(adapter);

                container.addView(recyclerView);

                return recyclerView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return Face.all(getContext()).get(position).name;
            }
        });

    }

    private void initRecord(View root) {

    }

    private void initGallery(View root) {

    }

    public void showFace() {
    }

    public void showRecord() {
    }

    public void showGallery() {
    }

    // 回调界面的Callback
    public interface PanelCallback {
        EditText getInputEditText();
    }
}
