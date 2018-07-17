package cn.lingmar.factory.data.group;

import android.text.TextUtils;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import cn.lingmar.factory.data.BaseDbRepository;
import cn.lingmar.factory.data.helper.GroupHelper;
import cn.lingmar.factory.model.db.Group;
import cn.lingmar.factory.model.db.Group_Table;
import cn.lingmar.factory.model.db.view.MemberUserModel;

/**
 * 我的群组的数据仓库，对GroupsDataSource的实现
 */
public class GroupsRepository extends BaseDbRepository<Group>
        implements GroupsDataSource {

    @Override
    public void load(SucceedCallback<List<Group>> callback) {
        super.load(callback);

        SQLite.select()
                .from(Group.class)
                .orderBy(Group_Table.name, true)
                .limit(100)
                .async()
                .queryListResultCallback(this)
                .execute();
    }

    @Override
    protected boolean isRequired(Group group) {
        if (group.getGroupMemberCount() > 0) {
            group.holder = buildGroupHolder(group);
        } else {
            group.holder = null;
            GroupHelper.refreshGroupMember(group);
        }

        return true;
    }

    // 初始化显示的成员信息
    private Object buildGroupHolder(Group group) {
        List<MemberUserModel> userModels = group.getLatelyGroupMembers();
        if (userModels == null || userModels.size() == 0)
            return null;

        StringBuilder builder = new StringBuilder();
        for (MemberUserModel userModel : userModels) {
            builder.append(TextUtils.isEmpty(userModel.alias) ? userModel.name : userModel.alias)
                    .append(", ");
        }

        builder.delete(builder.lastIndexOf(", "), builder.length());

        return builder.toString();
    }
}
