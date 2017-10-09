package dialog;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class PetraAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        final DataContext dataContext = e.getDataContext();
        final Project project = CommonDataKeys.PROJECT.getData(dataContext);

        final PluginId pluginId = PluginId.getId("IdPetra");
        final IdeaPluginDescriptor pluginDescriptor = PluginManager.getPlugin(pluginId);
        //Create folder "resources"
        if(!new File(pluginDescriptor.getPath().getAbsolutePath()+"/resources").exists())
        {
            new File(pluginDescriptor.getPath().getAbsolutePath() + "/resources").mkdirs();
            File dest_result = new File(pluginDescriptor.getPath().getAbsolutePath() + "/resources/result.csv");
            File apktool_result = new File(pluginDescriptor.getPath().getAbsolutePath() + "/resources/apktool_2.2.2.jar");
            File monkey_playback_result = new File(pluginDescriptor.getPath().getAbsolutePath() + "/resources/monkey_playback.py");
            try {
                FileUtils.copyURLToFile(getClass().getResource("/result.csv"),dest_result);
                FileUtils.copyURLToFile(getClass().getResource("/apktool_2.2.2.jar"),apktool_result);
                FileUtils.copyURLToFile(getClass().getResource("/monkey_playback.py"),monkey_playback_result);
            } catch (IOException e1) {
                e1.printStackTrace();
            }



        }
        new UIPetra(project).show();
    }
}
