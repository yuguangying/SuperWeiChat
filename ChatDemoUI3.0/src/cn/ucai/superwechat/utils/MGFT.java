package cn.ucai.superwechat.utils;

import android.app.Activity;
import android.content.Intent;

import com.hyphenate.easeui.domain.UserAvatar;

import cn.ucai.superwechat.ui.AddContactActivity;
import cn.ucai.superwechat.ui.ChatActivity;
import cn.ucai.superwechat.ui.FindDetailed;
import cn.ucai.superwechat.ui.GroupSimpleDetailActivity;
import cn.ucai.superwechat.ui.SendActivity;
import cn.ucai.superwechat.ui.UserProfileActivity;

/**
 * Created by Administrator on 2016/11/7.
 */
public class MGFT {
    public static void gotoAddFriend(Activity context) {
        Intent intent = new Intent();
        intent.setClass(context, AddContactActivity.class);
        context.startActivity(intent);
    }

    public static void gotoFindProfile(Activity context, UserAvatar user) {
        Intent intent = new Intent();
        intent.setClass(context, FindDetailed.class);
        intent.putExtra("name", user);
        context.startActivity(intent);
    }

    public static void gotoSend(Activity context, UserAvatar user) {
        Intent intent = new Intent();
        intent.setClass(context, SendActivity.class);
        intent.putExtra("send", user);
        context.startActivity(intent);
    }

    public static void gotoChat(Activity context,String username) {
        context.startActivity(new Intent(context, ChatActivity.class).putExtra("userId", username));
    }

}
