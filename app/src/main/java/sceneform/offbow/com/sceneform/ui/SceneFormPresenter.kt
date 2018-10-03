package sceneform.offbow.com.sceneform.ui

import android.content.Context
import sceneform.offbow.com.sceneform.arch.AppModule
import sceneform.offbow.com.sceneform.arch.BasePresenter

class SceneFormPresenter(view: SceneFormMvp.View,
                          val context: Context = AppModule.application) : BasePresenter<SceneFormMvp.View>(view), SceneFormMvp.Presenter {


}