package com.modernsky.istv.acitivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.aliyun.mbaas.oss.callback.SaveCallback;
import com.aliyun.mbaas.oss.model.OSSException;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.modernsky.istv.BaseActivity;
import com.modernsky.istv.R;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.action.UserAction;
import com.modernsky.istv.adapter.CommonAdapter;
import com.modernsky.istv.adapter.ViewHolder;
import com.modernsky.istv.bean.CityBean;
import com.modernsky.istv.bean.School;
import com.modernsky.istv.bean.VideoUpdateInfo;
import com.modernsky.istv.service.UserService;
import com.modernsky.istv.tool.BitmapTool;
import com.modernsky.istv.tool.SendActtionTool;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zqg on 2016/3/2.
 */
public class ApplyForActivity extends BaseActivity {
    View line1, line2, line3;
    EditText et1, et2, et3, et4, et5, et6, personnalInfoEdit;
    TextView btxText1, btxText2, btxText3, personInfobtx;// 必填项  1 2 3
    private static final int PHOTO_REQUEST_CAMERA = 101;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 201;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 301;// 结果
    private static final int VIDEO_REQUEST_CAMERA = 401;// 拍摄视频
    private static final int VIDEO_REQUEST_GALLERY = 501;// 获取视频
    //    private static final int CITY_GET = 601;// 获取城市
    private File videoFile;
    Dialog eduation_dialog;
    /* 头像名称 */
    private static String PHOTO_FILE_NAME = "temp_photo.png";
    private File tempFile;
    private Bitmap bitmap;
    private String picUrl;
    private ImageView haiBaoImg, idUpImg, idDownImg, peopleAndIdImg, studengtIdImg, lifeImg1, lifeImg2, myWorkImg;
    private String haiBaoUrl, idUpUrl, idDownUrl, peopleAndIdUrl, studengtIdUrl, lifeUrl1, lifeUrl2;
    //    private boolean hasHaibao,hasUpID,hasDownID,hasPeopleAndId,hasStudentId,hasLife1,hasLife2;
    //以上list  是包含必填项 的LIST
    //  1 所在城市  2 所在大学  3 所在专业  4 所在宿舍 5 所在 个人主页  6 作品数量  7 个人介绍
    private int updateImgNum = -1; //1,上传 海报  2 正面居民身份证  3 反面居民身份证  4 正面手持 证件 5 学生证
    private File mediaFile;
    private VideoUpdateInfo videoUpdateInfo;
    private boolean hasUpdateVideo;
    private String videoId;//上传给服务器乐事返回视频信息后 给的videoId
    //    private boolean hasReturnVideoInfoToServer;//是否将上传到乐视的视频信息返还给服务器
//    private boolean ifApplyAfterReturnService;

    private ImageView bacImg;
    private List<School> schools;
    private List<CityBean> citys;

    //6 第一张生活照 7 第二章生活照 8 上传作品
//    PopThreeShare popThreeShare;
    private ImageView mianzeImg;//免责声明左边的按钮
    private boolean hasMianZeState;
//        Uri imageUri;
    private static final String IMAGE_FILE_LOCATION = "file://"+Environment.getExternalStorageDirectory()+"/temp.jpg";//temp file
    Uri imageUri = Uri.parse(IMAGE_FILE_LOCATION);//The Uri to store the big bitmap
    private ListView schoolList;
    private ListView cityListView;

    private String cityName;
    private String cityId;
    private String schoolName;
    private String schoolId;

    @Override
    public void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_applyfor);
    }

    //    int editError = -1;//edit 错误项  与list相对应
    List<EditText> editList;
    List<View> lineList;
    List<TextView> textList; //必选项
    // list 中 （文字必选项）  1 所在城市  2 所在大学  3 所在专业   4  （有的话 最后一项，没有线） 是个人介绍
    private TextView btnUp;
    private String schoolKey;
    private String cityKey;
    private boolean isChoisedSchool;
    private boolean isChoisedCity;
    private boolean showSchooleListAfterchoised = true;
    private boolean showCityListAfterChoised = true;
    private boolean showCityListAfterSchooleListChoised=true;
    @Override
    public void findViewById() {
        initView();
        initList();
        initEditListenner();
//        setListViewHeightBasedOnChildren(schoolList);
    }

    private void initView() {
        LogUtils.d("userId=============" + UserService.getInatance().getUserBean(this).getId());
        line1 = findViewById(R.id.line1);
        line2 = findViewById(R.id.line2);
        line3 = findViewById(R.id.line3);
        et1 = (EditText) findViewById(R.id.edittext1);
        et2 = (EditText) findViewById(R.id.edittext2);
        et3 = (EditText) findViewById(R.id.edittext3);
        et4 = (EditText) findViewById(R.id.edittext4);
        et5 = (EditText) findViewById(R.id.edittext5);
        et6 = (EditText) findViewById(R.id.edittext6);
        btxText1 = (TextView) findViewById(R.id.text_bxx1);
//        btxText1.setOnClickListener(this);
        btxText2 = (TextView) findViewById(R.id.text_bxx2);
        btxText3 = (TextView) findViewById(R.id.text_bxx3);
        findViewById(R.id.tv_state_mianze).setOnClickListener(this);
        personInfobtx = (TextView) findViewById(R.id.text_bac_introduce);
        personnalInfoEdit = (EditText) findViewById(R.id.edit_introduce);
        btnUp = (TextView) findViewById(R.id.btn_publish);
        btnUp.setOnClickListener(this);//最下面的提交按钮
        bacImg = (ImageView) findViewById(R.id.img_me);
        bacImg.setOnClickListener(this);
        bacImg.setImageResource(R.drawable.icon_back);
        findViewById(R.id.img_search).setVisibility(View.GONE);
//        shareImg.setImageResource(R.drawable.icon_share);
        haiBaoImg = (ImageView) findViewById(R.id.img_haibao);
        haiBaoImg.setOnClickListener(this);
        idUpImg = (ImageView) findViewById(R.id.img_idup);
        idUpImg.setOnClickListener(this);
        idDownImg = (ImageView) findViewById(R.id.img_idbac);
        idDownImg.setOnClickListener(this);
        peopleAndIdImg = (ImageView) findViewById(R.id.img_idandfac);
        peopleAndIdImg.setOnClickListener(this);
        studengtIdImg = (ImageView) findViewById(R.id.img_studentid);
        studengtIdImg.setOnClickListener(this);
        lifeImg1 = (ImageView) findViewById(R.id.img_addlifepic1);
        lifeImg1.setOnClickListener(this);
        lifeImg2 = (ImageView) findViewById(R.id.img_addlifepic2);
        lifeImg2.setOnClickListener(this);
        myWorkImg = (ImageView) findViewById(R.id.img_addworks);
        myWorkImg.setOnClickListener(this);
        mianzeImg = (ImageView) findViewById(R.id.img_mianze);
        mianzeImg.setOnClickListener(this);
        schoolList = (ListView) findViewById(R.id.schooListView);
        cityListView = (ListView) findViewById(R.id.cityListView);

        findViewById(R.id.rootview).setOnClickListener(this);
        findViewById(R.id.layout_inner).setOnClickListener(this);
        schools = new ArrayList<School>();
        citys = new ArrayList<CityBean>();
        initSchoolList();
        initCityList();
    }

    private void initCityList() {
        if (cityCommonAdapter == null) {
            cityCommonAdapter = new CommonAdapter<CityBean>(ApplyForActivity.this,
                    citys,
                    R.layout.item_collegelist) {
                @Override
                public void convert(ViewHolder helper, CityBean item) {
                    TextView text = helper.getView(R.id.text);
                    text.setText(item.getCityName());
                }
            };
            cityListView.setAdapter(cityCommonAdapter);
        } else {
            cityCommonAdapter.notifyDataSetChanged();
        }
    }

    private CommonAdapter<School> schoolCommonAdapter;
    private CommonAdapter<CityBean> cityCommonAdapter;
    ForegroundColorSpan Span;
//    final List<String> list = Arrays.asList(new String[]{"北京大学", "北京理工大学", "北京航天航空大学",
//            "北京机械大学", "北京大学", "北京理工大学", "北京航天航空大学", "北京机械大学", "北京大学", "北京理工大学", "北京航天航空大学", "北京机械大学", "北京汉语言大学"});

    private void initSchoolList() {

        if (Span == null) {
            Span = new ForegroundColorSpan(getResources().getColor(R.color.whitee4_alpha));
        }


        if (schoolCommonAdapter == null) {
            schoolCommonAdapter = new CommonAdapter<School>(ApplyForActivity.this,
                    schools,
                    R.layout.item_collegelist) {
                @Override
                public void convert(ViewHolder helper, School item) {
                    TextView text = helper.getView(R.id.text);
                    text.setText(item.getSchoolName());
                }
            };
            schoolList.setAdapter(schoolCommonAdapter);
        } else {
            schoolCommonAdapter.notifyDataSetChanged();
        }
    }


    private void initList() {
        editList = Arrays.asList(new EditText[]{et1, et2, et3, personnalInfoEdit});
        lineList = Arrays.asList(new View[]{line1, line2, line3});
        textList = Arrays.asList(new TextView[]{btxText1, btxText2, btxText3, personInfobtx});
    }

    private boolean shouldShowSchoolList = true;
    private boolean shouldShowCityList = true;

    private void initEditListenner() {

        for (int i = 0; i < editList.size(); i++) {
            final int tempInt = i;
            editList.get(tempInt).setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    //失去焦点
                    if (!hasFocus) {
                        //最后一个没有线
                        if (tempInt == editList.size() - 1) {
                            if (TextUtils.isEmpty(editList.get(tempInt).getText().toString()) || editList.get(tempInt).getText().toString().length() < 20) {
                                textList.get(tempInt).setVisibility(View.VISIBLE);
                                textList.get(tempInt).setTextColor(ApplyForActivity.this.getResources().getColor(R.color.redc0));
                            } else {
                                textList.get(tempInt).setVisibility(View.INVISIBLE);
                            }
                        } else {

                            if (TextUtils.isEmpty(editList.get(tempInt).getText().toString())) {
                                lineList.get(tempInt).setBackgroundColor(ApplyForActivity.this.getResources().getColor(R.color.redc0));
                                textList.get(tempInt).setTextColor(ApplyForActivity.this.getResources().getColor(R.color.redc0));
                            } else {
                                lineList.get(tempInt).setBackgroundColor(ApplyForActivity.this.getResources().getColor(R.color.grey4e));
                                textList.get(tempInt).setTextColor(ApplyForActivity.this.getResources().getColor(R.color.grey4e));
                            }
                        }
                    }
//                    else {
//                        if (tempInt == 1) {
////                            initPopWindow();
//                        }
//                    }
                }
            });
        }
        et2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                schoolList.setVisibility(View.GONE);
                LogUtils.d("afterTextChanged---showSchooleListAfterchoised==" + showSchooleListAfterchoised);
                if (!showSchooleListAfterchoised) {
                    showSchooleListAfterchoised = true;
                    return;
                }
                et2.setTextColor(ApplyForActivity.this.getResources().getColor(R.color.grey4e));
                isChoisedSchool = false;
                if (et2.getText().length() >= 1) {
                    shouldShowSchoolList = true;
                    getSchoolList(et2.getText().toString());
                } else {
                    shouldShowSchoolList = false;
                }

            }
        });
        et1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                cityListView.setVisibility(View.GONE);
                LogUtils.d("afterTextChanged---showCityListAfterChoised==" + showCityListAfterChoised);
                if (!showCityListAfterChoised) {
                    showCityListAfterChoised = true;
                    return;
                }
                isChoisedCity=false;
                if (showCityListAfterSchooleListChoised) {
                    et1.setTextColor(ApplyForActivity.this.getResources().getColor(R.color.grey4e));
                    if (et1.getText().length() >= 1) {
                        shouldShowCityList = true;
                        LogUtils.d("afterTextChanged-------");
                        getCityList(et1.getText().toString());
                    } else {
                        shouldShowCityList = false;
                    }
                }
                showCityListAfterSchooleListChoised=true;

                if (!TextUtils.isEmpty(et1.getText().toString())) {
                    lineList.get(0).setBackgroundColor(ApplyForActivity.this.getResources().getColor(R.color.grey4e));
                    textList.get(0).setTextColor(ApplyForActivity.this.getResources().getColor(R.color.grey4e));
                }
            }
        });
        et3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(et3.getText().toString())) {
                    lineList.get(2).setBackgroundColor(ApplyForActivity.this.getResources().getColor(R.color.grey4e));
                    textList.get(2).setTextColor(ApplyForActivity.this.getResources().getColor(R.color.grey4e));
                }
            }
        });
        et2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(et2.getText().toString())) {
                    lineList.get(1).setBackgroundColor(ApplyForActivity.this.getResources().getColor(R.color.grey4e));
                    textList.get(1).setTextColor(ApplyForActivity.this.getResources().getColor(R.color.grey4e));
                }
            }
        });
        personnalInfoEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(personnalInfoEdit.getText().toString())) {
                    textList.get(3).setTextColor(ApplyForActivity.this.getResources().getColor(R.color.grey4e));
                }

            }
        });
        cityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showCityListAfterChoised = false;
                cityName = citys.get(position).getCityName();
                cityId = citys.get(position).getCityId() + "";
                et1.setText(citys.get(position).getCityName());
                    et1.setTextColor(ApplyForActivity.this.getResources().getColor(R.color.white));
                isChoisedCity = true;

            }
        });
        schoolList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                showSchooleListAfterchoised = false;
//                    shouldShowSchoolList=false;
                et2.setText(schools.get(position).getSchoolName());
                et2.setTextColor(ApplyForActivity.this.getResources().getColor(R.color.white));
                isChoisedSchool = true;
                schoolName = schools.get(position).getSchoolName();
                schoolId = schools.get(position).getSchoolId() + "";
//                    schoolList.setVisibility(View.GONE);
                if (!isChoisedCity) {
                    cityName = schools.get(position).getCityName();
                    cityId = schools.get(position).getCityId();
                    showCityListAfterSchooleListChoised=false;
                    et1.setText(cityName);
                    et1.setTextColor(ApplyForActivity.this.getResources().getColor(R.color.white));
//                        line1.setBackgroundColor();
                    line1.setBackgroundColor(ApplyForActivity.this.getResources().getColor(R.color.grey4e));
                    btxText1.setTextColor(ApplyForActivity.this.getResources().getColor(R.color.grey4e));
                    isChoisedCity = true;
                }

            }
        });

        personnalInfoEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(personnalInfoEdit.getText().toString())) {
                    personInfobtx.setVisibility(View.INVISIBLE);
                }
            }
        });


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.text_bxx1:
//                getCity();
//                break;
            case R.id.layout_inner:
            case R.id.rootview:
                if (schoolList.getVisibility() == View.VISIBLE) {
                    schoolList.setVisibility(View.GONE);
                }
                if (cityListView.getVisibility() == View.VISIBLE) {
                    cityListView.setVisibility(View.GONE);
                }
                break;
            case R.id.tv_state_mianze:
                Intent intent = new Intent(this, WebActivity.class);
                intent.putExtra(Constants.URL, Constants.URL_MIANZE);
                intent.putExtra(Constants.TYPE, 13);
                intent.putExtra(Constants.TITLE, "《免责声明》");
                startActivity(intent);
                break;
            case R.id.img_me:
                finish();
                break;
            case R.id.img_mianze:
//                finish();
                if (!hasMianZeState) {
                    mianzeImg.setImageResource(R.drawable.but_mianze_xuanze);
                    hasMianZeState = true;
                } else {
                    mianzeImg.setImageResource(R.drawable.but_mianze);
                    hasMianZeState = false;
                }
                break;
            case R.id.img_search:
//                Utils.toast(this, "分享按钮");
//                try {
//                    String shareUrl = "shareUrl";
//                    if (popThreeShare == null)
//                        popThreeShare = new PopThreeShare(this);
//                    popThreeShare.setShareUrl("title", "content", shareUrl);
//                    popThreeShare.showBototomPop();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

//                finish();
                break;
            case R.id.img_idup:
                updateImgNum = 2;
                choiseImgUpdate();
                break;
            case R.id.img_idbac:
                updateImgNum = 3;
                choiseImgUpdate();
                break;
            case R.id.img_idandfac:
                updateImgNum = 4;
                choiseImgUpdate();
                break;
            case R.id.img_studentid:
                updateImgNum = 5;
                choiseImgUpdate();
                break;
            case R.id.img_addlifepic1:
                updateImgNum = 6;
                choiseImgUpdate();
                break;
            case R.id.img_addlifepic2:
                updateImgNum = 7;
                choiseImgUpdate();
                break;
            case R.id.img_haibao:
                updateImgNum = 1;
                choiseImgUpdate();
                break;
            case R.id.img_addworks:
                updateImgNum = 8;
                choiseImgUpdate();
                break;
            case R.id.btn_publish:
                if (checkEditinfo()) {

                    if (TextUtils.isEmpty(haiBaoUrl)) {
                        Utils.toast(this, "请上传海报");
                        break;
                    }
                    if (TextUtils.isEmpty(idDownUrl) || TextUtils.isEmpty(idUpUrl) || TextUtils.isEmpty(peopleAndIdUrl)) {
                        Utils.toast(this, "请上传个人证件");
                        break;
                    }
                    if (TextUtils.isEmpty(lifeUrl1) && TextUtils.isEmpty(lifeUrl2)) {
                        Utils.toast(this, "请至少上传一张生活照");
                        break;
                    }
                    if (!hasUpdateVideo) {
                        Utils.toast(this, "请上传我的作品");
                        break;
                    }
                    if (!hasMianZeState) {
                        Utils.toast(this, "请选择免责声明");
                        break;
                    }

                    applyForAnchor();
                }
                break;
        }

    }

    private void getSchoolList(String key) {
        this.schoolKey = key;
        RequestParams params;
        if (isChoisedCity) {
            params = UrlTool.getParams("key", schoolKey, "cityName", cityName);
        } else {
            params = UrlTool.getParams("key", schoolKey);
        }
        SendActtionTool.postNoCheck(Constants.URL_GETCITY, null, UserAction.Action_GET_SCHOOLLIST, ApplyForActivity.this, params);
    }

//    private void initPopWindow() {
//        // 一个自定义的布局，作为显示的内容
//        View contentView = LayoutInflater.from(this).inflate(
//                R.layout.listpop, null);
//
//        final ForegroundColorSpan Span = new ForegroundColorSpan(getResources().getColor(R.color.whitee4_alpha));
//        final PopupWindow popupWindow = new PopupWindow(contentView,
//                line2.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT, true);
//        ListView lv = (ListView) contentView.findViewById(R.id.listView);
//        final List<String> list = Arrays.asList(new String[]{"北京大学", "北京理工大学", "北京航天航空大学", "北京机械大学", "北京大学", "北京理工大学", "北京航天航空大学", "北京机械大学", "北京大学", "北京理工大学", "北京航天航空大学", "北京机械大学", "北京汉语言大学"});
//        lv.setAdapter(new CommonAdapter<String>(ApplyForActivity.this,
//                list,
//                R.layout.item_collegelist) {
//            @Override
//            public void convert(ViewHolder helper, String item) {
//                TextView text = helper.getView(R.id.text);
//                Spannable word = new SpannableString(item);
//                word.setSpan(Span, 0, 2,
//                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                text.setText(word);
//            }
//        });
//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                popupWindow.dismiss();
//                et2.setText(list.get(position));
//            }
//        });
//        popupWindow.setTouchable(true);
//
//
////        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
////        // 我觉得这里是API的一个bug
//        popupWindow.setFocusable(true);
//        // 设置SelectPicPopupWindow弹出窗体动画效果
//        popupWindow.setAnimationStyle(R.style.AnimRight);
//        // 实例化一个ColorDrawable颜色为半透明
//        ColorDrawable dw = new ColorDrawable(0xb0000000);
//        // 设置SelectPicPopupWindow弹出窗体的背景
//        popupWindow.setBackgroundDrawable(dw);
//        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
//        popupWindow.setOutsideTouchable(true);
//        // 键盘监听
//        line2.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
//                if (keyCode == KeyEvent.KEYCODE_BACK) {
//                    popupWindow.dismiss();
//                }
//                return false;
//            }
//        });
//
////        popupWindow.setBackgroundDrawable(getResources().getDrawable(
////                R.drawable.selectmenu_bg_downward));
//
//        // 设置好参数之后再show
//        popupWindow.showAsDropDown(line2);
//    }

    private boolean checkEditinfo() {
        if (TextUtils.isEmpty(et1.getText().toString()) || !isChoisedCity) {
            btxText1.setTextColor(getResources().getColor(R.color.redc0));
            line1.setBackgroundColor(getResources().getColor(R.color.redc0));
//            editError = 0;
            LogUtils.d("et1.getText().toString()" + et1.getText().toString() + "isChoisedCity===" + isChoisedCity);
            Utils.toast(this, "请填写并选取城市");
            return false;
        }
        if (!cityName.equals(et1.getText().toString())) {
            Utils.toast(this, "请选取城市");
            return false;
        }
        if (TextUtils.isEmpty(et2.getText()) || !isChoisedSchool) {
            btxText2.setTextColor(getResources().getColor(R.color.redc0));
            line2.setBackgroundColor(getResources().getColor(R.color.redc0));
            Utils.toast(this, "请填写并选取学校");
//            editError = 1;
            return false;
        }
        if (TextUtils.isEmpty(et3.getText())) {
            btxText3.setTextColor(getResources().getColor(R.color.redc0));
            line3.setBackgroundColor(getResources().getColor(R.color.redc0));
            Utils.toast(this, "请填写专业");
//            editError = 2;
            return false;
        }
        if (TextUtils.isEmpty(personnalInfoEdit.getText()) || (personnalInfoEdit.getText().toString().length() < 20)) {
            personInfobtx.setText("字数不少于20字(必填项)");
            personInfobtx.setTextColor(getResources().getColor(R.color.redc0));
//            editError = 3;
            Utils.toast(this, "请填写个人介绍（至少20字）");
            return false;
        }

        return true;
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
                    LogUtils.d("PHOTO_REQUEST_CUT  updateImgNum=====" + updateImgNum);
                    if (updateImgNum == 1) {
                        crop(uri, true);
                    } else {
                        crop(uri, false);
                    }
                }
                break;
            case PHOTO_REQUEST_CAMERA:
                if (hasSdcard()) {
                    tempFile = new File(Environment.getExternalStorageDirectory(), PHOTO_FILE_NAME);
                    LogUtils.d("updateImgNum=====" + updateImgNum);
                    if (updateImgNum == 1) {
                        crop(Uri.fromFile(tempFile), true);
                    } else {
                        crop(Uri.fromFile(tempFile), false);
                    }
                } else {
                    Toast.makeText(this, "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
                }
                break;
            case PHOTO_REQUEST_CUT:

                if ((resultCode == RESULT_OK)) {
                    try {
                        LogUtils.d("PHOTO_REQUEST_CUT  updateImgNum=====" + updateImgNum);
                        if (updateImgNum == 1) {
                            bitmap = decodeUriAsBitmap(imageUri);
                        } else {
                            bitmap = data.getParcelableExtra("data");
                        }
//                    haiBaoImg.setImageBitmap(bitmap);
                        tempFile = new File(Environment.getExternalStorageDirectory(), PHOTO_FILE_NAME);
                        if (FileUtils.saveBitmap(tempFile, bitmap)) {
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
            case VIDEO_REQUEST_CAMERA:  //用照相机录取视频
//                Uri uri = data.getData();
//                String path = Utils.getAbsoluteImagePath(this, uri);
//                tempFile = new File(path);
////                updateImg(tempFile);
//                myWorkImg.setImageBitmap(getVideoThumbnail(path));
//                LogUtils.d("VIDEO_REQUEST_CAMERA----path=" + path + "---uri==" + uri.toString());
//                break;
            case VIDEO_REQUEST_GALLERY:  //从列表选择 视频  -1
                LogUtils.d("resultCode==" + resultCode);
                if (resultCode == RESULT_OK) {

                    if (data != null) {
                        // 得到图片的全路径
                        Uri urii = data.getData();
//                        String pathi = Utils.getAbsoluteImagePath(this, urii);
                        String pathi = Utils.getRealPath(this, urii);
                        LogUtils.d("VIDEO_REQUEST_GALLERY----path=" + pathi + "---uri==" + urii.toString() + "PATH--" + pathi);
                        videoFile = new File(pathi);
                        myWorkImg.setImageBitmap(getVideoThumbnail(pathi));
                        getUpdateVideoInfo(videoFile);
                    }
                } else {
                    Utils.toast(this, "请重新选择");
                }
                break;
//            case CITY_GET:
//                if (resultCode == RESULT_OK) {
//                    cityName = data.getStringExtra("cityName");
//                    cityId = data.getStringExtra("cityId");
//                    Utils.toast(this, "cityName----" + cityName + "cityId---" + cityId);
//                }
//                break;
            default://当不是上面的几种情况时， 说明是分享的
//                if (popThreeShare != null && data != null) {
//                    popThreeShare.setSinaWeibo(requestCode, resultCode, data);
//                }
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
     * 更换选中的新头像
     */
    private void updateImg(File file) {
        if (file.length()>1048576*10) {
            Utils.toast(this,"图片过大，请选取10M以下的图片");
            return;
        }
        showLoadingDialog("正在上传");
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
                        Utils.toast(ApplyForActivity.this, "上传失败");
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
                        switch (updateImgNum) {
                            case 1:
                                haiBaoUrl = picUrl;
                                BitmapTool.getInstance().getAdapterUitl().display(haiBaoImg, haiBaoUrl);
                                break;
                            case 2:
                                idUpUrl = picUrl;
                                BitmapTool.getInstance().getAdapterUitl().display(idUpImg, idUpUrl);
                                break;
                            case 3:
                                idDownUrl = picUrl;
                                BitmapTool.getInstance().getAdapterUitl().display(idDownImg, idDownUrl);
                                break;
                            case 4:
                                peopleAndIdUrl = picUrl;
                                BitmapTool.getInstance().getAdapterUitl().display(peopleAndIdImg, peopleAndIdUrl);
                                break;
                            case 5:
                                studengtIdUrl = picUrl;
                                BitmapTool.getInstance().getAdapterUitl().display(studengtIdImg, studengtIdUrl);
                                break;
                            case 6:
                                lifeUrl1 = picUrl;
                                BitmapTool.getInstance().getAdapterUitl().display(lifeImg1, lifeUrl1);
                                break;
                            case 7:
                                lifeUrl2 = picUrl;
                                BitmapTool.getInstance().getAdapterUitl().display(lifeImg2, lifeUrl2);
                                break;
                            case 8:
//                                myWorkUrl = picUrl;
//                                BitmapTool.getInstance().getAdapterUitl().display(myWorkImg, myWorkUrl);
                                break;
                        }
//                        Utils.toast(ApplyForActivity.this, "上传成功" + picUrl);
                        Utils.toast(ApplyForActivity.this, "上传成功");
                    }
                });
            }
        });
    }

    private void choiseImgUpdate() {

        View eatsView = View.inflate(this, R.layout.complete_choise_img, null);
        eduation_dialog = new Dialog(this, R.style.MmsDialogTheme);
        android.view.View.OnClickListener choiseListener = new android.view.View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.choise_img_phone:
                        PHOTO_FILE_NAME = MediaUtil.getFinalString() + ".png";

                        if (updateImgNum == 8) {
                            cameraVideo();
                        } else {
                            camera();
                        }
                        eduation_dialog.dismiss();
                        break;
                    case R.id.choise_img_pic:
                        if (updateImgNum == 8) {
                            videoGallery();
                        } else {
                            gallery();
                        }
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

    /**
     * 获取城市
     */
    public void getCityList(String key) {
        cityKey = key;
        RequestParams params = UrlTool.getParams("cityName", cityKey);
        SendActtionTool.postNoCheck(Constants.URL_GETCITY, null, UserAction.Action_GET_CITYLIST, ApplyForActivity.this, params);
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
    * 从相册获取
    */
    public void videoGallery() {
        // 激活系统图库，选择一张图片
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("video/*");
        startActivityForResult(intent, VIDEO_REQUEST_GALLERY);
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

    /*
     * 从相机获取
     */
    public void cameraVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        String videopath =Environment.getExternalStorageDirectory()+ "/upload/"
                + MediaUtil.getFinalString() + "a" + ".mp4";
        mediaFile = new File(videopath);
        Uri originalUri = Uri.fromFile(mediaFile);// 这是个实例变量，方便下面获取视频的时候用
        intent.putExtra(MediaStore.EXTRA_OUTPUT, originalUri);
        startActivityForResult(intent, VIDEO_REQUEST_CAMERA);
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
        LogUtils.d("isHaibao------" + isHaibao);
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
        intent.putExtra("outputFormat", "PNG");
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", !isHaibao);// true:不返回uri，false：返回uri
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, PHOTO_REQUEST_CUT);

    }

    private boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    private void updateVideo(String url) {
        LogUtils.d("updateVideo---url=" + url);
        RequestParams params = new RequestParams();
//        params.addHeader("name", "value");
        params.addQueryStringParameter("name", "value");
        params.addBodyParameter("name", videoFile.getName());
        params.addBodyParameter("video_file", videoFile);
        HttpUtils httpUtils = new HttpUtils(60 * 1000);
        showLoadingDialog("正在上传");
        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(final ResponseInfo<String> responseInfo) {
                LogUtils.d("responseInfo----" + responseInfo.result);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissDialog();
                        String json = responseInfo.result;
                        String message = "";
                        int code = -1;
                        try {
                            code = new JSONObject(json).getInt("code");
                            message = new JSONObject(json).getString("message");
                            LogUtils.d("code==" + code + "message===" + message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (code == 0) {
//                            Utils.toast(ApplyForActivity.this, "上传成功");
                            returnVideoInfoToServer();
//                            hasUpdateVideo = true;
                        } else {
                            Utils.toast(ApplyForActivity.this, "上传失败" + message);
                            dismissDialog();
                        }
                    }
                });

            }

            @Override
            public void onFailure(final HttpException error, final String msg) {
                LogUtils.d("responseInfo----" + error.toString() + msg);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Utils.toast(ApplyForActivity.this, "上传失败" + error.toString() + msg);
                        dismissDialog();
                    }
                });
            }
        });
    }

    private void applyForAnchor() {
//        ifApplyAfterReturnService = false;
        //  userId  postering  city  schoolId  schoolName  major  dorm   personlUrl
        //  workCount  personlProfile idImg personnalImg works buildTime
        String idImg = idUpUrl + "," + idDownUrl + "," + peopleAndIdUrl + "," + studengtIdUrl;
        String personalImg = lifeUrl1 + "," + lifeUrl2;
        String works = videoId;
        String time = System.currentTimeMillis() + "";

        String dorm = et4.getText().toString();
        String personlpage = et5.getText().toString();
        String workcount = et6.getText().toString();
        if (TextUtils.isEmpty(dorm)) {
            dorm = " ";
        }
        if (TextUtils.isEmpty(personlpage)) {
            personlpage = " ";
        }
        if (TextUtils.isEmpty(workcount)) {
            workcount = "0";
        }


        RequestParams params = UrlTool.getParams(Constants.USER_ID, UserService.getInatance().getUserBean(ApplyForActivity.this).getId(),//
                "posterImg", haiBaoUrl,//海报
                "city", cityName,//城市
                "cityId", cityId,//城市
                "schoolId", schoolId,//学校id
                "schoolName", schoolName,//学校名字
                "major", et3.getText().toString(),//专业
                "dorm", dorm,//宿舍
                "personlUrl", personlpage,//个人主页地址
                "workCount", workcount,//作品数量
                "personlProfile", personnalInfoEdit.getText().toString(),//个人简介
                "idImg", idImg,//身份证照集合，用逗号隔开
                "personlImg", personalImg,//生活照集合，用逗号隔开
                "works", works,//作品视频id集合，用逗号隔开
                "buildTime", time//创建时间
        );

        LogUtils.d("param-----" + "posterImg---" + haiBaoUrl + "city----" + et1.getText().toString() + "schoolId----" + "123" + "schoolName----"
                + et2.getText().toString() + "major----" + et3.getText().toString() + "dorm----" + et4.getText().toString() + "personlUrl----" + et5.getText().toString()
                + "workCount----" + et6.getText().toString() + "personlProfile----" + personnalInfoEdit.getText().toString()
                + "idImg----" + idImg + "personnalImg----" + personalImg + "works----" + works + "buildTime----" + time);

        SendActtionTool.get(Constants.UserParams.URL_APPLYFOR_ANCHOR, null, UserAction.ACTION_APPLYFOR_ANCHOR, ApplyForActivity.this, params);
    }

    private void getUpdateVideoInfo(File file) {
        LogUtils.d("VIDEO_NAME----" + file.getName());
        LogUtils.d("VIDEO_NAMEgetAbsolutePath----" + file.getAbsolutePath());
        RequestParams params = UrlTool.getParams(Constants.VIDEO_NAME, file.getName());
        SendActtionTool.postNoCheck(Constants.URL_INFO_UPDATEVIDEO, null, UserAction.ACTION_GETINFO_UPDATEVIDEO, ApplyForActivity.this, params);
//        SendActtionTool.get(Contansts.URL_INFO_UPDATEVIDEO, null, UserAction.ACTION_GETINFO_UPDATEVIDEO, ApplyForActivity.this, params);
    }

    public void returnVideoInfoToServer() {

        if (videoUpdateInfo == null || videoFile == null) {
            Utils.toast(this, "上传失败请重新上传");
            return;
        }
        RequestParams params = UrlTool.getParams(Constants.VIDEO_ID, videoUpdateInfo.getData().getVideo_id(),
                "videoUnique", videoUpdateInfo.getData().getVideo_unique(),
                "type", "2",
                Constants.VIDEO_NAME, videoFile.getName());
        LogUtils.d("returnVideoInfoToServer----VIDEO_ID=" + videoUpdateInfo.getData().getVideo_id()
                + "--videoUnique=" + videoUpdateInfo.getData().getVideo_unique()
                + "----videoname=" + videoFile.getName());
        SendActtionTool.get(Constants.UserParams.URL_RETURNVIDEOINFO_SERVER, null, UserAction.ACTION_RETURNVIDEOINFO_SERVER, ApplyForActivity.this, params);

    }

    int num = 0;

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        super.onSuccess(service, action, value);
        LogUtils.d("action--" + action + "onSuccessvalue----" + value.toString());
        switch ((UserAction) action) {
            case ACTION_APPLYFOR_ANCHOR:
                Utils.toast(ApplyForActivity.this, "提交成功");
                btnUp.setText("审核中");
                btnUp.setEnabled(false);
                Utils.sendBroadcastToMainactivityToUpdateUserinfo(this);
                break;
            case ACTION_RETURNVIDEOINFO_SERVER:
//            {"status":1,"data":{"videoId":1004},"msg":"success"}
                try {
                    videoId = new JSONObject(value.toString()).getJSONObject("data").getString("videoId");
                    hasUpdateVideo = true;
                    num = 0;
                    Utils.toast(ApplyForActivity.this, "上传成功");
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (num < 3) {
                        num++;
                        returnVideoInfoToServer();
                    } else {
                        Utils.toast(ApplyForActivity.this, "数据异常请重新上传 ");
                    }
//                    Utils.toast(ApplyForActivity.this, "数据异常请重新");
                }

//                hasUpdateVideo
//                hasReturnVideoInfoToServer = true;
//                if (ifApplyAfterReturnService) {
//                    applyForAnchor();
//                }

                break;
            case ACTION_GETINFO_UPDATEVIDEO:

//                JSONObject object = (JSONObject) value;
                String upDateUrl = null;
                try {
                    videoUpdateInfo = JSON.parseObject(
                            value.toString(),
                            new TypeReference<VideoUpdateInfo>() {
                            });
                    upDateUrl = videoUpdateInfo.getData().getUpload_url();
//                    upDateUrl = object.getJSONObject("data").getString("upload_url");
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                VideoUpdateInfo videoUpdateInfo= (VideoUpdateInfo) JSON.parse(value.toString());
//                String upDateUrl=videoUpdateInfo.getData().getUpload_url();
                LogUtils.d("upDateUrl====" + upDateUrl);
                if (videoFile != null && !TextUtils.isEmpty(upDateUrl)) {
                    updateVideo(upDateUrl);
                }
                break;
            case Action_GET_SCHOOLLIST:
                try {
                    schools.clear();
                    schools.addAll(JSON.parseArray(((JSONObject) value).getString("jsonList"), School.class));
                    LogUtils.d("Action_GET_SCHOOLLIST  schools.size()===" + schools.size());
                    if (schools == null || schools.size() == 0) {
                        schoolList.setVisibility(View.GONE);
                        cityListView.setVisibility(View.GONE);
                        break;
                    }
                    initSchoolList();
                    if (shouldShowSchoolList) {
                        schoolList.setVisibility(View.VISIBLE);
                        cityListView.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    schoolList.setVisibility(View.GONE);
                    cityListView.setVisibility(View.GONE);
                }
                break;
            case Action_GET_CITYLIST:
                try {

                    citys.clear();
                    citys.addAll(JSON.parseArray(((JSONObject) value).getString("jsonList"), CityBean.class));
                    LogUtils.d("Action_GET_SCHOOLLIST  citys.size()===" + citys.size());
                    if (citys == null || citys.size() == 0) {
                        cityListView.setVisibility(View.GONE);
                        break;
                    }
                    initCityList();
                    if (shouldShowCityList) {
                        schoolList.setVisibility(View.GONE);
                        cityListView.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    cityListView.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    public void onFaile(ServiceAction service, Object action, Object value) {
        LogUtils.d("action--" + action + "onFaile----" + value.toString());
        super.onFaile(service, action, value);
        switch ((UserAction) action) {
            case ACTION_APPLYFOR_ANCHOR:

                break;
            case ACTION_RETURNVIDEOINFO_SERVER:
                if (num < 3) {
                    num++;
                    returnVideoInfoToServer();
                } else {
                    Utils.toast(ApplyForActivity.this, "数据异常请重新上传 ");
                }
                break;
            case ACTION_GETINFO_UPDATEVIDEO:
                break;
        }
        Utils.toast(ApplyForActivity.this, value.toString());
    }

    @Override
    public void onException(ServiceAction service, Object action, Object value) {
        LogUtils.d("action--" + action + "onException----" + value.toString());
        super.onException(service, action, value);
        switch ((UserAction) action) {
            case ACTION_APPLYFOR_ANCHOR:
                break;
            case ACTION_RETURNVIDEOINFO_SERVER:
                if (num < 3) {
                    num++;
                    returnVideoInfoToServer();
                } else {
                    Utils.toast(ApplyForActivity.this, "数据异常请重新上传 ");
                }
                break;
        }
        Utils.toast(ApplyForActivity.this, value.toString());
    }

    // 从视频获取bitmap
    public Bitmap getVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

}
