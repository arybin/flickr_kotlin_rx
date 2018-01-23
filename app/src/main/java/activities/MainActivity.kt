package activities

import adapters.FlickrAdapter
import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.util.DiffUtil
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.view.Menu
import android.widget.SearchView
import com.example.andreirybin.janetest.R
import com.jakewharton.rxbinding2.widget.RxSearchView
import extensions.applySchedulers
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
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
        flickrAdapter = FlickrAdapter()
        pictureList.apply {
            itemAnimator = DefaultItemAnimator()
            layoutManager = GridLayoutManager(context, SPAN_COUNT)
            adapter = flickrAdapter
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
                    .filter { it.isNotEmpty() } //filter out empty results
                    .debounce(500, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
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


        val adapter = pictureList.adapter as? FlickrAdapter ?: FlickrAdapter()
        val previousList = adapter.searchedList

        previousList?.let {

            Flowable.fromCallable {
                val callback = MyDiffCallback(it, photoDetails)
                DiffUtil.calculateDiff(callback)
            }
                    .onBackpressureDrop()
                    .applySchedulers()
                    .subscribe({
                        Timber.wtf("Hi I am on ${Thread.currentThread().id} thread")
                        setAdapterData(photoDetails, adapter)
                        it.dispatchUpdatesTo(adapter)
                    })
        } ?: setAdapterData(photoDetails, adapter)
    }

    private fun setAdapterData(photoDetails: Array<PhotoDetails>, adapter: FlickrAdapter) {
        adapter.searchedList = photoDetails
    }

    inner class MyDiffCallback(val current: Array<PhotoDetails>, val next: Array<PhotoDetails>) : DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            current[oldItemPosition] == next[newItemPosition]

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                current[oldItemPosition] == next[newItemPosition]


        override fun getOldListSize(): Int = current.size
        override fun getNewListSize(): Int {
           Timber.wtf("Hi I am on ${Thread.currentThread().id} thread")
            return next.size
        }
    }

    companion object {
        private const val SEARCH_TERM = "searchTerm"
        private const val SPAN_COUNT = 2
    }
}