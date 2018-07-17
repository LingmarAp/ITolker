package cn.lingmar.factory.model.db.view;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.QueryModel;

import cn.lingmar.factory.model.db.AppDatabase;

/**
 * 群成员对应的用户简单信息表
 */
@QueryModel(database = AppDatabase.class)
public class MemberUserModel {
    @Column
    public String userId;
    @Column
    public String name;
    @Column
    public String alias;
    @Column
    public String portrait;

}
