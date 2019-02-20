package com.phonenix.stickerview;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.phonenix.sticker.BitmapStickerIcon;
import com.phonenix.sticker.DeleteIconEvent;
import com.phonenix.sticker.FlipHorizontallyEvent;
import com.phonenix.sticker.ImageSticker;
import com.phonenix.sticker.Sticker;
import com.phonenix.sticker.StickerIconEvent;
import com.phonenix.sticker.StickerView;
import com.phonenix.sticker.TextSticker;
import com.phonenix.sticker.ZoomIconEvent;
import com.phonenix.stickerview.R;
import com.phonenix.stickerview.util.FileUtil;

import java.io.File;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final int PERM_RQST_CODE = 110;
    private StickerView stickerView;
    private Context mContext;
    private int screenWidth;
    private int stickerIconWidth;
    private int screenHeight;
    private Button btn_add_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        stickerView = (StickerView) findViewById(R.id.sticker_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        btn_add_text= (Button) findViewById(R.id.btn_add_text);
        btn_add_text.setOnClickListener(this);
        //currently you can config your own icons and icon event
        //the event you can custom
        BitmapStickerIcon deleteIcon = new BitmapStickerIcon(ContextCompat.getDrawable(this,
                com.phonenix.sticker.R.drawable.sticker_ic_close_white_18dp),
                BitmapStickerIcon.LEFT_TOP);
        deleteIcon.setIconEvent(new DeleteIconEvent());
        stickerIconWidth = deleteIcon.getWidth();
        BitmapStickerIcon zoomIcon = new BitmapStickerIcon(ContextCompat.getDrawable(this,
                com.phonenix.sticker.R.drawable.sticker_ic_scale_white_18dp),
                BitmapStickerIcon.RIGHT_BOTOM);
        zoomIcon.setIconEvent(new ZoomIconEvent());

        BitmapStickerIcon flipIcon = new BitmapStickerIcon(ContextCompat.getDrawable(this,
                com.phonenix.sticker.R.drawable.sticker_ic_flip_white_18dp),
                BitmapStickerIcon.RIGHT_TOP);
        flipIcon.setIconEvent(new FlipHorizontallyEvent());

        BitmapStickerIcon heartIcon =
                new BitmapStickerIcon(ContextCompat.getDrawable(this, R.drawable.ic_favorite_white_24dp),
                        BitmapStickerIcon.LEFT_BOTTOM);
//    heartIcon.setIconEvent(new HelloIconEvent());
        heartIcon.setIconEvent(new StickerIconEvent() {
            @Override
            public void onActionDown(StickerView stickerView, MotionEvent event) {

            }

            @Override
            public void onActionMove(StickerView stickerView, MotionEvent event) {

            }

            @Override
            public void onActionUp(StickerView stickerView, MotionEvent event) {
                showTextInputDialog(false);
            }
        });
        stickerView.setScreenWidthAndHeight(screenWidth, screenHeight);
        stickerView.setDrawableStickerIcons(Arrays.asList(deleteIcon, zoomIcon, flipIcon));
        stickerView.setTextStickerIcons(Arrays.asList(zoomIcon, deleteIcon, heartIcon));


//        stickerView.setBackgroundColor(Color.YELLOW);
        stickerView.setLocked(false);
        stickerView.setConstrained(true);


        stickerView.setOnStickerOperationListener(new StickerView.OnStickerOperationListener() {
            @Override
            public void onStickerAdded(@NonNull Sticker sticker) {
                Log.d(TAG, "onStickerAdded");
            }

            @Override
            public void onStickerClicked(@NonNull Sticker sticker) {
                //stickerView.removeAllSticker();
                if (sticker instanceof TextSticker) {
                    ((TextSticker) sticker).setTextColor(Color.RED);
                    stickerView.replace(sticker);
                    stickerView.invalidate();
                }
                Log.d(TAG, "onStickerClicked");
            }

            @Override
            public void onStickerDeleted(@NonNull Sticker sticker) {
                Log.d(TAG, "onStickerDeleted");
            }

            @Override
            public void onStickerDragFinished(@NonNull Sticker sticker) {
                Log.d(TAG, "onStickerDragFinished");
            }

            @Override
            public void onStickerTouchedDown(@NonNull Sticker sticker) {
                Log.d(TAG, "onStickerTouchedDown");
            }

            @Override
            public void onStickerZoomStart(@NonNull Sticker sticker) {

            }

            @Override
            public void onStickerZoomFinished(@NonNull Sticker sticker) {
                Log.d(TAG, "onStickerZoomFinished");
            }

            @Override
            public void onStickerFlipped(@NonNull Sticker sticker) {
                Log.d(TAG, "onStickerFlipped");
            }

            @Override
            public void onStickerDoubleTapped(@NonNull Sticker sticker) {
                Log.d(TAG, "onDoubleTapped: double tap will be with two click");
                if (sticker instanceof TextSticker) {
                    showTextInputDialog(false);
                }
            }

            @Override
            public void onStickerNotClicked() {
                stickerView.invalidate();
            }
        });

        if (toolbar != null) {
            toolbar.setTitle(R.string.app_name);
            toolbar.inflateMenu(R.menu.menu_save);
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.item_save) {
                        File file = FileUtil.getNewFile(MainActivity.this, "Sticker");
                        if (file != null) {
                            stickerView.save(file);
                            Toast.makeText(MainActivity.this, "saved in " + file.getAbsolutePath(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "the file is null", Toast.LENGTH_SHORT).show();
                        }
                    }
                    //                    stickerView.replace(new DrawableSticker(
                    //                            ContextCompat.getDrawable(MainActivity.this, R.drawable.haizewang_90)
                    //                    ));
                    return false;
                }
            });
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERM_RQST_CODE);
        } else {
            loadSticker();
        }
    }
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            return false;
        }
    });
    private void loadSticker() {
        stickerView.addSticker(new ImageSticker(MainActivity.this,0,"",R.drawable.haizewang_215,screenWidth));
        stickerView.addSticker(new ImageSticker(MainActivity.this,0,"",R.drawable.haizewang_23,screenWidth), Sticker.Position.BOTTOM | Sticker.Position.RIGHT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERM_RQST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadSticker();
        }
    }

//    public void testReplace(View view) {
//        if (stickerView.replace(sticker)) {
//            Toast.makeText(MainActivity.this, "Replace Sticker successfully!", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(MainActivity.this, "Replace Sticker failed!", Toast.LENGTH_SHORT).show();
//        }
//    }

    public void testAddImage(View view) {
        stickerView.addSticker(new ImageSticker(MainActivity.this,0,"",R.drawable.haizewang_90,screenWidth));
    }
    public void testLock(View view) {
        stickerView.setLocked(!stickerView.isLocked());
    }

    public void testRemove(View view) {
        if (stickerView.removeCurrentSticker()) {
            Toast.makeText(MainActivity.this, "Remove current Sticker successfully!", Toast.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(MainActivity.this, "Remove current Sticker failed!", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public void testRemoveAll(View view) {
        stickerView.removeAllStickers();
    }

    public void reset(View view) {
        stickerView.removeAllStickers();
        loadSticker();
    }


    private void showTextInputDialog(final Boolean isNew) {
        //实例化布局
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_custom_content, null);
        //找到并对自定义布局中的控件进行操作的示例
        final EditText et = view.findViewById(R.id.et_input);
        String hint = "请输入";
        et.setHint(hint);
        if (!isNew) {
            if (stickerView.getCurrentSticker() instanceof TextSticker) {
                TextSticker textSticker = (TextSticker) stickerView.getCurrentSticker();
                String text = textSticker.getText();
                et.setText(text);
                et.setSelection(text.length());
            }
        }
        //创建对话框
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setIcon(R.mipmap.ic_launcher);//设置图标
        dialog.setView(view);//添加布局
        //设置按键
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (et.getText() != null && !TextUtils.isEmpty(et.getText().toString())) {
                    dialog.dismiss();
                    if (isNew) {
                        final TextSticker sticker = new TextSticker(mContext, et.getText().toString(), (int) (screenWidth - stickerIconWidth));
                        sticker.setTextColor(Color.BLUE);
                        sticker.setTextAlign(Layout.Alignment.ALIGN_NORMAL);
                        sticker.setDrawableSticker(false);
                        sticker.resizeText();
                        stickerView.addSticker(sticker);
                    } else {
                        if (stickerView.getCurrentSticker() instanceof TextSticker) {
                            TextSticker textSticker = (TextSticker) stickerView.getCurrentSticker();

                            final TextSticker sticker = new TextSticker(mContext, et.getText().toString(), (int) (screenWidth - stickerIconWidth));
                            sticker.setTextColor(Color.BLUE);
                            sticker.setTextAlign(Layout.Alignment.ALIGN_NORMAL);
                            sticker.setDrawableSticker(false);
                            sticker.resizeText();
                            stickerView.remove(textSticker);
                            stickerView.addSticker(sticker);
                            sticker.setMatrix(textSticker.getMatrix());
                            stickerView.invalidate();
                        }
                    }
                } else {
                    Toast.makeText(mContext, "请输入文字", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                hideKeyboard(MainActivity.this);
            }
        });
        dialog.show();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                et.setFocusable(true);
                et.setFocusableInTouchMode(true);
                et.requestFocus();
                InputMethodManager inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(et, 0);
            }
        }, 200);
    }
    public static void hideKeyboard(Activity activity) {
        try {
            InputMethodManager inputManager = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);

        } catch (Exception e) {
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_add_text:
                showTextInputDialog(true);
                break;
        }
    }
}
