package dagger.modules

import android.arch.persistence.room.Room
import android.content.Context
import dagger.Module
import dagger.Provides
import database.FlickrDatabase
import javax.inject.Singleton

/**
 * Created by andrei on 1/4/18.
 */
@Module
@Singleton
class DatabaseModule(val context: Context) {

    private val DB_NAME = "FlickrDatabase"

    private val db: FlickrDatabase =
            Room.databaseBuilder(context, FlickrDatabase::class.java, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .build()

    @Provides
    fun database(): FlickrDatabase = db
}