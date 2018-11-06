package finalProject;

import java.io.IOException;
import java.util.Vector;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint (value="/ss")
public class ServerSocket {
	
	private static Vector<Session> sessionVector = new Vector<Session>();
	
	@OnOpen
	public void open(Session session) {
		System.out.println("Connection made!");
		sessionVector.add(session);
	}
	
	@OnMessage
	public void onMessage(String message, Session session) {
		System.out.println(message);
		for(Session s: sessionVector) {
			try {
				s.getBasicRemote().sendText(message);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@OnClose
	public void close(Session session) {
		System.out.println("Disconnected");
		sessionVector.remove(session);
	}
	
	@OnError
	public void error(Throwable error) {
		error.printStackTrace();
	}
}
