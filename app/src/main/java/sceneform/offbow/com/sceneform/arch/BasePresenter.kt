package sceneform.offbow.com.sceneform.arch

abstract class BasePresenter<T : BaseMvp.View> protected constructor(protected var view: T?) : BaseMvp.Presenter {
    override fun onDestroy() {
        view = null
    }
}
