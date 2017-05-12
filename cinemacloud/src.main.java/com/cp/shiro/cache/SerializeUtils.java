package com.cp.shiro.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializeUtils<K,V>{
	


	public static <K> byte[] serialize(K key) {
		 if (key == null) {  
	            return null;  
	        }  
	        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);  
	        try {  
	            ObjectOutputStream oos = new ObjectOutputStream(baos);  
	            oos.writeObject(key);  
	            oos.flush();  
	        }  
	        catch (IOException ex) {  
	            throw new IllegalArgumentException("Failed to serialize object of type: " + key.getClass(), ex);  
	        }  
	        return baos.toByteArray();  
	}

	public static Object deserialize(byte[] rawValue) {
		if (rawValue == null) {  
            return null;  
        }  
        try {  
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(rawValue));  
            return ois.readObject();  
        }  
        catch (IOException ex) {  
            throw new IllegalArgumentException("Failed to deserialize object", ex);  
        }  
        catch (ClassNotFoundException ex) {  
            throw new IllegalStateException("Failed to deserialize object type", ex);  
        } 
	}

}
