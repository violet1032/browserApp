package com.zp.browser.adapter;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.zp.browser.AppConfig;
import com.zp.browser.AppContext;
import com.zp.browser.R;
import com.zp.browser.api.ApiCommon;
import com.zp.browser.db.Model.Collect;

import org.kymjs.kjframe.widget.AdapterHolder;
import org.kymjs.kjframe.widget.KJAdapter;

import java.util.Collection;

/**
 * Created by Administrator on 2018/4/1 0001.
 */
public class CollectHistoryListAdapter extends KJAdapter<Collect>{
    private Handler handler;

    public CollectHistoryListAdapter(AbsListView view, Collection<Collect> mDatas,Handler handler) {
        super(view, mDatas, R.layout.listitem_collect_history);
        this.handler = handler;
    }

    @Override
    public void convert(AdapterHolder helper, final Collect item, boolean isScrolling, int position) {
        super.convert(helper, item, isScrolling, position);

        helper.setText(R.id.listitem_collect_history_tv_title, item.getTitle());
        helper.setText(R.id.listitem_collect_history_tv_url, item.getDomain());

        ApiCommon.getNetBitmap(item.getIcon(), (ImageView) helper.getView(R.id.listitem_collect_history_img),R.drawable.internet);

        if (AppConfig.getInstance().getmPre().getBoolean("isNight", false)) {
            ((TextView)helper.getView(R.id.listitem_collect_history_tv_title)).setTextColor(AppContext.appContext.getResources().getColor(R.color.night_text_1));
            ((TextView)helper.getView(R.id.listitem_collect_history_tv_url)).setTextColor(AppContext.appContext.getResources().getColor(R.color.night_text_1));
        }else{
            ((TextView)helper.getView(R.id.listitem_collect_history_tv_title)).setTextColor(AppContext.appContext.getResources().getColor(R.color.black));
            ((TextView)helper.getView(R.id.listitem_collect_history_tv_url)).setTextColor(AppContext.appContext.getResources().getColor(R.color.gray));
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
    }
}
