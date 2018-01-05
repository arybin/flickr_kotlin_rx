package repositories

import api.FlickrApi
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

abstract class AbstractRepository {
  protected val disposables = CompositeDisposable()

  @Inject protected lateinit var api: FlickrApi

  fun clearDisposables() {
    disposables.clear()
  }
}