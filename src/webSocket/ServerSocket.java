package webSocket;

import java.io.IOException;
import java.util.Vector;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import javax.ws.rs.QueryParam;

import com.google.gson.Gson;

import dataModels.Message;

@ServerEndpoint (value="/chatWS")
public class ServerSocket {
	
	private Gson gson = new Gson();
	private String userID;
	private Session session;
	
	// https://www.mkyong.com/webservices/jax-rs/jax-rs-queryparam-example/
	@OnOpen
	public void open(@QueryParam("userID") String userID, Session session) {
		TimestampUtil.printMessage(userID + " is trying to open a connection.");
		this.session = session;
		this.userID = userID;
		GlobalSocketMap.add(userID, this);
		TimestampUtil.printMessage(userID + " has opened a connection.");
	}
	
	@OnMessage
	public void onMessage(String message, Session session) {
		
		Message m = gson.fromJson(message, Message.class);
		String type = m.getType();
		if (type.equals("NewMessage")) {
			
		} else if (type.equals("Vote")) {
			
		}
		
		System.out.println(message);
		
		/*
		for(Session s: sessionVector) {
			try {
				s.getBasicRemote().sendText(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		*/
	}
	
	@OnClose
	public void close(Session session) {
		System.out.println("Disconnected");
	}
	
	@OnError
	public void error(Throwable error) {
		error.printStackTrace();
	}
}
