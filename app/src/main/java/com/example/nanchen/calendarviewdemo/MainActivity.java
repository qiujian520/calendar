package com.example.nanchen.calendarviewdemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nanchen.calendarviewdemo.view.DateUtils;
import com.example.nanchen.calendarviewdemo.view.FormatDate;
import com.example.nanchen.calendarviewdemo.view.MyCalendarView;
import com.example.nanchen.calendarviewdemo.view.ClickDataListener;
import com.example.nanchen.calendarviewdemo.view.UserDBHelper;
import com.example.nanchen.calendarviewdemo.view.UserInfo;
import com.youcash.calendardemo.CalendarSelector;
import com.youcash.calendardemo.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * @author nanchen
 * @date 2016-08-11 10:48:21
 */
public class MainActivity extends Activity implements CalendarSelector.ICalendarSelectorCallBack {
    private TextView tv;
    private CalendarSelector mCalendarSelector;
    private MyCalendarView myCalendarView;
    SharedPreferences sharedPref;
    RelativeLayout layout;
    String mname = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layout = (RelativeLayout) findViewById(R.id.layout);
        sharedPref = this.getSharedPreferences("qiujian", this.MODE_PRIVATE);
        myCalendarView = (MyCalendarView) findViewById(R.id.main_calendar);
        myCalendarView.setClickDataListener(new ClickDataListener() {
            @Override
            public void onClickData(int year, int month, int day) {
                String date = String.format(Locale.CHINA, "%04d年%02d月%02d日", year, month, day);
                Toast.makeText(MainActivity.this, date, Toast.LENGTH_SHORT).show();
            }
        });

        tv = (TextView) findViewById(R.id.datetext);
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(FormatDate.DATE_FORMAT, Locale.CHINA);
        String nowDate = sdf.format(date);
        int year_c = Integer.parseInt(nowDate.split("-")[0]);
        int month_c = Integer.parseInt(nowDate.split("-")[1]);
        int day_c = Integer.parseInt(nowDate.split("-")[2]);
        mCalendarSelector = new CalendarSelector(this, year_c - 1901, month_c, day_c, this);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amend();
            }
        });
        findViewById(R.id.btn_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserDBHelper userDBHelper = UserDBHelper.getInstance(MainActivity.this, 0);
                userDBHelper.openReadLink();
                userDBHelper.deleteAll();
                while (layout.getChildCount()>0) {
                    layout.removeViewAt(layout.getChildCount() - 1);
                }
                initView();
            }
        });
        initView();
    }

    public void amend() {
        LayoutInflater factory = LayoutInflater.from(MainActivity.this);
        View textEntryView = factory.inflate(R.layout.myedit_amend, null);
        final EditText mname_edit = (EditText) textEntryView
                .findViewById(R.id.rename_edit);
        AlertDialog.Builder dialog = null;
        dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("请添加名字");
        dialog.setMessage("添加名字之后可以选择日期");
        dialog.setCancelable(true);
        dialog.setView(textEntryView);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mname_edit.getText() != null) {
                    MainActivity.this.mname = String.valueOf(mname_edit.getText());

                    mCalendarSelector.show(tv);
                }
            }
        });

        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        dialog.show();


    }


    public void initView() {
        String text = sharedPref.getString(this.getString(R.string.date_1), "");
        UserDBHelper userDBHelper = UserDBHelper.getInstance(this, 0);
        userDBHelper.openReadLink();
        ArrayList<UserInfo> userInfos = userDBHelper.queryAll();
        Log.i("-----------------", "initView: " + userInfos.size());
        if (userInfos.size() != 0) {
            Random rnd = new Random();
            int prevTextViewId = 0;

            for (int i = 0; i < userInfos.size(); i++) {

                final TextView textView = new TextView(this);

                textView.setText(userInfos.get(i).getName() + userInfos.get(i).getText());

                textView.setTextColor(rnd.nextInt() | 0xff000000);

                int curTextViewId = prevTextViewId + 1;

                textView.setId(curTextViewId);

                final RelativeLayout.LayoutParams params =

                        new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                params.addRule(RelativeLayout.BELOW, prevTextViewId);

                textView.setLayoutParams(params);

                prevTextViewId = curTextViewId;
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                layout.addView(textView, params);

            }
            Log.i("xxxxxxx", "initView: " + userInfos.get(0));
//            tv.setText(userInfos.get(0).getText());
        }

    }

    @Override
    public void transmitPeriod(HashMap<String, String> result) {
        String ar_y = result.get("year");
        String ar_m = result.get("month");
        String ar_d = result.get("day");
        int lu_m = 0;
        int lu_d = 0;
        HashMap<String, Object> mMap;

        for (int i = 0; i <= 12; i++) {
            if ((i + "月").equals(ar_m)) {
                lu_m = i - 1;
            }
        }
        for (int i = 0; i <= 31; i++) {
            if ((i + "日").equals(ar_d)) {
                lu_d = i - 1;
            }
        }
        Log.i("xxxxxx", "transmitPeriod: " + lu_m + ":" + lu_d);

        //转换为阴历
        mMap = Utils.average2Lunar(result.get("year"), lu_m, lu_d);
        int monthpo = (int) mMap.get("monthPosition");
        int daythpo = (int) mMap.get("dayPosition");
        String mmonth = ((List<String>) mMap.get("month")).get(monthpo);
        String mday = ((List<List<String>>) mMap.get("day")).get(monthpo).get(daythpo);
        Log.i("xxxxxxx", "transmitPeriod: " + mmonth + ":" + mday);
        String mtext = result.get("year") + "" + result.get("month") + "" + result.get("day") + ":" + mmonth + "-" + mday + "----还有：" + DateUtils.getdate(mmonth, mday) + "天生日";
        tv.setText(mtext);

        //今年的阳历
        HashMap<String, Object> mMap_yang;
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(FormatDate.DATE_FORMAT, Locale.CHINA);
        String nowDate = sdf.format(date);
        int year_c = Integer.parseInt(nowDate.split("-")[0]);
        mMap_yang = Utils.lunar2Average(year_c + "年", monthpo, daythpo);
        int monthpo_1 = (int) mMap_yang.get("monthPosition");
        int daythpo_1 = (int) mMap_yang.get("dayPosition");
        Log.i("xxxxxxxxxx-------", "transmitPeriod: " + monthpo_1 + ":" + daythpo_1);

//        DateUtils.setBirDate(this,mtext,result.get("year"),monthpo_1,daythpo_1,1);
        UserDBHelper userDBHelper = UserDBHelper.getInstance(this, 0);
        userDBHelper.openReadLink();
        ArrayList<UserInfo> cont = userDBHelper.queryAll();
        userDBHelper.openWriteLink();
        ArrayList<UserInfo> infoList = new ArrayList<>();
        UserInfo userInfo = new UserInfo();
        if(cont.size() >0) {
            Log.i("-------------", "transmitPeriod: " + ((cont.get(cont.size() - 1)).getRowid() + 1) + ":" + cont.size());
            userInfo.setRowid((cont.get(cont.size() - 1)).getRowid() + 1);
        }else {
            userInfo.setRowid(0);

        }
        userInfo.setText(mtext);
        userInfo.setYear(result.get("year"));
        userInfo.setMonth(monthpo_1);
        userInfo.setDay(daythpo_1);
        userInfo.setName(mname);
        infoList.add(userInfo);
        long result1 = userDBHelper.insert(infoList);
        myCalendarView.initView();
        initView();
    }
}
