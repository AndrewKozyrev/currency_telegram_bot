package com.skillbox.cryptobot.service.impl;

import com.skillbox.cryptobot.service.BackupUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@Slf4j
public class FileBackupUtility implements BackupUtility {

    private static final String CONTAINER_NAME = "pg_container";
    private static final String BACKUP_FOLDER = "backups/";

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Override
    public void backupDatabase() throws Exception {
        String fileName = generateBackupFileName();
        String fullPath = BACKUP_FOLDER + File.separator + fileName;
        String command = String.format("docker exec %s pg_dump -h localhost -U %s -p 5432 db > %s", CONTAINER_NAME, username, fullPath);
        ProcessBuilder builder = new ProcessBuilder("cmd", "/c", command);
        builder.environment().put("PGPASSWORD", password);
        Process process = builder.start();
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        int exitCode = process.waitFor();
        if (exitCode == 0) {
            log.debug("Database backup created successfully: " + fullPath);
        } else {
            log.error("Failed to create database backup! Exit code: " + exitCode);
            log.error("Error output:\n" + output);
        }
    }

    private String generateBackupFileName() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        return "db_backup_" + formatter.format(new Date()) + ".sql";
    }
}
