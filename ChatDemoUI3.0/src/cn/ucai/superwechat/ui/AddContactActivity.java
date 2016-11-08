/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.ucai.superwechat.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import cn.ucai.superwechat.SuperWeChatHelper;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.bean.Resultbean;
import cn.ucai.superwechat.db.UserDao;
import cn.ucai.superwechat.net.Dao;
import cn.ucai.superwechat.utils.CommonUtils;
import cn.ucai.superwechat.utils.MGFT;
import cn.ucai.superwechat.utils.OkHttpUtils;

import com.hyphenate.easeui.domain.UserAvatar;
import com.hyphenate.easeui.widget.EaseAlertDialog;

public class AddContactActivity extends BaseActivity{
	private EditText editText;
	private RelativeLayout searchedUserLayout;
	private TextView nameText;
	private Button searchBtn;
	private String toAddUsername;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.em_activity_add_contact);
		TextView mTextView = (TextView) findViewById(R.id.add_list_friends);
		
		editText = (EditText) findViewById(R.id.edit_note);
		String strAdd = getResources().getString(R.string.add_friend);
		mTextView.setText(strAdd);
		String strUserName = getResources().getString(R.string.user_name);
		editText.setHint(strUserName);
		searchedUserLayout = (RelativeLayout) findViewById(R.id.ll_user);
		nameText = (TextView) findViewById(R.id.name);
		searchBtn = (Button) findViewById(R.id.search);
	}
	
	
	/**
	 * search contact
	 * @param v
	 */
	public void searchContact(View v) {
		final String name = editText.getText().toString();
		String saveText = searchBtn.getText().toString();
		
		if (getString(R.string.button_search).equals(saveText)) {
			toAddUsername = name;
			if(TextUtils.isEmpty(name)) {
				new EaseAlertDialog(this, R.string.Please_enter_a_username).show();
				return;
			}
			Dao.findUser(this, toAddUsername, new OkHttpUtils.OnCompleteListener<Resultbean>() {
				@Override
				public void onSuccess(Resultbean result) {
					if (result.isRetMsg()){
						String s = result.getRetData().toString().trim();
						Gson gson = new Gson();
						UserAvatar userAvatar = gson.fromJson(s, UserAvatar.class);

						boolean isContact = SuperWeChatHelper.getInstance().getAppContactList().containsValue(userAvatar);
						Log.i("main", "onSuccess: add"+isContact);
						MGFT.gotoFindProfile(AddContactActivity.this,userAvatar,isContact);
					}else {
						Log.i("AddContact", "false: 未找到");
						CommonUtils.showLongToast("未找到");
					}
				}

				@Override
				public void onError(String error) {
					Log.i("AddContact", "onError: "+error);
					CommonUtils.showLongToast(error);
				}
			});

		} 
	}	
	

	
	public void back(View v) {
		finish();
	}
}
