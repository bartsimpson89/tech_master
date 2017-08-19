package com.example.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tech_master.ApplicationVar;
import com.example.tech_master.R;

public class LoginDialog extends Dialog {

	public LoginDialog(Context context) {
		super(context);
	}

	public LoginDialog(Context context, int theme) {
		super(context, theme);
	}

	public static class Builder {
		private Context context;
		private String title;
		private String line1_name;
		private String line1_hint;
		private String line2_name;
		private String line2_hint;
		private int def;
		private String positiveButtonText;
		private String negativeButtonText;
		private DialogInterface.OnClickListener positiveButtonClickListener;
		private DialogInterface.OnClickListener negativeButtonClickListener;
		
		private String usrName;
		private String usrPasswd;
		

		public Builder(Context context) {
			this.context = context;
		}

		public Builder setMessage(String message) {
			return this;
		}

		 public void dismiss() {
		    this.dismiss();
		 }

		/**
		 * Set the Dialog title from resource
		 * 
		 * @param title
		 * @return
		 */
		public Builder setTitle(int title) {
			this.title = (String) context.getText(title);
			return this;
		}

		/**
		 * Set the Dialog title from String
		 * 
		 * @param title
		 * @return
		 */

		public Builder setTitle(String title,String line1_name,String line1_hint,String line2_name,String line2_hint,int def) {
			this.title = title;
			this.line1_name = line1_name;
			this.line1_hint = line1_hint;
			this.line2_name = line2_name;
			this.line2_hint= line2_hint;
			this.def =def;
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
			this.positiveButtonText = (String) context
					.getText(positiveButtonText);
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setPositiveButton(String positiveButtonText,
				DialogInterface.OnClickListener listener) {
			this.positiveButtonText = positiveButtonText;
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(int negativeButtonText,
				DialogInterface.OnClickListener listener) {
			this.negativeButtonText = (String) context
					.getText(negativeButtonText);
			this.negativeButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(String negativeButtonText,
				DialogInterface.OnClickListener listener) {
			this.negativeButtonText = negativeButtonText;
			this.negativeButtonClickListener = listener;
			return this;
		}

		public LoginDialog create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the custom Theme
			final LoginDialog dialog = new LoginDialog(context,R.style.Dialog);
			View layout = inflater.inflate(R.layout.msg_register, null);
			final EditText et_usrName = (EditText) layout.findViewById(R.id.login_et_usrname);
			final EditText et_psWd = (EditText) layout.findViewById(R.id.login_et_passwd);
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			// set the dialog title
			((TextView) layout.findViewById(R.id.reg_title)).setText(title);
			((TextView) layout.findViewById(R.id.login_tv_usrname)).setText(line1_name);
			((EditText) layout.findViewById(R.id.login_et_usrname)).setHint(line1_hint);
			((TextView) layout.findViewById(R.id.login_tv_passwd)).setText(line2_name);
			((EditText) layout.findViewById(R.id.login_et_passwd)).setHint(line2_hint);
			
			if(def == 1)
			{
				((TextView) layout.findViewById(R.id.login_tv_passwd)).setVisibility(View.INVISIBLE);
				((EditText) layout.findViewById(R.id.login_et_passwd)).setVisibility(View.INVISIBLE);
				
			}
			else if(def == 2)
			{
				((TextView) layout.findViewById(R.id.login_tv_usrname)).setVisibility(View.INVISIBLE);
				((EditText) layout.findViewById(R.id.login_et_usrname)).setVisibility(View.INVISIBLE);
				
			}
			else if(def == 3)
			{
				((TextView) layout.findViewById(R.id.login_tv_passwd)).setVisibility(View.INVISIBLE);
				((EditText) layout.findViewById(R.id.login_et_passwd)).setVisibility(View.INVISIBLE);
				((TextView) layout.findViewById(R.id.login_tv_usrname)).setVisibility(View.INVISIBLE);
				((EditText) layout.findViewById(R.id.login_et_usrname)).setVisibility(View.INVISIBLE);
				
			}
			else if(def == 4)
			{
				((TextView) layout.findViewById(R.id.login_tv_passwd)).setVisibility(View.INVISIBLE);
				((EditText) layout.findViewById(R.id.login_et_passwd)).setVisibility(View.INVISIBLE);
				((TextView) layout.findViewById(R.id.login_tv_usrname)).setVisibility(View.INVISIBLE);
				((EditText) layout.findViewById(R.id.login_et_usrname)).setVisibility(View.INVISIBLE);
				((Button) layout.findViewById(R.id.login_negativeButton)).setVisibility(View.INVISIBLE);
				((Button) layout.findViewById(R.id.login_positiveButton)).setVisibility(View.INVISIBLE);
				
			}
			// set the confirm button
			if (positiveButtonText != null) {
				((Button) layout.findViewById(R.id.login_positiveButton))
						.setText(positiveButtonText);
				if (positiveButtonClickListener != null) {
					((Button) layout.findViewById(R.id.login_positiveButton))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									if(et_usrName.getText()!=null)
									{
									    usrName = et_usrName.getText().toString();
									    
									}
									if (et_psWd.getText()!=null)
									{
										usrPasswd = et_psWd.getText().toString();
									}
									positiveButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_POSITIVE);
									
									
									
									
									
								}
							});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.login_positiveButton).setVisibility(
						View.GONE);
			}
			// set the cancel button
			if (negativeButtonText != null) {
				((Button) layout.findViewById(R.id.login_negativeButton))
						.setText(negativeButtonText);
				if (negativeButtonClickListener != null) {
					((Button) layout.findViewById(R.id.login_negativeButton))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									negativeButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_NEGATIVE);
									
									
									
								}
							});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.login_negativeButton).setVisibility(
						View.GONE);
			}
			dialog.setContentView(layout);
			return dialog;
		}
		public String getEditName()
		{
			return usrName;
		}
		public String getEditPassWd()
		{
			return usrPasswd;
		}
     
		
		
	}
}
