package cn.ucai.superwechat.net;

import android.content.Context;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.bean.Resultbean;
import cn.ucai.superwechat.utils.OkHttpUtils;

/**
 * Created by Administrator on 2016/11/1.
 */
public class Dao {
    public static void register(Context context, String name , String nick, String password , OkHttpUtils.OnCompleteListener<Resultbean> listener){
        OkHttpUtils<Resultbean> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_REGISTER)
                .addParam(I.User.USER_NAME,name)
                .addParam(I.User.NICK,nick)
                .addParam(I.User.PASSWORD,password)
                .targetClass(Resultbean.class)
                .post()
                .execute(listener);
    }
    public static void unregister(Context context, String name , OkHttpUtils.OnCompleteListener<Resultbean> listener){
        OkHttpUtils<Resultbean> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_UNREGISTER)
                .addParam(I.User.USER_NAME,name)
                .targetClass(Resultbean.class)
                .execute(listener);
    }
    public static void login(Context context, String name ,String password, OkHttpUtils.OnCompleteListener<Resultbean> listener){
        OkHttpUtils<Resultbean> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_LOGIN)
                .addParam(I.User.USER_NAME,name)
                .addParam(I.User.PASSWORD,password)
                .targetClass(Resultbean.class)
                .execute(listener);
    }
}
