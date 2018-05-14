package cn.lingmar.common.app;

import cn.lingmar.factory.presenter.BaseContract;

public abstract class PresenterToolbarActivity<Presenter extends BaseContract.Presenter> extends ToolbarActivity
        implements BaseContract.View<Presenter> {
    protected Presenter mPresenter;

    @Override
    protected void initBefore() {
        super.initBefore();
        initPresenter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 界面关闭时进行销毁操作
        if (mPresenter != null) {
            mPresenter.destroy();
        }
    }

    /**
     * 初始化Presenter
     *
     * @return Presenter
     */
    protected abstract Presenter initPresenter();

    @Override
    public void showError(int str) {
        // 显示错误，优先使用占位布局
        if (mPlaceHolderView != null) {
            mPlaceHolderView.triggerError(str);
            return;
        }

        Application.showToast(str);
    }

    @Override
    public void showLoading() {
        // 显示一个Loading
        if (mPlaceHolderView != null) {
            mPlaceHolderView.triggerLoading();
        }
    }

    protected void hideLoading() {
        // 隐藏一个Loading
        if (mPlaceHolderView != null) {
            mPlaceHolderView.triggerOk();
        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
        mPresenter = presenter;
    }
}
