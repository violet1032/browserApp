package com.zp.browser.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.zp.browser.R;
import com.zp.browser.bean.News;
import com.zp.browser.utils.LogUtil;
import com.zp.browser.utils.StringUtils;
import com.zp.browser.utils.UIHelper;
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


        helper.setText(R.id.listitem_news_tv_content, item.getContent());
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

                int heightBefore = UIHelper.getViewHeight(helper.getConvertView());
                int maxLines = ((TextView) helper.getView(R.id.listitem_news_tv_content)).getMaxLines();
                if (maxLines == 5)
                    ((TextView) helper.getView(R.id.listitem_news_tv_content)).setMaxLines(1000);
                else
                    ((TextView) helper.getView(R.id.listitem_news_tv_content)).setMaxLines(5);

                int heightAfter = UIHelper.getViewHeight(helper.getConvertView());

                LogUtil.logError(NewsListAdapter.class,"heightBefore:"+heightBefore);
                LogUtil.logError(NewsListAdapter.class,"heightAfter:"+heightAfter);


                ScrollView scrollView = (ScrollView)listView.getParent().getParent();
                ViewGroup.LayoutParams layoutParams =  scrollView.getLayoutParams();

                LogUtil.logError(NewsListAdapter.class,"layoutParams.height:"+layoutParams.height);
                layoutParams.height = layoutParams.height + 400;
                scrollView.setLayoutParams(layoutParams);
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
