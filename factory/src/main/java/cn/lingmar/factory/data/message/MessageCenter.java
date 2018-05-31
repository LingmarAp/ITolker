package cn.lingmar.factory.data.message;

import cn.lingmar.factory.model.card.MessageCard;

/**
 * 消息中心，进行消息卡片的实现
 */
public interface MessageCenter {
    void dispatch(MessageCard... cards);
}
