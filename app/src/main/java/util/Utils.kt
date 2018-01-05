package util

import android.app.Activity
import android.graphics.Color
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.view.ViewGroup
import android.widget.TextView
import com.example.andreirybin.janetest.R
import extensions.isGone
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.textColor

/**
 * Created by arybin on 1/4/18.
 */
object Utils {

  fun showSnackbarError(activity: Activity, @StringRes errorMessage: Int, lenght: Int) {
    if(!activity.isGone()) {
      val container = activity.findViewById<ViewGroup?>(android.R.id.content)?.getChildAt(0)
      container?.let {
        val snackbar = Snackbar.make(container, errorMessage, lenght)
        val snackView = snackbar.view
        snackView.backgroundColor = ContextCompat.getColor(activity, R.color.snackbarError)
        val textView = snackView.findViewById<TextView?>(android.support.design.R.id.snackbar_text)
        textView?.textColor = Color.WHITE
        snackbar.show()
      }
    }
  }
}