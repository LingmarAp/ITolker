package cn.lingmar.itolker.frags.message;


import android.app.Fragment;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;

import java.util.List;

import butterknife.BindView;
import cn.lingmar.factory.model.db.Group;
import cn.lingmar.factory.model.db.view.MemberUserModel;
import cn.lingmar.factory.presenter.message.ChatContract;
import cn.lingmar.factory.presenter.message.ChatGroupPresenter;
import cn.lingmar.itolker.R;
import cn.lingmar.itolker.activities.GroupMemberActivity;
import cn.lingmar.itolker.activities.PersonalActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatGroupFragment extends ChatFragment<Group>
        implements ChatContract.GroupView {

    @BindView(R.id.im_header)
    ImageView mHeader;

    @BindView(R.id.lay_members)
    LinearLayout mLayMembers;

    @BindView(R.id.txt_member_more)
    TextView mMemberMore;

    public ChatGroupFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getHeaderLayoutId() {
        return R.layout.lay_chat_header_group;
    }


    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        Glide.with(this)
                .load(R.drawable.default_banner_group)
                .centerCrop()
                .into(new ViewTarget<CollapsingToolbarLayout, GlideDrawable>(mCollapsingLayout) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        this.view.setContentScrim(resource.getCurrent());
                    }
                });
    }

    // 头像布局的监听
    // 进行高度运算，用来透明头像和Icon
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        super.onOffsetChanged(appBarLayout, verticalOffset);
        View view = mLayMembers;

        if (verticalOffset == 0) {
            // 完全展开
            view.setVisibility(View.VISIBLE);
            view.setScaleX(1);
            view.setScaleY(1);
            view.setAlpha(1);
        } else {
            verticalOffset = Math.abs(verticalOffset);
            final int totalScrollRange = appBarLayout.getTotalScrollRange();
//            Log.e(TAG, "verticalOffset: " + verticalOffset);
//            Log.e(TAG, "totalScrollRange: " + totalScrollRange);
            if (verticalOffset >= totalScrollRange) {
                // 关闭状态
                view.setVisibility(View.INVISIBLE);
                view.setScaleX(0);
                view.setScaleY(0);
                view.setAlpha(0);
            } else {
                // 中间状态
                float progress = 1 - verticalOffset / (float) totalScrollRange;
                view.setVisibility(View.VISIBLE);
                view.setScaleX(progress);
                view.setScaleY(progress);
                view.setAlpha(progress);
            }
        }
    }

    @Override
    protected ChatContract.Presenter initPresenter() {
        return new ChatGroupPresenter(this, mReceiverId);
    }

    @Override
    public void onInit(Group group) {
        mCollapsingLayout.setTitle(group.getName());
        Glide.with(this)
                .load(group.getPicture())
                .centerCrop()
                .placeholder(R.drawable.default_banner_group)
                .into(mHeader);
    }

    @Override
    public void onInitGroupMembers(List<MemberUserModel> members, long moreCount) {
        if (members == null || members.size() == 0)
            return;

        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (MemberUserModel member : members) {
            // 添加成员头像
            ImageView portrait = (ImageView) inflater.inflate(R.layout.lay_chat_group_portrait, mLayMembers, false);
            mLayMembers.addView(portrait, 0);

            Glide.with(this)
                    .load(member.portrait)
                    .placeholder(R.drawable.default_portrait)
                    .centerCrop()
                    .dontAnimate()
                    .into(portrait);
            // 个人界面信息查看
            portrait.setOnClickListener(v -> PersonalActivity.show(getContext(), member.userId));
        }

        if (moreCount > 0) {
            mMemberMore.setText(String.format("+%s", moreCount));
            mMemberMore.setOnClickListener(v -> {
                // 显示成员列表
                GroupMemberActivity.show(getContext(), mReceiverId);
            });
        } else {
            mMemberMore.setVisibility(View.GONE);
        }
    }

    @Override
    public void showAdminOption(boolean isAdmin) {
        if (!isAdmin)
            return;

        mToolbar.inflateMenu(R.menu.chat_group);
        mToolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_add) {
                // 进行成员添加操作
                GroupMemberActivity.showAdmin(getContext(), mReceiverId);
                return true;
            }
            return false;
        });
    }
}
