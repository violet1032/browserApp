package com.zp.browser.ui.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.zp.browser.R;
import com.zp.browser.utils.StringUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by 10532 on 2018/2/12.
 */
public class DataTimePicker extends Activity {

    public static final String SELECT_DATE_ACTION = "select_date_action";
    public static final String SELECT__ACTION = "select__action";

    private int years;
    private int hourse;
    private int minites;
    private int month;
    private int days;

    private DatePicker datePicker;
    private TimePicker timePicker;
    private int type = 0;

    private String date;

    private Button btnSure;

    public static void startActivity(Activity activity, String date, int type) {
        Intent intent = new Intent();
        intent.setClass(activity, DataTimePicker.class);
        intent.putExtra("date", date);
        intent.putExtra("type", type);
        activity.startActivityForResult(intent, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datatimepicker);

        btnSure = findViewById(R.id.picker_btn_sure);
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SELECT_DATE_ACTION);
                intent.putExtra("years",years);
                intent.putExtra("months",month);
                intent.putExtra("days",days);
                intent.putExtra("type",type);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            date = intent.getStringExtra("date");
            type = intent.getIntExtra("type", 0);
        }

        long lDate = System.currentTimeMillis();
        if (!StringUtils.isEmpty(date))
            lDate = StringUtils.date_fromat_change_4(date);

        datePicker = findViewById(R.id.dpPicker);
        timePicker = findViewById(R.id.tpPicker);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(lDate));

        years = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        days = calendar.get(Calendar.DAY_OF_MONTH);

        if (type == 0) {
            datePicker.setVisibility(View.VISIBLE);
            timePicker.setVisibility(View.GONE);
            datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

                        @Override
                        public void onDateChanged(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                            // 获取一个日历对象，并初始化为当前选中的时间
                            years = year;
                            month = monthOfYear;
                            days = dayOfMonth;
                        }
                    });
        } else {
            datePicker.setVisibility(View.GONE);
            timePicker.setVisibility(View.VISIBLE);
            timePicker.setIs24HourView(true);
            timePicker
                    .setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                        @Override
                        public void onTimeChanged(TimePicker view, int hourOfDay,
                                                  int minute) {
                            Toast.makeText(DataTimePicker.this,
                                    hourOfDay + "小时" + minute + "分钟",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

}
