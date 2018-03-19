package com.zp.browser.adapter;

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

    public AdvertListAdapter(AbsListView view, Collection<Advert> mDatas) {
        super(view, mDatas, R.layout.listitem_advert);
    }

    @Override
    public void convert(AdapterHolder helper, Advert item, boolean isScrolling, int position) {
        super.convert(helper, item, isScrolling, position);

        helper.setText(R.id.listitem_advert_tv, item.getName());
        ApiCommon.getNetBitmap(item.getIcon(), (ImageView) helper.getView(R.id.listitem_advert_img), false);

        helper.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
