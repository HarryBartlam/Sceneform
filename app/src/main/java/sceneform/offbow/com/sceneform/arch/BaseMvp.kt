package sceneform.offbow.com.sceneform.arch

interface BaseMvp {
    interface View {

    }

    interface Presenter {
        fun onDestroy()
    }
}