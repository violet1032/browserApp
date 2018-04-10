package com.zp.browser.adapter;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.zp.browser.AppConfig;
import com.zp.browser.AppContext;
import com.zp.browser.R;
import com.zp.browser.api.ApiCommon;
import com.zp.browser.db.Model.ScanHistory;

import org.kymjs.kjframe.widget.AdapterHolder;
import org.kymjs.kjframe.widget.KJAdapter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/4/1 0001.
 */
public class HistoryListAdapter extends KJAdapter<ScanHistory> {

    private Handler handler;

    public boolean isAll;

    public boolean isEdit;

    private Map<Integer, Integer> map = new HashMap<>();

    public HistoryListAdapter(AbsListView view, Collection<ScanHistory> mDatas, Handler handler) {
        super(view, mDatas, R.layout.listitem_collect_history);
        this.handler = handler;
    }

    @Override
    public void convert(final AdapterHolder helper, final ScanHistory item, boolean isScrolling, int position) {
        super.convert(helper, item, isScrolling, position);

        helper.setText(R.id.listitem_collect_history_tv_title, item.getTitle());
        helper.setText(R.id.listitem_collect_history_tv_url, item.getDomain());

        ApiCommon.getNetBitmap(item.getIcon(), (ImageView) helper.getView(R.id.listitem_collect_history_img), R.drawable.internet);

        if (AppConfig.getInstance().getmPre().getBoolean("isNight", false)) {
            ((TextView) helper.getView(R.id.listitem_collect_history_tv_title)).setTextColor(AppContext.appContext.getResources().getColor(R.color.night_text_1));
            ((TextView) helper.getView(R.id.listitem_collect_history_tv_url)).setTextColor(AppContext.appContext.getResources().getColor(R.color.night_text_1));
        } else {
            ((TextView) helper.getView(R.id.listitem_collect_history_tv_title)).setTextColor(AppContext.appContext.getResources().getColor(R.color.black));
            ((TextView) helper.getView(R.id.listitem_collect_history_tv_url)).setTextColor(AppContext.appContext.getResources().getColor(R.color.gray));
        }

        if (isEdit) {
            CheckBox cbox = helper.getView(R.id.listitem_collect_history_cbox);
            helper.getView(R.id.listitem_collect_history_cbox).setVisibility(View.VISIBLE);
            if (isAll)
                cbox.setChecked(true);
            else
                cbox.setChecked(false);
            cbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b)
                        map.put(item.getId(), item.getId());
                    else
                        map.remove(item.getId(), item.getId());
                }
            });
        } else
            helper.getView(R.id.listitem_collect_history_cbox).setVisibility(View.GONE);

        if (isEdit)
            helper.getConvertView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckBox cbox = helper.getView(R.id.listitem_collect_history_cbox);
                    cbox.setChecked(!cbox.isChecked());
                }
            });
        else
            helper.getConvertView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Message message = new Message();
                    message.what = 2;
                    message.obj = item;
                    handler.sendMessage(message);
                }
            });

        helper.getConvertView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                handler.sendEmptyMessage(3);
                isEdit = true;
                notifyDataSetChanged();
                return false;
            }
        });
    }

    public Map<Integer, Integer> getChecked() {
        return map;
    }
}
