package org.caojun.cameracolor.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import org.caojun.cameracolor.Constant;
import org.caojun.cameracolor.R;
import org.caojun.cameracolor.utils.ColorUtils;
import java.text.DecimalFormat;

/**
 * HSV转RGB
 * Created by CaoJun on 2017/7/21.
 */

@Route(path = Constant.ACTIVITY_HSV2RGB)
public class HSV2RGBActivity extends AppCompatActivity {
    private EditText etRGB;
    private ImageView[] ivMinus, ivPlus;
    private EditText[] etColor;
    private SeekBar[] sbColor;
    private final static int[] IDMinus = {R.id.ivMinusH, R.id.ivMinusS, R.id.ivMinusV};
    private final static int[] IDPlus = {R.id.ivPlusH, R.id.ivPlusS, R.id.ivPlusV};
    private final static int[] MaxHSV = {359, 100, 100};
    private final static int[] IDETColor = {R.id.etH, R.id.etS, R.id.etV};
    private final static int[] IDSBColor = {R.id.sbH, R.id.sbS, R.id.sbV};

    private ImageView ivColor;
    private EditText[] etRGBs;
    private final static int[] IDETRGB = {R.id.etR, R.id.etG, R.id.etB};

    @Autowired
    protected String HEX;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hsv2rgb);
        ARouter.getInstance().inject(this);

        etRGB = (EditText) findViewById(R.id.etRGB);
        ivMinus = new ImageView[IDMinus.length];
        for (int i = 0;i < IDMinus.length;i ++) {
            ivMinus[i] = (ImageView) findViewById(IDMinus[i]);
            final int index = i;
            ivMinus[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        float color = Float.parseFloat(etColor[index].getText().toString());
                        color --;
                        if (color < 0) {
                            color = 0;
                        }
                        int icolor = (int) color;
                        setEditTextColor(etColor[index], icolor == color?String.valueOf(icolor):String.valueOf(color));
                        sbColor[index].setProgress(icolor);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        ivPlus = new ImageView[IDPlus.length];
        for (int i = 0;i < IDPlus.length;i ++) {
            ivPlus[i] = (ImageView) findViewById(IDPlus[i]);
            final int index = i;
            ivPlus[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        float color = Float.parseFloat(etColor[index].getText().toString());
                        color ++;
                        if (color > MaxHSV[index]) {
                            color = MaxHSV[index];
                        }
                        int icolor = (int) color;
                        setEditTextColor(etColor[index], icolor == color?String.valueOf(icolor):String.valueOf(color));
                        sbColor[index].setProgress(icolor);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        etColor = new EditText[IDETColor.length];
        for (int i = 0;i < IDETColor.length;i ++) {
            etColor[i] = (EditText) findViewById(IDETColor[i]);
            final int index = i;
            etColor[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String text = s.toString();
                    if (TextUtils.isEmpty(text)) {
                        sbColor[index].setProgress(0);
                        return;
                    }
                    float color = Float.parseFloat(text);
                    if (color > MaxHSV[index]) {
                        color = MaxHSV[index];
                    } else if (color < 0) {
                        color = 0;
                    }
                    sbColor[index].setProgress((int) color);
                }
            });
        }
        sbColor = new SeekBar[IDSBColor.length];
        for (int i = 0;i < IDSBColor.length;i ++) {
            sbColor[i] = (SeekBar) findViewById(IDSBColor[i]);
            final int index = i;
            sbColor[i].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (!fromUser) {
                        return;
                    }
                    setEditTextColor(etColor[index], String.valueOf(progress));
                    resetEditTextRGB();
                    setRGB();
                    resetImageView();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }

        ivColor = (ImageView) findViewById(R.id.ivColor);
        etRGBs = new EditText[IDETRGB.length];
        for (int i = 0;i < IDETRGB.length;i ++) {
            etRGBs[i] = (EditText) findViewById(IDETRGB[i]);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(HEX)) {
            etRGB.setText(HEX);
            etRGB.setSelection(HEX.length());

            int color = Integer.parseInt(HEX, 16);
            float hsv[] = {0, 0, 0};
            ColorUtils.RGBtoHSV(Color.red(color), Color.green(color), Color.blue(color), hsv);
            for (int i = 0;i < hsv.length;i ++) {
                String text;
                switch(i) {
                    case 0:
                        text = String.valueOf((int)hsv[i]);
                        break;
                    default:
                        hsv[i] *= 100;
                        DecimalFormat decimalFormat = new DecimalFormat("0.0");
                        text = decimalFormat.format(hsv[i]);
                        break;
                }
                setEditTextColor(etColor[i], text);
                sbColor[i].setProgress((int) hsv[i]);
            }

            setRGB();

            ViewTreeObserver vto = ivColor.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    ivColor.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    resetImageView();
                }
            });
        }
    }

    private void resetImageView() {
        int color = getColor();
        ivColor.setBackgroundColor(color);
    }

    private void setRGB() {
        int rgb = Integer.parseInt(HEX, 16);
        int[] color = {Color.red(rgb), Color.green(rgb), Color.blue(rgb)};
        for (int i = 0;i < etRGBs.length;i ++) {
            String text = String.valueOf(color[i]);
            etRGBs[i].setText(text);
            etRGBs[i].setSelection(text.length());
        }
    }

    private void setEditTextColor(EditText editText, String value) {
        editText.setText(value);
        editText.setSelection(value.length());
    }

    private void resetEditTextRGB() {
        int color = getColor();
        HEX = ColorUtils.toHexEncoding(color);
        etRGB.setText(HEX.toUpperCase());
        etRGB.setSelection(HEX.length());
    }

    private int getColor() {
        int h = sbColor[0].getProgress();
        int s = sbColor[1].getProgress();
        int v = sbColor[2].getProgress();
        float hsv[] = {h, s, v};
        return ColorUtils.HSVtoRGB(hsv);
    }
}
