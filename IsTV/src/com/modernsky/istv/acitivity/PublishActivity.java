package com.modernsky.istv.acitivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.aliyun.mbaas.oss.callback.SaveCallback;
import com.aliyun.mbaas.oss.model.OSSException;
import com.lidroid.xutils.http.RequestParams;
import com.modernsky.istv.BaseActivity;
import com.modernsky.istv.R;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.action.UserAction;
import com.modernsky.istv.adapter.CommonAdapter;
import com.modernsky.istv.adapter.ViewHolder;
import com.modernsky.istv.bean.School;
import com.modernsky.istv.service.UserService;
import com.modernsky.istv.tool.BitmapTool;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.tool.TimeTool;
import com.modernsky.istv.tool.UrlTool;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.FileUtils;
import com.modernsky.istv.utils.GeneralTool;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.MediaUtil;
import com.modernsky.istv.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by zqg on 2016/3/2.
 */
public class PublishActivity extends BaseActivity {
    ImageView haiBaoImg;//海报
    TextView title;
    EditText liveName; //直播名字
    TextView liveBtx;//必选项
    View liveLine;    //直播名字下的线
    TextView timeStateText;
    EditText placeName; //直播地点
    TextView placeBtx; //直播地点必填项
    View locationLine;  //直播地点下的线
    ListView schoolListView;
    EditText showInfoEdit; //直播介绍
    TextView showInfoBtx;  //直播介绍必填项
    private TextView publishBtn;
    NumberPicker picker1, picker2, picker3, picker4, picker5;
    int max1 = 99;
    int min1 = 16;
    int max2 = 12;
    int max3 = 31;
    int min2 = 1;
    int min3 = 1;
    int max4 = 23;
    int min4 = 0;
    int max5 = 1;
    int min5 = 0;
    private static final int PHOTO_REQUEST_CAMERA = 101;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 201;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 301;// 结果
    Dialog eduation_dialog;
    /* 头像名称 */
    private static String PHOTO_FILE_NAME = "temp_photo.jpg";
    private File tempFile;
    private Bitmap bitmap;
    private String picUrl;
    private boolean hasUpdateHaiBao;
    //56e38527e4b016927a8e2cd2
    //    private static final String IMAGE_FILE_LOCATION = "file:///sdcard/temp.jpg";//temp file
    private static final String IMAGE_FILE_LOCATION = "file://"+Environment.getExternalStorageDirectory()+"/temp.jpg";//temp file
    Uri imageUri = Uri.parse(IMAGE_FILE_LOCATION);//The Uri to store the big bitmap
    private CommonAdapter<School> schoolCommonAdapter;
    private boolean shouldShowSchoolList;
    private String schoolName;
    private String schoolId;
    private boolean showLocation=true;
//    private String location;
//    Uri imageUri;


    @Override
    public void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_publish);
    }


    @Override
    public void findViewById() {
        initView();
//        initListenner();
        initNumberPicker();
        getBeforePublishInfo();
    }

    private void initView() {
        findViewById(R.id.layout_inner).setOnClickListener(this);
        findViewById(R.id.outLayout).setOnClickListener(this);
        haiBaoImg = (ImageView) findViewById(R.id.img_haibao);
        haiBaoImg.setOnClickListener(this);
        title = (TextView) findViewById(R.id.tv_title);
        liveName = (EditText) findViewById(R.id.edittext);
        liveBtx = (TextView) findViewById(R.id.tv_bxx);
        placeBtx = (TextView) findViewById(R.id.btx_location);
        liveLine = (View) findViewById(R.id.line1);
        locationLine = (View) findViewById(R.id.line_location);
        placeName = (EditText) findViewById(R.id.edittext2);
        showInfoEdit = (EditText) findViewById(R.id.edit_introduce);
        showInfoBtx = (TextView) findViewById(R.id.text_bac_introduce);
        publishBtn = (TextView) findViewById(R.id.btn_publish);
        schoolListView = (ListView) findViewById(R.id.schooListView);
        publishBtn.setOnClickListener(this);
        timeStateText = (TextView) findViewById(R.id.tv_time_state);
        schools = new ArrayList<School>();
        findViewById(R.id.img_close).setOnClickListener(this);
        initSchoolList();
    }

    List<School> schools;

    private void initSchoolList() {


        if (schoolCommonAdapter == null) {
            schoolCommonAdapter = new CommonAdapter<School>(PublishActivity.this,
                    schools,
                    R.layout.item_collegelist) {
                @Override
                public void convert(ViewHolder helper, School item) {
                    TextView text = helper.getView(R.id.text);
                    text.setText(item.getSchoolName());
                }
            };
            schoolListView.setAdapter(schoolCommonAdapter);
        } else {
            schoolCommonAdapter.notifyDataSetChanged();
        }


    }


    private void initListenner() {
        liveName.setOnFocusChangeListener(focusListenner);
        placeName.setOnFocusChangeListener(focusListenner);
        showInfoEdit.setOnFocusChangeListener(focusListenner);
        showInfoEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(showInfoEdit.getText().toString())) {
                    showInfoBtx.setVisibility(View.INVISIBLE);
                }
            }
        });
        liveName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(liveName.getText().toString())) {
                    liveBtx.setTextColor(getResources().getColor(R.color.grey4e));
                    liveLine.setBackgroundColor(getResources().getColor(R.color.grey4e));
                }
            }
        });
        placeName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
//                placeName.setTextColor(PublishActivity.this.getResources().getColor(R.color.grey4e));
                if (!showLocation) {
                    showLocation = true;
                    return;
                }
                if (placeName.getText().length() >= 1) {
                    shouldShowSchoolList = true;
                    getSchoolList(placeName.getText().toString());
                } else {
                    shouldShowSchoolList = false;
                    schoolListView.setVisibility(View.GONE);
                }
            }
        });

        schoolListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showLocation = false;
                placeName.setText(schools.get(position).getSchoolName());
                schoolListView.setVisibility(View.GONE);
                schoolName = schools.get(position).getSchoolName();
                schoolId = schools.get(position).getSchoolId() + "";

            }
        });

    }

    private void getSchoolList(String key) {
//        this.schoolKey = key;
        RequestParams params;
        params = UrlTool.getParams("key", key);
        SendActtionTool.postNoCheck(Constants.URL_GETCITY, null, UserAction.Action_GET_SCHOOLLIST, PublishActivity.this, params);
    }


    View.OnFocusChangeListener focusListenner = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                switch (v.getId()) {
                    case R.id.edittext:
                        if (TextUtils.isEmpty(liveName.getText().toString())) {
                            liveBtx.setTextColor(getResources().getColor(R.color.redc0));
                            liveLine.setBackgroundColor(getResources().getColor(R.color.redc0));
                        } else {
                            liveBtx.setTextColor(getResources().getColor(R.color.grey4e));
                            liveLine.setBackgroundColor(getResources().getColor(R.color.grey4e));
                        }
                        break;
                    case R.id.edittext2:
                        if (TextUtils.isEmpty(placeName.getText().toString())) {
                            placeBtx.setTextColor(getResources().getColor(R.color.redc0));
                            locationLine.setBackgroundColor(getResources().getColor(R.color.redc0));
                        } else {
                            placeBtx.setTextColor(getResources().getColor(R.color.grey4e));
                            locationLine.setBackgroundColor(getResources().getColor(R.color.grey4e));
                        }

                        break;
                    case R.id.edit_introduce:
                        if (TextUtils.isEmpty(showInfoEdit.getText().toString()) || (showInfoEdit.getText().toString().length() < 20)) {
                            showInfoBtx.setVisibility(View.VISIBLE);
                            showInfoBtx.setText("字数不少于20字(必填项)");
                            showInfoBtx.setTextColor(getResources().getColor(R.color.redc0));
                        } else {
                            showInfoBtx.setVisibility(View.INVISIBLE);
                        }
                        break;
                }
            }
        }
    };

    private void getBeforePublishInfo() {
        SendActtionTool.get(Constants.UserParams.URL_BEFORE_PUBLISH, null, UserAction.Action_GET_BEFOREPUBLISH, PublishActivity.this);
    }

    private void initNumberPicker() {
        picker1 = (NumberPicker) findViewById(R.id.numberPicker1);
        picker2 = (NumberPicker) findViewById(R.id.numberPicker2);
        picker3 = (NumberPicker) findViewById(R.id.numberPicker3);
        picker4 = (NumberPicker) findViewById(R.id.numberPicker4);
        picker5 = (NumberPicker) findViewById(R.id.numberPicker5);
        picker1.setMaxValue(max1);
        picker1.setMinValue(min1);
        picker2.setMaxValue(max2);
        picker2.setMinValue(min2);
        picker3.setMaxValue(max3);
        picker3.setMinValue(min3);
        picker4.setMaxValue(max4);
        picker4.setMinValue(min4);
        picker5.setMaxValue(max5);
        picker5.setMinValue(min5);
        picker2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                picker3.setMaxValue(TimeTool.getDaysOfTheMonth("20" + picker1.getValue() + "/" + newVal));
                picker3.refreshDrawableState();
            }
        });
        Calendar rightNow = Calendar.getInstance();
        int month = rightNow.get(Calendar.MONTH);
        int day = rightNow.get(Calendar.DAY_OF_MONTH);
        picker2.setValue(month + 1);
        picker3.setValue(day);

        picker5.setDisplayedValues(new String[]{"0", "30"});
//        picker5.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
//            @Override
//            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//                if (newVal == 1) {
//                    picker5.setDisplayedValues(new String[]{"30"});
//                }
////                picker3.setMaxValue(TimeTool.getDaysOfTheMonth("20" + picker1.getValue() + "/" + newVal));
////                picker3.refreshDrawableState();
//            }
//        });
        setNumberPickerDividerColor(this, picker1);
        setNumberPickerDividerColor(this, picker2);
        setNumberPickerDividerColor(this, picker3);
        setNumberPickerDividerColor(this, picker4);
        setNumberPickerDividerColor(this, picker5);
    }

    private long getTime() {
//        "yyyy-MM-dd HH:mm:ss"
        Calendar c = Calendar.getInstance();
        String year = c.get(Calendar.YEAR) + "";
//        String time = "20" + picker1.getValue() + "-";
        String time = year + "-";

        if (picker2.getValue() < 10) {
            time = time + "0" + picker2.getValue() + "-";
        } else {
            time = time + picker2.getValue() + "-";
        }
        if (picker3.getValue() < 10) {
            time = time + "0" + picker3.getValue() + " ";
        } else {
            time = time + picker3.getValue() + " ";
        }
        if (picker4.getValue() < 10) {
            time = time + "0" + picker4.getValue() + ":";
        } else {
            time = time + picker4.getValue() + ":";
        }
        if (picker5.getValue() * 30 < 10) {
            time = time + "0" + picker5.getValue() + ":";
        } else {
            time = time + "30:";
        }
        time = time + "00";
        return TimeTool.getMillTime(time);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_publish:
                if (checkEditinfo()) {
                    if (hasUpdateHaiBao) {
                        publishPreviue();
                    } else {
                        Utils.toast(PublishActivity.this, "上传海报");
                    }
                }
                break;
            case R.id.img_close:
                finish();
                break;
            case R.id.img_haibao:
                choiseImgUpdate();
                break;
            case R.id.layout_inner:
            case R.id.outLayout:
                schoolListView.setVisibility(View.GONE);
                break;
        }
    }

    public void setNumberPickerDividerColor(Context context, NumberPicker numberPicker) {
        NumberPicker picker = numberPicker;
        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    //设置分割线的颜色值
                    pf.set(picker, new ColorDrawable(context.getResources().getColor(R.color.transparent)));
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    private boolean checkEditinfo() {
        if (TextUtils.isEmpty(liveName.getText().toString())) {
            Utils.toast(this, "请填写直播名字");
            liveBtx.setTextColor(getResources().getColor(R.color.redc0));
            liveLine.setBackgroundColor(getResources().getColor(R.color.redc0));
            return false;
        }


        if (TextUtils.isEmpty(placeName.getText().toString()) || !schoolName.equals(placeName.getText().toString())) {
            Utils.toast(this, "请选取地点");
            placeBtx.setTextColor(getResources().getColor(R.color.redc0));
            locationLine.setBackgroundColor(getResources().getColor(R.color.redc0));
            return false;
        }

        if (TextUtils.isEmpty(showInfoEdit.getText().toString()) || (showInfoEdit.getText().toString().length() < 20)) {
            showInfoBtx.setText("字数不少于20字(必填项)");
            showInfoBtx.setTextColor(getResources().getColor(R.color.redc0));
            Utils.toast(this, "请填写演出介绍（至少20字）");
            return false;
        }

        return true;
    }

    // 发布预告
    private void publishPreviue() {
        showLoadingDialog();
//        publishBtn.setClickable(false);
        publishBtn.setEnabled(false);
        RequestParams params = UrlTool.getPostParams(Constants.USER_ID, UserService.getInatance().getUserBean(this).getId(),
                Constants.TYPE, "2",// 1 普通直播  2 寝室大作战
                Constants.NAME, liveName.getText().toString(),
                Constants.STARTTIME, getTime() + "",
                Constants.ENDTIME, (getTime() + 1000 * 60 * 15) + "",  //启示时间后的20分中
                "liveProfile", showInfoEdit.getText().toString(),
                "location", placeName.getText().toString(),
                "posterImg", picUrl
        );

        LogUtils.d("时间是" + TimeTool.getFormaTime2(getTime()));
        SendActtionTool.post(Constants.UserParams.URL_PUBLISH_PREVIUE, null, UserAction.ACTION_PUBLISHPREVIUE, PublishActivity.this, params);
    }

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        super.onSuccess(service, action, value);

        switch ((UserAction) action) {
            case ACTION_PUBLISHPREVIUE:
                Utils.toast(PublishActivity.this, "发布成功");
                publishBtn.setText("审核中");
//                Utils.sendBroadcastToMainactivityToUpdateUserinfo(this);
                finish();
                break;
            case Action_GET_BEFOREPUBLISH:
                LogUtils.d("onSuccess---Action_GET_BEFOREPUBLISH----" + value.toString());
                try {
                    JSONObject data = ((JSONObject) value).getJSONObject("data");
                    timeStateText.setText(data.getString("message"));
                    picUrl = data.getString("posterImg");
//                    location=data.getString("location");
                    schoolName = data.getString("location");
                    placeName.setText(schoolName);
                    initListenner();
                    BitmapTool.getInstance().getAdapterUitl().display(haiBaoImg, picUrl);
                    hasUpdateHaiBao = !TextUtils.isEmpty(picUrl);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Action_GET_SCHOOLLIST:
                try {
                    schools.clear();
                    schools.addAll(JSON.parseArray(((JSONObject) value).getString("jsonList"), School.class));
                    LogUtils.d("Action_GET_SCHOOLLIST  schools.size()===" + schools.size());
                    if (schools == null || schools.size() == 0)
                        break;
                    initSchoolList();
                    if (shouldShowSchoolList) {
                        schoolListView.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    schoolListView.setVisibility(View.GONE);
                }
                break;

        }
    }

    @Override
    public void onFaile(ServiceAction service, Object action, Object value) {
        super.onFaile(service, action, value);
        switch ((UserAction) action) {
            case ACTION_PUBLISHPREVIUE:
                LogUtils.d("onFaile-----" + value.toString());
                dismissDialog();
                Utils.toast(PublishActivity.this, value.toString());
//                finish();
                break;
        }
    }

    @Override
    public void onException(ServiceAction service, Object action, Object value) {
        super.onException(service, action, value);
        switch ((UserAction) action) {
            case ACTION_PUBLISHPREVIUE:
                LogUtils.d("onFaile-----" + value.toString());
                dismissDialog();
                Utils.toast(PublishActivity.this, value.toString());
//                finish();
                break;
        }
    }

    @Override
    public void onFinish(ServiceAction service, Object action) {
        super.onFinish(service, action);
        dismissDialog();
        switch ((UserAction) action) {
            case ACTION_PUBLISHPREVIUE:
                publishBtn.setEnabled(true);
                Utils.sendBroadcastToMainactivityToUpdateUserinfo(this);
                break;
        }

    }

    private void choiseImgUpdate() {
        View eatsView = View.inflate(this, R.layout.complete_choise_img, null);
        eduation_dialog = new Dialog(this, R.style.MmsDialogTheme);
        android.view.View.OnClickListener choiseListener = new android.view.View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.choise_img_phone:
                        PHOTO_FILE_NAME = MediaUtil.getFinalString() + ".jpg";
                        camera();
                        eduation_dialog.dismiss();
                        break;
                    case R.id.choise_img_pic:
                        gallery();
                        eduation_dialog.dismiss();
                        break;
                    case R.id.choise_img_cancle:
                        eduation_dialog.dismiss();
                        break;
                    case R.id.rl_dialog:
                        eduation_dialog.dismiss();
                        break;
                    default:
                        break;
                }
            }
        };

        eatsView.findViewById(R.id.choise_img_phone).setOnClickListener(choiseListener);
        eatsView.findViewById(R.id.rl_dialog).setOnClickListener(choiseListener);
        eatsView.findViewById(R.id.choise_img_pic).setOnClickListener(choiseListener);
        eatsView.findViewById(R.id.choise_img_cancle).setOnClickListener(choiseListener);

        eduation_dialog.setContentView(eatsView);
        eduation_dialog.setCanceledOnTouchOutside(true);
        Window dialogWindow = eduation_dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.getDecorView().setPadding(0, 0, 0, 0);

        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = eduation_dialog.getWindow().getAttributes(); // 获取对话框当前的参数值
        p.width = (d.getWidth()); // 宽度设置为屏幕的0.95
        dialogWindow.setAttributes(p);
        eduation_dialog.setCanceledOnTouchOutside(true);
        eduation_dialog.show();
    }

    /*
  * 从相册获取
  */
    public void gallery() {
        // 激活系统图库，选择一张图片
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    /*
    * 从相机获取
    */
    public void camera() {
        Intent intentc = new Intent("android.media.action.IMAGE_CAPTURE");
        // 判断存储卡是否可以用，可用进行存储
        if (hasSdcard()) {
            intentc.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), PHOTO_FILE_NAME)));
        }
        startActivityForResult(intentc, PHOTO_REQUEST_CAMERA);
    }

    private boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PHOTO_REQUEST_GALLERY:
                if (data != null) {
                    // 得到图片的全路径
                    Uri uri = data.getData();
                    String path = Utils.getAbsoluteImagePath(this, uri);
                    tempFile = new File(path);
                    crop(uri, true);

                }
                break;
            case PHOTO_REQUEST_CAMERA:
                if (hasSdcard()) {
                    tempFile = new File(Environment.getExternalStorageDirectory(), PHOTO_FILE_NAME);
                    crop(Uri.fromFile(tempFile), true);
                } else {
                    Toast.makeText(this, "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
                }
                break;
            case PHOTO_REQUEST_CUT:
                if (resultCode == RESULT_OK) {
                    try {
                        bitmap = decodeUriAsBitmap(imageUri);
                        tempFile = new File(Environment.getExternalStorageDirectory(), PHOTO_FILE_NAME);
                        if ( FileUtils.saveBitmap(tempFile, bitmap)) {
                            // uploadImg("File", tempFile, user.getUserId());
                            updateImg(tempFile);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Utils.toast(this, "请重新选择");
                }

                break;

        }
    }

    private Bitmap decodeUriAsBitmap(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    /**
     * 剪切图片
     *
     * @param uri
     * @function:
     * @author:Jerry
     * @date:2013-12-30
     */
    private void crop(Uri uri, boolean isHaibao) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        if (isHaibao) {
            // 裁剪框的比例，1：1
            intent.putExtra("aspectX", 16);
            intent.putExtra("aspectY", 9);
            // 裁剪后输出图片的尺寸大小
            intent.putExtra("outputX", 621);
            intent.putExtra("outputY", 349);
        } else {
            // 裁剪框的比例，1：1
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("outputX", 250);
            intent.putExtra("outputY", 250);
        }
        // 图片格式
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
//        intent.putExtra("return-data", true);// true:不返回uri，false：返回uri

        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());


        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    /**
     * 更换选中的新头像
     */
    private void updateImg(File file) {
        if (file.length()>1048576*10) {
            Utils.toast(this,"图片过大，请选取10M以下的图片");
            return;
        }

        showLoadingDialog("正在上传图片");
        String uri = file.getAbsolutePath();
        LogUtils.t("uri--uri", uri);
        GeneralTool.uploadFile(uri, new SaveCallback() {
            @Override
            public void onProgress(String arg0, int arg1, int arg2) {
            }

            @Override
            public void onFailure(String arg0, OSSException arg1) {
                arg1.printStackTrace();
                LogUtils.e("OSSException------" + arg1.toString());
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        dismissDialog();
                        Utils.toast(PublishActivity.this, "上传失败");
                    }
                });
            }

            // 上传成功
            @Override
            public void onSuccess(String arg0) {
//                dismissDialog();
                picUrl = Constants.UserParams.USER_URL + arg0;
                LogUtils.d("picUrl==" + picUrl);


                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        dismissDialog();
                        Utils.toast(PublishActivity.this, "上传成功");
                        BitmapTool.getInstance().getAdapterUitl().display(haiBaoImg, picUrl);
                        hasUpdateHaiBao = true;
                    }
                });

//                Utils.toast(ApplyForActivity.this, "上传成功" + picUrl);

            }
        });
    }

}
