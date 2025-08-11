/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.bookmanagement.view;



import com.bookmanagement.service.RevenueService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.text.NumberFormat;
import java.time.Year;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

/**
 * Panel hiển thị thống kê doanh thu.
 * Lấy dữ liệu từ cơ sở dữ liệu thông qua RevenueService.
 *
 * @author ADMIN
 */
public class RevenueStatisticsPanel extends javax.swing.JPanel {

    private static final Logger LOGGER = Logger.getLogger(RevenueStatisticsPanel.class.getName());

    // Dữ liệu doanh thu sẽ được lấy từ database
    private Map<Integer, Double> monthlyRevenueData;
    
    // Tên cột cho bảng
    private final String[] TABLE_COLUMN_NAMES = {"Tháng", "Doanh thu"};

    // Panel riêng để vẽ biểu đồ
    private JPanel chartPanel;
    


    public RevenueStatisticsPanel() {
        // Phương thức này được tạo tự động bởi NetBeans, không nên sửa đổi
        initComponents();
        
        // Gọi các phương thức tùy chỉnh để thiết lập giao diện
        initializeCustomComponents();
        loadMonthlyRevenueAndPopulate();
        applyCustomStyles();
    }
    
    /**
     * Phương thức này được tạo tự động bởi NetBeans Form Editor.
     * Cẩn thận không sửa đổi mã này.
     */
    // Phương thức tùy chỉnh để thêm biểu đồ vào panel
    private void initializeCustomComponents() {
        chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBarChart(g);
            }
        };
        chartPanel.setPreferredSize(new Dimension(800, 300));
        chartPanel.setBackground(new Color(241, 245, 249));
        
        // Thêm chartPanel vào vị trí trung tâm của BorderLayout
        this.add(chartPanel, BorderLayout.CENTER);
    }
    
    private void applyCustomStyles() {
        // Cài đặt màu nền cho panel chính
        this.setBackground(new Color(241, 245, 249));
    }
    
    private void loadMonthlyRevenueAndPopulate() {
        RevenueService revenueService = new RevenueService();
        int currentYear = Year.now().getValue();
        monthlyRevenueData = revenueService.getMonthlyRevenue(currentYear);
        
        // Sau khi có dữ liệu, vẽ lại biểu đồ và điền vào bảng
        if (chartPanel != null) {
            chartPanel.repaint();
        }
        
        populateTableData();
    }
    
    private void populateTableData() {
        DefaultTableModel model = new DefaultTableModel(TABLE_COLUMN_NAMES, 0);
        
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        
        for (int i = 1; i <= 12; i++) {
            String monthLabel = "Tháng " + i;
            double revenue = monthlyRevenueData.getOrDefault(i, 0.0);
            String formattedRevenue = currencyFormat.format(revenue);
            model.addRow(new Object[]{monthLabel, formattedRevenue});
        }
        
        tblRevenue.setModel(model);
    }

    private void drawBarChart(Graphics g) {
        if (monthlyRevenueData == null || monthlyRevenueData.isEmpty()) {
            return;
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int panelWidth = chartPanel.getWidth();
        int panelHeight = chartPanel.getHeight();
        int padding = 40;
        int barWidth = (panelWidth - 2 * padding) / 13;
        int barGap = barWidth / 4;
        
        // Tính toán max revenue cho trục y
        double maxRevenue = monthlyRevenueData.values().stream()
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(1.0);
        
        double scale = (double) (panelHeight - 2 * padding) / maxRevenue;
        
        // Vẽ trục X và Y
        g2d.setColor(Color.BLACK);
        g2d.drawLine(padding, panelHeight - padding, panelWidth - padding, panelHeight - padding);
        g2d.drawLine(padding, padding, padding, panelHeight - padding);
        
        // Vẽ biểu đồ cột
        for (int i = 1; i <= 12; i++) {
            double revenue = monthlyRevenueData.getOrDefault(i, 0.0);
            int barHeight = (int) (revenue * scale);
            int x = padding + (i * barWidth);
            int y = panelHeight - padding - barHeight;
            
            // Vẽ thanh bar
            g2d.setColor(new Color(65, 105, 225)); // Màu xanh royal
            g2d.fillRect(x, y, barWidth - barGap, barHeight);
            
            // Vẽ label tháng
            g2d.setColor(Color.BLACK);
            String monthLabel = "T" + i;
            g2d.drawString(monthLabel, x + (barWidth - barGap) / 2 - 5, panelHeight - padding + 15);

            // Vẽ giá trị doanh thu
            String revenueString = String.format("%,.0f", revenue);
            g2d.drawString(revenueString, x, y - 5);
        }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnFilter = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtFrom = new javax.swing.JFormattedTextField();
        txtArrive = new javax.swing.JFormattedTextField();
        cbxCategory = new javax.swing.JComboBox<>();
        btnView = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        pnSummary = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        lblTotalRevenue = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblTotalDiscount = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        lblNetProfit = new javax.swing.JLabel();
        tabbedPaneCharts = new javax.swing.JTabbedPane();
        pnBookChart = new javax.swing.JPanel();
        pnCategoryChart = new javax.swing.JPanel();
        pnMonthlyChart = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblRevenueDetail = new javax.swing.JTable();

        setBackground(new java.awt.Color(204, 204, 204));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));

        jLabel1.setText("Filter over time:");

        jLabel2.setText("From");

        jLabel3.setText("Arrive:");

        jLabel4.setText("Category:");

        cbxCategory.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnView.setText("SEE");

        btnRefresh.setText("REFRESH");

        javax.swing.GroupLayout pnFilterLayout = new javax.swing.GroupLayout(pnFilter);
        pnFilter.setLayout(pnFilterLayout);
        pnFilterLayout.setHorizontalGroup(
            pnFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnFilterLayout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addGroup(pnFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnFilterLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 395, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(txtArrive, javax.swing.GroupLayout.PREFERRED_SIZE, 395, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnFilterLayout.createSequentialGroup()
                        .addGroup(pnFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnFilterLayout.createSequentialGroup()
                                .addComponent(btnView)
                                .addGap(18, 18, 18)
                                .addComponent(btnRefresh))
                            .addGroup(pnFilterLayout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addGap(18, 18, 18)
                                .addComponent(cbxCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel1))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        pnFilterLayout.setVerticalGroup(
            pnFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnFilterLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(pnFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtArrive, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(cbxCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnView)
                    .addComponent(btnRefresh))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel5.setText("Total revenue:");

        lblTotalRevenue.setText("0 VND");

        jLabel7.setText("Total discount: ");

        lblTotalDiscount.setText("0 VND");

        jLabel9.setText("Net profit:");

        lblNetProfit.setText("0 VND");

        javax.swing.GroupLayout pnSummaryLayout = new javax.swing.GroupLayout(pnSummary);
        pnSummary.setLayout(pnSummaryLayout);
        pnSummaryLayout.setHorizontalGroup(
            pnSummaryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnSummaryLayout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addGroup(pnSummaryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel9)
                    .addComponent(jLabel5))
                .addGap(115, 115, 115)
                .addGroup(pnSummaryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTotalRevenue)
                    .addComponent(lblTotalDiscount)
                    .addComponent(lblNetProfit))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnSummaryLayout.setVerticalGroup(
            pnSummaryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnSummaryLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(pnSummaryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(lblTotalRevenue))
                .addGap(34, 34, 34)
                .addGroup(pnSummaryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(lblTotalDiscount))
                .addGap(29, 29, 29)
                .addGroup(pnSummaryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(lblNetProfit))
                .addContainerGap(60, Short.MAX_VALUE))
        );

        tabbedPaneCharts.setBackground(new java.awt.Color(51, 51, 51));

        javax.swing.GroupLayout pnBookChartLayout = new javax.swing.GroupLayout(pnBookChart);
        pnBookChart.setLayout(pnBookChartLayout);
        pnBookChartLayout.setHorizontalGroup(
            pnBookChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1100, Short.MAX_VALUE)
        );
        pnBookChartLayout.setVerticalGroup(
            pnBookChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 234, Short.MAX_VALUE)
        );

        tabbedPaneCharts.addTab("Book Revenue", pnBookChart);

        javax.swing.GroupLayout pnCategoryChartLayout = new javax.swing.GroupLayout(pnCategoryChart);
        pnCategoryChart.setLayout(pnCategoryChartLayout);
        pnCategoryChartLayout.setHorizontalGroup(
            pnCategoryChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1100, Short.MAX_VALUE)
        );
        pnCategoryChartLayout.setVerticalGroup(
            pnCategoryChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 234, Short.MAX_VALUE)
        );

        tabbedPaneCharts.addTab("Category Revenue", pnCategoryChart);

        javax.swing.GroupLayout pnMonthlyChartLayout = new javax.swing.GroupLayout(pnMonthlyChart);
        pnMonthlyChart.setLayout(pnMonthlyChartLayout);
        pnMonthlyChartLayout.setHorizontalGroup(
            pnMonthlyChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1100, Short.MAX_VALUE)
        );
        pnMonthlyChartLayout.setVerticalGroup(
            pnMonthlyChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 234, Short.MAX_VALUE)
        );

        tabbedPaneCharts.addTab("Monthly Revenue", pnMonthlyChart);

        tblRevenueDetail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "STT", "Transaction Code", "Book Code", "Book Name", "Category", "Quantity", "Coin"
            }
        ));
        jScrollPane1.setViewportView(tblRevenueDetail);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pnFilter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pnSummary, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(tabbedPaneCharts))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pnSummary, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(tabbedPaneCharts, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnView;
    private javax.swing.JComboBox<String> cbxCategory;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblNetProfit;
    private javax.swing.JLabel lblTotalDiscount;
    private javax.swing.JLabel lblTotalRevenue;
    private javax.swing.JPanel pnBookChart;
    private javax.swing.JPanel pnCategoryChart;
    private javax.swing.JPanel pnFilter;
    private javax.swing.JPanel pnMonthlyChart;
    private javax.swing.JPanel pnSummary;
    private javax.swing.JTabbedPane tabbedPaneCharts;
    private javax.swing.JTable tblRevenueDetail;
    private javax.swing.JFormattedTextField txtArrive;
    private javax.swing.JFormattedTextField txtFrom;
    // End of variables declaration//GEN-END:variables
}
