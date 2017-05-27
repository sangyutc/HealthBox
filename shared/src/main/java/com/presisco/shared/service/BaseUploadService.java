package com.presisco.shared.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.presisco.shared.data.BaseEvent;
import com.presisco.shared.data.BaseEventData;
import com.presisco.shared.data.EventSummary;
import com.presisco.shared.network.request.UploadEventsRequest;
import com.presisco.shared.utils.LCAT;

public abstract class BaseUploadService extends Service implements Response.Listener<String>, Response.ErrorListener {
    private static final String ACTION_UPLOAD_EVENTS = "com.presisco.shared.service.action.UPLOAD_EVENTS";
    private AnalyzeStuffListener mAnalyzeStuffListener;
    private long event_baseline_id;
    public BaseUploadService() {

    }

    protected void setAnalyzeTaskListener(AnalyzeStuffListener listener) {
        mAnalyzeStuffListener = listener;
    }

    @Override
    public abstract void onCreate();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 开始分析与上传任务
     */
    protected void startTask() {
        event_baseline_id = mAnalyzeStuffListener.loadBaselineID();
        new AnalyzeTask().execute(event_baseline_id);
    }

    private void startUpload(EventSummary[] summaries) {
        UploadEventsRequest request = new UploadEventsRequest(summaries, this, this);
        Volley.newRequestQueue(this).add(request);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if (error.getMessage() != null) {
            LCAT.d(this, error.getMessage());
        } else {
            LCAT.d(this, "unknown failure");
        }
        stopSelf();
    }

    @Override
    public void onResponse(String response) {
        if (!response.contains("succeed")) {
            LCAT.d(this, response);
        } else {
            LCAT.d(this, "upload succeed");
            mAnalyzeStuffListener.updateBaselineID(event_baseline_id);
        }
        stopSelf();
    }

    public interface AnalyzeStuffListener<EVENT, EVENT_DATA> {

        /**
         * 读取要分析的第一个活动的id
         *
         * @return
         */
        long loadBaselineID();

        /**
         * 读取活动列表
         *
         * @param baseline_id 要分析的活动的id
         * @return 活动列表
         */
        EVENT[] loadEvents(long baseline_id);

        /**
         * 读取活动数据
         *
         * @param event_id 要分析的活动的id
         * @return 活动数据
         */
        EVENT_DATA[] loadEventData(long event_id);

        /**
         * 对数据进行分析
         *
         * @param event          活动信息
         * @param event_data_set 活动数据
         * @return 活动总结
         */
        EventSummary analyze(EVENT event, EVENT_DATA[] event_data_set);

        /**
         * 更新要分析的活动的id
         *
         * @param new_id 新的baseline id
         */
        void updateBaselineID(long new_id);
    }

    private class AnalyzeTask extends AsyncTask<Long, Void, EventSummary[]> {
        @Override
        protected void onPostExecute(EventSummary[] summaries) {
            if (summaries.length > 0) {
                event_baseline_id += summaries.length;
                startUpload(summaries);
            }
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected EventSummary[] doInBackground(Long... params) {
            BaseEvent[] events = (BaseEvent[]) mAnalyzeStuffListener.loadEvents(params[0]);
            EventSummary[] summaries = new EventSummary[events.length];
            for (int i = 0; i < events.length; ++i) {
                BaseEventData[] eventDataSet = (BaseEventData[]) mAnalyzeStuffListener.loadEventData(events[i].id);
                summaries[i] = mAnalyzeStuffListener.analyze(events[i], eventDataSet);
            }
            return summaries;
        }
    }
}
