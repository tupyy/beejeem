package stes.isami.bjm.components.jobinfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.regex.Pattern;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Created by tctupangiu on 17/05/2017.
 */
public class WatchServiceRunnable implements Runnable {

    private static final Logger logger = LoggerFactory
            .getLogger(WatchServiceRunnable.class);

    private static final int CODE_FILE = 1;
    private static final int BATCH_FILE = 2;
    private static final int HTML_FILE = 3;

    private final Path folderToWatch;
    private final LinkedBlockingDeque<Map<Integer, String>> outputQueue;
    private Pattern batchPattern = Pattern.compile("tse\\.o(\\d+)");
    private Pattern codePattern = Pattern.compile("py$");
    private Pattern htmlPattern = Pattern.compile("html$");

    private WatchService watchService;
    private WatchKey key;

    public WatchServiceRunnable(Path folder, LinkedBlockingDeque<Map<Integer,String>> outputQueue) throws IOException {
        this.folderToWatch = folder;
        this.outputQueue = outputQueue;

        registerDirectory(folder);
    }

    @Override
    public void run() {

        while (!Thread.currentThread().isInterrupted()) {
            // wait for key to be signalled
            WatchKey key = null;
            try {
                key = watchService.take();
            } catch (InterruptedException x) {
                try {
                    logger.info("Closing watch service");
                    watchService.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                @SuppressWarnings("rawtypes")
                WatchEvent.Kind kind = event.kind();

                // Context for directory entry event is the file name of entry
                @SuppressWarnings("unchecked")
                Path name = ((WatchEvent<Path>) event).context();
                Path child = folderToWatch.resolve(name);
                logger.info("Changed detected file: {}", child.toString());


                // if directory is created, and watching recursively, then register it and its sub-directories
                if (kind == ENTRY_CREATE || kind == ENTRY_MODIFY) {
                    if (Files.isRegularFile(child)) {
                        logger.info("Process file: {}", child.toString());
                        processFileContent(child.toFile());
                    }
                } else if (kind == ENTRY_DELETE) {
                    if (Files.isRegularFile(child)) {
                        Map<Integer, String> entry = new HashMap<Integer, String>();
                        entry.put(getFileType(child.toString()), "");
                        outputQueue.add(entry);
                    }

                }

                // reset key
                boolean valid = key.reset();
                if (!valid) {
                    logger.info("Watch key no longer valid");
                    break;
                } else {
                    logger.info("Key valid");
                }
            }
        }

        try {
            logger.info("Closing watch service");
            watchService.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }




    }

    private void registerDirectory(Path dir) throws IOException {
        watchService =  dir.getFileSystem().newWatchService();
        key = dir.register(watchService,ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
    }

    /**
     * Return the file type
     * @param filename
     * @return 1 code file
     *          2 batch file
     *          3 html file
     */
    private int getFileType(String filename) {
        if (batchPattern.matcher(filename).find()) {
            //is the batch file
                return BATCH_FILE;
        }
        else if (codePattern.matcher(filename).find()) {
            return CODE_FILE;
        }
        else if (htmlPattern.matcher(filename).find()) {
            return HTML_FILE;
        }

        return 0;
    }

    /**
     * Read the content of the file and put in the outputQueueu as {@code  Map<FileType,FileContent>}
     * @param file
     */
    private void processFileContent(File file) {

        int fileType = getFileType(file.getAbsolutePath());
        if (fileType != 0) {
            CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(new ReadFileTask(file));
            completableFuture.thenAccept(fileContent -> {
                Map<Integer,String> entry = new HashMap<Integer, String>();
                entry.put(fileType,fileContent);
                logger.info("File {} content size: {}",file.getName(),fileContent.length());
                outputQueue.add(entry);
            });
        }

    }

}
