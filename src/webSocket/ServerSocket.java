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
	private String classID;
	private Session session;
	
	public Session getSession() {
		return session;
	}
	
	public String getUserID() {
		return userID;
	}
	
	// https://www.mkyong.com/webservices/jax-rs/jax-rs-queryparam-example/
	@OnOpen
	public void open(@QueryParam("userID") String userID, @QueryParam("classID") String classID, Session session) {
		TimestampUtil.printMessage(userID + " from class " + classID + " is trying to open a connection.");
		this.session = session;
		this.userID = userID;
		this.classID = classID;
		GlobalSocketMap.add(userID, this);
		TimestampUtil.printMessage(userID + " has opened a connection.");
	}
	
	@OnMessage
	public void onMessage(String message, Session session) {
		
		Message m = gson.fromJson(message, Message.class);
		m.setTimeStamp();
		
		Utilities.saveMessageToDB(m);
		
		String type = m.getType();

		TimestampUtil.printMessage("Broadcasting message sent by " + userID + " from class " + classID + " of type " + m.getType() + ": " + m.getData());

		if (type.equals("NewMessage")) {
			for (ServerSocket s : GlobalSocketMap.map.values()) {
				if (s.classID.equals(m.getClassID())) {
					s.session.getAsyncRemote().sendText(gson.toJson(m));
				}
			}
		} else if (type.equals("Vote")) {
			for (ServerSocket s : GlobalSocketMap.map.values()) {
				if (s.classID.equals(m.getClassID())) {
					s.session.getAsyncRemote().sendText(gson.toJson(m));
				}
			}
		}
	}
	
	@OnClose
	public void close(Session session) {
		TimestampUtil.printMessage(userID + " has closed the connection.");
	}
	
	@OnError
	public void error(Throwable error) {
		error.printStackTrace();
	}
}
