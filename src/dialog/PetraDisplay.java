package dialog;


import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.ui.ScrollPaneFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class PetraDisplay {

    private final Map<PetraCategory, JTable> tables = new EnumMap<PetraCategory, JTable>(PetraCategory.class);
    private final JTabbedPane tabbedPane = new JTabbedPane();
    private final Project project;

    public PetraDisplay(@NotNull Project project) {
        JTable resultTable = getTable();
        this.project = project;
        tables.put(PetraCategory.Result,resultTable);
        JTable logTable = new JTable();
        tables.put(PetraCategory.Log,logTable);

        tabbedPane.add("Result PETrA", ScrollPaneFactory.createScrollPane(resultTable));
        tabbedPane.add("Log PETrA", ScrollPaneFactory.createScrollPane(logTable));
    }

    public JTabbedPane getTabbedPane()
    {
        return tabbedPane;
    }

    public JTable getTable(){
        JTable table = new JTable();
        JTableHeader header = table.getTableHeader();
        header.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.setAlignmentY(Component.CENTER_ALIGNMENT);

        header.setFont(new Font("Times",Font.BOLD,12));
        DefaultTableModel model = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Signature", "Joule", "Seconds"}
        );

        DecimalFormat df = new DecimalFormat("0.0000000");
        ArrayList<ConsumptionData> averagedConsumptionsDataList=new ArrayList<ConsumptionData>();
        ConsumptionData consumptionData = new ConsumptionData("com.int",10,12);
        averagedConsumptionsDataList.add(consumptionData);
        averagedConsumptionsDataList.add(new ConsumptionData("dialog.UIPetra",133,155 ));
        averagedConsumptionsDataList.add(new ConsumptionData("dialog.ClosePetraViewAction",133,155 ));
        averagedConsumptionsDataList.add(new ConsumptionData("dialog.ClosePetraViewAction.dialog.dialog",133,155 ));
        averagedConsumptionsDataList.add(new ConsumptionData("dialog.ClosePetraViewAction.dialog.dialogdialog.ClosePetraViewAction.dialog.dialog",133,155 ));
        averagedConsumptionsDataList.add(new ConsumptionData("dialog",133,155 ));
        averagedConsumptionsDataList.add(new ConsumptionData("dialog.ClosePetraViewAction.dialog.",133,155 ));
        averagedConsumptionsDataList.add(new ConsumptionData("dialog/ProvaPetra",133,155 ));
        averagedConsumptionsDataList.add(new ConsumptionData("com",133,155 ));
        averagedConsumptionsDataList.add(new ConsumptionData("com",133,155 ));

        for (ConsumptionData averagedConsumptionData : averagedConsumptionsDataList) {
            model.addRow(new Object[]{averagedConsumptionData.getSignature(), df.format(averagedConsumptionData.getJoule()), df.format(averagedConsumptionData.getSeconds())});
        }

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(1, SortOrder.DESCENDING));
        sorter.setSortKeys(sortKeys);
        sorter.sort();

        table.setCellSelectionEnabled(true);
        table.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(table.getSelectedColumn()==0)
                {
                    String name_class = table.getValueAt(table.getSelectedRow(),table.getSelectedColumn()).toString();
                    PsiManager psiManager = PsiManager.getInstance(project);
                    String class_to_open = psiManager.findDirectory(project.getBaseDir()).getVirtualFile().getPath()+"/src/"+name_class+".java";
                    VirtualFile file_class = LocalFileSystem.getInstance().findFileByPath(class_to_open);
                    FileEditorManager.getInstance(project).openFile(file_class,true,true);
                    if(FileEditorManager.getInstance(project).isFileOpen(file_class))
                        System.out.println("ok");
                    //FileEditorManager.getInstance(project).setSelectedEditor(file_class,"public void prova()");
                }

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });



        table.setModel(model);

        //Adapt columns to content
        for (int column = 0; column < 1; column++)
        {
            TableColumn tableColumn = table.getColumnModel().getColumn(column);
            int preferredWidth = tableColumn.getMinWidth();
            int maxWidth = tableColumn.getMaxWidth();

            for (int row = 0; row < table.getRowCount(); row++)
            {
                TableCellRenderer cellRenderer = table.getCellRenderer(row, column);
                Component c = table.prepareRenderer(cellRenderer, row, column);
                int width = c.getPreferredSize().width + table.getIntercellSpacing().width;
                preferredWidth = Math.max(preferredWidth, width);

                if (preferredWidth >= maxWidth)
                {
                    preferredWidth = maxWidth;
                    break;
                }
            }

            tableColumn.setPreferredWidth( preferredWidth );
        }

        table.setRowSorter(sorter);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setRowHeight(22);
        return table;
    }
}
