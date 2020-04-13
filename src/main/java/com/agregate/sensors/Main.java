package com.agregate.sensors;

import org.apache.spark.sql.*;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.spark.sql.types.DataTypes.StringType;
import static org.apache.spark.sql.types.DataTypes.TimestampType;

public class Main {

    final static int SENSOR_DATA_REPORT_MINUTES_INTERVAL = 15;

    public static void main(String[] args) throws AnalysisException {

        System.setProperty("hadoop.home.dir", "C:\\hadoop\\");

        final SparkSession session = SparkSession.builder()
                .appName("PsyQuationAggregateApp")
                .master("local[3]")
                .getOrCreate();

        final StructType sensor_info_schema = new StructType()
                .add("sensor_id_info", StringType,false)
                .add("channel_id_info", StringType,false)
                .add("channel_type", StringType,false)
                .add("location_id", StringType,false);

        final StructType sensor_type_schema = new StructType()
                .add("sensor_id", StringType,true)
                .add("channel_id", StringType,true)
                .add("timestamp", TimestampType,true)
                .add("value", StringType,true);

        final Dataset<Row> sensors_info = session.read()
                .option("header", "false")
                .option("delimiter",",")
                .schema(sensor_info_schema)
                .csv("input/ds1.csv");

        final Dataset<Row> sensors_data = session.read()
                .option("header", "false")
                .option("delimiter",",")
                .schema(sensor_type_schema)
                .csv("input/ds2.csv");

        final Dataset<SensorData> result = sensors_info.join(
                sensors_data,
                sensors_info.col("sensor_id_info").equalTo(sensors_data.col("sensor_id"))
                        .and(sensors_info.col("channel_id_info").equalTo(sensors_data.col("channel_id"))),
                "inner"
        ).select(
                sensors_data.col("timestamp").cast(DataTypes.TimestampType),
                sensors_info.col("location_id"),
                sensors_info.col("sensor_id_info"),
                sensors_data.col("channel_id"),
                sensors_info.col("channel_type"),
                sensors_data.col("value")
        ).as(Encoders.bean(SensorData.class));

        final Set<String> locations = result.collectAsList().stream()
                .map(SensorData::getLocation_id)
                .collect(Collectors.toSet());

        //final LocalDateTime firstSensorDate = result.first().getTimestamp().toLocalDateTime();

        //final LocalDateTime normalizedFirstSensorDate = firstSensorDate.withMinute(SENSOR_DATA_REPORT_MINUTES_INTERVAL * (firstSensorDate.getMinute() / SENSOR_DATA_REPORT_MINUTES_INTERVAL));

        //result.createGlobalTempView("sensors_joined_result");

        locations.forEach(l -> {
//            session.sql("set sensor_date_from=" + Timestamp.valueOf(normalizedFirstSensorDate.withSecond(0)).toString());
//            session.sql("set sensor_date_to=" + Timestamp.valueOf(normalizedFirstSensorDate.plusMinutes(15).withSecond(0)).toString());
//            session.sql("set location=" + l);
//
//            final Dataset<Row> sensorDataInTimeSlot = session.sql(
//                    "SELECT timestamp, location_id, sensor_id_info, channel_id, channel_type, value " +
//                            "FROM global_temp.sensors_joined_result " +
//                            "WHERE location_id = '${location} ' " +
//                            "AND timestamp > '${sensor_date_from}' " +
//                            "AND timestamp < '${sensor_date_to}'"
//            );
//
//

            final Dataset<SensorData> sensorDataForLocation = result.filter(result.col("location_id").equalTo(l));

            final Timestamp sensorDateStart = sensorDataForLocation.first().getTimestamp();
            final Timestamp sensorDateEnd = sensorDataForLocation.sort(new Column("timestamp").desc()).first().getTimestamp();

            Timestamp sensorDataPeriodStart = Timestamp.valueOf(sensorDateStart.toLocalDateTime().withSecond(0));
            Timestamp sensorDataPeriodEnd = Timestamp.valueOf(
                    sensorDateStart
                            .toLocalDateTime()
                            .withMinute(SENSOR_DATA_REPORT_MINUTES_INTERVAL * (sensorDateStart.toLocalDateTime().getMinute() / SENSOR_DATA_REPORT_MINUTES_INTERVAL))
                            .plusMinutes(15)
                            .withSecond(0)
            );

            List<Timestamp> timeslots = Arrays.asList(sensorDataPeriodStart);

            sensorDataForLocation.foreach(sd -> {
                 if() {

                 } else {

                 }


            });


            sensorDataForLocation.foreach(d -> {
                
            });

            final Dataset<SensorData> sensorDataInTimeSlot = sensorDataForLocation
                    .filter(result.col("timestamp").$greater(sensorDataPeriodStart.toString()))
                    .filter(result.col("timestamp").$less(sensorDataPeriodEnd.toString()));

            final Dataset<SensorData> temperatureData = sensorDataInTimeSlot
                    .filter(sensorDataInTimeSlot.col("channel_type").equalTo("temperature"));

            final Dataset<SensorData> presenceData = sensorDataInTimeSlot.filter(sensorDataInTimeSlot.col("channel_type").equalTo("presence"));

            presenceData.count();
            presenceData.filter(sensorDataInTimeSlot.col("value").gt(0)).count();

            new SensorAverageData(
                    sensorDataPeriodStart.toLocalDateTime(),
                    l,
                    temperatureData.agg(functions.min(sensorDataInTimeSlot.col("value"))).as(Encoders.STRING()).first(),
                    temperatureData.agg(functions.max(sensorDataInTimeSlot.col("value"))).as(Encoders.STRING()).first(),
                    temperatureData.agg(functions.avg(sensorDataInTimeSlot.col("value"))).as(Encoders.STRING()).first(),
                    temperatureData.count(),
                    presenceData.filter(sensorDataInTimeSlot.col("value").gt(0)).count() > 0,
                    presenceData.count()
            );
        });
    }
}
