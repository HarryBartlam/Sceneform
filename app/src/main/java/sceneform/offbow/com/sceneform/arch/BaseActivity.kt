package sceneform.offbow.com.sceneform.arch

import android.support.v7.app.AppCompatActivity

abstract class BaseActivity<T : BaseMvp.Presenter> : AppCompatActivity(), BaseMvp.View {
    protected lateinit var presenter: T

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }
}