package org.jsystemtest.mobile.frank_client.imp;

import java.util.List;

import org.apache.log4j.Logger;
import org.jsystemtest.mobile.common_mobile.client.enums.HardwareButtons;
import org.jsystemtest.mobile.common_mobile.client.interfaces.MobileClintInterface;
import org.jsystemtest.mobile.frank_ssh.SshIosDevice;

import com.dhemery.configuring.Configuration;
import com.dhemery.configuring.LoadProperties;
import com.dhemery.victor.IosApplication;
import com.dhemery.victor.IosDevice;
import com.dhemery.victor.UIQuery;
import com.dhemery.victor.Victor;
/**
 * 
 * @author Bortman Limor
 * 
 */
public class FrankClientImpl implements MobileClintInterface {

	private static IosApplication application;
	private static IosDevice device = null;
	private static Configuration configuration;
	private static Logger logger = null;

	public FrankClientImpl(String fileName){
		configuration = new Configuration();
		LoadProperties.fromFiles(fileName).into(configuration);

		Victor victor = new Victor(configuration);
		application = victor.application();
		logger = Logger.getLogger(FrankClientImpl.class);
	}

	public String launch() throws Exception {
		device = new SshIosDevice(configuration);
		device.start();
		return (String) ((SshIosDevice) device).getLestResult();
	}



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
		return (String) sendMessageWithRespons("textField",Integer.toString(index), "text");
	}

	public String clickOnMenuItem(String item) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public CharSequence clickOnView(String view, String marker)throws Exception {
		return sendMassageWithMarkerAsString(view, marker, "tap");
	}

	public String enterText(int index, String text) throws Exception {
		return sendMassageWithMarkerAsInt("textField", index, "setText:",text);

	}

	public String clickInList(int index) throws Exception {
		return sendMassageWithMarkerAsInt("tableViewCell", index, "tap");
	}

	public String clearEditText(int index) throws Exception {
		return enterText(index, "");
	}

	public String clickOnButtonWithText(String text) throws Exception {
		return sendMassageWithMarkerAsString("button", text, "tap");
	}
	
	public String clickOnButton(int index) throws Exception {
		return sendMassageWithMarkerAsInt("button", index, "tap");
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
	
	public String clickOnView(int index) throws Exception {
		return sendMassageWithMarkerAsInt("view", index, "tap");
	}


	public String goBack() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String clickOnHardwereButton(HardwareButtons button)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public byte[] pull(String fileName) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	

	private String sendMessage(String uiQueryPatern, String command,Object... arguments) {
		CharSequence result = sendMessageWithRespons(uiQueryPatern, command,arguments);
		if (!result.equals(ERROR_STRING)) {
			return (String) SUCCESS_STRING;
		}
		return (String) result;
	}
	
	private CharSequence sendMessageWithRespons(String uiQueryPatern,String command, Object... arguments) {
		List<String> result = application.view(UIQuery.uiquery(uiQueryPatern)).sendMessage(command,arguments);
		if (result.size() <= 0) {
			return ERROR_STRING;
		} else {
			if(result.get(0) != null){
				return result.get(0);
			}else{
				return SUCCESS_STRING;
			}
		}

	}

	private String sendMassageWithMarkerAsString(String view, String marker,String command, Object... arguments) {
		return sendMessage(view +" marked:"+marker, command, arguments);
	}

	private String sendMassageWithMarkerAsInt(String view, int marker,String command, Object... arguments) {
		return sendMessage(view +" tag:"+Integer.toString(marker), command, arguments);
	}

	@Override
	public String push(byte[] data, String newlocalFileName) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public String push(String fileName, String newlocalFileName)
//			throws Exception {
//		// TODO Auto-generated method stub
//		return null;
//	}


}
