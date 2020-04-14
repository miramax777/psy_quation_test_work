package com.agregate.sensors;

import java.io.Serializable;

public class SensorAverageData implements Serializable {

    private String timeSlotStart;
    private String location;
    private String tempMin;
    private String tempMax;
    private String tempAvg;
    private String tempCnt;
    private Boolean presence;
    private String presenceCnt;

    public SensorAverageData() {
    }

    public SensorAverageData(
            String timeSlotStart,
            String location,
            String tempMin,
            String tempMax,
            String tempAvg,
            String tempCnt,
            Boolean presence,
            String presenceCnt
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

    public String getTimeSlotStart() {
        return timeSlotStart;
    }

    public void setTimeSlotStart(String timeSlotStart) {
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

    public String getTempCnt() {
        return tempCnt;
    }

    public void setTempCnt(String tempCnt) {
        this.tempCnt = tempCnt;
    }

    public Boolean isPresence() {
        return presence;
    }

    public void setPresence(boolean presence) {
        this.presence = presence;
    }

    public String getPresencecnt() {
        return presenceCnt;
    }

    public void setPresencecnt(String presenceCnt) {
        this.presenceCnt = presenceCnt;
    }
}
