package mvp

/**
 * Created by andrei on 1/4/18.
 */
interface Presenter {
    fun onCreated()
    fun onResume()
    fun onPause()
    fun onStop()
    fun onDestroy()
}
