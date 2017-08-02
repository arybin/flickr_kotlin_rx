package repositories

/**
 * Created by arybin on 8/1/17.
 */
interface RepositoryCallbacks<in T> {
  fun onLoadFinished(response: T?)
  fun onError(t: Throwable)
}
