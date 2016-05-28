package com.xx.radarview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Administrator on 2016/5/28.
 */
public class RadarView extends View {

    //顶点个数
    private int vertexCount;
    //默认标题
    private String[] titles = {"android", "objective-C", "swift", "java", "oracle", "php"};
    //每两个点之间的角度
    private float angle;
    //以中心点到顶点出的半径
    private float radius;
    //数据最大值
    private float maxValue = 100;
    //每条线上对应的值
    private double[] data = {60, 40, 50, 80, 50, 20};
    //中心X坐标
    private int centerX;
    //中心Y坐标
    private int centerY;
    //标题画笔
    private Paint titlePaint;
    //网格画笔
    private Paint vertexPaint;
    //覆盖区画笔
    private Paint valuePaint;
    //标题字体大小
    private int titleTextSize;
    //标题字体颜色
    private int titleTextColor;

    private int widthMeasure;
    private int heightMeasure;

    //设置标题（每个顶点要显示的文字）
    public void setTitles(String[] titles) {
        this.titles = titles;
    }

    //设置顶点个数
    public void setVertexCount(int vertexCount) {
        this.vertexCount = vertexCount;
    }

    //设置数值
    public void setData(double[] data) {
        this.data = data;
    }

    //设置最大数值
    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }

    //设置网格颜色
    public void setVertexPaintColor(int color) {
        vertexPaint.setColor(color);
    }

    //设置标题颜色
    public void setTitlePaintColor(int color) {
        titlePaint.setColor(color);
    }

    //设置覆盖局域颜色
    public void setValuePaintColor(int color) {
        valuePaint.setColor(color);
    }

    public RadarView(Context context) {
        this(context, null);
    }

    public RadarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RadarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        vertexCount = titles.length;
        angle = (float) (Math.PI * 2 / vertexCount);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RadarView);
        titleTextColor = array.getColor(R.styleable.RadarView_title_text_color, context.getColor(R.color.colorPrimary));
        titleTextSize = (int) array.getDimension(R.styleable.RadarView_title_text_size, 20);

        vertexPaint = new Paint();
        vertexPaint.setAntiAlias(true);
        vertexPaint.setColor(Color.GRAY);
        vertexPaint.setStyle(Paint.Style.STROKE);

        valuePaint = new Paint();
        valuePaint.setAntiAlias(true);
        valuePaint.setColor(Color.BLUE);
        valuePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        titlePaint = new Paint();
        titlePaint.setTextSize(titleTextSize);
        titlePaint.setStyle(Paint.Style.FILL);
        titlePaint.setColor(titleTextColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = widthMeasureSpec;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawVertex(canvas);
        drawLines(canvas);
        drawTitle(canvas);
        drawRegion(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //在该方法中获取view的中心点
        centerX = w / 2;
        centerY = h / 2;
        //计算半径，取宽高中值小一点的，然后留一点边距
        radius = Math.min(w, h) * 0.3f;
        //postInvalidate可以直接在子线程中刷新view
        postInvalidate();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * 根据vertextCount绘制网格
     *
     * @param canvas
     */
    private void drawVertex(Canvas canvas) {
        Path path = new Path();
        //计算内外两个点之间的距离
        float d = radius / (vertexCount - 1);
        //中心点不用绘制，所以i从1开始
        for (int i = 1; i < vertexCount; i++) {
            float curR = d * i;//当前半径，从里往外绘制
            path.reset();
            for (int j = 0; j < vertexCount; j++) {
                if (j == 0) {
                    path.moveTo(centerX + curR, centerY);
                } else {
                    //根据半径计算出下一个点的坐标位置
                    float x = (float) (centerX + curR * Math.cos(angle * j));
                    float y = (float) (centerY + curR * Math.sin(angle * j));
                    path.lineTo(x, y);
                }
            }
            path.close();
            //绘制
            canvas.drawPath(path, vertexPaint);
        }

    }

    /**
     * 绘制从中心点到最外边点的直线
     */
    private void drawLines(Canvas canvas) {
        Path path = new Path();
        for (int i = 0; i < vertexCount; i++) {
            path.reset();
            path.moveTo(centerX, centerY);
            float x = (float) (centerX + radius * Math.cos(angle * i));
            float y = (float) (centerY + radius * Math.sin(angle * i));
            path.lineTo(x, y);
            canvas.drawPath(path, vertexPaint);
        }
    }

    /**
     * 绘制标题
     *
     * @param canvas
     */
    private void drawTitle(Canvas canvas) {
        Paint.FontMetrics fontMetrics = titlePaint.getFontMetrics();
        float fontHeight = fontMetrics.descent - fontMetrics.ascent;
        for (int i = 0; i < vertexCount; i++) {
            float x = (float) (centerX + (radius + fontHeight / 2) * Math.cos(angle * i));
            float y = (float) (centerY + (radius + fontHeight / 2) * Math.sin(angle * i));
            if (angle * i >= 0 && angle * i <= Math.PI / 2) {//第4象限
                canvas.drawText(titles[i], x, y, titlePaint);
            } else if (angle * i >= 3 * Math.PI / 2 && angle * i <= Math.PI * 2) {//第3象限
                canvas.drawText(titles[i], x, y, titlePaint);
            } else if (angle * i > Math.PI / 2 && angle * i <= Math.PI) {//第2象限
                float textLen = titlePaint.measureText(titles[i]);//文本长度
                canvas.drawText(titles[i], x - textLen, y, titlePaint);
            } else if (angle * i >= Math.PI && angle * i < 3 * Math.PI / 2) {//第1象限
                float textLen = titlePaint.measureText(titles[i]);//文本长度
                canvas.drawText(titles[i], x - textLen, y, titlePaint);
            }
        }
    }

    /**
     * 绘制覆盖区域
     * @param canvas
     */
    private void drawRegion(Canvas canvas){
        Path path = new Path();
        valuePaint.setAlpha(255);
        for(int i = 0; i < vertexCount; i++){
            double percent = data[i] / maxValue;
            float x = (float) (centerX + radius * Math.cos(angle * i) * percent);
            float y = (float) (centerY + radius * Math.sin(angle * i) * percent);
            if(i == 0){
                path.moveTo(x, centerY);
            }else{
                path.lineTo(x,y);
            }
            //绘制小圆点
            valuePaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(x, y, 10, valuePaint);
            if (i == vertexCount - 1) {
                x = (float) (centerX + radius * Math.cos(angle * 0) * data[0] / maxValue);
                y = (float) (centerY + radius * Math.sin(angle * 0) * data[0] / maxValue);
                path.lineTo(x,y);
            }
        }
        valuePaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path, valuePaint);
        valuePaint.setAlpha(125);
        //绘制填充区域
        valuePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawPath(path, valuePaint);
    }

}
