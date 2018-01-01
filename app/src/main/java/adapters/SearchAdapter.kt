package adapters

import activities.FullPhotoActivity
import adapters.SearchAdapter.FlickrViewHolder
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.flickr_row.view.flickrImagePreview
import models.PhotoDetails
import org.jetbrains.anko.AnkoContext.Companion
import views.FlickrRowCell

class SearchAdapter : RecyclerView.Adapter<FlickrViewHolder>() {
  var searchedList: Array<PhotoDetails>? = null


  override fun onBindViewHolder(holder: FlickrViewHolder?, position: Int) {
    searchedList?.let {
      holder?.bind(it[position])
    }
  }

  override fun getItemCount(): Int = searchedList?.size ?: 0

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlickrViewHolder {
    val view = FlickrRowCell().createView(Companion.create(parent.context, false))
    //val view = LayoutInflater.from(parent.context).inflate(R.layout.flickr_row, parent, false)
    return FlickrViewHolder(view)
  }

  inner class FlickrViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(photoDetails: PhotoDetails) {
      val imageURL = "http://farm${photoDetails.mFarm}.staticflickr.com/" +
          "${photoDetails.mServer}/" +
          "${photoDetails.mId}_${photoDetails.mSecret}"
      val size = "_m.jpg"
      itemView.apply {
        Picasso.with(context).load(imageURL + size).into(flickrImagePreview)
        setOnClickListener {
          showFullScreenPreview(imageURL, photoDetails.mId, context)
        }
      }
    }

    private fun showFullScreenPreview(imageURL: String, imageId: String?, context: Context) {
      val intent = FullPhotoActivity.newIntent(imageURL, imageId, context)
      context.startActivity(intent)
    }
  }
}