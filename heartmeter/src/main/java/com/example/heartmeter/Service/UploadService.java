package com.example.heartmeter.Service;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.heartmeter.Data.Event;
import com.example.heartmeter.Data.EventData;
import com.example.heartmeter.storage.SQLiteManager;
import com.presisco.shared.data.EventSummary;
import com.presisco.shared.service.BaseUploadService;


//定义成员变量
public class UploadService extends BaseUploadService
        implements BaseUploadService.AnalyzeStuffListener<Event, EventData> {
    private SQLiteManager mDataManager;
    private String username;
    private SharedPreferences mPreferences;

    @Override
    public void onCreate() {
        //链接数据库
        mDataManager = new SQLiteManager(this);
        //设置
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //用户名
        username = mPreferences.getString("username", "");
        setAnalyzeTaskListener(this);
        startTask();
    }

    /**
     * 读取要分析的活动的id
     *
     * @return
     */
    @Override
    public long loadBaselineID() {
        return mPreferences.getLong("baseline_id", 1);
    }

    /**
     * 读取活动列表
     *
     * @param baseline_id 要分析的活动的id
     * @return 活动列表
     */
    @Override
    public Event[] loadEvents(long baseline_id) {
        Event[] events = mDataManager.getEventsAfter(baseline_id);
        if (events == null) {
            events = new Event[0];
        }
        return events;
    }

    /**
     * 读取活动数据
     *
     * @param event_id 要分析的活动的id
     * @return 活动数据
     */
    @Override
    public EventData[] loadEventData(long event_id) {
        return mDataManager.getAllDataInEvent(event_id);
    }

    private EventSummary analyzeDefault(Event event, EventData[] event_data_set) {
        EventSummary summary = new EventSummary();

        summary.username = username;
        summary.event_type = event.type;
        String converted_datetime = event.start_time.replace("-", " ");
        converted_datetime = converted_datetime.replace("/", "-");
        summary.date = converted_datetime + ":00";

        int data_sum = 0;
        for (EventData data : event_data_set) {
            data_sum += data.heart_rate;
        }
        summary.average_stats = data_sum / event_data_set.length;
        summary.duration = event_data_set.length;
        summary.evaluation = "not available";

        return summary;
    }

    private EventSummary analyzeAerobic(Event event, EventData[] event_data_set) {
        EventSummary summary = new EventSummary();

        summary.username = username;
        summary.event_type = event.type;
        String converted_datetime = event.start_time.replace("-", " ");
        converted_datetime = converted_datetime.replace("/", "-");
        summary.date = converted_datetime + ":00";

        int data_sum = 0;
        for (EventData data : event_data_set) {
            data_sum += data.heart_rate;
        }
        summary.average_stats = data_sum / event_data_set.length;
        summary.duration = event_data_set.length;
        summary.evaluation = "not available";

        return summary;
    }

    private EventSummary analyzeAnaerobic(Event event, EventData[] event_data_set) {
        EventSummary summary = new EventSummary();
        //用户名
        summary.username = username;
        //区分四种事件类型
        summary.event_type = event.type;
        String converted_datetime = event.start_time.replace("-", " ");
        converted_datetime = converted_datetime.replace("/", "-");
        //由本地存储格式转换为服务器格式的时间
        summary.date = converted_datetime + ":00";

        int data_sum = 0;
        for (EventData data : event_data_set) {
            data_sum += data.heart_rate;
        }
        //上传平均心率
        summary.average_stats = data_sum / event_data_set.length;
        //上传时间时长
        summary.duration = event_data_set.length;
        //自我评估
        summary.evaluation = "not available";

        return summary;
    }

    private EventSummary analyzeSleep(Event event, EventData[] event_data_set) {
        EventSummary summary = new EventSummary();

        summary.username = username;
        summary.event_type = event.type;
        String converted_datetime = event.start_time.replace("-", " ");
        converted_datetime = converted_datetime.replace("/", "-");
        summary.date = converted_datetime + ":00";

        int data_sum = 0;
        for (EventData data : event_data_set) {
            data_sum += data.heart_rate;
        }
        summary.average_stats = data_sum / event_data_set.length;
        summary.duration = event_data_set.length;
        summary.evaluation = "not available";

        return summary;
    }

    /**
     * 对数据进行分析
     *
     * @param event          活动信息
     * @param event_data_set 活动数据
     * @return 活动总结
     * 识别不同模式的数据进行上传
     */
    @Override
    public EventSummary analyze(Event event, EventData[] event_data_set) {
        EventSummary summary = null;
        switch (event.type) {
            case Event.TYPE_DEFAULT:
                summary = analyzeDefault(event, event_data_set);
                break;
            case Event.TYPE_AEROBIC:
                summary = analyzeAerobic(event, event_data_set);
                break;
            case Event.TYPE_ANAEROBIC:
                summary = analyzeAnaerobic(event, event_data_set);
                break;
            case Event.TYPE_SLEEP:
                summary = analyzeSleep(event, event_data_set);
                break;
        }
        //体征
        summary.body_sign = "heart_rate";
        return summary;
    }

    /**
     * 更新要分析的活动的id
     *
     * @param new_id 新的baseline id
     */
    @Override
    public void updateBaselineID(long new_id) {
        mPreferences.edit()
                .putLong("baseline_id", new_id)
                .commit();
    }
}
