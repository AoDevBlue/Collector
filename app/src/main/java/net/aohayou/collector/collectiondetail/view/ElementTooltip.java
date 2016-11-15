package net.aohayou.collector.collectiondetail.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;

import net.aohayou.collector.R;
import net.aohayou.collector.util.ColorUtil;

public class ElementTooltip extends View {

    public static final class Direction {
        public static final int TOP_LEFT     = 0;
        public static final int TOP_RIGHT    = 1;
        public static final int BOTTOM_RIGHT = 2;
        public static final int BOTTOM_LEFT  = 3;

        private Direction() {}
    }

    private int size;
    private int direction;
    private Path path;
    private String text;

    private Paint backgroundPaint;
    private Paint textPaint;
    private Rect textBounds;

    public ElementTooltip(Context context) {
        super(context);
        setupDefaultValues();
        init();
    }

    public ElementTooltip(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDefaultValues();
        readAttributes(attrs, 0, 0);
        init();
    }

    public ElementTooltip(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupDefaultValues();
        readAttributes(attrs, defStyleAttr, 0);
        init();
    }

    @RequiresApi(Build.VERSION_CODES.M)
    public ElementTooltip(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setupDefaultValues();
        readAttributes(attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setupDefaultValues() {
        direction = 1;
    }

    public void readAttributes(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs, R.styleable.ElementTooltip, defStyleAttr, defStyleRes);

        try {
            direction = a.getInt(R.styleable.ElementTooltip_pointer, direction);
        } finally {
            a.recycle();
        }
    }

    private void init() {
        initBackgroundPaint();
        initTextPaint();
        textBounds = new Rect();
    }

    private void initBackgroundPaint() {
        backgroundPaint = new Paint();
        int color = ColorUtil.getColor(getContext(), R.color.acquired_element_focus);
        backgroundPaint.setColor(color);
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
    }

    private void initTextPaint() {
        textPaint = new Paint();
        int color = ColorUtil.getColor(getContext(),
                R.color.material_typography_primary_text_color_light);
        textPaint.setColor(color);
        float dimen = getResources().getDimension(
                material.values.R.dimen.material_typography_regular_display_1_text_size);
        textPaint.setTextSize(dimen);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    private void initPath() {
        path = new Path();

        RectF rect = new RectF(0, 0, size, size);
        path.lineTo(0, size/2);
        path.arcTo(rect, 180, -270);
        path.lineTo(0, 0);
        path.close();

        int angle;
        switch(direction) {
            case Direction.TOP_LEFT:
            default:
                angle = 0;
                break;
            case Direction.TOP_RIGHT:
                angle = 90;
                break;
            case Direction.BOTTOM_RIGHT:
                angle = 180;
                break;
            case Direction.BOTTOM_LEFT:
                angle = 270;
        }
        Matrix rotationMatrix = new Matrix();
        rotationMatrix.postRotate(angle, size/2, size/2);
        path.transform(rotationMatrix);
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
        initPath();
        invalidate();
    }

    public int getColor() {
        return backgroundPaint.getColor();
    }

    public void setColor(@ColorInt int color) {
        backgroundPaint.setColor(color);
    }

    @Nullable
    public String getText() {
        return text;
    }

    public void setText(@Nullable String text) {
        this.text = text;
    }

    public Point getPointerRelativePosition() {
        switch (direction) {
            case ElementTooltip.Direction.TOP_LEFT:
            default:
                return new Point(0, 0);
            case ElementTooltip.Direction.TOP_RIGHT:
                return new Point(getMeasuredWidth(), 0);
            case ElementTooltip.Direction.BOTTOM_RIGHT:
                return new Point(getMeasuredWidth(), getMeasuredHeight());
            case ElementTooltip.Direction.BOTTOM_LEFT:
                return new Point(0, getMeasuredHeight());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        size = (int) getResources().getDimension(
                material.values.R.dimen.material_baseline_grid_9x);

        setMeasuredDimension(size, size);

        initPath();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setOutlineProvider(new TooltipOutline());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawPath(path, backgroundPaint);
        if (text != null) {
            textPaint.getTextBounds(text, 0, text.length(), textBounds);
            canvas.drawText(text, canvas.getWidth()/2, canvas.getHeight()/2 - textBounds.exactCenterY(), textPaint);
        }
    }

    private class TooltipOutline extends ViewOutlineProvider {
        @Override
        public void getOutline(View view, Outline outline) {
            outline.setConvexPath(path);
        }
    }
}
