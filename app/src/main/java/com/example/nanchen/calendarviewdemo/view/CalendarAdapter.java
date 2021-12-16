package com.example.nanchen.calendarviewdemo.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.nanchen.calendarviewdemo.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 日历GridView中的每一个item显示的文本
 *
 * @author nanchen
 * @date  2016-08-11 10:14:52
 */
public class CalendarAdapter extends BaseAdapter {
	private boolean isLeapYear = false; // 是否为闰年
	private int daysOfMonth = 0; // 某月的天数
	private int dayOfWeek = 0; // 具体某一天是星期几
	private int lastDaysOfMonth = 0; // 上一个月的总天数
	private Context context;
	private String[] dayNumber = new String[42]; // 一个GridView中的日期存入此数组中，因为一般日历都是6行7列
	private SpecialCalendar sc = null;
	private LunarCalendar lc = null;
	private Resources res = null;
	private int currentYear = 0;
	private int currentMonth = 0;
	private int currentDay = 0;

	private SimpleDateFormat sdf = new SimpleDateFormat(FormatDate.DATE_FORMAT, Locale.CHINA);//格式化日期格式
	private String showYear = ""; // 用于在头部显示的年份
	private String showMonth = ""; // 用于在头部显示的月份
	private String animalsYear = "";
	private String leapMonth = ""; // 闰哪一个月
	private String cyclical = ""; // 天干地支
	// 系统当前时间
	private String sysDate = "";
	private String sys_year = "";
	private String sys_month = "";
	private String sys_day = "";
	private int colorDataPosition = -1;
	private int colorDataPosition_bir = -1;
	SharedPreferences sharedPref;

	private boolean isClickData = false;

	public CalendarAdapter() {
		Date date = new Date();
		sysDate = sdf.format(date); // 当期日期
		sys_year = sysDate.split("-")[0];
		sys_month = sysDate.split("-")[1];
		sys_day = sysDate.split("-")[2];

	}

	public CalendarAdapter(Context context, Resources rs, int jumpMonth,
			int jumpYear, int year_c, int month_c, int day_c) {
		this();
		this.context = context;
		sc = new SpecialCalendar();
		lc = new LunarCalendar();
		this.res = rs;
		sharedPref = context.getSharedPreferences("qiujian",context.MODE_PRIVATE);

		int stepYear = year_c + jumpYear;
		int stepMonth = month_c + jumpMonth;
		if (stepMonth > 0) {
			// 往下一个月滑动
			if (stepMonth % 12 == 0) {
				stepYear = year_c + stepMonth / 12 - 1;
				stepMonth = 12;
			} else {
				stepYear = year_c + stepMonth / 12;
				stepMonth = stepMonth % 12;
			}
		} else {
			// 往上一个月滑动
			stepYear = year_c - 1 + stepMonth / 12;
			stepMonth = stepMonth % 12 + 12;
			if (stepMonth % 12 == 0) {

			}
		}

		currentYear = stepYear; // 得到当前的年份
		currentMonth = stepMonth; // 得到本月
													// （jumpMonth为滑动的次数，每滑动一次就增加一月或减一月）
		currentDay = day_c; // 得到当前日期是哪天

		getCalendar(currentYear, currentMonth);

	}

	public CalendarAdapter(Context context, Resources rs, int year, int month,
			int day) {
		this();
		this.context = context;
		sc = new SpecialCalendar();
		lc = new LunarCalendar();
		this.res = rs;
		currentYear = year;// 得到跳转到的年份
		currentMonth = month; // 得到跳转到的月份
		currentDay = day; // 得到跳转到的天
		getCalendar(currentYear, currentMonth);
	}

	@Override
	public int getCount() {
		return dayNumber.length;
	}

	@Override
	public Object getItem(int position) {
		return dayNumber[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.calen_calendar_item, parent,false);
		}
		TextView textView = (TextView) convertView.findViewById(R.id.tvtext);
		String d = dayNumber[position].split("\\.")[0];
		String dv = dayNumber[position].split("\\.")[1];

		SpannableString sp = new SpannableString(d + "\n" + dv);
		sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0,
				d.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		sp.setSpan(new RelativeSizeSpan(1.2f), 0, d.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		if (dv != null || !TextUtils.isEmpty(dv)) {
			sp.setSpan(new RelativeSizeSpan(0.75f), d.length() + 1,
					dayNumber[position].length(),
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		textView.setText(sp);
		textView.setTextColor(Color.GRAY);

		if (position < daysOfMonth + dayOfWeek && position >= dayOfWeek) {
			// 当前月信息显示
			textView.setTextColor(Color.BLACK);// 当月字体设黑
			// drawable = new ColorDrawable(Color.rgb(23, 126, 214));
			if (position % 7 == 0 || position % 7 == 6) {
				// 当前月信息显示
				textView.setTextColor(Color.rgb(23, 126, 214));// 当月字体设黑
			}
		}



		if (colorDataPosition == position) {
			// 设置当天的背景
			textView.setTextColor(Color.WHITE);

			textView.setBackgroundResource(R.drawable.bg_circle);
		} else {
			textView.setBackgroundColor(res.getColor(android.R.color.transparent));
		}
		if (colorDataPosition_bir == position) {
			// 设置当天的背景
			textView.setTextColor(Color.WHITE);

			textView.setBackgroundResource(R.drawable.bg_circle);
		}
		return convertView;
	}

	// 得到某年的某月的天数且这月的第一天是星期几
	private void getCalendar(int year, int month) {
		isLeapYear = sc.isLeapYear(year); // 是否为闰年
		daysOfMonth = sc.getDaysOfMonth(isLeapYear, month); // 某月的总天数
		dayOfWeek = sc.getWeekdayOfMonth(year, month); // 某月第一天为星期几
		lastDaysOfMonth = sc.getDaysOfMonth(isLeapYear, month - 1); // 上一个月的总天数
		getweek(year, month);
	}

	// 将一个月中的每一天的值添加入数组dayNuber中
	private void getweek(int year, int month) {
		int j = 1;
		String lunarDay = "";
		// 得到当前月的所有日程日期(这些日期需要标记)
		for (int i = 0; i < dayNumber.length; i++) {
			if (i < dayOfWeek) { // 前一个月
				int temp = lastDaysOfMonth - dayOfWeek + 1;
				lunarDay = lc.getLunarDate(year, month - 1, temp + i, false);
				dayNumber[i] = (temp + i) + "." + lunarDay;

			} else if (i < daysOfMonth + dayOfWeek) { // 本月
				String day = String.valueOf(i - dayOfWeek + 1); // 得到的日期
				lunarDay = lc.getLunarDate(year, month, i - dayOfWeek + 1,
						false);
				dayNumber[i] = i - dayOfWeek + 1 + "." + lunarDay;
				// 对于当前月才去标记当前日期
				if (sys_year.equals(String.valueOf(year))
						&& sys_month.equals(String.valueOf(month))
						&& sys_day.equals(day)) {
					// 标记当前日期
					Log.i("xxxxx", "getweek: "+sys_month+":"+sys_day+":"+month+":"+day);
					colorDataPosition = i;
				}
				String m_a;
				int m_b;
				int m_c;
				m_a = sharedPref.getString(context.getString(R.string.year_m),"");
				m_b = sharedPref.getInt(context.getString(R.string.mon_m),0);
				m_c = sharedPref.getInt(context.getString(R.string.day_m),0);
				if ((m_b+1)==month && (m_c+1+"").equals(day)) {
					// 标记当前日期
					Log.i("xxxxxxxx", "getweek: "+month+":"+day);
					colorDataPosition_bir = i;
				}


				setShowYear(String.valueOf(year));
				setShowMonth(String.valueOf(month));
				setAnimalsYear(lc.animalsYear(year));
				setLeapMonth(lc.leapMonth == 0 ? "" : String
						.valueOf(lc.leapMonth));
				setCyclical(lc.cyclical(year));
			} else { // 下一个月
				lunarDay = lc.getLunarDate(year, month + 1, j, false);
				dayNumber[i] = j + "." + lunarDay;
				j++;
			}
		}

		String abc = "";
		for (int i = 0; i < dayNumber.length; i++) {
			abc = abc + dayNumber[i] + ":";
		}
		Log.d("DAYNUMBER", abc);

	}

	/**
	 * 点击每一个item时返回item中的日期
	 * 
	 * @param position
	 * @return
	 */
	public String getDateByClickItem(int position) {
		return dayNumber[position];
	}

	/**
	 * 在点击gridView时，得到这个月中第一天的位置
	 * 
	 * @return
	 */
	public int getStartPositon() {
		return dayOfWeek + 7;
	}

	/**
	 * 在点击gridView时，得到这个月中最后一天的位置
	 * 
	 * @return
	 */
	public int getEndPosition() {
		return (dayOfWeek + daysOfMonth + 7) - 1;
	}

	public String getShowYear() {
		return showYear;
	}

	public void setShowYear(String showYear) {
		this.showYear = showYear;
	}

	public String getShowMonth() {
		return showMonth;
	}

	public void setShowMonth(String showMonth) {
		this.showMonth = showMonth;
	}

	public String getAnimalsYear() {
		return animalsYear;
	}

	public void setAnimalsYear(String animalsYear) {
		this.animalsYear = animalsYear;
	}

	public String getLeapMonth() {
		return leapMonth;
	}

	public void setLeapMonth(String leapMonth) {
		this.leapMonth = leapMonth;
	}

	public String getCyclical() {
		return cyclical;
	}

	public void setCyclical(String cyclical) {
		this.cyclical = cyclical;
	}

	/**
	 * @param position
	 *            设置点击的日期的颜色位置
	 */
	public void setColorDataPosition(int position) {
		if (position >= dayOfWeek && position < daysOfMonth + dayOfWeek) {
			colorDataPosition = position;
			notifyDataSetChanged();
		}
	}
}
