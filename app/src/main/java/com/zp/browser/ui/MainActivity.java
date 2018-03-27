package com.zp.browser.ui;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zp.browser.AppConfig;
import com.zp.browser.AppContext;
import com.zp.browser.R;
import com.zp.browser.adapter.SearchHistoryListAdapter;
import com.zp.browser.api.ApiMain;
import com.zp.browser.api.ApiUser;
import com.zp.browser.api.FHttpCallBack;
import com.zp.browser.bean.Result;
import com.zp.browser.db.Model.SearchHistory;
import com.zp.browser.ui.common.BaseActivity;
import com.zp.browser.ui.dialog.MenuDialog;
import com.zp.browser.ui.fragment.MainFragment;
import com.zp.browser.ui.fragment.WebviewFragment;
import com.zp.browser.utils.JsonUtils;
import com.zp.browser.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.ui.KJFragment;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseActivity {

    private MainFragment mainFragment = new MainFragment();

    @BindView(id = R.id.act_main_lay_menu, click = true)
    private LinearLayout layMenu;

    @BindView(id = R.id.act_main_lay_top)
    private RelativeLayout layTop;

    private Handler handler;

    private LinkedList<Fragment> fragments = new LinkedList<>();

    private Map<Integer, KJFragment> fragmentMap = new HashMap<>();
    private int current = -1;

    @BindView(id = R.id.act_main_lay_left, click = true)
    private LinearLayout layPrevious;
    @BindView(id = R.id.act_main_lay_right, click = true)
    private LinearLayout layNext;

    @BindView(id = R.id.act_main_img_refresh, click = true)
    private ImageView imgRefresh;
    @BindView(id = R.id.act_main_search_tv_title, click = true)
    private TextView tvTitle;
    @BindView(id = R.id.act_main_tv_cancel, click = true)
    private TextView tvCancel;
    @BindView(id = R.id.act_main_lay_search_show)
    private RelativeLayout laySearchShow;
    @BindView(id = R.id.act_main_lay_search_input)
    private RelativeLayout laySearchInput;
    @BindView(id = R.id.act_main_search_tv_url)
    private EditText edtUrl;

    private List<String> searchHistory = new ArrayList<>();
    @BindView(id = R.id.act_main_search_history_lv)
    private ListView lvSearchHistory;
    private SearchHistoryListAdapter searchHistoryListAdapter;
    @BindView(id = R.id.act_main_img_clean, click = true)
    private ImageView imgClean;

    private Timer timer;
    private TimerTask timerTask;

    private Timer timerGetSuggestion;
    private TimerTask taskGetSuggestion;

    @BindView(id = R.id.act_main_lay_bg)
    private RelativeLayout layBg;
    @BindView(id = R.id.act_main_lay_top_2)
    private RelativeLayout layTop2;
    @BindView(id = R.id.act_main_lay_search_input_content)
    private RelativeLayout layInputContent;
    @BindView(id = R.id.act_main_lay_bg_1)
    private LinearLayout layBg_1;

    public static void startActivity(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void setRootView() {
        super.setRootView();
        setContentView(R.layout.activity_main);
    }

    @Override
    public void initData() {
        super.initData();

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what) {
                    case 102:
                        String uri = (String) message.obj;
                        webviewStart(uri);
                        break;
                    case 103:
                        String title = (String) message.obj;
                        tvTitle.setText(title);
                        break;
                    case 1:
                        // 搜索
                        SearchHistory searchHistory = (SearchHistory) message.obj;
                        edtUrl.setText(searchHistory.getContent());
                        break;
                    case 2:
                        // 填入内容
                        searchHistory = (SearchHistory) message.obj;
                        edtUrl.setText(searchHistory.getContent());

                        edtUrl.requestFocus();
                        edtUrl.setSelection(edtUrl.getText().length());
                        break;
                    case 104:
                        getSearchHistory();
                        break;
                }
                return false;
            }
        });

        // 自动登录
        autoLogin();

        // 定时更新用户信息
        if (timer == null)
            timer = new Timer();
        if (timerTask == null) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    autoGetUserInfo();
                }
            };
            timer.schedule(timerTask, 5000, 5000);
        }

        // 获取搜索引擎地址
        searchUrl();
    }

    @Override
    public void initWidget() {
        super.initWidget();

        addFragment(mainFragment);

        edtUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String content = charSequence.toString();
                if (StringUtils.isEmpty(content)) {
                    tvCancel.setText("取消");
                } else {
                    tvCancel.setText("搜索");
                }

                if (timerGetSuggestion == null || taskGetSuggestion == null) {
                    timerGetSuggestion = new Timer();
                    taskGetSuggestion = new TimerTask() {
                        @Override
                        public void run() {
                            handler.sendEmptyMessage(104);

                            timerGetSuggestion.cancel();
                            timerGetSuggestion = null;
                            taskGetSuggestion.cancel();
                            taskGetSuggestion = null;
                        }
                    };
                    timerGetSuggestion.schedule(taskGetSuggestion, 1000);
                } else {
                    timerGetSuggestion.cancel();
                    timerGetSuggestion = null;
                    taskGetSuggestion.cancel();
                    taskGetSuggestion = null;

                    timerGetSuggestion = new Timer();
                    taskGetSuggestion = new TimerTask() {
                        @Override
                        public void run() {
                            handler.sendEmptyMessage(104);

                            timerGetSuggestion.cancel();
                            timerGetSuggestion = null;
                            taskGetSuggestion.cancel();
                            taskGetSuggestion = null;
                        }
                    };
                    timerGetSuggestion.schedule(taskGetSuggestion, 1000);
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        changeStyle(true);
    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);

        switch (v.getId()) {
            case R.id.act_main_lay_menu:
                MenuDialog.startActivity(this);
                break;

            case R.id.act_main_lay_left:
                previousFragment();
                break;
            case R.id.act_main_lay_right:
                nextFragment();
                break;
            case R.id.act_main_img_refresh:
                ((WebviewFragment) fragmentMap.get(current)).refresh();
                break;
            case R.id.act_main_search_tv_title:
                laySearchShow.setVisibility(View.GONE);
                laySearchInput.setVisibility(View.VISIBLE);
                lvSearchHistory.setVisibility(View.VISIBLE);

                edtUrl.setText(((WebviewFragment) fragmentMap.get(current)).getUrl());

                edtUrl.setFocusableInTouchMode(true);
                edtUrl.requestFocus();
                edtUrl.selectAll();

                break;
            case R.id.act_main_tv_cancel:
                String str = tvCancel.getText().toString().trim();
                String content = edtUrl.getText().toString().trim();
                if (str.equals("搜索")) {
                    if (content.startsWith("http")) {
                        webviewStart(content);
                    } else {
                        List<SearchHistory> searchHistorys = AppContext.dBHelper.findAllByWhere(SearchHistory.class, "content='" + content + "'");
                        if (searchHistorys.size() == 0) {
                            SearchHistory searchHistory = new SearchHistory();
                            searchHistory.setContent(content);
                            searchHistory.setDateline(System.currentTimeMillis());
                            AppContext.dBHelper.save(searchHistory);
                        }

                        // 获取搜索引擎，跳转搜索
                        String url = AppConfig.getInstance().getmPre().getString("searchUrl", "https://www.baidu.com/s?wd=") + content;
                        webviewStart(url);
                    }
                } else if (str.equals("取消")) {
                    if (current == 0) {
                        layTop.setVisibility(View.GONE);
                        lvSearchHistory.setVisibility(View.GONE);

                        laySearchShow.setVisibility(View.VISIBLE);
                        laySearchInput.setVisibility(View.GONE);
                        lvSearchHistory.setVisibility(View.GONE);
                    } else {
                        laySearchShow.setVisibility(View.VISIBLE);
                        laySearchInput.setVisibility(View.GONE);
                        lvSearchHistory.setVisibility(View.GONE);
                    }
                }
                break;
            case R.id.act_main_img_clean:
                edtUrl.setText("");
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    private BroadcastReceiver broadcastReceiver;

    @Override
    public void registerBroadcast() {
        super.registerBroadcast();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("android.intent.style_change")) {
                    changeStyle(false);
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("android.intent.style_change"));
    }

    @Override
    public void unRegisterBroadcast() {
        super.unRegisterBroadcast();
        unregisterReceiver(broadcastReceiver);
    }

    public void webviewStart(String url) {

        layTop.setVisibility(View.VISIBLE);

        lvSearchHistory.setVisibility(View.GONE);

        laySearchShow.setVisibility(View.VISIBLE);
        laySearchInput.setVisibility(View.GONE);
        lvSearchHistory.setVisibility(View.GONE);

        WebviewFragment webviewFragment = new WebviewFragment(url, handler);
        addFragment(webviewFragment);
    }

    public void wakuang() {
        if (AppContext.user.getId() == 0) {
            LoginActivity.startActivity(this);
        } else {
            UserActivity.startActivity(this);
        }
    }

    public void addFragment(KJFragment fragment) {
        current++;
        changeFragment(R.id.act_main_fragment, fragment);
        fragmentMap.put(current, fragment);

        if (current > 0) {
            edtUrl.setText(((WebviewFragment) fragment).getUrl());
            tvTitle.setText(((WebviewFragment) fragment).getTitle());
        }

        arrowHandle();
    }

    public void nextFragment() {
        current++;
        if (fragmentMap.get(current) != null) {
            changeFragment(R.id.act_main_fragment, fragmentMap.get(current));

            layTop.setVisibility(View.VISIBLE);

            edtUrl.setText(((WebviewFragment) fragmentMap.get(current)).getUrl());
            tvTitle.setText(((WebviewFragment) fragmentMap.get(current)).getTitle());

            lvSearchHistory.setVisibility(View.GONE);

            laySearchShow.setVisibility(View.VISIBLE);
            laySearchInput.setVisibility(View.GONE);
            lvSearchHistory.setVisibility(View.GONE);

            arrowHandle();
        } else
            current--;
    }

    public void previousFragment() {
        current--;
        if (fragmentMap.get(current) != null) {
            changeFragment(R.id.act_main_fragment, fragmentMap.get(current));
        } else
            current++;

        if (current == 0) {
            layTop.setVisibility(View.GONE);
        } else {
            edtUrl.setText(((WebviewFragment) fragmentMap.get(current)).getUrl());
            tvTitle.setText(((WebviewFragment) fragmentMap.get(current)).getTitle());

            lvSearchHistory.setVisibility(View.GONE);

            laySearchShow.setVisibility(View.VISIBLE);
            laySearchInput.setVisibility(View.GONE);
            lvSearchHistory.setVisibility(View.GONE);
        }

        arrowHandle();
    }

    public void arrowHandle() {
        if (current == 0) {
            ((ImageView) layPrevious.getChildAt(0)).setImageResource(R.drawable.arrow_left_gray_1);
        } else
            ((ImageView) layPrevious.getChildAt(0)).setImageResource(R.drawable.arrow_left_gray_2);

        if (current == fragmentMap.size() - 1) {
            ((ImageView) layNext.getChildAt(0)).setImageResource(R.drawable.arrow_right_gray_1);
        } else
            ((ImageView) layNext.getChildAt(0)).setImageResource(R.drawable.arrow_right_gray_2);
    }

    private void getSearchHistory() {
        final String content = edtUrl.getText().toString();

        // 获取历史
        final List<SearchHistory> list = AppContext.dBHelper.findAllByWhere(SearchHistory.class, "content like '%" + content + "%'", "dateline desc");

        searchHistoryListAdapter = new SearchHistoryListAdapter(lvSearchHistory, list, handler, content);
        lvSearchHistory.setAdapter(searchHistoryListAdapter);

        if (StringUtils.isEmpty(content)) {
            return;
        }

        // 获取推荐搜索
        String url = AppConfig.getInstance().getmPre().getString("suggestion", "http://suggestion.baidu.com/su?ws=");
        HttpCallBack callBack = new HttpCallBack() {
            @Override
            public void onSuccess(byte[] t) {
                super.onSuccess(t);
                try {
                    String str = new String(t, "GB2312");
                    int start = str.indexOf("[");
                    int end = str.indexOf("]");
                    String str2 = str.substring(start, end + 1);
                    try {
                        JSONArray jsonArray = new JSONArray(str2);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            SearchHistory searchHistory = new SearchHistory();
                            searchHistory.setDateline(System.currentTimeMillis());
                            searchHistory.setContent(jsonArray.getString(i));
                            list.add(searchHistory);
                        }

                        searchHistoryListAdapter = new SearchHistoryListAdapter(lvSearchHistory, list, handler, content);
                        lvSearchHistory.setAdapter(searchHistoryListAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(String str) {
                super.onSuccess(str);


            }
        };
        ApiMain.getSearchSuggestion(url + content, callBack);
    }

    public void searchShow(String content) {
        layTop.setVisibility(View.VISIBLE);

        laySearchShow.setVisibility(View.GONE);
        laySearchInput.setVisibility(View.VISIBLE);
        lvSearchHistory.setVisibility(View.VISIBLE);

        edtUrl.setText(content);

        edtUrl.setFocusableInTouchMode(true);
        edtUrl.requestFocus();
        edtUrl.selectAll();
    }

    public void autoLogin() {
        String phone = AppConfig.getInstance().getmPre().getString("phone", null);
        String password = AppConfig.getInstance().getmPre().getString("password", null);

        if (!StringUtils.isEmpty(phone) && !StringUtils.isEmpty(password)) {
            FHttpCallBack callBack = new FHttpCallBack() {

                @Override
                public void onSuccess(Map<String, String> headers, byte[] t) {
                    super.onSuccess(headers, t);
                    String str = new String(t);
                    Result result = new Result();
                    result.parse(str);
                    if (result.isOk()) {
                        // 登录成功
                        AppContext.user.parse(str);
                    }
                }

                @Override
                public void onSuccess(String t) {
                    super.onSuccess(t);

                }
            };
            ApiUser.login(phone, password, callBack);
        }
    }

    public void autoGetUserInfo() {
        if (AppContext.user.getId() > 0) {
            FHttpCallBack callBack = new FHttpCallBack() {

                @Override
                public void onSuccess(Map<String, String> headers, byte[] t) {
                    super.onSuccess(headers, t);
                    String str = new String(t);
                    Result result = new Result();
                    result.parse(str);
                    if (result.isOk()) {
                        AppContext.user.parse(str);
                    }
                }

                @Override
                public void onSuccess(String t) {
                    super.onSuccess(t);

                }
            };
            ApiUser.getUserInfo(callBack);
        }
    }

    public void searchUrl() {
        FHttpCallBack callBack = new FHttpCallBack() {
            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                super.onSuccess(headers, t);
                String str = new String(t);
                Result result = new Result();
                result.parse(str);
                if (result.isOk()) {
                    try {
                        JsonUtils jsonUtils = new JsonUtils(str);
                        AppConfig.getInstance().mPreSet("searchUrl", jsonUtils.getString("search"));
                        AppConfig.getInstance().mPreSet("suggestion", jsonUtils.getString("suggestion"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        ApiMain.searchUrl(callBack);
    }

    private void changeStyle(boolean isFirst) {
        if (AppConfig.getInstance().getmPre().getBoolean("isNight", false)) {
            layBg.setBackgroundColor(getResources().getColor(R.color.night_black_1));
            layBg_1.setBackgroundColor(getResources().getColor(R.color.night_black_2));
            layTop2.setBackgroundColor(getResources().getColor(R.color.night_black_2));
            laySearchInput.setBackgroundColor(getResources().getColor(R.color.night_black_2));
            laySearchShow.setBackgroundColor(getResources().getColor(R.color.night_black_1));
            layInputContent.setBackgroundColor(getResources().getColor(R.color.night_black_1));

            tvCancel.setTextColor(getResources().getColor(R.color.night_text_1));
            tvTitle.setTextColor(getResources().getColor(R.color.night_text_1));
        } else {
            layBg.setBackgroundColor(getResources().getColor(R.color.main_skyblue));
            layBg_1.setBackgroundColor(getResources().getColor(R.color.main_skyblue));
            layTop2.setBackgroundColor(getResources().getColor(R.color.main_skyblue));
            laySearchInput.setBackgroundColor(getResources().getColor(R.color.main_skyblue));
            laySearchShow.setBackgroundColor(getResources().getColor(R.color.main_skyblue_3));
            layInputContent.setBackgroundColor(getResources().getColor(R.color.white));

            tvCancel.setTextColor(getResources().getColor(R.color.white));
            tvTitle.setTextColor(getResources().getColor(R.color.white));
        }

        if (!isFirst)
            mainFragment.changeStyle();

        if (!isFirst)
            for (Integer key :
                    fragmentMap.keySet()) {
                KJFragment kjFragment = fragmentMap.get(key);
                if (kjFragment instanceof WebviewFragment) {
                    ((WebviewFragment) kjFragment).changeStyle();
                    ;
                }
            }
    }
}
