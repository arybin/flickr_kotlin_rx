package database.dao

import android.arch.persistence.room.*
import io.reactivex.Flowable
import io.reactivex.Maybe
import models.PHOTO_DETAILS_TABLE_NAME
import models.PhotoDetails

/**
 * Created by andrei on 1/4/18.
 */
@Dao
abstract class PhotoDetailsDAO : AbstractDao<PhotoDetails> {

    @Query("SELECT * FROM $PHOTO_DETAILS_TABLE_NAME")
    abstract fun getAllPhotoDetails(): Flowable<Array<PhotoDetails>>

    @Query("DELETE FROM $PHOTO_DETAILS_TABLE_NAME")
    abstract fun deleteAllPhotoDetails()


    @Query("SELECT * FROM $PHOTO_DETAILS_TABLE_NAME WHERE mId = :id")
    abstract fun getImageById(id: String) : Maybe<PhotoDetails>

    @Transaction
    open fun clearInsert(photoDetails: Array<PhotoDetails>) {
        deleteAllPhotoDetails()
        //insertAll(photoDetails)
    }
}