package cn.lingmar.factory.presenter.contact;

import cn.lingmar.common.widget.recycler.RecyclerAdapter;
import cn.lingmar.factory.model.db.User;
import cn.lingmar.factory.presenter.BaseContract;

/**
 * 联系人协议
 */
public interface ContactContract {
    // 不需要额外定义，调用父类的start即可
    interface Presenter extends BaseContract.Presenter {

    }

    interface View extends BaseContract.RecyclerView<Presenter, User> {
        // 界面端智能刷新整个数据集合，不能精确到每一条数据更新
        // void onDone(List<User> users);

        // 拿到一个适配器，然后自己自主的刷新
        RecyclerAdapter<User> getRecyclerAdapter();

        // 当适配器数据更改了的时候触发
        void onAdapterDataChanged();
    }
}
