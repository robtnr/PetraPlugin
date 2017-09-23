package dialog;

import core.Process;
import core.ProcessOutput;
import core.exceptions.ADBNotFoundException;
import core.exceptions.AppNameCannotBeExtractedException;
import core.exceptions.NoDeviceFoundException;
import core.exceptions.NumberOfTrialsExceededException;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Task extends SwingWorker<Void, Void> {

    private final String apkLocationPath;
    private final int interactions;
    private final int timeBetweenInteractions;
    private final String scriptLocationPath;
    private final int scriptTime;
    private final int runs;
    private String appName;
    private String powerProfilePath;


    Task(String apkLocationPath, int interactions, int timeBetweenInteractions, String scriptLocationPath,
         int scriptTime, int runs, String powerProfilePath) {
        this.apkLocationPath = apkLocationPath;
        this.interactions = interactions;
        this.timeBetweenInteractions = timeBetweenInteractions;
        this.scriptTime = scriptTime;
        this.scriptLocationPath = scriptLocationPath;
        this.runs = runs;
        this.powerProfilePath = powerProfilePath;
    }

    @Override
    public Void doInBackground() throws AppNameCannotBeExtractedException, NoDeviceFoundException, ADBNotFoundException {
        try {
            Process process = new Process();
            setProgress(0);
            int progress;
            int trials = 0;
            BufferedWriter seedsWriter = null;

            appName = process.extractAppName(apkLocationPath);

            String outputLocationPath = new File(apkLocationPath).getParent() + File.separator + "test_data" + File.separator + appName;

            File appDataFolder = new File(outputLocationPath);

            appDataFolder.mkdirs();

            if (this.scriptLocationPath.isEmpty()) {
                File seedsFile = new File(outputLocationPath + File.separator + "seeds");
                seedsWriter = new BufferedWriter(new FileWriter(seedsFile, true));
            }
            process.installApp(apkLocationPath);

            if (this.powerProfilePath.isEmpty()) {
                process.extractPowerProfile(outputLocationPath);
                this.powerProfilePath = outputLocationPath + "/power_profile.xml";
            }

            int timeCapturing = (interactions * timeBetweenInteractions) / 1000;

            if (!scriptLocationPath.isEmpty()) {
                timeCapturing = scriptTime;
            }

            for (int run = 1; run <= runs; run++) {
                if (trials == 10) {
                    throw new NumberOfTrialsExceededException();
                }
                try {
                    ProcessOutput output = process.playRun(run, appName, interactions, timeBetweenInteractions, timeCapturing,
                            scriptLocationPath, powerProfilePath, outputLocationPath, appName);
                    if (seedsWriter != null) {
                        seedsWriter.append(String.valueOf(output.getSeed())).append("\n");
                    }
                    timeCapturing = output.getTimeCapturing();
                    progress = (100 * run / runs);
                    setProgress(progress);
                } catch (InterruptedException | IOException ex) {
                    run--;
                    trials++;
                }
            }
            process.uninstallApp(appName);
        } catch (IOException | NumberOfTrialsExceededException ex) {

        }
        return null;
    }
}
