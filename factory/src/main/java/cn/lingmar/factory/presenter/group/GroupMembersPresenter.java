package cn.lingmar.factory.presenter.group;

import java.util.List;

import cn.lingmar.factory.Factory;
import cn.lingmar.factory.data.helper.GroupHelper;
import cn.lingmar.factory.model.db.view.MemberUserModel;
import cn.lingmar.factory.presenter.BaseRecyclerPresenter;

public class GroupMembersPresenter extends BaseRecyclerPresenter<MemberUserModel, GroupMembersContract.View>
        implements GroupMembersContract.Presenter {

    public GroupMembersPresenter(GroupMembersContract.View view) {
        super(view);
    }

    @Override
    public void refresh() {
        start();

        // 异步加载
        Factory.runOnAsync(loader);
    }

    private Runnable loader = () -> {
        GroupMembersContract.View view = getView();
        if(view == null)
            return ;

        String groupId = view.getGroupId();

        List<MemberUserModel> models = GroupHelper.getMemberUsers(groupId, -1);// 查询所有

        refreshData(models);
    };
}
