package repositories

import io.reactivex.disposables.CompositeDisposable

abstract class AbstractRepository {
  protected val disposables = CompositeDisposable()

  fun clearDisposables() {
    disposables.clear()
  }
}