package cn.lingmar.factory.data.user;

import java.util.List;

import cn.lingmar.factory.data.DataSource;
import cn.lingmar.factory.model.db.User;

/**
 * 联系人数据源
 */
public interface ContactDataSource {
    /**
     * 对数据进行加载的一个职责
     *
     * @param callback 加载成功后返回的Callback
     */
    void load(DataSource.SucceedCallback<List<User>> callback);

    /**
     * 销毁操作
     */
    void dispose();
}
