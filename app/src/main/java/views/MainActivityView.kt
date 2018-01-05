package views

import android.content.Context
import com.example.andreirybin.janetest.R
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.dip
import org.jetbrains.anko.frameLayout
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.swipeRefreshLayout

/**
 * Created by arybin on 1/5/18.
 */
open class MainActivityView : AnkoComponent<Context> {
  override fun createView(ui: AnkoContext<Context>) = with(ui) {
    swipeRefreshLayout {
      layoutParams.height = matchParent
      layoutParams.width = matchParent
      id = R.id.refreshLayout

      frameLayout {
        lparams {
          id = R.id.mainLayout
          width = matchParent
          height = matchParent
        }
        recyclerView {
          id = R.id.pictureList
          lparams {
            marginStart = dip(16)
            marginEnd = dip(16)
            width = matchParent
            height = matchParent
            backgroundColor = R.color.colorAccent
          }
        }
      }
    }
  }

}