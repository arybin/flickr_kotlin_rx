package mvp.presenters

import api.await
import extensions.applySchedulers
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import models.FlickrResponse
import mvp.BaseModel
import mvp.interfaces.MainActivityInterface
import mvp.interfaces.MainActivityInterface.View
import util.Constants
import java.lang.ref.WeakReference

class SearchPresenter(private val view: WeakReference<View>) : BaseModel(), MainActivityInterface.Presenter {

  override fun dropView() {
    disposables.clear()
  }

  override fun requestPhotos(requestQuery: String) {
    disposables.add(api.getSearchResults(Constants.KEY, requestQuery)
        .subscribeOn(Schedulers.io())
        .doOnSubscribe{ view.get()?.showLoading() }
        .doOnTerminate { view.get()?.stopLoading() }
        .doOnError { view.get()?.showError(it) }
        .flatMap {
          it.mPhoto?.mPhotoDetailsList?.let { photoDetails ->
            Observable.fromCallable { db.photoDetailsDao().clearInsert(photoDetails) }
          } ?: Observable.fromCallable { true }
        }.subscribe())

  }

  override fun createSubscription() {
    disposables.add(db.photoDetailsDao()
        .getAllPhotoDetails()
        .applySchedulers()
        .subscribe({view.get()?.presentResult(it)}))
  }

  //coroutines kung fu
  suspend fun requestSearchAsync(searchParameter: String): FlickrResponse = api.getSearchResultsCoroutines(Constants.KEY, searchParameter).await()

}