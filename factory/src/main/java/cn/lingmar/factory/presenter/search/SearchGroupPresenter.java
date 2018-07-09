package cn.lingmar.factory.presenter.search;

import net.qiujuer.genius.kit.handler.Run;

import java.util.List;

import cn.lingmar.factory.data.DataSource;
import cn.lingmar.factory.data.helper.GroupHelper;
import cn.lingmar.factory.model.card.GroupCard;
import cn.lingmar.factory.presenter.BasePresenter;
import retrofit2.Call;

/**
 * 搜索群的逻辑实现
 */
public class SearchGroupPresenter extends BasePresenter<SearchContract.GroupView>
        implements SearchContract.Presenter, DataSource.Callback<List<GroupCard>> {

    private Call searchCall;

    public SearchGroupPresenter(SearchContract.GroupView view) {
        super(view);
    }

    @Override
    public void search(String content) {
        start();

        Call call = searchCall;
        if (call != null && call.isCanceled()) {
            // 如果有上一次的请求，并且没有取消，则调用取消请求操作
            call.cancel();
        }

        searchCall = GroupHelper.search(content, this);

    }

    @Override
    public void onDataLoaded(List<GroupCard> groupCards) {
        final SearchContract.GroupView view = getView();
        if(view != null) {
            Run.onUiAsync(() -> view.onSearchDone(groupCards));
        }
    }

    @Override
    public void onDataNotAvailable(int strRes) {
        final SearchContract.GroupView view = getView();
        if(view != null) {
            Run.onUiAsync(() -> view.showError(strRes));
        }

    }
}
