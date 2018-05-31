package cn.lingmar.factory.data.message;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import cn.lingmar.factory.data.helper.DbHelper;
import cn.lingmar.factory.data.helper.GroupHelper;
import cn.lingmar.factory.data.helper.MessageHelper;
import cn.lingmar.factory.data.helper.UserHelper;
import cn.lingmar.factory.model.card.MessageCard;
import cn.lingmar.factory.model.db.Group;
import cn.lingmar.factory.model.db.Message;
import cn.lingmar.factory.model.db.User;

public class MessageDispatcher implements MessageCenter {
    public static MessageCenter instance;
    // 单线程池
    private final Executor executor = Executors.newSingleThreadExecutor();

    public static MessageCenter instance() {
        if (instance == null)
            synchronized (MessageDispatcher.class) {
                if (instance == null) {
                    instance = new MessageDispatcher();
                }
            }
        return instance;
    }

    @Override
    public void dispatch(MessageCard... cards) {
        if (cards == null || cards.length == 0)
            return;

        executor.execute(new MessageHandler(cards));
    }

    private class MessageHandler implements Runnable {
        private MessageCard[] cards;

        MessageHandler(MessageCard[] cards) {
            this.cards = cards;
        }

        @Override
        public void run() {
            List<Message> messages = new ArrayList<>();
            for (MessageCard card : cards) {
                // 过滤
                if (card == null || TextUtils.isEmpty(card.getSenderId())
                        || TextUtils.isEmpty(card.getId())
                        || (TextUtils.isEmpty(card.getReceiverId()) // 接受者和群接受不可以同时为空
                        && TextUtils.isEmpty(card.getGroupId())))
                    continue;

                // 发送消息流程：写消息->存储本地->发送网络->网络返回->刷新本地状态
                Message message = MessageHelper.findFromLocal(card.getId());
                if (message != null) {
                    // 如果本地消息显示已经完成则不做处理
                    if (message.getStatus() == Message.STATUS_DONE)
                        continue;

                    // 新状态为完成才更新服务器时间，不然不做更新
                    if (card.getStatus() == Message.STATUS_DONE)
                        message.setCreateAt(card.getCreateAt());

                    // 更新一些会变化的内容
                    message.setContent(card.getContent());
                    message.setAttach(card.getAttach());
                    message.setStatus(card.getStatus());
                } else {
                    // 没找到本地消息
                    User sender = UserHelper.search(card.getSenderId());
                    User receiver = null;
                    Group group = null;
                    if (!TextUtils.isEmpty(card.getReceiverId())) {
                        receiver = UserHelper.search(card.getReceiverId());
                    } else if (!TextUtils.isEmpty(card.getGroupId())) {
                        group = GroupHelper.findFromLocal(card.getGroupId());
                    }

                    if (receiver == null && group == null && sender != null)
                        continue;

                    message = card.build(sender, receiver, group);
                }

                messages.add(message);
            }
            if (messages.size() > 0)
                DbHelper.save(Message.class, messages.toArray(new Message[0]));
        }
    }
}
