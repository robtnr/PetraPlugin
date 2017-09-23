package dialog;


import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.uiDesigner.core.GridConstraints;
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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
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

    private String csvFile;

    public PetraDisplay(@NotNull Project project, JTextArea statusArea) throws IOException {
        getTable();
        this.project = project;
        tables.put(PetraCategory.Result,consumptionTable);

        tabbedPane.add("Result PETrA", ScrollPaneFactory.createScrollPane(consumptionTable));
        tabbedPane.add("BoxPlot PETrA", ScrollPaneFactory.createScrollPane(tab2));
        tabbedPane.add("Log PETrA", ScrollPaneFactory.createScrollPane(statusArea));
    }

    public JTabbedPane getTabbedPane()
    {
        return tabbedPane;
    }

    public void getTable() throws IOException {
        this.csvFile = getClass().getResource("/result.csv").toString();//this.mergeRunResults(inputLocationPath);
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
        System.out.println(csvFile);
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
                    System.out.println("ciao");
                    String name_class = consumptionTable.getValueAt(consumptionTable.getSelectedRow(),consumptionTable.getSelectedColumn()).toString();
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

        consumptionTable.setModel(model);
        consumptionTable.setRowSorter(sorter);
    }

    private void filterData(String nameFilter) throws IOException {
        filteredConsumptionsDataList = new ArrayList<>();

        Reader in = new FileReader("C:/Users/Utente/.IdeaIC2017.2/system/plugins-sandbox/plugins/TesiPetra/classes/result.csv");
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
}
