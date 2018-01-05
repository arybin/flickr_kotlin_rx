package database.dao

import android.arch.persistence.room.*
import io.reactivex.Flowable
import models.PHOTO_DETAILS_TABLE_NAME
import models.PhotoDetails

/**
 * Created by andrei on 1/4/18.
 */
@Dao
abstract class PhotoDetailsDAO {

    @Query("SELECT * FROM $PHOTO_DETAILS_TABLE_NAME")
    abstract fun getAllPhotoDetails(): Flowable<Array<PhotoDetails>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertPhotoDetails(photos: Array<PhotoDetails>)

    @Query("DELETE FROM $PHOTO_DETAILS_TABLE_NAME")
    abstract fun deleteAllPhotoDetails()

    @Transaction
    open fun clearInsert(photoDetails: Array<PhotoDetails>) {
        deleteAllPhotoDetails()
        insertPhotoDetails(photoDetails)
    }
}