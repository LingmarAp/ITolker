package cn.lingmar.factory.presenter.group;

import android.text.TextUtils;

import net.qiujuer.genius.kit.handler.Run;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.lingmar.factory.Factory;
import cn.lingmar.factory.R;
import cn.lingmar.factory.data.DataSource;
import cn.lingmar.factory.data.helper.GroupHelper;
import cn.lingmar.factory.data.helper.UserHelper;
import cn.lingmar.factory.model.api.group.GroupCreateModel;
import cn.lingmar.factory.model.card.GroupCard;
import cn.lingmar.factory.model.db.view.UserSampleModel;
import cn.lingmar.factory.net.UploadHelper;
import cn.lingmar.factory.presenter.BaseRecyclerPresenter;

/**
 * 群创建界面的Presenter
 */
public class GroupCreatePresenter extends BaseRecyclerPresenter<GroupCreateContract.ViewModel, GroupCreateContract.View>
        implements GroupCreateContract.Presenter, DataSource.Callback<GroupCard> {

    private Set<String> users = new HashSet<>();

    public GroupCreatePresenter(GroupCreateContract.View view) {
        super(view);
    }

    @Override
    public void start() {
        super.start();

        Factory.runOnAsync(loader);
    }

    @Override
    public void create(String name, String desc, String picture) {
        GroupCreateContract.View view = getView();
        view.showLoading();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(desc) ||
                TextUtils.isEmpty(picture) || users.size() == 0) {
            view.showError(R.string.label_group_create_invalid);
            return;
        }

        Factory.runOnAsync(() -> {
            String url = uploadPicture(picture);
            if (TextUtils.isEmpty(url))
                return;

            // 进行网络请求
            GroupCreateModel model = new GroupCreateModel(name, desc, url, users);
            GroupHelper.create(model, GroupCreatePresenter.this);
        });
    }

    // 同步上传方法
    private String uploadPicture(String path) {
        String url = UploadHelper.uploadPortrait(path);
        if (TextUtils.isEmpty(url)) {
            Run.onUiAsync(() -> {
                GroupCreateContract.View view = getView();
                if (view != null)
                    view.showError(R.string.data_upload_error);
            });
        }

        return url;
    }

    @Override
    public void changeSelect(GroupCreateContract.ViewModel model, boolean isSelected) {
        if (isSelected)
            users.add(model.author.getId());
        else
            users.remove(model.author.getId());
    }

    private Runnable loader = () -> {
        List<UserSampleModel> sampleModels = UserHelper.getSampleContact();
        List<GroupCreateContract.ViewModel> models = new ArrayList<>();
        for (UserSampleModel sampleModel : sampleModels) {
            GroupCreateContract.ViewModel viewModel = new GroupCreateContract.ViewModel();
            viewModel.author = sampleModel;
            models.add(viewModel);
        }

        refreshData(models);
    };

    @Override
    public void onDataLoaded(GroupCard groupCard) {
        Run.onUiAsync(() -> {
            GroupCreateContract.View view = getView();
            if (view != null)
                view.onCreateSucceed();
        });
    }

    @Override
    public void onDataNotAvailable(int strRes) {
        Run.onUiAsync(() -> {
            GroupCreateContract.View view = getView();
            if (view != null)
                view.showError(strRes);
        });
    }
}
