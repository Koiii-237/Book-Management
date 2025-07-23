/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.bookmanagement.view;

/**
 *
 * @author ADMIN
 */
import com.bookmanagement.Dao.BookManagementDAO;
import com.bookmanagement.Dao.OrderDAO;
import com.bookmanagement.model.Book;
import com.bookmanagement.model.Order;
import com.bookmanagement.model.User;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.awt.Frame;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

public class OrderManagementPanel extends javax.swing.JPanel {

    /**
     * Creates new form BookPanel
     */
    OrderDAO orderDAO;
    private Order order;

    public OrderManagementPanel() {
        initComponents();
        setSize(800, 500);
        this.orderDAO = new OrderDAO();
        initTable();
        fillToTable();
    }

    private void initTable() {
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ORDER ID", "ORDER DATE", "TOTAL", "ROLE", "CUSTOMER ID"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblOrder.setModel(model);
        btnPrint.setEnabled(false);
    }

    private void fillToTable() {
        DefaultTableModel tableModel = (DefaultTableModel) tblOrder.getModel();
        tableModel.setRowCount(0);

        for (Order order : orderDAO.getAll()) {
            tableModel.addRow(new Object[]{
                order.getOrderId(),
                order.getOrderDate(),
                order.getTotalMoney(),
                order.getRole(),
                order.getCustomerId()
            });
        }
        btnPrint.setEnabled(false);
    }

    private Order getSelectedOrder() {
        int row = tblOrder.getSelectedRow();
        if (row < 0) {
            return null;
        }
        String id = tblOrder.getValueAt(row, 0).toString(); // Sửa từ 1 -> 0
        return orderDAO.findById(id);
    }

    public void search() {
        String searchTerm = txtSearch.getText().trim();
        ArrayList<Order> searchResults;

        if (searchTerm.isEmpty()) {
            searchResults = orderDAO.getAll();
        } else {
            searchResults = orderDAO.searchOrder(searchTerm);
        }

        DefaultTableModel model = (DefaultTableModel) tblOrder.getModel();
        model.setRowCount(0);

        if (searchResults.isEmpty() && !searchTerm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "NOT FOUND BOOK WITH: '" + searchTerm + "'.", "NOTIFICATION!", JOptionPane.INFORMATION_MESSAGE);
        }

        for (Order order : searchResults) {
            model.addRow(new Object[]{
                order.getOrderId(),
                order.getOrderDate(),
                order.getTotalMoney(),
                order.getRole(),
                order.getCustomerId()
            });
        }
    }

    private void showOrderDialog(Order existing) {
        OrderDialog dialog = new OrderDialog(
                SwingUtilities.getWindowAncestor(this),
                existing
        );
        dialog.setVisible(true);

        if (dialog.isSucceeded()) {
            Order o = dialog.getOrder();
            boolean ok = (existing == null)
                    ? orderDAO.insertOrder(o)
                    : orderDAO.updateOrder(o);
            if (ok) {
                JOptionPane.showMessageDialog(
                        this,
                        existing == null ? "Tạo đơn hàng thành công." : "Cập nhật đơn hàng thành công."
                );
                fillToTable();
            }
        }
    }

    // Lấy đơn hàng đang chọn, trả về null nếu chưa chọn
    private Order getSelectOrder() {
        int row = tblOrder.getSelectedRow();
        if (row < 0) {
            return null;
        }
        String id = tblOrder.getValueAt(row, 1).toString();
        return orderDAO.findById(id);
    }

    // Xóa đơn hàng
    private void deleteOrder() {
        Order o = getSelectOrder();
        if (o == null) {
            return;
        }
        if (orderDAO.deleteOrder(o.getOrderId())) {
            JOptionPane.showMessageDialog(this, "Hủy đơn hàng thành công.");
            fillToTable();
        }
    }

    // In hóa đơn
    private void printOrder() {
        Order o = getSelectedOrder();
        if (o == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn hàng để in hóa đơn.");
            return;
        }

        boolean ok = InvoicePrinter.printInvoice(o.getOrderId());
        if (ok) {
            // nếu in thành công, chuyển trạng thái
            if (orderDAO.updateStatus(o.getOrderId(), "Đã thanh toán")) {
                JOptionPane.showMessageDialog(this, "In hóa đơn thành công và đã cập nhật trạng thái.");
                fillToTable();
            } else {
                JOptionPane.showMessageDialog(this, "In thành công nhưng cập nhật trạng thái thất bại.");
            }
        } else {
            // in bị hủy hoặc lỗi
            JOptionPane.showMessageDialog(this, "Không in hóa đơn.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
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
        lblSearch = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        btnCreateOrder = new javax.swing.JButton();
        btnDetail = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        spBookTable = new javax.swing.JScrollPane();
        tblOrder = new javax.swing.JTable();

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

        btnCreateOrder.setText("CREATE ORDER");
        btnCreateOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateOrderActionPerformed(evt);
            }
        });
        pnToolbar.add(btnCreateOrder);

        btnDetail.setText("DETAIL");
        btnDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDetailActionPerformed(evt);
            }
        });
        pnToolbar.add(btnDetail);

        btnCancel.setText("CANCEL");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        pnToolbar.add(btnCancel);

        btnPrint.setText("PRINT");
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        pnToolbar.add(btnPrint);

        btnRefresh.setText("REFRESH");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });
        pnToolbar.add(btnRefresh);

        add(pnToolbar, java.awt.BorderLayout.PAGE_START);

        spBookTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                spBookTableMouseClicked(evt);
            }
        });

        tblOrder.setModel(new javax.swing.table.DefaultTableModel(
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
        tblOrder.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblOrderMouseClicked(evt);
            }
        });
        spBookTable.setViewportView(tblOrder);

        add(spBookTable, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_txtSearchActionPerformed

    private void tblOrderMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblOrderMouseClicked
        if (tblOrder.getSelectedRow() != -1) {
            btnPrint.setEnabled(true);
        } else {
            btnPrint.setEnabled(false);
        }
    }//GEN-LAST:event_tblOrderMouseClicked

    private void btnCreateOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateOrderActionPerformed
        // TODO add your handling code here:
        showOrderDialog(null);
    }//GEN-LAST:event_btnCreateOrderActionPerformed

    private void btnDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDetailActionPerformed
        Order selected = getSelectedOrder();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn hàng để xem chi tiết.");
            return;
        }
        try {
            // Lấy orderId từ đối tượng đã chọn
            InvoiceDialog dlg = new InvoiceDialog(
                    SwingUtilities.getWindowAncestor(this),
                    selected.getOrderId()
            );
            dlg.setVisible(true);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi mở hóa đơn: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }//GEN-LAST:event_btnDetailActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        // TODO add your handling code here:
        deleteOrder();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        // TODO add your handling code here:
        fillToTable();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void spBookTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_spBookTableMouseClicked
        // TODO add your handling code here:
        if (tblOrder.getSelectedRow() != -1) {
            btnCancel.setEnabled(true);
            btnDetail.setEnabled(true);
        } else {
            btnCancel.setEnabled(false);
            btnDetail.setEnabled(false);
        }
    }//GEN-LAST:event_spBookTableMouseClicked

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        // TODO add your handling code here:
        search();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        // TODO add your handling code here:
        printOrder();
    }//GEN-LAST:event_btnPrintActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnCreateOrder;
    private javax.swing.JButton btnDetail;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnSearch;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblSearch;
    private javax.swing.JPanel pnToolbar;
    private javax.swing.JScrollPane spBookTable;
    private javax.swing.JTable tblOrder;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables

}
