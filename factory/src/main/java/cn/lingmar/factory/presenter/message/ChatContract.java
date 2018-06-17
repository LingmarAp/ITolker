package cn.lingmar.factory.presenter.message;

import cn.lingmar.factory.model.db.Group;
import cn.lingmar.factory.model.db.Message;
import cn.lingmar.factory.model.db.User;
import cn.lingmar.factory.presenter.BaseContract;

public interface ChatContract {
    interface Presenter extends BaseContract.Presenter {
        void pushText(String content);

        void pushAudio(String path);

        void pushImage(String[] paths);

        boolean rePush(Message message);
    }

    interface View<InitModel> extends BaseContract.RecyclerView<Presenter, Message> {
        // 初始化的Model
        void onInit(InitModel model);
    }

    interface UserView extends View<User> {

    }

    interface GroupView extends View<Group> {

    }
}
