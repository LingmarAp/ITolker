package cn.lingmar.factory.data.helper;

import cn.lingmar.factory.R;
import cn.lingmar.factory.data.DataSource;
import cn.lingmar.factory.model.api.account.RegisterModel;
import cn.lingmar.factory.model.db.User;

public class AccountHelper {

    /**
     * 注册的接口，异步调用
     *
     * @param model    传递一个注册的Model进来
     * @param callback 成功与失败的接口回送
     */
    public static void register(RegisterModel model, final DataSource.Callback<User> callback) {
        new Thread() {
            @Override
            public void run() {
                super.run();

                try {
                    Thread.sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                callback.onDataNotAvailable(R.string.data_rsp_error_parameters);
            }
        }.start();
    }
}
