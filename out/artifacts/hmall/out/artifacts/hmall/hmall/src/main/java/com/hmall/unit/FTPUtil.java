package com.hmall.unit;


import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class FTPUtil {

    private static final Logger logger= LoggerFactory.getLogger(FTPUtil.class);
    private static String ftpip=PropertieUitl.getProperty("ftp.server.ip");
    private static String ftpuser=PropertieUitl.getProperty("ftp.user");
    private static String ftppass=PropertieUitl.getProperty("ftp.pass");

    private String ip;
    private int port;
    private String user;
    private String pwd;
    private FTPClient ftpClient;

    public FTPUtil(String ip, int port, String user, String pwd) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.pwd = pwd;
        this.ftpClient = ftpClient;
    }
    public static boolean uploadfile(List<File> fileList) throws IOException {
        FTPUtil ftpUtil=new FTPUtil(ftpip,21,ftpuser,ftppass);
       logger.info("开始连接ftp服务器");
        boolean result=ftpUtil.uploadfile("img",fileList);
        logger.info("开始连接ftp服务器,结束上传，上传结果");
        return result;

    }

    private boolean uploadfile(String remotepath, List<File> fileList) throws IOException {
        boolean uploadfile=true;
        FileInputStream fis=null;
        //连接服务器
        if(conectServer(this.getIp(),this.port,this.user,this.pwd)){
            try {
                ftpClient.changeWorkingDirectory(remotepath);
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();//打开本地被动模式
                for(File fileItem:fileList){
                    fis=new FileInputStream(fileItem);
                    ftpClient.storeFile(fileItem.getName(),fis);//开始上传文件
                }
            } catch (IOException e) {
                logger.error("上传文件异常",e);
                e.printStackTrace();
            }finally {
                fis.close();
                ftpClient.disconnect();
            }
        }
        return uploadfile;
    }



    private boolean conectServer(String ip,int port,String user,String pwd){
        boolean isSuccess=false;
        ftpClient=new FTPClient();

        try {
            ftpClient.connect(ip);
            ftpClient.login(user,pwd);
        } catch (IOException e) {
            logger.error("连接服务器失败",e);
            e.printStackTrace();
        }
        return isSuccess;
    }
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
