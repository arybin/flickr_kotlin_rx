package activities

import adapters.HomeScreenAdapter
import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.View
import com.example.andreirybin.janetest.R
import kotlinx.android.synthetic.main.activity_main.mainLayout
import kotlinx.android.synthetic.main.activity_main.pictureList
import kotlinx.android.synthetic.main.activity_main.progressBar
import models.FlickrResponse
import repositories.MainActivityRepository
import repositories.RepositoryCallbacks
import timber.log.Timber
import java.lang.ref.WeakReference

/*
This is just a sample app that queries the Flickr API and shows the images corresponding to the parameters
This app needs tests added to it, which I haven't done for the sake of time
I added the extra feature to be able to share the image with other apps that accepts the intent of sharing images
Also, added an ability to download an image, it's not perfect and needs to be tested more
 */

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener, RepositoryCallbacks<FlickrResponse> {


  override fun onLoadFinished(response: FlickrResponse?) {
    progressBar.visibility = View.GONE
    flickrAdapter = pictureList.adapter as? HomeScreenAdapter ?: HomeScreenAdapter()
    flickrAdapter?.searchedList = response?.mPhoto?.mPhotoDetailsList
    flickrAdapter?.notifyDataSetChanged()
    pictureList.adapter = flickrAdapter
  }

  private var mainActivityRepository: MainActivityRepository? = null
  private var flickrAdapter: HomeScreenAdapter? = null
  private var searchString: String? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    pictureList.apply {
      itemAnimator = DefaultItemAnimator()
      layoutManager = GridLayoutManager(context, SPAN_COUNT)
    }

    mainActivityRepository = MainActivityRepository(
        WeakReference(this))
    progressBar.visibility = View.GONE
  }

  private fun makeNetworkRequest(searchString: String) {
    progressBar.visibility = View.VISIBLE
    mainActivityRepository?.requestSearch(searchString)
  }


  override fun onError(t: Throwable) {
    val error = "Error when searching Flickr"
    Timber.wtf(t, error)
    Snackbar.make(mainLayout, error, Snackbar.LENGTH_SHORT).show()
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.activity_main_menu, menu)
    val searchManager = getSystemService(Context.SEARCH_SERVICE) as? SearchManager
    val searchMenuItem = menu?.findItem(R.id.menu_search)
    val searchView = searchMenuItem?.actionView as? SearchView

    searchView?.setSearchableInfo(searchManager?.getSearchableInfo(componentName))
    searchView?.isSubmitButtonEnabled = true
    searchView?.setOnQueryTextListener(this)

    return true
  }

  override fun onQueryTextSubmit(query: String?): Boolean {
    return false
  }

  override fun onQueryTextChange(newText: String?): Boolean {
    newText?.let {
      searchString = it
      makeNetworkRequest(it)
      return true
    } ?: return false
  }

  override fun onSaveInstanceState(outState: Bundle?) {
    super.onSaveInstanceState(outState)
    outState?.putString(SEARCH_TERM, searchString)
  }

  override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
    super.onRestoreInstanceState(savedInstanceState)
    searchString = savedInstanceState?.getString(SEARCH_TERM)

    searchString?.let {
      makeNetworkRequest(it)
    }
  }

  override fun onResume() {
    super.onResume()
    searchString?.let {
      makeNetworkRequest(it)
    }
  }

  override fun onDestroy() {
    mainActivityRepository?.clearDisposables()
    super.onDestroy()
  }

  companion object {
    private const val SEARCH_TERM = "searchTerm"
    private const val SPAN_COUNT = 3
  }
}