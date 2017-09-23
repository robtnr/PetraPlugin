package core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dario Di Nucci
 */
class SysTraceRunner implements Runnable {

    private final int timeCapturing;
    private final String systraceFilename;
    private final String platformToolsFolder;

    SysTraceRunner(int timeCapturing, String systraceFilename, String platformToolsFolder) {
        this.timeCapturing = timeCapturing;
        this.systraceFilename = systraceFilename;
        this.platformToolsFolder = platformToolsFolder;
    }

    private static void executeCommand(String command) {
        try {
            List<String> listCommands = new ArrayList<>();

            String[] arrayExplodedCommands = command.split(" ");
            listCommands.addAll(Arrays.asList(arrayExplodedCommands));
            ProcessBuilder pb = new ProcessBuilder(listCommands);
            pb.inheritIO();
            java.lang.Process commandProcess = pb.start();
            commandProcess.waitFor();
            Thread.sleep(3000);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(SysTraceRunner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        String command = "python " + platformToolsFolder + "/systrace/systrace.py --time=" + this.timeCapturing + " freq idle -o " + this.systraceFilename;

        SysTraceRunner.executeCommand(command);
    }
}
