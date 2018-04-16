package cn.lingmar.factory.persistence;

public class Account {
    // 设备的推送Id
    private static String pushId;

    public static String getPushId() {
        return pushId;
    }

    public static void setPushId(String pushId) {
        Account.pushId = pushId;
    }
}
