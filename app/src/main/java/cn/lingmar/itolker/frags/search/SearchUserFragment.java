package cn.lingmar.itolker.frags.search;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.compat.UiCompat;
import net.qiujuer.genius.ui.drawable.LoadingCircleDrawable;
import net.qiujuer.genius.ui.drawable.LoadingDrawable;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.lingmar.common.app.PresenterFragment;
import cn.lingmar.common.widget.EmptyView;
import cn.lingmar.common.widget.PortraitView;
import cn.lingmar.common.widget.recycler.RecyclerAdapter;
import cn.lingmar.factory.model.card.UserCard;
import cn.lingmar.factory.presenter.contact.FollowContract;
import cn.lingmar.factory.presenter.contact.FollowPresenter;
import cn.lingmar.factory.presenter.search.SearchContract;
import cn.lingmar.factory.presenter.search.SearchUserPresenter;
import cn.lingmar.itolker.R;
import cn.lingmar.itolker.activities.PersonalActivity;
import cn.lingmar.itolker.activities.SearchActivity;

/**
 * 搜索人接口实现
 */
public class SearchUserFragment extends PresenterFragment<SearchContract.Presenter>
        implements SearchActivity.SearchFragment, SearchContract.UserView {

    @BindView(R.id.empty)
    EmptyView mEmptyView;

    @BindView(R.id.recycler)
    RecyclerView mRecycler;

    private RecyclerAdapter<UserCard> mAdapter;

    public SearchUserFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_search_user;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        // 初始化Recycler
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycler.setAdapter(mAdapter = new RecyclerAdapter<UserCard>() {

            @Override
            protected int getItemViewType(int position, UserCard userCard) {
                return R.layout.cell_search_list;
            }

            @Override
            protected ViewHolder<UserCard> onCreateViewHolder(View root, int viewType) {
                return new SearchUserFragment.ViewHolder(root);
            }
        });

        // 初始化占位布局
        mEmptyView.bind(mRecycler);
        setPlaceHolderView(mEmptyView);
    }

    @Override
    protected void initData() {
        super.initData();
        search("");
    }

    @Override
    public void search(String content) {
        mPresenter.search(content);
    }

    @Override
    protected SearchContract.Presenter initPresenter() {
        // 初始化Presenter
        return new SearchUserPresenter(this);
    }

    @Override
    public void onSearchDone(List<UserCard> userCards) {
        // 数据获取成功后返回数据
        mAdapter.replace(userCards);
        // 没有数据则显示空布局
        mPlaceHolderView.triggerOkOrEmpty(mAdapter.getItemCount() > 0);
    }

    class ViewHolder extends RecyclerAdapter.ViewHolder<UserCard>
            implements FollowContract.View {
        @BindView(R.id.im_portrait)
        PortraitView mPortrait;

        @BindView(R.id.txt_name)
        TextView mName;

        @BindView(R.id.im_follow)
        ImageView mFollow;

        private FollowContract.Presenter mPresenter;

        public ViewHolder(View itemView) {
            super(itemView);
            // 当前View和Presenter绑定
            new FollowPresenter(this);
        }

        @Override
        protected void onBind(UserCard userCard) {
            mPortrait.setup(Glide.with(SearchUserFragment.this), userCard);

            mName.setText(userCard.getName());
            mFollow.setEnabled(!userCard.isFollow());
        }

        @OnClick(R.id.im_portrait)
        void onPortraitClick() {
            PersonalActivity.show(getContext(), mData.getId());
        }

        @OnClick(R.id.im_follow)
        void onFollowClick() {
            mPresenter.follow(mData.getId());
        }

        @Override
        public void onFollowSucceed(UserCard userCard) {
            // 更改当前界面状态
            if (mFollow.getDrawable() instanceof LoadingDrawable) {
                ((LoadingDrawable) mFollow.getDrawable()).stop();
                // 设置为默认的
                mFollow.setImageResource(R.drawable.sel_opt_done_add);
            }
            // 发起更新
            updateData(userCard);
        }

        @Override
        public void showError(int str) {
            // 更改当前界面状态
            if (mFollow.getDrawable() instanceof LoadingDrawable) {
                LoadingDrawable drawable = (LoadingDrawable) mFollow.getDrawable();
                drawable.setProgress(1);
                drawable.stop();
            }
        }

        @Override
        public void showLoading() {
            int minSize = (int) Ui.dipToPx(getResources(), 22);
            int maxSize = (int) Ui.dipToPx(getResources(), 30);
            // 初始化一个圆形的动画Drawable
            LoadingCircleDrawable drawable = new LoadingCircleDrawable(minSize, maxSize);
            drawable.setBackgroundColor(0);

            int[] color = {UiCompat.getColor(getResources(), R.color.white_alpha_208)};
            drawable.setForegroundColor(color);

            mFollow.setImageDrawable(drawable);
            // 启动动画
            drawable.start();
        }

        @Override
        public void setPresenter(FollowContract.Presenter presenter) {
            mPresenter = presenter;
        }
    }
}
