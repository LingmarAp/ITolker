package cn.lingmar.itolker.frags.main;


import cn.lingmar.common.app.Fragment;
import cn.lingmar.common.widget.GalleryView;
import cn.lingmar.itolker.R;

public class AcitveFragment extends Fragment {

    GalleryView mGalley;

    public AcitveFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_acitve;
    }

    @Override
    protected void initData() {
        super.initData();

        mGalley = (GalleryView) getActivity().findViewById(R.id.galleryView);

        mGalley.setup(getLoaderManager(), new GalleryView.SelectedChangeListener() {
            @Override
            public void onSelectedCountChanged(int count) {

            }
        });
    }

}
