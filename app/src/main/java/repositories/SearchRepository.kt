package repositories

import api.ApiManager
import api.await
import util.Constants
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import models.FlickrResponse
import timber.log.Timber
import java.lang.ref.WeakReference

class SearchRepository: AbstractRepository() {


    suspend fun requestSearchAsync(searchParameter: String) : FlickrResponse =
            ApiManager.getFlickrService().getSearchResultsCoroutines(Constants.KEY, searchParameter).await()

}