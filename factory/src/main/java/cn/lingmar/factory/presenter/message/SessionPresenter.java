package cn.lingmar.factory.presenter.message;

import android.support.v7.util.DiffUtil;

import java.util.List;

import cn.lingmar.factory.data.message.SessionDataSource;
import cn.lingmar.factory.data.message.SessionRepository;
import cn.lingmar.factory.model.db.Session;
import cn.lingmar.factory.presenter.BaseSourcePresenter;
import cn.lingmar.factory.utils.DiffUiDataCallback;

/**
 * 最近聊天列表的Presenter
 */
public class SessionPresenter extends BaseSourcePresenter<Session, Session,
        SessionDataSource, SessionContract.View>
        implements SessionContract.Presenter {

    public SessionPresenter(SessionContract.View view) {
        super(new SessionRepository(), view);
    }

    @Override
    public void onDataLoaded(List<Session> sessions) {
        SessionContract.View view = getView();
        if (view == null)
            return ;

        // 差异对比
        List<Session> old = view.getRecyclerAdapter().getItems();
        DiffUiDataCallback<Session> callback = new DiffUiDataCallback<>(old, sessions);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);

        // 刷新界面
        refreshData(result, sessions);
    }
}
