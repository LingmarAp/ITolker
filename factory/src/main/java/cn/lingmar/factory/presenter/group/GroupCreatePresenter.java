package cn.lingmar.factory.presenter.group;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.lingmar.factory.Factory;
import cn.lingmar.factory.data.helper.UserHelper;
import cn.lingmar.factory.model.db.view.UserSampleModel;
import cn.lingmar.factory.presenter.BaseRecyclerPresenter;

/**
 * 群创建界面的Presenter
 */
public class GroupCreatePresenter extends BaseRecyclerPresenter<GroupCreateContract.ViewModel, GroupCreateContract.View>
        implements GroupCreateContract.Presenter {

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

    }

    @Override
    public void changeSelect(GroupCreateContract.ViewModel model, boolean isSelected) {
        if(isSelected)
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
}
