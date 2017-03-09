package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private RequestType request_type;
    private Socket connection;
    private byte[] body_buffer;
    
    private enum RequestType{
    	GET,
    	POST,
    	ERROR,
    }
    
    public RequestHandler(Socket connectionSocket) 
    {
        this.connection = connectionSocket;
    }
    private void process()
    {
    	
    }
    private void analyze(String[] tokens)
    {
    	if(tokens[0].equals("GET"))
    	{
    		request_type = RequestType.GET;
    		if(tokens[1].endsWith(".html")){
    			body_buffer = loadResourceFile(tokens[1]);
    		}
    		
    	}
    	
    }
    private void parseRequest(InputStream in)
    {
    	BufferedReader br=null;
		try {
			br = new BufferedReader(new InputStreamReader(in,"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
		}
    	String line = null;
    	
    	try {
			while((line=br.readLine())!=null && !"".equals(line))
			{
				
				log.debug("Msg from Client:"+line);
				String []tokens = line.split(" ");
				analyze(tokens);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
		}
    	log.debug("end");
    }
    private byte[] loadResourceFile(String url)
    {
    	byte[] data = null;
    	try {
			data = Files.readAllBytes(new File("./webapp"+url).toPath());
			log.debug(data.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return data;
    }
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());
        
        request_type = RequestType.ERROR;
        
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 
        	body_buffer = "There is nothing to show for you.".getBytes();
        	parseRequest(in);
        	process();
            response(out);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    private void response(OutputStream out)
    {
    	DataOutputStream dos = new DataOutputStream(out);
    	response200Header(dos, body_buffer.length);
    	responseBody(dos, body_buffer);
    }
    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
