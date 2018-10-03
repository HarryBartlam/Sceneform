package sceneform.offbow.com.sceneform.ui


import com.google.ar.core.Frame
import com.google.ar.core.Session
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Scene
import sceneform.offbow.com.sceneform.arch.BaseMvp

interface SceneFormMvp:BaseMvp {
    interface View : BaseMvp.View {
        fun placeAnchorNode(node: AnchorNode)
        fun setSession(session: Session)
    }

    interface Presenter : BaseMvp.Presenter {

        fun onResumeContinued()
        fun onPause()
        fun onFrameUpdate(arFrame: Frame, scene: Scene)
    }
}