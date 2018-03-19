package com.zp.browser.adapter;

import android.view.View;
import android.widget.AbsListView;

import com.zp.browser.R;
import com.zp.browser.bean.News;
import com.zp.browser.utils.StringUtils;

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

    public NewsListAdapter(AbsListView view, Collection<News> mDatas) {
        super(view, mDatas, R.layout.listitem_news);
    }

    @Override
    public void convert(AdapterHolder helper, News item, boolean isScrolling, int position) {
        super.convert(helper, item, isScrolling, position);

        String date = time(item.getDateline(), position);
        if (!StringUtils.isEmpty(date)) {
            helper.getView(R.id.listitem_news_lay_time).setVisibility(View.VISIBLE);
            helper.setText(R.id.listitem_news_tv_time_tile, date);
        } else {
            helper.getView(R.id.listitem_news_lay_time).setVisibility(View.GONE);
        }


        helper.setText(R.id.listitem_news_tv_content, item.getContent());
        helper.setText(R.id.listitem_news_tv_time, StringUtils.getDateHM(StringUtils.date_fromat_change_4(item.getDateline())));

        helper.getView(R.id.listitem_news_lay_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        helper.getView(R.id.listitem_news_lay_content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
}
