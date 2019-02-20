[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-StickerView-brightgreen.svg?style=flat)]() 
StickerView
=========
在原作StickerView上做了一些修改

A view which can add sticker and zoom,drag,flip,delete it

**I hope you can copy the source code to your project so you can design your own function.**

## Screenshots
![](https://github.com/alvinzhangyuntian/StickerView/screenshots/capture1.png)
![](https://github.com/alvinzhangyuntian/StickerView/screenshots/capture2.png)

## Usage

**Suggestion**

**copy the source code to your project so you can design your own function.**

or

In your **build.gradle**

```gradle
compile 'com.flying.xiaopo:sticker:1.6.0'
```

**Tips**:StickerView extends FrameLayout
#### In layout
```xml
<com.xiaopo.flying.sticker.StickerView
        android:id="@+id/sticker_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_gravity="center">
    <!-- custom, maybe you will like to put an ImageView--> 
    <ImageView
        android:src="@drawable/haizewang_2"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
</com.xiaopo.flying.sticker.StickerView>
```
#### Add sticker
支持图片和文字贴图

```java
stickerView.addSticker(sticker)
stickerView.replace(sticker)
stickerView.remove(sticker)
stickerView.removeCurrentSticker()
stickerView.removeAllStickers()
stcikerView.setLocked(true)
```

自定义贴图按钮和按钮事件

```java
 BitmapStickerIcon heartIcon =
        new BitmapStickerIcon(ContextCompat.getDrawable(this, R.drawable.ic_favorite_white_24dp),
            BitmapStickerIcon.LEFT_BOTTOM);
heartIcon.setIconEvent(new HelloIconEvent());

stickerView.setDrawableStickerIcons(Arrays.asList(deleteIcon, zoomIcon, flipIcon));
stickerView.setTextStickerIcons(Arrays.asList(zoomIcon, deleteIcon, heartIcon));
```

## Update
在原StickerView项目中修改了如下功能：<br>
1.点击外部隐藏边框<br>
2.图片和文字贴图按钮添加分开<br>
3.修改文字贴图逻辑，增加换行和边框贴合（为了多语言和多种字体加大了四周边距）等逻辑<br>
4.增加取消选中贴图的逻辑<br>
5.StickerView改变大小，不重置内部贴图<br>
6.图片sticker支持添加内置图片和手机本地图片<br>
一些其他细微改动<br>



