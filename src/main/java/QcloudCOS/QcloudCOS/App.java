package QcloudCOS.QcloudCOS;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.request.UploadFileRequest;

public class App 
{
    public static void main( String[] args )
    {
    	// 创建 COSClient实例
    	String bucketName = "<bucketName>";
    	int appId = 0;
		String secretId = "<secretId>";
		String secretKey = "<secretKey>";
		COSClient cosClient = new COSClient(appId, secretId, secretKey);
		
		// 要访问的URL
		String path = "https://www.baidu.com/img/bd_logo1.png";
		// 获取文件后缀
		String suffix = path.substring(path.lastIndexOf('.')+1);
		long currentTimeMillis = System.currentTimeMillis();
		// 已时间戳的形式命名, 保证文件不重复
		String localPath = "d://"+currentTimeMillis+"."+suffix;
		// 存储到COS 的路径
		String cosPath = "/haha.png";
		// 将文件存储到本地
		try {
			URL url = new URL(path);
			URLConnection urlConnection = url.openConnection();
			InputStream inputStream = urlConnection.getInputStream();
			File outFile = new File(localPath);
			OutputStream outputStream = new FileOutputStream(outFile);
			byte[] buf = new byte[1024];
			while (true) {
                int read = 0;
                if (inputStream != null) {
                    read = inputStream.read(buf);
                }
                if (read == -1) {
                    break;
                }
                outputStream.write(buf, 0, read);
            }
			outputStream.flush();
			outputStream.close();
			inputStream.close();
			// 文件上传到腾讯COS
			UploadFileRequest uploadFileRequest = new UploadFileRequest(bucketName, cosPath, localPath);
			cosClient.uploadFile(uploadFileRequest);
			// 判断本地文件是否存在, 如果存在则删除
			if(outFile.exists()){
				outFile.delete();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 关闭COS连接
		cosClient.shutdown();
    }
}
