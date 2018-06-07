package cn.lingmar.factory.presenter.contact;

import android.support.v7.util.DiffUtil;

import java.util.List;

import cn.lingmar.common.widget.recycler.RecyclerAdapter;
import cn.lingmar.factory.data.DataSource;
import cn.lingmar.factory.data.helper.UserHelper;
import cn.lingmar.factory.data.user.ContactDataSource;
import cn.lingmar.factory.data.user.ContactRepository;
import cn.lingmar.factory.model.db.User;
import cn.lingmar.factory.presenter.BaseRecyclerPresenter;
import cn.lingmar.factory.utils.DiffUiDataCallback;

/**
 * 联系人Presenter实现
 */
public class ContactPresenter extends BaseRecyclerPresenter<User, ContactContract.View>
        implements ContactContract.Presenter, DataSource.SucceedCallback<List<User>> {
    private ContactDataSource mSource;

    public ContactPresenter(ContactContract.View view) {
        super(view);
        this.mSource = new ContactRepository();
    }

    @Override
    public void start() {
        super.start();

        // 进行本地的数据加载，并添加监听
        mSource.load(this);

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

    @Override
    public void destroy() {
        super.destroy();
        // 当界面销毁时，把数据监听一起销毁
        mSource.dispose();
    }
}
