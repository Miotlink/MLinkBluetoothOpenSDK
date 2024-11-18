package com.miotlink.smart.bluetooth.view;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.PopupWindow;

import androidx.annotation.Nullable;

import com.miotlink.smart.bluetooth.R;


/**
 * Created by Administrator on 2016/7/12 0012.
 */
public class LoadingViewDialog {

	private Context mContext;


	public LoadingViewDialog(Context context) {
		this.mContext=context;
	}
	@Nullable
	private PopupWindow popupWindow;
	/**
	 * 网络请求加载对话框
	 */
	@Nullable
    View view=null;



	private ImageView imageView=null;

	public boolean isShow(){
		if (popupWindow!=null&&popupWindow.isShowing()){
			return true;
		}
		return false;
	}

	public void show() {
		Animation operatingAnim = AnimationUtils.loadAnimation(mContext, R.anim.rotate_anim);
		LinearInterpolator lin = new LinearInterpolator();
		operatingAnim.setInterpolator(lin);

		if (popupWindow == null) {
			view = LayoutInflater.from(mContext).inflate(R.layout.dialog_loading_view, null);
			popupWindow = new PopupWindow(view, WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.WRAP_CONTENT);

			popupWindow.setFocusable(true);
			popupWindow.setOutsideTouchable(true);
			popupWindow.setTouchable(true);

//			view.setFocusable(true);
//			view.setFocusableInTouchMode(true);
			view.setOnKeyListener(new View.OnKeyListener() {
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK && popupWindow.isShowing()) {
						popupWindow.dismiss();
					}
					return false;
				}
			});
			imageView=view.findViewById(R.id.loadview);

		}
		if (!popupWindow.isShowing()&&!((Activity)mContext).isFinishing()) {
			popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
			imageView.startAnimation(operatingAnim);
		}
	}


	/**
	 * 取消加载对话框
	 */
	public void cancel() {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
			imageView.clearAnimation();
		}
	}
}
