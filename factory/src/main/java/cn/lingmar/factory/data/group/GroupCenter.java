package cn.lingmar.factory.data.group;

import cn.lingmar.factory.model.card.GroupCard;
import cn.lingmar.factory.model.card.GroupMemberCard;

/**
 * 群中心的接口定义
 */
public interface GroupCenter {
    void dispatch(GroupCard... cards);

    void dispatch(GroupMemberCard... cards);
}
