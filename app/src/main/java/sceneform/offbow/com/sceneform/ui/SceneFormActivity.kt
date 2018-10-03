package sceneform.offbow.com.sceneform.ui

import android.os.Bundle
import sceneform.offbow.com.sceneform.R
import sceneform.offbow.com.sceneform.arch.BaseActivity

class SceneFormActivity : BaseActivity<SceneFormMvp.Presenter>(), SceneFormMvp.View {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = SceneFormPresenter(this)
        setContentView(R.layout.activity_scene_form)
    }
}
