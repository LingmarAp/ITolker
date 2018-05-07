package cn.lingmar.itolker.frags.search;


import cn.lingmar.common.app.Fragment;
import cn.lingmar.itolker.R;
import cn.lingmar.itolker.activities.SearchActivity;

/**
 * 搜索群接口实现
 */
public class SearchGroupFragment extends Fragment
        implements SearchActivity.SearchFragment {


    public SearchGroupFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_search_group;
    }

    @Override
    public void search(String content) {

    }
}
