package cn.lingmar.factory.model.api.group;

public class GroupMemberUpdateModel {
    // 别名
    private String alias;

    // 消息的通知级别
    private int notifyLevel;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getNotifyLevel() {
        return notifyLevel;
    }

    public void setNotifyLevel(int notifyLevel) {
        this.notifyLevel = notifyLevel;
    }
}
