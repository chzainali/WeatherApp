package com.example.weatherapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.weatherapp.models.FinalForecastModel

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "forecast_database"
        private const val TABLE_NAME = "forecast_table"

        // Columns
        private const val KEY_ID = "id"
        private const val KEY_DAY = "day"
        private const val KEY_TIME = "time"
        private const val KEY_LOCATION = "location"
        private const val KEY_STATUS = "status"
        private const val KEY_TEMPERATURE = "temperature"
        private const val KEY_MIN_TEMPERATURE = "min_temperature"
        private const val KEY_MAX_TEMPERATURE = "max_temperature"
        private const val KEY_SUNRISE = "sunrise"
        private const val KEY_SUNSET = "sunset"
        private const val KEY_WIND = "wind"
        private const val KEY_PRESSURE = "pressure"
        private const val KEY_HUMIDITY = "humidity"
        private const val KEY_CLOUD = "cloud"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_TABLE = ("CREATE TABLE $TABLE_NAME("
                + "$KEY_ID INTEGER PRIMARY KEY,"
                + "$KEY_LOCATION TEXT,"
                + "$KEY_DAY TEXT,"
                + "$KEY_TIME TEXT,"
                + "$KEY_STATUS TEXT,"
                + "$KEY_TEMPERATURE TEXT,"
                + "$KEY_MIN_TEMPERATURE TEXT,"
                + "$KEY_MAX_TEMPERATURE TEXT,"
                + "$KEY_SUNRISE TEXT,"
                + "$KEY_SUNSET TEXT,"
                + "$KEY_WIND TEXT,"
                + "$KEY_PRESSURE TEXT,"
                + "$KEY_HUMIDITY TEXT,"
                + "$KEY_CLOUD TEXT"
                + ")")
        db.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addForecast(model: FinalForecastModel): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_DAY, model.day)
        values.put(KEY_STATUS, model.status)
        values.put(KEY_LOCATION, model.city)
        values.put(KEY_TEMPERATURE, model.temperature)
        values.put(KEY_MIN_TEMPERATURE, model.minTemperature)
        values.put(KEY_MAX_TEMPERATURE, model.maxTemperature)
        values.put(KEY_SUNRISE, model.sunrise)
        values.put(KEY_SUNSET, model.sunset)
        values.put(KEY_WIND, model.wind)
        values.put(KEY_PRESSURE, model.pressure)
        values.put(KEY_HUMIDITY, model.humidity)
        values.put(KEY_CLOUD, model.cloud)
        values.put(KEY_TIME, model.time)

        val id = db.insert(TABLE_NAME, null, values)
        db.close()
        return id
    }


    fun getAllForecasts(): List<FinalForecastModel> {
        val forecastList = ArrayList<FinalForecastModel>()
        val selectQuery = "SELECT * FROM $TABLE_NAME"
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(selectQuery, null)
        cursor.use { cursor ->
            while (cursor.moveToNext()) {
                val city = cursor.getString(cursor.getColumnIndexOrThrow(KEY_LOCATION))
                val day = cursor.getString(cursor.getColumnIndexOrThrow(KEY_DAY))
                val status = cursor.getString(cursor.getColumnIndexOrThrow(KEY_STATUS))
                val temperature = cursor.getString(cursor.getColumnIndexOrThrow(KEY_TEMPERATURE))
                val minTemperature = cursor.getString(cursor.getColumnIndexOrThrow(KEY_MIN_TEMPERATURE))
                val maxTemperature = cursor.getString(cursor.getColumnIndexOrThrow(KEY_MAX_TEMPERATURE))
                val sunrise = cursor.getString(cursor.getColumnIndexOrThrow(KEY_SUNRISE))
                val sunset = cursor.getString(cursor.getColumnIndexOrThrow(KEY_SUNSET))
                val wind = cursor.getString(cursor.getColumnIndexOrThrow(KEY_WIND))
                val pressure = cursor.getString(cursor.getColumnIndexOrThrow(KEY_PRESSURE))
                val humidity = cursor.getString(cursor.getColumnIndexOrThrow(KEY_HUMIDITY))
                val cloud = cursor.getString(cursor.getColumnIndexOrThrow(KEY_CLOUD))
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID))
                val time = cursor.getString(cursor.getColumnIndexOrThrow(KEY_TIME))

                val forecastModel = FinalForecastModel(
                    city,
                    day,
                    status,
                    temperature,
                    minTemperature,
                    maxTemperature,
                    sunrise,
                    sunset,
                    wind,
                    pressure,
                    humidity,
                    cloud,
                    id,
                    time.toString()
                )
                if (!forecastList.any { it.time ==time.toString() }) {
                    forecastList.add(forecastModel)
                }
            }
        }

        cursor.close()
        return forecastList
    }

    fun updateForecast(model: FinalForecastModel): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_DAY, model.day)
        values.put(KEY_STATUS, model.status)
        values.put(KEY_TEMPERATURE, model.temperature)
        values.put(KEY_MIN_TEMPERATURE, model.minTemperature)
        values.put(KEY_MAX_TEMPERATURE, model.maxTemperature)
        values.put(KEY_SUNRISE, model.sunrise)
        values.put(KEY_SUNSET, model.sunset)
        values.put(KEY_WIND, model.wind)
        values.put(KEY_PRESSURE, model.pressure)
        values.put(KEY_HUMIDITY, model.humidity)
        values.put(KEY_CLOUD, model.cloud)

        return db.update(
            TABLE_NAME, values, "$KEY_ID = ?",
            arrayOf(model.day.toString())
        )
    }

    fun deleteForecast(id: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "$KEY_ID = ?", arrayOf(id.toString()))
        db.close()
    }

    fun getAllEntriesWithDate( date: String): List<FinalForecastModel> {

        val db = this.writableDatabase

        val entries = mutableListOf<FinalForecastModel>()

        val query = "SELECT * FROM $TABLE_NAME WHERE $KEY_TIME = ?"
        val selectionArgs = arrayOf(date)
        val cursor = db.rawQuery(query, selectionArgs)

        cursor.use { cursor ->
            while (cursor.moveToNext()) {
                val city = cursor.getString(cursor.getColumnIndexOrThrow(KEY_LOCATION))
                val day = cursor.getString(cursor.getColumnIndexOrThrow(KEY_DAY))
                val status = cursor.getString(cursor.getColumnIndexOrThrow(KEY_STATUS))
                val temperature = cursor.getString(cursor.getColumnIndexOrThrow(KEY_TEMPERATURE))
                val minTemperature = cursor.getString(cursor.getColumnIndexOrThrow(KEY_MIN_TEMPERATURE))
                val maxTemperature = cursor.getString(cursor.getColumnIndexOrThrow(KEY_MAX_TEMPERATURE))
                val sunrise = cursor.getString(cursor.getColumnIndexOrThrow(KEY_SUNRISE))
                val sunset = cursor.getString(cursor.getColumnIndexOrThrow(KEY_SUNSET))
                val wind = cursor.getString(cursor.getColumnIndexOrThrow(KEY_WIND))
                val pressure = cursor.getString(cursor.getColumnIndexOrThrow(KEY_PRESSURE))
                val humidity = cursor.getString(cursor.getColumnIndexOrThrow(KEY_HUMIDITY))
                val cloud = cursor.getString(cursor.getColumnIndexOrThrow(KEY_CLOUD))
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID))
                val time = cursor.getString(cursor.getColumnIndexOrThrow(KEY_TIME))

                val forecastModel = FinalForecastModel(
                    city,
                    day,
                    status,
                    temperature,
                    minTemperature,
                    maxTemperature,
                    sunrise,
                    sunset,
                    wind,
                    pressure,
                    humidity,
                    cloud,
                    id,time
                )
                entries.add(forecastModel)
            }
        }
        db.close()

        return entries
    }


}
