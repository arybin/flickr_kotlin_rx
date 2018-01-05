package dagger.modules

import api.ApiManager
import api.FlickrApi
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by andrei on 1/4/18.
 */

@Module
@Singleton
class ApiModule {

    @Provides
    fun api(): FlickrApi = ApiManager.getFlickrService()
}