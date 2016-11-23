package com.modernsky.istv.acitivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.http.RequestParams;
import com.modernsky.istv.BaseActivity;
import com.modernsky.istv.R;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.adapter.CommonAdapter;
import com.modernsky.istv.adapter.ViewHolder;
import com.modernsky.istv.bean.ArtistVideo;
import com.modernsky.istv.bean.LatestVideo;
import com.modernsky.istv.bean.ResultBean;
import com.modernsky.istv.tool.BitmapTool;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.tool.UrlTool;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.Utils;
import com.modernsky.istv.view.DrageLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rendy
 *         <p/>
 *         音乐节 单个艺人界面
 */
public class ArtistDetailActiivty extends BaseActivity {
    private String objectId;

    private ListView listview;
    private CommonAdapter<LatestVideo> commonAdapter;
    private List<LatestVideo> datas;
    private PullToRefreshListView pullToRefreshListView;
    private View headView;
    private String albumId;
    private ImageView imageView;

    private TextView textView;

    private TextView textView2;

    private TextView btn_allVideo;

    private int targetId;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                onBackPressed();
                break;
            case R.id.img_head:

                break;
            case R.id.btn_allVideo:
                if (targetId != 0)
                    Utils.playAtristAllVideo(this, String.valueOf(targetId), 0);
                else
                    LogUtils.e("targetId == 0");
                break;

            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDrageLayout.initDrageLayoutPosition();
    }

    @Override
    public void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_yinyuejieheji);
        objectId = getIntent().getStringExtra(Constants.OBJECT_ID);
        findViewById(R.id.img_back).setOnClickListener(this);
        TextView titleView = (TextView) findViewById(R.id.tv_title);
        String title = getIntent().getStringExtra(Constants.TITLE);
        albumId = getIntent().getStringExtra(Constants.ALBUM_ID);
        titleView.setText(title);
        initDrager();
    }

    private DrageLayout mDrageLayout;
    private RelativeLayout mDrageView;
    RelativeLayout mDragButtomView;

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
        datas = new ArrayList<LatestVideo>();
        commonAdapter = new CommonAdapter<LatestVideo>(
                ArtistDetailActiivty.this, datas, R.layout.item_ershoumiegui) {

            @Override
            public void convert(ViewHolder helper,
                                final LatestVideo item) {

                helper.setImageByUrl(R.id.imageView1, item.getStandardPic());
                helper.setVisibility(R.id.item_rotate_textview, 8);
                // helper.setText(R.id.tv_video_name, item.getName());
                // VideoPlayInfo videoPlayInfo = item.getVideoPlayInfo();
                // if (videoPlayInfo != null)
                // helper.setText(R.id.item_rotate_textview,
                // String.valueOf(videoPlayInfo.getVideoId()));
                helper.setText(R.id.textView2, item.getName());
                helper.getView(R.id.imageView1).setOnClickListener(
                        new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                LogUtils.d("点击爱大基地埃及第几");
                                Utils.playPersonDetailVideo(
                                        ArtistDetailActiivty.this,
                                        item.getVideoId() + "", objectId,
                                        item.getName(), albumId);
                            }
                        });
            }

        };
        headView = LayoutInflater.from(this).inflate(R.layout.head_imageview,
                null);
        initHeadView(headView, null);
        listview.addHeaderView(headView);
        listview.setAdapter(commonAdapter);
        pullToRefreshListView.setRefreshing();
        // getUrlData();
    }

    /**
     * @param headView2
     * @param artistVideo
     */
    private void initHeadView(View headView2, ArtistVideo artistVideo) {
        if (imageView == null) {
            imageView = (ImageView) headView2.findViewById(R.id.img_head);
            imageView.setOnClickListener(this);
        }
        if (btn_allVideo == null) {
            btn_allVideo = (TextView) headView2.findViewById(R.id.btn_allVideo);
            btn_allVideo.setOnClickListener(this);
        }
        if (textView == null)
            textView = (TextView) headView2.findViewById(R.id.tv_name);
        if (textView2 == null)
            textView2 = (TextView) headView2.findViewById(R.id.tv_time);
        if (artistVideo != null) {

            BitmapTool.getInstance().getAdapterUitl()
                    .display(imageView, artistVideo.getStandardPic());
            textView.setText(artistVideo.getName());
            textView2.setText(getIntent().getStringExtra(Constants.ALBUM_NAME));
        }

    }

    /**
     * 获取网络数据
     */
    private void getUrlData() {
        RequestParams params = UrlTool.getParams(Constants.OBJECT_ID, objectId,
                Constants.ALBUM_ID, albumId, Constants.FILTER,
                Constants.FILTER_OBJECTID);
        LogUtils.d("getUrlData ==albumId==" + albumId + "objectId==" + objectId);
        SendActtionTool.get(Constants.URL_GET_STARDETAIL,
                ServiceAction.Action_Comment, null, this, params);
    }

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        switch (service) {
            // 当前页面的数据
            case Action_Comment:
                ResultBean<ArtistVideo> tempBean = JSON.parseObject(
                        value.toString(),
                        new TypeReference<ResultBean<ArtistVideo>>() {
                        });
                if (tempBean != null) {
                    ArtistVideo artistVideo = tempBean.data;
                    initHeadView(headView, artistVideo);
                    targetId = artistVideo.getTargetId();
                    List<LatestVideo> data = artistVideo.getData();
                    if (data != null && data.size() > 0) {
                        datas.clear();
                        datas.addAll(data);
                        commonAdapter.notifyDataSetChanged();
                    }
                }

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
}
