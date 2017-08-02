package com.example.andreirybin.janetest.activities.util

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import com.example.andreirybin.janetest.activities.service.FlickrApi

class ApiManager {

    companion object {
      private val retrofit: Retrofit =
          Retrofit.Builder()
              .baseUrl(Constants.BASE_FLICKR_URL)
              .addConverterFactory(GsonConverterFactory.create())
              .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
              .build()

      private val flickr = retrofit.create(FlickrApi::class.java)

      fun getFlickrService() : FlickrApi {
        return flickr
      }
    }


}