package mvp.interfaces

import models.PhotoDetails
import mvp.BasePresenter
import mvp.BaseView

/**
 * Created by arybin on 1/4/18.
 */
interface MainActivityInterface {

  interface View : BaseView {
    fun showError(error: Throwable)
    fun showLoading()
    fun stopLoading()
    fun presentResult(photoDetails: Array<PhotoDetails>)
  }

  interface Presenter : BasePresenter {
    fun createSubscription()
    fun requestPhotos(requestQuery: String)
  }
}