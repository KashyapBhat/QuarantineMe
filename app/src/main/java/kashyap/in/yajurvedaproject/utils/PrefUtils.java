package kashyap.in.yajurvedaproject.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import java.lang.ref.WeakReference;

import kashyap.in.yajurvedaproject.common.ConstantsKt;

/**
 * THANKS TO : https://gist.github.com/john1jan/ for this.
 * Created by john.francis on 24/05/16.
 */
public class PrefUtils {

    public static void saveToPrefs(Context context, String key, Object value) {
        WeakReference<Context> contextWeakReference = new WeakReference<Context>(context);
        if (contextWeakReference.get() != null) {
            SharedPreferences prefs =
                    PreferenceManager.getDefaultSharedPreferences(contextWeakReference.get());
            final SharedPreferences.Editor editor = prefs.edit();
            if (value instanceof Integer) {
                editor.putInt(key, ((Integer) value).intValue());
            } else if (value instanceof String) {
                editor.putString(key, value.toString());
            } else if (value instanceof Boolean) {
                editor.putBoolean(key, ((Boolean) value).booleanValue());
            } else if (value instanceof Long) {
                editor.putLong(key, ((Long) value).longValue());
            } else if (value instanceof Float) {
                editor.putFloat(key, ((Float) value).floatValue());
            } else if (value instanceof Double) {
                editor.putLong(key, Double.doubleToRawLongBits((double) value));
            }
            editor.commit();
        }
    }

    public static boolean hasKey(Context context, String key) {
        WeakReference<Context> contextWeakReference = new WeakReference<Context>(context);
        if (contextWeakReference.get() != null) {
            SharedPreferences prefs =
                    PreferenceManager.getDefaultSharedPreferences(contextWeakReference.get());
            return prefs.contains(key);
        }
        return false;
    }

    public static Object getFromPrefs(Context context, String key, Object defaultValue) {
        WeakReference<Context> contextWeakReference = new WeakReference<Context>(context);
        if (contextWeakReference.get() != null) {
            SharedPreferences sharedPrefs =
                    PreferenceManager.getDefaultSharedPreferences(contextWeakReference.get());
            try {
                if (defaultValue instanceof String) {
                    return sharedPrefs.getString(key, defaultValue.toString());
                } else if (defaultValue instanceof Integer) {
                    return sharedPrefs.getInt(key, ((Integer) defaultValue).intValue());
                } else if (defaultValue instanceof Boolean) {
                    return sharedPrefs.getBoolean(key, ((Boolean) defaultValue).booleanValue());
                } else if (defaultValue instanceof Long) {
                    return sharedPrefs.getLong(key, ((Long) defaultValue).longValue());
                } else if (defaultValue instanceof Float) {
                    return sharedPrefs.getFloat(key, ((Float) defaultValue).floatValue());
                } else if (defaultValue instanceof Double) {
                    return Double.longBitsToDouble(sharedPrefs.getLong(key, Double.doubleToLongBits((double) defaultValue)));
                }
            } catch (Exception e) {
                Log.e("Execption", e.getMessage());
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public static boolean hasQuarantineValue(Context context) {
        return PrefUtils.hasKey(context, ConstantsKt.IS_QUARANTINED);
    }

    public static boolean isQuarantined(Context context) {
        return (boolean) PrefUtils.getFromPrefs(context, ConstantsKt.IS_QUARANTINED, false);
    }

    public static long userPhoneNumber(Context context) {
        return (long) PrefUtils.getFromPrefs(context, ConstantsKt.USER_PHONE_NUMBER, "0");
    }

    public static String userId(Context context) {
        return PrefUtils.getFromPrefs(context, ConstantsKt.USER_PHONE_NUMBER, "").toString().trim();
    }

    public static void removeFromPrefs(Context context, String key) {
        WeakReference<Context> contextWeakReference = new WeakReference<Context>(context);
        if (contextWeakReference.get() != null) {
            SharedPreferences prefs =
                    PreferenceManager.getDefaultSharedPreferences(contextWeakReference.get());
            final SharedPreferences.Editor editor = prefs.edit();
            editor.remove(key);
            editor.commit();
        }
    }
}
