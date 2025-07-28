/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.bookmanagement.view;

import com.bookmanagement.model.UserSession;
import com.bookmanagement.service.AuthService;
import java.awt.CardLayout;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public class HomePanel extends javax.swing.JPanel {

    /**
     * Creates new form HomePanel
     */
    
    CardLayout dataPannelLayout;
    private UserSession currentSession;
    private UserSession session;
    private MainFrame mainFrame;
    
    public HomePanel(MainFrame mf) {
        this.mainFrame = mf;
        setSize(1200, 700);
        initComponents();
        
        dataPannelLayout = (CardLayout) dataPannel.getLayout();
        dataPannel.add(new Home(), "Home");
        dataPannel.add(new BookManagementPanel(), "BookManagement");
        dataPannel.add(new InventoryManagementPanel(), "InventoryManagement");
        dataPannel.add(new CustomerManagementPanel(), "CustomerManagement");
        dataPannel.add(new OrderManagementPanel(), "OrderManagement");
        dataPannel.add(new UserManagementPanel(), "UserManagement");

        // 3. Gắn ActionListener cho các nút Sidebar
        btnHome.addActionListener(e -> dataPannelLayout.show(dataPannel, "Home"));
        btnBook.addActionListener(e -> dataPannelLayout.show(dataPannel, "BookManagement"));
        btnInventory.addActionListener(e -> dataPannelLayout.show(dataPannel, "InventoryManagement"));
        btnCustomer.addActionListener(e -> dataPannelLayout.show(dataPannel, "CustomerManagement"));
        btnOrder.addActionListener(e -> dataPannelLayout.show(dataPannel, "OrderManagement"));
        btnUser.addActionListener(e -> dataPannelLayout.show(dataPannel, "UserManagement"));

        // 5. Hiển thị màn hình "Trang Chủ" mặc định khi khởi động
        setAllManagementButtonsVisible(false);
        btnHome.setVisible(true);
        System.out.println("HomePanel constructor finished.");
    }
    
    public void setSessionAndPermissions(UserSession session) {
        System.out.println("HomePanel.setSessionAndPermissions called. Session hash: " + (session != null ? session.hashCode() : "null"));
        this.currentSession = session;
        if (session != null && session.getCurrentUser() != null) {
            String userName = session.getCurrentUser().getFullName();
            System.out.println("User is logged in. Full name: " + userName);
            lblWelcomeUser.setText("Xin chào, " + userName + "!");
            updateUIPermissions(); // Cập nhật trạng thái các nút
        } else {
            System.out.println("User session is null or current user is null. Resetting UI.");
            lblWelcomeUser.setText("Xin chào!");
            // Nếu không có session, ẩn tất cả các nút quản lý
            setAllManagementButtonsVisible(false);
        }
    }
    
    private void updateUIPermissions() {
        System.out.println("HomePanel.updateUIPermissions called.");
        if (currentSession == null) {
            System.out.println("currentSession is null in updateUIPermissions. Hiding all buttons.");
            setAllManagementButtonsVisible(false);
            return;
        }

        // Ẩn tất cả các nút trước khi kiểm tra quyền
        setAllManagementButtonsVisible(false);
        System.out.println("Permissions for current user:");
        List<String> permissions = currentSession.getPermissions();
        if (permissions != null) {
            for (String perm : permissions) {
                System.out.println("- " + perm);
            }
        } else {
            System.out.println("- No permissions found.");
        }


        // Hiển thị các nút dựa trên quyền hạn của người dùng
        // Sử dụng các tên quyền hạn đúng như trong DataBookManagement.sql
        btnBook.setVisible(currentSession.hasPermission("QUANLY_SACH"));
        btnInventory.setVisible(currentSession.hasPermission("QUANLY_KHO"));
        btnCustomer.setVisible(currentSession.hasPermission("QUANLY_KHACHHANG"));
        btnOrder.setVisible(currentSession.hasPermission("QUANLY_DONHANG") || currentSession.hasPermission("TAO_DONHANG"));
        btnUser.setVisible(currentSession.hasPermission("QUANLY_NGUOIDUNG"));
        // Nút Home luôn hiển thị
        btnHome.setVisible(true);
        System.out.println("UI permissions updated.");
    }
    
    // Phương thức trợ giúp để ẩn/hiện tất cả các nút quản lý
    private void setAllManagementButtonsVisible(boolean visible) {
        btnBook.setVisible(visible);
        btnInventory.setVisible(visible);
        btnCustomer.setVisible(visible);
        btnOrder.setVisible(visible);
        btnUser.setVisible(visible);
    }

    private void logout() {
        AuthService.logout(); // Kết thúc session thông qua AuthService
        mainFrame.showLogin(); // Chuyển về màn hình đăng nhập
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnMainScreen = new javax.swing.JPanel();
        sidebarPanel = new javax.swing.JPanel();
        btnHome = new javax.swing.JButton();
        btnBook = new javax.swing.JButton();
        btnCustomer = new javax.swing.JButton();
        btnInventory = new javax.swing.JButton();
        btnOrder = new javax.swing.JButton();
        btnUser = new javax.swing.JButton();
        dataPannel = new javax.swing.JPanel();
        pnHeader = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        userInforPanel = new javax.swing.JPanel();
        lblWelcomeUser = new javax.swing.JLabel();
        btnLogout = new javax.swing.JToggleButton();
        pnContent = new javax.swing.JPanel();

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

        pnMainScreen.add(sidebarPanel);

        dataPannel.setBackground(new java.awt.Color(102, 102, 102));
        dataPannel.setLayout(new java.awt.CardLayout());
        pnMainScreen.add(dataPannel);

        add(pnMainScreen, java.awt.BorderLayout.WEST);

        pnHeader.setBackground(new java.awt.Color(0, 153, 204));
        pnHeader.setLayout(new java.awt.BorderLayout());

        jLabel1.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel1.setText("BOOK MANAGEMENT");
        pnHeader.add(jLabel1, java.awt.BorderLayout.CENTER);

        userInforPanel.setOpaque(false);

        lblWelcomeUser.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N
        lblWelcomeUser.setText("Welcome: ");
        userInforPanel.add(lblWelcomeUser);

        btnLogout.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnLogout.setText("LOG OUT");
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });
        userInforPanel.add(btnLogout);

        pnHeader.add(userInforPanel, java.awt.BorderLayout.LINE_END);

        add(pnHeader, java.awt.BorderLayout.NORTH);

        pnContent.setBackground(new java.awt.Color(102, 102, 102));

        javax.swing.GroupLayout pnContentLayout = new javax.swing.GroupLayout(pnContent);
        pnContent.setLayout(pnContentLayout);
        pnContentLayout.setHorizontalGroup(
            pnContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 579, Short.MAX_VALUE)
        );
        pnContentLayout.setVerticalGroup(
            pnContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 620, Short.MAX_VALUE)
        );

        add(pnContent, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void btnHomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHomeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnHomeActionPerformed

    private void btnBookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBookActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnBookActionPerformed

    private void btnCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCustomerActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCustomerActionPerformed

    private void btnInventoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInventoryActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnInventoryActionPerformed

    private void btnOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOrderActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnOrderActionPerformed

    private void btnUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUserActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnUserActionPerformed

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        // TODO add your handling code here:
        logout();
    }//GEN-LAST:event_btnLogoutActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBook;
    private javax.swing.JButton btnCustomer;
    private javax.swing.JButton btnHome;
    private javax.swing.JButton btnInventory;
    private javax.swing.JToggleButton btnLogout;
    private javax.swing.JButton btnOrder;
    private javax.swing.JButton btnUser;
    private javax.swing.JPanel dataPannel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lblWelcomeUser;
    private javax.swing.JPanel pnContent;
    private javax.swing.JPanel pnHeader;
    private javax.swing.JPanel pnMainScreen;
    private javax.swing.JPanel sidebarPanel;
    private javax.swing.JPanel userInforPanel;
    // End of variables declaration//GEN-END:variables
}
