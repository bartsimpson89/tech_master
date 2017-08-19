package com.example.tech_master;
import java.util.ArrayList;
import java.util.HashMap;

import com.example.dialog.LoginDialog;
import com.example.tech_master.ApplicationVar.LoginState;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity {

	Intent intent = new Intent();
	private boolean netState = false; 
    Button btnLogin;
	Button btnRegiter;
	EditText etName;
	EditText etPwd;
	private ArrayList<HashMap<String,Object>> gUsrInfo   = new  ArrayList<HashMap<String,Object>>();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LoginState ret = ApplicationVar.getInstance().onStart();
        
        ApplicationVar.getInstance().getArrayUsrInfo(gUsrInfo);
	        if(LoginState.LoginOk!=ret)
	        {
	        	if(LoginState.LoginNull==ret||LoginState.LoginErrRouter==ret)
	        	{
	        	    Toast.makeText(Login.this, "请连接路由器TJKJ2016", Toast.LENGTH_SHORT).show();
	        	}
	        	else 
	        	{
	        		Toast.makeText(Login.this, "请绑定该设备静态IP192.168.1.2", Toast.LENGTH_SHORT).show();	
				}
	        	
	        	netState = false;
	        }
	        else 
	        {
	        	Toast.makeText(Login.this, "wifi成功接入TJKJ2016", Toast.LENGTH_SHORT).show();
				netState = true;
			}
	    
	        btnLogin = (Button) findViewById(R.id.login_btn_yes);
	        btnRegiter = (Button) findViewById(R.id.login_btn_register);
	        etName = (EditText)findViewById(R.id.login_et_usrname);
	        etPwd = (EditText)findViewById(R.id.login_et_passwd);
	        
	        
	        
	        btnLogin.setOnClickListener(new MyClickListener());
	        btnRegiter.setOnClickListener(new MyClickListener());
	        ApplicationVar.getInstance().writeLog(new Exception(),"login is success!"); 
	    	
        
    }
    class MyClickListener implements OnClickListener 
    {
    	 
        @Override
        public void onClick(View v) 
        {
          // TODO Auto-generated method stub
        	 // TODO Auto-generated method stub
            switch (v.getId()) 
            {
            case R.id.login_btn_register:
            	 showDialogRegister();
                 break;
            case R.id.login_btn_yes:
            	String name = etName.getText().toString();
            	String pwd = etPwd.getText().toString();
            	if(checkUsrAccount(name,pwd)&&netState)
            	{
            	    intent.setClass(Login.this, MainFuncLogin.class);  
            	    startActivity(intent);
            	}
            	else
            	{
            		if(!checkUsrAccount(name,pwd))
            		{
            		    Toast.makeText(Login.this, "用户输入不正确", Toast.LENGTH_SHORT).show();
            		}
            		else 
            		{
            			Toast.makeText(Login.this, "设备未成功接入网络", Toast.LENGTH_SHORT).show();
					}
            	}
            	 break;
            default:
              break;
            }
            
        }
      }
    public void showDialogRegister()
    {
    	final LoginDialog.Builder builder = new LoginDialog.Builder(this);
		builder.setTitle("注册新用户","账号","请输入账号","密码","请输入密码",0);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				//设置你的操作事项
				String usrName = builder.getEditName();
				String usrPwd = builder.getEditPassWd();
				
				if(registerUsrAccount(usrName,usrPwd))
				{
					Toast.makeText(Login.this, "注册新用户成功", Toast.LENGTH_SHORT).show();
				}
				else
				{
					Toast.makeText(Login.this, "用户已经存在", Toast.LENGTH_SHORT).show();
				}
				
				dialog.dismiss();
				
			}
		});
		builder.setNegativeButton("取消",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.create().show();
    	
    }
    private boolean registerUsrAccount(String name,String pwd)
    {
    	
    	int i = 0;
    	for(i=0;i<gUsrInfo.size();i++)
    	{
    		String strName = (String)gUsrInfo.get(i).get("name");
    		String strPwd = (String)gUsrInfo.get(i).get("pwd");
    		if(name.equals(strName)&&strPwd.equals(strPwd))
    		{
    			break;
    		}
    	}
    	if(i<gUsrInfo.size())
    	{
    		return false;
    	}
    	
    	HashMap<String,Object> map = new HashMap<String,Object>();
		map.put("name", name);
		map.put("pwd", pwd);
		map.put("auth", "1");
		gUsrInfo.add(map);
		ApplicationVar.getInstance().setArrayUsrInfo(gUsrInfo);
    	return true;
    }
    private boolean checkUsrAccount(String name,String pwd)
    {
    	int i = 0;
    	for(i=0;i<gUsrInfo.size();i++)
    	{
    		String strName = (String)gUsrInfo.get(i).get("name");
    		String strPwd = (String)gUsrInfo.get(i).get("pwd");
    		if(name.equals(strName)&&strPwd.equals(pwd))
    		{
    			break;
    		}

    	}
    	if(i<gUsrInfo.size())
    	{
    		return true;
    	}
    	else
    	{
    		return false;
    	}
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) { 
    	 if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) 
    	 { 
    		/* try {
 				ApplicationVar.getInstance().onQuit();
 			} catch (IOException e) {
 				// TODO Auto-generated catch block
 				e.printStackTrace();
 			}*/
         	Toast.makeText(Login.this, "已经退出主机连接状态", Toast.LENGTH_SHORT).show();
    		
    	 } 
    	 return super.onKeyDown(keyCode, event) ;
    	 
    	}
    
    

}
