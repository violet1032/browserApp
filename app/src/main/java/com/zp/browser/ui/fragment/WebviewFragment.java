package com.zp.browser.ui.fragment;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.zp.browser.AppConfig;
import com.zp.browser.AppContext;
import com.zp.browser.R;
import com.zp.browser.api.ApiUser;
import com.zp.browser.api.FHttpCallBack;
import com.zp.browser.db.Model.ScanHistory;
import com.zp.browser.ui.common.BaseFragment;
import com.zp.browser.utils.LogUtil;
import com.zp.browser.utils.StringUtils;

import org.kymjs.kjframe.ui.BindView;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * <p/>
 * 描述:
 * <p/>
 * 作者:Administrator
 * <p/>
 * 时间:2018/3/22 11:31
 * <p/>
 * 版本:
 */
public class WebviewFragment extends BaseFragment {

    @BindView(id = R.id.fragment_webview)
    private WebView webView;

    private String url;
    private String webTitle;
    private String iconurl;

    private Handler mainHandler;

    private boolean isNight = false;

    private int current;

    private boolean hasGetReward = false;
    private boolean hasRemove = false;

    public static final WebviewFragment newInstance(String url, int current) {
        WebviewFragment fragment = new WebviewFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putInt("current", current);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getArguments().getString("url");
        current = getArguments().getInt("current");
    }

    public void setMainHandler(Handler mainHandler) {
        this.mainHandler = mainHandler;
    }

    public WebviewFragment() {

    }

    public String getUrl() {
        return this.url;
    }

    public String getTitle() {
        return this.webTitle;
    }

    public String getIcon() {
        return this.iconurl;
    }

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = View.inflate(getActivity(), R.layout.fragment_webview, null);
        return view;
    }

    @Override
    protected void initWidget(View parentView) {
        super.initWidget(parentView);

        isNight = AppConfig.getInstance().getmPre().getBoolean("isNight", false);

        WebSettings webSettings = webView.getSettings();
        webSettings.setSupportZoom(true);
        webSettings.setDefaultTextEncodingName("gbk");
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);//关键点
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.setWebViewClient(new webviewClient());
        webSettings.setLoadWithOverviewMode(true);

        WebChromeClient webChromeClient = new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                webTitle = title;
                Message message = new Message();
                message.what = 103;
                message.obj = title;
                mainHandler.sendMessage(message);
            }

            @Override
            public void onReceivedTouchIconUrl(WebView view, String iconurl, boolean precomposed) {
                super.onReceivedTouchIconUrl(view, iconurl, precomposed);

                WebviewFragment.this.iconurl = iconurl;
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                setNight();

                if (newProgress >= 100) {
                    // 判断是否为空页面
                    if (view.getContentHeight() == 0) {
                        // 非空页面
                        if (!hasRemove) {
                            hasRemove = true;
                            // 做出处理
                            Message message = new Message();
                            message.what = 4;
                            message.arg1 = current;
                            mainHandler.sendMessage(message);
                        }
                    } else {
                        if (!hasGetReward) {
                            readAward();

                            // 添加历史记录
                            List<ScanHistory> list = AppContext.dBHelper.findAllByWhere(ScanHistory.class, "url='" + url + "'", "dateline desc");
                            if (list.size() == 0) {
                                ScanHistory scanHistory = new ScanHistory();
                                scanHistory.setDateline(System.currentTimeMillis());
                                scanHistory.setDomain(StringUtils.getDomain(url));
                                scanHistory.setUrl(url);
                                scanHistory.setTitle(webTitle);
                                scanHistory.setIcon(iconurl);
                                AppContext.dBHelper.save(scanHistory);
                            } else {
                                ScanHistory scanHistory = list.get(0);
                                scanHistory.setIcon(iconurl);
                                AppContext.dBHelper.update(scanHistory);
                            }
                        }
                    }
                }
            }


        };

        webView.setWebChromeClient(webChromeClient);

        AppConfig.getInstance().mPreSet("lastUrl", url);

        webView.loadUrl(url);
    }

    @Override
    public void onChange() {
        super.onChange();
        if (isNight != AppConfig.getInstance().getmPre().getBoolean("isNight", false)) {
            isNight = AppConfig.getInstance().getmPre().getBoolean("isNight", false);
            changeStyle();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void widgetClick(View v) {
        super.widgetClick(v);
    }

    private class webviewClient extends WebViewClient {
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            LogUtil.logError(WebviewFragment.class, "url:" + url);
//            if (url.startsWith("http") || url.startsWith("https")) {
//                Message message = new Message();
//                message.what = 102;
//                message.obj = request.getUrl().toString();
//                mainHandler.sendMessage(message);
//                return true;
//            }
//            return false;

            if (url.startsWith("http:") || url.startsWith("https:")) {
                Message message = new Message();
                message.what = 102;
                message.obj = request.getUrl().toString();
                mainHandler.sendMessage(message);
            }
            return true;
//
//            return true;

        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            setNight();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            setNight();
        }
    }

    public void refresh() {
        webView.reload();
    }

    public void readAward() {
        hasGetReward = true;
        if (AppContext.user.getId() > 0) {
            FHttpCallBack callBack = new FHttpCallBack() {
                @Override
                public void onSuccess(Map<String, String> headers, byte[] t) {
                    super.onSuccess(headers, t);
                }
            };
            ApiUser.readAward(callBack);
        }
    }

    public void changeStyle() {
        webView.reload();
//        if (AppConfig.getInstance().getmPre().getBoolean("isNight", false)) {
//            setNight();
//        } else {
//            webView.setBackgroundColor(getResources().getColor(R.color.transparent)); // 设置背景色
//            webView.getBackground().setAlpha(0); // 设置填充透明度 范围：0-255
//        }
    }

    private void setNight() {
        if (AppConfig.getInstance().getmPre().getBoolean("isNight", false)) {

            InputStream is = getActivity().getResources().openRawResource(R.raw.night);
            byte[] buffer = new byte[0];
            try {
                buffer = new byte[is.available()];
                is.read(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            String code = Base64.encodeToString(buffer, Base64.NO_WRAP);

            webView.loadUrl("javascript:(function() {" + "var parent = document.getElementsByTagName('head').item(0);"
                    + "var style = document.createElement('style');" + "style.type = 'text/css';"
                    + "style.innerHTML = window.atob('" + code + "');"
                    + "parent.appendChild(style)" + "})();");
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            if (webView != null)
                webView.onPause();
        }else {
            if (webView != null)
                webView.onResume();
        }
    }
}
