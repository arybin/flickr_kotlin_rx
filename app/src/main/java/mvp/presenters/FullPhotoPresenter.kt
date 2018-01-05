package mvp.presenters

import extensions.applySchedulers
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import mvp.BaseModel
import mvp.interfaces.FullPhotoActivityInterface
import timber.log.Timber
import util.Constants
import java.lang.ref.WeakReference

/**
 * Created by arybin on 1/5/18.
 */
class FullPhotoPresenter(private val callback: WeakReference<FullPhotoActivityInterface.View>)
  : BaseModel(), FullPhotoActivityInterface.Presenter {
  override fun dropView() {
    disposables.clear()
  }

  override fun requestPhotoDetails(id: String?) {
    val imageId = id ?: return
     disposables.add(db.photoDetailsDao()
        .getImageById(imageId)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          callback.get()?.imageInfoLoaded(it)}, {
          Timber.wtf(it, "Error requesting image's info")
          callback.get()?.showError()
        }))

    disposables.add(api.getPhotoInfo(imageId, Constants.KEY)
        .applySchedulers()
        .subscribe({
          callback.get()?.loadFinished(it)
        }, {
          Timber.wtf(it, "Error requesting photo's info")
          callback.get()?.showError()
        })
    )
  }

}