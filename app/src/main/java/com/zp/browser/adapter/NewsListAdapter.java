package com.zp.browser.adapter;

import android.text.Html;
import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;

import com.zp.browser.R;
import com.zp.browser.bean.News;
import com.zp.browser.utils.StringUtils;
import com.zp.browser.widget.ListviewScroll;

import org.kymjs.kjframe.widget.AdapterHolder;
import org.kymjs.kjframe.widget.KJAdapter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/19 0019.
 */
public class NewsListAdapter extends KJAdapter<News> {

    private Map<String, Integer> map = new HashMap<>();

    private ListviewScroll listView;

    public NewsListAdapter(AbsListView view, Collection<News> mDatas) {
        super(view, mDatas, R.layout.listitem_news);
        listView = (ListviewScroll)view;
    }

    @Override
    public void convert(final AdapterHolder helper, News item, boolean isScrolling, int position) {
        super.convert(helper, item, isScrolling, position);

        String date = time(item.getDateline(), position);
        if (!StringUtils.isEmpty(date)) {
            helper.getView(R.id.listitem_news_lay_time).setVisibility(View.VISIBLE);
            helper.setText(R.id.listitem_news_tv_time_tile, date);
        } else {
            helper.getView(R.id.listitem_news_lay_time).setVisibility(View.GONE);
        }

        TextView tv = helper.getView(R.id.listitem_news_tv_content);
//        tv.setText(Html.fromHtml(item.getContent()));
        tv.setText(Html.fromHtml("<h1 style=\"color:blue; text-align:center\">This is a header</h1>\n" +
                "<p style=\"color:red\">This is a paragraph.</p>"));

//        helper.setText(R.id.listitem_news_tv_content, Html.fromHtml(item.getContent()));
        helper.setText(R.id.listitem_news_tv_time, StringUtils.getDateHM(StringUtils.date_fromat_change_4(item.getDateline())));
        helper.setText(R.id.listitem_news_tv_countdown, getCoundDown(item.getDateline(), item.getHours()));

        helper.getView(R.id.listitem_news_lay_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        helper.getView(R.id.listitem_news_lay_content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int maxLines = ((TextView) helper.getView(R.id.listitem_news_tv_content)).getMaxLines();
                if (maxLines == 5) {
                    ((TextView) helper.getView(R.id.listitem_news_tv_content)).setMaxLines(1000);
                }else
                    ((TextView) helper.getView(R.id.listitem_news_tv_content)).setMaxLines(5);
            }
        });

    }

    private String time(String date, int position) {
        String str = StringUtils.getDateYMD(StringUtils.date_fromat_change_4(date));

        if (map.get(str) != null && map.get(str) != position) {
            return null;
        } else {
            map.put(str, position);
            String d = StringUtils.getDateMD(StringUtils.date_fromat_change_4(date));
            return d;
        }
    }

    private String getCoundDown(String time, int hours) {
        long l = StringUtils.date_fromat_change_4(time);
        long ll = l + hours * 1000 * 60 * 60;
        long offset = ll - System.currentTimeMillis();
        if (offset <= 0)
            return "已结束";

        long h = offset / 1000 / 60 / 60;
        long m = (offset - h * 1000 * 60 * 60) / 1000 / 60;
        long s = (offset - h * 1000 * 60 * 60 - m * 1000 * 60);

        StringBuffer stringBuffer = new StringBuffer();
        if (h > 0)
            stringBuffer.append(StringUtils.zeroFill((int) h) + ":");
        if (m > 0)
            stringBuffer.append(StringUtils.zeroFill((int) m) + ":");
        if (s > 0)
            stringBuffer.append(StringUtils.zeroFill((int) s));
        return stringBuffer.toString();
    }
}
