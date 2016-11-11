package cn.ucai.superwechat.net;

import android.content.Context;
import android.util.Log;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;

import java.io.File;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.SuperWeChatHelper;
import cn.ucai.superwechat.bean.Resultbean;
import cn.ucai.superwechat.utils.L;
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
    public static void updateNick(Context context, String name ,String newnick, OkHttpUtils.OnCompleteListener<Resultbean> listener){
        OkHttpUtils<Resultbean> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_UPDATE_USER_NICK)
                .addParam(I.User.USER_NAME,name)
                .addParam(I.User.NICK,newnick)
                .targetClass(Resultbean.class)
                .execute(listener);
    }
    public static void updateAvatar(Context context, String name , File file, OkHttpUtils.OnCompleteListener<Resultbean> listener){
        OkHttpUtils<Resultbean> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_UPDATE_AVATAR)
                .addParam(I.NAME_OR_HXID,name)
                .addParam(I.AVATAR_TYPE,I.AVATAR_TYPE_USER_PATH)
                .addFile2(file)
                .targetClass(Resultbean.class)
                .post()
                .execute(listener);
    }
    public static void downloadAvatar(Context context, String name , OkHttpUtils.OnCompleteListener<Resultbean> listener){
        OkHttpUtils<Resultbean> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_UPDATE_AVATAR)
                .addParam(I.NAME_OR_HXID,name)
                .addParam(I.AVATAR_TYPE,"user_avatar")
                .addFormParam(I.Avatar.AVATAR_SUFFIX,I.AVATAR_SUFFIX_JPG)
                .targetClass(Resultbean.class)
                .execute(listener);
    }
    public static void findUser(Context context, String name , OkHttpUtils.OnCompleteListener<Resultbean> listener){
        OkHttpUtils<Resultbean> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_FIND_USER)
                .addParam(I.User.USER_NAME,name)
                .targetClass(Resultbean.class)
                .execute(listener);
    }
    //http://101.251.196.90:8000/SuperWeChatServerV2.0/addContact?m_contact_user_name=a&m_contact_cname=f
    public static void addContact(Context context, String name ,String cname , OkHttpUtils.OnCompleteListener<Resultbean> listener){
        OkHttpUtils<Resultbean> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_ADD_CONTACT)
                .addParam(I.Contact.USER_NAME,name)
                .addParam(I.Contact.CU_NAME,cname)
                .targetClass(Resultbean.class)
                .execute(listener);
    }
    //http://101.251.196.90:8000/SuperWeChatServerV2.0/downloadContactAllList?m_contact_user_name=long
    public static void downloadContactAllList(Context context, OkHttpUtils.OnCompleteListener<Resultbean> listener){
        OkHttpUtils<Resultbean> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_DOWNLOAD_CONTACT_ALL_LIST)
                .addParam(I.Contact.USER_NAME, EMClient.getInstance().getCurrentUser())
                .targetClass(Resultbean.class)
                .execute(listener);
    }
    //http://101.251.196.90:8000/SuperWeChatServerV2.0/deleteContact?m_contact_user_name=a&m_contact_cname=f
    public static void deleteContact(Context context, String name ,String cname , OkHttpUtils.OnCompleteListener<Resultbean> listener){
        OkHttpUtils<Resultbean> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_DELETE_CONTACT)
                .addParam(I.Contact.USER_NAME,name)
                .addParam(I.Contact.CU_NAME,cname)
                .targetClass(Resultbean.class)
                .execute(listener);
    }
    //http://101.251.196.90:8000/SuperWeChatServerV2.0/createGroup?
    // m_group_hxid=d&m_group_name=d&m_group_description=d&m_group_owner=d&m_group_is_public=true&m_group_allow_invites=true
    public static void createGroup(Context context, EMGroup emGroup, File file, OkHttpUtils.OnCompleteListener<Resultbean> listener){
        OkHttpUtils<Resultbean> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_CREATE_GROUP)
                .addParam(I.Group.HX_ID,emGroup.getGroupId())
                .addParam(I.Group.NAME,emGroup.getGroupName())
                .addParam(I.Group.DESCRIPTION,emGroup.getDescription())
                .addParam(I.Group.OWNER,emGroup.getOwner())
                .addParam(I.Group.IS_PUBLIC,String.valueOf(emGroup.isPublic()))
                .addParam(I.Group.ALLOW_INVITES,String.valueOf(emGroup.isAllowInvites()))
                .targetClass(Resultbean.class)
                .addFile2(file)
                .post()
                .execute(listener);
    }
    public static void createGroup(Context context, EMGroup emGroup , OkHttpUtils.OnCompleteListener<Resultbean> listener){
        OkHttpUtils<Resultbean> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_CREATE_GROUP)
                .addParam(I.Group.HX_ID,emGroup.getGroupId())
                .addParam(I.Group.NAME,emGroup.getGroupName())
                .addParam(I.Group.DESCRIPTION,emGroup.getDescription())
                .addParam(I.Group.OWNER,emGroup.getOwner())
                .addParam(I.Group.IS_PUBLIC,String.valueOf(emGroup.isPublic()))
                .addParam(I.Group.ALLOW_INVITES,String.valueOf(emGroup.isAllowInvites()))
                .targetClass(Resultbean.class)

                .onPostExecute(listener);
    }
    //http://101.251.196.90:8000/SuperWeChatServerV2.0/addGroupMembers?m_member_user_name=a&m_member_group_hxid=a
    public static void addGroupMembers(Context context, EMGroup emGroup , OkHttpUtils.OnCompleteListener<Resultbean> listener){
        String memberArr = "";
        for (String m:emGroup.getMembers()){
            if (!m.equals(SuperWeChatHelper.getInstance().getCurrentUsernName())){
                memberArr += m+",";
            }
        }
        memberArr = memberArr.substring(0,memberArr.length()-1);
        L.e("addGroupMembers","memberarr  Dao : "+memberArr);
        OkHttpUtils<Resultbean> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_ADD_GROUP_MEMBERS)
                .addParam(I.Member.GROUP_HX_ID,emGroup.getGroupId())
                .addParam(I.Member.USER_NAME,memberArr)
                .targetClass(Resultbean.class)
                .execute(listener);
    }
}
