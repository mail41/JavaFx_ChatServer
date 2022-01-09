package ch.fixy.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ch.fixy.util.LoggerUtil;
import ch.fixy.util.UuidUtil;
import ch.fixy.model.Client;
import ch.fixy.model.ClientBucket;
import ch.fixy.controller.ComponentController;
import ch.fixy.view.RootLayoutController;
import javafx.scene.control.TextArea;

public class Server extends Thread{
	
	private ServerSocket serverSocket;
	private ExecutorService executorService;
	private RootLayoutController rootLayoutController;
	private TextArea log;
	private boolean serverStatus;
	
	public Server() {
		// 인자 개수만큼 고정된 스레드풀 생성
		executorService = Executors.newFixedThreadPool(
									Runtime.getRuntime().availableProcessors());
		rootLayoutController = RootLayoutController.getInst();
		log = rootLayoutController.getLog();
		serverStatus = true;				
	}
	
	public void serverStop() {
		serverStatus = false;
		
		try {
			int bucketSize = ClientBucket.getClientBucketMap().size();
			
			if(bucketSize >= 1) {
				ClientBucket.getClientBucketMap()
				.entrySet()
				.parallelStream()
				.forEach(clientElement -> {
					Client client = clientElement.getValue();
					client.getPw().println("서버가 종료 되었습니다.");
					try {
						client.getSocket().close();
					} catch (IOException e) {
						System.out.println("클라이언트 접속 해제!");
					}
				});
				ClientBucket.getClientBucketMap().clear();
			}
			serverSocket.close();
			executorService.shutdown();					
		} catch (IOException e) {
			LoggerUtil.error(e.getMessage());
		}
	}
	
	@Override
	public void run() {	
		try {
			 serverSocket = new ServerSocket(10001);
			
				// while문을 사용해서 소켓서버를 열어둔다
				while(serverStatus) {		
					ComponentController.printServerlog(log, "클라이언트 기다리는 중...");
					ComponentController.printServerlog(log, "현재 접속 클라이언트 [ " + ClientBucket.getClientBucketMap().size() + " ]");
						Socket socket = serverSocket.accept();
						
						String clientId = UuidUtil.getUuid();
						Client client = new Client(socket, clientId);
						ClientBucket.getClientBucketMap().put(clientId, client);
						executorService.submit(client); // 쓰레드 풀
						LoggerUtil.info("CLIENT SOCKET CONNECTED...[ " + clientId + " ]");
				}
		}catch(IOException e) {
			ComponentController.printServerlog(log, "서버 종료!");
			LoggerUtil.error(e.getMessage());
		}catch(Exception e) {
			ComponentController.printServerlog(log, "서버  비정상 종료!");
			LoggerUtil.error(e.getMessage());
		}
	}		
	
}

