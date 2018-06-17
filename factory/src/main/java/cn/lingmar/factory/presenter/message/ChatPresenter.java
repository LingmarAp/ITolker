package cn.lingmar.factory.presenter.message;

import android.support.v7.util.DiffUtil;

import java.util.List;

import cn.lingmar.factory.data.helper.MessageHelper;
import cn.lingmar.factory.data.message.MessageDataSource;
import cn.lingmar.factory.model.api.message.MsgCreateModel;
import cn.lingmar.factory.model.db.Message;
import cn.lingmar.factory.persistence.Account;
import cn.lingmar.factory.presenter.BaseSourcePresenter;
import cn.lingmar.factory.utils.DiffUiDataCallback;

/**
 * 聊天Presenter基础类
 */
@SuppressWarnings("WeakerAccess")
public class ChatPresenter<View extends ChatContract.View>
        extends BaseSourcePresenter<Message, Message, MessageDataSource, View>
        implements ChatContract.Presenter {

    protected String mReceiverId;
    protected int mReceiverType;

    public ChatPresenter(MessageDataSource source, View view,
                         String receiverId, int receiverType) {
        super(source, view);
        this.mReceiverId = receiverId;
        this.mReceiverType = receiverType;
    }

    @Override
    public void pushText(String content) {
        // 构建一个新消息
        MsgCreateModel model = new MsgCreateModel.Builder()
                .receiver(mReceiverId, mReceiverType)
                .content(content, Message.TYPE_STR)
                .builder();

        // 进行网络发送
        MessageHelper.push(model);
    }

    @Override
    public void pushAudio(String path) {
        // TODO 发送语音
    }

    @Override
    public void pushImage(String[] paths) {
        // TODO 发送图片
    }

    @Override
    public boolean rePush(Message message) {
        // 用户已登录且当前用户是发送者的情况下
        if (Account.isLogin()
                && Account.getUserId().equalsIgnoreCase(message.getSender().getId())) {
            message.setStatus(Message.STATUS_CREATED);

            MsgCreateModel model = MsgCreateModel.buildWithMessage(message);
            MessageHelper.push(model);

            return true;
        }

        return false;
    }

    @Override
    public void onDataLoaded(List<Message> messages) {
        ChatContract.View view = getView();
        if (view == null)
            return;

        @SuppressWarnings("unchecked")
        List<Message> old = view.getRecyclerAdapter().getItems();

        DiffUiDataCallback<Message> callback = new DiffUiDataCallback<>(old, messages);
        final DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);

        refreshData(result, messages);
    }
}
