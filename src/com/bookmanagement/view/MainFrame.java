package com.bookmanagement.view;

import com.bookmanagement.Modell.UserSession;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
// Không còn cần HashSet/Set trực tiếp ở đây vì UserSession sẽ cung cấp chúng

public class MainFrame extends JFrame {

    private JPanel mainContentPanel;
    private CardLayout cardLayout;
    private JLabel lblWelcomeUser;

    private UserSession currentUserSession; // Lưu trữ phiên người dùng hiện tại

    public MainFrame(UserSession userSession) {
        this.currentUserSession = userSession; // Gán phiên người dùng được truyền vào

        setTitle("QUẢN LÝ CỬA HÀNG SÁCH");
        setSize(1000, 700); // Kích thước cửa sổ
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Đặt cửa sổ ra giữa màn hình
        setLayout(new BorderLayout()); // Sử dụng BorderLayout cho frame chính

        // --- 1. Header Panel ---
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(70, 130, 180)); // Màu xanh thép
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTitle = new JLabel("QUẢN LÝ CỬA HÀNG SÁCH", SwingConstants.LEFT);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        userInfoPanel.setOpaque(false); // Làm trong suốt panel con
        
        lblWelcomeUser = new JLabel("Xin chào: " + currentUserSession.getUserName()); // Hiển thị tên từ UserSession
        lblWelcomeUser.setFont(new Font("Arial", Font.PLAIN, 16));
        lblWelcomeUser.setForeground(Color.WHITE);
        userInfoPanel.add(lblWelcomeUser);

        JButton btnLogout = new JButton("Đăng Xuất");
        btnLogout.setFont(new Font("Arial", Font.PLAIN, 14));
        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(MainFrame.this,
                                "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận đăng xuất",
                                JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    // Logic đăng xuất: đóng cửa sổ hiện tại
                    dispose();
                    // Trong ứng dụng thực tế, bạn sẽ mở lại cửa sổ đăng nhập tại đây:
                    // new LoginForm().setVisible(true);
                    System.out.println("Đã đăng xuất.");
                }
            }
        });
        userInfoPanel.add(btnLogout);

        headerPanel.add(userInfoPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // --- 2. Navigation Sidebar Panel ---
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setBackground(new Color(230, 230, 250)); // Màu tím lavender nhạt
        sidebarPanel.setLayout(new GridLayout(0, 1, 0, 10)); // Layout lưới 1 cột, khoảng cách 10px
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        sidebarPanel.setPreferredSize(new Dimension(200, getHeight())); // Chiều rộng sidebar

        // --- 3. Main Content Panel (sử dụng CardLayout để chuyển đổi các panel con) ---
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Khởi tạo các Panel chức năng và thêm vào mainContentPanel ---
        // Mỗi Panel chức năng sẽ là một lớp riêng biệt kế thừa từ JPanel
        // Hiện tại chỉ là các Placeholder
        JPanel homePanel = createHomePanel(); // Màn hình Trang Chủ
        mainContentPanel.add(homePanel, "Home");

        JPanel bookManagementPanel = createPlaceholderPanel("Quản lý Sách");
        mainContentPanel.add(bookManagementPanel, "BookManagement");

        JPanel inventoryManagementPanel = createPlaceholderPanel("Quản lý Kho");
        mainContentPanel.add(inventoryManagementPanel, "InventoryManagement");

        JPanel customerManagementPanel = createPlaceholderPanel("Quản lý Khách hàng");
        mainContentPanel.add(customerManagementPanel, "CustomerManagement");

        JPanel orderManagementPanel = createPlaceholderPanel("Quản lý Đơn hàng");
        mainContentPanel.add(orderManagementPanel, "OrderManagement");

        JPanel userManagementPanel = createPlaceholderPanel("Quản lý Người dùng");
        mainContentPanel.add(userManagementPanel, "UserManagement");


        // --- Thêm các nút chức năng vào Sidebar, kiểm tra quyền từ currentUserSession ---
        addButtonToSidebar(sidebarPanel, "Trang Chủ", "Home");
        addButtonToSidebar(sidebarPanel, "Quản lý Sách", "BookManagement", "PERM001"); // QUANLY_SACH
        addButtonToSidebar(sidebarPanel, "Quản lý Kho", "InventoryManagement", "PERM002"); // QUANLY_KHO
        addButtonToSidebar(sidebarPanel, "Quản lý Khách hàng", "CustomerManagement", "PERM003"); // QUANLY_KHACHHANG
        // Đối với Quản lý Đơn hàng, chỉ cần có một trong các quyền liên quan là đủ để hiển thị mục menu
        addButtonToSidebar(sidebarPanel, "Quản lý Đơn hàng", "OrderManagement", "PERM004", "PERM005"); // TAO_DONHANG hoặc QUANLY_DONHANG
        // Chức năng quản lý người dùng chỉ hiển thị cho admin (người có quyền QUANLY_NGUOIDUNG)
        addButtonToSidebar(sidebarPanel, "Quản lý Người dùng", "UserManagement", "PERM007"); // QUANLY_NGUOIDUNG


        add(sidebarPanel, BorderLayout.WEST);
        add(mainContentPanel, BorderLayout.CENTER);

        // Hiển thị màn hình Trang Chủ mặc định khi khởi động
        cardLayout.show(mainContentPanel, "Home");
    }

    // --- Phương thức tạo nút và thêm vào sidebar, kiểm tra quyền hạn ---
    private void addButtonToSidebar(JPanel sidebar, String buttonText, String panelName, String... requiredPermissions) {
        // Mặc định là có thể truy cập nếu không yêu cầu quyền cụ thể
        boolean canAccess = true; 
        if (requiredPermissions != null && requiredPermissions.length > 0) {
            canAccess = false; // Nếu có quyền yêu cầu, mặc định là không thể truy cập
            for (String perm : requiredPermissions) {
                if (currentUserSession.hasPermission(perm)) { // Kiểm tra quyền hạn của người dùng hiện tại
                    canAccess = true;
                    break; // Chỉ cần có một quyền là đủ
                }
            }
        }
        
        if (canAccess) {
            JButton button = new JButton(buttonText);
            button.setFont(new Font("Arial", Font.BOLD, 16));
            button.setHorizontalAlignment(SwingConstants.LEFT);
            button.setFocusPainted(false); // Bỏ viền focus khi click
            button.setBackground(new Color(173, 216, 230)); // Màu xanh nhạt
            button.setForeground(Color.DARK_GRAY);
            button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 10)); // Padding

            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cardLayout.show(mainContentPanel, panelName);
                    // Có thể thêm logic highlight nút được chọn tại đây
                }
            });
            sidebar.add(button);
        }
    }

    // --- Phương thức tạo Panel Placeholder cho các chức năng chưa triển khai ---
    private JPanel createPlaceholderPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Đây là giao diện " + title + " (Sẽ được triển khai chi tiết)", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 28));
        label.setForeground(new Color(100, 100, 100));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    // --- Phương thức tạo Panel cho Trang Chủ ---
    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15); // Padding giữa các thành phần
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        JLabel welcomeLabel = new JLabel("Chào mừng bạn đến với Hệ thống Quản lý Cửa hàng sách!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(50, 100, 150));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Chiếm 2 cột
        panel.add(welcomeLabel, gbc);

        // Các thẻ thông tin tổng quan
        gbc.gridwidth = 1; // Trở lại 1 cột
        gbc.gridy = 1;

        // Các giá trị này sẽ được cập nhật từ database sau khi panel được hiển thị
        // hoặc thông qua một cơ chế tải dữ liệu nền.
        panel.add(createSummaryCard("Tổng số Sách", "...", "cuốn"), gbc);
        gbc.gridx = 1;
        panel.add(createSummaryCard("Tổng số Khách hàng", "...", "khách"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(createSummaryCard("Đơn hàng hôm nay", "...", "đơn"), gbc);
        gbc.gridx = 1;
        panel.add(createSummaryCard("Doanh thu hôm nay", "...", "VNĐ"), gbc);

        return panel;
    }

    private JPanel createSummaryCard(String title, String value, String unit) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)), // Viền ngoài
                BorderFactory.createEmptyBorder(15, 15, 15, 15) // Padding bên trong
        ));
        card.setBackground(new Color(245, 245, 245));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(80, 80, 80));
        card.add(titleLabel, BorderLayout.NORTH);

        JLabel valueLabel = new JLabel(value + " " + unit, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 36));
        valueLabel.setForeground(new Color(60, 120, 180));
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }


    // --- Main method để chạy ứng dụng (chỉ để kiểm tra giao diện) ---
    public static void main(String[] args) {

        // Ví dụ: Mô phỏng người dùng Admin (có tất cả quyền)
        java.util.Set<String> adminPermissions = new java.util.HashSet<>();
        adminPermissions.add("PERM001"); // QUANLY_SACH
        adminPermissions.add("PERM002"); // QUANLY_KHO
        adminPermissions.add("PERM003"); // QUANLY_KHACHHANG
        adminPermissions.add("PERM004"); // TAO_DONHANG
        adminPermissions.add("PERM005"); // QUANLY_DONHANG
        adminPermissions.add("PERM006"); // IN_HOADON
        adminPermissions.add("PERM007"); // QUANLY_NGUOIDUNG
        UserSession demoAdminUser = new UserSession("ND001", "admin", "Nguyễn Thị Thu (Admin)", adminPermissions);

        // Ví dụ: Mô phỏng người dùng Bán hàng (chỉ có một số quyền)
        java.util.Set<String> salesPermissions = new java.util.HashSet<>();
        salesPermissions.add("PERM003"); // QUANLY_KHACHHANG
        salesPermissions.add("PERM004"); // TAO_DONHANG
        salesPermissions.add("PERM005"); // QUANLY_DONHANG
        salesPermissions.add("PERM006"); // IN_HOADON
        UserSession demoSalesUser = new UserSession("ND002", "thungan1", "Trần Minh Khang (NV Bán hàng)", salesPermissions);


        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // CHỌN người dùng mô phỏng để kiểm tra giao diện:
                new MainFrame(demoAdminUser).setVisible(true); // Chạy với quyền Admin để thấy tất cả chức năng

                // HOẶC bỏ comment dòng dưới và comment dòng trên để chạy với quyền Nhân viên Bán hàng
                // new MainFrame(demoSalesUser).setVisible(true);
            }
        });
    }
}