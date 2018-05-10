package cn.lingmar.factory.presenter.contact;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import cn.lingmar.factory.data.DataSource;
import cn.lingmar.factory.data.helper.UserHelper;
import cn.lingmar.factory.model.card.UserCard;
import cn.lingmar.factory.model.db.User;
import cn.lingmar.factory.model.db.User_Table;
import cn.lingmar.factory.persistence.Account;
import cn.lingmar.factory.presenter.BasePresenter;

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

        // TODO 加载数据
        SQLite.select()
                .from(User.class)
                .where(User_Table.isFollow.eq(true))
                .and(User_Table.id.notEq(Account.getUserId()))
                .orderBy(User_Table.name, true)
                .limit(100)
                .async()
                .queryListResultCallback((transaction, tResult) -> {
                    getView().getRecyclerAdapter().replace(tResult);
                    getView().onAdapterDataChanged();
                })
        .execute();

        // 请求网络数据
        UserHelper.refreshContacts(new DataSource.Callback<List<UserCard>>() {
            @Override
            public void onDataNotAvailable(int strRes) {

            }

            @Override
            public void onDataLoaded(List<UserCard> userCards) {

            }
        });

    }
}
