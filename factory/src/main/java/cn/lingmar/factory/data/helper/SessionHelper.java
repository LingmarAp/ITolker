package cn.lingmar.factory.data.helper;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import cn.lingmar.factory.model.db.Session;
import cn.lingmar.factory.model.db.Session_Table;

/**
 * 会话的辅助工具类
 */
public class SessionHelper {
    public static Session findFromLocal(String id) {
        return SQLite.select()
                .from(Session.class)
                .where(Session_Table.id.eq(id))
                .querySingle();
    }
}
