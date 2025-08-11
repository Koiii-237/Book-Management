/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.bookmanagement.view;

import com.bookmanagement.model.User;
import com.bookmanagement.model.UserSession;
import com.bookmanagement.service.AuthService;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
/**
 *
 * @author ADMIN
 */
public class LoginPanel extends javax.swing.JPanel {

    /**
     * Creates new form LoginPanel
     */
    private static final Logger LOGGER = Logger.getLogger(LoginPanel.class.getName());

    private MainFrame mainFrame;
    private final Color primaryColor = new Color(79, 70, 229);  // Indigo-600
    private final Color primaryHoverColor = new Color(67, 56, 202); // Indigo-700
    private final Color secondaryColor = new Color(243, 244, 246); // Gray-100
    private final Color textColor = new Color(55, 65, 81); // Gray-700
    private final Color borderColor = new Color(209, 213, 219); // Gray-300
    private final Color errorColor = new Color(239, 68, 68); // Red-500
    private final Color successColor = new Color(34, 197, 94);

    public LoginPanel(MainFrame mf) {
        this.mainFrame = mf;
        initComponents();
        applyCustomStyles();
    }
    
    public void clearFields() {
        txtUserName.setText("");
        txtPassword.setText("");
        lblDisplayResult.setText(""); // Xóa cả thông báo lỗi nếu có
    }
    
    private void performLogin() {
        String username = txtUserName.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            lblDisplayResult.setText("Vui lòng nhập đủ tên đăng nhập và mật khẩu.");
            lblDisplayResult.setForeground(errorColor);
            return;
        }

        System.out.println("Attempting login for user: " + username);

        // Gọi AuthService để đăng nhập. Phương thức login giờ trả về boolean.
        AuthService authService = new AuthService();
        boolean loginSuccess = authService.login(username, password);

        if (loginSuccess) {
            System.out.println("Login successful.");
            lblDisplayResult.setForeground(successColor);

            // Lấy User từ UserSession tĩnh sau khi đăng nhập thành công
            User currentUser = UserSession.getCurrentUser();

            if (currentUser != null) {
                // Cập nhật thông tin người dùng trong HomePanel
                HomePanel home = mainFrame.getHomePanel();
                if (home != null) {
                    home.setWelcomeMessage(currentUser.getUsername());
                    // GỌI PHƯƠNG THỨC NÀY để chuyển đổi màn hình
                    mainFrame.showHomePanel();
                }
            } else {
                lblDisplayResult.setText("Lỗi hệ thống: Không thể lấy thông tin người dùng.");
            }
        } else {
            lblDisplayResult.setText("Tên đăng nhập hoặc mật khẩu không đúng.");
            LOGGER.warning("Login failed for user: " + username);
        }
    }
    
    private void applyCustomStyles() {
        // Cài đặt màu nền cho panel chính
        this.setBackground(secondaryColor);
        
        // Cài đặt font và màu cho tiêu đề
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitle.setForeground(primaryColor);
        
        // Cài đặt font và màu cho các nhãn
        lblUserName.setFont(new Font("Arial", Font.PLAIN, 16));
        lblUserName.setForeground(textColor);
        lblPassword.setFont(new Font("Arial", Font.PLAIN, 16));
        lblPassword.setForeground(textColor);
        
        // Tùy chỉnh ô nhập liệu
        txtUserName.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 1, true),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 1, true),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        txtUserName.setPreferredSize(new Dimension(250, 35));
        txtPassword.setPreferredSize(new Dimension(250, 35));
        txtUserName.setFont(new Font("Arial", Font.PLAIN, 16));
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 16));

        // Tùy chỉnh nút Đăng nhập
        btnLogin.setFont(new Font("Arial", Font.BOLD, 16));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBackground(primaryColor);
        btnLogin.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnLogin.setFocusPainted(false);
        btnLogin.setOpaque(true);
        btnLogin.setBorderPainted(false);
        btnLogin.setPreferredSize(new Dimension(150, 40));

        // Thêm hiệu ứng di chuột
        btnLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnLogin.setBackground(primaryHoverColor);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnLogin.setBackground(primaryColor);
            }
        });
        
        // Cài đặt nhãn thông báo kết quả
        lblDisplayResult.setFont(new Font("Arial", Font.PLAIN, 14));
        lblDisplayResult.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
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

        pnLogin = new javax.swing.JPanel();
        txtPassword = new javax.swing.JPasswordField();
        btnLogin = new javax.swing.JButton();
        txtUserName = new javax.swing.JTextField();
        lblDisplayResult = new javax.swing.JLabel();
        lblTitle = new javax.swing.JLabel();
        lblPassword = new javax.swing.JLabel();
        lblUserName = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        pnLogin.setBorder(javax.swing.BorderFactory.createEmptyBorder(25, 25, 25, 25));
        pnLogin.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        pnLogin.add(txtPassword, gridBagConstraints);

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
        pnLogin.add(btnLogin, gridBagConstraints);

        txtUserName.setColumns(20);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        pnLogin.add(txtUserName, gridBagConstraints);

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
        pnLogin.add(lblDisplayResult, gridBagConstraints);

        lblTitle.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setText("LOGIN TO SYSTEM");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 25, 0);
        pnLogin.add(lblTitle, gridBagConstraints);

        lblPassword.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N
        lblPassword.setText("PASSWORD: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        pnLogin.add(lblPassword, gridBagConstraints);

        lblUserName.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N
        lblUserName.setText("USER NAME: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        pnLogin.add(lblUserName, gridBagConstraints);

        add(pnLogin, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed
        // TODO add your handling code here:
        performLogin();
    }//GEN-LAST:event_btnLoginActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLogin;
    private javax.swing.JLabel lblDisplayResult;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblUserName;
    private javax.swing.JPanel pnLogin;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUserName;
    // End of variables declaration//GEN-END:variables
}
