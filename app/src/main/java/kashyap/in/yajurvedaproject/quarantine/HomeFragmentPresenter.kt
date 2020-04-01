package kashyap.`in`.yajurvedaproject.quarantine

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.CountDownTimer
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.face.Face
import com.google.android.gms.vision.face.FaceDetector
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kashyap.`in`.yajurvedaproject.R
import kashyap.`in`.yajurvedaproject.common.COUNT_DOWN_START_TIME
import kashyap.`in`.yajurvedaproject.common.ISSUES_DOC
import kashyap.`in`.yajurvedaproject.common.ISSUE_SELECTED
import kashyap.`in`.yajurvedaproject.common.WRITTEN_ISSUE
import kashyap.`in`.yajurvedaproject.issue.IssueAdapter
import kashyap.`in`.yajurvedaproject.models.Quarantine
import kashyap.`in`.yajurvedaproject.quarantine.QuarantinedHomeFragment.Companion.CAMERA_REQUEST
import kashyap.`in`.yajurvedaproject.utils.GeneralUtils
import kashyap.`in`.yajurvedaproject.utils.PrefUtils
import java.io.ByteArrayOutputStream

class HomeFragmentPresenter(
    val context: Context?,
    val view: HomeContract.view?,
    val quarantine: Quarantine?
) {

    private var bannerAdapter: BannerAdapter? = null
    private var bitmap: Bitmap? = null

    fun showBanner(rvBanner: RecyclerView?) {
        rvBanner?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        bannerAdapter = BannerAdapter(getImages(), context)
        rvBanner?.adapter = bannerAdapter
        bannerAdapter?.notifyDataSetChanged()
        rvBanner?.onFlingListener = null
        val linearSnapHelper = SnapHelperOneByOne()
        linearSnapHelper.attachToRecyclerView(rvBanner)
        if (getImages()?.size ?: 0 > 1) {
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
    }

    private fun getImages(): List<String?>? {
        return quarantine?.bannerImages ?: emptyList()
    }

    fun handleStopWatch() {
        // TODO: Save to firebase
        // Handle timings
        val xMins: Long = quarantine?.xMins ?: 15
        val timeRemaining: Long = getTimeRemaining()
        view?.handleSubmitScreen(false)
        if (timeRemaining > xMins) {
            view?.handleSubmitScreen(true)
        } else {
            object : CountDownTimer(xMins - timeRemaining, 1000) {

                override fun onFinish() {
                    view?.handleSubmitScreen(true)
                }

                override fun onTick(millisUntilFinished: Long) {
                    val sec: Int = (millisUntilFinished / 1000).toInt()
                    val min: Int = (sec / 60)
                    val secRem: Int = (sec % 60)
                    view?.showStopWatchText(min.toString().plus(":").plus(secRem.toString()))
                }
            }.start()
        }
    }

    private fun getTimeRemaining(): Long {
        val currentTime: Long = System.currentTimeMillis()
        var countDownStartTime: Long = 0
        var countDownFBStartTime: Long = 0
        if (!PrefUtils.hasKey(context, COUNT_DOWN_START_TIME)) {
            countDownStartTime = currentTime
            PrefUtils.saveToPrefs(context, COUNT_DOWN_START_TIME, currentTime)
        } else {
            countDownStartTime =
                PrefUtils.getFromPrefs(
                    context,
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

    fun takeImage(activity: Fragment?) {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(
            "android.intent.extras.CAMERA_FACING",
            android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT
        )
        cameraIntent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1)
        cameraIntent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true)
        activity?.startActivityForResult(cameraIntent, CAMERA_REQUEST)
    }

    fun detectFace(
        bitmap: Bitmap?,
        activity: Fragment?
    ) {
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
            faces.size() < 1 -> view?.showHomeSnackBar("Face Not Detected", "Retry",
                Runnable { takeImage(activity) }
            )
            faces.size() == 1 -> {
                view?.showHomeSnackBar("Face Detected", "Great", null)
                this.bitmap = bitmap
                view?.handleImageButton(false)
            }
            faces.size() > 1 -> view?.showHomeSnackBar("More than one face detected", "Retry",
                Runnable { takeImage(activity) }
            )
        }
    }

    fun onSubmitButtonClick(etTempValue: EditText?) {
        if (etTempValue?.text?.isEmpty() == true || bitmap == null) {
            view?.showHomeSnackBar(
                "Please add image, temperature value, issues if any and then submit",
                "Okay", null
            )
            return
        }
        view?.showHomeSnackBar("Your data is submitted.", "Great", null)
        storeTemperatureValue(etTempValue)
        storeImageInCloud(bitmap)
    }

    private fun storeTemperatureValue(etTempValue: EditText?) {
        val user = hashMapOf(
            "Temperature" to etTempValue?.text?.toString()
        )
        val db = FirebaseFirestore.getInstance()
        db.collection("userData")
            .document("userId1")
            .set(user)
            .addOnSuccessListener { documentReference ->
                Log.d("TAG", "DocumentSnapshot added with ID: $documentReference")
                etTempValue?.clearFocus()
                etTempValue?.setText("")
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error adding document", e)
                etTempValue?.clearFocus()
                etTempValue?.setText("")
            }
    }

    private fun storeImageInCloud(bitmap: Bitmap?) {
        val storage: FirebaseStorage = FirebaseStorage.getInstance()
        val ref: StorageReference = storage.reference.child("userFace")
            .child(PrefUtils.userId(context))
        val mountainsRef = ref.child(
            (GeneralUtils.getCurrentDate()).plus("---").plus(GeneralUtils.getCurentTime())
        )
        val baos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask = mountainsRef.putBytes(data)
        uploadTask.addOnSuccessListener {
            view?.handleImageButton(true)
            view?.handleIssuesButton(true)
        }.addOnFailureListener {
            view?.handleImageButton(true)
            view?.handleIssuesButton(true)
        }
    }

    fun showIssue(): BottomSheetDialog? {
        val selectedIssues = ArrayList<String>()
        val sheetView: View =
            LayoutInflater.from(context).inflate(R.layout.select_health_issue, null, false)
        val dialog: BottomSheetDialog? = context?.let { BottomSheetDialog(it, R.style.DialogStyle) }
        if (dialog?.isShowing == true) {
            dialog.dismiss()
        }
        val btnDone = sheetView.findViewById<Button>(R.id.btDone)
        val writtenIssue: EditText = sheetView.findViewById(R.id.etIssueWritten)
        val rvList: RecyclerView = sheetView.findViewById(R.id.rvIssue)
        val manager = LinearLayoutManager(context)
        rvList.layoutManager = manager
        val issueAdapter = IssueAdapter(getIssuesList(), context, selectedIssues)
        rvList.adapter = issueAdapter
        btnDone.setOnClickListener {
            view?.handleIssuesButton(false)
            checkIssuesAndSaveToFB(
                writtenIssue.text?.toString()?.trim(),
                selectedIssues
            )
            dialog?.dismiss()
        }
        dialog?.setContentView(sheetView)
        dialog?.show()
        return dialog
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
                view?.showHomeSnackBar("Something went wrong", "Retry", null)
            }
    }

    private fun getIssuesList(): List<String?>? {
        return quarantine?.healthIssues ?: emptyList()
    }

    fun handleEmergencyButton(activity: FragmentActivity?) {
        val phNumber = quarantine?.emergencyNumber ?: "9483214259"
        val uri = Uri.parse("tel:".plus(phNumber.trim()).trim())
        try {
            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = uri
            if (activity?.let {
                    ActivityCompat.checkSelfPermission(
                        it,
                        Manifest.permission.CALL_PHONE
                    )
                } == PackageManager.PERMISSION_GRANTED
            ) {
                activity.startActivity(callIntent)
            }
        } catch (exception: Exception) {
            val callIntent = Intent(Intent.ACTION_DIAL)
            callIntent.data = uri
            activity?.startActivity(callIntent)
        }
    }

    class SnapHelperOneByOne : LinearSnapHelper() {
        override fun findTargetSnapPosition(
            layoutManager: RecyclerView.LayoutManager,
            velocityX: Int,
            velocityY: Int
        ): Int {
            if (layoutManager !is RecyclerView.SmoothScroller.ScrollVectorProvider) {
                return RecyclerView.NO_POSITION
            }
            val currentView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
            val currentPosition = layoutManager.getPosition(currentView)
            return if (currentPosition == RecyclerView.NO_POSITION) {
                RecyclerView.NO_POSITION
            } else currentPosition
        }
    }

    fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}