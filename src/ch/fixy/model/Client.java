package ch.fixy.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import ch.fixy.util.LoggerUtil;
import ch.fixy.common.ServerSplitCode;
import ch.fixy.controller.ComponentController;
import ch.fixy.view.RootLayoutController;
import javafx.scene.control.TextArea;

public class Client extends Thread {
	
	private Socket socket;
	private TextArea log;
	private String clientId;
	private String nickName;
	
   /**
    * SOCKET I/O
    */
	private InputStreamReader isr;
	private BufferedReader br;
	
	private OutputStreamWriter osw;
	private PrintWriter pw;

	public Client(Socket socket, String clientId) {
		this.log = RootLayoutController.getInst().getLog();
		this.socket = socket;
		this.clientId = clientId;
	}

	@Override
	public void run() {
		try {
			isr = new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8);
			br = new BufferedReader(isr);
			
			osw = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
			pw = new PrintWriter(osw, true);
			
			String payLoad;
			String[] payLoadElement;
			Integer cmd;
			String msg;
			
			ComponentController.printServerlog(log, "[ " + clientId + " ]" + " SOCKET : " + socket.isConnected());
			
			while(true) {
				payLoad = br.readLine();
				
				if (payLoad == null) {
					throw new IOException();
				}
				
				payLoadElement = payLoad.split(ServerSplitCode.SPLIT.getCode());
				ComponentController.printServerlog(log, "[" + clientId + "] "+ " SEVER CODE: " + "[ " + payLoadElement[0] + " ]" + "COTENTS : [ " + payLoadElement[1] + " ]");
				LoggerUtil.info("[ " + clientId + " ]" + " [ " + Thread.currentThread().getName() + " ]");
				
				cmd = Integer.parseInt(payLoadElement[0]);
				msg = payLoadElement[1];
				
				switch (cmd.intValue()) {
				
				case 0 : 
					// 입장
					doJoin(msg);
					doSendMsg("[ " + this.nickName + " ] 님이 채팅방에 접속 하셨습니다.");
					ComponentController.printServerlog(log, "[ " + clientId + " ]"+ " NICK NAME : " + msg);
					break;
				case 1 :
					// 퇴장
					doSendMsg("[ "+ this.nickName + " ] 님이 채팅을 종료 하셨습니다.");
					doQuit();
					break;
				default : 
					// 메시지 전송
					doSendMsg(this.nickName + " : " +  msg);
					break;
				}
			}
		
		} catch (IOException e) {
			// 접속 종료
			ComponentController.printServerlog(log, "[ " +  clientId  + " ]" + "강제 종료!");
			doQuit();
		}
	}

	private void doJoin(String nickName) {
		this.nickName = nickName;
	}
	
	private void doSendMsg(String msg) {
		int bucketSize = ClientBucket.getClientBucketMap().size();
		
		if(bucketSize >= 1) {
			ClientBucket.getClientBucketMap()
			.entrySet()
			.parallelStream() // 병렬처리
			.filter(clientElement -> {return !clientElement.getKey().equals(this.clientId);})
			.forEach(clientElement -> {
				clientElement.getValue().getPw().println(msg);
			});	
		}
	}
	
	public void doQuit() {
		ClientBucket.getClientBucketMap().remove(this.clientId);
	}
	
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	
	public void getNickName(String nickName) {
		this.nickName = nickName;
	}
	
	public BufferedReader getBr() {
		return br;
	}
	
	public PrintWriter getPw() {
		return pw;
	}
	
	public Socket getSocket() {
		return socket;
	}
	
}