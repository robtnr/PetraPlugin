package batch;

import core.Process;
import core.ProcessOutput;
import core.exceptions.ADBNotFoundException;
import core.exceptions.ApkNotFoundException;
import core.exceptions.NoDeviceFoundException;
import core.exceptions.NumberOfTrialsExceededException;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

class EMSEExperiment {

    public static void main(String[] args) throws IOException {

        try {
            Process process = new Process();

            ArrayList<String> appNames = new ArrayList<>();
            ArrayList<String> apkNames = new ArrayList<>();

            String apksLocation = "/home/dardin88/Desktop/energy_consumption_bad_smell/icse_experiment/apks/";

            String line;
            BufferedReader br = new BufferedReader(new FileReader("/home/dardin88/Desktop/energy_consumption_bad_smell/icse_experiment/app_list.csv"));
            while ((line = br.readLine()) != null) {
                appNames.add(line);
                apkNames.add(line + ".apk");
            }

            for (int appCounter = 0; appCounter < appNames.size(); appCounter++) {
                int trials = 0;
                String appName = appNames.get(appCounter);
                String outputLocation = "/home/dardin88/Desktop/energy_consumption_bad_smell/icse_experiment/member_ignoring_method_test_data/" + appName + "/";
                int maxRun = 0;
                int maxTrials = 0;
                int interactions = 0;
                int timeBetweenInteractions = 0;
                String powerProfilePath = "";

                try {
                    ConfigManager configManager = new ConfigManager("config.properties");
                    maxRun = configManager.getRuns();
                    maxTrials = configManager.getTrials();
                    interactions = configManager.getInteractions();
                    timeBetweenInteractions = configManager.getTimeBetweenInteractions();
                    powerProfilePath = configManager.getOutputLocation() + "/power_profile.xml";
                } catch (IOException ex) {
                    Logger.getLogger(EMSEExperiment.class.getName()).log(Level.SEVERE, null, ex);
                }

                File appDataFolder = new File(outputLocation);

                int timeCapturing = (interactions * timeBetweenInteractions) / 1000;

                appDataFolder.mkdirs();

                File seedsFile = new File(outputLocation + "seeds");
                BufferedWriter seedsWriter = new BufferedWriter(new FileWriter(seedsFile, true));

                File apkLocation = new File(apksLocation + apkNames.get(appCounter));
                if (apkLocation.exists()) {
                    process.installApp(apkNames.get(appCounter));
                } else {
                    throw new ApkNotFoundException();
                }

                for (int run = 1; run <= maxRun; run++) {
                    try {
                        if (trials == maxTrials) {
                            throw new NumberOfTrialsExceededException();
                        }
                        ProcessOutput output = process.playRun(run, appName, interactions,
                                timeBetweenInteractions, timeCapturing, "",
                                powerProfilePath, outputLocation, appName);
                        seedsWriter.append(String.valueOf(output.getSeed())).append("\n");
                        timeCapturing = output.getTimeCapturing();
                    } catch (InterruptedException | IOException ex) {
                        run--;
                        trials++;
                    }
                }
                process.uninstallApp(appName);
            }
        } catch (ApkNotFoundException | FileNotFoundException | ADBNotFoundException | NoDeviceFoundException | NumberOfTrialsExceededException e) {
            e.printStackTrace();
        }

    }
}
