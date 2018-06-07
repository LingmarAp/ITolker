package cn.lingmar.factory.presenter.contact;

import android.support.v7.util.DiffUtil;

import java.util.List;

import cn.lingmar.common.widget.recycler.RecyclerAdapter;
import cn.lingmar.factory.data.DataSource;
import cn.lingmar.factory.data.helper.UserHelper;
import cn.lingmar.factory.data.user.ContactDataSource;
import cn.lingmar.factory.data.user.ContactRepository;
import cn.lingmar.factory.model.db.User;
import cn.lingmar.factory.presenter.BaseSourcePresenter;
import cn.lingmar.factory.utils.DiffUiDataCallback;

/**
 * 联系人Presenter实现
 */
public class ContactPresenter
        extends BaseSourcePresenter<User, User, ContactDataSource, ContactContract.View>
        implements ContactContract.Presenter, DataSource.SucceedCallback<List<User>> {

    public ContactPresenter(ContactContract.View view) {
        super(new ContactRepository(), view);
    }

    @Override
    public void start() {
        super.start();

        // 请求网络数据
        UserHelper.refreshContacts();
    }

    @Override
    public void onDataLoaded(List<User> users) {
        final ContactContract.View view = getView();
        if (view == null)
            return;

        RecyclerAdapter<User> adapter = view.getRecyclerAdapter();
        List<User> old = adapter.getItems();

        // 进行数据对比
        DiffUtil.Callback callback = new DiffUiDataCallback<>(old, users);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);

        // 调用基类方法进行界面刷新
        refreshData(result, users);
    }
}
