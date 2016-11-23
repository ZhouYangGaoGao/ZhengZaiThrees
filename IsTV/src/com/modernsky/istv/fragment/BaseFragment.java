package com.modernsky.istv.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.listener.CommonListener;
import com.modernsky.istv.manager.BaseApplication;
import com.modernsky.istv.tool.DialogTool;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.view.LodingDialog;
import com.squareup.leakcanary.RefWatcher;

/**
 * @author zxm
 *         <p>
 *         fragment 界面 base
 */
public abstract class BaseFragment extends Fragment implements CommonListener,
        OnClickListener {
    private LodingDialog lodingDialog;

    /**
     * 展示加加载信息等待 dialog
     */
    public void showLoadingDialog() {
        showLoadingDialog("");
    }

    /**
     * 展示loading弹窗，并提示相应信息
     *
     * @param msg
     */
    public void showLoadingDialog(String msg) {
        if (lodingDialog == null) {
            lodingDialog = DialogTool.createLoadingDialog(getActivity(), msg);
        }
        if (lodingDialog.isShowing()) {
            return;
        } else {
            lodingDialog.show();
        }
    }

    /**
     * 隐藏加载信息 dialog
     */
    public void dismissDialog() {
        if (lodingDialog == null) {
            return;
        }
        if (lodingDialog.isShowing()) {
            lodingDialog.dismiss();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = setContentView(inflater, container, savedInstanceState);
        initView(rootView);
        return rootView;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("BaseActivity", getClass().getSimpleName());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dismissDialog();
        RefWatcher refWatcher = BaseApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }

    public abstract View setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    public abstract void initView(View rootView);

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        if (action != null && value != null)
            LogUtils.t(this.getClass().getSimpleName() + "--onSuccess--action:" + action.toString(), value.toString());
        else if (service != null && value != null)
            LogUtils.t(this.getClass().getSimpleName() + "--onSuccess--searvice:" + service.toString(), value.toString());
        else if (value != null) {
            LogUtils.t(this.getClass().getSimpleName() + "--onSuccess", value.toString());
        }

    }


    @Override
    public void onFaile(ServiceAction service, Object action, Object value) {
        if (action != null && value != null)
            LogUtils.t(this.getClass().getSimpleName() + "--onFaile--action:" + action.toString(), value.toString());
        else if (service != null && value != null)
            LogUtils.t(this.getClass().getSimpleName() + "--onFaile--searvice:" + service.toString(), value.toString());
        else if (value != null) {
            LogUtils.t(this.getClass().getSimpleName() + "--onFaile", value.toString());
        }
    }

    @Override
    public void onException(ServiceAction service, Object action, Object value) {
        if (action != null && value != null)
            LogUtils.t(this.getClass().getSimpleName() + "--onException--action:" + action.toString(), value.toString());
        else if (service != null && value != null)
            LogUtils.t(this.getClass().getSimpleName() + "--onException--searvice:" + service.toString(), value.toString());
        else if (value != null) {
            LogUtils.t(this.getClass().getSimpleName() + "--onException", value.toString());
        }
    }

    @Override
    public void onStart(ServiceAction service, Object action) {
    }

    @Override
    public void onFinish(ServiceAction service, Object action) {
    }
}
