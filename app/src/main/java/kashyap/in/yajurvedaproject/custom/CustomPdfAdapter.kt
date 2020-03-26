package kashyap.`in`.yajurvedaproject.custom

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.view.View
import android.view.ViewGroup

import androidx.viewpager.widget.ViewPager

import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView

import es.voghdev.pdfviewpager.library.adapter.BasePDFPagerAdapter
import es.voghdev.pdfviewpager.library.adapter.PdfScale
import es.voghdev.pdfviewpager.library.util.EmptyClickListener
import kashyap.`in`.yajurvedaproject.R

/**
 * Created by Kashyap Bhat on 2019-12-20.
 */
class CustomPdfAdapter(context: Context, pdfPath: String) : BasePDFPagerAdapter(context, pdfPath) {

    internal var scale = PdfScale()
    internal var pageClickListener: View.OnClickListener = EmptyClickListener()
    private var ssiv: SubsamplingScaleImageView? = null

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val v = inflater.inflate(R.layout.view_pdf_page, container, false)
        ssiv = v.findViewById(R.id.subsamplingImageView)

        if (renderer == null || count < position) {
            return v
        }
        val page = getPDFPage(renderer, position)

        val bitmap = bitmapContainer.get(position)
        ssiv!!.setImage(ImageSource.bitmap(bitmap))
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        page.close()
        container.addView(v, 0)

        return v
    }

    override fun close() {
        super.close()
    }

    class Builder(internal var context: Context) {
        internal var pdfPath = ""
        internal var scale = DEFAULT_SCALE
        internal var centerX = 0f
        internal var centerY = 0f
        internal var offScreenSize = BasePDFPagerAdapter.DEFAULT_OFFSCREENSIZE
        internal var renderQuality = BasePDFPagerAdapter.DEFAULT_QUALITY
        internal var pageClickListener: View.OnClickListener = EmptyClickListener()

        fun setScale(scale: Float): CustomPdfAdapter.Builder {
            this.scale = scale
            return this
        }

        fun setScale(scale: PdfScale): CustomPdfAdapter.Builder {
            this.scale = scale.scale
            this.centerX = scale.centerX
            this.centerY = scale.centerY
            return this
        }

        fun setCenterX(centerX: Float): CustomPdfAdapter.Builder {
            this.centerX = centerX
            return this
        }

        fun setCenterY(centerY: Float): CustomPdfAdapter.Builder {
            this.centerY = centerY
            return this
        }

        fun setRenderQuality(renderQuality: Float): CustomPdfAdapter.Builder {
            this.renderQuality = renderQuality
            return this
        }

        fun setOffScreenSize(offScreenSize: Int): CustomPdfAdapter.Builder {
            this.offScreenSize = offScreenSize
            return this
        }

        fun setPdfPath(path: String): CustomPdfAdapter.Builder {
            this.pdfPath = path
            return this
        }

        fun setOnPageClickListener(listener: View.OnClickListener?): CustomPdfAdapter.Builder {
            if (listener != null) {
                pageClickListener = listener
            }
            return this
        }

        fun create(): CustomPdfAdapter {
            val adapter = CustomPdfAdapter(context, pdfPath)
            adapter.scale.scale = scale
            adapter.scale.centerX = centerX
            adapter.scale.centerY = centerY
            adapter.offScreenSize = offScreenSize
            adapter.renderQuality = renderQuality
            adapter.pageClickListener = pageClickListener
            return adapter
        }
    }

    companion object {

        private val DEFAULT_SCALE = 1f
    }
}
