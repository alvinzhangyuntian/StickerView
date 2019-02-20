package com.phonenix.sticker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.Dimension;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.SparseArray;

/**
 * Customize your sticker with text and image background.
 * You can place some text into a given region, however,
 * you can also add a plain text sticker. To support text
 * auto resizing , I take most of the code from AutoResizeTextView.
 * See https://adilatwork.blogspot.com/2014/08/android-textview-which-resizes-its-text.html
 * Notice: It's not efficient to add long text due to too much of
 * StaticLayout object allocation.
 * Created by liutao on 30/11/2016.
 */

public class TextSticker extends Sticker {

    /**
     * Our ellipsis string.
     */
    private static final String mEllipsis = "\u2026";

    private final Context context;
//    private final Rect realBounds;
    private final Rect textRect;
    private final TextPaint textPaint;
    private Drawable drawable;
    private StaticLayout staticLayout;
    private Layout.Alignment alignment;
    private String text;
    private String text_origin="";
    private int maxWidth;
    /**
     * Upper bounds for text size.
     * This acts as a starting point for resizing.
     */
    private float maxTextSizePixels;

    /**
     * Lower bounds for text size.
     */
    private float minTextSizePixels;

    /**
     * Line spacing multiplier.
     */
    private float lineSpacingMultiplier = 1.0f;

    /**
     * Additional line spacing.
     */
    private float lineSpacingExtra = 0.0f;
    private float curTextSize = 25;
    /**
     * Map used to store views' tags.
     */
    private SparseArray<Object> mKeyedTags;
    private int stickerWidth_origin;
    private int stickerHeight_origin;
    private boolean isFirstGetWidth = true;
    private boolean isFirstGetHeight = true;

    public TextSticker(@NonNull Context context) {
        this(context, "", 100);
    }

    //  public TextSticker(@NonNull Context context, @Nullable Drawable drawable) {
//    this.context = context;
//    this.drawable = drawable;
//    if (drawable == null) {
//      this.drawable = ContextCompat.getDrawable(context, R.drawable.sticker_transparent_background);
//    }
//    textPaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
//    realBounds = new Rect(0, 0, getWidth(), getHeight());
//    textRect = new Rect(0, 0, getWidth(), getHeight());
//    minTextSizePixels = convertSpToPx(6);
//    maxTextSizePixels = convertSpToPx(32);
//    alignment = Layout.Alignment.ALIGN_CENTER;
//    textPaint.setTextSize(maxTextSizePixels);
//  }
    public TextSticker(@NonNull Context context, String text, int  maxWidth) {
        this.context = context;
        this.text = text;
        this.maxWidth = maxWidth;
        curTextSize = convertSpToPx(25);
        textPaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
        minTextSizePixels = convertSpToPx(12);
        maxTextSizePixels = convertSpToPx(50);
        alignment = Layout.Alignment.ALIGN_NORMAL;
        textPaint.setTextSize(curTextSize);
        textPaint.setStyle(Paint.Style.FILL);
//        realBounds = new Rect(0, 0, getWidth(), getHeight());
        textRect = new Rect(0, 0, getWidth(), getHeight());
        staticLayout =
                new StaticLayout(this.text, textPaint, textRect.width(), alignment, lineSpacingMultiplier,
                        lineSpacingExtra, true);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        Matrix matrix = getMatrix();
//        canvas.save();
//        canvas.concat(matrix);
//        if (drawable != null) {
//            drawable.setBounds(realBounds);
//            drawable.draw(canvas);
//        }
//        canvas.restore();

        canvas.save();
        canvas.concat(matrix);
        if (textRect.width() == getWidth()) {
            int dy = getHeight() / 2 - staticLayout.getHeight() / 2;
            // center vertical
            canvas.translate(0, dy);
        } else {
            int dx = textRect.left;
            int dy = textRect.top + textRect.height() / 2 - staticLayout.getHeight() / 2;
            canvas.translate(dx, dy);
        }
        staticLayout.draw(canvas);
        canvas.restore();
    }

    @Override
    public int getWidth() {
        // 单行
        if(!text.equals(text_origin)||isFirstGetWidth) {
            text_origin = text;
            isFirstGetWidth = false;
            if (text.split("\n").length < 2) {
                if (textPaint.measureText(text) < maxWidth) {
                    stickerWidth_origin = (int) textPaint.measureText(text)+ 18;
                    return stickerWidth_origin;
                } else {
                    stickerWidth_origin = maxWidth+ 18;
                    return stickerWidth_origin;
                }
            }
            String[] texts = text.split("\n");
            stickerWidth_origin = 0;
            int text_m;
            for (int i = 0; i < texts.length; i++) {
                if (textPaint.measureText(texts[i]) < maxWidth) {
                    text_m = (int) textPaint.measureText(texts[i]);
                } else {
                    text_m = maxWidth;
                }
                if (text_m > stickerWidth_origin) {
                    stickerWidth_origin = text_m;
                }
            }
            stickerWidth_origin = stickerWidth_origin + 18;
            return stickerWidth_origin;
        }
        return stickerWidth_origin;
    }

    @Override
    public int getHeight() {
        if(!text.equals(text_origin)||isFirstGetHeight) {
            text_origin= text;
            isFirstGetHeight = false;
            int lines = 0;
            String[] texts = text.split("\n");
            int line_m;
            for (int i = 0; i < texts.length; i++) {
                line_m = (int) Math.ceil(textPaint.measureText(texts[i]) / maxWidth);
                if (line_m > 1) {
                    lines += line_m;
                } else {
                    lines++;
                }
            }
            if (text.endsWith("\n")) {
                lines++;
            }
//        int lines = (int) Math.ceil(textPaint.measureText(text) / maxWidth);
//        Rect bounds = new Rect();
//        textPaint.getTextBounds(text, 0, text.length(), bounds);
            stickerHeight_origin = (int) (textPaint.getFontSpacing() * lines)+16;
            return stickerHeight_origin;
//        Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
//        return (Math.abs(fontMetrics.top) + Math.abs(fontMetrics.bottom)) * lines;//字体高度


//        float y = fontMetrics.descent+curTextSize/10+5;
//        float temph =(int)(fontMetrics.bottom-fontMetrics.top)/2;
//        temph += y;
//        return (int) (temph + 2)*lines +2;
        }
        return stickerHeight_origin;

    }

    @Override
    public int getMinWidth() {
        return stickerWidth_origin *12/25;
    }

    @Override
    public int getMinHeight() {
        return 0;
    }

    @Override
    public void release() {
        super.release();
//        if (drawable != null) {
//            drawable = null;
//        }
    }

    @NonNull
    @Override
    public TextSticker setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        textPaint.setAlpha(alpha);
        return this;
    }

    @NonNull
    @Override
    public Drawable getDrawable() {
        return drawable;
    }

    @Override
    public TextSticker setDrawable(@NonNull Drawable drawable) {
        this.drawable = drawable;
//        realBounds.set(0, 0, getWidth(), getHeight());
        textRect.set(0, 0, getWidth(), getHeight());
        return this;
    }

    @NonNull
    public TextSticker setDrawable(@NonNull Drawable drawable, @Nullable Rect region) {
        this.drawable = drawable;
//        realBounds.set(0, 0, getWidth(), getHeight());
        if (region == null) {
            textRect.set(0, 0, getWidth(), getHeight());
        } else {
            textRect.set(region.left, region.top, region.right, region.bottom);
        }
        return this;
    }

    @NonNull
    public TextSticker setTypeface(@Nullable Typeface typeface) {
        // 切换字体时，重置宽高
        isFirstGetWidth = true;
        isFirstGetHeight =true;
        textPaint.setTypeface(typeface);
        return this;
    }

    @NonNull
    public TextSticker setTextColor(@ColorInt int color) {
        textPaint.setColor(color);
        return this;
    }

    @NonNull
    public TextSticker setTextAlign(@NonNull Layout.Alignment alignment) {
        this.alignment = alignment;
        return this;
    }

    @NonNull
    public TextSticker setMaxTextSize(@Dimension(unit = Dimension.SP) float size) {
        curTextSize = size;
        textPaint.setTextSize(convertSpToPx(size));
        maxTextSizePixels = textPaint.getTextSize();
        return this;
    }

    public void setTag(int key, final Object tag) {
        if (mKeyedTags == null) {
            mKeyedTags = new SparseArray<Object>(2);
        }
        mKeyedTags.put(key, tag);
    }

    public Object getTag(int key) {
        if (mKeyedTags != null) return mKeyedTags.get(key);
        return null;
    }

    /**
     * Sets the lower text size limit
     *
     * @param minTextSizeScaledPixels the minimum size to use for text in this view,
     *                                in scaled pixels.
     */
    @NonNull
    public TextSticker setMinTextSize(float minTextSizeScaledPixels) {
        minTextSizePixels = convertSpToPx(minTextSizeScaledPixels);
        return this;
    }

    @NonNull
    public TextSticker setLineSpacing(float add, float multiplier) {
        lineSpacingMultiplier = multiplier;
        lineSpacingExtra = add;
        return this;
    }

    @NonNull
    public TextSticker setText(@Nullable String text) {
        this.text = text;
        return this;
    }

    @Nullable
    public String getText() {
        return text;
    }

    /**
     * Resize this view's text size with respect to its width and height
     * (minus padding). You should always call this method after the initialization.
     */
    @NonNull
    public TextSticker resizeText() {
//        final int availableHeightPixels = textRect.height();
//
//        final int availableWidthPixels = textRect.width();
//
//        final CharSequence text = getText();
//
//        // Safety check
//        // (Do not resize if the view does not have dimensions or if there is no text)
//        if (text == null
//                || text.length() <= 0
//                || availableHeightPixels <= 0
//                || availableWidthPixels <= 0
//                || maxTextSizePixels <= 0) {
//            return this;
//        }
        if (text == null
                || text.length() <= 0){
            return  this;
        }
//        float targetTextSizePixels = maxTextSizePixels;
//        int targetTextHeightPixels =
//                getTextHeightPixels(text, availableWidthPixels, targetTextSizePixels);
//
//        // Until we either fit within our TextView
//        // or we have reached our minimum text size,
//        // incrementally try smaller sizes
//        while (targetTextHeightPixels > availableHeightPixels
//                && targetTextSizePixels > minTextSizePixels) {
//            targetTextSizePixels = Math.max(targetTextSizePixels - 2, minTextSizePixels);
//
//            targetTextHeightPixels =
//                    getTextHeightPixels(text, availableWidthPixels, targetTextSizePixels);
//        }
//
//        // If we have reached our minimum text size and the text still doesn't fit,
//        // append an ellipsis
//        // (NOTE: Auto-ellipsize doesn't work hence why we have to do it here)
//        if (targetTextSizePixels == minTextSizePixels
//                && targetTextHeightPixels > availableHeightPixels) {
//            // Make a copy of the original TextPaint object for measuring
//            TextPaint textPaintCopy = new TextPaint(textPaint);
//            textPaintCopy.setTextSize(targetTextSizePixels);
//
//            // Measure using a StaticLayout instance
//            StaticLayout staticLayout =
//                    new StaticLayout(text, textPaintCopy, availableWidthPixels, Layout.Alignment.ALIGN_NORMAL,
//                            lineSpacingMultiplier, lineSpacingExtra, false);
//
//            // Check that we have a least one line of rendered text
//            if (staticLayout.getLineCount() > 0) {
//                // Since the line at the specific vertical position would be cut off,
//                // we must trim up to the previous line and add an ellipsis
//                int lastLine = staticLayout.getLineForVertical(availableHeightPixels) - 1;
//
//                if (lastLine >= 0) {
//                    int startOffset = staticLayout.getLineStart(lastLine);
//                    int endOffset = staticLayout.getLineEnd(lastLine);
//                    float lineWidthPixels = staticLayout.getLineWidth(lastLine);
//                    float ellipseWidth = textPaintCopy.measureText(mEllipsis);
//
//                    // Trim characters off until we have enough room to draw the ellipsis
//                    while (availableWidthPixels < lineWidthPixels + ellipseWidth) {
//                        endOffset--;
//                        lineWidthPixels =
//                                textPaintCopy.measureText(text.subSequence(startOffset, endOffset + 1).toString());
//                    }
//
//                    setText(text.subSequence(0, endOffset) + mEllipsis);
//                }
//            }
//        }
//        textPaint.setTextSize(curTextSize);
//            staticLayout =
//                    new StaticLayout(this.text, textPaint, textRect.width(), alignment, lineSpacingMultiplier,
//                            lineSpacingExtra, true);



        staticLayout =
                new StaticLayout(this.text, textPaint, getWidth(), alignment, lineSpacingMultiplier,
                        lineSpacingExtra, true);

        return this;
    }

    /**
     * @return lower text size limit, in pixels.
     */
    public float getMinTextSizePixels() {
        return minTextSizePixels;
    }

    /**
     * Sets the text size of a clone of the view's {@link TextPaint} object
     * and uses a {@link StaticLayout} instance to measure the height of the text.
     *
     * @return the height of the text when placed in a view
     * with the specified width
     * and when the text has the specified size.
     */
    protected int getTextHeightPixels(@NonNull CharSequence source, int availableWidthPixels,
                                      float textSizePixels) {
        textPaint.setTextSize(textSizePixels);
        // It's not efficient to create a StaticLayout instance
        // every time when measuring, we can use StaticLayout.Builder
        // since api 23.
        StaticLayout staticLayout =
                new StaticLayout(source, textPaint, availableWidthPixels, Layout.Alignment.ALIGN_NORMAL,
                        lineSpacingMultiplier, lineSpacingExtra, true);
        return staticLayout.getHeight();
    }

    /**
     * @return the number of pixels which scaledPixels corresponds to on the device.
     */
    private float convertSpToPx(float scaledPixels) {
        return scaledPixels * context.getResources().getDisplayMetrics().scaledDensity;
    }
}
