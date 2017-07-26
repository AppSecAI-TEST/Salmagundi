package org.caojun.salmagundi.today;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.socks.library.KLog;
import org.caojun.salmagundi.Constant;
import org.caojun.salmagundi.R;
import org.caojun.salmagundi.today.adapter.HistoryAdapter;
import org.caojun.salmagundi.today.ormlite.History;
import org.caojun.salmagundi.today.ormlite.HistoryDatabase;
import org.caojun.salmagundi.utils.TimeUtils;

import java.util.Calendar;
import java.util.List;

/**
 * Created by CaoJun on 2017/7/10.
 */

@Route(path = Constant.ACTIVITY_TODAY)
public class TodayActivity extends Activity {

    private GetBroadcastReceiver mGetBroadcastReceiver;
    private Intent mIntent;
    private ListView listView;
    private EditText etMonth, etDay;
    private HistoryAdapter adapter;
    private List<History> list;
    private String month, day;

    public class GetBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String result = intent.getStringExtra("result");
            JSONObject json = JSONObject.parseObject(result);
            KLog.json("onReceive", result);
            int status = json.getIntValue("status");
            if (status != 0) {
                Toast.makeText(TodayActivity.this, json.getString("msg"), Toast.LENGTH_LONG).show();
            } else {
                JSONArray array = json.getJSONArray("result");
                for (int i = 0;i < array.size();i ++) {
                    JSONObject object = (JSONObject) array.get(i);
                    String title = object.getString("title");
                    String year = object.getString("year");
                    String month = object.getString("month");
                    String day = object.getString("day");
                    String content = object.getString("content");
                    HistoryDatabase.getInstance(TodayActivity.this).insert(title, year, month, day, content);
                }
            }
            showList();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);

        listView = (ListView) findViewById(R.id.lvHistory);
        etMonth = (EditText) findViewById(R.id.etMonth);
        etDay = (EditText) findViewById(R.id.etDay);

        etMonth.setEnabled(false);
        etDay.setEnabled(false);

        Calendar calendar = TimeUtils.getCalendar();
        month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

        etMonth.setText(month);
        etDay.setText(day);

        if (!showList()) {
            mGetBroadcastReceiver = new GetBroadcastReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(TodayConstant.BroadcastAction);
            registerReceiver(mGetBroadcastReceiver, intentFilter);
            startGetService();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                History history = list.get(position);
                ARouter.getInstance().build(Constant.ACTIVITY_WEBVIEW).withString("content", history.getContent()).navigation();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (mIntent != null) {
            stopService(mIntent);
        }
        if (mGetBroadcastReceiver != null) {
            unregisterReceiver(mGetBroadcastReceiver);
        }
        super.onDestroy();
    }

    private void startGetService() {
        mIntent = new Intent(this, GetService.class);
        mIntent.putExtra("month", month);
        mIntent.putExtra("day", day);
        startService(mIntent);
    }

    private boolean showList() {
        boolean isNew = list == null || adapter == null;
        list = HistoryDatabase.getInstance(this).query(month, day);
        if(isNew) {
            adapter = new HistoryAdapter(this, list);
            listView.setAdapter(adapter);
        } else {
            adapter.setData(list);
            adapter.notifyDataSetChanged();
        }

        return list != null && !list.isEmpty();
    }
}
