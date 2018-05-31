package cn.lingmar.factory.data.group;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import cn.lingmar.factory.data.helper.DbHelper;
import cn.lingmar.factory.data.helper.GroupHelper;
import cn.lingmar.factory.data.helper.UserHelper;
import cn.lingmar.factory.model.card.GroupCard;
import cn.lingmar.factory.model.card.GroupMemberCard;
import cn.lingmar.factory.model.db.Group;
import cn.lingmar.factory.model.db.GroupMember;
import cn.lingmar.factory.model.db.User;

public class GroupDispatcher implements GroupCenter {
    public static GroupCenter instance;
    // 单线程池
    private final Executor executor = Executors.newSingleThreadExecutor();

    public static GroupCenter instance() {
        if (instance == null)
            synchronized (GroupDispatcher.class) {
                if (instance == null) {
                    instance = new GroupDispatcher();
                }
            }
        return instance;
    }

    @Override
    public void dispatch(GroupCard... cards) {
        if (cards == null || cards.length == 0)
            return;

        executor.execute(new GroupHandler(cards));
    }

    @Override
    public void dispatch(GroupMemberCard... cards) {
        if (cards == null || cards.length == 0)
            return;

        executor.execute(new GroupMemberHandler(cards));
    }

    private class GroupMemberHandler implements Runnable {
        private final GroupMemberCard[] cards;

        public GroupMemberHandler(GroupMemberCard[] cards) {
            this.cards = cards;
        }

        @Override
        public void run() {
            List<GroupMember> members = new ArrayList<>();
            for (GroupMemberCard card : cards) {
                User user = UserHelper.search(card.getUserId());
                Group group = GroupHelper.find(card.getGroupId());

                if (user != null && group != null) {
                    GroupMember member = card.build(group, user);
                    members.add(member);
                }
            }
            if (members.size() > 0)
                DbHelper.save(GroupMember.class, members.toArray(new GroupMember[0]));
        }
    }

    private class GroupHandler implements Runnable {
        private final GroupCard[] cards;

        public GroupHandler(GroupCard[] cards) {
            this.cards = cards;
        }

        @Override
        public void run() {
            List<Group> groups = new ArrayList<>();
            for (GroupCard card : cards) {
                User owner = UserHelper.search(card.getOwnerId());
                if (owner != null) {
                    Group group = card.build(owner);
                    groups.add(group);
                }
                if (groups.size() > 0)
                    DbHelper.save(Group.class, groups.toArray(new Group[0]));
            }
        }
    }
}
