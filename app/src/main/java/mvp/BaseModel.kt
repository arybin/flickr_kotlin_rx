package mvp

import api.FlickrApi
import dagger.injector.Injector
import database.FlickrDatabase
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created by andrei on 1/4/18.
 */
abstract class BaseModel {
    protected val disposables = CompositeDisposable()

    @Inject protected lateinit var db: FlickrDatabase
    @Inject protected lateinit var api: FlickrApi


    fun onDestroy() {
        disposables.clear()
    }

    init {
        Injector.component()?.inject(this)
    }
}