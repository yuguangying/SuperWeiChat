package cn.ucai.superwechat.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.redpacketui.utils.RedPacketUtil;
import com.hyphenate.easeui.utils.EaseUserUtils;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.superwechat.Constant;
import cn.ucai.superwechat.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {


    ImageView profileAvatar;
    TextView profileNick;
    TextView profileWeixin;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initView(view);
        ButterKnife.inject(this, view);
        return view;
    }

    private void initView(View view) {
        profileAvatar = (ImageView) view.findViewById(R.id.profile_avatar);
        profileNick = (TextView) view.findViewById(R.id.profile_nick);
        profileWeixin = (TextView) view.findViewById(R.id.profile_weixin);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
            return;
        setUserInfo();
    }

    private void setUserInfo() {
        EaseUserUtils.setAppCurrentUserAvatar(getContext(), profileAvatar);
        EaseUserUtils.setAppCurrentUserNick(profileNick);
        EaseUserUtils.setAppCurrentUserNameWithNo(profileWeixin);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick({R.id.profile_phone, R.id.profile_money, R.id.profile_setting,R.id.profile})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.profile_phone:
                break;
            case R.id.profile:
                startActivity(new Intent(getActivity(),UserProfileActivity.class));
                break;
            //red packet code : 进入零钱页面
            case R.id.profile_money:
                RedPacketUtil.startChangeActivity(getActivity());
                break;
            //end of red packet code
            case R.id.profile_setting:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (((MainActivity) getActivity()).isConflict) {
            outState.putBoolean("isConflict", true);
        } else if (((MainActivity) getActivity()).getCurrentAccountRemoved()) {
            outState.putBoolean(Constant.ACCOUNT_REMOVED, true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setUserInfo();
    }
}
