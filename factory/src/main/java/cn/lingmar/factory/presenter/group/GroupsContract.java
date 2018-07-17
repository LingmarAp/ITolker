package cn.lingmar.factory.presenter.group;

import cn.lingmar.factory.model.db.Group;
import cn.lingmar.factory.presenter.BaseContract;

/**
 * 我的群列表契约
 */
public interface GroupsContract {
    // 不需要额外定义，调用父类的start即可
    interface Presenter extends BaseContract.Presenter {

    }

    interface View extends BaseContract.RecyclerView<Presenter, Group> {
    }
}