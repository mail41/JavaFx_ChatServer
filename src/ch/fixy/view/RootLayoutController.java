package ch.fixy.view;

import java.net.URL;
import java.util.ResourceBundle;

import ch.fixy.controller.ComponentController;
import ch.fixy.server.Server;
import ch.fixy.util.LoggerUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class RootLayoutController implements Initializable {

	// @FXML은 무조건 private
	@FXML
	private TextArea log;
	
	@FXML
	private TextArea textArea;
	
	@FXML
	private Button startBtn;
	
	@FXML
	private TextField text;
	
	public static RootLayoutController inst; // final은 생성자에만 쓸 수 있음
	
	private Server server;
	
	private boolean chatValid;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		chatValid = false; // 기본 서버 중지 상태
		inst = this;
	}
	
	public static RootLayoutController getInst() {
		return inst;
	}

	@FXML
	private void startChat() {
		// 서버가 동작 중이지 않으면
		if(!chatValid) {			
			chatValid = true;
			
			server= new Server();
			server.setDaemon(true);
			server.start();
			
			ComponentController.changeBtnText(startBtn, "채팅 종료하기");
			ComponentController.printServerlog(log, "채팅시작!");		
			LoggerUtil.info("채팅 서버 실행...");
		}else {
			chatValid = false;
			
			ComponentController.changeBtnText(startBtn, "채팅 시작하기");
			server.serverStop();
			server = null;
		}
	}

	public TextArea getLog() {
		return log;
	}
	
	
}
