package activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.andreirybin.janetest.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_full_photo.imagePreview
import kotlinx.android.synthetic.main.activity_full_photo.photoDescription
import kotlinx.android.synthetic.main.activity_full_photo.photoTitle
import models.FlickrResponse
import repositories.FullPhotoCallback
import repositories.FullPhotoRepository
import timber.log.Timber
import java.io.File
import java.lang.ref.WeakReference

class FullPhotoActivity: AppCompatActivity(), FullPhotoCallback {

    private var photoRepository: FullPhotoRepository? = null
    private var absolutePath: String? = null

    val imageURL: String by lazy { intent.getStringExtra(IMAGE_URL) }
    val imageId: String?  by lazy { intent.getStringExtra(IMAGE_ID) }
    val size = "_z.jpg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_photo)
        Picasso.with(this).load(imageURL+size).into(imagePreview)

        photoRepository = FullPhotoRepository(WeakReference(this))
        imageId?.let {
            photoRepository?.getImageInfo(imageURL)
        }
    }

    override fun onLoadFinished(response: FlickrResponse?) {
        response?.let {
            photoDescription.text = it.mPhotoInfo?.mDescription?.mContent
            photoTitle.text = it.mPhotoInfo?.mTitle?.mContent
        }
    }

    override fun onError(t: Throwable) {
        Timber.wtf(t, "Error while getting image's data")
    }

    override fun setPath(path: String?) {
        absolutePath = path
        Toast.makeText(this, "File downloaded at $absolutePath", Toast.LENGTH_SHORT).show()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.acitivity_full_photo_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
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
                photoRepository?.downloadImage(imageURL, imageId)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        photoRepository?.clearDisposables()
        super.onDestroy()
    }

    override fun onSupportNavigateUp(): Boolean {
        photoRepository?.clearDisposables()
        onBackPressed()
        return true
    }

    companion object {
        private const val IMAGE_URL = "imageURL"
        private const val IMAGE_ID = "imageId"

        fun newIntent(urlString: String, imageId: String?, context: Context): Intent{
            val intent = Intent(context, FullPhotoActivity::class.java).apply {
                putExtra(IMAGE_URL, urlString)
                putExtra(IMAGE_ID, imageId)
            }
            return intent
        }
    }
}