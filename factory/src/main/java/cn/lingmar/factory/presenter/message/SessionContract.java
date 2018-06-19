package cn.lingmar.factory.presenter.message;

import cn.lingmar.factory.model.db.Session;
import cn.lingmar.factory.presenter.BaseContract;

public interface SessionContract {
    interface Presenter extends BaseContract.Presenter {
    }

    interface View extends BaseContract.RecyclerView<Presenter, Session> {
    }
}
