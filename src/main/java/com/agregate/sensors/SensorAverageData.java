package com.agregate.sensors;

import java.io.Serializable;
import java.time.LocalDateTime;

public class SensorAverageData implements Serializable {

    private LocalDateTime timeSlotStart;
    private String location;
    private String tempMin;
    private String tempMax;
    private String tempAvg;
    private long tempCnt;
    private boolean presence;
    private long presenceCnt;

    public SensorAverageData(
            LocalDateTime timeSlotStart,
            String location,
            String tempMin,
            String tempMax,
            String tempAvg,
            long tempCnt,
            boolean presence,
            long presenceCnt
    ) {
        this.timeSlotStart = timeSlotStart;
        this.location = location;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.tempAvg = tempAvg;
        this.tempCnt = tempCnt;
        this.presence = presence;
        this.presenceCnt = presenceCnt;
    }

    public LocalDateTime getTimeSlotStart() {
        return timeSlotStart;
    }

    public void setTimeSlotStart(LocalDateTime timeSlotStart) {
        this.timeSlotStart = timeSlotStart;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTempMin() {
        return tempMin;
    }

    public void setTempMin(String tempMin) {
        this.tempMin = tempMin;
    }

    public String getTempMax() {
        return tempMax;
    }

    public void setTempMax(String tempMax) {
        this.tempMax = tempMax;
    }

    public String getTempAvg() {
        return tempAvg;
    }

    public void setTempAvg(String tempAvg) {
        this.tempAvg = tempAvg;
    }

    public long getTempCnt() {
        return tempCnt;
    }

    public void setTempCnt(long tempCnt) {
        this.tempCnt = tempCnt;
    }

    public boolean isPresence() {
        return presence;
    }

    public void setPresence(boolean presence) {
        this.presence = presence;
    }

    public long getPresencecnt() {
        return presenceCnt;
    }

    public void setPresencecnt(long presenceCnt) {
        this.presenceCnt = presenceCnt;
    }
}
