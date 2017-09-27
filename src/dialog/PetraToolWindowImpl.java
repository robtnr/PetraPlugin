package dialog;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public final class PetraToolWindowImpl extends PetraToolWindow {

    private final Project project;
    private final JPanel panel;
    private final PetraDisplay display;
    private ToolWindow myToolWindow = null;

    public PetraToolWindowImpl(@NotNull Project project) throws IOException {
        this.project = project;
        final DefaultActionGroup toolbarGroup = new DefaultActionGroup();
        toolbarGroup.add(new ExportCSVAction(new ImageIcon(getClass().getResource("/csv.png"))));
        display = new PetraDisplay(project,TOOL_STATUS_AREA,outputLocationPath1);
        List<ConsumptionData> averagedConsumptionsDataList = display.getAveragedConsumptionsDataList();
        toolbarGroup.add(new ExportXMLAction(new ImageIcon(getClass().getResource("/xml.png")), averagedConsumptionsDataList));
        toolbarGroup.add(new ClosePetraViewAction(this));
        final ActionManager actionManager = ActionManager.getInstance();
        final ActionToolbar toolbar  = actionManager.createActionToolbar(TOOL_WINDOW_ID, toolbarGroup,false);
        panel = new JPanel(new BorderLayout());
        panel.add(toolbar.getComponent(),BorderLayout.WEST);
        panel.add(display.getTabbedPane(),BorderLayout.CENTER);
        register();
    }

    public void register(){
        final ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        myToolWindow = toolWindowManager.registerToolWindow("PETrA",panel, ToolWindowAnchor.BOTTOM);
        myToolWindow.setTitle("Risultati");
        myToolWindow.setIcon(new ImageIcon(getClass().getResource("/graph.png")));
        myToolWindow.setAvailable(false,null);
    }

    @Override
    public void show() {
        myToolWindow.setAvailable(true,null);
        myToolWindow.show(null);
    }

    @Override
    public void close() {
        myToolWindow.hide(null);
        myToolWindow.setAvailable(false,null);
    }

    @Override
    public void dispose() {
        final ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        toolWindowManager.unregisterToolWindow(TOOL_WINDOW_ID);
    }
}
