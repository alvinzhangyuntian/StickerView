package com.phonenix.sticker;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;


/**
 * @author wupanjie
 */
public class ImageSticker extends Sticker {

  private  Activity context;
  private  String stickerPath;
  private Drawable drawable;
  private Rect realBounds;
  private int type;// 是否内置素材 0:内置 1:外置
  private int screenWidth;

  public ImageSticker(Activity context, final int type, final String stickerPath, final int stickerId, int screenWidth){
    this.context = context;
    this.screenWidth = screenWidth;
    this.type = type;
    if(type==0){
      this.drawable = context.getResources().getDrawable(stickerId);
    }else{
      this.stickerPath = stickerPath;
    }
    realBounds = new Rect(0, 0, getWidth(), getHeight());
  }
  @NonNull @Override public Drawable getDrawable() {
    return drawable;
  }

  @Override public ImageSticker setDrawable(@NonNull Drawable drawable) {
    this.drawable = drawable;
    return this;
  }

  @Override public void draw(@NonNull Canvas canvas) {
    canvas.save();
    canvas.concat(getMatrix());
    if(type==0){
      drawable.setBounds(realBounds);
      drawable.draw(canvas);
    }else{
      Bitmap bitmap = BitmapFactory.decodeFile(stickerPath);
      canvas.drawBitmap(bitmap,null,realBounds,new Paint(Paint.ANTI_ALIAS_FLAG));
    }
    canvas.restore();
  }

  @NonNull @Override public ImageSticker setAlpha(@IntRange(from = 0, to = 255) int alpha) {
    drawable.setAlpha(alpha);
    return this;
  }

  @Override public int getWidth() {
      return screenWidth/4;
  }

  @Override public int getHeight() {
      return screenWidth/4;
  }

  @Override
  public int getMinWidth() {
     return screenWidth/10;
  }

  @Override
  public int getMinHeight() {
    return 0;
  }

  @Override public void release() {
    super.release();
    if (drawable != null) {
      drawable = null;
    }
  }
}
