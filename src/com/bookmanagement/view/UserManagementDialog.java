/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.bookmanagement.view;

/**
 *
 * @author ADMIN
 */
import com.bookmanagement.Dao.UserDAO; // Giả định bạn có UserDAO
import com.bookmanagement.model.User; // Giả định bạn có lớp User model
import javax.swing.JOptionPane;
import java.awt.Frame;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;

public class UserManagementDialog extends javax.swing.JDialog {

    private static final Logger LOGGER = Logger.getLogger(UserManagementDialog.class.getName());
    private UserDAO userDAO;
    private User currentUser; // Đối tượng User hiện tại (nếu đang sửa)
    private boolean isEditMode; // true nếu đang ở chế độ sửa, false nếu thêm mới
    private boolean isSucceeded = false; // Biến để kiểm tra thao tác có thành công không

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
        this.userDAO = new UserDAO(); // Khởi tạo UserDAO
        this.currentUser = user;
        this.isEditMode = isEditMode;
        initDialogState(); // Thiết lập trạng thái ban đầu của dialog
        this.setLocationRelativeTo(parent); // Canh giữa dialog
    }

    public boolean isSucceeded() {
        return isSucceeded;
    }

    // --- Phương thức thiết lập trạng thái ban đầu của dialog ---
    private void initDialogState() {
        if (isEditMode && currentUser != null) {
            setTitle("SỬA THÔNG TIN NGƯỜI DÙNG");
            txtUserId.setText(currentUser.getUserID());
            txtUserId.setEditable(false); // Không cho phép sửa ID
            txtUserName.setText(currentUser.getUserName());
            txtUserName.setEditable(false); // Không cho phép sửa tên đăng nhập khi sửa
            txtPassWord.setText(""); // Để trống, chỉ nhập khi muốn đổi
            txtConfirmPassword.setText(""); // Để trống, chỉ nhập khi muốn đổi
            txtFullName.setText(currentUser.getFullName());
            txtEmail.setText(currentUser.getEmail());

            // Thiết lập checkbox vai trò
            chkAdmin.setSelected(currentUser.getRoles().contains("Admin"));
            chkUser.setSelected(currentUser.getRoles().contains("User"));
            chkManager.setSelected(currentUser.getRoles().contains("Manager"));

        } else {
            setTitle("THÊM MỚI THÔNG TIN NGƯỜI DÙNG");
            txtUserId.setText("(AUTO GEN / Không sửa)");
            txtUserId.setEditable(false); // Luôn không cho phép sửa ID
            txtUserName.setText("");
            txtUserName.setEditable(true); // Cho phép nhập tên đăng nhập khi thêm mới
            txtPassWord.setText("");
            txtConfirmPassword.setText("");
            txtFullName.setText("");
            txtEmail.setText("");

            // Mặc định chọn vai trò User khi thêm mới
            chkAdmin.setSelected(false);
            chkUser.setSelected(true);
            chkManager.setSelected(false);
        }
    }

    // --- Phương thức thu thập dữ liệu từ các trường UI ---
    private User collectFormData() {
        String userId = isEditMode ? currentUser.getUserID() : null; // ID sẽ được tạo tự động nếu thêm mới
        String userName = txtUserName.getText().trim();
        String password = new String(txtPassWord.getPassword());
        String confirmPassword = new String(txtConfirmPassword.getPassword());
        String fullName = txtFullName.getText().trim();
        String email = txtEmail.getText().trim();

        // Thu thập các vai trò đã chọn
        List<String> roles = new ArrayList<>();
        if (chkAdmin.isSelected()) {
            roles.add("Admin");
        }
        if (chkUser.isSelected()) {
            roles.add("User");
        }
        if (chkManager.isSelected()) {
            roles.add("Manager");
        }

        // Kiểm tra tính hợp lệ của dữ liệu
        if (!validateInput(userName, password, confirmPassword, fullName, email, roles)) {
            return null;
        }

        // Nếu là chế độ sửa và mật khẩu không thay đổi, giữ mật khẩu cũ
        // Kiểm tra password.isEmpty() cho cả txtPassWord và txtConfirmPassword
        if (isEditMode && password.isEmpty() && new String(txtConfirmPassword.getPassword()).isEmpty()) {
            password = currentUser.getPassword(); // Giả định currentUser.getPassword() trả về mật khẩu đã hash/mã hóa
        }
        // Nếu là chế độ thêm mới và mật khẩu trống, hoặc chế độ sửa và mật khẩu mới trống,
        // và mật khẩu cũ cũng trống (trường hợp không có mật khẩu)
        else if (password.isEmpty()) { // Đảm bảo mật khẩu không trống nếu có ý định thay đổi
            JOptionPane.showMessageDialog(this, "Mật khẩu không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        return new User(userId, userName, password, fullName, email, roles);
    }

    // --- Phương thức kiểm tra tính hợp lệ của dữ liệu nhập vào ---
    private boolean validateInput(String userName, String password, String confirmPassword, String fullName, String email, List<String> roles) {
        if (userName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên đăng nhập không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        // Mật khẩu không được trống khi thêm mới HOẶC khi người dùng nhập mật khẩu mới ở chế độ sửa
        if (!isEditMode && password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mật khẩu không được để trống khi thêm mới.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        // Chỉ kiểm tra khớp mật khẩu nếu người dùng có nhập mật khẩu mới
        if (!password.isEmpty() && !password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu và xác nhận mật khẩu không khớp.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (fullName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Họ và tên không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Email không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        // Basic email validation (can be enhanced with regex)
        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(this, "Email không hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (roles.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ít nhất một vai trò.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    // --- Phương thức xử lý logic lưu (thêm mới hoặc cập nhật) ---
    private void handleSaveLogic() {
        User user = collectFormData();
        if (user == null) {
            return; // Dữ liệu không hợp lệ, đã có thông báo lỗi
        }

        boolean success; // Xử lý lỗi trùng tên đăng nhập nếu có (ví dụ: e.getSQLState().startsWith("23"))
        // Tùy thuộc vào thông báo lỗi của DB
        if (isEditMode) {
            success = userDAO.updateUser(user); // Giả định UserDAO có updateUser()
        } else {
            success = userDAO.addUser(user); // Giả định UserDAO có addUser()
        }
        if (success) {
            isSucceeded = true; // Đặt trạng thái thành công
            JOptionPane.showMessageDialog(this, "Lưu thông tin người dùng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            dispose(); // Đóng dialog
        } else {
            JOptionPane.showMessageDialog(this, "Không thể lưu thông tin người dùng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- Phương thức xử lý hủy bỏ thao tác ---
    private void handleCancelLogic() {
        isSucceeded = false; // Đặt trạng thái không thành công
        dispose(); // Đóng dialog
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

        formPanel = new javax.swing.JPanel();
        lblUserName = new javax.swing.JLabel();
        lblUserId = new javax.swing.JLabel();
        lblPassWord = new javax.swing.JLabel();
        lblConfirmPassword = new javax.swing.JLabel();
        lblEmail = new javax.swing.JLabel();
        txtUserId = new javax.swing.JTextField();
        txtUserName = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();
        lblFullName = new javax.swing.JLabel();
        txtFullName = new javax.swing.JTextField();
        txtPassWord = new javax.swing.JPasswordField();
        lblRoles = new javax.swing.JLabel();
        pnCheckRoles = new javax.swing.JPanel();
        chkAdmin = new javax.swing.JCheckBox();
        chkUser = new javax.swing.JCheckBox();
        chkManager = new javax.swing.JCheckBox();
        txtConfirmPassword = new javax.swing.JPasswordField();
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

        lblConfirmPassword.setText("CONFIRM PASSWORD: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(lblConfirmPassword, gridBagConstraints);

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

        lblFullName.setText("FULL NAME: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(lblFullName, gridBagConstraints);

        txtFullName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFullNameActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(txtFullName, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(txtPassWord, gridBagConstraints);

        lblRoles.setText("ROLES: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(lblRoles, gridBagConstraints);

        chkAdmin.setText("ADMIN");
        pnCheckRoles.add(chkAdmin);

        chkUser.setText("USER");
        pnCheckRoles.add(chkUser);

        chkManager.setText("MANAGER");
        pnCheckRoles.add(chkManager);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(pnCheckRoles, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(txtConfirmPassword, gridBagConstraints);

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

    private void txtFullNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFullNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFullNameActionPerformed

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
        handleSaveLogic();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        // TODO add your handling code here:
        handleCancelLogic();
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
    private javax.swing.JCheckBox chkAdmin;
    private javax.swing.JCheckBox chkManager;
    private javax.swing.JCheckBox chkUser;
    private javax.swing.JPanel formPanel;
    private javax.swing.JLabel lblConfirmPassword;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblFullName;
    private javax.swing.JLabel lblPassWord;
    private javax.swing.JLabel lblRoles;
    private javax.swing.JLabel lblUserId;
    private javax.swing.JLabel lblUserName;
    private javax.swing.JPanel pnButton;
    private javax.swing.JPanel pnCheckRoles;
    private javax.swing.JPasswordField txtConfirmPassword;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtFullName;
    private javax.swing.JPasswordField txtPassWord;
    private javax.swing.JTextField txtUserId;
    private javax.swing.JTextField txtUserName;
    // End of variables declaration//GEN-END:variables
}
