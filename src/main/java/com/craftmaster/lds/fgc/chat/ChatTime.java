package com.craftmaster.lds.fgc.chat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatTime {
	public String getCurrentTime(){
			// Returns formatted current time as a string.
		   DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd h:mm:ss a");  
		   LocalDateTime now = LocalDateTime.now();  
		   //System.out.println(dtf.format(now)); 
		   return dtf.format(now);
	}

}
