package kashyap.`in`.yajurvedaproject.quarantine

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.face.Face
import com.google.android.gms.vision.face.FaceDetector
import kashyap.`in`.yajurvedaproject.R
import kashyap.`in`.yajurvedaproject.base.BaseActivity
import kashyap.`in`.yajurvedaproject.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_quarantined_home.*

class QuarantinedHomeFragment : BaseFragment() {
    private var url: String = ""
    private val CAMERA_REQUEST = 1888

    companion object {
        @JvmStatic
        fun newInstance() =
            QuarantinedHomeFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onCreateViewSetter(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_quarantined_home, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btPhoto.setOnClickListener { takeImage() }
    }

    private fun takeImage() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(
            "android.intent.extras.CAMERA_FACING",
            android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT
        )
        cameraIntent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1)
        cameraIntent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true)
        startActivityForResult(cameraIntent, CAMERA_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            val photo = data?.extras!!["data"] as Bitmap?
            photo?.let { detectFace(it) }
        }
    }

    private fun detectFace(bitmap: Bitmap) {
        val detector: FaceDetector = FaceDetector.Builder(context)
            .setTrackingEnabled(false)
            .setLandmarkType(FaceDetector.ALL_LANDMARKS)
            .setMode(FaceDetector.FAST_MODE)
            .build()

        val frame: Frame = Frame.Builder().setBitmap(bitmap).build()
        val faces: SparseArray<Face> = detector.detect(frame)
        for (index in 0 until faces.size()) {
            val face = faces.valueAt(index)
        }
        when {
            faces.size() < 1 -> showSnackBar(
                "Face Not Detected",
                "Retry",
                Runnable { takeImage() }
            )
            faces.size() == 1 -> showSnackBar(
                "Face Detected",
                "Great",
                null
            )
            faces.size() > 1 -> showSnackBar(
                "More than one face detected",
                "Retry",
                Runnable { takeImage() }
            )
        }
    }
}