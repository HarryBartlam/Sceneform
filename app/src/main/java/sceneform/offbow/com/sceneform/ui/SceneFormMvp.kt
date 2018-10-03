package sceneform.offbow.com.sceneform.ui


import sceneform.offbow.com.sceneform.arch.BaseMvp

interface SceneFormMvp:BaseMvp {
    interface View : BaseMvp.View {

    }

    interface Presenter : BaseMvp.Presenter {

    }
}