package cn.lingmar.factory.presenter.message;

import java.util.List;

import cn.lingmar.factory.data.helper.GroupHelper;
import cn.lingmar.factory.data.message.MessageGroupRepository;
import cn.lingmar.factory.model.db.Group;
import cn.lingmar.factory.model.db.Message;
import cn.lingmar.factory.model.db.view.MemberUserModel;
import cn.lingmar.factory.persistence.Account;

public class ChatGroupPresenter extends ChatPresenter<ChatContract.GroupView>
        implements ChatContract.Presenter {

    public ChatGroupPresenter(ChatContract.GroupView view, String receiverId) {
        super(new MessageGroupRepository(receiverId), view, receiverId, Message.RECEIVER_TYPE_GROUP);
    }

    @Override
    public void start() {
        super.start();

        // 拿群的信息
        Group group = GroupHelper.findFromLocal(mReceiverId);
        if (group != null) {
            // 初始化操作
            ChatContract.GroupView view = getView();

            boolean isAdmin = Account.getUserId().equalsIgnoreCase(group.getOwner().getId());
            view.showAdminOption(isAdmin);

            view.onInit(group);

            List<MemberUserModel> models = group.getLatelyGroupMembers();
            long memberCount = group.getGroupMemberCount();
            long moreCount = memberCount - models.size(); // 没有显示的成员数量

            view.onInitGroupMembers(models, moreCount);
        }
    }
}
