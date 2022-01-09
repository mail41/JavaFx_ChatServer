package ch.fixy.model;

import java.util.concurrent.ConcurrentHashMap;

public class ClientBucket {
	private static ConcurrentHashMap<String, Client> getClientBucketMap = new ConcurrentHashMap<>();
	
	private ClientBucket() {}
	
	public static ConcurrentHashMap<String, Client> getClientBucketMap() {
		return getClientBucketMap;
	}
}
