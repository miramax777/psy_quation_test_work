package com.agregate.sensors;

import org.apache.spark.sql.*;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.apache.spark.sql.types.DataTypes.StringType;
import static org.apache.spark.sql.types.DataTypes.TimestampType;

public class Main {

    final static int SENSOR_DATA_REPORT_MINUTES_INTERVAL = 15;

    public static void main(String[] args) throws AnalysisException {

        System.setProperty("hadoop.home.dir", "C:\\hadoop\\");

        final SparkSession session = SparkSession.builder()
                .appName("PsyQuationAggregateApp")
                .master("local[*]")
                .getOrCreate();

        final StructType sensor_info_schema = new StructType()
                .add("sensor_id_info", StringType, false)
                .add("channel_id_info", StringType, false)
                .add("channel_type", StringType, false)
                .add("location_id", StringType, false);

        final StructType sensor_type_schema = new StructType()
                .add("sensor_id", StringType, true)
                .add("channel_id", StringType, true)
                .add("timestamp", TimestampType, true)
                .add("value", StringType, true);

        final Dataset<Row> sensors_info = session.read()
                .option("header", "false")
                .option("delimiter", ",")
                .schema(sensor_info_schema)
                .csv("input/ds1.csv");

        final Dataset<Row> sensors_data = session.read()
                .option("header", "false")
                .option("delimiter", ",")
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

        result.createGlobalTempView("sensors_joined_result");

        final List<String> locations = session.sql("SELECT DISTINCT location_id FROM global_temp.sensors_joined_result ORDER BY location_id")
                .as(Encoders.STRING())
                .collectAsList();

        List<SensorAverageData> sensorResult = new ArrayList<>();

        locations.forEach(l -> {
            //final Dataset<SensorData> sensorDataForLocation = result.filter(result.col("location_id").equalTo(l));
            final List<SensorData> sensorDataForLocation = session.sql("SELECT * FROM global_temp.sensors_joined_result where location_id = '" + l + "'")
                    .as(Encoders.bean(SensorData.class))
                    .collectAsList();


//            final Timestamp sensorDateStart = sensorDataForLocation.first().getTimestamp();
            final Timestamp sensorDateStart = sensorDataForLocation.iterator().next().getTimestamp();
            //final Timestamp sensorDateEnd = sensorDataForLocation.sort(new Column("timestamp").desc()).first().getTimestamp();
            final Timestamp sensorDateEnd = sensorDataForLocation.get(sensorDataForLocation.size() - 1).getTimestamp();

            final Timestamp sensorDataPeriodStart = Timestamp.valueOf(
                    sensorDateStart.toLocalDateTime()
                            .withMinute(SENSOR_DATA_REPORT_MINUTES_INTERVAL * (sensorDateStart.toLocalDateTime().getMinute() / SENSOR_DATA_REPORT_MINUTES_INTERVAL))
                            .withSecond(0)
            );

            List<Timestamp> timeslots = new ArrayList<>(Collections.singletonList(sensorDataPeriodStart));

            while (timeslots.get(timeslots.size() - 1).before(Timestamp.valueOf(sensorDateEnd.toLocalDateTime()))) {
                timeslots.add(Timestamp.valueOf(timeslots.get(timeslots.size() - 1).toLocalDateTime().plusMinutes(15)));
            }

            timeslots.forEach(i -> {
//                final Dataset<SensorData> sensorDataInTimeSlot = sensorDataForLocation
//                        .filter(result.col("timestamp").$greater(i.toString()))
//                        .filter(result.col("timestamp").$less(Timestamp.valueOf(i.toLocalDateTime().plusMinutes(15)).toString()));
                session.sql("set sensor_date_from=" + i.toString());
                session.sql("set sensor_date_to=" + Timestamp.valueOf(i.toLocalDateTime().plusMinutes(15)).toString());
                session.sql("set location=" + l);

                final Dataset<SensorData> sensorDataInTimeSlot = session.sql(
                        "SELECT timestamp, location_id, sensor_id_info, channel_id, channel_type, value " +
                                "FROM global_temp.sensors_joined_result " +
                                "WHERE location_id = '${location} ' " +
                                "AND timestamp > '${sensor_date_from}' " +
                                "AND timestamp < '${sensor_date_to}'"
                ).as(Encoders.bean(SensorData.class));

                final Dataset<SensorData> temperatureData = sensorDataInTimeSlot
                        .filter(sensorDataInTimeSlot.col("channel_type").equalTo("temperature"));

                final Dataset<SensorData> presenceData = sensorDataInTimeSlot.filter(sensorDataInTimeSlot.col("channel_type").equalTo("presence"));

                sensorResult.add(
                        new SensorAverageData(
                                i.toLocalDateTime(),
                                l,
                                temperatureData.agg(functions.min(sensorDataInTimeSlot.col("value"))).as(Encoders.STRING()).first(),
                                temperatureData.agg(functions.max(sensorDataInTimeSlot.col("value"))).as(Encoders.STRING()).first(),
                                temperatureData.agg(functions.avg(sensorDataInTimeSlot.col("value"))).as(Encoders.STRING()).first(),
                                temperatureData.count(),
                                presenceData.filter(sensorDataInTimeSlot.col("value").gt(0)).count() > 0,
                                presenceData.count()
                        )
                );
            });
        });

        sensorResult.size();
    }
}
