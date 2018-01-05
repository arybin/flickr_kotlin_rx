package activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.andreirybin.janetest.R
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_full_photo.imagePreview
import kotlinx.android.synthetic.main.activity_full_photo.photoDescription
import kotlinx.android.synthetic.main.activity_full_photo.photoTitle
import models.FlickrResponse
import models.PhotoDetails
import mvp.interfaces.FullPhotoActivityInterface
import mvp.presenters.FullPhotoPresenter
import util.Utils
import java.io.File
import java.lang.ref.WeakReference

class FullPhotoActivity : AppCompatActivity(), FullPhotoActivityInterface.View {


  private var presenter: FullPhotoPresenter? = null
  private var absolutePath: String? = null

  val imageId: String?  by lazy { intent.getStringExtra(IMAGE_ID) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_full_photo)
    supportStartPostponedEnterTransition()

    ViewCompat.setTransitionName(imagePreview, imageId)

    presenter = FullPhotoPresenter(WeakReference(this))
    presenter?.requestPhotoDetails(imageId)

  }

  override fun imageInfoLoaded(photoDetails: PhotoDetails) {
    Picasso.with(this).load(photoDetails.imageURLLarge()).into(imagePreview, object : Callback {
          override fun onSuccess() {
            supportStartPostponedEnterTransition()
          }

          override fun onError() {
            supportStartPostponedEnterTransition()
          }
        })
  }

  override fun loadFinished(flickrResponse: FlickrResponse) {
    photoDescription.text = flickrResponse.mPhotoInfo?.mDescription?.mContent
    photoTitle.text = flickrResponse.mPhotoInfo?.mTitle?.mContent
  }

  override fun showError() {
    Utils.showSnackbarError(this, R.string.error_getting_photos_for_search_string, Snackbar.LENGTH_SHORT)
  }


  fun setPath(path: String?) {
    absolutePath = path
    Toast.makeText(this, "File downloaded at $absolutePath", Toast.LENGTH_SHORT).show()
  }


  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.acitivity_full_photo_menu, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    when (item?.itemId) {
      R.id.share_photo -> {
        val intent = Intent(Intent.ACTION_SEND).apply {
          type = "image/jpg"
          absolutePath?.let {
            val file = File(it)
            putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file))

          } ?: return false
        }
        startActivity(Intent.createChooser(intent, "Share image using"))
        return true
      }

      R.id.download_photo -> {
        //TODO this should be tested, and perhaps in its own service but for the sake of time it's here
        //presenter?.downloadImage(imageURLMed, imageId)
        return true
      }
    }
    return super.onOptionsItemSelected(item)
  }

  override fun onDestroy() {
    presenter?.dropView()
    super.onDestroy()
  }

  override fun onSupportNavigateUp(): Boolean {
    presenter?.dropView()
    onBackPressed()
    return true
  }

  companion object {
    private const val IMAGE_ID = "imageId"

    fun newIntent(imageId: String?, context: Context): Intent {
      return Intent(context, FullPhotoActivity::class.java).apply {
        putExtra(IMAGE_ID, imageId)
      }
    }
  }
}