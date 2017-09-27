package dialog;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NonNls;

import javax.swing.*;

public abstract class PetraToolWindow implements Disposable
{
    @NonNls
    public static final String TOOL_WINDOW_ID ="PETrA";
    public static JTextArea TOOL_STATUS_AREA;
    public static String outputLocationPath1;

    public static PetraToolWindow getInstance(Project project,JTextArea statusArea, String outputLocationPath)
    {
        outputLocationPath1 = outputLocationPath;
        TOOL_STATUS_AREA = statusArea;
        return ServiceManager.getService(project,PetraToolWindow.class);
    }

    public abstract void show();
    public abstract void close();
}
