package com.agregate.sensors;

import java.sql.Timestamp;

public class SensorData {

    private String sensor_id_info;
    private String channel_id;
    private String channel_type;
    private String location_id;
    private Timestamp timestamp;
    private String value;

    public SensorData() {
    }

    public SensorData(
            String sensor_id,
            String channel_id,
            String channel_type,
            String location_id,
            Timestamp timestamp,
            String value
    ) {
        this.sensor_id_info = sensor_id;
        this.channel_id = channel_id;
        this.channel_type = channel_type;
        this.location_id = location_id;
        this.timestamp = timestamp;
        this.value = value;
    }

    public String getSensor_id_info() {
        return sensor_id_info;
    }

    public void setSensor_id_info(String sensor_id_info) {
        this.sensor_id_info = sensor_id_info;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public String getChannel_type() {
        return channel_type;
    }

    public void setChannel_type(String channel_type) {
        this.channel_type = channel_type;
    }

    public String getLocation_id() {
        return location_id;
    }

    public void setLocation_id(String location_id) {
        this.location_id = location_id;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "SensorData{" +
                "sensor_id_info='" + sensor_id_info + '\'' +
                ", channel_id='" + channel_id + '\'' +
                ", channel_type='" + channel_type + '\'' +
                ", location_id='" + location_id + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
