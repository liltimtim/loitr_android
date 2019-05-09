package com.tddevelopment.loitr.model

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context

@Database(entities = [FenceEvent::class], version = 1)
@TypeConverters(Converters::class)
abstract class LoitrDatabase : RoomDatabase() {
    abstract fun fenceDao() : FenceDao

    companion object {
        private var instance: LoitrDatabase? = null
        fun getInstance(context: Context) : LoitrDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(context, LoitrDatabase::class.java, "LoitrDatabase").build()
            }
            return instance as LoitrDatabase
        }
    }
}