package activities

import adapters.FlickrAdapter
import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.view.Menu
import android.widget.SearchView
import com.example.andreirybin.janetest.R
import com.jakewharton.rxbinding2.widget.RxSearchView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.pictureList
import kotlinx.android.synthetic.main.activity_main.refreshLayout
import models.PhotoDetails
import mvp.interfaces.MainActivityInterface
import mvp.presenters.SearchPresenter
import timber.log.Timber
import util.Utils
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit

/*
This is just a sample app that queries the Flickr API and shows the images corresponding to the parameters
This app needs tests added to it, which I haven't done for the sake of time
I added the extra feature to be able to share the image with other apps that accepts the intent of sharing images
Also, added an ability to download an image, it's not perfect and needs to be tested more
 */

class MainActivity : AppCompatActivity(), MainActivityInterface.View, SwipeRefreshLayout.OnRefreshListener {
  private val disposables = CompositeDisposable()

  private var presenter: MainActivityInterface.Presenter? = null
  private var flickrAdapter: FlickrAdapter? = null
  private var searchString: String? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    pictureList.apply {
      itemAnimator = DefaultItemAnimator()
      layoutManager = GridLayoutManager(context, SPAN_COUNT)
    }

    presenter = SearchPresenter(WeakReference(this))
    presenter?.createSubscription()

    refreshLayout.setOnRefreshListener(this)
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.activity_main_menu, menu)
    val searchManager = getSystemService(Context.SEARCH_SERVICE) as? SearchManager
    val searchMenuItem = menu?.findItem(R.id.menu_search)
    val searchView = searchMenuItem?.actionView as? SearchView

    searchView?.queryHint = getString(R.string.search_photos)
    searchView?.let {
      disposables.add(RxSearchView.queryTextChanges(it)
          .debounce(500, TimeUnit.MILLISECONDS)
          .observeOn(AndroidSchedulers.mainThread())
          .skip(1)
          .subscribe { text ->
              searchString = text.toString()
              presenter?.requestPhotos(text.toString())
          }
      )
    }
    searchView?.setSearchableInfo(searchManager?.getSearchableInfo(componentName))
    return true
  }

  override fun onSaveInstanceState(outState: Bundle?) {
    super.onSaveInstanceState(outState)
    outState?.putString(SEARCH_TERM, searchString)
  }

  override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
    super.onRestoreInstanceState(savedInstanceState)
    searchString = savedInstanceState?.getString(SEARCH_TERM)

  }

  override fun onDestroy() {
    presenter?.dropView()
    disposables.clear()
    super.onDestroy()
  }

  override fun showError(error: Throwable) {
    stopLoading()
    Timber.wtf(error, "Error requesting info from Flickr API")
    Utils.showSnackbarError(this, R.string.error_getting_photos_for_search_string, Snackbar.LENGTH_SHORT)
  }

  override fun showLoading() {
    refreshLayout.isRefreshing = true
  }

  override fun stopLoading() {
    refreshLayout.isRefreshing = false
  }

  override fun onRefresh() {
    showLoading()
    searchString?.let {
      presenter?.requestPhotos(it)
    }
  }

  override fun presentResult(photoDetails: Array<PhotoDetails>) {
    flickrAdapter = pictureList.adapter as? FlickrAdapter ?: FlickrAdapter()
    flickrAdapter?.searchedList = photoDetails
    flickrAdapter?.notifyDataSetChanged()
    pictureList.adapter = flickrAdapter
  }

  companion object {
    private const val SEARCH_TERM = "searchTerm"
    private const val SPAN_COUNT = 2
  }
}