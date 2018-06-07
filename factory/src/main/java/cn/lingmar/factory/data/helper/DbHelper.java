package cn.lingmar.factory.data.helper;

import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.lingmar.factory.model.db.AppDatabase;
import cn.lingmar.factory.model.db.Group;
import cn.lingmar.factory.model.db.GroupMember;
import cn.lingmar.factory.model.db.Group_Table;
import cn.lingmar.factory.model.db.Message;
import cn.lingmar.factory.model.db.Session;

/**
 * 数据库的辅助工具类
 * 辅助完成：增删改
 */
public class DbHelper {
    private static final DbHelper instance;

    static {
        instance = new DbHelper();
    }

    /**
     * 观察者集合
     * Class<?>：观察的表
     * Set<ChangedListener>：每张表对应的不同的观察者
     */
    private final Map<Class<?>, Set<ChangedListener>> changedListeners = new HashMap<>();

    private <Model extends BaseModel> Set<ChangedListener> getListeners(Class<Model> tClass) {
        if (changedListeners.containsKey(tClass))
            return changedListeners.get(tClass);
        return null;
    }

    /**
     * 添加一个监听
     *
     * @param tClass   对某个表关注
     * @param listener 监听者
     * @param <Model>  表的泛型
     */
    public static <Model extends BaseModel> void addChangedListener(final Class<Model> tClass,
                                                     ChangedListener<Model> listener) {
        Set<ChangedListener> changedListeners = instance.getListeners(tClass);
        if (changedListeners == null) {
            changedListeners = new HashSet<>();
            instance.changedListeners.put(tClass, changedListeners);
        }
        changedListeners.add(listener);
    }

    public static <Model extends BaseModel> void removeChangedListener(final Class<Model> tClass,
                                                                       ChangedListener<Model> listener) {
        Set<ChangedListener> changedListeners = instance.getListeners(tClass);
        if (changedListeners == null) {
            return;
        }
        changedListeners.remove(listener);
    }

    /**
     * 新增或修改的统一方法
     *
     * @param tClass  传递Class信息
     * @param models  Class对应的实例数组
     * @param <Model> 这个实例的泛型
     */
    @SafeVarargs
    public static <Model extends BaseModel> void save(final Class<Model> tClass, final Model... models) {
        if (models == null || models.length == 0)
            return;

        DatabaseDefinition definition = FlowManager.getDatabase(AppDatabase.class);
        definition.beginTransactionAsync(databaseWrapper -> {
            FlowManager.getModelAdapter(tClass)
                    .saveAll(Arrays.asList(models));
            // 唤起通知
            instance.notifySave(tClass, models);
        }).build().execute();
    }

    /**
     * 进行数据库删除的同一方法
     *
     * @param tClass  传递Class信息
     * @param models  Class对应的实例数组
     * @param <Model> 这个实例的泛型
     */
    @SafeVarargs
    public static <Model extends BaseModel> void delete(Class<Model> tClass, Model... models) {
        if (models == null || models.length == 0)
            return;

        DatabaseDefinition definition = FlowManager.getDatabase(AppDatabase.class);
        definition.beginTransactionAsync(databaseWrapper -> {
            FlowManager.getModelAdapter(tClass)
                    .deleteAll(Arrays.asList(models));
            // 唤起通知
            instance.notifyDelete(tClass, models);
        }).build().execute();
    }

    /**
     * 进行通知调用
     *
     * @param tClass  通知的类型
     * @param models  通知的Model数组
     * @param <Model> 这个实例的泛型
     */
    @SuppressWarnings("unchecked")
    public final <Model extends BaseModel> void notifySave(Class<Model> tClass,
                                                           Model... models) {
        Set<ChangedListener> listeners = getListeners(tClass);
        if (listeners != null && listeners.size() > 0) {
            for (ChangedListener<Model> listener : listeners) {
                listener.onDataSave(models);
            }
        }

        if (GroupMember.class.equals(tClass)) {
            updateGroup((GroupMember[]) models);
        } else if (Message.class.equals(tClass)) {
            updateSession((Message[]) models);
        }
    }

    /**
     * 进行通知调用
     *
     * @param tClass  通知的类型
     * @param models  通知的Model数组
     * @param <Model> 这个实例的泛型
     */
    @SuppressWarnings("unchecked")
    public final <Model extends BaseModel> void notifyDelete(Class<Model> tClass,
                                                             Model... models) {
        Set<ChangedListener> listeners = getListeners(tClass);
        if (listeners != null && listeners.size() > 0) {
            for (ChangedListener<Model> listener : listeners) {
                listener.onDataDelete(models);
            }
        }

        if (GroupMember.class.equals(tClass)) {
            updateGroup((GroupMember[]) models);
        } else if (Message.class.equals(tClass)) {
            updateSession((Message[]) models);
        }
    }

    /**
     * 从成员中找出对应的群，并对群进行更新
     *
     * @param members 群成员列表
     */
    private void updateGroup(GroupMember... members) {
        Set<String> groupIds = new HashSet<>();
        for (GroupMember member : members) {
            groupIds.add(member.getGroup().getId());
        }

        DatabaseDefinition definition = FlowManager.getDatabase(AppDatabase.class);
        definition.beginTransactionAsync(databaseWrapper -> {
            List<Group> groups = SQLite.select()
                    .from(Group.class)
                    .where(Group_Table.id.in(groupIds))
                    .queryList();
            // 进行一次通知分发
            instance.notifySave(Group.class, groups.toArray(new Group[0]));
        }).build().execute();
    }

    /**
     * 从消息列表中，筛选出对应的会话，并对会话进行更新
     *
     * @param messages Message列表
     */
    private void updateSession(Message... messages) {
        Set<Session.Identify> identifies = new HashSet<>();
        for (Message message : messages) {
            Session.Identify identify = Session.createSessionIdentify(message);
            identifies.add(identify);
        }

        DatabaseDefinition definition = FlowManager.getDatabase(AppDatabase.class);
        definition.beginTransactionAsync(databaseWrapper -> {
            ModelAdapter<Session> adapter = FlowManager.getModelAdapter(Session.class);
            Session[] sessions = new Session[identifies.size()];

            int index = 0;
            for (Session.Identify identify : identifies) {
                Session session = SessionHelper.findFromLocal(identify.id);

                if(session == null) {
                    // 第一次聊天，建立一个你和对方的一个会话
                    session = new Session(identify);
                }

                // 把会话刷新到当前Message的最新状态
                session.refreshToNow();

                // 数据存储
                adapter.save(session);
                sessions[index++] = session;
            }

            // 进行一次通知分发
            instance.notifySave(Session.class, sessions);
        }).build().execute();
    }

    /**
     * 通知监听器
     */
    @SuppressWarnings("unchecked")
    public interface ChangedListener<Data> {
        void onDataSave(Data... list);

        void onDataDelete(Data... list);
    }

}
