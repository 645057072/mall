package com.hmall.service.Impl;

import com.google.common.collect.Lists;
import com.hmall.service.IFileService;
import com.hmall.unit.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service("iFileService")
public class IFileServiceImpl implements IFileService {

    private Logger logger= LoggerFactory.getLogger(IFileService.class);
    public String upload(MultipartFile file,String path){
        String filename= file.getOriginalFilename();//获取文件名
        String fileExtensionname = filename.substring(filename.lastIndexOf(".")+1);
            //获取扩展名
       String uploadfilename= UUID.randomUUID().toString()+"."+fileExtensionname;
       logger.info("开始上传文件，上传文件名{},上传路径{},新文件名{}",filename,path,uploadfilename);

//       创建文件夹目录
        File fileDir=new File(path);
        if(!fileDir.exists()){//判断是否存在
            fileDir.setWritable(true);//赋予权限可写
            fileDir.mkdirs();
        }

        File targetFile=new File(path,uploadfilename);

        try {
            file.transferTo(targetFile);//上传文件成功
            FTPUtil.uploadfile(Lists.newArrayList(targetFile));
            //todo   将文件上传到FTP服务器
            targetFile.delete();
            //todo  删除upload文件夹下的文件
        } catch (IOException e) {
            logger.error("上传文件失败",e);
            return null;
        }
        return targetFile.getName();
    }

}
