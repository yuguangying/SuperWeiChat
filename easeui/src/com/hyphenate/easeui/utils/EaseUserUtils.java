package com.hyphenate.easeui.utils;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.controller.EaseUI.EaseUserProfileProvider;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.domain.UserAvatar;

public class EaseUserUtils {

    static EaseUserProfileProvider userProvider = EaseUI.getInstance().getUserProfileProvider();

//    static {
//        userProvider = EaseUI.getInstance().getUserProfileProvider();
//    }

    /**
     * get EaseUser according username
     *
     * @param username
     * @return
     */
    public static EaseUser getUserInfo(String username) {
        if (userProvider != null)
            return userProvider.getUser(username);

        return null;
    }

    public static UserAvatar getAppUserInfo(String username) {
        if (userProvider != null) {
            Log.i("main", "getAppUserInfo: "+userProvider.getAppUser(username));
            return userProvider.getAppUser(username);
        }
        return null;
    }

    /**
     * set user avatar
     *
     * @param username
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView) {
        EaseUser user = getUserInfo(username);
        if (user != null && user.getAvatar() != null) {
            try {
                int avatarResId = Integer.parseInt(user.getAvatar());
                Glide.with(context).load(avatarResId).into(imageView);
            } catch (Exception e) {
                //use default avatar
                Glide.with(context).load(user.getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.ease_default_avatar).into(imageView);
            }
        } else {
            Glide.with(context).load(R.drawable.ease_default_avatar).into(imageView);
        }
    }

    /**
     * set user's nickname
     */
    public static void setUserNick(String username, TextView textView) {
        if (textView != null) {
            EaseUser user = getUserInfo(username);
            if (user != null && user.getNick() != null) {
                textView.setText(user.getNick());
            } else {
                textView.setText(username);
            }
        }
    }

    /**
     * set app user avatar
     *
     * @param username
     */
    public static void setAppUserAvatar(Context context, String username, ImageView imageView) {
        UserAvatar user = getAppUserInfo(username);
        Log.i("avatar", "setAppUserAvatar: "+user);
        if (user==null){
            user = new UserAvatar(username);
        }
        if (user != null && user.getAvatar() != null) {
            try {
                int avatarResId = Integer.parseInt(user.getAvatar());
                Glide.with(context).load(avatarResId).into(imageView);
            } catch (Exception e) {
                //use default avatar
                Log.i("avatar", "setAppUserAvatar: use default avatar");
                Log.i("avatar", "setAppUserAvatar: Exception "+e.toString());
                Glide.with(context).load(user.getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.ease_default_avatar).into(imageView);
            }
        } else {
            Log.i("avatar", "user==null: use default avatar");
            Glide.with(context).load(R.drawable.ease_default_avatar).into(imageView);
        }
    }

    /**
     * set app user's nickname
     */
    public static void setAppUserNick(String username, TextView textView) {
        if (textView != null) {
            UserAvatar user = getAppUserInfo(username);
            Log.i("main", "setAppUserNick: "+user);
            if (user != null && user.getMUserNick() != null) {
                textView.setText(user.getMUserNick());
            } else {
                textView.setText(username);
            }
        }
    }

    public static void setAppCurrentUserAvatar(Context context, ImageView profileAvatar) {
        String name = EMClient.getInstance().getCurrentUser();
        setAppUserAvatar(context,name,profileAvatar);
    }

    public static void setAppCurrentUserNick( TextView textView) {
        String name = EMClient.getInstance().getCurrentUser();
        setAppUserNick(name,textView);
    }


    public static void setAppCurrentUserNameWithNo(TextView textView) {
        String name = EMClient.getInstance().getCurrentUser();
        setAppUserNameWithNo(name,textView);
    }

    private static void setAppUserNameWithNo(String name, TextView textView) {
        textView.setText("微信号："+name);
    }
}
