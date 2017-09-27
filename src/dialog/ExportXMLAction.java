package dialog;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NonNls;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class ExportXMLAction extends AnAction {

    private List<ConsumptionData> averagedConsumptionsDataList;

    public ExportXMLAction(ImageIcon icon, List<ConsumptionData> averagedConsumptionsDataList)
    {
        super("Export XML", "Download del file XML",icon);
        this.averagedConsumptionsDataList = averagedConsumptionsDataList;
    }


    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        JFileChooser chooser = new JFileChooser();
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.addChoosableFileFilter(new FileFilter() {

            public String getDescription() {
                return "XML Documents (*.xml)";
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
        File selectedFile = chooser.getSelectedFile();
        String fileName = selectedFile.getAbsolutePath();
        final String extension = ".xml";
        if (!fileName.endsWith(extension)) {
            fileName += extension;
        }
        File file = new File(fileName);
        GregorianCalendar date = new GregorianCalendar();
        String data = date.get(Calendar.DAY_OF_MONTH)+"/"+date.get(Calendar.MONTH)+"/"+date.get(Calendar.YEAR)+" "+date.get(Calendar.HOUR)+":"+date.get(Calendar.MINUTE)+":"+date.get(Calendar.SECOND);
        @NonNls final PrintWriter writer;
        try {
            writer = new PrintWriter(new FileOutputStream(fileName));
            writer.println("<PETRA date='"+data+"'>");
            for (int i=0;i<averagedConsumptionsDataList.size();i++)
            {
                writer.println("\t<SIGNATURE name='"+averagedConsumptionsDataList.get(i).getSignature()+"'>");
                writer.println("\t\t<JOULE>"+averagedConsumptionsDataList.get(i).getJoule()+"</JOULE>");
                writer.println("\t\t<SECONDS>"+averagedConsumptionsDataList.get(i).getSeconds()+"</SECONDS>");
                writer.println("\t</SIGNATURE>");
            }
            writer.println("</PETRA>");
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
