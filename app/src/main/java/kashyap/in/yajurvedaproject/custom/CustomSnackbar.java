package kashyap.in.yajurvedaproject.custom;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;

import com.google.android.material.snackbar.BaseTransientBottomBar;

import kashyap.in.yajurvedaproject.R;

/**
 * Created by Kashyap Bhat on 2019-12-18.
 */

public class CustomSnackbar extends BaseTransientBottomBar<CustomSnackbar> {

    private CustomSnackbar(ViewGroup parent, View content, ContentViewCallback callback) {
        super(parent, content, callback);
    }

    public static CustomSnackbar make(@NonNull ViewGroup parent, @Duration int duration) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View content = inflater.inflate(R.layout.snackbar_layout, parent, false);
        final ContentViewCallback viewCallback = new ContentViewCallback(content);
        final CustomSnackbar customSnackbar = new CustomSnackbar(parent, content, viewCallback);

        customSnackbar.getView().setPadding(0, 0, 0, 0);
        customSnackbar.setDuration(duration);
        return customSnackbar;
    }

    public CustomSnackbar setText(CharSequence text) {
        TextView textView = getView().findViewById(R.id.snackbar_text);
        textView.setText(text);
        return this;
    }

    public CustomSnackbar setAction(CharSequence text, final View.OnClickListener listener) {
        Button actionView = getView().findViewById(R.id.snackbar_action);
        actionView.setText(text);
        actionView.setVisibility(View.VISIBLE);
        actionView.setOnClickListener(view -> {
            listener.onClick(view);
            dismiss();
        });
        return this;
    }

    private static class ContentViewCallback implements BaseTransientBottomBar.ContentViewCallback {

        private View content;

        public ContentViewCallback(View content) {
            this.content = content;
        }

        @Override
        public void animateContentIn(int delay, int duration) {
            ViewCompat.setScaleY(content, 0f);
            ViewCompat.animate(content).scaleY(1f).setDuration(duration).setStartDelay(delay);
        }

        @Override
        public void animateContentOut(int delay, int duration) {
            ViewCompat.setScaleY(content, 1f);
            ViewCompat.animate(content).scaleY(0f).setDuration(duration).setStartDelay(delay);
        }
    }
}
