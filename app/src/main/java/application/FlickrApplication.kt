package application

import android.content.Context
import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication
import com.facebook.stetho.Stetho
import dagger.DaggerFlickrComponent
import dagger.FlickrComponent
import dagger.injector.Injector
import dagger.modules.ApiModule
import dagger.modules.DatabaseModule

/**
 * Created by andrei on 1/4/18.
 */
class FlickrApplication : MultiDexApplication() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        Injector.init(createDaggerComponent())
        Stetho.initializeWithDefaults(this)
    }

    private fun createDaggerComponent(): FlickrComponent =
            DaggerFlickrComponent.builder()
                    .databaseModule(DatabaseModule(this))
                    .apiModule(ApiModule())
                    .build()

}