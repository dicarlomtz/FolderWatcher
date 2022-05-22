package com.dicarlomtz.controllers;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import com.dicarlomtz.models.ExcelFileWorker;
import com.dicarlomtz.models.FileWorker;
import com.dicarlomtz.models.FolderWatcher;

public class WatcherController {

    private static WatcherController controller;
    private HashMap<String, FolderWatcher> watchers;

    private WatcherController() {
        watchers = new HashMap<String, FolderWatcher>();
    }

    public static WatcherController getController() {
        if (controller == null) {
            controller = new WatcherController();
        }
        return controller;
    }

    private void startWatcher(Path contextPath, FileWorker fileWorker) throws IOException, InterruptedException {
        FolderWatcher watcher = new FolderWatcher(contextPath, fileWorker);
        watcher.startWatcher();
        watchers.put(contextPath.toString(), watcher);
    }

    public void createExcelFolderWatcher(String contextPath) throws IOException, InterruptedException {
        Path path = Paths.get(contextPath);
        FileWorker fileWorker = new ExcelFileWorker(path.toString());
        startWatcher(path, fileWorker);
    }

    public int getSize() {
        return watchers.size();
    }

}
