package com.dicarlomtz.models;

import java.util.Set;
import java.util.HashSet;
import java.io.IOException;

public abstract class FileWorker {

    private Set<String> extensionsAllowed;

    public FileWorker(Set<String> extensionsAllowed) {
        this.extensionsAllowed = extensionsAllowed;
    }

    public FileWorker() {
        this(new HashSet<String>());
    }

    public void addExtensionAllowed(String extension) {
        getExtensionsAllowed().add(extension);
    }

    public void removeExtensionAllowed(String extension) {
        getExtensionsAllowed().remove(extension);
    }

    public Set<String> getExtensionsAllowed() {
        return this.extensionsAllowed;
    }

    public boolean isExtensionAllowed(String fileName) {
        return getExtensionsAllowed().contains(fileName.substring(fileName.lastIndexOf(".") + 1));
    }

    abstract void fileTaskWorker(String fileName, String contextPath) throws IOException;

}
