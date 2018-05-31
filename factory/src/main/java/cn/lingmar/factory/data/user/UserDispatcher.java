package cn.lingmar.factory.data.user;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import cn.lingmar.factory.data.helper.DbHelper;
import cn.lingmar.factory.model.card.UserCard;
import cn.lingmar.factory.model.db.User;

public class UserDispatcher implements UserCenter {
    private static UserCenter instance;
    // 单线程池；线程队列里一个个消息进行处理
    private final Executor executor = Executors.newSingleThreadExecutor();

    public static UserCenter instance() {
        if (instance == null) {
            synchronized (UserDispatcher.class) {
                if (instance == null)
                    instance = new UserDispatcher();
            }
        }
        return instance;
    }

    @Override
    public void dispatch(UserCard... cards) {
        if (cards == null || cards.length == 0)
            return;

        executor.execute(new UserCardHandler(cards));
    }

    /**
     * 线程调度的时候就会触发run方法
     */
    private class UserCardHandler implements Runnable {
        private UserCard[] cards;

        public UserCardHandler(UserCard[] cards) {
            this.cards = cards;
        }

        @Override
        public void run() {
            // 当被线程调度的时候触发
            List<User> users = new ArrayList<>();
            for(UserCard card : cards) {
                // 进行过滤
                if(card==null || TextUtils.isEmpty(card.getId()))
                    continue;

                users.add(card.build());
            }

            DbHelper.save(User.class, users.toArray(new User[0]));
        }
    }
}
