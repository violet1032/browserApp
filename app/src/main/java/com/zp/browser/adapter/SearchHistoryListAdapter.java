package com.zp.browser.adapter;

import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.widget.AbsListView;

import com.zp.browser.R;
import com.zp.browser.db.Model.SearchHistory;
import com.zp.browser.utils.StringUtils;

import org.kymjs.kjframe.widget.AdapterHolder;
import org.kymjs.kjframe.widget.KJAdapter;

import java.util.Collection;

/**
 * Created by Administrator on 2018/3/19 0019.
 */
public class SearchHistoryListAdapter extends KJAdapter<SearchHistory> {

    private Handler handler;
    private String select;

    public SearchHistoryListAdapter(AbsListView view, Collection<SearchHistory> mDatas, Handler handler, String str) {
        super(view, mDatas, R.layout.listitem_search_history);
        this.handler = handler;
        select = str;
    }

    @Override
    public void convert(AdapterHolder helper, final SearchHistory item, boolean isScrolling, int position) {
        super.convert(helper, item, isScrolling, position);

        String str = item.getContent();
        int t = str.indexOf(select);
        String str1 = "";
        String str2 = "";
        if (t >= 0) {
            str1 = str.substring(0, t);
            str2 = str.substring(t + select.length());

            StringBuffer stringBuffer = new StringBuffer();
            if (!StringUtils.isEmpty(str1))
                stringBuffer.append(str1);
            if (!StringUtils.isEmpty(select))
                stringBuffer.append("<font color = '#50ade6'>" + select + "</font>");
            if (!StringUtils.isEmpty(str2))
                stringBuffer.append(str2);

            helper.setText(R.id.listitem_search_history_tv_content, Html.fromHtml(stringBuffer.toString()));
        }else{
            helper.setText(R.id.listitem_search_history_tv_content, Html.fromHtml(item.getContent()));
        }

        helper.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message message = new Message();
                message.what = 1;
                message.obj = item;
                handler.sendMessage(message);
            }
        });

        helper.getView(R.id.listitem_search_history_img_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message message = new Message();
                message.what = 2;
                message.obj = item;
                handler.sendMessage(message);
            }
        });
    }
}
