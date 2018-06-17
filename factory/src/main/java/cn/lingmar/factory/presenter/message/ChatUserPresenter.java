package cn.lingmar.factory.presenter.message;

import cn.lingmar.factory.data.helper.UserHelper;
import cn.lingmar.factory.data.message.MessageRepository;
import cn.lingmar.factory.model.db.Message;
import cn.lingmar.factory.model.db.User;

public class ChatUserPresenter extends ChatPresenter<ChatContract.UserView>
        implements ChatContract.Presenter {

    public ChatUserPresenter(ChatContract.UserView view, String receiverId) {
        super(new MessageRepository(receiverId), view, receiverId, Message.RECEIVER_TYPE_NONE);
    }

    @Override
    public void start() {
        super.start();

        User receiver = UserHelper.findFromLocal(mReceiverId);
        getView().onInit(receiver);
    }
}
