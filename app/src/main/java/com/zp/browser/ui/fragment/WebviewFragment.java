package com.zp.browser.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.zp.browser.R;
import com.zp.browser.ui.common.BaseFragment;

import org.kymjs.kjframe.ui.BindView;

/**
 * <p>
 * 描述:
 * <p>
 * 作者:Administrator
 * <p>
 * 时间:2018/3/22 11:31
 * <p>
 * 版本:
 */
public class WebviewFragment extends BaseFragment {

    @BindView(id = R.id.fragment_webview)
    private WebView webView;

    private String url;
    private String webTitle;

    private Handler mainHandler;

    public WebviewFragment(String url, Handler handler) {
        this.url = url;
        mainHandler = handler;
    }

    public WebviewFragment() {

    }

    public String getUrl(){
        return this.url;
    }
    public String getTitle(){
        return this.webTitle;
    }

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = View.inflate(getActivity(), R.layout.fragment_webview, null);
        return view;
    }

    @Override
    protected void initWidget(View parentView) {
        super.initWidget(parentView);

        WebSettings webSettings = webView.getSettings();
        webSettings.setSupportZoom(true);
        webSettings.setDefaultTextEncodingName("gbk");
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);//关键点
        webView.setWebViewClient(new webviewClient());
        webSettings.setLoadWithOverviewMode(true);

        WebChromeClient webChromeClient = new WebChromeClient(){
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                webTitle = title;
                Message message = new Message();
                message.what = 103;
                message.obj = title;
                mainHandler.sendMessage(message);
            }
        };

        webView.setWebChromeClient(webChromeClient);

        webView.loadUrl(url);
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
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            Message message = new Message();
            message.what = 102;
            message.obj = request.getUrl().toString();
            mainHandler.sendMessage(message);
            return true;
        }
    }

    public void refresh(){
        webView.loadUrl(url);
    }
}
