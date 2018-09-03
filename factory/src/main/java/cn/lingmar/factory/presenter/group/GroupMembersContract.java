package cn.lingmar.factory.presenter.group;

import cn.lingmar.factory.model.db.view.MemberUserModel;
import cn.lingmar.factory.presenter.BaseContract;

/**
 * 群成员的契约
 */
public interface GroupMembersContract{
    interface Presenter extends BaseContract.Presenter {
        // 刷新方法
        void refresh();
    }

    interface View extends BaseContract.RecyclerView<Presenter, MemberUserModel> {
        // 获取群的ID
        String getGroupId();
    }
}
