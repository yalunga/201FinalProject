package webSocket;

import java.util.concurrent.ConcurrentHashMap;

public class GlobalSocketMap {

	// [userID: ServerSocket]
	private static ConcurrentHashMap<String, ServerSocket> map = new ConcurrentHashMap<>();
	
	public static boolean add(String userID, ServerSocket ss) {
		if (map.containsKey(userID)) {
			return false;
		} else {
			map.put(userID, ss);
			return true;
		}
	}
	
	public static boolean remove(String userID) {
		if (map.containsKey(userID)) {
			map.remove(userID);
			return true;
		}
		return false;
	}
	
}
