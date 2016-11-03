package org.caojun.salmagundi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import org.caojun.salmagundi.bankcard.BankCardActivity;
import org.caojun.salmagundi.color.Color;
import org.caojun.salmagundi.color.ColorActivity;
import org.caojun.salmagundi.color.ColorUtils;
import org.caojun.salmagundi.qrcode.QRCodeActivity;
import org.caojun.salmagundi.utils.DataStorageUtils;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    private Button btnQRCode, btnColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        this.startActivity(new Intent(this, QRCodeActivity.class));
//        this.startActivity(new Intent(this, ColorActivity.class));
        btnQRCode = (Button) this.findViewById(R.id.btnQRCode);
        btnColor = (Button) this.findViewById(R.id.btnColor);
        Button btnBankCard = (Button) this.findViewById(R.id.btnBankCard);

        btnQRCode.setOnClickListener(this);
        btnColor.setOnClickListener(this);
        btnBankCard.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        createColorIcon();
    }

    /**
     * 更新渐变色按钮图标
     */
    private void createColorIcon()
    {
        Color[] color = ColorUtils.getSavedColors(this);
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.icon_color);
        int density = (int) getResources().getDisplayMetrics().density;
        Color[] colors = ColorUtils.getGradientColor(color[0], color[1], bitmap.getWidth() * density);
        Bitmap bmGradient = ColorUtils.createGradientImage(colors, bitmap.getWidth() * density, bitmap.getHeight() * density);
        if(bmGradient != null) {
            Drawable drawable = new BitmapDrawable(bmGradient);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            btnColor.setCompoundDrawables(null, drawable, null, null);
        }
        Integer[] intColorStart = new Integer[]{color[0].getAlpha(), color[0].getRed(), color[0].getGreen(), color[0].getBlue()};
        Integer[] intColorEnd = new Integer[]{color[1].getAlpha(), color[1].getRed(), color[1].getGreen(), color[1].getBlue()};
        DataStorageUtils.saveIntArray(this, "GradientColor", "colorStart", intColorStart);
        DataStorageUtils.saveIntArray(this, "GradientColor", "colorEnd", intColorEnd);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch(v.getId())
        {
            case R.id.btnQRCode:
                intent = new Intent(this, QRCodeActivity.class);
                break;
            case R.id.btnColor:
                intent = new Intent(this, ColorActivity.class);
                break;
            case R.id.btnBankCard:
                intent = new Intent(this, BankCardActivity.class);
                break;
            default:
                return;
        }
        this.startActivity(intent);
    }
}
