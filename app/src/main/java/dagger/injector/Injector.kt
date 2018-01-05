package dagger.injector

import dagger.FlickrComponent

/**
 * Created by andrei on 1/4/18.
 */
object Injector {
    private var component: FlickrComponent? = null

    fun init(component: FlickrComponent) {
        this.component = component
    }

    fun component(): FlickrComponent? = component
}