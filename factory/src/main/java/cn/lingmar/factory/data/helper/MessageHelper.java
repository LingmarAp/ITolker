package cn.lingmar.factory.data.helper;

import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import cn.lingmar.factory.Factory;
import cn.lingmar.factory.model.api.RspModel;
import cn.lingmar.factory.model.api.message.MsgCreateModel;
import cn.lingmar.factory.model.card.MessageCard;
import cn.lingmar.factory.model.db.Message;
import cn.lingmar.factory.model.db.Message_Table;
import cn.lingmar.factory.net.Network;
import cn.lingmar.factory.net.RemoteService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 消息辅助工具类
 */
public class MessageHelper {
    public static Message findFromLocal(String id) {
        return SQLite.select()
                .from(Message.class)
                .where(Message_Table.id.eq(id))
                .querySingle();
    }

    public static void push(final MsgCreateModel model) {
        // 已发送成功的消息不可以再次发送（正在发送的也一样）
        Message local = findFromLocal(model.getId());
        if (local != null && local.getStatus() != Message.STATUS_FAILED)
            return;

        // TODO 如果是文件类型的（语音，图片，文件） 需要先上传后才发送

        // 发送的时候需要通知界面更新状态，Card
        final MessageCard card = model.buildCard();
        Factory.getMessageCenter().dispatch(card);

        // 进行网络调度，发送信息
        Factory.runOnAsync(() -> {
            RemoteService service = Network.remote();
            service.msgPush(model).enqueue(new Callback<RspModel<MessageCard>>() {
                @Override
                public void onResponse(Call<RspModel<MessageCard>> call, Response<RspModel<MessageCard>> response) {
                    RspModel<MessageCard> rspModel = response.body();
                    if (rspModel != null && rspModel.success()) {
                        MessageCard rspCard = rspModel.getResult();
                        if (rspCard != null)
                            Factory.getMessageCenter().dispatch(rspCard);
                    } else {
                        // 检查是否是账户异常
                        Factory.decodeRspCode(rspModel, null);
                        onFailure(call, null);
                    }
                }

                @Override
                public void onFailure(Call<RspModel<MessageCard>> call, Throwable t) {
                    card.setStatus(Message.STATUS_FAILED);
                    Factory.getMessageCenter().dispatch(card);
                }
            });
        });
    }

    /**
     * 查询一个消息，这个消息是一个群中的最后一条消息
     * @param groupId ID
     * @return 群中的最后一条消息
     */
    public static Message findLastWithGroup(String groupId) {
        return SQLite.select()
                .from(Message.class)
                .where(Message_Table.group_id.eq(groupId))
                .orderBy(Message_Table.createAt, false) // 倒叙查询
                .querySingle();
    }

    /**
     * 查询一个消息，这个消息是一个人中的最后一条消息
     * @param userId UserId
     * @return 一个人中的最后一条消息
     */
    public static Message findLastWithUser(String userId) {
        return SQLite.select()
                .from(Message.class)
                .where(OperatorGroup.clause()
                        .and(Message_Table.sender_id.eq(userId))
                        .and(Message_Table.group_id.isNull()))
                .or(Message_Table.receiver_id.eq(userId))
                .orderBy(Message_Table.createAt, false)
                .querySingle();
    }
}
