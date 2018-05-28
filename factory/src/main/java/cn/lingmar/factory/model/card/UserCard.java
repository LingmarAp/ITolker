package cn.lingmar.factory.model.card;

import java.sql.Date;

import cn.lingmar.factory.model.Author;
import cn.lingmar.factory.model.db.User;

/**
 * 用户卡片，用于接收服务器返回
 */
public class UserCard implements Author {

    private String id;
    // 用户名
    private String name;
    // 电话
    private String phone;
    // 头像
    private String portrait;
    // 描述
    private String description;
    // 性别
    private int sex = 0;

    // 用户关注人的数量
    private int follows;

    // 用户粉丝数的数量
    private int following;

    // 我与当前User的关系状态，是否已经关注
    private boolean isFollow;

    // 用户信息最后更新的时间
    private Date modifyAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getFollows() {
        return follows;
    }

    public void setFollows(int follows) {
        this.follows = follows;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }

    public Date getModifyAt() {
        return modifyAt;
    }

    public void setModifyAt(Date modifyAt) {
        this.modifyAt = modifyAt;
    }

    // 缓存一个对应的User
    // transient修饰后不被Gson解析
    private transient User user;

    public User build() {
        if (user == null) {
            User user = new User();
            user.setId(id);
            user.setName(name);
            user.setPortrait(portrait);
            user.setPhone(phone);
            user.setDescription(description);
            user.setSex(sex);
            user.setFollow(isFollow);
            user.setFollows(follows);
            user.setFollowing(following);
            user.setModifyAt(modifyAt);
            this.user = user;
        }
        return user;
    }
}
