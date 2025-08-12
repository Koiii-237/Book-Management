/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.bookmanagement.view;

/**
 *
 * @author ADMIN
 */
import com.bookmanagement.Dao.UserDAO;
import com.bookmanagement.model.User;
import javax.swing.JOptionPane;
import java.awt.Frame;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class UserManagementDialog extends javax.swing.JDialog {

    private static final Logger LOGGER = Logger.getLogger(UserManagementDialog.class.getName());
    private UserDAO userDAO;
    private User currentUser; // Đối tượng User hiện tại (nếu đang sửa)
    private boolean isEditMode; // true nếu đang ở chế độ sửa, false nếu thêm mới
    private boolean isSucceeded = false;

    /**
     * Creates new form UserManagementDialog
     *
     * @param parent Frame cha của dialog.
     * @param user Đối tượng User để chỉnh sửa (null nếu thêm mới).
     * @param isEditMode true nếu là chế độ chỉnh sửa, false nếu thêm mới.
     */
    public UserManagementDialog(Frame parent, boolean isEditMode, User user) {
        super(parent, true); // Luôn là modal
        initComponents();
        this.userDAO = new UserDAO();
        this.currentUser = user;
        this.isEditMode = isEditMode;
        initDialogState();
        this.setLocationRelativeTo(parent); // Canh giữa dialog
    }

    public boolean isSucceeded() {
        return isSucceeded;
    }

    private void initDialogState() {
        if (isEditMode && currentUser != null) {
            setTitle("SỬA THÔNG TIN NGƯỜI DÙNG");
            txtUserId.setText(String.valueOf(currentUser.getUserId()));
            txtUserId.setEditable(false);
            txtUserName.setText(currentUser.getUsername());
            txtPassWord.setText("");
            txtPassWord.setEditable(false);
            lblPassWord.setEnabled(false);
            txtEmail.setText(currentUser.getEmail());
            jdcCreateAt.setDate(Date.from(currentUser.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()));
            jdcCreateAt.setEnabled(false);
        } else {
            setTitle("THÊM MỚI NGƯỜI DÙNG");
            txtUserId.setText("Tự động");
            txtUserId.setEditable(false); 
            jdcCreateAt.setDate(new Date());
            jdcCreateAt.setEnabled(false);
        }
    }

    private void handleSave() {
        String username = txtUserName.getText().trim();
        String password = new String(txtPassWord.getPassword()).trim();
        String email = txtEmail.getText().trim();

        // Kiểm tra dữ liệu bắt buộc
        if (username.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên đăng nhập và Email không được để trống.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (isEditMode) {
            // Chế độ CẬP NHẬT người dùng
            currentUser.setUsername(username);
            currentUser.setEmail(email);
            
            // Cập nhật mật khẩu nếu có mật khẩu mới được nhập
            if (!password.isEmpty()) {
                // Cập nhật mật khẩu trực tiếp, không hash
                currentUser.setPasswordHash(password);
                if (userDAO.updateUser(currentUser)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật người dùng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    isSucceeded = true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật người dùng thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                try {
                    // Nếu mật khẩu để trống, chỉ cập nhật các thông tin khác
                    if (userDAO.updateUserWithoutPassword(currentUser)) {
                        JOptionPane.showMessageDialog(this, "Cập nhật thông tin người dùng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        isSucceeded = true;
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Cập nhật thông tin người dùng thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(UserManagementDialog.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            try {
                // Chế độ THÊM MỚI người dùng
                if (password.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Mật khẩu không được để trống khi thêm mới.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Kiểm tra tên đăng nhập đã tồn tại chưa
                if (userDAO.isUsernameExists(username)) {
                    JOptionPane.showMessageDialog(this, "Tên đăng nhập đã tồn tại. Vui lòng chọn tên khác.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Tạo đối tượng User mới và thêm vào CSDL
                User newUser = new User(0, username, password, email, LocalDateTime.now());
                if (userDAO.addUser(newUser)) {
                    JOptionPane.showMessageDialog(this, "Thêm người dùng mới thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    isSucceeded = true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Thêm người dùng mới thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                Logger.getLogger(UserManagementDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        formPanel = new javax.swing.JPanel();
        lblUserName = new javax.swing.JLabel();
        lblUserId = new javax.swing.JLabel();
        lblPassWord = new javax.swing.JLabel();
        lblEmail = new javax.swing.JLabel();
        txtUserId = new javax.swing.JTextField();
        txtUserName = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();
        txtPassWord = new javax.swing.JPasswordField();
        lblDateCreate = new javax.swing.JLabel();
        jdcCreateAt = new com.toedter.calendar.JDateChooser();
        pnButton = new javax.swing.JPanel();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ADD/UPDATE USER INFORMATION");
        setResizable(false);

        formPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        formPanel.setLayout(new java.awt.GridBagLayout());

        lblUserName.setText("USER NAME: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(lblUserName, gridBagConstraints);

        lblUserId.setText("USER ID: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(lblUserId, gridBagConstraints);

        lblPassWord.setText("PASSWORD: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(lblPassWord, gridBagConstraints);

        lblEmail.setText("EMAIL: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(lblEmail, gridBagConstraints);

        txtUserId.setEditable(false);
        txtUserId.setText("AUTO GENERATE");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(txtUserId, gridBagConstraints);

        txtUserName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUserNameActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(txtUserName, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(txtEmail, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(txtPassWord, gridBagConstraints);

        lblDateCreate.setText("Date Create: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(lblDateCreate, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(jdcCreateAt, gridBagConstraints);

        getContentPane().add(formPanel, java.awt.BorderLayout.CENTER);

        pnButton.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        btnSave.setText("SAVE");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        pnButton.add(btnSave);

        btnCancel.setText("CANCEL");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        pnButton.add(btnCancel);

        getContentPane().add(pnButton, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtUserNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUserNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUserNameActionPerformed

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchActionPerformed

    private void tblBookMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblBookMouseClicked

    }//GEN-LAST:event_tblBookMouseClicked

    private void btnTimKiemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTimKiemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnTimKiemActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        handleSave();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        // TODO add your handling code here:
        dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

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
            java.util.logging.Logger.getLogger(UserManagementDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(UserManagementDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(UserManagementDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UserManagementDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                UserManagementDialog dialog = new UserManagementDialog(new javax.swing.JFrame(), true, null);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnSave;
    private javax.swing.JPanel formPanel;
    private com.toedter.calendar.JDateChooser jdcCreateAt;
    private javax.swing.JLabel lblDateCreate;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblPassWord;
    private javax.swing.JLabel lblUserId;
    private javax.swing.JLabel lblUserName;
    private javax.swing.JPanel pnButton;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JPasswordField txtPassWord;
    private javax.swing.JTextField txtUserId;
    private javax.swing.JTextField txtUserName;
    // End of variables declaration//GEN-END:variables
}
