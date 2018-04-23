package com.zp.browser.ui;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
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
import com.zp.browser.db.Model.Collect;
import com.zp.browser.db.Model.SearchHistory;
import com.zp.browser.ui.common.BaseActivity;
import com.zp.browser.ui.dialog.MenuDialog;
import com.zp.browser.ui.dialog.VersionUpdateDialog;
import com.zp.browser.ui.fragment.MainFragment;
import com.zp.browser.ui.fragment.WebviewFragment;
import com.zp.browser.utils.JsonUtils;
import com.zp.browser.utils.LogUtil;
import com.zp.browser.utils.StringUtils;
import com.zp.browser.utils.UIHelper;
import com.zp.browser.widget.MyScrollView;
import com.zp.browser.widget.OnOverScrolledListener;

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

    //    private Map<Integer, KJFragment> fragmentLinkedList = new HashMap<>();
    private LinkedList<KJFragment> fragmentLinkedList = new LinkedList<>();
    private int current = -1;

    @BindView(id = R.id.act_main_lay_left, click = true)
    private LinearLayout layPrevious;
    @BindView(id = R.id.act_main_lay_right, click = true)
    private LinearLayout layNext;
    @BindView(id = R.id.act_main_lay_home, click = true)
    private LinearLayout layHome;

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
    @BindView(id = R.id.act_main_lay_bottom)
    private LinearLayout layBottom;

    private int pageNumber = 0;
    private boolean isLast;
    @BindView(id = R.id.act_main_scrollview, click = true)
    private MyScrollView scrollView;

    public static Map<String, Integer> urlMap = new HashMap<>();

    @BindView(id = R.id.act_main_lay_page)
    private LinearLayout layPage;
    @BindView(id = R.id.act_main_img_page_up, click = true)
    private ImageView imgPageUp;
    @BindView(id = R.id.act_main_img_page_down, click = true)
    private ImageView imgPageDown;

    @BindView(id = R.id.act_main_lay_refresh)
    private SwipeRefreshLayout layRefresh;

    @BindView(id = R.id.act_main_tv_unread, click = true)
    private TextView tvNotRead;

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
                    case 3:
                        // 刷新
                        KJFragment kjFragment = fragmentLinkedList.get(current);
                        if (kjFragment instanceof WebviewFragment) {
                            ((WebviewFragment) kjFragment).refresh();
                        }
                        break;
                    case 4:
                        // 加载了空页面

                        int i = message.arg1;

                        // 将该页面移除，current减一

                        LogUtil.logError(MainActivity.class, "当前页面:" + current);
                        LogUtil.logError(MainActivity.class, "需要移除的页面:" + i);

                        fragmentLinkedList.remove(i);
                        current--;
                        break;
                    case 5:
                        // 收藏
                        // 判断是否已收藏
                        WebviewFragment webviewFragment = (WebviewFragment) fragmentLinkedList.get(current);
                        List<Collect> list = AppContext.dBHelper.findAllByWhere(Collect.class, "url='" + webviewFragment.getUrl() + "'");
                        if (list.size() > 0) {
                            // 已收藏
                            UIHelper.ToastMessage("已收藏");
                        } else {
                            Collect collect = new Collect();
                            collect.setDomain(StringUtils.getDomain(webviewFragment.getUrl()) + "…");
                            collect.setDateline(System.currentTimeMillis());
                            collect.setIcon(webviewFragment.getIcon());
                            collect.setUrl(webviewFragment.getUrl());
                            collect.setTitle(webviewFragment.getTitle());
                            AppContext.dBHelper.save(collect);
                            UIHelper.ToastMessage("收藏成功");
                        }
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

        getVersion();

        if (AppConfig.getInstance().isLast()) {
            // 打开上次页面
            final String url = AppConfig.getInstance().getmPre().getString("lastUrl", "");
            if (!StringUtils.isEmpty(url)) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        message.what = 102;
                        message.obj = url;
                        handler.sendMessage(message);
                    }
                }, 1000);
            }
        }
    }

    @Override
    public void initWidget() {
        super.initWidget();

        layRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (current == 0) {
                    mainFragment.reload();
                }
                layRefresh.setRefreshing(false);
            }
        });

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

        scrollView.setOnOverScrolledListener(new OnOverScrolledListener() {
            @Override
            public void scrollTop() {

            }

            @Override
            public void scrollBottom() {
                if (current == 0) {
                    mainFragment.getNewsList();
                }
            }
        });

        changeStyle(true);
    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);

        switch (v.getId()) {
            case R.id.act_main_lay_menu:
                // 获取当前url
                String currUrl = "";
                KJFragment kjFragment = fragmentLinkedList.get(current);
                if (kjFragment instanceof WebviewFragment) {
                    currUrl = ((WebviewFragment) kjFragment).getUrl();
                }

                MenuDialog.startActivity(this, currUrl, handler);
                break;
            case R.id.act_main_lay_left:
                previousFragment();
                break;
            case R.id.act_main_lay_right:
                nextFragment();
                break;
            case R.id.act_main_img_refresh:
                ((WebviewFragment) fragmentLinkedList.get(current)).refresh();
                break;
            case R.id.act_main_search_tv_title:
                laySearchShow.setVisibility(View.GONE);
                laySearchInput.setVisibility(View.VISIBLE);
                lvSearchHistory.setVisibility(View.VISIBLE);

                edtUrl.setText(((WebviewFragment) fragmentLinkedList.get(current)).getUrl());

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

                        layTop.setFocusable(true);
                        layTop.setFocusableInTouchMode(true);
                    }
                }
                break;
            case R.id.act_main_img_clean:
                edtUrl.setText("");
                break;
            case R.id.act_main_lay_home:
                current = 0;
                AppConfig.getInstance().mPreSet("lastUrl", "");
                changeFragment(R.id.act_main_fragment, fragmentLinkedList.get(current));
                break;
            case R.id.act_main_img_page_up:
                scrollView.scrollBy(0, -(AppContext.screenHeight - 300));
                break;
            case R.id.act_main_img_page_down:
                scrollView.scrollBy(0, AppContext.screenHeight - 300);
                break;
            case R.id.act_main_tv_unread:
                notRead = 0;
                if (current == 0)
                    mainFragment.reload();
                hideNotRead();
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
        layTop.setFocusable(true);
        layTop.setFocusableInTouchMode(true);

        lvSearchHistory.setVisibility(View.GONE);

        laySearchShow.setVisibility(View.VISIBLE);
        laySearchInput.setVisibility(View.GONE);
        lvSearchHistory.setVisibility(View.GONE);

        WebviewFragment webviewFragment = new WebviewFragment(url, handler, current + 1);
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
        if (current == 0) {
            // 如果是从主页开始，那么直接清除掉后面所有的记录
            KJFragment main = fragmentLinkedList.get(0);

            fragmentLinkedList = null;
            fragmentLinkedList = new LinkedList<>();
            fragmentLinkedList.add(main);
        }

        current++;
        changeFragment(R.id.act_main_fragment, fragment);
        fragmentLinkedList.add(fragment);

        if (current > 0) {
            edtUrl.setText(((WebviewFragment) fragment).getUrl());
            tvTitle.setText(((WebviewFragment) fragment).getTitle());
        }

        if(current > 0)
            hideNotRead();

        arrowHandle();
    }

    public void nextFragment() {
        current++;
        if (current < fragmentLinkedList.size() && fragmentLinkedList.get(current) != null) {
            changeFragment(R.id.act_main_fragment, fragmentLinkedList.get(current));

            layTop.setVisibility(View.VISIBLE);

            edtUrl.setText(((WebviewFragment) fragmentLinkedList.get(current)).getUrl());
            tvTitle.setText(((WebviewFragment) fragmentLinkedList.get(current)).getTitle());

            lvSearchHistory.setVisibility(View.GONE);

            AppConfig.getInstance().mPreSet("lastUrl", ((WebviewFragment) fragmentLinkedList.get(current)).getUrl());

            laySearchShow.setVisibility(View.VISIBLE);
            laySearchInput.setVisibility(View.GONE);
            lvSearchHistory.setVisibility(View.GONE);

            arrowHandle();

            hideNotRead();
        } else
            current--;
    }

    public void previousFragment() {
        current--;
        if (current < 0) {
            current++;
        } else if (current < fragmentLinkedList.size() && fragmentLinkedList.get(current) != null) {
//        if (current >= 0 && fragmentLinkedList.get(current) != null) {
//            changeFragment(R.id.act_main_fragment, fragmentLinkedList.get(current));
//        } else
//            current++;
            changeFragment(R.id.act_main_fragment, fragmentLinkedList.get(current));

            if (current == 0) {
                showNotRead(notRead);

                layTop.setVisibility(View.GONE);

                AppConfig.getInstance().mPreSet("lastUrl", "");
            } else {
                edtUrl.setText(((WebviewFragment) fragmentLinkedList.get(current)).getUrl());
                tvTitle.setText(((WebviewFragment) fragmentLinkedList.get(current)).getTitle());

                lvSearchHistory.setVisibility(View.GONE);

                laySearchShow.setVisibility(View.VISIBLE);
                laySearchInput.setVisibility(View.GONE);
                lvSearchHistory.setVisibility(View.GONE);

                AppConfig.getInstance().mPreSet("lastUrl", ((WebviewFragment) fragmentLinkedList.get(current)).getUrl());
            }

            arrowHandle();
        }
    }

    public void arrowHandle() {
        if (current == 0) {
            ((ImageView) layPrevious.getChildAt(0)).setImageResource(R.drawable.arrow_left_gray_1);
            layPage.setVisibility(View.GONE);
        } else {
            ((ImageView) layPrevious.getChildAt(0)).setImageResource(R.drawable.arrow_left_gray_2);

            if (AppConfig.getInstance().getmPre().getBoolean("page_1", false))
                layPage.setVisibility(View.VISIBLE);
        }

        if (current == fragmentLinkedList.size() - 1) {
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

        layTop.setFocusable(true);
        layTop.setFocusableInTouchMode(true);
    }

    public void autoLogin() {
        String phone = AppConfig.getInstance().getmPre().getString("phone", null);
        String password = AppConfig.getInstance().getmPre().getString("password", null);

        if (!StringUtils.isEmpty(phone) && !StringUtils.isEmpty(password) && AppConfig.getInstance().getmPre().getBoolean("autoLogin", false)) {
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

            layBottom.setBackgroundColor(getResources().getColor(R.color.night_black_3));
        } else {
            layBg.setBackgroundColor(getResources().getColor(R.color.main_skyblue));
            layBg_1.setBackgroundColor(getResources().getColor(R.color.main_skyblue));
            layTop2.setBackgroundColor(getResources().getColor(R.color.main_skyblue));
            laySearchInput.setBackgroundColor(getResources().getColor(R.color.main_skyblue));
            laySearchShow.setBackgroundColor(getResources().getColor(R.color.main_skyblue_3));
            layInputContent.setBackgroundColor(getResources().getColor(R.color.white));

            tvCancel.setTextColor(getResources().getColor(R.color.white));
            tvTitle.setTextColor(getResources().getColor(R.color.white));
            layBottom.setBackgroundColor(getResources().getColor(R.color.white));
        }

        if (!isFirst)
            mainFragment.changeStyle();

        if (!isFirst)
//            for (Integer key :
//                    fragmentLinkedList.keySet()) {
//                KJFragment kjFragment = fragmentLinkedList.get(key);
//                if (kjFragment instanceof WebviewFragment) {
//                    ((WebviewFragment) kjFragment).changeStyle();
//                    ;
//                }
//            }
            for (KJFragment kjFragment :
                    fragmentLinkedList) {
                if (kjFragment instanceof WebviewFragment) {
                    ((WebviewFragment) kjFragment).changeStyle();
                    ;
                }
            }
    }

    private long exitTime = 0;//再按一次退出登录

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {

            if (laySearchInput.getVisibility() == View.VISIBLE) {
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

                    layTop.setFocusable(true);
                    layTop.setFocusableInTouchMode(true);
                }

            } else if (current > 0) {
                previousFragment();
            } else if (current == 0) {
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    UIHelper.ToastMessage("再按一次退出程序");
                    exitTime = System.currentTimeMillis();
                } else {
                    System.exit(0);
                }
            }
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (AppConfig.getInstance().getmPre().getBoolean("page_3", false) && current > 0) {
                scrollView.scrollBy(0, -(AppContext.screenHeight - 300));
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (AppConfig.getInstance().getmPre().getBoolean("page_3", false) && current > 0) {
                scrollView.scrollBy(0, AppContext.screenHeight - 300);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void getVersion() {
        FHttpCallBack callBack = new FHttpCallBack() {
            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                super.onSuccess(headers, t);
                String str = new String(t);
                Result result = new Result().parse(str);
                if (result.isOk())
                    try {
                        JsonUtils j = new JsonUtils(str);
                        JsonUtils jsonUtils = j.getJSONUtils("version");

                        if (jsonUtils.getString("version").compareTo(AppContext.versionName) > 0) {
                            VersionUpdateDialog.startActivity(MainActivity.this, jsonUtils.getString("version"),
                                    jsonUtils.getString("content"), jsonUtils.getBoolean("must"), jsonUtils.getString("url"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }
        };
        ApiMain.getVersion(callBack);
    }

    int notRead = 0;

    public void showNotRead(int count) {
        notRead = count;
        if (current == 0 && count > 0) {
            tvNotRead.setText("↑" + count + "条新快讯");
            tvNotRead.setVisibility(View.VISIBLE);
        }
    }

    public void hideNotRead(){
        tvNotRead.setVisibility(View.GONE);
    }
}
