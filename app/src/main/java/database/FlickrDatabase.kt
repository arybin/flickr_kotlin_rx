package database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import database.dao.PhotoDetailsDAO
import models.PhotoDetails

/**
 * Created by andrei on 1/4/18.
 */
@Database(entities = [PhotoDetails::class],  version = 1, exportSchema = false)
abstract class FlickrDatabase : RoomDatabase() {
    abstract fun photoDetailsDao() : PhotoDetailsDAO
}