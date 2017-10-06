package dialog;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
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
        //Creazione cartella "risorse"
        if(!new File(pluginDescriptor.getPath().getAbsolutePath()+"/risorse").exists())
        {
            new File(pluginDescriptor.getPath().getAbsolutePath() + "/risorse").mkdirs();
            File result = new File(getClass().getClassLoader().getResource("/result.csv").getFile());
            System.out.println(result.getAbsolutePath());
            File dest = new File(pluginDescriptor.getPath().getAbsolutePath() + "/risorse/result.csv");
            try {
                FileUtils.copyFile(result, dest);
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }
        new UIPetra(project).show();
    }
}
