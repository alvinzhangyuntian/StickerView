package com.phonenix.sticker;

import android.view.MotionEvent;

/**
 * @author wupanjie
 */

public class ZoomIconEvent implements StickerIconEvent {
  @Override public void onActionDown(StickerView stickerView, MotionEvent event) {

    if (stickerView.getOnStickerOperationListener() != null) {
      stickerView.getOnStickerOperationListener()
              .onStickerZoomStart(stickerView.getCurrentSticker());
    }
  }

  @Override public void onActionMove(StickerView stickerView, MotionEvent event) {
    stickerView.zoomAndRotateCurrentSticker(event);
  }

  @Override public void onActionUp(StickerView stickerView, MotionEvent event) {
    if (stickerView.getOnStickerOperationListener() != null) {
      stickerView.getOnStickerOperationListener()
          .onStickerZoomFinished(stickerView.getCurrentSticker());
    }
  }
}
