package me.miki.shindo.management.remote.download.file;

import java.io.File;

public class DownloadZipFile extends DownloadFile {

    private final long unzippedSize;

    public DownloadZipFile(String url, String fileName, File outputDir, long size, long unzippedSize) {
        super(url, fileName, outputDir, size);
        this.unzippedSize = unzippedSize;
    }

    public long getUnzippedSize() {
        return unzippedSize;
    }
}
