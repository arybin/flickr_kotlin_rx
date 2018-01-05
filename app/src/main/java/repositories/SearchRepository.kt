package repositories

import api.await
import models.FlickrResponse
import mvp.BaseModel
import util.Constants

class SearchRepository: BaseModel() {

    suspend fun requestSearchAsync(searchParameter: String) : FlickrResponse =
            api.getSearchResultsCoroutines(Constants.KEY, searchParameter).await()

}