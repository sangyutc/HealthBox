package com.presisco.shared.data;

/**
 * Created by presisco on 2017/5/11.
 */

public class EventSummary {
    private String date;
    private String type;
    private int duration;
    private int averageStats;
    private String evaluation;

    public EventSummary(String _date, String _type, int _duration, int _average_stats, String _evaluation) {
        date = _date;
        type = _type;
        duration = _duration;
        averageStats = _average_stats;
        evaluation = _evaluation;
    }

    public String getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public int getDuration() {
        return duration;
    }

    public int getAverageStats() {
        return averageStats;
    }

    public String getEvaluation() {
        return evaluation;
    }
}
