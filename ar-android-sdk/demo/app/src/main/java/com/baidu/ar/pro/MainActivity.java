/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.ar.pro;

import java.util.ArrayList;
import java.util.List;

import com.baidu.ar.ARController;
import com.baidu.ar.bean.DuMixARConfig;
import com.baidu.ar.resloader.ArCaseDownloadListener;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

    private String[] mArName;
    private String[] mArDesciption;
    private ListView mListView;
    private ArrayAdapter mAdapter;
    private List<ListItemBean> mListData;

    private ARController mARController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置App Id
        DuMixARConfig.setAppId("10000");
        // 设置API Key
        DuMixARConfig.setAPIKey("2288883fb087c4a37fbaf12bce65916e");
        // 设置Secret Key
        DuMixARConfig.setSecretKey("");

        setContentView(R.layout.activity_main);
        initData();
        initView();
    }

    private void initData() {
        Resources res = getResources();
        mArName = res.getStringArray(R.array.ar_name);
        mArDesciption = res.getStringArray(R.array.ar_description);
    }

    private void initView() {
        mListData = getListItemData();
        mListView = (ListView) findViewById(R.id.demo_list);
        mListView.addFooterView(new ViewStub(this));
        mAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, mArName);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ARActivity.class);
                Bundle bundle = new Bundle();
                ListItemBean listItemBean = mListData.get(position);
                bundle.putString(Config.AR_KEY, listItemBean.getARKey());
                bundle.putInt(Config.AR_TYPE, listItemBean.getARType());
                bundle.putString(Config.AR_FILE, listItemBean.getCasePath());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private List<ListItemBean> getListItemData() {
        List<ListItemBean> list = new ArrayList<>();

        // SLAM AR 小熊
        list.add(new ListItemBean(5, "10002502", null, mArName[0], mArDesciption[0]));
        // 本地识图 扫描assets下面的识别图
        list.add(new ListItemBean(6, "", null, mArName[1], mArDesciption[1]));
        // 云端识图 扫描assets下面的识别图
        list.add(new ListItemBean(7, "", null, mArName[2], mArDesciption[2]));
        // Track AR城市地图case 扫描assets下面的识别图
        list.add(new ListItemBean(0, "10187229", null, mArName[3], mArDesciption[3]));
        // IMU AR 请财神case
        list.add(new ListItemBean(0, "10109642", null, mArName[4], mArDesciption[4]));
        // 语音
        list.add(new ListItemBean(0, "10002504", null, mArName[5], mArDesciption[5]));
        // TTS
        list.add(new ListItemBean(0, "10002505", null, mArName[6], mArDesciption[6]));
        // 滤镜
        list.add(new ListItemBean(0, "10062568", null, mArName[7], mArDesciption[7]));

        // 本地case 入口
        list.add(new ListItemBean(5, "", "/sdcard/001_pinkskirt", mArName[8], mArDesciption[8]));
        // logo识别
        list.add(new ListItemBean(0, "10074867", "", mArName[9], mArDesciption[9]));
        // 手势识别
        list.add(new ListItemBean(0, "10008672", "", mArName[10], mArDesciption[10]));
        // 在线视频
        list.add(new ListItemBean(0, "10096034", "", mArName[11], mArDesciption[11]));
        // 背景分割
        list.add(new ListItemBean(0, "10087594", "", mArName[12], mArDesciption[12]));
        // 业务层case通信
        list.add(new ListItemBean(5, "10249694", "", mArName[13], mArDesciption[13]));


        // TODO: 2018/8/3 测试本地case ，以下对应路径不能为空 否则crash
//        list.add(new ListItemBean(5, "", "/sdcard/slam", mArName[14], mArDesciption[14]));
//        list.add(new ListItemBean(0, "", "/sdcard/track", mArName[15], mArDesciption[15]));
//        list.add(new ListItemBean(0, "", "/sdcard/imu", mArName[16], mArDesciption[16]));
        return list;
    }

    private class ListItemBean {
        String mARKey;
        int mARType;
        String mCasePath;
        String mName;
        String mDescription;

        public ListItemBean(int arType, String arKey, String path, String name, String description) {
            this.mARType = arType;
            this.mARKey = arKey;
            this.mName = name;
            this.mDescription = description;
            this.mCasePath = path;
        }

        public String getARKey() {
            return mARKey;
        }

        public int getARType() {
            return mARType;
        }

        public String getName() {
            return mName;
        }

        public String getDescription() {
            return mDescription;
        }

        public String getCasePath() {
            return mCasePath;
        }
    }

    /**
     * 缓存case
     * 缓存按钮点击处理
     *
     * @param view
     */
    public void cacheCase(View view) {
        final TextView progress = findViewById(R.id.progress);
        String caseId = ((EditText) findViewById(R.id.cache_id)).getText().toString();
        mARController = new ARController(this);
        mARController.downloadCase(caseId, new ArCaseDownloadListener() {
            @Override
            public void onProgress(String s, int i) {
                progress.setText(i + "");
            }

            @Override
            public void onFinish(String s, boolean b, String s1) {

            }
        });
    }

    /**
     * 停止缓存case
     *
     * @param view
     */
    public void cancelCase(View view) {
        String caseId = ((EditText) findViewById(R.id.cache_id)).getText().toString();
        if (mARController != null) {
            mARController.cancelDownloadCase(caseId);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mARController != null) {
            mARController.release();
        }
    }
}