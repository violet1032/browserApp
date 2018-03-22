package com.zp.browser.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.zp.browser.R;

import org.kymjs.kjframe.KJDB;
import org.kymjs.kjframe.KJDB.DbUpdateListener;

/**
 * <p/>
 * description:
 * <p/>
 * author:zipeng
 * <p/>
 * createTime:2015/9/14 20:47
 * <p/>
 * version:1.0
 */
public class DBHelper {
    private final static int VERSION = 1;
    private static KJDB kjdb;

    private static DbUpdateListener dbUpdateListener = new DbUpdateListener() {
        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            // 数据库版本更新
            /*
            版本 2
             */
        }
    };

    public static KJDB getInstance(Context context) {
        if (kjdb == null) {
//            // 判断是否保存有数据库名，
//            String dbName = AppConfig.getInstance ().getmPre ().getString ("daName", null);
//            if (StringUtils.isEmpty (dbName)) {
//                // 如果没有保存，那么代表第一次运行app,那么生成一个唯一的数据库名，
//                // 之后如果用户登录，那么这个数据库名将和账号绑定
//                long time = System.currentTimeMillis ();
//                dbName = context.getString (R.string.app_name_en) + "_"+time + ".db";
//                AppConfig.getInstance ().mPreSet ("dbName", dbName);
//            }
            String dbName = context.getString(R.string.app_name_en) + ".db";
            kjdb = kjdb.create(context, dbName
                    , true,
                    VERSION, dbUpdateListener);

        }
        return kjdb;
    }
}
