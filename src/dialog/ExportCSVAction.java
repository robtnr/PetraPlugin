package dialog;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.extensions.PluginId;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ExportCSVAction extends AnAction {

    public ExportCSVAction(ImageIcon icon)
    {
        super("Export CSV", "Download del CSV",icon);
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        JFileChooser chooser = new JFileChooser();
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.addChoosableFileFilter(new FileFilter() {

            public String getDescription() {
                return "CSV Documents (*.csv)";
            }

            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                } else {
                    return f.getName().toLowerCase().endsWith(".csv");
                }
            }
        });
        chooser.showOpenDialog(null);
        if(chooser.getSelectedFile()!=null) {
            File selectedFile = chooser.getSelectedFile();
            String fileName = selectedFile.getAbsolutePath();
            final String extension = ".csv";
            if (!fileName.endsWith(extension)) {
                fileName += extension;
            }
            final PluginId pluginId = PluginId.getId("IdPetra");
            final IdeaPluginDescriptor pluginDescriptor = PluginManager.getPlugin(pluginId);
            File source = new File(pluginDescriptor.getPath().getAbsolutePath() + "/risorse/result.csv");
            File dest = new File(fileName);
            try {
                FileUtils.copyFile(source, dest);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Notifications.Bus.notify(new Notification("IDNotifyCSV", "Success", "CSV Downloaded Successfully.", NotificationType.INFORMATION));
        }
        else
        {

        }
    }
}
