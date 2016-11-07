package cn.ucai.superwechat.parse;

import android.content.Context;

import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.UserAvatar;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.util.EMLog;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.superwechat.SuperWeChatHelper;

public class AppParseManager {

	private static final String TAG = AppParseManager.class.getSimpleName();
	
	private static final String ParseAppID = "UUL8TxlHwKj7ZXEUr2brF3ydOxirCXdIj9LscvJs";
	private static final String ParseClientKey = "B1jH9bmxuYyTcpoFfpeVslhmLYsytWTxqYqKQhBJ";
	
//	private static final String ParseAppID = "task";
//	private static final String ParseClientKey = "123456789";

	private static final String CONFIG_TABLE_NAME = "hxuser";
	private static final String CONFIG_USERNAME = "username";
	private static final String CONFIG_NICK = "nickname";
	private static final String CONFIG_AVATAR = "avatar";

	private static AppParseManager instance = new AppParseManager();
	

	private AppParseManager() {
	}

	public static AppParseManager getInstance() {
		return instance;
	}

	public void onInit(Context context) {
		Context appContext = context.getApplicationContext();
		Parse.enableLocalDatastore(appContext);
		Parse.initialize(context, ParseAppID, ParseClientKey);
//		Parse.initialize(new Parse.Configuration.Builder(appContext)
//		        .applicationId(ParseAppID)
//		        .server("http://114.215.141.221:1337/parse/")
//		        .build());
	}

	public boolean updateParseNickName(final String nickname) {
		String username = EMClient.getInstance().getCurrentUser();
		ParseQuery<ParseObject> pQuery = ParseQuery.getQuery(CONFIG_TABLE_NAME);
		pQuery.whereEqualTo(CONFIG_USERNAME, username);
		ParseObject pUser = null;
		try {
			pUser = pQuery.getFirst();
			if (pUser == null) {
				return false;
			}
			pUser.put(CONFIG_NICK, nickname);
			pUser.save();
			return true;
		} catch (ParseException e) {
			if(e.getCode()==ParseException.OBJECT_NOT_FOUND){
				pUser = new ParseObject(CONFIG_TABLE_NAME);
				pUser.put(CONFIG_USERNAME, username);
				pUser.put(CONFIG_NICK, nickname);
				try {
					pUser.save();
					return true;
				} catch (ParseException e1) {
					e1.printStackTrace();
					EMLog.e(TAG, "parse error " + e1.getMessage());
				}
				
			}
			e.printStackTrace();
			EMLog.e(TAG, "parse error " + e.getMessage());
		} catch(Exception e) {
			EMLog.e(TAG, "updateParseNickName error");
			e.printStackTrace();
		}
		return false;
	}

	public void getContactInfos(List<String> usernames, final EMValueCallBack<List<UserAvatar>> callback) {
		ParseQuery<ParseObject> pQuery = ParseQuery.getQuery(CONFIG_TABLE_NAME);
		pQuery.whereContainedIn(CONFIG_USERNAME, usernames);
		pQuery.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> arg0, ParseException arg1) {
				if (arg0 != null) {
					List<UserAvatar> mList = new ArrayList<UserAvatar>();
					for (ParseObject pObject : arg0) {
					    UserAvatar user = new UserAvatar(pObject.getString(CONFIG_USERNAME));
						ParseFile parseFile = pObject.getParseFile(CONFIG_AVATAR);
						if (parseFile != null) {
							user.setAvatar(parseFile.getUrl());
						}
						user.setMUserNick(pObject.getString(CONFIG_NICK));
						EaseCommonUtils.setAppUserInitialLetter(user);
						mList.add(user);
					}
					callback.onSuccess(mList);
				} else {
					callback.onError(arg1.getCode(), arg1.getMessage());
				}
			}
		});
	}

	
	public void asyncGetCurrentUserInfo(final EMValueCallBack<UserAvatar> callback){
		final String username = EMClient.getInstance().getCurrentUser();
		asyncGetUserInfo(username, new EMValueCallBack<UserAvatar>() {

			@Override
			public void onSuccess(UserAvatar value) {
				callback.onSuccess(value);
			}

			@Override
			public void onError(int error, String errorMsg) {
				if (error == ParseException.OBJECT_NOT_FOUND) {
					ParseObject pUser = new ParseObject(CONFIG_TABLE_NAME);
					pUser.put(CONFIG_USERNAME, username);
					pUser.saveInBackground(new SaveCallback() {
						
						@Override
						public void done(ParseException arg0) {
							if(arg0==null){
								callback.onSuccess(new UserAvatar(username));
							}
						}
					});
				}else{
					callback.onError(error, errorMsg);
				}
			}
		});
	}
	
	public void asyncGetUserInfo(final String username,final EMValueCallBack<UserAvatar> callback){
		ParseQuery<ParseObject> pQuery = ParseQuery.getQuery(CONFIG_TABLE_NAME);
		pQuery.whereEqualTo(CONFIG_USERNAME, username);
		pQuery.getFirstInBackground(new GetCallback<ParseObject>() {
			
			@Override
			public void done(ParseObject pUser, ParseException e) {
				if(pUser!=null){
					String nick = pUser.getString(CONFIG_NICK);
					ParseFile pFile = pUser.getParseFile(CONFIG_AVATAR);
					if(callback!=null){
					    UserAvatar user = SuperWeChatHelper.getInstance().getAppContactList().get(username);
						if(user!=null){
							user.setMUserNick(nick);
							if (pFile != null && pFile.getUrl() != null) {
								user.setAvatar(pFile.getUrl());
							}
						}else{
						    user = new UserAvatar(username);
						    user.setMUserNick(nick);
						    if (pFile != null && pFile.getUrl() != null) {
                                user.setAvatar(pFile.getUrl());
                            }
						}
						callback.onSuccess(user);
					}
				}else{
					if(callback!=null){
						callback.onError(e.getCode(), e.getMessage());
					}
				}
				
			}
		});
	}

	public String uploadParseAvatar(byte[] data) {
		String username = EMClient.getInstance().getCurrentUser();
		ParseQuery<ParseObject> pQuery = ParseQuery.getQuery(CONFIG_TABLE_NAME);
		pQuery.whereEqualTo(CONFIG_USERNAME, username);
		ParseObject pUser = null;
		try {
			pUser = pQuery.getFirst();
			if (pUser == null) {
				pUser = new ParseObject(CONFIG_TABLE_NAME);
				pUser.put(CONFIG_USERNAME, username);
			}
			ParseFile pFile = new ParseFile(data);
			pUser.put(CONFIG_AVATAR, pFile);
			pUser.save();
			return pFile.getUrl();
		} catch (ParseException e) {
			if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {
				try {
					pUser = new ParseObject(CONFIG_TABLE_NAME);
					pUser.put(CONFIG_USERNAME, username);
					ParseFile pFile = new ParseFile(data);
					pUser.put(CONFIG_AVATAR, pFile);
					pUser.save();
					return pFile.getUrl();
				} catch (ParseException e1) {
					e1.printStackTrace();
					EMLog.e(TAG, "parse error " + e1.getMessage());
				}
			} else {
				e.printStackTrace();
				EMLog.e(TAG, "parse error " + e.getMessage());
			}
		} catch(Exception e) {
			EMLog.e(TAG, "uploadParseAvatar error");
			e.printStackTrace();
		}
		return null;
	}

}
