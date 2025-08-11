/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.bookmanagement.view;

import com.bookmanagement.model.User;
import com.bookmanagement.model.UserSession;
import com.bookmanagement.service.AuthService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.BorderFactory;

/**
 *
 * @author ADMIN
 */
public class HomePanel extends javax.swing.JPanel {

    private static final Logger LOGGER = Logger.getLogger(HomePanel.class.getName());
    private final Map<String, JPanel> contentPanels;

    private final MainFrame mainFrame;
    private final SlidingPanel contentSlider;
    private JButton activeButton = null;
    
    // Màu sắc cho hiệu ứng
    private final Color defaultButtonColor = new Color(153, 204, 255); // Màu nền mặc định cũ
    private final Color hoverColor = new Color(51, 65, 85); // Slate-700
    private final Color selectedColor = new Color(79, 70, 229); // Màu khi nút được chọn (Indigo-600)
    private final Color selectedFgColor = Color.WHITE;
    private final Color sidebarDefaultFgColor = Color.WHITE; // Màu chữ mặc định cho sidebar
    private final Color buttonTextColor = new Color(255, 255, 255);
    
    // Màu nền cho sidebar
    private final Color sidebarBgColor = new Color(30, 41, 59); // Slate-800


    public HomePanel(MainFrame mf) {
        this.mainFrame = mf;
        this.contentSlider = new SlidingPanel();
        this.contentPanels = new HashMap<>();
        initComponents();
        applyCustomStyles();
        
        pnData.add(contentSlider, BorderLayout.CENTER);
        
        // Khởi tạo và lưu trữ các panel nội dung
        contentPanels.put("Home", new Home());
        contentPanels.put("BookManagement", new BookManagementPanel());
        contentPanels.put("InventoryManagement", new InventoryManagementPanel());
        contentPanels.put("CustomerManagement", new CustomerPanel());
        contentPanels.put("OrderManagement", new OrderManagementPanel());
        contentPanels.put("UserManagement", new UserManagementPanel());
        contentPanels.put("RevenueStatistics", new RevenueStatisticsPanel()); // Thêm panel thống kê

        // Áp dụng hiệu ứng di chuột cho tất cả các nút
        addHoverEffect(btnHome);
        addHoverEffect(btnBook);
        addHoverEffect(btnInventory);
        addHoverEffect(btnCustomer);
        addHoverEffect(btnOrder);
        addHoverEffect(btnUser);
        addHoverEffect(btnRevenue); // Áp dụng hiệu ứng cho nút mới
        addHoverEffect(btnLogout);

        // Hiển thị panel Home và đánh dấu nút Home là nút mặc định được chọn
        contentSlider.showPanel(contentPanels.get("Home"));
        setActiveButton(btnHome);
        
        // Cập nhật các nút dựa trên quyền hạn của người dùng
        setupComponents();
    }
    
    
    private void setupComponents() {
        // Thiết lập layout cho pnData
        pnData.setLayout(new BorderLayout());
        pnData.add(contentSlider, BorderLayout.CENTER);
        
        // Khởi tạo các panel nội dung và thêm vào Map
        contentPanels.put("home", new Home());
        contentPanels.put("book", new BookManagementPanel());
        contentPanels.put("customer", new CustomerPanel());
        contentPanels.put("inventory", new InventoryManagementPanel());
        contentPanels.put("order", new OrderManagementPanel());
        contentPanels.put("user", new UserManagementPanel());
        contentPanels.put("revenue", new RevenueStatisticsPanel());
        
        // Hiển thị panel trang chủ ban đầu
        contentSlider.showPanel(contentPanels.get("home"));
        setActiveButton(btnHome);
        
        // Thiết lập màu sắc và font cho các nút
        setupButton(btnHome, "Trang chủ");
        setupButton(btnBook, "Sách");
        setupButton(btnCustomer, "Khách hàng");
        setupButton(btnInventory, "Kho hàng");
        setupButton(btnOrder, "Đơn hàng");
        setupButton(btnUser, "Người dùng");
        setupButton(btnRevenue, "Doanh thu");
        
        // Hiển thị thông tin người dùng
        User currentUser = UserSession.getCurrentUser();
        if (currentUser != null) {
            lblWelcomeUser.setText("WELCOME, " + currentUser.getUsername() + "!");
        }
    }
    
    
     private void setupButton(JButton button, String buttonText) {
        button.setBackground(defaultButtonColor);
        button.setForeground(buttonTextColor);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button != activeButton) {
                    button.setBackground(hoverColor);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button != activeButton) {
                    button.setBackground(defaultButtonColor);
                }
            }
        });
    }
    
    /**
     * Phương thức này chứa toàn bộ code tùy chỉnh giao diện.
     * Nó được gọi sau initComponents() để đảm bảo an toàn.
     */
    private void applyCustomStyles() {
        // Cài đặt màu nền chung cho các panel
        this.setBackground(new Color(241, 245, 249)); // Slate-100
        pnData.setBackground(new Color(241, 245, 249));

        // Header styling (tràn viền)
        pnHeader.setBackground(new Color(0, 153, 204));
        pnHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); // Loại bỏ viền
        lblApplication.setFont(new Font("Arial", Font.BOLD, 24));
        lblApplication.setForeground(Color.WHITE);
        userInforPanel.setBackground(new Color(0, 153, 204));
        lblWelcomeUser.setFont(new Font("Arial", Font.PLAIN, 16));
        lblWelcomeUser.setForeground(Color.WHITE);

        // Sidebar styling (tràn viền)
        pnSideBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        sidebarPanel.setBackground(sidebarBgColor);
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        JButton[] sidebarButtons = {btnHome, btnBook, btnInventory, btnCustomer, btnOrder, btnUser, btnRevenue};
        for (JButton button : sidebarButtons) {
            button.setFont(new Font("Arial", Font.BOLD, 16));
            button.setBackground(sidebarBgColor);
            button.setForeground(sidebarDefaultFgColor);
            button.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20)); // Vẫn giữ padding để tạo khoảng cách bên trong
            button.setFocusPainted(false);
            button.setOpaque(true);
            button.setBorderPainted(false); // Loại bỏ viền nút
            button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
            button.setPreferredSize(new java.awt.Dimension(250, 50)); // Đảm bảo nút có chiều cao hợp lý
        }
        
        // Tùy chỉnh màu cho nút Đăng xuất để nổi bật
        btnLogout.setBackground(new Color(239, 68, 68)); // Red-500
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFont(new Font("Arial", Font.BOLD, 16));
        btnLogout.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        btnLogout.setFocusPainted(false);
        btnLogout.setOpaque(true);
        btnLogout.setBorderPainted(false);
        btnLogout.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnLogout.setPreferredSize(new java.awt.Dimension(250, 50));
    }
    
    private void showPanel(String panelName, JButton clickedButton) {
        setActiveButton(clickedButton);
        contentSlider.showPanel(contentPanels.get(panelName));
    }
    
     private void setActiveButton(JButton button) {
        if (activeButton != null) {
            activeButton.setBackground(defaultButtonColor);
        }
        activeButton = button;
        if (activeButton != null) {
            activeButton.setBackground(selectedColor);
        }
    }
    
    private void addHoverEffect(JButton button) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button != activeButton) {
                    button.setBackground(hoverColor);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button != activeButton) {
                    button.setBackground(sidebarBgColor);
                }
            }
        });
    }

    public void setWelcomeMessage(String fullName) {
        lblWelcomeUser.setText("Xin chào, " + fullName);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnSideBar = new javax.swing.JPanel();
        sidebarPanel = new javax.swing.JPanel();
        btnHome = new javax.swing.JButton();
        btnBook = new javax.swing.JButton();
        btnCustomer = new javax.swing.JButton();
        btnInventory = new javax.swing.JButton();
        btnPromotion = new javax.swing.JButton();
        btnOrder = new javax.swing.JButton();
        btnUser = new javax.swing.JButton();
        btnRevenue = new javax.swing.JButton();
        pnHeader = new javax.swing.JPanel();
        lblApplication = new javax.swing.JLabel();
        userInforPanel = new javax.swing.JPanel();
        lblWelcomeUser = new javax.swing.JLabel();
        btnLogout = new javax.swing.JButton();
        pnData = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        sidebarPanel.setBackground(new java.awt.Color(204, 255, 255));
        sidebarPanel.setPreferredSize(new java.awt.Dimension(250, 586));
        sidebarPanel.setLayout(new java.awt.GridLayout(0, 1));

        btnHome.setBackground(new java.awt.Color(153, 204, 255));
        btnHome.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        btnHome.setForeground(new java.awt.Color(0, 0, 0));
        btnHome.setText("HOME");
        btnHome.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        btnHome.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnHome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHomeActionPerformed(evt);
            }
        });
        sidebarPanel.add(btnHome);

        btnBook.setBackground(new java.awt.Color(153, 204, 255));
        btnBook.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        btnBook.setForeground(new java.awt.Color(0, 0, 0));
        btnBook.setText("BOOK MANAGEMENT");
        btnBook.setAlignmentY(0.0F);
        btnBook.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        btnBook.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnBook.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBookActionPerformed(evt);
            }
        });
        sidebarPanel.add(btnBook);

        btnCustomer.setBackground(new java.awt.Color(153, 204, 255));
        btnCustomer.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        btnCustomer.setForeground(new java.awt.Color(0, 0, 0));
        btnCustomer.setText("CUSTOMER MANAGEMENT");
        btnCustomer.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        btnCustomer.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCustomerActionPerformed(evt);
            }
        });
        sidebarPanel.add(btnCustomer);

        btnInventory.setBackground(new java.awt.Color(153, 204, 255));
        btnInventory.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        btnInventory.setForeground(new java.awt.Color(0, 0, 0));
        btnInventory.setText("INVENTORY MANAGEMENT");
        btnInventory.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        btnInventory.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnInventory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInventoryActionPerformed(evt);
            }
        });
        sidebarPanel.add(btnInventory);

        btnPromotion.setBackground(new java.awt.Color(153, 204, 255));
        btnPromotion.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        btnPromotion.setForeground(new java.awt.Color(0, 0, 0));
        btnPromotion.setText("PROMOTION MANAGEMENT");
        btnPromotion.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        btnPromotion.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnPromotion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPromotionActionPerformed(evt);
            }
        });
        sidebarPanel.add(btnPromotion);

        btnOrder.setBackground(new java.awt.Color(153, 204, 255));
        btnOrder.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        btnOrder.setForeground(new java.awt.Color(0, 0, 0));
        btnOrder.setText("ORDER MANAGEMENT");
        btnOrder.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        btnOrder.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOrderActionPerformed(evt);
            }
        });
        sidebarPanel.add(btnOrder);

        btnUser.setBackground(new java.awt.Color(153, 204, 255));
        btnUser.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        btnUser.setForeground(new java.awt.Color(0, 0, 0));
        btnUser.setText("USER MANAGEMENT");
        btnUser.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        btnUser.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUserActionPerformed(evt);
            }
        });
        sidebarPanel.add(btnUser);

        btnRevenue.setBackground(new java.awt.Color(153, 204, 255));
        btnRevenue.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        btnRevenue.setForeground(new java.awt.Color(0, 0, 0));
        btnRevenue.setText("REVENUE");
        btnRevenue.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        btnRevenue.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRevenue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRevenueActionPerformed(evt);
            }
        });
        sidebarPanel.add(btnRevenue);

        pnSideBar.add(sidebarPanel);

        add(pnSideBar, java.awt.BorderLayout.WEST);

        pnHeader.setBackground(new java.awt.Color(0, 153, 204));
        pnHeader.setLayout(new java.awt.BorderLayout());

        lblApplication.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        lblApplication.setText("BOOK MANAGEMENT");
        pnHeader.add(lblApplication, java.awt.BorderLayout.CENTER);

        userInforPanel.setOpaque(false);

        lblWelcomeUser.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N
        lblWelcomeUser.setText("Welcome: ");
        userInforPanel.add(lblWelcomeUser);

        btnLogout.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        btnLogout.setText("LOG OUT");
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });
        userInforPanel.add(btnLogout);

        pnHeader.add(userInforPanel, java.awt.BorderLayout.LINE_END);

        add(pnHeader, java.awt.BorderLayout.NORTH);

        pnData.setBackground(new java.awt.Color(102, 102, 102));
        pnData.setLayout(new java.awt.CardLayout());
        add(pnData, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void btnHomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHomeActionPerformed
        // TODO add your handling code here:
        contentSlider.showPanel(contentPanels.get("home"));
        setActiveButton(btnHome);
    }//GEN-LAST:event_btnHomeActionPerformed

    private void btnBookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBookActionPerformed
        // TODO add your handling code here:
        contentSlider.showPanel(contentPanels.get("book"));
        setActiveButton(btnBook);
    }//GEN-LAST:event_btnBookActionPerformed

    private void btnCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCustomerActionPerformed
        // TODO add your handling code here:
        contentSlider.showPanel(contentPanels.get("customer"));
        setActiveButton(btnCustomer);
    }//GEN-LAST:event_btnCustomerActionPerformed

    private void btnInventoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInventoryActionPerformed
        // TODO add your handling code here:
        contentSlider.showPanel(contentPanels.get("inventory"));
        setActiveButton(btnInventory);
    }//GEN-LAST:event_btnInventoryActionPerformed

    private void btnOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOrderActionPerformed
        // TODO add your handling code here:
        contentSlider.showPanel(contentPanels.get("order"));
        setActiveButton(btnOrder);
    }//GEN-LAST:event_btnOrderActionPerformed

    private void btnUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUserActionPerformed
        // TODO add your handling code here:
        contentSlider.showPanel(contentPanels.get("user"));
        setActiveButton(btnUser);
    }//GEN-LAST:event_btnUserActionPerformed

    private void btnRevenueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRevenueActionPerformed
        // TODO add your handling code here:
        contentSlider.showPanel(contentPanels.get("revenue"));
        setActiveButton(btnRevenue);
    }//GEN-LAST:event_btnRevenueActionPerformed

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        // TODO add your handling code here:
        AuthService authService = new AuthService();
        authService.logout();
        mainFrame.showLoginPanel();
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void btnPromotionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPromotionActionPerformed
        // TODO add your handling code here:
        contentSlider.showPanel(contentPanels.get("promotion"));
        setActiveButton(btnPromotion);
    }//GEN-LAST:event_btnPromotionActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBook;
    private javax.swing.JButton btnCustomer;
    private javax.swing.JButton btnHome;
    private javax.swing.JButton btnInventory;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnOrder;
    private javax.swing.JButton btnPromotion;
    private javax.swing.JButton btnRevenue;
    private javax.swing.JButton btnUser;
    private javax.swing.JLabel lblApplication;
    private javax.swing.JLabel lblWelcomeUser;
    private javax.swing.JPanel pnData;
    private javax.swing.JPanel pnHeader;
    private javax.swing.JPanel pnSideBar;
    private javax.swing.JPanel sidebarPanel;
    private javax.swing.JPanel userInforPanel;
    // End of variables declaration//GEN-END:variables
}
