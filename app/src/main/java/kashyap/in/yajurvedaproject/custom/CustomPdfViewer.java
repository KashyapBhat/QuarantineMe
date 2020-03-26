package kashyap.in.yajurvedaproject.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

import kashyap.in.yajurvedaproject.R;

/**
 * Created by Kashyap Bhat on 2019-12-20.
 */
public class CustomPdfViewer extends ViewPager {
    protected Context context;

    public CustomPdfViewer(Context context, String pdfPath) {
        super(context);
        this.context = context;
        init(pdfPath);
    }

    public CustomPdfViewer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(attrs);
    }

    protected void init(String pdfPath) {
        initAdapter(context, pdfPath);
    }

    protected void init(AttributeSet attrs) {
        if (isInEditMode()) {
            setBackgroundResource(R.drawable.flaticon_pdf_dummy);
            return;
        }

        if (attrs != null) {
            TypedArray a;

            a = context.obtainStyledAttributes(attrs, R.styleable.PDFViewPager);
            String assetFileName = a.getString(R.styleable.PDFViewPager_assetFileName);

            if (assetFileName != null && assetFileName.length() > 0) {
                initAdapter(context, assetFileName);
            }

            a.recycle();
        }
    }

    protected void initAdapter(Context context, String pdfPath) {
        setAdapter(new CustomPdfAdapter.Builder(context)
                .setPdfPath(pdfPath)
                .setOffScreenSize(getOffscreenPageLimit())
                .create());
    }

    /**
     * PDFViewPager uses PhotoView, so this bugfix should be added
     * Issue explained in https://github.com/chrisbanes/PhotoView
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }
}
