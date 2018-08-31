package test.billy.jee.socket.client;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * socket 客户端
 * 
 * @author liulei
 * @since 2016-08-07 19:20
 *
 */
public class SocketClient {

    private static final Logger logger = Logger.getLogger(SocketClient.class);
    
    public String send2Server(String s) throws Exception {
        String ip = "127.0.0.1";
        int port = 27015;
        Socket socket = new Socket(ip, port);
        socket.setSoTimeout(15000);
        
        OutputStream outputStream = socket.getOutputStream();  
        logger.info("send: " + s );
        outputStream.write(s.getBytes("utf-8"));
        outputStream.flush(); 

        socket.shutdownOutput();
        InputStream inputStream = socket.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        
        StringBuffer sb = new StringBuffer(1024);
        String readLine = "";
        while (readLine != null) {
            sb.append(readLine);
            readLine = bufferedReader.readLine();
        }
        
        logger.info("response from server"
                + ": " + sb.toString());
        
        bufferedReader.close();
        inputStreamReader.close();
        inputStream.close();
        outputStream.close();
        socket.close();
        
        return null;
    }
    
    public static void main(String[] args) throws Exception {
        String s = "hello, server." + System.currentTimeMillis();
        new SocketClient().send2Server(s);
    }
}
