package com.cmcciot.mat.pushapi.cmobile.cmpp;

import java.io.IOException;
import com.cmcciot.mat.pushapi.cmobile.constant.SocketConstant;
import com.cmcciot.mat.pushapi.cmobile.object.CmppSendSmsObject;
import com.cmcciot.mat.pushapi.cmobile.object.ServerObject;
import com.cmcciot.mat.pushapi.cmobile.object.SingleCmppObject;
import com.cmcciot.mat.pushapi.sms.util.Util;
import com.cmcciot.mat.pushapi.sqlite.SQLiteExecute;
import com.simpleteam.connection.ConnectionException;
import com.simpleteam.log.EventID;
import com.simpleteam.log.LogRequests;
import com.simpleteam.packet.BufferException;
import com.simpleteam.packet.cmpp.CMPP3Submit;
import com.simpleteam.packet.cmpp.CMPPActiveTest;
import com.simpleteam.packet.cmpp.CMPPCommandID;
import com.simpleteam.packet.cmpp.CMPPPacket;
import com.simpleteam.packet.cmpp.CMPPTerminate;
import com.simpleteam.packet.cmpp.CMPPTerminateResponse;

public class CmppSendThread extends Thread {
	// 短信内容长度
	int maxLength = 140;
	int curSequnce = 100000000;

	/**
	 * Constructor
	 * 
	 * @param serverObject
	 *            配置
	 */
	public CmppSendThread(ServerObject serverObject,
			SingleCmppObject singleCmppObject) {
		this.serverObject = serverObject;
		this.singleCmppObject = singleCmppObject;
	}

	/**
	 * @return aliveFlag
	 */
	public synchronized boolean isAliveFlag() {
		return aliveFlag;
	}

	/**
	 * @param aliveFlag
	 *            aliveFlag
	 */
	public synchronized void setAliveFlag(boolean aliveFlag) {
		this.aliveFlag = aliveFlag;
	}

	/**
	 * @return the singleCmppObject
	 */
	public synchronized SingleCmppObject getSingleCmppObject() {
		return singleCmppObject;
	}

	/**
	 * @return the serverObject
	 */
	public synchronized ServerObject getServerObject() {
		return serverObject;
	}

	/**
	 * 发送一条小于140字节长度的独立短信
	 * 
	 * @throws IOException
	 */
	private void sendOneFullSms(CmppSendSmsObject singleMsg, byte[] msg_content)
			throws IOException {
		int sequence = 0;
		// 初始化，需要按传入的短信来获取sequence
		if (singleMsg.getSequenceNo().equals("100000000")) {
			sequence = Integer.valueOf(singleMsg.getSequenceNo())
					+ curSequnce++;
		}
		// 每次发独立短信都加sequence都加1
		else {
			sequence = Integer.valueOf(singleMsg.getSequenceNo())
					+ curSequnce++;
		}
		CMPP3Submit submit = new CMPP3Submit(sequence);
		// 为了兼容两个网关编码统一为GBK
		byte[] gbk_msg_content = Util.UTF16BEToGBK(msg_content);
		// Set parameters.
		submit.msg_id = 1L;
		// Packet Total
		submit.pk_total = 1;
		// Packet Number
		submit.pk_number = 1;
		// Registered Delivery
		submit.registered_delivery = 1;// Report needed.
		// Service ID
		// submit.service_id = "ptzta";

		submit.service_id = this.getServerObject().getServiceId();
		// Fee User Type
		submit.fee_user_type = 0;
		// Fee Terminal ID
		submit.fee_terminal_id = null;
		// TP_PID
		submit.tp_pid = 0;
		// TP_UDHI
		submit.tp_udhi = 0;
		// Message encoding
		if (1 == singleMsg.getCarrierType()) {
			submit.msg_fmt = 15; // 第三方还是用GBK
		} else {
			submit.msg_fmt = 0; // 移动自己短信网关用UCS2即 标准的unicode编码
		}
		// Message Source
		submit.msg_src = serverObject.getEnterPriseNo();
		// Fee Type
		submit.fee_type = "01";
		// Fee Code
		submit.fee_code = "000000";
		// Valid Time
		submit.valid_time = null;
		// AT Time
		submit.at_time = null;
		// Need Report
		if (serverObject.isReportNeedFlag()) {
			submit.registered_delivery = 1;
		} else {
			submit.registered_delivery = 0;
		}
		// Source Terminal ID
		submit.src_terminal_id = serverObject.getAccessNo();
		// Total Destination User
		submit.dest_usr_tl = 1;
		// Destination Terminal ID
		submit.dest_terminal_id = new String[] { singleMsg.getToUserPhone() };
		// Link id.
		submit.link_id = "012345678901234567890";
		// Reserved Field
		submit.reserve = null;
		// Message Content
		if (1 == singleMsg.getCarrierType()) {
			submit.msg_content = gbk_msg_content; // 第三方还是用GBK
		} else {
			submit.msg_content = msg_content;// 移动自己短信网关用UCS2即 标准的unicode编码
		}
		// Message Length
		submit.msg_length = submit.msg_content.length;
		// Reserved Field
		submit.reserve = "";
		// Write packet.
		writePacket(submit);
	}

	/**
	 * 发送一条小于140字节长度的长短信之一的片断
	 * 
	 * @throws IOException
	 */
	private void sendOnePartSms(int i, CmppSendSmsObject singleMsg,
			byte[] msg_content, byte[] msgHead, int msgSendCount)
			throws IOException {
		CMPP3Submit submit = new CMPP3Submit(Integer.valueOf(singleMsg
				.getSequenceNo()));
		msgHead[5] = (byte) (i + 1);
		byte[] needMsg = null;
		// 消息头+消息内容拆分
		if (i != msgSendCount - 1) {
			int start = (maxLength - 6) * i;
			int end = (maxLength - 6) * (i + 1);
			needMsg = Util.getMsgBytes(msg_content, start, end);
		} else {
			int start = (maxLength - 6) * i;
			int end = msg_content.length;
			needMsg = Util.getMsgBytes(msg_content, start, end);
		}
		// int subLength = needMsg.length + msgHead.length;
		byte[] sendMsg = null;
		if (1 == singleMsg.getCarrierType()) {
			byte[] gbk_msg_content = Util.UTF16BEToGBK(needMsg);
			sendMsg = new byte[gbk_msg_content.length + msgHead.length];
			System.arraycopy(msgHead, 0, sendMsg, 0, 6);
			System.arraycopy(gbk_msg_content, 0, sendMsg, 6,
					gbk_msg_content.length);
		} else {
			byte[] ucs2_msg_content = needMsg;
			sendMsg = new byte[ucs2_msg_content.length + msgHead.length];
			System.arraycopy(msgHead, 0, sendMsg, 0, 6);
			System.arraycopy(ucs2_msg_content, 0, sendMsg, 6,
					ucs2_msg_content.length);
		}

		// Set parameters.
		submit.msg_id = 1L;
		// Packet Total
		submit.pk_total = (byte) msgSendCount;
		// Packet Number
		submit.pk_number = (byte) (i + 1);
		// Registered Delivery
		submit.registered_delivery = 1;// Report needed.
		// Service ID
		// submit.service_id = "ptzta";
		submit.service_id = this.getServerObject().getServiceId();
		// Fee User Type
		submit.fee_user_type = 0;
		// Fee Terminal ID
		submit.fee_terminal_id = null;
		// TP_PID
		submit.tp_pid = 0;
		// TP_UDHI
		submit.tp_udhi = 1;
		// Message encoding
		if (1 == singleMsg.getCarrierType()) {
			submit.msg_fmt = 15; // 第三方还是用GBK
		} else {
			submit.msg_fmt = 0; // 移动自己短信网关用UCS2即 标准的unicode编码
		}
		// Message Source
		submit.msg_src = serverObject.getEnterPriseNo();
		// Fee Type
		submit.fee_type = "01";
		// Fee Code
		submit.fee_code = "000000";
		// Valid Time
		submit.valid_time = null;
		// AT Time
		submit.at_time = null;
		// Need Report
		if (serverObject.isReportNeedFlag()) {
			submit.registered_delivery = 1;
		} else {
			submit.registered_delivery = 0;
		}
		// Source Terminal ID
		submit.src_terminal_id = serverObject.getAccessNo();
		// Total Destination User
		submit.dest_usr_tl = 1;
		// Destination Terminal ID
		submit.dest_terminal_id = new String[] { singleMsg.getToUserPhone() };
		// Link id.
		submit.link_id = "012345678901234567890";
		// Reserved Field
		submit.reserve = null;
		// Message Content
		submit.msg_content = sendMsg;
		// Message Length
		submit.msg_length = submit.msg_content.length;
		// Reserved Field
		submit.reserve = "";

		Util.log.info("send long message: ["
				+ (i + 1)
				+ " / "
				+ msgSendCount
				+ "]  msg is "
				+ ((1 == singleMsg.getCarrierType()) ? new String(sendMsg,
						"GBK") : new String(sendMsg, "UTF-16BE")));
		// Write packet.
		writePacket(submit);
	}

	/**
	 * 线程运行体
	 */
	public void run() {
		// 设置线程标志位
		setAliveFlag(SocketConstant.TRUE_FLAG);
		// 初始化发送列表
		CmppSendSmsObject singleMsg;

		while (isAliveFlag()) {
			try {
				singleMsg = SendQueue.takeSmsTask();
				if (singleMsg == null) {
					Thread.sleep(SocketConstant.SEND_THREAD_WAIT_TIME);
					continue;
				}
			} catch (Exception e) {
				Util.log.error("takeSmsTask error ", e);
				continue;
			}
			Util.log.info( "send sms to"+singleMsg.getToUserPhone()+",content="+singleMsg.getContent());
			
			
			
			if (singleMsg.getContent().equals("ActiveTest")) {
				Util.log.debug("CmppSendThread  ActiveTest  send start");
				CMPPActiveTest cmppTestPacket = new CMPPActiveTest(
						nextSequence());
				try {
					writePacket(cmppTestPacket);
					Util.log.debug("CmppSendThread  ActiveTest  send end");
				} catch (IOException e) {
					setAliveFlag(SocketConstant.FALSE_FLAG);
					Util.log.error(e.getMessage(), e);
					Util.log.error("CmppSendThread  "
							+ serverObject.getAreaCode()
							+ "   ActiveTest  send failed");
					// 关闭链接
					close();
					// 关闭所有线程
					CmppInit.closeAllThread(serverObject.getAreaCode());
					return;
				}
				Util.log.debug("CmppSendThread  " + serverObject.getAreaCode()
						+ "   ActiveTest  send success");
				continue;
			}

			try {
				// Create submit.
				byte[] msg_content = singleMsg.getContent()
						.getBytes("UTF-16BE");
				int msgLength = msg_content.length;
				// 这儿的逻辑是如果非移动标准短信网关，如果长度小于255直接按一条来发送，如果长度大于255截成多条来发送，如果是移动标准短信网关（支持长短信），按长短信协议来发
				if ((1 == singleMsg.getCarrierType())
						|| (0 == singleMsg.getCarrierType() && msgLength <= maxLength)) {
					Util.log.info("send short message:"
							+ singleMsg.getContent());
					if (1 == singleMsg.getCarrierType() && msgLength > 255) {// 第三条短信接口，且长度大于255
																				// 第三方接口短信长度为255,因为Unicode原因，我们取偶数
						int thirdSmsLength = 254;
						int curSmslength = 254;
						int smsNum = (msgLength % 254 == 0) ? (msgLength / 254)
								: (msgLength / 254 + 1);
						for (int i = 0; i < smsNum; i++) {
							byte[] oneAllSmsBytes = new byte[curSmslength];
							if (i != smsNum - 1) {
								int start = thirdSmsLength * i;
								int end = thirdSmsLength * (i + 1);
								oneAllSmsBytes = Util.getMsgBytes(msg_content,
										start, end);
							} else {
								int start = thirdSmsLength * i;
								int end = msg_content.length;
								oneAllSmsBytes = Util.getMsgBytes(msg_content,
										start, end);
							}

							sendOneFullSms(singleMsg, oneAllSmsBytes);
							Util.log.info("send ind sms [" + (i + 1) + "/"
									+ smsNum + "]: "
									+ new String(oneAllSmsBytes, "UTF-16BE"));
						}
					} else {// 发送字节长度小于140的短信
						sendOneFullSms(singleMsg, msg_content);
					}
				} else {// 发送长短信

					// 拆分的条数
					int msgSendCount = msgLength % (maxLength - 6) == 0 ? msgLength
							/ (maxLength - 6)
							: msgLength / (maxLength - 6) + 1;

					Util.log.info("send long message:msgSendCount is "
							+ msgSendCount);
					// 短信息内容头拼接
					byte[] msgHead = new byte[6];
					// 表示剩余协议头的长度
					msgHead[0] = 0x05;
					msgHead[1] = 0x00;
					msgHead[2] = 0x03;
					// 这批短信的唯一标志，事实上，SME(手机或者SP)把消息合并完之后，
					// 就重新记录，所以这个标志是否唯一并不是很重要。
					msgHead[3] = (byte) Util.getSequence();
					// 这批短信的数量。如果一个超长短信总共5条，这里的值就是5。
					msgHead[4] = (byte) msgSendCount;
					msgHead[5] = 0x01;

					for (int i = 0; i < msgSendCount; i++) {// 逐条发送带头的片断短信
						sendOnePartSms(i, singleMsg, msg_content, msgHead,
								msgSendCount);
					}
				}
			} catch (Exception e) {
				// e.printStackTrace();
				Util.log.error("send sms error", e);
				setAliveFlag(SocketConstant.FALSE_FLAG);
				Util.log.error("send msg [" + singleMsg.getContent()
						+ "]  to [" + singleMsg.getToUserPhone() + "] failed");
				Util.log.error("CMPP Send Thread Quit!" + e.getMessage());
				// 关闭链接
				close();
				// 关闭所有线程
				CmppInit.closeAllThread(serverObject.getAreaCode());
				try {
					updateSmsStatus(singleMsg.getSequenceNo());
				} catch (Exception ee) {
					Util.log.error(ee.getMessage(), ee);
				}
				return;
			}

			Util.log.info("send msg [" + singleMsg.getContent() + "]  to ["
					+ singleMsg.getToUserPhone() + "] success");

		}
	}

	/**
	 * 新增消息
	 * @param message
	 *            消息
	 */
	public void addMsgToList(CmppSendSmsObject message) {
		try {
			SendQueue.addSmsTask(message);
		} catch (InterruptedException e) {
			  Util.log.error("addSmsTask error",e);
			  this.updateSmsStatus(message.getSequenceNo());
		}
		 
	 
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
		return singleCmppObject.getSequence() != null ? singleCmppObject
				.getSequence().nextInteger() : 0;
	}

	/**
	 * Write packet.
	 * 
	 * @param packet
	 *            Packet for writing.
	 * @return <p>
	 *         No results returned.
	 *         </p>
	 */
	public synchronized void writePacket(CMPPPacket packet) throws IOException {
		try {
			// Return result.
			if (singleCmppObject.getConnection() != null)
				singleCmppObject.getConnection().writePacket(packet);
		} catch (BufferException e) {
			throw new IOException(e.getMessage());
		} catch (ConnectionException e) {
			throw new IOException(e.getMessage());
		}
	}

	/**
	 * Close.
	 * 
	 * @param None
	 *            No parameters needed.
	 * @return <p>
	 *         No results returned.
	 *         </p>
	 */
	public void close() {
		// Check status.
		if (!singleCmppObject.getConnection().isClosed()) {
			// Check terminated.
			if (!singleCmppObject.getConnection().isTerminated()) {
				// Check authenticated.
				if (!singleCmppObject.isAuthenticated()) {
					/*
					 * //Log event.
					 * if(LogRequests.isRequested(EventID.SMGP3_PACKET |
					 * EventID.INFORMATION)) SimpleLog.log(CLASS_NAME,"close").
					 * message("session is not authenticated !").end();
					 */
				}
				// Logout session.
				else if (!logout()) {
					/*
					 * //Log event.
					 * if(LogRequests.isRequested(EventID.SMGP3_PACKET |
					 * EventID.INFORMATION)) SimpleLog.log(CLASS_NAME,"close").
					 * message("fail to logout !").end();
					 */
				}
			}
			// 关闭链接
			try {
				singleCmppObject.getConnection().close();
			} catch (ConnectionException e) {
				Util.log.error(e.getMessage(), e);
			}
			// Log event.
			Util.log.info("connection was closed !");
		} else {
			Util.log.info("connection has already been closed !");
		}
	}

	/**
	 * Logout session.
	 * 
	 * <p>
	 * It depends on session type and gateway type. It must be overrided.
	 * </p>
	 * 
	 * @param None
	 *            No parameters needed.
	 * @return <p>
	 *         Return true, if logouted successfully.
	 *         </p>
	 */
	private boolean logout() {
		// Create exit.
		CMPPTerminate exit = new CMPPTerminate();
		// Set sequence.
		exit.sequence = nextSequence();

		try {
			// Write packet.
			singleCmppObject.getConnection().writePacket(exit);
			// Read packet.
			CMPPPacket cmpp = (CMPPPacket) singleCmppObject.getConnection()
					.readPacket();
			// Check result.
			if (cmpp == null) {
				Util.log.error("fail to read packet !");
				return false;
			}
			// Check command.
			if (cmpp.command == CMPPCommandID.TERMINATE) {
				// Create response.
				CMPPTerminateResponse response = new CMPPTerminateResponse(
						cmpp.sequence);
				// Write response.
				singleCmppObject.getConnection().writePacket(response);
				// Log event.
				Util.log.info("exit request was received !");
			} else if (cmpp.command == CMPPCommandID.TERMINATE_RESPONSE) {
				// Log event.
				if (LogRequests.isRequested(EventID.CMPP_PACKET
						| EventID.INFORMATION))
					Util.log.info("exit response was received !");
			} else {
				// Log event.
				if (LogRequests.isRequested(EventID.CMPP_PACKET
						| EventID.EXCEPTION))
					Util.log.info("invalid exit response packet !");
				return false;
			}
			// Set authenticated.
			singleCmppObject.setAuthenticated(false);
			// Return true.
			return true;
		} catch (Exception e) {
			if (LogRequests.isRequested(EventID.CMPP_PACKET | EventID.EXCEPTION)) {
				Util.log.error(e.getMessage(), e);
			}
		}
		// Return false.
		return false;
	}

	/**
	 * 日志Log4j
	 */

	/**
	 * 配置
	 */
	private ServerObject serverObject;

	/**
	 * SMGP配置
	 */
	private SingleCmppObject singleCmppObject;

	/**
	 * 线程活动标志位
	 */
	private boolean aliveFlag = false;

	/**
	 * 更新短信发送状态为失败
	 * 
	 * @param sequenceNo
	 */
	private void updateSmsStatus(String sequenceNo) {
		StringBuffer updateSQL = new StringBuffer(
				"update sms_msg  set  process_status=0  where SMSID='"
						+ sequenceNo + "'");
		SQLiteExecute.executeUpdate(updateSQL.toString());
	}

}
