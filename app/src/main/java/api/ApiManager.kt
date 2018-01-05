package api

import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import util.Constants
import java.util.concurrent.TimeUnit.SECONDS

object ApiManager {

  private val retrofit: Retrofit =
      Retrofit.Builder()
          .client(getClient())
          .baseUrl(Constants.BASE_FLICKR_URL)
          .addConverterFactory(GsonConverterFactory.create())
          .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
          .build()

  private fun getClient() : OkHttpClient {
    return OkHttpClient.Builder()
        .addNetworkInterceptor(StethoInterceptor())
        .retryOnConnectionFailure(true)
        .connectTimeout(10, SECONDS)
        .build()
  }
  private val flickr = retrofit.create(FlickrApi::class.java)

  fun getFlickrService(): FlickrApi {
    return flickr
  }

}