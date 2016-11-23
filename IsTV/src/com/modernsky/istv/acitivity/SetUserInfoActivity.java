package com.modernsky.istv.acitivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliyun.mbaas.oss.callback.SaveCallback;
import com.aliyun.mbaas.oss.model.OSSException;
import com.modernsky.istv.BaseActivity;
import com.modernsky.istv.R;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.action.UserAction;
import com.modernsky.istv.bean.UserEntity;
import com.modernsky.istv.service.UserService;
import com.modernsky.istv.tool.BitmapTool;
import com.modernsky.istv.tool.PictureTool;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.tool.UrlTool;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.Constants.ResultConst;
import com.modernsky.istv.utils.Constants.UserParams;
import com.modernsky.istv.utils.GeneralTool;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.MediaUtil;
import com.modernsky.istv.utils.Utils;
import com.modernsky.istv.view.PopPictureView;

import java.io.File;

/**
 * @author rendy 设置个人信息界面
 */
public class SetUserInfoActivity extends BaseActivity {
    PopPictureView popView;
    private String uri = "";
    private TextView tetName, tetPhone, tetEmail;
    private ImageView imgUser;
    private UserEntity userBean;
    // 上传成功 之后的新的URL
    private String userFaceUrl;
    private File tempFile;

    @Override
    public void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_setuserinfo);
    }

    @Override
    public void findViewById() {
        findViewById(R.id.userinfo_emailArea).setOnClickListener(this);
        findViewById(R.id.userinfo_imgArea).setOnClickListener(this);
        findViewById(R.id.userinfo_nameArea).setOnClickListener(this);
        findViewById(R.id.userinfo_phoneArea).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
        imgUser = (ImageView) findViewById(R.id.userinfo_userImg);
        tetEmail = (TextView) findViewById(R.id.userinfo_emialTet);
        tetName = (TextView) findViewById(R.id.userinfo_name);
        tetPhone = (TextView) findViewById(R.id.userinfo_phoneTet);
    }

    private void initUserInfo() {
        userBean = UserService.getInatance().getUserBean(this);
        if (!GeneralTool.isEmpty(userBean.getFaceUrl())) {
            BitmapTool.getInstance().getAdapterUitl()
                    .display(imgUser, userBean.getFaceUrl());
        }
        tetEmail.setText(userBean.getEmail());
        tetName.setText(userBean.getUserName());
        tetPhone.setText(userBean.getMobile());

    }

    @Override
    protected void onResume() {
        super.onResume();
        initUserInfo();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.userinfo_imgArea:
                if (popView == null) {
                    popView = new PopPictureView(SetUserInfoActivity.this,
                            SetUserInfoActivity.this);
                }
                popView.showAtLocation(v, 0, 0, 0);
                break;
            case R.id.userinfo_nameArea:
                Intent name = new Intent(getApplicationContext(),
                        EditUserInfoActivity.class);
                name.putExtra(EditUserInfoActivity.class.getName(), R.string._name);
                startActivity(name);
                break;
            case R.id.userinfo_phoneArea:
                Intent phone = new Intent(getApplicationContext(),
                        EditUserInfoActivity.class);
                phone.putExtra(EditUserInfoActivity.class.getName(),
                        R.string._phone);
                startActivity(phone);
                break;
            case R.id.userinfo_emailArea:
                Intent email = new Intent(getApplicationContext(),
                        EditUserInfoActivity.class);
                email.putExtra(EditUserInfoActivity.class.getName(),
                        R.string._emial);
                startActivity(email);
                break;
            case R.id.btn_pick_photo:
                GeneralTool.showFileChooser(SetUserInfoActivity.this,
                        ResultConst.RESULT_SHARE_LOCAL_PHOTO);
                popView.dismiss();
                break;
            case R.id.btn_pick_take:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // 处理拍照后的图片保存路径
                tempFile = new File(MediaUtil.createPictureFile());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
                startActivityForResult(intent, ResultConst.RESULT_TAKE_PHOTO);
                popView.dismiss();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK != resultCode) {
            return;
        }
        switch (requestCode) {

            // 照完照片后的 回调
            case ResultConst.RESULT_TAKE_PHOTO:
                String sdStatus = Environment.getExternalStorageState();
                if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
                    return;
                }
                uri = tempFile.getAbsolutePath();
                startPhotoZoom(Uri.fromFile(tempFile));
                break;
            // 本地照片后的 回调
            case ResultConst.RESULT_SHARE_LOCAL_PHOTO:
                if (data == null) {
                    break;
                }
                LogUtils.t("本地照片", data.getData().toString());
                String tempFile = PictureTool.getPicturePath(
                        SetUserInfoActivity.this, data.getData());
                if (tempFile == null) {
                    LogUtils.t("本地照片", "NULL");

                    break;
                }
                File temPath = new File(tempFile);
                uri = tempFile;
                startPhotoZoom(Uri.fromFile(temPath));
                break;
            case ResultConst.RESULT_SCALE_PIC: // 处理完的照片
                if (null == data) {
                    Utils.toast(SetUserInfoActivity.this, "头像处理失败");
                    break;
                } else {
                    Utils.toast(getApplicationContext(), "处理图片成功");
                }
                updateUserIcon();
                break;
            default:
                break;
        }
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        // CropImageIntentBuilder cropImage = new CropImageIntentBuilder(300,
        // 300,
        // uri);
        // cropImage.setSourceImage(uri);
        // this.startActivityForResult(cropImage.getIntent(this),
        // ResultConst.RESULT_SCALE_PIC);
        // Intent intent = new Intent("com.android.camera.action.CROP");
        // intent.setDataAndType(uri, "image/*");
        // // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        // intent.putExtra("crop", "true");
        // // aspectX aspectY 是宽高的比例
        // intent.putExtra("aspectX", 1);
        // intent.putExtra("aspectY", 1);
        // // outputX outputY 是裁剪图片宽高
        // intent.putExtra("outputX", 300);
        // intent.putExtra("return-data", true);
        // startActivityForResult(intent, 5);
    }

    /**
     * 更换选中的新头像
     */
    private void updateUserIcon() {
        showLoadingDialog();
        LogUtils.t("uri--uri", uri);
        GeneralTool.uploadFile(uri, new SaveCallback() {
            @Override
            public void onProgress(String arg0, int arg1, int arg2) {
            }

            @Override
            public void onFailure(String arg0, OSSException arg1) {
                dismissDialog();
                Utils.toast(getApplicationContext(), "上传失败");
            }

            // 上传成功
            @Override
            public void onSuccess(String arg0) {
                LogUtils.t(
                        "SetUserInfoActivity.updateUserIcon()onSuccess()", arg0);

                userFaceUrl = UserParams.USER_URL + arg0;
                SendActtionTool.post(UserParams.URL_USER_UPDATE, null,
                        UserAction.Action_Update_Face_Url,
                        SetUserInfoActivity.this, UrlTool.getPostParams(
                                UserParams.KEY, UserParams.FACE_URL,
                                UserParams.VALUE, userFaceUrl,
                                Constants.USER_ID, userBean.getId()));
            }
        });
    }

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        super.onSuccess(service, action, value);
        switch ((UserAction) action) {
            case Action_Update_Face_Url:
                userBean.setFaceUrl(userFaceUrl);
                UserService.getInatance().setUserBean(userBean, this);
                initUserInfo();
                break;
            default:
                break;
        }
    }

    @Override
    public void onFaile(ServiceAction service, Object action, Object value) {
        Utils.toast(getApplicationContext(), value.toString());
    }

    @Override
    public void onFinish(ServiceAction service, Object action) {
        dismissDialog();
    }

}
