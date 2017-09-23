package dialog;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.PrintStream;

public class UIPetra extends DialogWrapper
{

    private JTextArea statusArea;
    private JPanel contentPane;
    private JTextField apkLocationField;
    private JLabel apkLocationLabel;
    private JButton apkLocationButton;
    private JSlider interactionsSlider;
    private JLabel interactionsLabel;
    private JLabel timeBetweenInteractionsLabel;
    private JSlider timeBetweenInteractionsSlider;
    private JTextField scriptLocationField;
    private JButton scriptLocationButton;
    private JLabel scriptLocationLabel;
    private JSlider runsSlider;
    private JLabel runsLabel;
    private JLabel powerProfileFileLabel;
    private JTextField powerProfileFileField;
    private JButton powerProfileFileButton;
    private JLabel scriptTimeField;
    private JSpinner scriptTimeSpinner;
    private JLabel interactionToolField;
    private JRadioButton monkeyRadioButton;
    private JRadioButton monkeyrunnerRadioButton;
    private MouseAdapter scriptLocationMouseAdapter;
    private String appName;
    private @NotNull Project project;

    public UIPetra(Project project)
    {
        super(null);
        init();
        this.project = project;
        setTitle("PETrA");
        setResizable(false);

        apkLocationButton.addActionListener(evt -> apkLocationButtonActionPerformed());
        apkLocationField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                apkLocationButtonActionPerformed();
            }
        });
        powerProfileFileButton.addActionListener(evt -> powerProfileFileButtonActionPerformed());
        powerProfileFileField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                powerProfileFileButtonActionPerformed();
            }
        });
        monkeyRadioButton.addActionListener(evt -> setMonkeyRadioButtonActionPerformed());
        monkeyrunnerRadioButton.addActionListener(evt -> setMonkeyrunnerRadioButtonActionPerformed());
        scriptLocationButton.addActionListener(evt -> scriptLocationButtonActionPerformed());
        scriptLocationMouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                scriptLocationButtonActionPerformed();
            }
        };

        setOKButtonIcon(new ImageIcon(getClass().getResource("/play-button.png")));
        setOKButtonText("Start Energy Estimation");
        setOKActionEnabled(false);
        statusArea = new JTextArea();
        PrintStream out = new PrintStream(new TextAreaOutputStream(statusArea));
        System.setOut(out);

    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {

        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(9, 5, new Insets(10, 10, 10, 10), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new GridConstraints(3, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        panel1.setBorder(BorderFactory.createTitledBorder("Monkeyrunner Options"));
        scriptLocationLabel = new JLabel();
        scriptLocationLabel.setText("Script Location");
        panel1.add(scriptLocationLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        scriptLocationField = new JTextField();
        scriptLocationField.setEditable(false);
        scriptLocationField.setToolTipText("The location of the Monkeyrunner script.");
        panel1.add(scriptLocationField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(400, -1), null, 0, false));
        scriptLocationButton = new JButton();
        scriptLocationButton.setEnabled(false);
        scriptLocationButton.setIcon(new ImageIcon(getClass().getResource("/folder.png")));
        scriptLocationButton.setText("Open");
        scriptLocationButton.setToolTipText("Select monkeyrunner script.");
        panel1.add(scriptLocationButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        scriptTimeField = new JLabel();
        scriptTimeField.setText("Time needed for executing the script");
        panel1.add(scriptTimeField, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        scriptTimeSpinner = new JSpinner();
        scriptTimeSpinner.setEnabled(false);
        panel1.add(scriptTimeSpinner, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        contentPane.add(spacer1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(-1, 5), null, 0, false));
        final Spacer spacer2 = new Spacer();
        contentPane.add(spacer2, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(-1, 5), null, 0, false));
        final Spacer spacer3 = new Spacer();
        contentPane.add(spacer3, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(-1, 5), null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel2, new GridConstraints(1, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        panel2.setBorder(BorderFactory.createTitledBorder("Monkey Options"));
        interactionsSlider = new JSlider();
        interactionsSlider.setEnabled(false);
        interactionsSlider.setMajorTickSpacing(500);
        interactionsSlider.setMaximum(5000);
        interactionsSlider.setMinimum(100);
        interactionsSlider.setMinorTickSpacing(100);
        interactionsSlider.setPaintLabels(true);
        interactionsSlider.setPaintTicks(true);
        interactionsSlider.setSnapToTicks(true);
        interactionsSlider.setToolTipText("Time between an Android Monkey event and the next one in ms.");
        interactionsSlider.setValue(100);
        panel2.add(interactionsSlider, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(400, -1), null, 0, false));
        interactionsLabel = new JLabel();
        interactionsLabel.setText("Interactions");
        panel2.add(interactionsLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        timeBetweenInteractionsLabel = new JLabel();
        timeBetweenInteractionsLabel.setText("Time Between Interactions");
        panel2.add(timeBetweenInteractionsLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        timeBetweenInteractionsSlider = new JSlider();
        timeBetweenInteractionsSlider.setEnabled(false);
        timeBetweenInteractionsSlider.setMajorTickSpacing(500);
        timeBetweenInteractionsSlider.setMaximum(5000);
        timeBetweenInteractionsSlider.setMinimum(100);
        timeBetweenInteractionsSlider.setMinorTickSpacing(100);
        timeBetweenInteractionsSlider.setPaintLabels(true);
        timeBetweenInteractionsSlider.setPaintTicks(true);
        timeBetweenInteractionsSlider.setSnapToTicks(true);
        timeBetweenInteractionsSlider.setToolTipText("Number of Android Monkey event to perform.");
        timeBetweenInteractionsSlider.setValue(100);
        panel2.add(timeBetweenInteractionsSlider, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(400, -1), null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(4, 7, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel3, new GridConstraints(0, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        powerProfileFileLabel = new JLabel();
        powerProfileFileLabel.setText("Power Profile File");
        panel3.add(powerProfileFileLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        powerProfileFileField = new JTextField();
        powerProfileFileField.setEditable(false);
        powerProfileFileField.setEnabled(true);
        powerProfileFileField.setToolTipText("Device power profile (see https://source.android.com/devices/tech/power/).");
        panel3.add(powerProfileFileField, new GridConstraints(1, 1, 1, 5, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        apkLocationField = new JTextField();
        apkLocationField.setEditable(false);
        apkLocationField.setToolTipText("Apk to analyze.");
        panel3.add(apkLocationField, new GridConstraints(0, 1, 1, 5, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(300, -1), null, 0, false));
        apkLocationButton = new JButton();
        apkLocationButton.setIcon(new ImageIcon(getClass().getResource("/folder.png")));
        apkLocationButton.setText("Open");
        apkLocationButton.setToolTipText("Select apk.");
        panel3.add(apkLocationButton, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        powerProfileFileButton = new JButton();
        powerProfileFileButton.setIcon(new ImageIcon(getClass().getResource("/folder.png")));
        powerProfileFileButton.setText("Open");
        powerProfileFileButton.setToolTipText("Select power profile file.");
        panel3.add(powerProfileFileButton, new GridConstraints(1, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        runsLabel = new JLabel();
        runsLabel.setText("Runs");
        panel3.add(runsLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        runsSlider = new JSlider();
        runsSlider.setMajorTickSpacing(5);
        runsSlider.setMaximum(30);
        runsSlider.setMinimum(1);
        runsSlider.setMinorTickSpacing(1);
        runsSlider.setPaintLabels(true);
        runsSlider.setPaintTicks(true);
        runsSlider.setSnapToTicks(true);
        runsSlider.setValue(1);
        panel3.add(runsSlider, new GridConstraints(2, 1, 1, 6, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        apkLocationLabel = new JLabel();
        apkLocationLabel.setText("Apk Location");
        panel3.add(apkLocationLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        interactionToolField = new JLabel();
        interactionToolField.setText("Tool for perform interactions");
        panel3.add(interactionToolField, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        monkeyRadioButton = new JRadioButton();
        monkeyRadioButton.setText("Monkey");
        monkeyRadioButton.setToolTipText("Select a tool.");
        panel3.add(monkeyRadioButton, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        monkeyrunnerRadioButton = new JRadioButton();
        monkeyrunnerRadioButton.setText("Monkeyrunner");
        monkeyrunnerRadioButton.setToolTipText("Select a tool.");
        panel3.add(monkeyrunnerRadioButton, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ButtonGroup buttonGroup;
        buttonGroup = new ButtonGroup();
        buttonGroup.add(monkeyRadioButton);
        buttonGroup.add(monkeyrunnerRadioButton);
        return contentPane;
    }


    @Override
    protected void doOKAction() {
        super.doOKAction();
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Title"){
            public void run(@NotNull ProgressIndicator progressIndicator) {

                // start your process

                // Set the progress bar percentage and text
                progressIndicator.setFraction(0.10);
                progressIndicator.setText("90% to finish");

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // 50% done
                progressIndicator.setFraction(0.50);
                progressIndicator.setText("50% to finish");

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // Finished
                Notifications.Bus.notify(new Notification("github", "Success", "Successfully created project ''" + "PEtrA" + "'' on github", NotificationType.INFORMATION));
                Notifications.Bus.notify(new Notification("github", "Error", "Error created project ''" + "PEtrA" + "'' on github", NotificationType.ERROR));

                progressIndicator.setFraction(1.0);
                progressIndicator.setText("finished");

                //Notifica nella status bar
                ApplicationManager.getApplication().invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        final PetraToolWindow toolWindow = PetraToolWindow.getInstance(project,statusArea);
                        toolWindow.show();
                    }
                });

            }});

            //ToolWindowManager.getInstance(project).notifyByBalloon(PetraToolWindow.TOOL_WINDOW_ID,MessageType.INFO, "okkei");

    }

    private void apkLocationButtonActionPerformed() {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Apk Files", "apk");
        chooser.setFileFilter(filter);
        int res = chooser.showOpenDialog(null);
        if (res == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            String filename = f.getAbsolutePath();
            apkLocationField.setText(filename);
        }
    }

    private void powerProfileFileButtonActionPerformed() {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("XML Files", "xml");
        chooser.setFileFilter(filter);
        int res = chooser.showOpenDialog(null);
        if (res == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            String filename = f.getAbsolutePath();
            powerProfileFileField.setText(filename);
        }
    }

    private void setMonkeyRadioButtonActionPerformed() {
        scriptLocationField.setText("");
        scriptTimeSpinner.setValue(0);
        interactionsSlider.setEnabled(true);
        timeBetweenInteractionsSlider.setEnabled(true);
        scriptLocationField.setEnabled(false);
        scriptLocationField.removeMouseListener(scriptLocationMouseAdapter);
        scriptLocationButton.setEnabled(false);
        scriptTimeSpinner.setEnabled(false);
        setOKActionEnabled(true);
    }

    private void setMonkeyrunnerRadioButtonActionPerformed() {
        interactionsSlider.setEnabled(false);
        timeBetweenInteractionsSlider.setEnabled(false);
        scriptLocationField.setEnabled(true);
        scriptLocationField.addMouseListener(scriptLocationMouseAdapter);
        scriptLocationButton.setEnabled(true);
        scriptTimeSpinner.setEnabled(true);
        setOKActionEnabled(true);
    }

    private void scriptLocationButtonActionPerformed() {
        JFileChooser chooser = new JFileChooser();
        int res = chooser.showOpenDialog(null);
        if (res == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            String filename = f.getAbsolutePath();
            scriptLocationField.setText(filename);
        }
    }
}
