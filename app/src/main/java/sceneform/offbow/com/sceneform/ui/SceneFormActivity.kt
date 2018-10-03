package sceneform.offbow.com.sceneform.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import com.fondesa.kpermissions.extension.listeners
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.google.ar.core.Session
import com.google.ar.sceneform.AnchorNode
import kotlinx.android.synthetic.main.activity_scene_form.*
import sceneform.offbow.com.sceneform.R
import sceneform.offbow.com.sceneform.arch.BaseActivity

class SceneFormActivity : BaseActivity<SceneFormMvp.Presenter>(), SceneFormMvp.View {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = SceneFormPresenter(this)
        setContentView(R.layout.activity_scene_form)
        scene_view.planeRenderer

        scene_view.scene.addOnUpdateListener { _ ->
            presenter.onFrameUpdate(scene_view.arFrame, scene_view.scene)
        }
    }

    override fun onResume() {
        super.onResume()
        onHasCameraPermission()
    }

    private fun onHasCameraPermission() {
        val request = permissionsBuilder(Manifest.permission.CAMERA).build()
        var permissionDenied = false
        request.listeners {
            onAccepted { _ ->
                presenter.onResumeContinued()
                scene_view.resume()
            }
            onDenied { _ ->
                permissionDenied = true
                Snackbar.make(scene_view, R.string.camera_permission_deny, Snackbar.LENGTH_LONG)
                        .show()
            }
            onPermanentlyDenied { _ ->
                permissionDenied = true
                Snackbar.make(scene_view, R.string.camera_permission_disabled, Snackbar.LENGTH_LONG)
                        .setAction(R.string.camera_permission_disabled_actions) {
                            goToSettings()
                        }
                        .show()
            }
            onShouldShowRationale { _, nonce ->
                if (!permissionDenied) {
                    nonce.use()
                    permissionDenied = true
                }
            }
        }
        request.send()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
        scene_view.pause()
    }

    override fun setSession(session: Session) {
        scene_view.setupSession(session)
    }

    override fun placeAnchorNode(node: AnchorNode) {
        scene_view.scene.addChild(node)
    }

    private fun goToSettings() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.data = Uri.parse("package:" + this.packageName)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        ContextCompat.startActivity(this, intent, null)
    }
}
