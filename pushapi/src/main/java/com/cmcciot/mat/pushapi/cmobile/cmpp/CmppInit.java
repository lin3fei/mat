package com.cmcciot.mat.pushapi.cmobile.cmpp;

import java.net.InetAddress;
import java.net.Socket;

import com.cmcciot.mat.pushapi.cmobile.object.ServerObject;
import com.cmcciot.mat.pushapi.cmobile.object.SingleCmppObject;
import com.cmcciot.mat.pushapi.cmobile.server.ParseConfig;
import com.cmcciot.mat.pushapi.sms.util.Util;
import com.simpleteam.IntegerTimestamp;
import com.simpleteam.SimpleSequence;
import com.simpleteam.TimeConstant;
import com.simpleteam.adapter.stream.SocketAdapter;
import com.simpleteam.connection.protocol.cmpp.CMPP3Connection;
import com.simpleteam.packet.cmpp.CMPP3ConnectResponse;
import com.simpleteam.packet.cmpp.CMPPCommandID;
import com.simpleteam.packet.cmpp.CMPPConnect;
import com.simpleteam.packet.cmpp.CMPPPacket;
import com.simpleteam.tag.TagNode;
import com.simpleteam.transactor.authenticate.CMPPAuthenticate;

/**
 * CMPP初始化线程
 * 
 * @author PTERO
 * 
 */
public class CmppInit {


	/**
	 * Simple sequence.
	 */
	private SimpleSequence sequence;

	/**
	 * SMGP配置
	 */
	private SingleCmppObject singleCmppObject;

	/**
	 * @return the singleSmgpObject
	 */
	public SingleCmppObject getSingleCmppObject() {
		return singleCmppObject;
	}

	/**
	 * Constructor
	 * 
	 * @param serverObject
	 *            配置信息
	 */
	public CmppInit(ServerObject serverObject) {
		singleCmppObject = new SingleCmppObject();
		// Create sequence.
		singleCmppObject.setSequence(new SimpleSequence());
		// Create connection.
		singleCmppObject.setConnection(new CMPP3Connection());
		// Create node.
		TagNode node = new TagNode("authenticate");
		// Add node.
		node.addNode("account", serverObject.getAccount());
		// Add node.
		node.addNode("password", serverObject.getPassword());
		// Add node.
		node.addNode("service_code", serverObject.getAccessNo());
		// Add node.
		node.addNode("enterprise_code", serverObject.getEnterPriseNo());
		// Create authenticate.
		singleCmppObject.setAuthenticate(new CMPPAuthenticate(node));
	}

	/**
	 * Connect.
	 * 
	 * @param remoteHost
	 *            Address of remote host.
	 * @param remotePort
	 *            Port of remote host.
	 * @return <p>
	 *         Return true, if successfully done.
	 *         </p>
	 */
	public boolean connect(String remoteHost, int remotePort) {
		try {
			Util.log.info("CmppInit connect start");
			// Socket.
			Socket socket = new Socket(InetAddress.getByName(remoteHost),
					remotePort);
			// Create socket adapter.
			SocketAdapter adapter = new SocketAdapter(socket);
			// Set adapter timeout.
			adapter.setTimeout(5 * TimeConstant.SECOND);
			// Open.
			singleCmppObject.getConnection().open(adapter);
			
			Util.log.info("CmppInit connect end");
			// Return login.
			return login();
		} catch (Exception e) {
			// Log event.
			Util.log.error("CmppInit connect error:"+e);
		}
		// Return false.
		return false;
	}

	/**
	 * Login session.
	 * 
	 * <p>
	 * It depends on session type and gateway type. It must be overrided.
	 * </p>
	 * 
	 * @param None
	 *            No parameters needed.
	 * @return <p>
	 *         Return true, if logined successfully.
	 *         </p>
	 */
	private boolean login() {
		Util.log.info("CmppInit login start");
		
		// Create timestamp.
		IntegerTimestamp timestamp = new IntegerTimestamp();
		// Create connect.
		CMPPConnect connect = new CMPPConnect();
		// Set sequence.
		connect.sequence = nextSequence();
		// Set version.
		connect.version = 0x30;
		// Set timestamp.
		connect.timestamp = timestamp.getTimestamp();
		// Set source address.
		connect.source_addr = singleCmppObject.getAuthenticate().getAccount();
		// Get authenticate.
		connect.authenticator_sp = singleCmppObject.getAuthenticate()
				.getAuthenticate(timestamp);
		// Check validation.
		if (!connect.isValid()) {
			Util.log.info("invalid CMPP3 login !");
			return false;
		}
		
		try {
			// Write packet.
			singleCmppObject.getConnection().writePacket(connect);
			// Read packet.
			CMPPPacket cmpp = (CMPPPacket) singleCmppObject.getConnection()
					.readPacket();
			// Check result.
			if (cmpp == null) {
				// Log event.
				Util.log.info("fail to read packet !");
				return false;
			}
			// Check command
			if (cmpp.command != CMPPCommandID.CONNECT_RESPONSE) {
				// Log event.
				Util.log.info("invalid login response packet !");
				return false;
			}
			// Check sequence.
			if (cmpp.sequence != connect.sequence) {
				// Log event.
				Util.log.info("sequence is not according to previous !");
			}
			// Get login response.
			CMPP3ConnectResponse response = (CMPP3ConnectResponse) cmpp;
			// Check result.
			if (response.status != 0) {
				// Log event.
				Util.log.info("response.status is "+response.status);
				return false;
			}
			// Set authenticated.
			singleCmppObject.setAuthenticated(true);
			
			Util.log.info("CmppInit login end");
			// Return true.
			return true;
		} catch (Exception e) {
			// Log event.
			e.printStackTrace();
			Util.log.error("CmppInit login error:"+e);
		}
		// Return false.
		return false;
	}

	/**
	 * Get next sequence.
	 * 
	 * @param None
	 *            No parameters needed.
	 * @return <p>
	 *         Next sequence.
	 *         </p>
	 */
	public int nextSequence() {
		// Return result.
		return sequence != null ? sequence.nextInteger() : 0;
	}

	/**
	 * Init SMGP
	 * 
	 * @return Success Or Failed
	 */
	public boolean init(ServerObject serverObject) {
		Util.log.info(" CmppInit Init start: serverObject.getCmppServerIp()="+(serverObject!=null?serverObject.getCmppServerIp():"")+"  serverObject.getCmppServerPort()="+(serverObject!=null?serverObject.getCmppServerPort():"") );
		boolean flag = connect(serverObject.getCmppServerIp(),
				serverObject.getCmppServerPort());
		Util.log.info(" CmppInit Init end");
		return flag;
	}

	/**
	 * 清理所有内存
	 * 
	 * @param areaCode
	 */
	public synchronized static void closeAllThread(String areaCode) {
		// 干掉线程
		CmppSendThread sendThread = ParseConfig.getCmppSendHashMap().get(
				areaCode);
		if (sendThread != null) {
			if (!sendThread.isInterrupted()) {
				sendThread.interrupt();
				sendThread.setAliveFlag(false);
			}
		}
		CmppReceiveThread receiveThread = ParseConfig.getCmppReceiveHashMap()
				.get(areaCode);
		if (receiveThread != null) {
			if (!receiveThread.isInterrupted()) {
				receiveThread.interrupt();
				receiveThread.setAliveFlag(false);
			}
		}
		CmppActiveTestThread checkLinkThread = ParseConfig
				.getCmppCheckHashMap().get(areaCode);
		if (checkLinkThread != null) {
			if (!checkLinkThread.isInterrupted()) {
				checkLinkThread.interrupt();
				checkLinkThread.setAliveFlag(false);
			}
		}
	}
}
