package cn.lingmar.itolker.frags.message;


import android.app.Fragment;

import cn.lingmar.factory.model.db.Group;
import cn.lingmar.factory.presenter.message.ChatContract;
import cn.lingmar.itolker.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatGroupFragment extends ChatFragment<Group>
        implements ChatContract.GroupView {

    public ChatGroupFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_chat_group;
    }

    @Override
    protected ChatContract.Presenter initPresenter() {
        return null;
    }

    @Override
    public void onInit(Group group) {

    }
}
