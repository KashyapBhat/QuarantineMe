package kashyap.`in`.yajurvedaproject.quarantine

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SmoothScroller.ScrollVectorProvider
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.face.Face
import com.google.android.gms.vision.face.FaceDetector
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kashyap.`in`.yajurvedaproject.R
import kashyap.`in`.yajurvedaproject.base.BaseActivity
import kashyap.`in`.yajurvedaproject.base.BaseFragment
import kashyap.`in`.yajurvedaproject.common.*
import kashyap.`in`.yajurvedaproject.issue.IssueAdapter
import kashyap.`in`.yajurvedaproject.utils.GeneralUtils
import kashyap.`in`.yajurvedaproject.utils.PrefUtils
import kotlinx.android.synthetic.main.fragment_quarantined_home.*
import java.io.ByteArrayOutputStream

class QuarantinedHomeFragment : BaseFragment() {

    private val CAMERA_REQUEST = 1888
    private var bannerAdapter: BannerAdapter? = null
    private var dialog: BottomSheetDialog? = null

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
        customShowAndHideProgress(true)
        return inflater.inflate(R.layout.fragment_quarantined_home, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun afterFBDataFetch() {
        customShowAndHideProgress(true)
        handleQuarantineOrNot()
        btPhoto?.setOnClickListener { takeImage() }
        btIssue?.setOnClickListener { handleIssue() }
        tvActiveValue?.text = quarantine?.activeCase
        tvCuredValue?.text = quarantine?.curedCase
        tvDeathValue?.text = quarantine?.deathCase
        handleBanner()
        handleStopWatch()
        handleEmergencyButton()
        customShowAndHideProgress(false)
    }

    private fun handleIssue() {
        val selectedIssues = ArrayList<String>()
        val sheetView: View =
            LayoutInflater.from(context).inflate(R.layout.select_health_issue, null, false)
        dialog = context?.let { BottomSheetDialog(it, R.style.DialogStyle) }
        if (dialog?.isShowing == true) {
            dialog?.dismiss()
        }
        val btnDone = sheetView.findViewById<Button>(R.id.btDone)
        val writtenIssue: EditText = sheetView.findViewById(R.id.etIssueWritten)
        val rvList: RecyclerView = sheetView.findViewById(R.id.rvIssue)
        val manager = LinearLayoutManager(context)
        rvList.layoutManager = manager
        val issueAdapter = IssueAdapter(getIssuesList(), context, selectedIssues)
        rvList.adapter = issueAdapter
        btnDone.setOnClickListener {
            checkIssuesAndSaveToFB(writtenIssue.text?.toString()?.trim(), selectedIssues)
            dialog?.dismiss()
        }
        dialog?.setContentView(sheetView)
        dialog?.show()
    }

    private fun getIssuesList(): List<String?>? {
        return quarantine?.healthIssues ?: emptyList()
    }

    private fun checkIssuesAndSaveToFB(
        writtenIssue: String?,
        selectedIssues: ArrayList<String>
    ) {
        val init = hashMapOf(
            ISSUE_SELECTED to TextUtils.join(", ", selectedIssues),
            WRITTEN_ISSUE to writtenIssue
        )
        val db = FirebaseFirestore.getInstance()
        db.collection(PrefUtils.userId(context))
            .document(ISSUES_DOC)
            .set(init)
            .addOnSuccessListener { documentReference ->
                Log.d("TAG", "DocumentSnapshot added with ID: $documentReference")

            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error adding document", e)
                showSnackBar("Something went wrong", "Retry", null)
            }
    }

    private fun handleQuarantineOrNot() {
        cvMain?.visibility = if (PrefUtils.isQuarantined(context)) View.VISIBLE else View.GONE
    }

    override fun showProgress() {

    }

    override fun hideProgress() {

    }

    private fun customShowAndHideProgress(shouldShowProgress: Boolean) {
        if (shouldShowProgress) {
            (context as BaseActivity?)?.showProgress()
        } else Handler().postDelayed({ (context as BaseActivity?)?.hideProgress() }, 400)
    }

    private fun handleBanner() {
        rvBanner?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        bannerAdapter = BannerAdapter(getImages(), context)
        rvBanner?.adapter = bannerAdapter
        bannerAdapter?.notifyDataSetChanged()
        rvBanner?.onFlingListener = null
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

    private fun getImages(): List<String?>? {
        return quarantine?.bannerImages ?: emptyList()
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
        val xMins: Long = quarantine?.xMins ?: 15
        val timeRemaining: Long = getTimeRemaining()
        enableSubmitScreen(false)
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

    private fun getTimeRemaining(): Long {
        val currentTime: Long = System.currentTimeMillis()
        var countDownStartTime: Long = 0
        var countDownFBStartTime: Long = 0
        if (!PrefUtils.hasKey(getActivity(), COUNT_DOWN_START_TIME)) {
            countDownStartTime = currentTime
            PrefUtils.saveToPrefs(getActivity(), COUNT_DOWN_START_TIME, currentTime)
        } else {
            countDownStartTime =
                PrefUtils.getFromPrefs(
                    getActivity(),
                    COUNT_DOWN_START_TIME,
                    currentTime
                ) as Long
        }

        val db = FirebaseFirestore.getInstance()
        db.collection("userData")
            .document("userId1")
            .get()
            .addOnSuccessListener {
                Log.d("Firebase Document:", "Data: " + it.data?.entries)
                countDownFBStartTime =
                    it?.data?.get(COUNT_DOWN_START_TIME) as Long? ?: 0.toLong()
            }.addOnFailureListener {
                Log.d("Firebase Document:", "Data: $it")
            }
        if (countDownFBStartTime == 0.toLong()) {
            val user = hashMapOf(
                "countDownStartTime" to countDownStartTime
            )
            db.collection("userData")
                .document("userId1")
                .set(user)
                .addOnSuccessListener { documentReference ->
                    Log.d("TAG", "DocumentSnapshot added with ID: $documentReference")
                }
                .addOnFailureListener { e ->
                    Log.w("TAG", "Error adding document", e)
                }

        }
        return currentTime - countDownStartTime
    }

    private fun onTimeFinish() {
        enableSubmitScreen(true)
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

    private fun detectFace(bitmap: Bitmap?) {
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

    private fun handleImageUpload(bitmap: Bitmap?) {
        enableImageButton(false)
        // TODO: Store the bitmap into image
        // TODO: Save to firebase
        btSubmit?.setOnClickListener {
            if (etTempValue?.text?.isEmpty() == true || bitmap == null) {
                showSnackBar(
                    "Please add image, temperature value, issues if any and then submit",
                    "Okay",
                    null
                )
            }
            storeTemperatureValue()
            storeImageInCloud(bitmap)
        }
    }

    private fun enableImageButton(enable: Boolean) {
        btPhoto?.text = if (enable) "Add Image" else "Image Added"
        btPhoto?.setTextColor(resources.getColor(if (enable) R.color.black else R.color.grey))
        btPhoto?.setBackgroundColor(resources.getColor(if (enable) R.color.white else R.color.darker_grey))
        btPhoto?.isEnabled = enable
    }

    private fun enableSubmitScreen(enable: Boolean) {
        llAdd?.visibility = if (enable) View.VISIBLE else View.GONE
        rlTemp?.visibility = if (enable) View.VISIBLE else View.GONE
        tvStopwatch?.text =
            if (enable) "Please enter the required data and submit." else "You will be prompted to submit your data after stopwatch ends."
        btSubmit?.visibility = if (enable) View.VISIBLE else View.GONE
        tvStopwatchValue?.visibility = if (!enable) View.VISIBLE else View.GONE

    }

    private fun storeImageInCloud(bitmap: Bitmap?) {
        val storage: FirebaseStorage = FirebaseStorage.getInstance()
        val ref: StorageReference = storage.reference.child("userFace")
        val mountainsRef = ref.child("image.jpg")
        val baos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask = mountainsRef.putBytes(data)
        uploadTask.addOnSuccessListener {
            enableImageButton(true)
        }.addOnFailureListener {
            enableImageButton(true)
        }
    }

    private fun storeTemperatureValue() {
        val user = hashMapOf(
            "Temperature" to etTempValue?.text?.toString()
        )
        val db = FirebaseFirestore.getInstance()
        db.collection("userData")
            .document("userId1")
            .set(user)
            .addOnSuccessListener { documentReference ->
                Log.d("TAG", "DocumentSnapshot added with ID: $documentReference")
                etTempValue?.setText("")
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error adding document", e)
                etTempValue?.setText("")
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

    override fun onDestroy() {
        super.onDestroy()
        dialog?.dismiss()
    }

}