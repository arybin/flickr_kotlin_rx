package repositories

import activities.FullPhotoActivity
import com.example.andreirybin.janetest.activities.util.ApiManager
import com.example.andreirybin.janetest.activities.util.Constants
import com.example.andreirybin.janetest.activities.util.DownloadManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import models.FlickrResponse
import okhttp3.ResponseBody
import java.lang.ref.WeakReference

interface FullPhotoCallback : RepositoryCallbacks<FlickrResponse> {
  fun setPath(path: String?)
}

class FullPhotoRepository(val callback: WeakReference<FullPhotoCallback>) : AbstractRepository() {

  fun getImageInfo(imageId: String) {
    disposables.add(
        ApiManager.getFlickrService().getPhotoInfo(imageId, Constants.KEY)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
              response: FlickrResponse ->
              callback.get()?.onLoadFinished(response)
            }, {
              error ->
              callback.get()?.onError(error)
            })
    )
  }

  fun downloadImage(url: String, imageId: String?) {
    disposables.add(
        ApiManager.getFlickrService().downloadPhoto(url)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
              responseBody: ResponseBody ->
              callback.get()?.setPath(
                  DownloadManager.writeResponseBodyToDisk(
                      responseBody,
                      imageId,
                      (callback.get() as? FullPhotoActivity)?.applicationContext
                  ))
            }, {
              error ->
              callback.get()?.onError(error)
            })
    )
  }

}