package cn.lingmar.itolker.frags.user;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yalantis.ucrop.UCrop;

import net.qiujuer.genius.ui.widget.Button;
import net.qiujuer.genius.ui.widget.Loading;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import cn.lingmar.common.app.Application;
import cn.lingmar.common.app.PresenterFragment;
import cn.lingmar.common.widget.PortraitView;
import cn.lingmar.factory.presenter.user.UpdateInfoContract;
import cn.lingmar.factory.presenter.user.UpdateInfoPresenter;
import cn.lingmar.itolker.R;
import cn.lingmar.itolker.activities.MainActivity;
import cn.lingmar.itolker.frags.media.GalleryFragment;

import static android.app.Activity.RESULT_OK;

/**
 * 更新用户信息的界面
 */
public class UpdateInfoFragment extends PresenterFragment<UpdateInfoContract.Presenter>
        implements UpdateInfoContract.View {

    @BindView(R.id.im_sex)
    ImageView mSex;

    @BindView(R.id.edit_desc)
    EditText mDesc;

    @BindView(R.id.im_portrait)
    PortraitView mPortrait;

    @BindView(R.id.loading)
    Loading mLoading;

    @BindView(R.id.btn_submit)
    Button mSubmit;


    // 头像的本地路径
    private String mPortraitPath;
    // 性别
    private boolean isMan = true;

    private static final String TAG = UpdateInfoFragment.class.getSimpleName();

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
                Application.showToast(R.string.data_rsp_error_unknown);
                final Throwable cropError = UCrop.getError(data);
            }
        }
    }

    /**
     * 加载Uri到当前头像中
     * @param uri
     */
    private void loadPortrait(Uri uri) {
        // 得到头像地址
        mPortraitPath = uri.getPath();

        Glide.with(getContext())
                .load(uri)
                .asBitmap()
                .centerCrop()
                .into(mPortrait);
    }

    @OnClick(R.id.btn_submit)
    void OnSubmitClick() {
        String dec = mDesc.getText().toString();

        // 调动P层进行登录
        mPresenter.update(mPortraitPath, dec, isMan);
    }

    @OnClick(R.id.im_sex)
    void OnSexClick() {
        // 性别图标点击触发的事件
        isMan = !isMan; // 反转性别

        Drawable drawable = getResources().getDrawable(isMan ?
                R.drawable.ic_sex_man : R.drawable.ic_sex_woman);
        mSex.setImageDrawable(drawable);
        mSex.getBackground().setLevel(isMan ? 0 : 1);
    }

    @Override
    protected UpdateInfoContract.Presenter initPresenter() {
        return new UpdateInfoPresenter(this);
    }

    @Override
    public void showError(int str) {
        super.showError(str);
        // 当需要显示错误的时候触发，一定是结束了

        // 停止Loading
        mLoading.stop();
        // 让控件可以输入
        mSex.setEnabled(true);
        mDesc.setEnabled(true);
        mPortrait.setEnabled(true);
        // 提交按钮可以进行点击
        mSubmit.setEnabled(true);
    }

    @Override
    public void showLoading() {
        super.showLoading();
        // 正在进行时， 正在进行注册，界面不可操作

        // 开始Loading
        mLoading.start();
        // 让控件不可以输入
        mSex.setEnabled(false);
        mDesc.setEnabled(false);
        mPortrait.setEnabled(false);
        // 提交按钮不可以进行点击
        mSubmit.setEnabled(false);
    }

    @Override
    public void updateSucceed() {
        MainActivity.show(getContext());
        getActivity().finish();
    }
}
