package kashyap.`in`.yajurvedaproject.common

/**
 * Created by Kashyap Bhat on 2019-12-17.
 */
//Intent
const val INTENT_CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE"
const val OPEN_URL = "URL"
const val QUARANTINE_DATA = "QuarantineData"
const val WEBVIEW_URL = "WebviewURL"


//FireStore
const val FS_COLLECTION_NAME = "Documents"
const val FS_DOCUMENT_NAME = "Path"

// KEYS
const val FS_FB_STORAGE_PATH_KEY = "FBStorageFilePath"
const val FS_PHONE_STORAGE_PATH_KEY = "PhoneStoragePath"
const val FS_PHONE_STORAGE_FILE_NAME_KEY = "PhoneStorageFileName"
const val FS_SHOULD_REFRESH_KEY = "ShouldRefresh"
const val FS_GOOGLE_DOC_RENDERER_URL_KEY = "GoogleDocRendererUrl"
const val FS_PDF_STORAGE_URL_KEY = "FBPdfStorageUrl"
const val FS_MIN_APP_VERSION_KEY = "MinAppVersion"

//DEFAULT
const val DEFAULT_FB_STORAGE_PATH = "PdfFiles/yjv.pdf"
const val DEFAULT_FOLDER_NAME = "storage/bin"
const val DEFAULT_FILE_NAME = "yjf.pdf"
const val DEFAULT_SHOULD_REFRESH = true
const val DEFAULT_DOCS_RENDERER_URL = "https://docs.google.com/gview?embedded=true&url="
const val DEFAULT_PDF_STORAGE_URL =
    "https://firebasestorage.googleapis.com/v0/b/yajurvedaapp.appspot.com/o/PdfFiles%2Fyjv.pdf?alt=media&token=3e53abe5-3e54-453e-9ff3-bb39d704973d"
const val DEFAULT_MIN_APP_VERSION = 1

//Request Codes
const val ALARM_REQUEST_CODE = 1101
const val REMINDER_WORKER = "REMINDER_WORKER"

//SharedPref keys
const val IS_REMINDER_ALREADY_SET = "isReminderAlreadySet"
const val IS_QUARANTINED = "ISQuarantined"
const val HOME_LOCATION = "HomeLocation"
const val HOME_ADDRESS = "HomeAddress"
const val LATITUDE = "Latitude"
const val LONGITUDE = "Longitude"
const val COUNT_DOWN_START_TIME = "countDownStartTime"
const val USER_PHONE_NUMBER = "UserPhoneNumber"
const val USER_ID = "UserID"


//View related

const val MENU_ONE: Int = kashyap.`in`.yajurvedaproject.R.id.navOne
const val MENU_TWO: Int = kashyap.`in`.yajurvedaproject.R.id.navTwo
const val MENU_THREE: Int = kashyap.`in`.yajurvedaproject.R.id.navThree
const val MENU_FOUR: Int = kashyap.`in`.yajurvedaproject.R.id.navFour

//Interactions helper

const val WEBVIEW_FRAGMENT = 1
const val INFO_FRAGMENT = 2