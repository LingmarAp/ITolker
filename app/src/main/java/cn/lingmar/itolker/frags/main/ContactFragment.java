package cn.lingmar.itolker.frags.main;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.OnClick;
import cn.lingmar.common.app.PresenterFragment;
import cn.lingmar.common.widget.EmptyView;
import cn.lingmar.common.widget.PortraitView;
import cn.lingmar.common.widget.recycler.RecyclerAdapter;
import cn.lingmar.factory.model.db.User;
import cn.lingmar.factory.presenter.contact.ContactContract;
import cn.lingmar.factory.presenter.contact.ContactPresenter;
import cn.lingmar.itolker.R;
import cn.lingmar.itolker.activities.MessageActivity;
import cn.lingmar.itolker.activities.PersonalActivity;

public class ContactFragment extends PresenterFragment<ContactContract.Presenter>
    implements ContactContract.View{

    @BindView(R.id.empty)
    EmptyView mEmptyView;

    @BindView(R.id.recycler)
    RecyclerView mRecycler;

    private RecyclerAdapter<User> mAdapter;

    public ContactFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_contact;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        // 初始化Recycler
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycler.setAdapter(mAdapter = new RecyclerAdapter<User>() {
            @Override
            protected int getItemViewType(int position, User userCard) {
                // 返回cell的布局id
                return R.layout.cell_contact_list;
            }

            @Override
            protected ViewHolder<User> onCreateViewHolder(View root, int viewType) {
                return new ContactFragment.ViewHolder(root);
            }
        });

        // item点击事件监听
        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<User>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, User user) {
                super.onItemClick(holder, user);
                MessageActivity.show(getContext(), user);
            }
        });

        // 初始化占位布局
        mEmptyView.bind(mRecycler);
        setPlaceHolderView(mEmptyView);
    }

    @Override
    protected void onFirstInit() {
        super.onFirstInit();
        // 进行一次数据加载
        mPresenter.start();
    }

    @Override
    protected ContactContract.Presenter initPresenter() {
        return new ContactPresenter(this);
    }

    @Override
    public RecyclerAdapter<User> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        // 进行界面操作
        mPlaceHolderView.triggerOkOrEmpty(mAdapter.getItemCount() > 0);
    }

    class ViewHolder extends RecyclerAdapter.ViewHolder<User> {
        @BindView(R.id.im_portrait)
        PortraitView mPortrait;

        @BindView(R.id.txt_name)
        TextView mName;

        @BindView(R.id.txt_desc)
        TextView mDesc;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(User user) {
            mPortrait.setup(Glide.with(ContactFragment.this), user);
            mName.setText(user.getName().toString());
            mDesc.setText(user.getDescription().toString());
        }

        @OnClick(R.id.im_portrait)
        void onPortraitClick() {
            PersonalActivity.show(getContext(), mData.getId());
        }
    }
}
