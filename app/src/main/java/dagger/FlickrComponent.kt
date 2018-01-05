package dagger

import application.FlickrApplication
import dagger.modules.ApiModule
import dagger.modules.DatabaseModule
import mvp.BaseModel
import javax.inject.Singleton

/**
 * Created by andrei on 1/4/18.
 */
@Singleton
@Component(modules = [DatabaseModule::class, ApiModule::class])
interface FlickrComponent {
    fun inject(app: FlickrApplication)
    fun inject(model: BaseModel)
}