package cn.lingmar.factory.data.message;

import cn.lingmar.factory.data.DbDataSource;
import cn.lingmar.factory.model.db.Message;

/**
 * 消息的数据源定义
 * 实现类是：MessageRepository
 * 专注的是：Message表
 */
public interface MessageDataSource extends DbDataSource<Message> {
}
