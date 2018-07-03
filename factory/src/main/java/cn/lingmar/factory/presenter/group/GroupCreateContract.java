package cn.lingmar.factory.presenter.group;

import cn.lingmar.factory.model.Author;
import cn.lingmar.factory.presenter.BaseContract;

public interface GroupCreateContract {
    interface Presenter extends BaseContract.Presenter {
        // 创建
        void create(String name, String desc, String picture);

        // 更改一个Model的选中状态
        void changeSelect(ViewModel model, boolean isSelected);
    }

    interface View extends BaseContract.RecyclerView<Presenter, ViewModel> {
        // 创建成功
        void onCreateSucceed();
    }

    class ViewModel {
        // 用户信息
        Author author;
        // 是否选中
        boolean isSelect;
    }
}
