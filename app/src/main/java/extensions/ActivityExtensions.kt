package extensions

import android.app.Activity

/**
 * Created by arybin on 1/4/18.
 */

fun Activity.isGone() : Boolean {
  return this.isFinishing || this.isDestroyed
}