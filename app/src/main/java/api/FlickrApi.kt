package api

import io.reactivex.Observable
import models.FlickrResponse
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface FlickrApi {
  @GET("/services/rest/?method=flickr.photos.search" +
      "&privacy_filter=1" +
      "&per_page=30" +
      "&format=json" +
      "&nojsoncallback=1")
  fun getSearchResults(@Query("api_key") key: String,
      @Query("text") searchText: String): Observable<FlickrResponse>

  @GET("services/rest/?method=flickr.photos.getInfo" +
      "&format=json" +
      "&nojsoncallback=1")
  fun getPhotoInfo(@Query("photo_id") photoId: String,
      @Query("api_key") key: String): Observable<FlickrResponse>

  @GET
  fun downloadPhoto(@Url fileUrl: String): Observable<ResponseBody>
}