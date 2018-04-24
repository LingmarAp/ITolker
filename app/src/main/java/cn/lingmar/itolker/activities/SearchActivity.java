package cn.lingmar.itolker.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import cn.lingmar.common.app.ToolbarActivity;
import cn.lingmar.itolker.R;

public class SearchActivity extends ToolbarActivity {
    private static final String EXTRA_TYPE = "EXTRA_TYPE";
    public static final int TYPE_USER = 1; // 搜索人
    public static final int TYPE_GROUP = 2; // 搜索群

    // 具体要显示的Type
    private int type;

    /**
     * 显示搜索界面
     *
     * @param context 上下文
     * @param type    显示的类型
     */
    public static void show(Context context, int type) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra(EXTRA_TYPE, type);
        context.startActivity(intent);
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        type = bundle.getInt(EXTRA_TYPE);
        return type == TYPE_USER || type == TYPE_GROUP;
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 初始化菜单
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);

        // 找到搜索菜单
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        if (searchView != null) {
            // 拿到一个搜索管理器
            SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            
            // 添加搜索监听
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    search(s);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    // 当文字改变的时候不会及时搜索，只会在为null的情况下进行搜索
                    if(TextUtils.isEmpty(s)) {
                        search("");
                        return true;
                    }
                    return false;
                }
            });
        }


        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 搜索的发起点
     * @param s 搜索的文字
     */
    private void search(String s) {

    }
}
