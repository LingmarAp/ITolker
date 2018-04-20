package cn.lingmar.factory.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import cn.lingmar.factory.Factory;
import cn.lingmar.factory.model.api.account.AccountRspModel;
import cn.lingmar.factory.model.db.User;
import cn.lingmar.factory.model.db.User_Table;

public class Account {
    private static final String KEY_PUSH_ID = "KEY_PUSH_ID";
    private static final String KEY_IS_BIND = "KEY_IS_BIND";
    private static final String KEY_TOKEN = "KEY_TOKEN";
    private static final String KEY_USER_ID = "KEY_USER_ID";
    private static final String KEY_ACCOUNT = "KEY_ACCOUNT";

    // 设备的推送Id
    private static String pushId;
    // 设备Id是否已经绑定到了服务器
    private static boolean isBind;
    // 登录状态的Token，用来接口请求
    private static String token;
    // 登录的用户ID
    private static String userId;
    // 登录的账户
    private static String account;

    /**
     * 存储数据到XML文件，持久化
     */
    private static void save(Context context) {
        // 共享参数
        SharedPreferences sp = context.getSharedPreferences(Account.class.getName(),
                Context.MODE_PRIVATE);
        sp.edit()
                .putString(KEY_PUSH_ID, pushId)
                .putString(KEY_TOKEN, token)
                .putString(KEY_USER_ID, userId)
                .putString(KEY_ACCOUNT, account)
                .apply();
    }

    /**
     * 进行数据加载
     *
     * @param context 上下文
     */
    public static void load(Context context) {
        // 共享参数
        SharedPreferences sp = context.getSharedPreferences(Account.class.getName(),
                Context.MODE_PRIVATE);
        pushId = sp.getString(KEY_PUSH_ID, "");
        token = sp.getString(KEY_TOKEN, "");
        userId = sp.getString(KEY_USER_ID, "");
        account = sp.getString(KEY_ACCOUNT, "");
    }

    public static String getPushId() {
        return pushId;
    }

    /**
     * 设置并存储设备Id
     *
     * @param pushId 设备的推送Id
     */
    public static void setPushId(String pushId) {
        Account.pushId = pushId;
        Account.save(Factory.app());
    }

    /**
     * 返回当前账号是否登录
     *
     * @return
     */
    public static boolean isLogin() {
        return !TextUtils.isEmpty(userId)
                && !TextUtils.isEmpty(token);
    }

    /**
     * 是否已经完善了用户信息
     * @return  true-完善了
     */
    public static boolean isComplete() {
        return isLogin();
    }

    /**
     * 是否已经绑定到了服务器
     *
     * @return True已绑定
     */
    public static boolean isBind() {
        return isBind;
    }

    /**
     * 设置绑定状态
     * @param isBind
     */
    public static void setBind(boolean isBind) {
        Account.isBind = isBind;
        Account.save(Factory.app());
    }

    /**
     * 保存我自己的信息到持久化XML中
     * @param model
     */
    public static void login(AccountRspModel model) {
        // 存储当前登录的token，用户Id，方便查询用户信息
        Account.token = model.getToken();
        Account.account = model.getAccount();
        Account.userId = model.getUser().getId();
        save(Factory.app());
    }

    /**
     * 获取当前登录的用户信息
     * @return user
     */
    public static User getUser() {
        return TextUtils.isEmpty(userId) ? new User() : SQLite.select()
                .from(User.class)
                .where(User_Table.id.eq(userId))
                .querySingle();
    }

    public static String getToken() {
        return token;
    }
}
