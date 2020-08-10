package org.sharefiles.root.helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.Date;
import java.util.Objects;

import org.apache.commons.io.FileUtils;

import org.sharefiles.root.config.ShareFilesConfig;

import javax.annotation.PostConstruct;


@Service

public class CleanUpFoldersTasks {

    private static Logger logger = LoggerFactory.getLogger(CleanUpFoldersTasks.class);
    private static boolean isFinished = false;

    @Value("${upload.service.main.directory}")
    private String uploadDirectory;


    @Scheduled(cron = ShareFilesConfig.cronRunAtNight)
    public void cleanUpAnonymousFiles(){
        File[] files = new File(ShareFilesConfig.anonymousDirectory).listFiles();
        showAndDeleteFiles(files, 2);
    }

    @Scheduled(cron = ShareFilesConfig.cronRunAtNight)
    public void cleanUpRegisteredFilesAndFolder(){
        File[] files = new File(ShareFilesConfig.anonymousDirectory).listFiles();
        showAndDeleteFiles(files, 7);
    }
    
    @Scheduled(cron = ShareFilesConfig.cronRunAtNight)
    public void removeEmptyDirectory(){
        do{
            isFinished = true;
            deleteEmptyFolders(ShareFilesConfig.registeredUserDirectory);
        }while(!isFinished);
    }

    public static void showAndDeleteFiles(File[] files, int days) {
        for (File file : files){
            if (file.isDirectory()){
                showAndDeleteFiles(Objects.requireNonNull(file.listFiles()), days);
            } else{
                long diff = new Date().getTime()- file.lastModified();
                if (diff > days * 24 * 60 *60 *1000){
                    file.delete();
                }
            }
        }
    }

    public static void deleteEmptyFolders(String directoryLocation){
        File folder = new File(directoryLocation);
        File[] listofFiles = folder.listFiles();
        assert listofFiles != null;
        if(listofFiles.length==0){
            folder.delete();
            isFinished=false;
        }else{
            for (File file : listofFiles) {
                if (file.isDirectory()) {
                    deleteEmptyFolders(file.getAbsolutePath());
                }
            }
        }
    }


}
