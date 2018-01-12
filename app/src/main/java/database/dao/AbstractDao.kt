package database.dao

import android.arch.persistence.room.*

/**
 * Created by andrei.rybin on 1/10/18.
 */
@Dao
interface AbstractDao<I>{

    @Delete
    fun delete(obj: I)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(list: Array<I>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(item: I)
}


