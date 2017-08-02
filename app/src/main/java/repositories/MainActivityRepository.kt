package repositories

import com.example.andreirybin.janetest.activities.util.ApiManager
import com.example.andreirybin.janetest.activities.util.Constants
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import models.FlickrResponse
import timber.log.Timber
import java.lang.ref.WeakReference

/**
 * Created by arybin on 8/1/17.
 */


class MainActivityRepository(
    val callback: WeakReference<RepositoryCallbacks<FlickrResponse>>) : AbstractRepository() {

  fun requestSearch(searchParameter: String) {
    disposables.add(ApiManager.getFlickrService()
        .getSearchResults(Constants.KEY, searchParameter)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            { response: FlickrResponse ->
              callback.get()?.onLoadFinished(response)
              Timber.d("Response came back")
            },
            { error -> callback.get()?.onError(error) }
        ))
  }
}