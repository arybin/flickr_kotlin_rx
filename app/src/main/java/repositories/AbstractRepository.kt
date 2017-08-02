package repositories

import io.reactivex.disposables.CompositeDisposable

/**
 * Created by arybin on 8/1/17.
 */
abstract class AbstractRepository {
  protected val disposables = CompositeDisposable()

  fun clearDisposables() {
    disposables.clear()
  }
}