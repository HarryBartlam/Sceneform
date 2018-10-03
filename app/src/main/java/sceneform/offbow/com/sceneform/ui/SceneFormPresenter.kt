package sceneform.offbow.com.sceneform.ui

import android.content.Context
import android.media.MediaPlayer
import com.google.ar.core.*
import com.google.ar.core.exceptions.CameraNotAvailableException
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ExternalTexture
import com.google.ar.sceneform.rendering.ModelRenderable
import sceneform.offbow.com.sceneform.R
import sceneform.offbow.com.sceneform.arch.AppModule
import sceneform.offbow.com.sceneform.arch.BasePresenter
import timber.log.Timber

class SceneFormPresenter(view: SceneFormMvp.View,
                          val context: Context = AppModule.application) : BasePresenter<SceneFormMvp.View>(view), SceneFormMvp.Presenter {
    private lateinit var session: Session
    private val androidAnchor = AnchorNode()

    private lateinit var androidMediaPlayer: MediaPlayer
    lateinit var androidExternalTexture: ExternalTexture
    private var androidVideoRenderable: ModelRenderable? = null

    override fun onResumeContinued() {
        configureSession()
        setupMediaPlayers()
        try {
            session.resume()
        } catch (e: CameraNotAvailableException) {
            Timber.e(e)
            return
        }
    }

    override fun onPause() {
        if (this::session.isInitialized) {
            session.pause()
        }
    }

    private fun configureSession() {
        if (this::session.isInitialized.not()) {
            session = Session(context)

            //setup AugmentedImageDatabase
            val config = Config(session)
            val inputStream = context.assets.open("imageRec.imgdb")
            val imageDatabase = AugmentedImageDatabase.deserialize(session, inputStream)
            config.augmentedImageDatabase = imageDatabase
            config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
            config.planeFindingMode = Config.PlaneFindingMode.DISABLED
            session.configure(config)

            view?.setSession(session)
        } else {
            return
        }
    }

    override fun onFrameUpdate(arFrame: Frame, scene: Scene) {
        val updatedAugmentedImages = arFrame.getUpdatedTrackables(AugmentedImage::class.java)

        for (augmentedImage in updatedAugmentedImages) {
            if (augmentedImage.trackingState == TrackingState.TRACKING) {
                if (augmentedImage.index == 0) {//index one is the android img as per imageRec.imgdb-imglist.txt
                    setNodeForAndroid(augmentedImage, scene)
                }
            }
        }
    }

    private fun setupMediaPlayers() {

        androidExternalTexture = ExternalTexture()

        androidMediaPlayer = MediaPlayer.create(context, R.raw.android)
        androidMediaPlayer.setSurface(androidExternalTexture.surface)
        androidMediaPlayer.isLooping = true


        ModelRenderable.builder()
                .setSource(context, R.raw.black_plane)
                .build()
                .thenAccept { renderable ->
                    androidVideoRenderable = renderable
                    renderable.getMaterial().setExternalTexture("videoTexture", androidExternalTexture)
                }.exceptionally {
                    Timber.e(it, "Unable to display black plane modal")
                    return@exceptionally null
                }
    }

    private fun setNodeForAndroid(image: AugmentedImage, scene: Scene) {

        if (androidVideoRenderable == null) {
            return
        }

        androidAnchor.anchor = image.createAnchor(image.centerPose)
        androidAnchor.setParent(scene)

        val node = Node()

        node.localScale = Vector3(0.019f, 0.0035f, 0.013f)// model is not using the correct metrics so done manually
//        Timber.d("$$$ got ${image.extentX}, ${image.extentZ}")
        node.localRotation = Quaternion.axisAngle(Vector3(0f, 1f, 0f), -90f)
        node.setParent(androidAnchor)

        if (!androidMediaPlayer.isPlaying) {
            androidMediaPlayer.start()
            androidExternalTexture
                    .surfaceTexture
                    .setOnFrameAvailableListener { _ ->
                        node.renderable = androidVideoRenderable
                        androidExternalTexture.surfaceTexture.setOnFrameAvailableListener(null)
                    }
        } else {
            node.renderable = androidVideoRenderable
        }
        view?.placeAnchorNode(androidAnchor)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::androidMediaPlayer.isInitialized) {
            androidMediaPlayer.release()
        }
    }
}