package cn.lingmar.itolker.frags.message;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.qiujuer.genius.ui.compat.UiCompat;
import net.qiujuer.genius.ui.widget.Loading;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTouch;
import cn.lingmar.common.app.PresenterFragment;
import cn.lingmar.common.widget.PortraitView;
import cn.lingmar.common.widget.adapter.TextWatcherAdapter;
import cn.lingmar.common.widget.recycler.RecyclerAdapter;
import cn.lingmar.factory.model.db.Message;
import cn.lingmar.factory.model.db.User;
import cn.lingmar.factory.persistence.Account;
import cn.lingmar.factory.presenter.message.ChatContract;
import cn.lingmar.itolker.R;
import cn.lingmar.itolker.activities.MessageActivity;

public abstract class ChatFragment<InitModel>
        extends PresenterFragment<ChatContract.Presenter>
        implements AppBarLayout.OnOffsetChangedListener,
        ChatContract.View<InitModel> {

    protected String mReceiverId;
    protected Adapter mAdapter;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    @BindView(R.id.appbar)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.collapsingToolbarLayout)
    CollapsingToolbarLayout mCollapsingLayout;

    @BindView(R.id.edit_content)
    EditText mContent;

    @BindView(R.id.btn_submit)
    View mSubmit;

    @BindView(R.id.im_header)
    ImageView mHeader;

    @Override
    protected void initArgs(Bundle bundle) {
        super.initArgs(bundle);
        mReceiverId = bundle.getString(MessageActivity.KEY_RECEIVER_ID);
    }

    @Override
    protected final int getContentLayoutId() {
        return R.layout.fragment_chat_common;
    }

    @LayoutRes
    protected abstract int getHeaderLayoutId();

    @Override
    protected void initWidget(View root) {
        // 在布局加载之前替换顶部占位布局
        ViewStub stub = (ViewStub) root.findViewById(R.id.view_stub_header);
        stub.setLayoutResource(getHeaderLayoutId());
        stub.inflate();

        super.initWidget(root);

        initAppbar();
        initToolbar();
        initEditContent();
        // 基本设置
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new Adapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        super.initData();
        // 开始初始化操作
        mPresenter.start();
    }

    protected void initToolbar() {
        Toolbar toolbar = mToolbar;
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(v -> {
            getActivity().finish();
        });
    }

    // Appbar监听，得到关闭与打开时候的进度
    private void initAppbar() {
        mAppBarLayout.addOnOffsetChangedListener(this);
    }

    private void initEditContent() {
        mContent.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                String content = s.toString().trim();
                boolean needSendMsg = !TextUtils.isEmpty(content);
                // 如果非空，展示发送图标
                mSubmit.setActivated(needSendMsg);
            }
        });
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

    }

    @OnClick(R.id.btn_face)
    void onFaceClick() {
    }

    @OnClick(R.id.btn_record)
    void onRecordClick() {
    }

    @OnTouch(R.id.edit_content)
    boolean onEditClick() {
        mAppBarLayout.setExpanded(false);
        return false;
    }

    @OnClick(R.id.btn_submit)
    void onSubmitClick() {
        if (mSubmit.isActivated()) {
            // 发送
            String content = mContent.getText().toString();
            mContent.setText("");
            mPresenter.pushText(content);
        } else {
            onMoreClick();
        }

    }

    private void onMoreClick() {

    }

    @Override
    public RecyclerAdapter<Message> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        // 跳转到最底部
        mRecyclerView.scrollToPosition(mAdapter.getItemCount()-1);
    }

    private class Adapter extends RecyclerAdapter<Message> {

        @Override
        protected int getItemViewType(int position, Message message) {
            // 返回对应的布局xml
            boolean isRight = Objects.equals(message.getSender().getId(), Account.getUserId());

            switch (message.getType()) {
                // 文字内容
                case Message.TYPE_STR:
                    return isRight ? R.layout.cell_chat_text_right : R.layout.cell_chat_text_left;

                // 语音内容
                case Message.TYPE_AUDIO:
                    return isRight ? R.layout.cell_chat_audio_right : R.layout.cell_chat_audio_left;

                // 图片内容
                case Message.TYPE_PIC:
                    return isRight ? R.layout.cell_chat_pic_right : R.layout.cell_chat_pic_left;

                // 其他内容: 文件内容
                default:
                    return isRight ? R.layout.cell_chat_text_right : R.layout.cell_chat_text_left;
            }
        }

        @Override
        protected ViewHolder<Message> onCreateViewHolder(View root, int viewType) {
            // 返回对应布局Cell的Holder
            switch (viewType) {
                case R.layout.cell_chat_text_left:
                case R.layout.cell_chat_text_right:
                    return new TextHolder(root);

                case R.layout.cell_chat_audio_left:
                case R.layout.cell_chat_audio_right:
                    return new AudioHolder(root);

                case R.layout.cell_chat_pic_left:
                case R.layout.cell_chat_pic_right:
                    return new PicHolder(root);

                default:
                    return new TextHolder(root);
            }
        }
    }

    class BaseHolder extends RecyclerAdapter.ViewHolder<Message> {
        @BindView(R.id.im_portrait)
        PortraitView mPortrait;

        @Nullable // 允许为空，左边没有，右边有
        @BindView(R.id.loading)
        Loading mLoading;

        public BaseHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            User sender = message.getSender();
            sender.load(); // 进行数据加载（DBFlow）

            mPortrait.setup(Glide.with(ChatFragment.this), sender);
            if (mLoading != null) {
                // 当前布局在右边
                int status = message.getStatus();
                if (status == Message.STATUS_DONE) {
                    // 正常状态，隐藏Loading
                    mLoading.stop();
                    mLoading.setVisibility(View.GONE);
                } else if (status == Message.STATUS_CREATED) {
                    // 创建状态
                    mLoading.setVisibility(View.VISIBLE);
                    mLoading.setProgress(0);
                    mLoading.setForegroundColor(UiCompat.getColor(getResources(), R.color.colorAccent));
                    mLoading.start();
                } else if (status == Message.STATUS_FAILED) {
                    // 发送失败状态
                    mLoading.setVisibility(View.VISIBLE);
                    mLoading.stop();
                    mLoading.setProgress(1);
                    mLoading.setForegroundColor(UiCompat.getColor(getResources(), R.color.alertImportant));
                }

                mPortrait.setEnabled(status == Message.STATUS_FAILED);
            }
        }

        @OnClick(R.id.im_portrait)
        void onRePushClick() {
            // 点击头像，进行重新发送
            if (mLoading != null && mPresenter.rePush(mData)) {
                // 必须是右边的布局才允许从新发送
                updateData(mData);
            }
        }
    }

    class TextHolder extends BaseHolder {
        @BindView(R.id.txt_content)
        TextView mContent;

        public TextHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);

            mContent.setText(message.getContent());
        }
    }

    class AudioHolder extends BaseHolder {

        public AudioHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);
        }
    }

    class PicHolder extends BaseHolder {

        public PicHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);
        }
    }
}
