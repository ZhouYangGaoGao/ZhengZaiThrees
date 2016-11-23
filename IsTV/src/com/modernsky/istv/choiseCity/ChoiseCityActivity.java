package com.modernsky.istv.choiseCity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.modernsky.istv.BaseActivity;
import com.modernsky.istv.R;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.action.UserAction;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.LogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * @author http://blog.csdn.net/finddreams
 * @Description:联系人显示界面
 */
public class ChoiseCityActivity extends BaseActivity {

    private View mBaseView;
    private ListView sortListView;
    private SideBar sideBar;
    private TextView dialog;
    private SortAdapter adapter;
    private ClearEditText mClearEditText;
    private Map<String, String> callRecords;

    private CharacterParser characterParser;
    private List<SortModel> SourceDateList;

    private PinyinComparator pinyinComparator;
//    private String[]
//    = {"北京", "石家庄 ", "沧州", "承德 ", "秦皇岛", "唐山", "保定", "廊坊 ", "邢台", " 衡水",
//            "张家口", "邯郸 ", "任丘", " 河间", "泊头", "武安", "沙河", "南宫 ", "深州 ", "冀州",
//            "黄骅", "高碑店", "安国", " 涿州", "定州", "三河", "霸州 ", "迁安 ", "遵化",
//            "鹿泉", "新乐", "晋州 ", "藁城 ", "辛集"};


    @Override
    public void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_choisecity);
        SourceDateList = new ArrayList<SortModel>();
        initView();
        initData();
    }

    @Override
    public void findViewById() {
        getCitys();
    }

    private void getCitys() {
        SendActtionTool.get(Constants.URL_GET_CITYS, null, UserAction.Action_GET_CITYLIST, this);
        showLoadingDialog();
    }

    private void initView() {
        sideBar = (SideBar) this.findViewById(R.id.sidrbar);
        dialog = (TextView) this.findViewById(R.id.dialog);
        sortListView = (ListView) this.findViewById(R.id.sortlist);

    }

    Intent mresultData;

    private void initData() {
        // 实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();

        pinyinComparator = new PinyinComparator();

        sideBar.setTextView(dialog);

        // 设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @SuppressLint("NewApi")
            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    sortListView.setSelection(position);
                }
            }
        });

        sortListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // 这里要利用adapter.getItem(position)来获取当前position所对应的对象
                // Toast.makeText(getApplication(),
                // ((SortModel)adapter.getItem(position)).getName(),
                // Toast.LENGTH_SHORT).show();
//				String number = callRecords.get(((SortModel) adapter
//						.getItem(position)).getName());
                mresultData = new Intent();
                mresultData.putExtra("cityName", ((SortModel) adapter
                        .getItem(position)).getCityName());
                mresultData.putExtra("cityId", ((SortModel) adapter
                        .getItem(position)).getCityId());
                LogUtils.d("cityId=====" + ((SortModel) adapter
                        .getItem(position)).getCityId());
                ChoiseCityActivity.this.setResult(RESULT_OK, mresultData);
                ChoiseCityActivity.this.finish();

            }
        });

//		new ConstactAsyncTask().execute(0);
//		SourceDateList = filledData(citys);
        initSourceData();
    }

    private void initSourceData() {
//        SourceDateList = filledData(citys);

        // 根据a-z进行排序源数据
        Collections.sort(SourceDateList, pinyinComparator);
        adapter = new SortAdapter(ChoiseCityActivity.this, SourceDateList);
        sortListView.setAdapter(adapter);

        mClearEditText = (ClearEditText) ChoiseCityActivity.this
                .findViewById(R.id.filter_edit);
        mClearEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View arg0, boolean arg1) {
                mClearEditText.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);

            }
        });
        // 根据输入框输入值的改变来过滤搜索
        mClearEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                // 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    @Override
    public void onClick(View v) {

    }

    private class ConstactAsyncTask extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected Integer doInBackground(Integer... arg0) {
            int result = -1;
//			callRecords = ConstactUtil.getAllCallRecords(ChoiseCityActivity.this);
            result = 1;
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (result == 1) {
                List<String> constact = new ArrayList<String>();
                for (Iterator<String> keys = callRecords.keySet().iterator(); keys
                        .hasNext(); ) {
                    String key = keys.next();
                    constact.add(key);
                }
                String[] names = new String[]{};
                names = constact.toArray(names);
                SourceDateList = filledData(names);

                // 根据a-z进行排序源数据
                Collections.sort(SourceDateList, pinyinComparator);
                adapter = new SortAdapter(ChoiseCityActivity.this, SourceDateList);
                sortListView.setAdapter(adapter);

                mClearEditText = (ClearEditText) ChoiseCityActivity.this
                        .findViewById(R.id.filter_edit);
                mClearEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

                    @Override
                    public void onFocusChange(View arg0, boolean arg1) {
                        mClearEditText.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);

                    }
                });
                // 根据输入框输入值的改变来过滤搜索
                mClearEditText.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence s, int start,
                                              int before, int count) {
                        // 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                        filterData(s.toString());
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

    }


    private void updateList() {

        for (SortModel model : SourceDateList) {
            // 汉字转换成拼音
            String pinyin = characterParser.getSelling(model.getCityName());
            String sortString = pinyin.substring(0, 1).toUpperCase();
//            LogUtils.d("date[i]===" + date[i] + "i==" + i + "pinyin==" + pinyin + "sortString==" + sortString);
            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                model.setSortLetters(sortString.toUpperCase());
            } else {
                model.setSortLetters("#");
            }
        }
        // 根据a-z进行排序源数据
        Collections.sort(SourceDateList, pinyinComparator);
        adapter.updateListView(SourceDateList);

    }


    /**
     * 为ListView填充数据
     *
     * @param date
     * @return
     */
    private List<SortModel> filledData(String[] date) {
        List<SortModel> mSortList = new ArrayList<SortModel>();

        for (int i = 0; i < date.length; i++) {
            SortModel sortModel = new SortModel();
            sortModel.setCityName(date[i]);
            // 汉字转换成拼音
            String pinyin = characterParser.getSelling(date[i]);
            String sortString = pinyin.substring(0, 1).toUpperCase();
//            LogUtils.d("date[i]===" + date[i] + "i==" + i + "pinyin==" + pinyin + "sortString==" + sortString);
            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;

    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<SortModel> filterDateList = new ArrayList<SortModel>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = SourceDateList;
        } else {
            filterDateList.clear();
            for (SortModel sortModel : SourceDateList) {
                String name = sortModel.getCityName();
//                'name.indexOf(filterStr.toString()) != -1' can be replaced with 'name.contains(filterStr.toString())'
                if (name.contains(filterStr.toString())
                        || characterParser.getSelling(name).startsWith(
                        filterStr.toString())) {
                    filterDateList.add(sortModel);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        adapter.updateListView(filterDateList);
    }

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        super.onSuccess(service, action, value);
        switch ((UserAction) action) {
            case Action_GET_CITYLIST:
                try {
                    JSONObject obj = (JSONObject) value;
                    JSONArray jsonArray = obj.getJSONArray(Constants.DATA);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject o = (JSONObject) jsonArray.get(i);
                        if (o.getInt(Constants.TYPE) == 3) {
                            List<SortModel> tempList = JSON.parseArray(o.getString(Constants.DATA), SortModel.class);
                            if (tempList != null && tempList.size() > 0) {
                                SourceDateList.clear();
                                SourceDateList.addAll(tempList);
                                updateList();
                            }
//                            PreferencesUtils.savePreferences(this, PreferencesUtils.TYPE_Hot_City, o.getString(Constants.DATA));
                        }

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;


        }
    }

    @Override
    public void onFinish(ServiceAction service, Object action) {
        super.onFinish(service, action);
        dismissDialog();
    }
}
