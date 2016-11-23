package com.modernsky.istv.tool;

import com.lidroid.xutils.db.table.KeyValue;
import com.lidroid.xutils.http.RequestParams;
import com.modernsky.istv.utils.LogUtils;

import org.apache.http.NameValuePair;

import java.util.List;


/**
 * 常用工具包装
 */
public class UrlTool {
    /**
     * 2015年2月10日
     *
     * @param values key value 键值对 传入
     * @return 服务器请求的参数
     */
    public static RequestParams getParams(String... values) {
        RequestParams params = new RequestParams();
        String string = "";
        for (int i = 0; i < values.length; i += 2) {
            params.addQueryStringParameter(values[i], values[i + 1]);
            string = string + values[i] + "," + values[i + 1] + ",";
        }
        LogUtils.t("RequestParams," + values[0], string);
        return params;
    }

    public static RequestParams getPostParams(String... values) {
        RequestParams params = new RequestParams();
        String string = "";
        for (int i = 0; i < values.length; i += 2) {
            params.addBodyParameter(values[i], values[i + 1]);
            string = string + values[i] + "," + values[i + 1] + ",";
        }
        LogUtils.t("PostRequestParams," + values[0], string);
        return params;
    }

    public static String getParamsString(RequestParams params) {
        String string = "";
        if (params != null) {
            List<NameValuePair> queryStringParams = params.getQueryStringParams();
            List<NameValuePair> bodyParams = params.getbodyParams();
            if (queryStringParams != null) {
                for (int i = 0; i < queryStringParams.size(); i++) {
                    string = string + queryStringParams.get(i).getName() + "=" + queryStringParams.get(i).getValue() + "&";
                }
            }
            if (bodyParams != null) {
                for (int i = 0; i < bodyParams.size(); i++) {
                    string = string + bodyParams.get(i).getName() + "=" + bodyParams.get(i).getValue() + "&";
                }
            }
        }
        return string;
    }

    //

    public static RequestParams getParams(String url, RequestParams requestParams) {
        List<KeyValue> list = null;
//        requestParams.getQueryStringParams();
        RequestParams params = new RequestParams(url);
//        for (int i = 0; i < list.size(); i++) {
////            params.addQueryStringParameter(list.get(i).key, list.get(i).getValueStr());
//        }
        return params;
    }
}
