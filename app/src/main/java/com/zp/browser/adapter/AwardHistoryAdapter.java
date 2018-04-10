package com.zp.browser.adapter;

import android.widget.AbsListView;
import android.widget.TextView;

import com.zp.browser.AppConfig;
import com.zp.browser.AppContext;
import com.zp.browser.R;
import com.zp.browser.bean.AwardHistory;

import org.kymjs.kjframe.widget.AdapterHolder;
import org.kymjs.kjframe.widget.KJAdapter;

import java.util.Collection;

/**
 * Created by Administrator on 2018/3/19 0019.
 */
public class AwardHistoryAdapter extends KJAdapter<AwardHistory> {

    public AwardHistoryAdapter(AbsListView view, Collection<AwardHistory> mDatas) {
        super(view, mDatas, R.layout.listitem_award_history);
    }

    @Override
    public void convert(final AdapterHolder helper, AwardHistory item, boolean isScrolling, int position) {
        super.convert(helper, item, isScrolling, position);

        helper.setText(R.id.listitem_award_history_tv_time, item.getDateline());
        helper.setText(R.id.listitem_award_history_tv_title, item.getContent());
        helper.setText(R.id.listitem_award_history_coin, "+" + item.getCoin().toString());

        if(AppConfig.getInstance().getmPre().getBoolean("isNight", false))
            ((TextView)helper.getView(R.id.listitem_award_history_tv_title)).setTextColor(AppContext.appContext.getColor(R.color.night_text_1));
    }
}
