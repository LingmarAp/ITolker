package cn.lingmar.factory.data.user;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.util.LinkedList;
import java.util.List;

import cn.lingmar.factory.data.DataSource;
import cn.lingmar.factory.data.helper.DbHelper;
import cn.lingmar.factory.model.db.User;
import cn.lingmar.factory.model.db.User_Table;
import cn.lingmar.factory.persistence.Account;

/**
 * 联系人仓库
 */
public class ContactRepository implements ContactDataSource,
        QueryTransaction.QueryResultListCallback<User>,
        DbHelper.ChangedListener<User> {
    private final List<User> users = new LinkedList<>();

    private DataSource.SucceedCallback<List<User>> callback;

    @Override
    public void load(DataSource.SucceedCallback<List<User>> callback) {
        this.callback = callback;
        // 对数据库辅助工具类添加一个数据更新的监听
        DbHelper.addChangedListener(User.class, this);

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
    public void dispose() {
        this.callback = null;
        DbHelper.removeChangedListener(User.class, this);
    }

    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<User> tResult) {
        // 添加到自己当前的缓冲区
//        users.addAll(tResult);
        // 数据库加载数据成功
        if (tResult.size() == 0) {
            users.clear();
            notifyDataChange();
            return;
        }
        // 回到数据集更新的操作中
        onDataSave(tResult.toArray(new User[0]));
    }

    @Override
    public void onDataSave(User... list) {
        boolean isChanged = false;
        for (User user : list) {
            if (isRequired(user)) {
                insertOrUpdate(user);
                isChanged = true;
            }
        }

        if (isChanged)
            notifyDataChange();
    }

    @Override
    public void onDataDelete(User... list) {
        boolean isChanged = false;
        for (User user : list) {
            if (users.remove(user))
                isChanged = true;
        }

        if (isChanged)
            notifyDataChange();
    }

    private void insertOrUpdate(User user) {
        int index = indexOf(user);
        if (index >= 0) {
            replace(index, user);
        } else {
            insert(user);
        }
    }

    private void insert(User user) {
        users.add(user);
    }

    private void replace(int index, User user) {
        users.remove(index);
        users.add(index, user);
    }

    private int indexOf(User user) {
        int index = -1;
        for (User user1 : users) {
            index++;
            if (user.isSame(user1)) {
                return index;
            }
        }
        return -1;
    }

    private void notifyDataChange() {
        if (callback != null)
            callback.onDataLoaded(users);
    }

    /**
     * 检查是否是我需要关注的数据
     *
     * @param user User
     * @return True
     */
    private boolean isRequired(User user) {
        return user.isFollow() && !user.getId().equals(Account.getUserId());
    }
}
