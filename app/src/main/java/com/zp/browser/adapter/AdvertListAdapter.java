package com.zp.browser.adapter;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;

import com.zp.browser.R;
import com.zp.browser.api.ApiCommon;
import com.zp.browser.bean.Advert;

import org.kymjs.kjframe.widget.AdapterHolder;
import org.kymjs.kjframe.widget.KJAdapter;

import java.util.Collection;

/**
 * Created by Administrator on 2018/3/19 0019.
 */
public class AdvertListAdapter extends KJAdapter<Advert> {

    private Handler handler;

    public AdvertListAdapter(AbsListView view, Collection<Advert> mDatas,Handler handler) {
        super(view, mDatas, R.layout.listitem_advert);
        this.handler = handler;
    }

    @Override
    public void convert(AdapterHolder helper, final Advert item, boolean isScrolling, int position) {
        super.convert(helper, item, isScrolling, position);

        helper.setText(R.id.listitem_advert_tv, item.getName());
        ApiCommon.getNetBitmap(item.getIcon(), (ImageView) helper.getView(R.id.listitem_advert_img), false);

        helper.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message message = new Message();
                message.what = 101;
                message.obj = item;
                handler.sendMessage(message);
            }
        });
    }
}
