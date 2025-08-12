/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.bookmanagement.view;
import com.bookmanagement.dao.BookDAO;
import com.bookmanagement.model.Book; // Cần import class Book để lấy thông tin sách
import com.bookmanagement.model.RevenueReport;
import com.bookmanagement.service.RevenueService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.Year;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import com.toedter.calendar.JDateChooser;
import org.jfree.chart.ChartPanel;

/**
 * Panel hiển thị thống kê doanh thu. Lấy dữ liệu từ cơ sở dữ liệu thông qua
 * RevenueService.
 *
 * @author ADMIN
 */
public class RevenueStatisticsPanel extends javax.swing.JPanel {

    private static final Logger LOGGER = Logger.getLogger(RevenueStatisticsPanel.class.getName());

    private RevenueService revenueService;
    private RevenueReport revenueReport;
    private DefaultTableModel tableModel;
    private BookDAO bookDAO; // Sử dụng để lấy danh sách danh mục sách

    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    private final String[] TABLE_COLUMN_NAMES = {"Order ID", "Book Name", "Category", "Quantity", "Price", "Date", "Discount"};

    private MonthlyBarChartPanel monthlyBarChartPanel;
    private BookBarChartPanel bookBarChartPanel;
    private CategoryBarChartPanel categoryBarChartPanel;

    private List<RevenueReport> revenueReports;

    public RevenueStatisticsPanel() {
        // Khởi tạo các dịch vụ
        this.revenueService = new RevenueService();
        this.bookDAO = new BookDAO();
        initComponents();
        initTable();
        initUI();
        initData();
        setupComponents();
    }

    public void initTable() {
        tableModel = new DefaultTableModel(TABLE_COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblRevenueDetail.setModel(tableModel);
    }
    
     private void initUI() {
        initCategoryCombobox();
        
        // Khởi tạo và thêm các panel biểu đồ tùy chỉnh vào JTabbedPane
        monthlyBarChartPanel = new MonthlyBarChartPanel();
        pnMonthlyChart.setLayout(new BorderLayout());
        pnMonthlyChart.add(monthlyBarChartPanel, BorderLayout.CENTER);
        
        bookBarChartPanel = new BookBarChartPanel();
        pnBookChart.setLayout(new BorderLayout());
        pnBookChart.add(bookBarChartPanel, BorderLayout.CENTER);
        
        categoryBarChartPanel = new CategoryBarChartPanel();
        pnCategoryChart.setLayout(new BorderLayout());
        pnCategoryChart.add(categoryBarChartPanel, BorderLayout.CENTER);
    }

    /**
     * Khởi tạo dữ liệu ban đầu cho các bộ lọc và tải báo cáo.
     */
    private void setupComponents() {
        // Tải danh mục sách vào JComboBox
        initCategoryCombobox();

        // Thiết lập sự kiện cho các JDateChooser để tự động tải lại dữ liệu khi thay đổi ngày
        jdcStartDate.addPropertyChangeListener("date", e -> refreshData());
        jdcEndDate.addPropertyChangeListener("date", e -> refreshData());
    }

    private void initData() {
        refreshData();
    }

    /**
     * Tải lại toàn bộ dữ liệu thống kê và cập nhật giao diện. Phương thức này
     * có thể được gọi từ các lớp khác để tự động cập nhật khi có thay đổi dữ
     * liệu (ví dụ: một đơn hàng mới được tạo).
     */
    private void refreshData() {
        LocalDate startDate = null;
        if (jdcStartDate.getDate() != null) {
            startDate = jdcStartDate.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } else {
            startDate = LocalDate.now().minusMonths(1);
        }
        LocalDate endDate = null;
        if (jdcEndDate.getDate() != null) {
            endDate = jdcEndDate.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } else {
            endDate = LocalDate.now();
        }
        
        if (startDate.isAfter(endDate)) {
            JOptionPane.showMessageDialog(this, "Ngày bắt đầu không được sau ngày kết thúc.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String selectedCategory = cbxCategory.getSelectedItem() != null ? cbxCategory.getSelectedItem().toString() : "Tất cả";
        
        List<RevenueReport> revenueReports = revenueService.getRevenueReport(startDate, endDate, selectedCategory);
        updateSummaryPanel(revenueReports);
        updateTable(revenueReports);
        updateCharts(revenueReports);
    }

// Giả định các phương thức cập nhật UI nhận List<RevenueReport> làm tham số
    private void updateSummaryPanel(List<RevenueReport> reports) {
        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        BigDecimal netProfit = BigDecimal.ZERO;

        for (RevenueReport report : reports) {
            totalRevenue = totalRevenue.add(report.getTotalRevenue());
            totalDiscount = totalDiscount.add(report.getTotalDiscount());
            netProfit = netProfit.add(report.getNetProfit());
        }

        lblTotalRevenue.setText(NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(totalRevenue));
        lblTotalDiscount.setText(NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(totalDiscount));
        lblNetProfit.setText(NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(netProfit));
    }

    private void updateTable(List<RevenueReport> reports) {
        DefaultTableModel tableModel = (DefaultTableModel) tblRevenueDetail.getModel();
        tableModel.setRowCount(0); // Xóa tất cả các hàng hiện có

        // Đảm bảo các cột được thiết lập
        if (tableModel.getColumnCount() == 0) {
            tableModel.setColumnIdentifiers(new Object[]{"Mã đơn hàng", "Tên khách hàng", "Ngày bán", "Tổng tiền", "Giảm giá", "Doanh thu", "Lợi nhuận"});
        }

        // Thêm dữ liệu từ danh sách báo cáo vào bảng
        for (RevenueReport report : reports) {
            Object[] rowData = {
                report.getOrderId(),
                report.getCustomerName(),
                report.getOrderDate(),
                NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(report.getTotalAmount()),
                NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(report.getTotalDiscount()),
                NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(report.getTotalRevenue()),
                NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(report.getNetProfit())
            };
            tableModel.addRow(rowData);
        }
    }

    /**
     * Cập nhật tất cả các biểu đồ.
     *
     * @param report Đối tượng RevenueReport chứa dữ liệu.
     */
    private void updateCharts(List<RevenueReport> reports) {
        // Cập nhật dữ liệu cho biểu đồ hàng tháng
        updateMonthlyChart(reports);
        updateBookChart(reports);
        updateCategoryChart(reports);
    }
    
    
     private void updateMonthlyChart(List<RevenueReport> reports) {
        Map<String, BigDecimal> monthlyRevenue = new LinkedHashMap<>();
        for (RevenueReport report : reports) {
            String monthKey = report.getOrderDate().toString().substring(0, 7); // "YYYY-MM"
            monthlyRevenue.put(monthKey, monthlyRevenue.getOrDefault(monthKey, BigDecimal.ZERO).add(report.getTotalRevenue()));
        }
        monthlyBarChartPanel.setData(monthlyRevenue);
    }

    /**
     * Cập nhật biểu đồ doanh thu theo sách bán chạy.
     * @param reports Danh sách các đối tượng RevenueReport.
     */
    private void updateBookChart(List<RevenueReport> reports) {
        Map<String, BigDecimal> bookRevenue = new HashMap<>();
        for (RevenueReport report : reports) {
            try {
                // Giả định BookDAO có phương thức để lấy tiêu đề sách từ orderId
                Book book = bookDAO.getBookByOrderId(report.getOrderId());
                if (book != null) {
                    bookRevenue.put(book.getTitle(), bookRevenue.getOrDefault(book.getTitle(), BigDecimal.ZERO).add(report.getTotalRevenue()));
                }
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Lỗi khi lấy tên sách", ex);
            }
        }
        // Sắp xếp giảm dần theo doanh thu và chỉ lấy top 10
        List<Map.Entry<String, BigDecimal>> sortedEntries = new ArrayList<>(bookRevenue.entrySet());
        sortedEntries.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        
        Map<String, BigDecimal> topBookRevenue = new LinkedHashMap<>();
        int count = 0;
        for (Map.Entry<String, BigDecimal> entry : sortedEntries) {
            if (count < 10) {
                topBookRevenue.put(entry.getKey(), entry.getValue());
                count++;
            } else {
                break;
            }
        }
        bookBarChartPanel.setData(topBookRevenue);
    }

    /**
     * Cập nhật biểu đồ doanh thu theo danh mục.
     * @param reports Danh sách các đối tượng RevenueReport.
     */
    private void updateCategoryChart(List<RevenueReport> reports) {
        Map<String, BigDecimal> categoryRevenue = new HashMap<>();
        for (RevenueReport report : reports) {
            try {
                // Giả định BookDAO có phương thức để lấy danh mục sách từ orderId
                Book book = bookDAO.getBookByOrderId(report.getOrderId());
                if (book != null) {
                    categoryRevenue.put(book.getCategory(), categoryRevenue.getOrDefault(book.getCategory(), BigDecimal.ZERO).add(report.getTotalRevenue()));
                }
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Lỗi khi lấy danh mục sách", ex);
            }
        }
        categoryBarChartPanel.setData(categoryRevenue);
    }
    
    /**
     * Khởi tạo và điền dữ liệu cho combobox danh mục.
     */
    private void initCategoryCombobox() {
        cbxCategory.removeAllItems();
        cbxCategory.addItem("All Category");
        try {
            // Giả lập việc lấy danh sách danh mục từ database
            // Trong thực tế, bạn sẽ gọi một phương thức từ BookDAO
            List<String> categories = new ArrayList<>();
            categories.add("Science fiction");
            categories.add("History");
            categories.add("Economy");
            categories.add("Novel");
            categories.add("Develop yourself");
            categories.add("Life Skills");
            for (String category : categories) {
                cbxCategory.addItem(category);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy danh mục sách", ex);
        }
    }

    /**
     * Xử lý sự kiện khi nhấn nút "Xem".
     */
    /**
     * Xử lý sự kiện khi nhấn nút "Làm mới".
     */
    class BarChartPanel extends JPanel {
        protected Map<String, BigDecimal> data;
        protected String title;
        protected String xLabel;
        protected String yLabel;
        
        public BarChartPanel(String title, String xLabel, String yLabel) {
            this.title = title;
            this.xLabel = xLabel;
            this.yLabel = yLabel;
            this.data = new LinkedHashMap<>();
            setBackground(Color.WHITE);
        }

        public void setData(Map<String, BigDecimal> data) {
            this.data = data;
            revalidate();
            repaint();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (data == null || data.isEmpty()) {
                return;
            }

            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Kích thước của panel và lề
            int width = getWidth();
            int height = getHeight();
            int margin = 20;
            int topMargin = 50;
            int bottomMargin = 80;
            int leftMargin = 80;
            int rightMargin = 20;

            // Vùng vẽ biểu đồ
            int chartWidth = width - leftMargin - rightMargin;
            int chartHeight = height - topMargin - bottomMargin;

            // Tìm giá trị lớn nhất để chia tỷ lệ
            BigDecimal maxValue = data.values().stream()
                    .max(BigDecimal::compareTo)
                    .orElse(BigDecimal.ONE);
            
            // Vẽ tiêu đề
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));
            g2d.drawString(title, (width - g2d.getFontMetrics().stringWidth(title)) / 2, 30);
            
            // Vẽ trục tọa độ
            g2d.drawLine(leftMargin, topMargin + chartHeight, leftMargin + chartWidth, topMargin + chartHeight); // Trục x
            g2d.drawLine(leftMargin, topMargin, leftMargin, topMargin + chartHeight); // Trục y

            // Vẽ nhãn trục y
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2d.setColor(Color.GRAY);
            g2d.drawString(yLabel, 10, height / 2);
            
            // Vẽ các cột
            int barCount = data.size();
            int barWidth = Math.max(10, (chartWidth - (barCount - 1) * 5) / barCount);
            int gap = (chartWidth - barCount * barWidth) / (barCount - 1);
            if (barCount <= 1) { // Xử lý trường hợp chỉ có 1 cột
                gap = 0;
                barWidth = chartWidth / 2;
            }

            int x = leftMargin;
            int barIndex = 0;
            for (Map.Entry<String, BigDecimal> entry : data.entrySet()) {
                BigDecimal value = entry.getValue();
                String label = entry.getKey();

                // Tính toán chiều cao của cột
                int barHeight = (int) (value.doubleValue() / maxValue.doubleValue() * chartHeight);
                int barY = topMargin + chartHeight - barHeight;

                // Vẽ cột
                g2d.setColor(new Color(51, 153, 255));
                g2d.fillRect(x, barY, barWidth, barHeight);
                g2d.setColor(Color.BLACK);
                g2d.drawRect(x, barY, barWidth, barHeight);

                // Vẽ giá trị trên cột
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                String formattedValue = NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(value);
                int stringWidth = g2d.getFontMetrics().stringWidth(formattedValue);
                g2d.drawString(formattedValue, x + (barWidth - stringWidth) / 2, barY - 5);

                // Vẽ nhãn trục x
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                g2d.rotate(Math.toRadians(45), x + barWidth / 2, topMargin + chartHeight + 10);
                g2d.drawString(label, x + barWidth / 2, topMargin + chartHeight + 10);
                g2d.rotate(Math.toRadians(-45), x + barWidth / 2, topMargin + chartHeight + 10);

                x += barWidth + gap;
                barIndex++;
            }
        }
    }
    
    
    class MonthlyBarChartPanel extends BarChartPanel {
        public MonthlyBarChartPanel() {
            super("Doanh thu theo tháng", "Tháng", "Doanh thu (VNĐ)");
        }
    }
    
    class BookBarChartPanel extends BarChartPanel {
        public BookBarChartPanel() {
            super("Top sách bán chạy", "Tên sách", "Doanh thu (VNĐ)");
        }
    }
    
    class CategoryBarChartPanel extends BarChartPanel {
        public CategoryBarChartPanel() {
            super("Doanh thu theo danh mục", "Danh mục", "Doanh thu (VNĐ)");
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
        cbxCategory = new javax.swing.JComboBox<>();
        btnView = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        jdcStartDate = new com.toedter.calendar.JDateChooser();
        jdcEndDate = new com.toedter.calendar.JDateChooser();
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

        jLabel2.setText("Start Date: ");

        jLabel3.setText("End Date: ");

        jLabel4.setText("Category:");

        cbxCategory.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnView.setText("SEE");
        btnView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewActionPerformed(evt);
            }
        });

        btnRefresh.setText("REFRESH");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

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
                        .addComponent(jdcStartDate, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(54, 54, 54)
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(jdcEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2)
                    .addComponent(jdcStartDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jdcEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24)
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
            .addGap(0, 929, Short.MAX_VALUE)
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
            .addGap(0, 929, Short.MAX_VALUE)
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
            .addGap(0, 929, Short.MAX_VALUE)
        );
        pnMonthlyChartLayout.setVerticalGroup(
            pnMonthlyChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 234, Short.MAX_VALUE)
        );

        tabbedPaneCharts.addTab("Monthly Revenue", pnMonthlyChart);

        tblRevenueDetail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
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
                            .addComponent(tabbedPaneCharts, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))
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

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        // TODO add your handling code here:
        refreshData();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void btnViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewActionPerformed
        // TODO add your handling code here:
        refreshData();
    }//GEN-LAST:event_btnViewActionPerformed


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
    private com.toedter.calendar.JDateChooser jdcEndDate;
    private com.toedter.calendar.JDateChooser jdcStartDate;
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
    // End of variables declaration//GEN-END:variables
}
