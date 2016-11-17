package QcloudCOS.QcloudCOS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.request.DelFileRequest;
import com.qcloud.cos.request.DelFolderRequest;
import com.qcloud.cos.request.ListFolderRequest;

public class ConnectTest {
	static int appId = 0;
	static String secretId = "";
	static String secretKey = "";
	static String bucketName = "";
	public static void main(String[] args) throws IOException {
		Scanner scanner = new Scanner(System.in);
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		// 输入appId, secretId, secretKey
		System.out.println("请输入 appId 并以回车结束！");
		appId = scanner.nextInt();
		System.out.println("请输入 secretId 并以回车结束！");
		secretId = in.readLine();
		System.out.println("请输入 secretKey 并以回车结束！");
		secretKey = in.readLine();
		System.out.println("请输入 bucketName 并以回车结束！");
		bucketName = in.readLine();
		System.out.println("请输入 要删除的目录 例如  /test/test02/test03/");
		String next = in.readLine();
		String cosPath = next.replaceAll(" ", "");
		// 建立CosClient 连接对象
		COSClient cosClient = new COSClient(appId, secretId, secretKey);
		tra(cosClient, cosPath);
		// 关闭输入流
		scanner.close();
		// 断开cos连接
		cosClient.shutdown();
	}
	public static void tra(COSClient cosClient, String cosPath){
		ListFolderRequest listFolderRequest = new ListFolderRequest(bucketName, cosPath);
		// 读取目录
		String listFolder = cosClient.listFolder(listFolderRequest);
		JsonObject jsonObject = new JsonParser().parse(listFolder).getAsJsonObject();
		JsonObject dataJson = jsonObject.getAsJsonObject("data");
		JsonArray jsonArray = dataJson.getAsJsonArray("infos");
		if (jsonArray.size() == 0) return;
		for(int i = 0; i < jsonArray.size(); i++){
			JsonObject dir = (JsonObject) jsonArray.get(i);
			String asString = dir.get("name").getAsString();
			String subcosPath = cosPath + asString + "/";			
			if(dir.get("sha")==null)
				tra(cosClient, subcosPath);
			DelFileRequest delFileRequest = new DelFileRequest(bucketName, cosPath+asString);
			String delFile = cosClient.delFile(delFileRequest);
			JsonObject asJsonObject = new JsonParser().parse(delFile).getAsJsonObject();
			JsonElement jsonElement = asJsonObject.get("code");
			int asInt = jsonElement.getAsInt();
			String log;
			if(asInt == 0){
			}else if (asInt == -166) {
				DelFolderRequest folderRequest = new DelFolderRequest(bucketName, subcosPath);
				delFile = cosClient.delFolder(folderRequest);
				System.out.println(delFile);
			}
			JsonObject asJsonObject2 = new JsonParser().parse(delFile).getAsJsonObject();
			String asString2 = asJsonObject2.get("message").getAsString();
			log = "操作：删除" + subcosPath+" \t"+asString2;
			System.out.println(log);
		}
		DelFolderRequest folderRequest = new DelFolderRequest(bucketName, cosPath);
		String delFile = cosClient.delFolder(folderRequest);
		JsonObject asJsonObject2 = new JsonParser().parse(delFile).getAsJsonObject();
		String asString2 = asJsonObject2.get("message").getAsString();
		String log = "操作：删除" + cosPath+" \t"+asString2;
		System.out.println(log);
	}
}
