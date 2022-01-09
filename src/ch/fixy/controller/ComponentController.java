package ch.fixy.controller;

import java.lang.ref.WeakReference;
import java.util.logging.Logger;

import ch.fixy.util.DateUtil;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class ComponentController {
	
   /**
    * 싱글톤 LazyHolder 패턴
    */
	
   public final static Logger log = Logger.getGlobal();
	
   private ComponentController() {}
   
   private static class LazyHolder {
	   // 최초에 한개만 생성 시켜준다
      public static final ComponentController INSTANCE = new ComponentController();
   }
   
   public static ComponentController getInstance() {
      return LazyHolder.INSTANCE;
   }
   
   // main 스레드와 따로 비동기방식으로 텍스트를 textArea에 append 시켜주는 스레드(병렬처리)
   public void printToTextArea(TextArea textArea, String msg) {
	   Platform.runLater(() -> {
			textArea.appendText( msg + "\n");
		});
   }
   
   public void printToTextField(TextArea textArea, String msg) {
	   Platform.runLater(() -> {
			textArea.appendText( msg + "\n");
		});
   }
   
   // 서버 로그 출력
   public static void printServerlog(TextArea textArea, String msg) {
	   Platform.runLater(() -> {
		   StringBuffer sb = new StringBuffer();
		   // WeakReference(약한참조) - 효율적인 메모리관리를 위해 사용
		   WeakReference<StringBuffer> weakReference = new WeakReference<StringBuffer>(sb);
		   
		   sb.append("[");
		   sb.append(DateUtil.getDate());
		   sb.append("]");
		   sb.append(msg);
		   sb.append("\n");
		   
		   textArea.appendText(sb.toString());
		   sb = null;
	   });
   }
   
   // 버튼 text 체인지
   public static void changeBtnText(Button button, String msg) {
		Platform.runLater(() -> {
			button.setText(msg);
		});
	}
}
