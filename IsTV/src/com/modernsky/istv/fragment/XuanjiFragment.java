/**
 *
 */
package com.modernsky.istv.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lidroid.xutils.http.RequestParams;
import com.modernsky.istv.R;
import com.modernsky.istv.acitivity.PlayActivity;
import com.modernsky.istv.action.CommentAction;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.adapter.CommonAdapter;
import com.modernsky.istv.adapter.ViewHolder;
import com.modernsky.istv.bean.ArtistVideo;
import com.modernsky.istv.bean.LatestVideo;
import com.modernsky.istv.bean.ResultBean;
import com.modernsky.istv.bean.ResultList;
import com.modernsky.istv.bean.VideoPlayInfo;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.tool.TimeTool;
import com.modernsky.istv.tool.UrlTool;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author： fengqingyun2008
 * @Email： fengqingyun2008@gmail.com
 * @version：1.0
 * @创建时间：2015-5-22 下午5:28:43
 * @类说明：
 */
@SuppressLint("ValidFragment")
public class XuanjiFragment extends BaseFragment implements OnItemClickListener {
    private ListView listView;
    private CommonAdapter<LatestVideo> adapter;
    private List<LatestVideo> latestVideos;
    //    LookForwardActivity playActivity;
    PlayActivity playActivity;
    private int mIndex;

//    public XuanjiFragment(String albumId, String objectId) {
//        this.objectId = objectId;
//        this.albumId = albumId;
//    }

    public XuanjiFragment() {
        super();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public View setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        playActivity = (PlayActivity) getActivity();
        return inflater.inflate(R.layout.fragment_xuanji, container, false);
    }

    @Override
    public void initView(View rootView) {
        latestVideos = new ArrayList<LatestVideo>();
        listView = (ListView) rootView.findViewById(R.id.listview);
        listView.setOnItemClickListener(this);
        adapter = new CommonAdapter<LatestVideo>(getActivity(),

                latestVideos, R.layout.item_listview_topic) {

            @Override
            public void convert(ViewHolder helper,
                                LatestVideo item) {
                String name = item.getName();
                helper.setImageByUrl(R.id.item_listview_topic_img,
                        item.getStandardPic());

                if (item.getShowTime() == null) {
                    helper.setVisibility(R.id.textView3, 8);
                } else {
                    helper.setVisibility(R.id.textView3, 0);
                    long parseLong = 0;
                    try {
                        parseLong = Long.parseLong(item.getShowTime());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    helper.setText(R.id.textView3, TimeTool.getDayTime(parseLong));
                }
                helper.setText(R.id.xiuchang_jingyanzhiTet, item.getStarringNames());
                helper.setText(R.id.xiuchang_jingyanzhiTet, name);
            }
        };
        listView.setAdapter(adapter);
    }

    /**
     * 获取艺人视频列表
     */
    public void getAlbumVideo(String albumId) {
        RequestParams params = UrlTool.getPostParams(Constants.ALBUM_ID, albumId,
                Constants.FILTER, Constants.FILTER_ALBUM_VIDEO);
        SendActtionTool.post(Constants.URL_ALL_VIDEO_LIST,
                ServiceAction.Action_Comment,
                CommentAction.Action_getAlbumVideo, this, params);
    }

    // 获取艺人的列表和artistDetail的列表一样的
    public void getArtistDetailData(String objectId, String albumId) {
        RequestParams params = UrlTool.getParams(Constants.OBJECT_ID, objectId,
                Constants.ALBUM_ID, albumId, Constants.FILTER,
                Constants.FILTER_OBJECTID);
        SendActtionTool.get(Constants.URL_GET_STARDETAIL,
                ServiceAction.Action_Comment,
                CommentAction.Action_ArtistPersonList, this, params);
    }

    /**
     * 获取艺人全部视频列表 , Constants.FILTER, Constants.FILTER_ARTIST_ALL_VIDEO
     *
     * @param collectId
     */
    public void getArtistAllVideo(String collectId, int index) {
        mIndex = index;
        RequestParams params = UrlTool.getPostParams(Constants.COLLECT_ID,
                collectId, Constants.SIZE, "100");
        LogUtils.d("collectId==" + collectId);
        SendActtionTool.post(Constants.URL_GET_ARTIST_ALL_VIDEO,
                ServiceAction.Action_Comment,
                CommentAction.Action_getArtistAllVideo, this, params);
    }

    @Override
    public void onException(ServiceAction service, Object action, Object value) {
        LogUtils.t("onExceptiononFaileaction:===" + action.toString(),
                value.toString());
        super.onException(service, action, value);
    }

    @Override
    public void onFaile(ServiceAction service, Object action, Object value) {
        LogUtils.t("onFaileaction:===" + action.toString(), value.toString());
        super.onFaile(service, action, value);
    }

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        String jsonString = value.toString();
        if (getActivity() == null)
            return;
        LogUtils.t("onSuccessaction:===" + action.toString(), jsonString);
        switch ((CommentAction) action) {
            case Action_getAlbumVideo:
                try {
                    ResultList<LatestVideo> resultBean = JSON
                            .parseObject(
                                    jsonString,
                                    new TypeReference<ResultList<LatestVideo>>() {
                                    });

                    List<LatestVideo> albumLastVideo = resultBean.data;
                    if (albumLastVideo != null) {
                        latestVideos.clear();
                        latestVideos.addAll(albumLastVideo);
                        adapter.notifyDataSetChanged();
                        if (playActivity.getStringType().equals(
                                Constants.ALBUM_NAME)) {
                            playActivity.playVideo(String.valueOf(latestVideos.get(
                                    mIndex).getVideoId()));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case Action_getArtistAllVideo:
                try {
                    ResultList<LatestVideo> resultBean = JSON.parseObject(jsonString,
                            new TypeReference<ResultList<LatestVideo>>() {
                            });
                    List<LatestVideo> albumLastVideo = resultBean.data;
                    if (albumLastVideo != null) {
                        latestVideos.clear();
                        latestVideos.addAll(albumLastVideo);
                        adapter.notifyDataSetChanged();
                        if (mIndex >= albumLastVideo.size()) {
                            mIndex = 0;
                        }
                        LatestVideo currentVideo = latestVideos.get(
                                mIndex);
//                        String videoId = String.valueOf(currentVideo.getVideoId());
//                        playActivity.playVideo(videoId);
                        playActivity.playVideoByUrl(currentVideo.getVideoId() + "", currentVideo.getVideoPlayInfo().getForeignUnique(), currentVideo.getName());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.e("Action_getArtistAllVideo解析异常" + e.toString());
                }
                break;
            case Action_ArtistPersonList:
                ResultBean<ArtistVideo> tempBean = JSON.parseObject(value.toString(),
                        new TypeReference<ResultBean<ArtistVideo>>() {
                        });
                if (tempBean != null) {
                    ArtistVideo artistVideo = tempBean.data;
                    List<LatestVideo> data = artistVideo.getData();
                    if (data != null && data.size() > 0) {
                        latestVideos.clear();
                        latestVideos.addAll(data);
                        adapter.notifyDataSetChanged();
                        // TODO 这段以后要改
                        int videoId = latestVideos.get(0).getVideoId();
                        String videoURL = latestVideos.get(0).getVideoPlayInfo().getVideoUrl();
                        String videoName = latestVideos.get(0).getName();
                        if ("3".equals(latestVideos.get(0).getIsVRsource().trim()) && latestVideos.get(0).getVideoType() == 1) {
                            VideoPlayInfo videoPlayInfo1 = latestVideos.get(0).getVideoPlayInfo();
                            Utils.playVRNetworkStream(this.getActivity(), videoPlayInfo1.getVideoUrl(), latestVideos.get(0).getName());
                            this.getActivity().finish();
                            return;
                        } else {
                            playActivity.playVideoByUrl(videoId + "", videoURL, videoName);
                            playActivity.mTitle = videoName;
                        }


                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        LatestVideo latestVideo = latestVideos.get(position);
        if (latestVideo != null) {
            if ("3".equals(latestVideo.getIsVRsource().trim()) && latestVideo.getVideoType() == 1) {
                VideoPlayInfo videoPlayInfo1 = latestVideo.getVideoPlayInfo();
                Utils.playVRNetworkStream(this.getActivity(), videoPlayInfo1.getVideoUrl(), latestVideo.getName());
                this.getActivity().finish();
                return;
            } else {
                playActivity.playVideoByUrl(latestVideo.getVideoId() + "",
                        latestVideo.getVideoPlayInfo().getVideoUrl(), latestVideo.getName());
            }
        }

    }

}
