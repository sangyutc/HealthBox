package com.presisco.shared.data;

/**
 * Created by presisco on 2017/5/11.
 */

public class EventSummary {
    public String username;
    public String body_sign;
    public String date;
    public String event_type;
    public int duration;
    public int average_stats;
    public String evaluation;

    public EventSummary() {

    }

    public EventSummary(String _username, String _body_sign, String _date, String _type, int _duration, int _average_stats, String _evaluation) {
        username = _username;
        body_sign = _body_sign;
        date = _date;
        event_type = _type;
        duration = _duration;
        average_stats = _average_stats;
        evaluation = _evaluation;
    }

    public String getBody_sign() {
        return body_sign;
    }

    public String getUsername() {
        return username;
    }

    public String getDate() {
        return date;
    }

    public String getEvent_type() {
        return event_type;
    }

    public int getDuration() {
        return duration;
    }

    public int getAverage_stats() {
        return average_stats;
    }

    public String getEvaluation() {
        return evaluation;
    }
}
