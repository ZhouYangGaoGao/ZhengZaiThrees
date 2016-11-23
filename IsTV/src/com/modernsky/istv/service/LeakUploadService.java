//package com.modernsky.istv.service;
//
//import com.modernsky.istv.utils.LogUtils;
//import com.squareup.leakcanary.AnalysisResult;
//import com.squareup.leakcanary.DisplayLeakService;
//import com.squareup.leakcanary.HeapDump;
//
///**
// * Created by zhengzai_zxm on 16/3/29.
// */
//public class LeakUploadService extends DisplayLeakService {
//    @Override
//    protected void afterDefaultHandling(HeapDump heapDump, AnalysisResult result, String leakInfo) {
//        if (!result.leakFound || result.excludedLeak) {
//            return;
//        }
////        myServer.uploadLeakBlocking(heapDump.heapDumpFile, leakInfo);
//        // TODO: 16/3/29 检测到内存泄露可在此上传服务器
//        LogUtils.e("检测到内存泄露: " + leakInfo);
//
//    }
//
//}
