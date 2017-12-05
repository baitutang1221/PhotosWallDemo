package com.example.photoswalldemo;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;


/**
 * 照片墙主活动，使用GridView展示照片墙。
 * 
 * @author guolin
 */
public class MainActivity extends Activity {

	/**
	 * 用于展示照片墙的GridView
	 */
	private GridView mPhotoWall;

	/**
	 * GridView的适配器
	 */
	private PhotoWallAdapter mAdapter;
	private Button refresh_btn;

	private int mImageThumbSize;
	private int mImageThumbSpacing;

	private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSIONS = 1;
	private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSIONS = 2;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/*动态请求权限*/
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
					|| checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				requestPermissions(
						new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
						REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSIONS);
				requestPermissions(
						new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
						REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSIONS);
			}
		}


		mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
		mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);
		mPhotoWall = (GridView) findViewById(R.id.photo_wall);
		refresh_btn = (Button) findViewById(R.id.refresh_btn);
		mAdapter = new PhotoWallAdapter(this, 0, Images.imageThumbUrls,	mPhotoWall);
		mPhotoWall.setAdapter(mAdapter);
		mPhotoWall.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						final int numColumns = (int) Math.floor(mPhotoWall.getWidth() / (mImageThumbSize + mImageThumbSpacing));
						if (numColumns > 0) {
							int columnWidth = (mPhotoWall.getWidth() / numColumns) - mImageThumbSpacing;
							mAdapter.setItemHeight(columnWidth);
							mPhotoWall.getViewTreeObserver().removeGlobalOnLayoutListener(this);
						}
					}
				});


		//安卓7.0不显示，手动刷新显示图片
		refresh_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mPhotoWall.setAdapter(mAdapter);
				mAdapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		mAdapter.fluchCache();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 退出程序时结束所有的下载任务
		mAdapter.cancelAllTasks();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch (requestCode) {
			case REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSIONS: {
				for (int i = 0; i < permissions.length; i++) {
					if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
						Toast.makeText(this, "允许读写存储！", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(this, "未允许读写存储！", Toast.LENGTH_SHORT).show();
					}
				}
			}
			case REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSIONS: {
				for (int i = 0; i < permissions.length; i++) {
					if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
						Toast.makeText(this, "允许读写存储！", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(this, "未允许读写存储！", Toast.LENGTH_SHORT).show();
					}
				}
			}

			break;
			default: {
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
			}
		}
	}
}