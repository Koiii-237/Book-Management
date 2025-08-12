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
    private User selectedUser;

    /**
     * Creates new form UserManagementPanel
     */
    public UserManagementPanel() {
        initComponents();
        this.userDAO = new UserDAO();
        initTable();
        fillToTable();

        // Thêm ListSelectionListener cho bảng để kích hoạt/vô hiệu hóa các nút
        tblUser.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = tblUser.getSelectedRow();
                    boolean rowSelected = selectedRow >= 0;
                    btnUpdate.setEnabled(rowSelected);
                    btnDelete.setEnabled(rowSelected);
                    btnResetPassword.setEnabled(rowSelected);
                    btnPermissions.setEnabled(rowSelected);
                    
                    if (rowSelected) {
                        // Lấy thông tin từ hàng được chọn
                        int userId = (int) tableModel.getValueAt(selectedRow, 0);
                        String username = (String) tableModel.getValueAt(selectedRow, 1);
                        String email = (String) tableModel.getValueAt(selectedRow, 2);
                        // Lấy user đầy đủ từ database
                        selectedUser = userDAO.getUserById(userId);
                    } else {
                        selectedUser = null;
                    }
                }
            }
        });
        
        // Ban đầu vô hiệu hóa các nút sửa, xóa, đặt lại mật khẩu, phân quyền
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
        btnResetPassword.setEnabled(false);
        btnPermissions.setEnabled(false);
    }

    private void initTable() {
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new Object[]{"Mã người dùng", "Tên đăng nhập", "Email", "Ngày tạo"});
        tblUser.setModel(tableModel);
    }

    private void fillToTable() {
        tableModel.setRowCount(0); // Xóa dữ liệu cũ
        List<User> userList = userDAO.getAllUsers();
        for (User user : userList) {
            tableModel.addRow(new Object[]{
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt()
            });
        }
    }
    
    private void handleRefreshTable() {
        fillToTable();
        txtSearch.setText(""); // Xóa nội dung ô tìm kiếm
    }
    
    private void performSearch() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            fillToTable();
            return;
        }
        
        tableModel.setRowCount(0); // Xóa dữ liệu cũ
        List<User> userList = (List<User>) userDAO.getUserByUsername(keyword);
        if (userList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy người dùng nào có tên đăng nhập chứa '" + keyword + "'", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } else {
            for (User user : userList) {
                tableModel.addRow(new Object[]{
                    user.getUserId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getCreatedAt()
                });
            }
        }
    }
    
    // --- Phương thức cập nhật trạng thái nút ---
    private void handleAddUser() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        UserManagementDialog dialog = new UserManagementDialog(parentFrame, true, null);
        dialog.setVisible(true);
        if (dialog.isSucceeded()) {
            fillToTable(); // Cập nhật lại bảng nếu thêm thành công
        }
    }

    // --- Phương thức xử lý thêm người dùng mới ---
    private void handleUpdateUser() {
        if (selectedUser != null) {
            Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
            UserManagementDialog dialog = new UserManagementDialog(parentFrame, true, selectedUser);
            dialog.setVisible(true);
            if (dialog.isSucceeded()) {
                fillToTable(); // Cập nhật lại bảng nếu sửa thành công
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một người dùng để cập nhật.", "Lỗi", JOptionPane.WARNING_MESSAGE);
        }
    }

    // --- Phương thức xử lý xóa người dùng ---
    private void handleDeleteUser() {
        int selectedRow = tblUser.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một người dùng để xóa.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int userId = (int) tableModel.getValueAt(selectedRow, 0);
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
    }

    // --- Phương thức xử lý đặt lại mật khẩu ---
    private void handleResetPassword() {
        if (selectedUser != null) {
            String newPassword = JOptionPane.showInputDialog(this, "Nhập mật khẩu mới cho người dùng '" + selectedUser.getUsername() + "':", "Đặt lại mật khẩu", JOptionPane.PLAIN_MESSAGE);
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                try {
                    userDAO.updatePassword(selectedUser.getUserId(), newPassword); // Gọi phương thức updatePassword
                    JOptionPane.showMessageDialog(this, "Đặt lại mật khẩu thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException ex) {
                    Logger.getLogger(UserManagementPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (newPassword != null) {
                JOptionPane.showMessageDialog(this, "Mật khẩu không được để trống.", "Lỗi", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một người dùng để đặt lại mật khẩu.", "Lỗi", JOptionPane.WARNING_MESSAGE);
        }
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
        jSeparator1 = new javax.swing.JSeparator();
        btnAdd = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnPermissions = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        txtSearch = new javax.swing.JTextField();
        lblSearch = new javax.swing.JLabel();
        btnSearch = new javax.swing.JButton();
        btnResetPassword = new javax.swing.JButton();
        spUserTable = new javax.swing.JScrollPane();
        tblUser = new javax.swing.JTable();

        pnToolbar.setBorder(javax.swing.BorderFactory.createTitledBorder("Toolbar"));

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        btnAdd.setText("ADD");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnDelete.setText("DELETE");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnUpdate.setText("UPDATE");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnPermissions.setText("PERMISSIONS");
        btnPermissions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPermissionsActionPerformed(evt);
            }
        });

        btnRefresh.setText("REFRESH");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        txtSearch.setColumns(20);
        txtSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchActionPerformed(evt);
            }
        });

        lblSearch.setText("Search: ");

        btnSearch.setText("SEARCH");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        btnResetPassword.setText("RESET PASSWORD");

        javax.swing.GroupLayout pnToolbarLayout = new javax.swing.GroupLayout(pnToolbar);
        pnToolbar.setLayout(pnToolbarLayout);
        pnToolbarLayout.setHorizontalGroup(
            pnToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnToolbarLayout.createSequentialGroup()
                .addGap(203, 203, 203)
                .addGroup(pnToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAdd))
                .addGap(18, 18, 18)
                .addComponent(btnUpdate)
                .addGap(18, 18, 18)
                .addComponent(btnDelete)
                .addGap(18, 18, 18)
                .addComponent(btnPermissions)
                .addGap(18, 18, 18)
                .addComponent(btnResetPassword)
                .addGap(18, 18, 18)
                .addComponent(btnRefresh)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnToolbarLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(lblSearch)
                .addGap(18, 18, 18)
                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnSearch)
                .addGap(428, 428, 428))
        );
        pnToolbarLayout.setVerticalGroup(
            pnToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnToolbarLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42)
                .addGroup(pnToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAdd)
                    .addComponent(btnDelete)
                    .addComponent(btnUpdate)
                    .addComponent(btnPermissions)
                    .addComponent(btnRefresh)
                    .addComponent(btnResetPassword))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 73, Short.MAX_VALUE)
                .addGroup(pnToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSearch)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSearch))
                .addContainerGap())
        );

        spUserTable.setBorder(javax.swing.BorderFactory.createTitledBorder("User Table"));
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(spUserTable, javax.swing.GroupLayout.PREFERRED_SIZE, 933, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 61, Short.MAX_VALUE))
                    .addComponent(pnToolbar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnToolbar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                .addComponent(spUserTable, javax.swing.GroupLayout.PREFERRED_SIZE, 426, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
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
        handleAddUser();
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
        handleDeleteUser();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // TODO add your handling code here:
        handleUpdateUser();
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

    private void btnPermissionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPermissionsActionPerformed
        // TODO add your handling code here:
        int selectedRow = tblUser.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một người dùng để phân quyền.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Lấy ID người dùng từ cột đầu tiên của hàng được chọn
        int userId = (int) tblUser.getValueAt(selectedRow, 0);
        // Lấy đối tượng User đầy đủ từ database
        User userToDecentralize = userDAO.getUserById(userId);
        if (userToDecentralize != null) {
            // Mở dialog phân quyền và truyền đối tượng User vào
            Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
            UserPermissionDialog dialog = new UserPermissionDialog(parentFrame, true, userToDecentralize);
            dialog.setVisible(true);
            
            // Nếu dữ liệu đã được lưu, làm mới bảng để cập nhật các thay đổi
            if (dialog.isDataSaved()) {
                handleRefreshTable();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy người dùng với ID đã chọn.", "Lỗi Dữ liệu", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnPermissionsActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnPermissions;
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
