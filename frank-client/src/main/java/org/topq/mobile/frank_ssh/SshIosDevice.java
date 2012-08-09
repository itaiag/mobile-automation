package org.topq.mobile.frank_ssh;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.junit.internal.ExactComparisonCriteria;
import org.topq.mobile.frank_client.imp.FrankClientImpl;
import org.topq.mobile.frank_client.interfaces.FrankClient;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import net.schmizz.sshj.xfer.FileSystemFile;
import net.schmizz.sshj.xfer.scp.SCPFileTransfer;

import com.dhemery.configuration.Configuration;
import com.dhemery.victor.IosDevice;


/**
 * 
 * @author Bortman Limor
 *
 */
public class SshIosDevice implements IosDevice{
	SSHClient ssh;
	private String detDiractor;
	private String compailDir ;
	private CharSequence lestResult;


	private static Logger logger= null;

	public SshIosDevice(Configuration conf) throws Exception {
		logger = Logger.getLogger(FrankClientImpl.class);
		ssh = new SSHClient(); 
		ssh.addHostKeyVerifier("5e:bd:2c:17:25:6f:7f:e5:d3:63:59:a5:f9:d3:eb:2c");
		ssh.connect(conf.option("victor.frank.host"));
		String sorcDir = conf.option("victor.application.bundle.path");
		String[] sorcDirASArray = sorcDir.split("\\\\");
		detDiractor = conf.option("victor.application.targer.path") +"/"+ sorcDirASArray[sorcDirASArray.length-1];
		compailDir = detDiractor+"/"+conf.option("victor.application.compaile.folder");
		ssh.authPassword(conf.option("victor.frank.host.userName"), conf.option("victor.frank.host.password"));
		new SCPFileTransfer(ssh).newSCPUploadClient().copy(new FileSystemFile(sorcDir), detDiractor); 
		//runCommand("gem install frank-cucumber");
		runCommand("frank setup");
		runCommand("frank build");
	}

	public void rotateLeft() {
		// TODO Auto-generated method stub

	}

	public void rotateRight() {
		// TODO Auto-generated method stub

	}

	public void saveScreenShot() {
		// TODO Auto-generated method stub

	}

	public void start() {
		try {
			runCommand("frank launch");
		} catch (Exception e) {
			logger.error("Faile lanuch",e);
		}	
	}

	public void stop() {
		try {
			runCommand("killall \"iPhone Simulator\"");
			ssh.close();
		} catch (Exception e) {
			logger.error("Faile lanuch",e);
		}	
	}
	//#######################################################Getters and Setters######################################################################
	public CharSequence getLestResult() {
		return lestResult;
	}

	private void runCommand(String command) throws Exception{
		String totalCommand  = "cd "+compailDir+"; "+command;
		Session session = ssh.startSession();
		Command cmd =session.exec(totalCommand);
		cmd.join(30, TimeUnit.SECONDS);
		if(IOUtils.readFully(cmd.getInputStream()).toString().contains("Error") || IOUtils.readFully(cmd.getInputStream()).toString().contains("FAIELD")||cmd.getExitStatus()!= 0){
			lestResult = FrankClient.ERROR_STRING;
			throw new Exception("The command "+totalCommand+"FAIELD!\nOutput:"+IOUtils.readFully(cmd.getInputStream()).toString()+"\nExit Status:"+cmd.getExitStatus());
		}
		lestResult = FrankClient.SUCCESS_STRING;

	}

}
