package test.billy.jee.uploader;

import com.billy.jee.tinytomcat.core.util.ByteUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author liulei@bshf360.com
 * @since 2018-04-25 14:35
 */
public class UploaderClient {
    private static final int PORT = 4000;
    private static final String CHARSET = "utf-8";

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", PORT);
        OutputStream os = socket.getOutputStream();//字节输出流
        os.write(1); // flag
        String fileNameStr = "aaaaaaaa.png";
        byte[] fileNameByteArr = fileNameStr.getBytes(CHARSET);

        os.write(ByteUtil.intToByteArray(fileNameByteArr.length)); // filename length
        os.write(fileNameByteArr); // filename
        String filePathIn = "z:/upload/verifycode.png";
        int SIZE = 300;
        InputStream inputStream = new FileInputStream(filePathIn);

        byte[] buffer = new byte[SIZE];
        int readSize = 0;

        while (readSize != -1) {
            readSize = inputStream.read(buffer);
            os.write(buffer);
            os.flush();
        }

        inputStream.close();

//        socket.shutdownOutput();
        os.flush();
        os.close();
        socket.close();
    }
}
