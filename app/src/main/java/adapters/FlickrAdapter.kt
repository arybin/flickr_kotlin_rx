package adapters

import activities.FullPhotoActivity
import activities.MainActivity
import adapters.FlickrAdapter.FlickrViewHolder
import android.content.Context
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.view.ViewCompat
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import extensions.applySchedulers
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.flickr_row.view.flickrImagePreview
import models.PhotoDetails
import org.jetbrains.anko.AnkoContext.Companion
import timber.log.Timber
import views.FlickrRowCell

class FlickrAdapter : RecyclerView.Adapter<FlickrViewHolder>(), Disposable {

    private var searchedList: Array<PhotoDetails> = emptyArray()
    private val flickrAdapterFlowable = FlickerAdapterFlowable()
    private val disposable: Disposable


    init {
        disposable = flickrAdapterFlowable.flowable
                .applySchedulers()
                .subscribe({
                    it.dispatchUpdatesTo(this@FlickrAdapter)
                })

    }


    fun updateData(nextList: Array<PhotoDetails>) {
        flickrAdapterFlowable.calculateDiffResult(searchedList, nextList)
    }


    override fun onBindViewHolder(holder: FlickrViewHolder?, position: Int) {
        searchedList.let {
            holder?.bind(it[position])
        }
    }

    override fun getItemCount(): Int = searchedList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlickrViewHolder {
        val view = FlickrRowCell().createView(Companion.create(parent.context, false))
        return FlickrViewHolder(view)
    }

    override fun isDisposed(): Boolean = disposable.isDisposed

    override fun dispose() {
        disposable.dispose()
    }

    inner class FlickrViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(photoDetails: PhotoDetails) {

            itemView.apply {
                ViewCompat.setTransitionName(flickrImagePreview, photoDetails.mId)
                Picasso.with(context).load(photoDetails.imageURLMed()).into(flickrImagePreview)
                setOnClickListener {
                    showFullScreenPreview(photoDetails.mId, context)
                }
            }
        }

        private fun showFullScreenPreview(imageId: String, context: Context) {
            val intent = FullPhotoActivity.newIntent(imageId, context)
            itemView.apply {
                val activity = context as MainActivity
                val makeSceneTransitionAnimation = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, flickrImagePreview, ViewCompat.getTransitionName(flickrImagePreview))

                context.startActivity(intent, makeSceneTransitionAnimation.toBundle())
            }
        }
    }

    inner class FlickerAdapterFlowable {
        private val publishProcessor: PublishProcessor<FlickrAdapterDiffCallback> =
                PublishProcessor.create()

        val flowable: Flowable<DiffUtil.DiffResult> =
                Flowable.fromPublisher(publishProcessor)
                        .flatMap {
                            Flowable.fromCallable {
                                DiffUtil.calculateDiff(it)
                            }.subscribeOn(Schedulers.computation())
                        }

        fun calculateDiffResult(current: Array<PhotoDetails>, next: Array<PhotoDetails>) {
            publishProcessor.onNext(FlickrAdapterDiffCallback(current, next))
        }

        fun stopPublisher() {
            publishProcessor.onComplete()
        }
    }

    inner class FlickrAdapterDiffCallback(val current: Array<PhotoDetails>, val next: Array<PhotoDetails>) : DiffUtil.Callback() {
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
}