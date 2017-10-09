package dialog;


import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.jetbrains.annotations.NotNull;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.BoxAndWhiskerToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PetraDisplay {

    private final Map<PetraCategory, JTable> tables = new EnumMap<PetraCategory, JTable>(PetraCategory.class);
    private final JTabbedPane tabbedPane = new JTabbedPane();
    private final Project project;
    private List<ConsumptionData> filteredConsumptionsDataList;
    private List<ConsumptionData> averagedConsumptionsDataList;
    private JTable consumptionTable;
    private JPanel tab2;
    private JPanel panel_first;

    private String csvFile;
    private String outputLocationPath;
    private JTextField search = new JTextField();
    private JButton button_search;
    private JTextArea statusArea;
    private JButton clean;

    public PetraDisplay(@NotNull Project project, JTextArea statusArea, String outputLocationPath) throws IOException {
        this.statusArea = statusArea;
        this.outputLocationPath = outputLocationPath;

        getTable();

        panel_first = new JPanel(new GridLayoutManager(3,4));
        button_search = new JButton("Filter Results", new ImageIcon(getClass().getResource("/filter.png")));
        button_search.setToolTipText("Filter Results.");
        button_search.addActionListener(evt -> filterResultsActionPerformed());
        clean = new JButton("Clean Results", new ImageIcon(getClass().getResource("/clean.png")));
        clean.setToolTipText("Clean.");
        clean.addActionListener(evt -> cleanResultsActionPerformed());
        panel_first.add(search, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        panel_first.add(button_search, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel_first.add(clean, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        search.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    filterResultsActionPerformed();
                }
            }
        });
        this.project = project;
        //tables.put(PetraCategory.Result,consumptionTable);

        panel_first.add(consumptionTable.getTableHeader(), new GridConstraints(1, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel_first.add(consumptionTable, new GridConstraints(2, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        tabbedPane.add("Result PETrA", ScrollPaneFactory.createScrollPane(panel_first));
        tabbedPane.add("BoxPlot PETrA", ScrollPaneFactory.createScrollPane(tab2));
        tabbedPane.add("Log PETrA", ScrollPaneFactory.createScrollPane(this.statusArea));
    }

    public JTabbedPane getTabbedPane()
    {
        return tabbedPane;
    }

    public void getTable() throws IOException {
        final PluginId pluginId = PluginId.getId("IdPetra");
        final IdeaPluginDescriptor pluginDescriptor = PluginManager.getPlugin(pluginId);
        this.csvFile = pluginDescriptor.getPath().getAbsolutePath() + "/resources/result.csv";//this.mergeRunResults(outputLocationPath);
        this.updateTable("");
        this.updateBoxplot("");
        /*

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

        */
    }

    //Da definire
    private String mergeRunResults(String inputLocationPath) throws IOException {
        Collection<File> runResults = FileUtils.listFiles(new File(inputLocationPath), new NameFileFilter("result.csv"), TrueFileFilter.INSTANCE);
        File outputFile = new File(inputLocationPath + File.separator + "allRuns.csv");
        FileUtils.deleteQuietly(outputFile);
        try (PrintWriter pw = new PrintWriter(outputFile)) {
            int run = 0;
            for (File runResult : runResults) {
                boolean isHeader = true;
                LineIterator it = FileUtils.lineIterator(runResult, "UTF-8");
                while (it.hasNext()) {
                    String line = it.nextLine();
                    if (!isHeader | run == 0) {
                        pw.write(line + "\n");
                    }
                    isHeader = false;
                }
                run++;
            }
        }
        return outputFile.getAbsolutePath();
    }

    private void updateTable(String nameFilter) throws IOException {

        this.filterData(nameFilter);
        this.calculateAverages();

        consumptionTable = new JTable();
        DefaultTableModel model = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Signature", "Joule", "Seconds"}
        );
        consumptionTable.setDefaultEditor(Object.class,null);

        DecimalFormat df = new DecimalFormat("0.0000000");

        for (ConsumptionData averagedConsumptionData : averagedConsumptionsDataList) {
            model.addRow(new Object[]{averagedConsumptionData.getSignature(), df.format(averagedConsumptionData.getJoule()), df.format(averagedConsumptionData.getSeconds())});
        }

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(1, SortOrder.DESCENDING));
        sorter.setSortKeys(sortKeys);
        sorter.sort();

        //Click cell table
        consumptionTable.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(consumptionTable.getSelectedColumn()==0)
                {
                   /* String name_class = consumptionTable.getValueAt(consumptionTable.getSelectedRow(),consumptionTable.getSelectedColumn()).toString();
                    PsiManager psiManager = PsiManager.getInstance(project);
                    String class_to_open = psiManager.findDirectory(project.getBaseDir()).getVirtualFile().getPath()+"/src/"+name_class+".java";
                    VirtualFile file_class = LocalFileSystem.getInstance().findFileByPath(class_to_open);
                    FileEditorManager.getInstance(project).openFile(file_class,true,true);
                    if(FileEditorManager.getInstance(project).isFileOpen(file_class))
                        System.out.println("ok");
                    //FileEditorManager.getInstance(project).setSelectedEditor(file_class,"public void prova()");
                    */
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

        consumptionTable.setModel(model);
        consumptionTable.setRowSorter(sorter);
    }

    private void filterData(String nameFilter) throws IOException {
        filteredConsumptionsDataList = new ArrayList<>();

        Reader in = new FileReader(csvFile);
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);

        Pattern pattern = Pattern.compile("^\\S*");

        for (CSVRecord record : records) {
            String signature = record.get("signature").replaceAll("^(\\.*)", "");
            double joule = Double.parseDouble(record.get(" joule"));
            double seconds = Double.parseDouble(record.get(" seconds"));

            if (signature.contains(nameFilter) || nameFilter.equals("")) {
                Matcher matcher = pattern.matcher(signature);
                if (matcher.find()) {
                    ConsumptionData consumptionData = new ConsumptionData(matcher.group(), joule, seconds);
                    filteredConsumptionsDataList.add(consumptionData);
                }
            }
        }

    }

    private void calculateAverages() {

        averagedConsumptionsDataList = new ArrayList<>();

        for (ConsumptionData consumptionData : filteredConsumptionsDataList) {
            String signature = consumptionData.getSignature();
            double joule = consumptionData.getJoule();
            double seconds = consumptionData.getSeconds();

            boolean found = false;
            for (ConsumptionData averageConsumptionData : averagedConsumptionsDataList) {
                if (signature.equals(averageConsumptionData.getSignature())) {
                    averageConsumptionData.setJoule(averageConsumptionData.getJoule() + joule);
                    averageConsumptionData.setSeconds(averageConsumptionData.getSeconds() + seconds);
                    averageConsumptionData.setNumOfTraces(averageConsumptionData.getNumOfTraces() + 1);
                    found = true;
                }
            }
            if (!found) {
                ConsumptionData averageConsumptionData = new ConsumptionData(signature, joule, seconds);
                averagedConsumptionsDataList.add(averageConsumptionData);
            }
        }

        for (ConsumptionData averageConsumptionData : averagedConsumptionsDataList) {
            double joule = averageConsumptionData.getJoule();
            double seconds = averageConsumptionData.getSeconds();
            int numOfTraces = averageConsumptionData.getNumOfTraces();

            averageConsumptionData.setJoule(joule / numOfTraces);
            averageConsumptionData.setSeconds(seconds / numOfTraces);
        }
    }

    private void updateBoxplot(String nameFilter) throws IOException {
        this.filterData(nameFilter);
        this.calculateAverages();

        averagedConsumptionsDataList.sort((ConsumptionData o1, ConsumptionData o2) -> -Double.compare(o1.getJoule(), o2.getJoule()));

        int numOfBoxplot = 5;

        if (averagedConsumptionsDataList.size() < 5) {
            numOfBoxplot = averagedConsumptionsDataList.size();
        }

        List<ConsumptionData> mostGreedyAveragedData = averagedConsumptionsDataList.subList(0, numOfBoxplot);

        DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();

        int i = 0;
        for (ConsumptionData mInstance : mostGreedyAveragedData) {
            BoxplotData mostGreedyData = new BoxplotData(mInstance.getSignature());
            for (ConsumptionData fInstance : filteredConsumptionsDataList) {
                if (fInstance.getSignature().equals(mInstance.getSignature())) {
                    mostGreedyData.addValue(fInstance.getJoule());
                }
            }
            dataset.add(mostGreedyData.getValues(), (i + 1) + ": " + mostGreedyData.getSignature(), (i + 1));
            i++;
        }

        String boxplot_title = "Top 5 Energy Greedy Methods";

        CategoryAxis xAxis = new CategoryAxis("Signatures");
        NumberAxis yAxis = new NumberAxis("Consumptions");

        yAxis.setAutoRangeIncludesZero(false);
        BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();

        renderer.setFillBox(true);
        renderer.setBaseToolTipGenerator(new BoxAndWhiskerToolTipGenerator());

        CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);

        JFreeChart chart = new JFreeChart(
                boxplot_title,
                new Font("SansSerif", Font.BOLD, 14),
                plot,
                true
        );

        chart.setAntiAlias(true);

        ChartPanel chartPanel = new ChartPanel(chart, true, true, true, true, true);
        chartPanel.setMouseWheelEnabled(true);
        tab2 = new JPanel();
        tab2.removeAll();
        tab2.add(chartPanel, new GridConstraints());
    }

    private void filterResultsActionPerformed() {
        try {
            updateTable(search.getText());
            updateBoxplot(search.getText());
        } catch (IOException ex) {

        }

        panel_first = new JPanel(new GridLayoutManager(3,4));
        button_search = new JButton("Filter Results", new ImageIcon(getClass().getResource("/filter.png")));
        button_search.setToolTipText("Filter Results.");
        button_search.addActionListener(evt -> filterResultsActionPerformed());
        clean = new JButton("Clean Results", new ImageIcon(getClass().getResource("/clean.png")));
        clean.setToolTipText("Clean.");
        clean.addActionListener(evt -> cleanResultsActionPerformed());
        panel_first.add(search, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        panel_first.add(button_search, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel_first.add(clean, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));


        search.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    filterResultsActionPerformed();
                }
            }
        });

        panel_first.add(consumptionTable.getTableHeader(), new GridConstraints(1, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel_first.add(consumptionTable, new GridConstraints(2, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));


        tabbedPane.removeAll();
        tabbedPane.add("Result PETrA", ScrollPaneFactory.createScrollPane(panel_first));
        tabbedPane.add("BoxPlot PETrA", ScrollPaneFactory.createScrollPane(tab2));
        tabbedPane.add("Log PETrA", ScrollPaneFactory.createScrollPane(this.statusArea));
    }

    private void cleanResultsActionPerformed() {
        try {
            updateTable("");
            updateBoxplot("");
            button_search.setText("");
        } catch (IOException ex) {

        }

        panel_first = new JPanel(new GridLayoutManager(3,4));
        button_search = new JButton("Filter Results", new ImageIcon(getClass().getResource("/filter.png")));
        button_search.setToolTipText("Filter Results.");
        button_search.addActionListener(evt -> filterResultsActionPerformed());
        clean = new JButton("Clean Results", new ImageIcon(getClass().getResource("/clean.png")));
        clean.setToolTipText("Clean.");
        clean.addActionListener(evt -> cleanResultsActionPerformed());
        panel_first.add(search, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        panel_first.add(button_search, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel_first.add(clean, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));


        search.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    filterResultsActionPerformed();
                }
            }
        });

        panel_first.add(consumptionTable.getTableHeader(), new GridConstraints(1, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel_first.add(consumptionTable, new GridConstraints(2, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));


        tabbedPane.removeAll();
        tabbedPane.add("Result PETrA", ScrollPaneFactory.createScrollPane(panel_first));
        tabbedPane.add("BoxPlot PETrA", ScrollPaneFactory.createScrollPane(tab2));
        tabbedPane.add("Log PETrA", ScrollPaneFactory.createScrollPane(this.statusArea));
    }

    public List<ConsumptionData> getAveragedConsumptionsDataList() {
        return averagedConsumptionsDataList;
    }
}
