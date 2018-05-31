package cn.lingmar.factory.presenter.contact;

import android.support.v7.util.DiffUtil;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import cn.lingmar.factory.data.helper.UserHelper;
import cn.lingmar.factory.model.db.User;
import cn.lingmar.factory.model.db.User_Table;
import cn.lingmar.factory.persistence.Account;
import cn.lingmar.factory.presenter.BasePresenter;
import cn.lingmar.factory.utils.DiffUiDataCallback;

/**
 * 联系人Presenter实现
 */
public class ContactPresenter extends BasePresenter<ContactContract.View>
        implements ContactContract.Presenter {

    public ContactPresenter(ContactContract.View view) {
        super(view);
    }

    @Override
    public void start() {
        super.start();

        // 加载数据，从本地数据库
        SQLite.select()
                .from(User.class)
                .where(User_Table.isFollow.eq(true))
                .and(User_Table.id.notEq(Account.getUserId()))
                .orderBy(User_Table.name, true)
                .limit(100)
                .async()
                .queryListResultCallback((transaction, tResult) -> {
                    // 刷新到界面
                    getView().getRecyclerAdapter().replace(tResult);
                    getView().onAdapterDataChanged();
                })
                .execute();

        // 请求网络数据
        UserHelper.refreshContacts();

        /*List<User> users = new ArrayList<>();
        for (UserCard u : userCards) {
            users.add(u.build());
        }

        DatabaseDefinition definition = FlowManager.getDatabase(AppDatabase.class);
        definition.beginTransactionAsync(databaseWrapper -> {
            FlowManager.getModelAdapter(User.class)
                    .saveAll(users);
        }).build().execute();

        // 对比网络请求的数据和本地数据的差异
        diff(getView().getRecyclerAdapter().getItems(), users);*/


    }

    private void diff(List<User> oldList, List<User> newList) {
        // 进行数据对比
        DiffUtil.Callback callback = new DiffUiDataCallback<>(oldList, newList);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);

        // 对比完成后进行数据的赋值
        getView().getRecyclerAdapter().replace(newList);

        // 刷新到界面
        result.dispatchUpdatesTo(getView().getRecyclerAdapter());
        getView().onAdapterDataChanged();
    }

}
