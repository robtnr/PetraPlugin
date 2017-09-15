package dialog;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NonNls;

public abstract class PetraToolWindow implements Disposable
{
    @NonNls
    public static final String TOOL_WINDOW_ID ="PETrA";

    public static PetraToolWindow getInstance(Project project)
    {
        return ServiceManager.getService(project,PetraToolWindow.class);
    }

    public abstract void show();
    public abstract void close();
}
