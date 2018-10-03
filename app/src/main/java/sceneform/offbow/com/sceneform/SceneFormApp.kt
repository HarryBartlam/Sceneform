package sceneform.offbow.com.sceneform

import android.app.Application
import sceneform.offbow.com.sceneform.arch.AppModule


open class SceneFormApp : Application() {
    override fun onCreate() {
        AppModule.application = this
        super.onCreate()

    }
}
