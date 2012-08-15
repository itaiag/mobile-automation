package org.topq.mobile.frank_client.imp;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.topq.mobile.frank_client.interfaces.FrankClient;
import org.topq.mobile.frank_ssh.SshIosDevice;


import com.dhemery.configuration.Configuration;
import com.dhemery.victor.By;
import com.dhemery.victor.IosApplication;
import com.dhemery.victor.IosDevice;
import com.dhemery.victor.configuration.CreateIosApplication;
import com.dhemery.victor.configuration.CreateIosDevice;

/**
 * 
 * @author Bortman Limor
 *
 */
public class FrankClientImpl implements FrankClient{
	
	private final static IosApplication application;
	private static IosDevice device = null;
	private final static Configuration configuration;
	 private  By query= null;
	 private static Logger logger= null;
	 
	static{
		
		configuration = new Configuration("C:\\Users\\Vadim\\Limor\\workspace\\frankd-client\\src\\resources\\test.properties");

		application = CreateIosApplication.withConfiguration(configuration);
		logger = Logger.getLogger(FrankClientImpl.class);
	}
	

	public String launch() throws Exception {
			device = new  SshIosDevice(configuration);
			device.start();
			return (String) ((SshIosDevice) device).getLestResult();
	}
	
//	public CharSequence  nevigte(String UITabBarButtonMarkeder){
//		return sendMassageWithMarkerAsString("UITabBarButton",UITabBarButtonMarkeder,"tap");
//	}

	public String getTextView(int index) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTextViewIndex(String text) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public String getCurrentTextViews() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public String getText(int index) throws Exception {
		return (String) sendMessageWithRespons("UITextField", Integer.toString(index), "text");
	}

	public String clickOnMenuItem(String item) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public CharSequence clickOnView(String view,String marker) throws Exception {
		return sendMassageWithMarkerAsString(view, marker, "tap");
	}

	public String enterText(int index, String text) throws Exception {
		return sendMassageWithMarkerAsInt("UITextField", index, "setText:", text);
		
	}

	public String clickInList(int index) throws Exception {
		return sendMassageWithMarkerAsInt("UITableViewCell", index, "tap");
	}

	public String clearEditText(int index) throws Exception {
		return enterText(index,"");
	}

	public String clickOnButtonWithText(String text) throws Exception {
		return sendMassageWithMarkerAsString("UIButton",text,"tap");
	}

	public String clickOnText(String text) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public String sendKey(int key) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public void closeConnection() throws Exception {
		device.stop();
		
	}
	private CharSequence sendMessageWithRespons(String view,String marker,String command, Object... arguments){
		 query= By.uiQuery("view:'"+view+"' marked:"+marker);
		 List<String> result = application.view(query).sendMessage(command,arguments);
		 if(result.size() < 0 ){
			 return ERROR_STRING;
		 }else{
			 return result.get(0);
		 }
		 
	}
	
	 private String  sendMessage(String view,String marker,String command, Object... arguments) {
		 CharSequence result =  sendMessageWithRespons(view, marker, command, arguments);
		 if(!result.equals(ERROR_STRING) ){
			 return (String) SUCCESS_STRING;
		 }
		 	return (String) result;
	    }
	 private  String sendMassageWithMarkerAsString(String view,String marker,String command, Object... arguments){
		 return sendMessage(view,"'"+marker+"'",command,arguments);
		}
	 private  String sendMassageWithMarkerAsInt(String view,int marker,String command, Object... arguments){
		 return sendMessage(view,Integer.toString(marker),command,arguments);
		}

	public String clickOnView(int index) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public String clickOnButton(int index) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public String goBack() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
}
