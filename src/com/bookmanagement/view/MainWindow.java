/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.bookmanagement.view;

import com.bookmanagement.Dao.UserDAO;
import com.bookmanagement.model.User;
import com.bookmanagement.model.UserSession;
import com.bookmanagement.service.AuthService;
import com.bookmanagement.view.BookManagementPanel;
import com.bookmanagement.view.CustomerManagementPanel;
import com.bookmanagement.view.HomePanel;
import com.bookmanagement.view.InventoryManagementPanel;
import com.bookmanagement.view.Login;
import com.bookmanagement.view.OrderManagementPanel;
import com.bookmanagement.view.UserManagementPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

/**
 *
 * @author ADMIN
 */
public class MainWindow extends javax.swing.JFrame {

    /**
     * Creates new form Main_Interface
     */
    CardLayout dataPannelLayout;
    CardLayout containerPannelLayout;

    private UserSession currentSession;
    private static String LOGIN_SCREEN_NAME = "pnLoginScreen";
    private static String MAIN_SCREEN_NAME = "pnMainScreen";
    private UserSession session;

    public MainWindow() {
        initComponents();
        setLocationRelativeTo(null);
        setSize(1200, 700);
        
        containerPannelLayout = (CardLayout) containerPannel.getLayout();
        containerPannel.add(pnLoginScreen, LOGIN_SCREEN_NAME);
        containerPannel.add(pnMainScreen, MAIN_SCREEN_NAME);

        // 2. Thêm các Panel chức năng vào mainContentPane
        // Mỗi màn hình chức năng sẽ là một thể hiện của JPanel Form bạn tạo riêng
        dataPannelLayout = (CardLayout) dataPannel.getLayout();
        dataPannel.add(new HomePanel(), "Home");
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
        containerPannelLayout.show(containerPannel, LOGIN_SCREEN_NAME);
    }

    
    private void performLogin() {
        String username = txtUserName.getText().trim();
        String password = new String(txtPassword.getPassword());

        // Kiểm tra login (giả sử login thành công)
        this.session = AuthService.login(username, password); // Hoặc code tự tạo UserSession

        if (this.session != null) {
            // swap in the pannel according to role
            this.buildMenu();
            containerPannelLayout.show(containerPannel, MAIN_SCREEN_NAME);
        } else {
            JOptionPane.showMessageDialog(this, "Sai tên đăng nhập hoặc mật khẩu");
        }
    }

    private void applyPermissions() {
        if (currentSession == null) {
            return;
        }

        List<String> permissions = currentSession.getPermissions();

        // Map quyền mã sang mô tả
        boolean canManageBook = permissions.contains("PERM001"); // QUANLY_SACH
        boolean canManageInventory = permissions.contains("PERM002"); // QUANLY_KHO
        boolean canPrintCustomer = permissions.contains("PERM003"); // QUANLY_KHACHHANG
        boolean canCreateOrder = permissions.contains("PERM004"); // TAO_DONHANG
        boolean canManageOrder = permissions.contains("PERM005"); // QUANLY_DONHANG
        boolean canManagePrint = permissions.contains("PERM006"); // IN_HOADON
        boolean canManageUser = permissions.contains("PERM007"); // QUANLY_NGUOIDUNG

        btnCustomer.setVisible(canPrintCustomer);
        btnOrder.setVisible(canManageOrder || canCreateOrder || canManagePrint);
        btnInventory.setVisible(canManageInventory);
        btnBook.setVisible(canManageBook);
        btnUser.setVisible(canManageUser);
    }

    private void logout() {
        this.dispose();
        java.awt.EventQueue.invokeLater(() -> new Login().setVisible(true));
    }

    public void setSessionAndPermissions(UserSession session) {
        this.currentSession = session;
        lblWelcomeUser.setText("Welcome: " + session.getUser().getUserName());

        applyPermissions();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pnHeader = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        userInforPanel = new javax.swing.JPanel();
        lblWelcomeUser = new javax.swing.JLabel();
        btnLogout = new javax.swing.JToggleButton();
        containerPannel = new javax.swing.JPanel();
        pnLoginScreen = new javax.swing.JPanel();
        txtPassword = new javax.swing.JPasswordField();
        btnLogin = new javax.swing.JButton();
        txtUserName = new javax.swing.JTextField();
        lblDisplayResult = new javax.swing.JLabel();
        lblTitle = new javax.swing.JLabel();
        lblPassword = new javax.swing.JLabel();
        lblUserName = new javax.swing.JLabel();
        pnMainScreen = new javax.swing.JPanel();
        sidebarPanel = new javax.swing.JPanel();
        btnHome = new javax.swing.JButton();
        btnBook = new javax.swing.JButton();
        btnCustomer = new javax.swing.JButton();
        btnInventory = new javax.swing.JButton();
        btnOrder = new javax.swing.JButton();
        btnUser = new javax.swing.JButton();
        dataPannel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("BOOK MANAGEMENT");
        setAlwaysOnTop(true);
        setMinimumSize(new java.awt.Dimension(800, 500));

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

        getContentPane().add(pnHeader, java.awt.BorderLayout.PAGE_START);

        containerPannel.setLayout(new java.awt.CardLayout());

        pnLoginScreen.setBorder(javax.swing.BorderFactory.createEmptyBorder(25, 25, 25, 25));
        pnLoginScreen.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        pnLoginScreen.add(txtPassword, gridBagConstraints);

        btnLogin.setText("LOGIN");
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        pnLoginScreen.add(btnLogin, gridBagConstraints);

        txtUserName.setColumns(20);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        pnLoginScreen.add(txtUserName, gridBagConstraints);

        lblDisplayResult.setForeground(new java.awt.Color(255, 0, 0));
        lblDisplayResult.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        pnLoginScreen.add(lblDisplayResult, gridBagConstraints);

        lblTitle.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setText("LOGIN TO SYSTEM");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 25, 0);
        pnLoginScreen.add(lblTitle, gridBagConstraints);

        lblPassword.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N
        lblPassword.setText("PASSWORD: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        pnLoginScreen.add(lblPassword, gridBagConstraints);

        lblUserName.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N
        lblUserName.setText("USER NAME: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        pnLoginScreen.add(lblUserName, gridBagConstraints);

        containerPannel.add(pnLoginScreen, "card2");

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

        containerPannel.add(pnMainScreen, "card2");

        getContentPane().add(containerPannel, java.awt.BorderLayout.LINE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        // TODO add your handling code here:
        logout();
    }//GEN-LAST:event_btnLogoutActionPerformed

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

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed
        // TODO add your handling code here:
        performLogin();
    }//GEN-LAST:event_btnLoginActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBook;
    private javax.swing.JButton btnCustomer;
    private javax.swing.JButton btnHome;
    private javax.swing.JButton btnInventory;
    private javax.swing.JButton btnLogin;
    private javax.swing.JToggleButton btnLogout;
    private javax.swing.JButton btnOrder;
    private javax.swing.JButton btnUser;
    private javax.swing.JPanel containerPannel;
    private javax.swing.JPanel dataPannel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lblDisplayResult;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblUserName;
    private javax.swing.JLabel lblWelcomeUser;
    private javax.swing.JPanel pnHeader;
    private javax.swing.JPanel pnLoginScreen;
    private javax.swing.JPanel pnMainScreen;
    private javax.swing.JPanel sidebarPanel;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUserName;
    private javax.swing.JPanel userInforPanel;
    // End of variables declaration//GEN-END:variables

    private void buildMenu() {
        if (this.session.role == Role.ADMIN) {
            
        }
    }
}
