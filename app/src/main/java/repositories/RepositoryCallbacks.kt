package repositories

interface RepositoryCallbacks<in T> {
  fun onLoadFinished(response: T?)
  fun onError(t: Throwable)
}
