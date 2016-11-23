package com.modernsky.istv.acitivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.http.RequestParams;
import com.modernsky.istv.BaseActivity;
import com.modernsky.istv.R;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.adapter.CommonAdapter;
import com.modernsky.istv.adapter.ViewHolder;
import com.modernsky.istv.bean.Data;
import com.modernsky.istv.bean.FocusPictureModel;
import com.modernsky.istv.service.YinyuejieService;
import com.modernsky.istv.tool.BitmapTool;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.tool.TimeTool;
import com.modernsky.istv.tool.UrlTool;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.view.DrageLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rendy 音乐节合集页
 */
public class YinyueJieHejiActivtity extends BaseActivity {
    private String albumId;
    private ListView listview;
    private CommonAdapter<FocusPictureModel> commonAdapter;
    private List<FocusPictureModel> datas;
    private PullToRefreshListView pullToRefreshListView;
    private ImageView img_head;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                onBackPressed();
                break;

            default:
                break;
        }

    }

    @Override
    public void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_yinyuejieheji);
        albumId = getIntent().getStringExtra(Constants.ALBUM_ID);
        findViewById(R.id.img_back).setOnClickListener(this);
        TextView titleView = (TextView) findViewById(R.id.tv_title);
        String title = getIntent().getStringExtra(Constants.TITLE);
        titleView.setText(title);
        initDrager();
    }

    private DrageLayout mDrageLayout;
    private RelativeLayout mDrageView;
    RelativeLayout mDragButtomView;

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mDrageLayout.initDrageLayoutPosition();
    }

    private void initDrager() {
        mDrageLayout = (DrageLayout) findViewById(R.id.drageLayout);
        mDrageView = (RelativeLayout) findViewById(R.id.draglayoutView);
        mDragButtomView = (RelativeLayout) findViewById(R.id.layoutButtom_drag);
        mDrageLayout.setView(mDrageView, mDragButtomView);

    }

    @Override
    public void findViewById() {
        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.listview);
        pullToRefreshListView
                .setOnRefreshListener(new OnRefreshListener<ListView>() {

                    @Override
                    public void onRefresh(
                            PullToRefreshBase<ListView> refreshView) {
                        getUrlData();
                    }
                });
        listview = pullToRefreshListView.getRefreshableView();
        datas = new ArrayList<FocusPictureModel>();
        commonAdapter = new CommonAdapter<FocusPictureModel>(
                YinyueJieHejiActivtity.this, datas, R.layout.item_yinyuejie) {

            @Override
            public void convert(ViewHolder helper, final FocusPictureModel item) {

                helper.setImageByUrl(R.id.imageView1, item.getPic());
                helper.setText(R.id.tv_video_name, item.getName());
                helper.setVisibility(R.id.tv_video_name, 0);
                helper.setText(R.id.textView2,
                        TimeTool.getFormaTime(item.getShowtime()));
                // helper.setText(R.id.item_rotate_textview,
                // item.getCategory());
                // helper.setText(R.id.tv_video_name_detil, "回顾");
                helper.getView(R.id.imageView1).setOnClickListener(
                        new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(
                                        YinyueJieHejiActivtity.this,
                                        ArtistDetailActiivty.class);
                                intent.putExtra(Constants.OBJECT_ID,
                                        item.getObjectId());
                                intent.putExtra(Constants.ALBUM_ID,
                                        item.getAlbumId());
                                intent.putExtra(Constants.TITLE, item.getName());
                                intent.putExtra(Constants.ALBUM_NAME, focusName);
                                startActivity(intent);
                            }
                        });
            }

        };
        View headView = LayoutInflater.from(this).inflate(R.layout.head_image,
                null);
        img_head = (ImageView) headView.findViewById(R.id.img_head);
        listview.addHeaderView(headView);
        listview.setAdapter(commonAdapter);
        pullToRefreshListView.setRefreshing();
        // getUrlData();
    }

    /**
     * 获取网络数据
     */
    private void getUrlData() {
        RequestParams params = UrlTool.getParams(Constants.ALBUM_ID, albumId);
        SendActtionTool.get(Constants.URL_GET_SUBJECT,
                ServiceAction.Action_Comment, null, this, params);
    }

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        JSONObject obj = (JSONObject) value;
        switch (service) {
            // 当前页面的数据
            case Action_Comment:
                updatePageDate(obj);
                break;
            default:
                break;
        }
    }

    @Override
    public void onFinish(ServiceAction service, Object action) {
        switch (service) {
            // 当前页面的数据
            case Action_Comment:
                pullToRefreshListView.onRefreshComplete();
                break;

            default:
                break;
        }
    }

    /**
     * @param object 界面数据 json
     *               <p/>
     *               解析当前界面的 所有数据
     */
    private void updatePageDate(JSONObject object) {
        try {
            JSONArray array = object.getJSONArray(Constants.DATA);
            List<Data> datas = JSON.parseArray(array.toString(), Data.class);
            Data data = null;
            for (int i = 0; i < datas.size(); i++) {
                int key = -1;
                data = datas.get(i);
                key = data.getSubscribe();
                switch (key) {
                    // 广告寻展
                    case 0:
                        YinyuejieService.getInstance().setAdvertisementsDatas(data);
                        break;
                    // 独家 热播
                    case 2:
                        YinyuejieService.getInstance().setDujiaData(data);
                        break;
                    default:
                        break;
                }
            }
            updateData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String focusName;

    private void updateData() {
        // 初始化 巡展 Øß
        initHeader();
        // 独家
        initDujia();
    }

    private void initHeader() {
        Data advertisementsDatas = YinyuejieService.getInstance().getAdvertisementsDatas();
        if (advertisementsDatas == null)
            return;
        FocusPictureModel focusPictureModel = advertisementsDatas.getData().get(0);
        if (focusPictureModel != null)
            BitmapTool.getInstance().getAdapterUitl().display(img_head, focusPictureModel.getPic());
    }

    /**
     *
     */
    private void initDujia() {

        Data data = YinyuejieService.getInstance().getDujiaData();
        if (data == null) {
            return;
        }
        focusName = data.getFocusName();
        List<FocusPictureModel> tempPictureModels = data.getData();
        if (tempPictureModels != null && tempPictureModels.size() > 0) {
            datas.clear();
            datas.addAll(tempPictureModels);
            commonAdapter.notifyDataSetChanged();
        }
    }


}
