package cn.lingmar.factory.presenter.group;

import android.support.v7.util.DiffUtil;

import java.util.List;

import cn.lingmar.factory.data.group.GroupsDataSource;
import cn.lingmar.factory.data.group.GroupsRepository;
import cn.lingmar.factory.data.helper.GroupHelper;
import cn.lingmar.factory.model.db.Group;
import cn.lingmar.factory.presenter.BaseSourcePresenter;
import cn.lingmar.factory.utils.DiffUiDataCallback;

public class GroupsPresenter extends BaseSourcePresenter<Group, Group,
        GroupsDataSource, GroupsContract.View> implements GroupsContract.Presenter {

    public GroupsPresenter(GroupsContract.View view) {
        super(new GroupsRepository(), view);
    }

    @Override
    public void start() {
        super.start();

        // 请求网络数据
        GroupHelper.refreshGroups();
    }

    @Override
    public void onDataLoaded(List<Group> groups) {
        GroupsContract.View view = getView();
        if (view == null)
            return;

        List<Group> old = view.getRecyclerAdapter().getItems();
        DiffUiDataCallback<Group> callback = new DiffUiDataCallback<>(old, groups);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);

        // 界面刷新
        refreshData(result, groups);
    }
}
