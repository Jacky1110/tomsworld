package com.jotangi.tomsworld.common;


import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import java.util.Random;
/**
 * Created by carolyn on 2017/11/10.
 * 用於圖片驗證碼的工具類
 */

public class CodeUtils {
    private static final char[] CHARS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };

    private static CodeUtils mCodeUtils;
    private int mPaddingLeft, mPaddingTop;
    private StringBuilder mBuilder = new StringBuilder();
    private Random mRandom = new Random();

    //Default Settings
    private static final int DEFAULT_CODE_LENGTH = 5;//驗證碼的長度這裡是5位
    private static final int DEFAULT_FONT_SIZE = 120;//字體大小
    private static final int DEFAULT_LINE_NUMBER = 6;//多少條干擾線
    private static final int BASE_PADDING_LEFT = 100; //左邊距
    private static final int RANGE_PADDING_LEFT = 60;//左邊距範圍值
    private static final int BASE_PADDING_TOP = 100;//上邊距
    private static final int RANGE_PADDING_TOP = 100;//上邊距範圍值
    private static final int DEFAULT_WIDTH = 800;//默認寬度.圖片的總寬
    private static final int DEFAULT_HEIGHT = 200;//默認高度.圖片的總高
    private static final int DEFAULT_COLOR = 0xDF;//默認背景颜色值

    private String code;

    public static CodeUtils getInstance() {
        if(mCodeUtils == null) {
            mCodeUtils = new CodeUtils();
        }
        return mCodeUtils;
    }

    //生成驗證碼圖片
    public Bitmap createBitmap() {
        mPaddingLeft = 0; //每次生成驗證碼圖片時初始化
        mPaddingTop = 0;

        Bitmap bitmap = Bitmap.createBitmap(DEFAULT_WIDTH, DEFAULT_HEIGHT, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        code = createCode();

        canvas.drawColor(Color.rgb(DEFAULT_COLOR, DEFAULT_COLOR, DEFAULT_COLOR));
        Paint paint = new Paint();
        paint.setTextSize(DEFAULT_FONT_SIZE);

        for (int i = 0; i < code.length(); i++) {
            randomTextStyle(paint);
            randomPadding();
            canvas.drawText(code.charAt(i) + "" , mPaddingLeft, mPaddingTop, paint);
        }

        //干擾線
        for (int i = 0; i < DEFAULT_LINE_NUMBER; i++) {
            drawLine(canvas, paint);
        }

        //canvas.save(Canvas.ALL_SAVE_FLAG);//保存
        canvas.save();//保存
        canvas.restore();
        return bitmap;
    }
    /**
     * 得到圖片中的驗證碼字符串
     * @return
     */
    public String getCode() {
        return code;
    }

    //生成驗證碼
    public String createCode() {
        mBuilder.delete(0, mBuilder.length()); //使用之前首先清空内容

        for (int i = 0; i < DEFAULT_CODE_LENGTH; i++) {
            mBuilder.append(CHARS[mRandom.nextInt(CHARS.length)]);
        }

        return mBuilder.toString();
    }

    //生成干擾線
    private void drawLine(Canvas canvas, Paint paint) {
        int color = randomColor();
        int startX = mRandom.nextInt(DEFAULT_WIDTH);
        int startY = mRandom.nextInt(DEFAULT_HEIGHT);
        int stopX = mRandom.nextInt(DEFAULT_WIDTH);
        int stopY = mRandom.nextInt(DEFAULT_HEIGHT);
        paint.setStrokeWidth(1);
        paint.setColor(color);
        canvas.drawLine(startX, startY, stopX, stopY, paint);
    }

    //隨機颜色
    private int randomColor() {
        mBuilder.delete(0, mBuilder.length()); //使用之前首先清空内容

        String haxString;
        for (int i = 0; i < 3; i++) {
            haxString = Integer.toHexString(mRandom.nextInt(0xFF));
            if (haxString.length() == 1) {
                haxString = "0" + haxString;
            }

            mBuilder.append(haxString);
        }

        return Color.parseColor("#" + mBuilder.toString());
    }

    //隨機文本樣式
    private void randomTextStyle(Paint paint) {
        int color = randomColor();
        paint.setColor(color);
        paint.setFakeBoldText(mRandom.nextBoolean());  //true為粗體，false為非粗體
        float skewX = mRandom.nextInt(11) / 10;
        skewX = mRandom.nextBoolean() ? skewX : -skewX;
        paint.setTextSkewX(skewX); //float類型參數，負數表示右斜，整數左斜
//        paint.setUnderlineText(true); //true為下劃線，false為非下劃線
//        paint.setStrikeThruText(true); //true為删除線，false為非删除線
    }

    //隨機間距
    private void randomPadding() {
        mPaddingLeft += BASE_PADDING_LEFT + mRandom.nextInt(RANGE_PADDING_LEFT);
        mPaddingTop = BASE_PADDING_TOP + mRandom.nextInt(RANGE_PADDING_TOP);
    }
}
