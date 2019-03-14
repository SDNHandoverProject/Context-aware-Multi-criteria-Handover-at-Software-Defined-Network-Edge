package com.javamedia;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;


public class ProgersssDialog extends Dialog {
	private Context context;
	private ImageView progress_img;
	TextView progress_txt;

	public ProgersssDialog(Context context) {
		
		super(context, R.style.progress_dialog);
		this.context = context;
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.progress_dialog, null);
		progress_img = (ImageView) view.findViewById(R.id.progress_img);
		progress_txt = (TextView) view.findViewById(R.id.progress_txt);

		Animation anim = AnimationUtils.loadAnimation(context, R.anim.loading_dialog_progressbar);
		progress_img.setAnimation(anim);
		progress_txt.setText(R.string.progressbar_dialog_txt);

		setContentView(view);
		show();
		
	}

	public void setMsg(String msg) {
		progress_txt.setText(msg);
	}

	public void setMsg(int msgId) {
		progress_txt.setText(msgId);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

}
