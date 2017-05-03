package com.cmcciot.mat.pushapi.cmobile.cmpp;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmcciot.mat.pushapi.cmobile.constant.SocketConstant;
import com.cmcciot.mat.pushapi.cmobile.object.ServerObject;
import com.cmcciot.mat.pushapi.cmobile.object.SingleCmppObject;
import com.cmcciot.mat.pushapi.sms.util.Consts;
import com.cmcciot.mat.pushapi.sms.util.Util;
import com.cmcciot.mat.pushapi.sqlite.SQLiteExecute;
import com.google.gson.Gson;
import com.simpleteam.connection.ConnectionException;
import com.simpleteam.packet.BufferException;
import com.simpleteam.packet.cmpp.CMPP3Deliver;
import com.simpleteam.packet.cmpp.CMPP3DeliverResponse;
import com.simpleteam.packet.cmpp.CMPP3SubmitResponse;
import com.simpleteam.packet.cmpp.CMPPActiveTest;
import com.simpleteam.packet.cmpp.CMPPActiveTestResponse;
import com.simpleteam.packet.cmpp.CMPPCommandID;
import com.simpleteam.packet.cmpp.CMPPPacket;
import com.simpleteam.packet.cmpp.CMPPTerminate;
import com.simpleteam.packet.cmpp.CMPPTerminateResponse;

public class CmppReceiveThread extends Thread {

	/**
	 * 线程活动标志位
	 */
	private boolean aliveFlag = false;

	/**
	 * CMPP配置
	 */
	private SingleCmppObject singleCmppObject;

	/**
	 * 配置信息
	 */
	private ServerObject serverObject;

	/**
	 * Constructor
	 * 
	 * @param cmppConfig
	 */
	public CmppReceiveThread(ServerObject serverObject,
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
	 * Read packet.
	 * 
	 * @param None
	 *            No parameters needed.
	 * @return <p>
	 *         Read packet.
	 *         </p>
	 */
	public synchronized CMPPPacket readPacket() throws IOException {
		try {
			// Return result.
			return singleCmppObject.getConnection() != null ? (CMPPPacket) singleCmppObject
					.getConnection().readPacket() : null;
		} catch (BufferException e) {
			throw new IOException(e.getMessage());
		} catch (ConnectionException e) {
			throw new IOException(e.getMessage());
		}
	}

	/**
	 * Whether there is available.
	 * 
	 * @param None
	 *            No parameters needed.
	 * @return <p>
	 *         Return true, if packet available.
	 *         </p>
	 */
	public synchronized boolean available() throws IOException {
		try {
			// Return result.
			return singleCmppObject.getConnection() != null ? singleCmppObject
					.getConnection().available() : false;
		} catch (BufferException e) {
			throw new IOException(e.getMessage());
		} catch (ConnectionException e) {
			throw new IOException(e.getMessage());
		}
	}

	@Override
	public void run() {
		// 设置线程标志位
		setAliveFlag(SocketConstant.TRUE_FLAG);
		Long requestId = 0L;
		while (isAliveFlag()) {
			
			//Util.log.debug(requestId + " isAliveFlag ");
			try {
			    //Thread.sleep(serverObject.getReceiveThreadWaitTime());
			    Thread.sleep(100);
			} catch (InterruptedException e1) {
				setAliveFlag(SocketConstant.FALSE_FLAG);
				Util.log.error(e1.getMessage(), e1);
				Util.log.error(requestId + " CMPP Receive Thread Quit!"
						+ serverObject.getAreaCode());
				// 关闭链接
				close();
				// 关闭所有线程
				CmppInit.closeAllThread(serverObject.getAreaCode());
				return;
			}
			
			try {
				// Check available.
				if (available()) {
					requestId++;
					// Read packet.
					CMPPPacket packet = readPacket();
					if (packet == null) {
						Util.log.warn(requestId + " packet is null");
					}
					// Check instance.
					if (packet instanceof CMPP3Deliver) {
						Util.log.debug(requestId
								+ " packet instanceof CMPP3Deliver ");

						// Get deliver.
						CMPP3Deliver deliver = (CMPP3Deliver) packet;
						// Create deliver response.
						CMPP3DeliverResponse response = new CMPP3DeliverResponse(
								packet.sequence);
						// Set result.
						response.result = 0;
						// Set response.
						response.msg_id = deliver.msg_id;
						// Write response.
						asyncWritePacket(response,requestId);
						Util.log.debug(requestId + " response to "
								+ deliver.src_terminal_id + " with command: "
								+ deliver.command + " traceId: "
								+ deliver.traceID + " msg_id: "
								+ deliver.msg_id);

						if ((deliver.registered_delivery & 0x0f) == 1) {
							// 收到网关响应消息了
							Util.log.debug(requestId + " status of sended msg["
									+ deliver.msg_id + "] to ["
									+ deliver.src_terminal_id + "]:"
									+ deliver.status);
						} else {
							// 收到网关发来的上行消息了
							Util.log.info(requestId + " received msg["
									+ deliver.msg_id + "] from ["
									+ deliver.src_terminal_id + "] to ["
									+ deliver.dest_terminal_id + "]");
							// Print.
							// int msgLength = deliver.msg_length;
							int mgsType = deliver.msg_fmt;
							byte[] msg = deliver.msg_content;
							if (msg == null) {
								Util.log.warn(requestId
										+ " received msg from deliver is null and skip.");
								continue;
							}
							Util.log.debug(requestId
									+ " Deliver Message mgsType  = " + mgsType);
							String msgContent = null;
							if (mgsType == 0) {
								msgContent = new String(msg, "GBK");
								Util.log.debug(requestId
										+ " Deliver Message megContent = "
										+ msgContent);
							} else {
								msgContent = new String(msg, "UTF-16");
								Util.log.debug(requestId
										+ " Deliver Message megContent UTF-16 = "
										+ msgContent);
							}

							if (!"".equals(Consts.callbackUrl)
									&& Consts.callbackUrl != null) {
								new CallBackThread(deliver.src_terminal_id,
										msgContent).start();
							} else {
								Util.log.warn(requestId
										+ " Consts.callbackUrl is null");
							}
						}
					}
					// Check instance.
					else if (packet instanceof CMPPActiveTest) {
						Util.log.debug(requestId
								+ " Receive CMPP Activetest Packet!!!");
						// Create activetest response.
						CMPPActiveTestResponse response = new CMPPActiveTestResponse(
								packet.sequence);
						// Set result.
						response.success_id = 0;
						// Write response.
						asyncWritePacket(response,requestId);
					}
					// Check CMPPActiveTestResponse
					else if (packet instanceof CMPPActiveTestResponse) {
						Util.log.debug(requestId
								+ " Receive CMPP CMPPActiveTestResponse!!!");
					}
					// Check command.
					else if (packet instanceof CMPP3SubmitResponse) {
						Util.log.debug(requestId
								+ " packet instanceof CMPP3SubmitResponse");
						// Create submit response.
						CMPP3SubmitResponse response = (CMPP3SubmitResponse) packet;

						Util.log.debug(requestId + " response.sequence = "
								+ response.sequence + "   response.error = "
								+ response.error + "   response.msg_id = "
								+ response.msg_id);
						try {
							if (0 == response.error) {
								updateSmsStatus(response.sequence + "");
							}
						} catch (Exception ee) {
							Util.log.error(ee.getMessage(), ee);
						}
					} else {
						Util.log.warn(requestId + " unknow package: "
								+ new Gson().toJson(packet));
					}
				}
			} catch (Exception e) {
				Util.log.error(requestId + " CMPP Receive Thread Quit!", e);
			}
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
		CMPPTerminate terminate = new CMPPTerminate();
		// Set sequence.
		terminate.sequence = nextSequence();

		try {
			// Write packet.
			singleCmppObject.getConnection().writePacket(terminate);
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
				Util.log.info("exit response was received !");
			} else {
				Util.log.info("invalid exit response packet !");
				return false;
			}
			// Set authenticated.
			singleCmppObject.setAuthenticated(false);
			// Return true.
			return true;
		} catch (Exception e) {
			Util.log.error(e.getMessage(), e);
		}
		// Return false.
		return false;
	}

 
/*	public synchronized void writePacket(CMPPPacket packet) throws IOException {
		try {
			// Return result.
			if (singleCmppObject.getConnection() != null)
				singleCmppObject.getConnection().writePacket(packet);
		} catch (BufferException e) {
			throw new IOException(e.getMessage());
		} catch (ConnectionException e) {
			throw new IOException(e.getMessage());
		}
	}*/

	/**
	 * 异步的写回包
	 * @param packet
	 * @throws IOException
	 */
	public void asyncWritePacket(CMPPPacket packet,long requestId) throws IOException {
		final CMPPPacket p2=packet;
		Util.log.info("asyncWritePacket,requestId="+requestId);
		Runnable r=new Runnable(){
			private Logger log = LoggerFactory.getLogger(this.getClass());
			@Override
			public void run() {
				try {

					synchronized (singleCmppObject) {

						if (singleCmppObject.getConnection() != null)
							singleCmppObject.getConnection().writePacket(p2);
					}
				} catch ( Exception e) {
					log.error("writePacket error", e);
				}  
				
			}
			
		};
		
		new Thread(r).start();
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
	 * 更新短信发送状态为失败
	 * 
	 * @param sequenceNo
	 */
	private void updateSmsStatus(String sequenceNo) {
		final String sequenceNo2= sequenceNo;
		Util.log.info("updateSmsStatus,sequenceNo="+sequenceNo);
		/*StringBuffer updateSQL = new StringBuffer(
				"update sms_msg  set  process_status=0  where SMSID='"
						+ sequenceNo + "'");
		SQLiteExecute.executeUpdate(updateSQL.toString());
		*/
		Runnable r=new Runnable(){

			@Override
			public void run() {
				StringBuffer updateSQL = new StringBuffer(
						"update sms_msg set process_status=0 where SMSID='"
								+ sequenceNo2 + "'");
				SQLiteExecute.executeUpdate(updateSQL.toString());
			}
			
		};
		new Thread(r).start();
		
	}

}
