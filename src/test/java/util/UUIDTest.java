package util;

import java.util.UUID;

import org.junit.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

public class UUIDTest {
	
	private static final Logger log = (Logger) LoggerFactory.getLogger(UUIDTest.class);
	
	@Test
	public void uuid(){
		log.debug("{}",UUID.randomUUID());
		
	}
	
	
}
