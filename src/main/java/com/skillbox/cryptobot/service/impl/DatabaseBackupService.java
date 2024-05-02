package com.skillbox.cryptobot.service.impl;

import com.skillbox.cryptobot.service.BackupUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DatabaseBackupService {

    private final BackupUtility backupUtility;

    @Scheduled(fixedRate = 60000)
    public void backupDatabase() throws Exception {
        backupUtility.backupDatabase();
    }
}
