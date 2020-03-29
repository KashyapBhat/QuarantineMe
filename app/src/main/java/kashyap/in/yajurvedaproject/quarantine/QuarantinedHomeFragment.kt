package kashyap.`in`.yajurvedaproject.quarantine

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SmoothScroller.ScrollVectorProvider
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.face.Face
import com.google.android.gms.vision.face.FaceDetector
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kashyap.`in`.yajurvedaproject.R
import kashyap.`in`.yajurvedaproject.base.BaseFragment
import kashyap.`in`.yajurvedaproject.common.COUNT_DOWN_START_TIME
import kashyap.`in`.yajurvedaproject.common.QUARANTINE_DATA
import kashyap.`in`.yajurvedaproject.models.Quarantine
import kashyap.`in`.yajurvedaproject.utils.PrefUtils
import kotlinx.android.synthetic.main.fragment_quarantined_home.*

class QuarantinedHomeFragment : BaseFragment() {

    private val CAMERA_REQUEST = 1888
    private var quarantine: Quarantine? = null
    private var bannerAdapter: BannerAdapter? = null

    companion object {
        @JvmStatic
        fun newInstance(quarantine: Quarantine?) =
            QuarantinedHomeFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(QUARANTINE_DATA, quarantine)
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
            quarantine = it.getParcelable(QUARANTINE_DATA)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btPhoto?.setOnClickListener { takeImage() }
        handleBanner()
        handleStopWatch()
        handleEmergencyButton()
    }

    private fun handleBanner() {
        rvBanner?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        bannerAdapter = BannerAdapter(getImages(), context)
        rvBanner?.adapter = bannerAdapter
        bannerAdapter?.notifyDataSetChanged()
        val linearSnapHelper = SnapHelperOneByOne()
        linearSnapHelper.attachToRecyclerView(rvBanner)
        var i = 0
        object : CountDownTimer(100000, 5000) {

            override fun onFinish() {

            }

            override fun onTick(millisUntilFinished: Long) {
                if (i == getImages()?.size) {
                    i = 0
                }
                rvBanner?.smoothScrollToPosition(i++)
            }
        }.start()
    }

    private fun getImages(): List<String>? {
        val storage: FirebaseStorage = FirebaseStorage.getInstance()
        val pdfRef: StorageReference = storage.reference.child("banner")
        pdfRef.listAll()
            .addOnSuccessListener {
                Log.d("Firebase: ", "Url created $it")
            }.addOnFailureListener {
                Log.d("", it.toString())
            }
        return listOf(
            "https://firebasestorage.googleapis.com/v0/b/qarantineme.appspot.com/o/banner%2F1.png?alt=media&token=f8d8ee48-f0e0-4dac-aa9f-bdad983e6fa1",
            "https://firebasestorage.googleapis.com/v0/b/qarantineme.appspot.com/o/banner%2F2.png?alt=media&token=c84b46e0-74e2-4d58-bac1-5ab098fbdca7",
            "https://firebasestorage.googleapis.com/v0/b/qarantineme.appspot.com/o/banner%2F3.png?alt=media&token=79829f1e-0221-4561-b256-d78c59afce0f",
            "https://firebasestorage.googleapis.com/v0/b/qarantineme.appspot.com/o/banner%2F4.png?alt=media&token=e702ee6b-01d9-474a-b82b-593dbfc3668f",
            "https://firebasestorage.googleapis.com/v0/b/qarantineme.appspot.com/o/banner%2F5.png?alt=media&token=331c519c-b878-4cda-96bb-2ea3a5b4c966"
        )
    }

    class SnapHelperOneByOne : LinearSnapHelper() {
        override fun findTargetSnapPosition(
            layoutManager: RecyclerView.LayoutManager,
            velocityX: Int,
            velocityY: Int
        ): Int {
            if (layoutManager !is ScrollVectorProvider) {
                return RecyclerView.NO_POSITION
            }
            val currentView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
            val currentPosition = layoutManager.getPosition(currentView)
            return if (currentPosition == RecyclerView.NO_POSITION) {
                RecyclerView.NO_POSITION
            } else currentPosition
        }
    }

    private fun handleStopWatch() {
        // TODO: Save to firebase
        // Handle timings
        var countDownStartTime: Long = 0
        val xMins: Long = quarantine?.xMins ?: 15
        val currentTime: Long = System.currentTimeMillis();

        if (!PrefUtils.hasKey(getActivity(), COUNT_DOWN_START_TIME)) {
            countDownStartTime = currentTime
            PrefUtils.saveToPrefs(getActivity(), COUNT_DOWN_START_TIME, currentTime)
        } else {
            countDownStartTime =
                PrefUtils.getFromPrefs(getActivity(), COUNT_DOWN_START_TIME, currentTime) as Long
        }

        val timeRemaining = currentTime - countDownStartTime
        if (timeRemaining > xMins) {
            onTimeFinish()
        } else {
            object : CountDownTimer(xMins - timeRemaining, 1000) {

                override fun onFinish() {
                    onTimeFinish()
                }

                override fun onTick(millisUntilFinished: Long) {
                    val sec: Int = (millisUntilFinished / 1000).toInt()
                    val min: Int = (sec / 60)
                    val secRem: Int = (sec % 60)
                    tvStopwatchValue?.text = min.toString().plus(":").plus(secRem.toString())
                }
            }.start()
        }
    }

    private fun onTimeFinish() {
        tvStopwatchValue?.text = 0.toString().plus(":").plus(0.toString())

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
            faces.size() == 1 -> {
                showSnackBar(
                    "Face Detected",
                    "Great",
                    null
                )
                handleImageUpload(bitmap)
            }
            faces.size() > 1 -> showSnackBar(
                "More than one face detected",
                "Retry",
                Runnable { takeImage() }
            )
        }
    }

    private fun handleImageUpload(bitmap: Bitmap) {
        btPhoto?.text = "Image Added"
        // TODO: Store the bitmap into image
        // TODO: Save to firebase
        btSubmit?.setOnClickListener {

        }
    }

    private fun handleEmergencyButton() {
        btEmergency?.setOnClickListener {
            val phNumber = quarantine?.emergencyNumber ?: "9483214259"
            val uri = Uri.parse("tel:".plus(phNumber.trim()).trim())
            try {
                val callIntent = Intent(Intent.ACTION_CALL)
                callIntent.data = uri
                startActivity(callIntent)
            } catch (exception: Exception) {
                val callIntent = Intent(Intent.ACTION_DIAL)
                callIntent.data = uri
                startActivity(callIntent)
            }
        }
    }

}