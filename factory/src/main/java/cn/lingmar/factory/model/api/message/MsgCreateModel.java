package cn.lingmar.factory.model.api.message;

import java.util.Date;
import java.util.UUID;

import cn.lingmar.factory.model.card.MessageCard;
import cn.lingmar.factory.model.db.Message;
import cn.lingmar.factory.persistence.Account;

public class MsgCreateModel {    // 客户端生成的UUID
    private String id;
    private String content;
    private String attach;

    // 消息类型
    private int type = Message.TYPE_STR;

    // 接受者
    private String receiverId;

    // 接受类型
    private int receiverType = Message.RECEIVER_TYPE_NONE;

    private MsgCreateModel() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getAttach() {
        return attach;
    }

    public int getType() {
        return type;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public int getReceiverType() {
        return receiverType;
    }

    private MessageCard card;

    public MessageCard buildCard() {
        if (card == null) {
            card = new MessageCard();
            card.setId(id);
            card.setContent(content);
            card.setAttach(attach);
            card.setType(type);
            card.setSenderId(Account.getUserId());

            if (receiverType == Message.RECEIVER_TYPE_GROUP)
                card.setGroupId(receiverId);
            else
                card.setReceiverId(receiverId);

            // 通过当前model建立的Card就是一个初步状态的Card
            card.setStatus(Message.STATUS_CREATED);
            card.setCreateAt(new Date());
            this.card = card;
        }
        return this.card;
    }

    /**
     * 建造者模式，快速的建立一个发送Model
     */
    public static class Builder {
        private MsgCreateModel model;

        public Builder() {
            this.model = new MsgCreateModel();
        }

        // 设置接受者
        public Builder receiver(String receiverId, int receiverType) {
            model.receiverId = receiverId;
            model.receiverType = receiverType;
            return this;
        }

        // 设置内容
        public Builder content(String content, int type) {
            this.model.content = content;
            this.model.type = type;
            return this;
        }

        public Builder attach(String attach) {
            this.model.attach = attach;
            return this;
        }

        public MsgCreateModel builder() {
            return this.model;
        }
    }

    public static MsgCreateModel buildWithMessage(Message message) {
        MsgCreateModel model = new MsgCreateModel();
        model.id = message.getId();
        model.content = message.getContent();
        model.type = message.getType();
        model.attach = message.getAttach();

        if (message.getReceiver() != null) {
            model.receiverId = message.getReceiver().getId();
            model.receiverType = Message.RECEIVER_TYPE_NONE;
        } else {
            model.receiverId = message.getGroup().getId();
            model.receiverType = Message.RECEIVER_TYPE_GROUP;
        }

        return model;
    }

}
