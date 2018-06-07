package cn.lingmar.factory.data.user;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.LinkedList;
import java.util.List;

import cn.lingmar.factory.data.BaseDbRepository;
import cn.lingmar.factory.data.DataSource;
import cn.lingmar.factory.model.db.User;
import cn.lingmar.factory.model.db.User_Table;
import cn.lingmar.factory.persistence.Account;

/**
 * 联系人仓库
 */
public class ContactRepository extends BaseDbRepository<User> implements ContactDataSource {
    private final List<User> users = new LinkedList<>();

    @Override
    public void load(DataSource.SucceedCallback<List<User>> callback) {
        super.load(callback);

        // 加载数据，从本地数据库
        SQLite.select()
                .from(User.class)
                .where(User_Table.isFollow.eq(true))
                .and(User_Table.id.notEq(Account.getUserId()))
                .orderBy(User_Table.name, true)
                .limit(100)
                .async()
                .queryListResultCallback(this)
                .execute();
    }

    @Override
    protected boolean isRequired(User user) {
        return user.isFollow() && !user.getId().equals(Account.getUserId());
    }
}
