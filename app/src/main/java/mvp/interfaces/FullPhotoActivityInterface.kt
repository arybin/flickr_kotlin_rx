package mvp.interfaces

import models.FlickrResponse
import models.PhotoDetails
import mvp.BasePresenter
import mvp.BaseView

/**
 * Created by arybin on 1/5/18.
 */
interface FullPhotoActivityInterface {

  interface View : BaseView {
    fun imageInfoLoaded(photoDetails: PhotoDetails)
    fun loadFinished(flickrResponse: FlickrResponse)
    fun showError()
  }

  interface Presenter : BasePresenter {
    fun requestPhotoDetails(id: String?)
  }

}