package com.example.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.example.tech_master.R;

public class HelpDialog extends Dialog {

	public HelpDialog(Context context) {
		super(context);
	}

	public HelpDialog(Context context, int theme) {
		super(context, theme);
	}

	public static class Builder {
		private Context context;
		private String title;
		private String HelpButtonText;
		private DialogInterface.OnClickListener HelpButtonClickListener;
        private boolean isShow =false;
		

		public Builder(Context context) {
			this.context = context;
		}

		public Builder setMessage(String message) {
			return this;
		}

		 public void dismiss()
		 {
		    this.dismiss();
		    isShow = false;
		 }
         public void show()
         {
        	 this.show();
        	 isShow = true;
         }
         public boolean isShow()
         {
        	 return isShow;
         }
		/**
		 * Set the Dialog title from resource
		 * 
		 * @param title
		 * @return
		 */
		public Builder setTitle(String title) {
			this.title =  title;
			return this;
		}

		public Builder setContentView(View v) {
			return this;
		}

		/**
		 * Set the positive button resource and it's listener
		 * 
		 * @param positiveButtonText
		 * @return
		 */
		public Builder setPositiveButton(int positiveButtonText,
				DialogInterface.OnClickListener listener) {
			this.HelpButtonText = (String) context
					.getText(positiveButtonText);
			this.HelpButtonClickListener = listener;
			return this;
		}

		public Builder setPositiveButton(String positiveButtonText,
				DialogInterface.OnClickListener listener) {
			this.HelpButtonText = positiveButtonText;
			this.HelpButtonClickListener = listener;
			return this;
		}
         


		public HelpDialog create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the custom Theme
			final HelpDialog dialog = new HelpDialog(context,R.style.Dialog);
			View layout = inflater.inflate(R.layout.view_device_help, null);
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			// set the dialog title
			((TextView) layout.findViewById(R.id.view_tv_device_help_info)).setText(title);
	
			
			// set the confirm button
			if (HelpButtonText != null) 
			{
				((Button) layout.findViewById(R.id.btn_receive_help))
						.setText(HelpButtonText);
				if (HelpButtonClickListener != null) 
				{
					((Button) layout.findViewById(R.id.btn_receive_help))
							.setOnClickListener(new View.OnClickListener() 
							{
								public void onClick(View v) 
								{
									
									HelpButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_POSITIVE);
								}
							});
				}
			} 
			else 
			{
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.login_positiveButton).setVisibility(
						View.GONE);
			}
			
            isShow = true;
			dialog.setContentView(layout);
			return dialog;
		}
	}
}

