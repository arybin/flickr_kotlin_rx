package activities

import adapters.FlickrAdapter
import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.view.Menu
import android.view.View
import android.widget.SearchView
import com.example.andreirybin.janetest.R
import com.jakewharton.rxbinding2.widget.RxSearchView
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.pictureList
import kotlinx.android.synthetic.main.activity_main.progressBar
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import models.FlickrResponse
import repositories.SearchRepository
import timber.log.Timber
import java.util.concurrent.TimeUnit

/*
This is just a sample app that queries the Flickr API and shows the images corresponding to the parameters
This app needs tests added to it, which I haven't done for the sake of time
I added the extra feature to be able to share the image with other apps that accepts the intent of sharing images
Also, added an ability to download an image, it's not perfect and needs to be tested more
 */

class MainActivity : AppCompatActivity() {

    private var mainActivityRepository: SearchRepository? = null
    private var flickrAdapter: FlickrAdapter? = null
    private var searchString: String? = null

    private fun onLoadFinished(response: FlickrResponse?) {
        progressBar.visibility = View.GONE
        flickrAdapter = pictureList.adapter as? FlickrAdapter ?: FlickrAdapter()
        flickrAdapter?.searchedList = response?.mPhoto?.mPhotoDetailsList
        flickrAdapter?.notifyDataSetChanged()
        pictureList.adapter = flickrAdapter
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pictureList.apply {
            itemAnimator = DefaultItemAnimator()
            layoutManager = GridLayoutManager(context, SPAN_COUNT)
        }

        mainActivityRepository = SearchRepository()
        progressBar.visibility = View.GONE
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main_menu, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as? SearchManager
        val searchMenuItem = menu?.findItem(R.id.menu_search)
        val searchView = searchMenuItem?.actionView as? SearchView

        searchView?.queryHint = getString(R.string.search_photos)
        searchView?.let {
            RxSearchView.queryTextChanges(it)
                    .debounce(500, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .skip(1)
                    .subscribe { text ->
                        progressBar?.visibility = View.VISIBLE
                        async(UI) {
                            try {
                                val result = mainActivityRepository?.requestSearchAsync(text.toString())
                                onLoadFinished(result)

                            } catch (e: Exception) {
                                Timber.wtf(e, "Error requesting images from Flickr for $text")
                            }
                        }
                    }
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
        mainActivityRepository?.onDestroy()
        super.onDestroy()
    }

    companion object {
        private const val SEARCH_TERM = "searchTerm"
        private const val SPAN_COUNT = 3
    }
}