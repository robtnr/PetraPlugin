package dialog;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class ClosePetraViewAction extends AnAction {

    private final PetraToolWindow toolWindow;

    ClosePetraViewAction(PetraToolWindowImpl petraToolWindow) {
       super("Close", "Close Petra", AllIcons.Actions.Cancel);
       this.toolWindow = petraToolWindow;
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
     toolWindow.close();
    }
}
