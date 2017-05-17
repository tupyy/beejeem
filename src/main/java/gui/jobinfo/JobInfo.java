package gui.jobinfo;

import core.job.Job;
import core.parameters.Parameter;
import gui.MainController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.regex.Pattern;

/**
 * Created by tctupangiu on 17/05/2017.
 */
public class JobInfo {

    private static final Logger logger = LoggerFactory
            .getLogger(MainController.class);

    private Job job = null;
    private Pane rootPane = null;
    private LinkedBlockingDeque<Map<Integer,String>> outputQueue = new LinkedBlockingDeque<>();
    private Thread readQueueThread;
    private Thread watchServiceThread;
    private JobInfoController jobInfoController;

    public JobInfo(Job job) {
        this();
        this.job = job;

        try {
            if (job != null) {
                Parameter tempFolderParameter = job.getParameters().getParameter("temporaryFolder");

                //read initial content
                File tempFolder = new File(tempFolderParameter.getValue().toString());
                readDirectory(tempFolder);

                //start WatchService
                startWatchService(tempFolder.toPath());
            }
        }
        catch (IllegalArgumentException ex) {
            logger.error("Temporary folder doesn't exist for job {}", job.getID());
        }

    }

    public JobInfo() {

        try {
            FXMLLoader loader = new FXMLLoader(JobInfo.class.getClassLoader().getResource("views/jobinfo.fxml"));

            rootPane = loader.load();
            jobInfoController = loader.<JobInfoController>getController();

        } catch (IOException e) {
            logger.error("Jobinfo Error loading fxml file: {}",e.getMessage());
        }
    }

    /**
     * Get the root pane
     * @return null if the rootPane cannot be initialized
     */
    public Pane getRootPane() {
        return  rootPane;
    }

    public void close() {

        if (readQueueThread != null) {
            readQueueThread.interrupt();
        }

        if (watchServiceThread != null) {
            watchServiceThread.interrupt();
        }
    }

    private void startWatchService(Path folderPath) {
        try {
            WatchServiceRunnable watchServiceRunnable = new WatchServiceRunnable(folderPath, outputQueue);
            watchServiceThread = new Thread(watchServiceRunnable);
            watchServiceThread.start();

            readQueueThread = new Thread(() -> {
                try {
                    while (true) {
                        Map<Integer, String> entry = outputQueue.take();
                        for (Map.Entry<Integer, String> item : entry.entrySet()) {
                            jobInfoController.accept(item.getKey(), item.getValue());
                        }
                    }
                } catch (InterruptedException e) {
                    return;
                }
            });
        }
        catch(IOException ex) {
            logger.error("JobInfo: Cannot start WatchServiceRunnable Exception: {}",ex.getMessage());
        }
    }

    private void readDirectory(File folder) {

        if (folder.isDirectory() && folder.canRead()) {
            for (File file : folder.listFiles()) {
                int fileType = getFileType(file.getName());

                if (fileType > 0) {
                     CompletableFuture<Void> completableFuture = CompletableFuture.supplyAsync(new ReadFileTask(file.getAbsoluteFile()))
                            .thenAccept(fileContent -> {
                                jobInfoController.accept(fileType, fileContent);
                            });
                    completableFuture.exceptionally(th -> {
                        logger.error(th.getMessage());
                        return null;
                    });
                }
            }
        }
    }

    /**
     * Return the file type
     * @param filename
     * @return 1 code file
     *          2 batch file
     *          3 html file
     */
    private int getFileType(String filename) {

        Pattern batchPattern = Pattern.compile("o[0-9]+$");
        Pattern codePattern = Pattern.compile("py$");
        Pattern htmlPattern = Pattern.compile("html$");


        if (batchPattern.matcher(filename).find()) {
            //is the batch file
            return 2;
        }
        else if (codePattern.matcher(filename).find()) {
            return 1;
        }
        else if (htmlPattern.matcher(filename).find()) {
            return 3;
        }

        return 0;
    }

}
