package test.billy.jee.uploader;

import com.billy.jee.tinytomcat.core.util.ByteUtil;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author liulei@bshf360.com
 * @since 2018-04-25 14:35
 */
public class UploaderServer {
    private ServerSocket ss = null;
    private static final int PORT = 4000;
    private static final String CHARSET = "utf-8";
    private static String basePath = "z:/";

    public void publish() throws IOException {
        ss = new ServerSocket(PORT);

        while (true) {
            Socket socket = ss.accept();

            socket.setSoTimeout(30000);
            doHandle(socket, this);
        }
    }

    /**
     *
     * flag: 1 byte
     * filenameLength: 4 bytes
     * filenameBytes: N bytes
     *
     * @param socket socket
     * @param uploaderServer uploadServer
     * @throws IOException ex
     */
    private void doHandle(Socket socket, UploaderServer uploaderServer) throws IOException {
        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();

        int flag = inputStream.read();

        byte[] filenameLengthArr = new byte[4];
        inputStream.read(filenameLengthArr);

        int filenameLength = ByteUtil.byteArrayToInt(filenameLengthArr);
        byte[] filenameBytesArr = new byte[filenameLength];
        inputStream.read(filenameBytesArr);

        String filenameUtf8 = new String(filenameBytesArr, CHARSET);
        System.out.println(filenameUtf8);

        int SIZE = 300;
        byte[] buffer = new byte[SIZE];
        int readSize = 0;

        FileOutputStream fileOutputStream = new FileOutputStream(basePath + filenameUtf8);
        System.out.println("file ["+basePath + filenameUtf8+"] was created.");
        while (readSize != -1) {
            readSize = inputStream.read(buffer);
            fileOutputStream.write(buffer);
            fileOutputStream.flush();
        }
        fileOutputStream.flush();
        fileOutputStream.close();

        outputStream.close();
        inputStream.close();
    }

    /**
     * 启动服务
     * @param args args
     */
    public static void main(String[] args) {
        UploaderServer uploaderServer = new UploaderServer();
        try {
            uploaderServer.publish();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
