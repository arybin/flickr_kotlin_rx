package views

import android.content.Context
import android.widget.ImageView.ScaleType.CENTER_CROP
import com.example.andreirybin.janetest.R
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.dip
import org.jetbrains.anko.frameLayout
import org.jetbrains.anko.imageView
import org.jetbrains.anko.margin
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.padding
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.wrapContent

/**
 * Created by arybin on 1/1/18.
 */
open class FlickrRowCell : AnkoComponent<Context> {
  override fun createView(ui: AnkoContext<Context>) = with(ui) {
    frameLayout {
      lparams {
        width = matchParent
        height = wrapContent
        margin = dip(4)

        imageView {
          id = R.id.flickrImagePreview
          scaleType = CENTER_CROP
        }.lparams {
          width = matchParent
          height = matchParent
        }
      }
    }
  }
}