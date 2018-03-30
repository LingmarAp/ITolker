package cn.lingmar.itolker.frags.account;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;

import com.bumptech.glide.Glide;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import cn.lingmar.common.app.Application;
import cn.lingmar.common.app.Fragment;
import cn.lingmar.common.widget.PortraitView;
import cn.lingmar.itolker.R;
import cn.lingmar.itolker.frags.media.GalleryFragment;

import static android.app.Activity.RESULT_OK;

/**
 * 更新用户信息的界面
 */
public class UpdateInfoFragment extends Fragment {

    @BindView(R.id.im_portrait)
    PortraitView mPortrait;

    public UpdateInfoFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_update_info;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
    }

    @OnClick(R.id.im_portrait)
    void OnPortraitClick() {
        // Log.e("TGA", "点击了用户头像");
        new GalleryFragment()
                .setListener(new GalleryFragment.OnSelectedListener() {
                    @Override
                    public void onSelectedImage(String path) {
                        UCrop.Options options = new UCrop.Options();
                        // 设置图片处理的格式JPEG
                        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
                        // 设置压缩后的图片精度
                        options.setCompressionQuality(96);

                        File dPath = Application.getPortraitTmpFile();

                        UCrop.of(Uri.fromFile(new File(path)), Uri.fromFile(dPath))
                                .withAspectRatio(1, 1)
                                .withMaxResultSize(520, 520)
                                .withOptions(options)
                                .start(getActivity());
                    }
                }).show(getChildFragmentManager(), GalleryFragment.class.getName());
    }

    // 获取Activity传递的回调
    // 所以要从AccountAcitivty中进行捕获，并将动作传递到这里完成操作
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            if (null != resultUri) {
                loadPortrait(resultUri);
            } else if (resultCode == UCrop.RESULT_ERROR) {
                final Throwable cropError = UCrop.getError(data);
            }
        }
    }

    private void loadPortrait(Uri uri) {
        Glide.with(getContext())
                .load(uri)
                .asBitmap()
                .centerCrop()
                .into(mPortrait);
    }

//    04-4-0代码提交
//            完成
//    主界面底部导航栏Fragment跳转封装
//            图片选择
//    图片剪切
}
