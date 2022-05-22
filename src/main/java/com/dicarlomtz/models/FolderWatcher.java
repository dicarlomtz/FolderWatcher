package com.dicarlomtz.models;

import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.WatchKey;
import java.nio.file.WatchEvent;
import java.nio.file.FileSystems;
import java.nio.file.WatchService;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.nio.file.StandardWatchEventKinds;

public class FolderWatcher {

    private WatchService watchService;

    private Path path;

    private FileWorker fileWorker;

    public FolderWatcher(Path path, FileWorker fileWorker) throws IOException {
        this.watchService = FileSystems.getDefault().newWatchService();
        this.path = path;
        this.fileWorker = fileWorker;
        registerPath();
    }

    private void registerPath() throws IOException {
        getPath().register(getWatchService(), StandardWatchEventKinds.ENTRY_CREATE);
    }

    public void startWatcher()
            throws InterruptedException, IOException {

        Runnable r = new Runnable() {
            private WatchKey watchKey = null;

            public void run() {
                try {
                    while ((watchKey = watchService.take()) != null) {
                        for (WatchEvent<?> event : watchKey.pollEvents()) {
                            try {
                                getFileWorker().fileTaskWorker(event.context().toString(),
                                        getPath().toString());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        watchKey.reset();
                    }
                } catch (InterruptedException e) {
                    Logger.getLogger(FolderWatcher.class.getName()).log(Level.SEVERE, null, e);
                    e.printStackTrace();
                }
            }
        };

        new Thread(r).start();
    }

    private Path getPath() {
        return this.path;
    }

    private WatchService getWatchService() {
        return this.watchService;
    }

    public FileWorker getFileWorker() {
        return this.fileWorker;
    }

}