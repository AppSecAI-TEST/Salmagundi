package org.caojun.salmagundi.clicaptcha;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by CaoJun on 2017/6/26.
 */

public class CaptchaView extends View {

    private static final byte N = 4;
    //将背景分成WIDTH*HEIGHT个格子
    private static final byte WIDTH = N + 2;
    private static final byte HEIGHT = N + 1;
    private static final byte COUNT = WIDTH * HEIGHT;
    private byte[] idCount = new byte[COUNT];

    private CaptchaImage[] buttons;

    public CaptchaView(Context context) {
        this(context, null);
    }

    public CaptchaView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CaptchaView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        init(context, N);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(WIDTH * CaptchaImage.SIZE, HEIGHT * CaptchaImage.SIZE);
    }

    public void init(Context context, byte n) {
        if (n <= 0) {
            return;
        }
        buttons = new CaptchaImage[n];
        for (byte i = 0;i < n;i ++) {
            buttons[i] = new CaptchaImage(context);
            buttons[i].setClickable(true);
            int id = getRandomId();
            while (idCount[id] > 0) {
                id = getRandomId();
            }
            idCount[id] = (byte)(i + 1);
        }
    }

    private int getRandomId() {
        int id = CaptchaUtils.getRandom(WIDTH + 1, COUNT - WIDTH - 2);
        while (id % WIDTH == 0 || id % WIDTH == WIDTH - 1) {
            id = CaptchaUtils.getRandom(WIDTH + 1, COUNT - WIDTH - 2);
        }
        return id;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(CaptchaImage.BG_COLOR);
        for (byte i = 0;i < COUNT;i ++) {
            if (idCount[i] == 0) {
                continue;
            }
            int id = idCount[i] - 1;
            int x0 = i % WIDTH;
            int y0 = i / WIDTH;
            int x = x0 * CaptchaImage.SIZE;
            int y = y0 * CaptchaImage.SIZE;
            canvas.translate(x , y);
            buttons[id].draw(canvas);
            canvas.translate(-x, -y);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            int id = getButtonID(event.getX(), event.getY());
            if (id > 0) {
                //TODO
                return false;
            }
        }
        return true;
    }

    private int getButtonID(float x, float y) {
        int x0 = (int)(x / CaptchaImage.SIZE);
        int y0 = (int)(y / CaptchaImage.SIZE);
        int id = y0 * WIDTH + x0;
        return idCount[id];
    }
}
