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

        lblTitle = new javax.swing.JLabel();
        pnChart = new javax.swing.JPanel();
        pnTable = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblRevenue = new javax.swing.JTable();

        setBackground(new java.awt.Color(241, 245, 249));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setLayout(new java.awt.BorderLayout());

        lblTitle.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(30, 41, 59));
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setText("Statistics of revenue in 2025");
        add(lblTitle, java.awt.BorderLayout.PAGE_START);

        pnChart.setBackground(new java.awt.Color(255, 255, 255));
        pnChart.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
        pnChart.setPreferredSize(new java.awt.Dimension(0, 400));

        javax.swing.GroupLayout pnChartLayout = new javax.swing.GroupLayout(pnChart);
        pnChart.setLayout(pnChartLayout);
        pnChartLayout.setHorizontalGroup(
            pnChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 634, Short.MAX_VALUE)
        );
        pnChartLayout.setVerticalGroup(
            pnChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 262, Short.MAX_VALUE)
        );

        add(pnChart, java.awt.BorderLayout.CENTER);

        pnTable.setBackground(new java.awt.Color(241, 245, 249));
        pnTable.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Details of monthly revenue", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12), new java.awt.Color(0, 0, 0))); // NOI18N
        pnTable.setPreferredSize(new java.awt.Dimension(0, 250));
        pnTable.setLayout(new java.awt.BorderLayout());

        tblRevenue.setBackground(new java.awt.Color(204, 204, 204));
        tblRevenue.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Month", "Revenue"
            }
        ));
        tblRevenue.setFillsViewportHeight(true);
        tblRevenue.setRowHeight(30);
        tblRevenue.setShowGrid(true);
        jScrollPane2.setViewportView(tblRevenue);

        pnTable.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        add(pnTable, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel pnChart;
    private javax.swing.JPanel pnTable;
    private javax.swing.JTable tblRevenue;
    // End of variables declaration//GEN-END:variables
}
