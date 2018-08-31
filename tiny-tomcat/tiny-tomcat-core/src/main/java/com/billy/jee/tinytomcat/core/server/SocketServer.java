package com.billy.jee.tinytomcat.core.server;

import com.billy.jee.tinytomcat.core.Webapp;
import com.billy.jee.tinytomcat.core.server.processor.DefaultServletProcessor;
import com.billy.jee.tinytomcat.core.server.processor.ServletProcessor;
import com.billy.jee.tinytomcat.core.server.request.RawRequest;
import com.billy.jee.tinytomcat.core.server.request.RawResponse;
import com.billy.jee.tinytomcat.core.server.request.parser.DefaultRequestParser;
import com.billy.jee.tinytomcat.core.server.request.parser.RequestParser;
import com.billy.jee.tinytomcat.core.server.webapp.DefaultWebappParser;
import com.billy.jee.tinytomcat.core.server.webapp.FileSystemWebappLoader;
import com.billy.jee.tinytomcat.core.server.webapp.WebappLoader;
import com.billy.jee.tinytomcat.core.server.webapp.WebappParser;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * socket server
 *
 * @author liulei
 * @since 2016-08-07 18:20
 *
 */
public class SocketServer {
    private static final Logger logger = Logger.getLogger(SocketServer.class);
    private ServerSocket ss = null;
    private boolean published = false;
    private static final int PORT = 27016;
    private ExecutorService threadPool = Executors.newFixedThreadPool(20);
    private List<Webapp> webapps = new ArrayList<>();
    private WebappLoader webappLoader = new FileSystemWebappLoader();
    private WebappParser webappParser = new DefaultWebappParser();

    public List<Webapp> getWebapps() {
        return webapps;
    }

    /**
     * 启动服务
     *
     * @throws Exception e
     */
    public synchronized void publish() throws Exception {
        if (published) {
            logger.warn("service is starting, and can not be started again.");
            return ;
        }
        ss = new ServerSocket(PORT);
        logger.info("service is started at " + PORT + ", please access "+"127.0.0.1:27016/main"+" to visit.");
        published = true;

        loadWebapps();

        while (true) {
            Socket socket = ss.accept();

            socket.setSoTimeout(30000);
            doDeal(socket, this);
        }
    }

    private void loadWebapps() {
        loadWebapp();
    }

    private void loadWebapp() {
        String path = "D:\\code-git\\tiny-tomcat\\tiny-tomcat-core\\src\\main\\webapp";

        String webXmlPath = webappLoader.load(path);

        webapps.add(webappParser.parse(webXmlPath));
    }

    private void doDeal(Socket socket, SocketServer socketServer) {
        SocketWorker socketWorker = new SocketWorker(socket, socketServer);

        threadPool.submit(socketWorker);
    }

    public static void main(String[] args) {
        SocketServer ss = new SocketServer();
        try {
            ss.publish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

/**
 * 处理客户端请求的线程
 * @author liulei
 *
 */
class SocketWorker implements Runnable {
    private static final Logger LOG = Logger.getLogger(SocketWorker.class);
    private final Socket socket;
    private final SocketServer socketServer;
    private RequestParser requestParser = new DefaultRequestParser();

    public SocketWorker(Socket socket, SocketServer socketServer) {
        this.socket = socket;
        this.socketServer = socketServer;
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
//        InputStreamReader inputStreamReader =
//                new InputStreamReader(inputStream, "utf-8");
//        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            byte[] b = new byte[10240];
            int a = inputStream.read(b);
            if (a < 1) {
                return;
            }
            byte[] bFinal = new byte[a];
            System.arraycopy(b, 0, bFinal, 0, a);
            StringBuilder sb = new StringBuilder(a + 1);
            sb.append(new String(bFinal));
            socket.shutdownInput();

            InetAddress inetAddress = socket.getInetAddress();
            LOG.info("Request from " + inetAddress.getHostName() + "/" + inetAddress.getHostAddress());

            LOG.info("to parse request");
            RawRequest rawRequest = requestParser.parse(sb.toString());
            RawResponse rawResponse = new RawResponse();
            ServletProcessor processor = new DefaultServletProcessor(socketServer);
            processor.process(rawRequest, rawResponse);
            LOG.info("parsed request");

            LOG.info("response to client starts");
            String servletResponseString = rawResponse.getResponseString();
            String headerResponseString = "HTTP/1.1 200 OK\r\n"
                    + "Server: tiny-tomcat\r\n"
                    + "Content-Type: text/html\r\n"
                    + "Content-Length: " + servletResponseString.length() +"\r\n"
                    + "\r\n"
                    + servletResponseString;
            out.write(headerResponseString.getBytes());
            out.flush();
            LOG.info("response to client ends");
            out.close();

            // 如此就可以模拟http服务器了，哈哈
//            String data2Return = "<html><head><title>this is head</title></head><body><h2>hello, socket." + System.currentTimeMillis() + "</h2></body>";
//            LOG.info("response: " + data2Return);
//            out.write(data2Return.getBytes());
//            out.flush();
//            out.close();
            inputStream.close();

            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
