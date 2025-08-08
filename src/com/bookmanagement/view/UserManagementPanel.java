/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.bookmanagement.view;

/**
 *
 * @author ADMIN
 */
import com.bookmanagement.Dao.UserDAO;
import com.bookmanagement.model.User; 
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.awt.Frame;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class UserManagementPanel extends javax.swing.JPanel {

   private static final Logger LOGGER = Logger.getLogger(UserManagementPanel.class.getName());
    private UserDAO userDAO;
    private DefaultTableModel tableModel;

    /**
     * Creates new form UserManagementPanel
     */
    public UserManagementPanel() {
        initComponents();
        this.userDAO = new UserDAO(); // Khởi tạo UserDAO
        initTable(); // Khởi tạo cấu trúc bảng
        fillToTable(); // Đổ dữ liệu vào bảng

        // Thêm ListSelectionListener cho bảng để kích hoạt/vô hiệu hóa các nút
        tblUser.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) { // Đảm bảo sự kiện chỉ kích hoạt một lần khi chọn xong
                    updateButtonStates();
                }
            }
        });
        updateButtonStates(); // Cập nhật trạng thái nút ban đầu
    }

    private void initTable() {
        tableModel = new DefaultTableModel(
                new Object[]{"Mã ND", "Tên Đăng Nhập", "Họ Tên", "Email", "Vai Trò"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép chỉnh sửa trực tiếp trên bảng
            }
        };
        tblUser.setModel(tableModel);
    }

    private void fillToTable() {
        tableModel.setRowCount(0); // Xóa các hàng hiện có

        try {
            ArrayList<User> allUsers = (ArrayList<User>) userDAO.getAllUsers(); // Giả định UserDAO có getAllUsers()
            for (User user : allUsers) {
                // Chuyển đổi List<String> roles thành một chuỗi để hiển thị
                String rolesDisplay = (user.getRoles() != null && !user.getRoles().isEmpty())
                                      ? String.join(", ", user.getRoles())
                                      : "N/A";
                tableModel.addRow(new Object[]{
                    user.getUserID(),
                    user.getUserName(),
                    user.getFullName(),
                    user.getEmail(),
                    rolesDisplay
                });
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tải dữ liệu người dùng: " + e.getMessage(), e);
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu người dùng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- Phương thức cập nhật trạng thái nút ---
    private void updateButtonStates() {
        int selectedRow = tblUser.getSelectedRow();
        boolean isRowSelected = selectedRow != -1;

        btnUpdate.setEnabled(isRowSelected); // Đã sửa từ btnEdit sang btnUpdate theo GUI
        btnDelete.setEnabled(isRowSelected);
        btnResetPassword.setEnabled(isRowSelected); // Kích hoạt nút đặt lại mật khẩu
    }

    // --- Phương thức xử lý tìm kiếm ---
    private void performSearch() {
        String searchTerm = txtSearch.getText().trim();
        ArrayList<User> searchResults;

        try {
            if (searchTerm.isEmpty()) {
                searchResults = (ArrayList<User>) userDAO.getAllUsers();
            } else {
                // Sửa lời gọi phương thức searchUser (số ít)
                searchResults = (ArrayList<User>) userDAO.searchUser(searchTerm);
            }

            tableModel.setRowCount(0); // Xóa dữ liệu cũ

            if (searchResults.isEmpty() && !searchTerm.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy người dùng với từ khóa: '" + searchTerm + "'.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }

            for (User user : searchResults) {
                String rolesDisplay = (user.getRoles() != null && !user.getRoles().isEmpty())
                                      ? String.join(", ", user.getRoles())
                                      : "N/A";
                tableModel.addRow(new Object[]{
                    user.getUserID(),
                    user.getUserName(),
                    user.getFullName(),
                    user.getEmail(),
                    rolesDisplay
                });
            }
            updateButtonStates();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tìm kiếm người dùng: " + e.getMessage(), e);
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm người dùng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- Phương thức xử lý thêm người dùng mới ---
    private void handleAddNewUser() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        // Mở dialog ở chế độ thêm mới (truyền null cho user và false cho isEditMode)
        UserManagementDialog dialog = new UserManagementDialog(parentFrame, false, null);
        dialog.setVisible(true);

        if (dialog.isSucceeded()) {
            fillToTable(); // Làm mới bảng sau khi thêm mới thành công
            JOptionPane.showMessageDialog(this, "Thêm mới người dùng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Thao tác thêm mới bị hủy hoặc thất bại.", "Thông báo", JOptionPane.WARNING_MESSAGE);
        }
        updateButtonStates();
    }

    // --- Phương thức xử lý sửa thông tin người dùng ---
    private void handleEditUser() {
        int selectedRow = tblUser.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một người dùng để sửa.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String userId = (String) tableModel.getValueAt(selectedRow, 0);
        User user = userDAO.getUserById(userId); // Giả định UserDAO có getUserById()
        if (user != null) {
            Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
            // Mở dialog ở chế độ chỉnh sửa (truyền đối tượng user và true cho isEditMode)
            UserManagementDialog dialog = new UserManagementDialog(parentFrame, true, user);
            dialog.setVisible(true);
            
            if (dialog.isSucceeded()) {
                fillToTable(); // Làm mới bảng sau khi sửa thành công
                JOptionPane.showMessageDialog(this, "Cập nhật thông tin người dùng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Thao tác cập nhật bị hủy hoặc thất bại.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy người dùng để sửa.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        updateButtonStates();
    }

    // --- Phương thức xử lý xóa người dùng ---
    private void handleDeleteUser() {
        int selectedRow = tblUser.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một người dùng để xóa.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String userId = (String) tableModel.getValueAt(selectedRow, 0);
        String userName = (String) tableModel.getValueAt(selectedRow, 1); // Tên đăng nhập để hiển thị trong thông báo

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa người dùng '" + userName + "' (Mã ND: " + userId + ") không?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Thêm try-catch cho deleteUser
            boolean success = userDAO.deleteUser(userId); // Giả định UserDAO có deleteUser()
            if (success) {
                JOptionPane.showMessageDialog(this, "Xóa người dùng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                fillToTable(); // Làm mới bảng
            } else {
                JOptionPane.showMessageDialog(this, "Không thể xóa người dùng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
        updateButtonStates();
    }

    // --- Phương thức xử lý đặt lại mật khẩu ---
    private void handleResetPassword() {
        int selectedRow = tblUser.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một người dùng để đặt lại mật khẩu.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String userId = (String) tableModel.getValueAt(selectedRow, 0);
        String userName = (String) tableModel.getValueAt(selectedRow, 1);

        String newPassword = JOptionPane.showInputDialog(this, "Nhập mật khẩu mới cho người dùng '" + userName + "':", "Đặt lại mật khẩu", JOptionPane.PLAIN_MESSAGE);

        if (newPassword != null && !newPassword.trim().isEmpty()) {
            try {
                // Thêm try-catch cho resetPassword
                boolean success = userDAO.resetPassword(userId, newPassword.trim()); // Giả định UserDAO có resetPassword()
                if (success) {
                    JOptionPane.showMessageDialog(this, "Đặt lại mật khẩu thành công cho người dùng '" + userName + "'.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể đặt lại mật khẩu cho người dùng '" + userName + "'.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                Logger.getLogger(UserManagementPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (newPassword != null) { // Người dùng nhấn OK nhưng để trống
            JOptionPane.showMessageDialog(this, "Mật khẩu mới không được để trống.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        }
    }

    // --- Phương thức xử lý làm mới bảng ---
    private void handleRefreshTable() {
        fillToTable();
        txtSearch.setText(""); // Xóa nội dung tìm kiếm
        updateButtonStates();
        JOptionPane.showMessageDialog(this, "Dữ liệu đã được làm mới.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnToolbar = new javax.swing.JPanel();
        lblSearch = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        btnAdd = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnResetPassword = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        spUserTable = new javax.swing.JScrollPane();
        tblUser = new javax.swing.JTable();

        setLayout(new java.awt.BorderLayout());

        lblSearch.setText("Search: ");
        pnToolbar.add(lblSearch);

        txtSearch.setColumns(20);
        txtSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchActionPerformed(evt);
            }
        });
        pnToolbar.add(txtSearch);

        btnSearch.setText("SEARCH");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });
        pnToolbar.add(btnSearch);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        pnToolbar.add(jSeparator1);

        btnAdd.setText("ADD");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });
        pnToolbar.add(btnAdd);

        btnDelete.setText("DELETE");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });
        pnToolbar.add(btnDelete);

        btnUpdate.setText("UPDATE");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });
        pnToolbar.add(btnUpdate);

        btnResetPassword.setText("RESET PASSWORD");
        btnResetPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetPasswordActionPerformed(evt);
            }
        });
        pnToolbar.add(btnResetPassword);

        btnRefresh.setText("REFRESH");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });
        pnToolbar.add(btnRefresh);

        add(pnToolbar, java.awt.BorderLayout.PAGE_START);

        spUserTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                spUserTableMouseClicked(evt);
            }
        });

        tblUser.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblUser.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblUserMouseClicked(evt);
            }
        });
        spUserTable.setViewportView(tblUser);

        add(spUserTable, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchActionPerformed

    private void tblUserMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblUserMouseClicked
        if (tblUser.getSelectedRow() != -1) {
            btnDelete.setEnabled(true);
            btnUpdate.setEnabled(true);
        } else {
            btnDelete.setEnabled(false);
            btnUpdate.setEnabled(false);
        }
    }//GEN-LAST:event_tblUserMouseClicked

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        // TODO add your handling code here:
        handleAddNewUser();
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
        handleDeleteUser();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // TODO add your handling code here:
        handleEditUser();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        // TODO add your handling code here:
        handleRefreshTable();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void spUserTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_spUserTableMouseClicked

    }//GEN-LAST:event_spUserTableMouseClicked

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        // TODO add your handling code here:
        performSearch();

    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnResetPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetPasswordActionPerformed
        // TODO add your handling code here:
        handleResetPassword();
    }//GEN-LAST:event_btnResetPasswordActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnResetPassword;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblSearch;
    private javax.swing.JPanel pnToolbar;
    private javax.swing.JScrollPane spUserTable;
    private javax.swing.JTable tblUser;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
