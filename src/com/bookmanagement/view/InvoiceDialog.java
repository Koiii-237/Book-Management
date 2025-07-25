/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.bookmanagement.view;

/**
 *
 * @author ADMIN
 */
import com.bookmanagement.Dao.BookManagementDAO;
import com.bookmanagement.Dao.InvoiceDAO;
import com.bookmanagement.Dao.OrderDAO;
import com.bookmanagement.Dao.OrderDetailDAO;
import com.bookmanagement.model.Book;
import com.bookmanagement.model.Order;

import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import com.bookmanagement.model.Order;
import com.bookmanagement.model.Customer;
import com.bookmanagement.model.Invoice;
import com.bookmanagement.model.OrderDetail;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InvoiceDialog extends javax.swing.JDialog {

    /**
     * Creates new form BookManagementDialog
     */
    private Order currentOrder; // Đối tượng Book đang được chỉnh sửa (null nếu thêm mới)
    private OrderDAO orderDAO;
    private boolean dataSaved = false;
    private ArrayList<Order> allOrders;
    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    NumberFormat currencyFmt = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    private Order order;
    private Customer customer;

    /**
     * Constructor
     *
     * @param parent
     * @param modal
     * @param bookId
     */
    public InvoiceDialog(Window parent, String orderId) throws SQLException {
        super(parent, "Hóa Đơn Đơn Hàng - " + orderId, ModalityType.APPLICATION_MODAL);
        initComponents();
        loadData(orderId);
        pack();
        setLocationRelativeTo(parent);

    }
    
    private void loadData(String orderId) throws SQLException {
        // formatters
         DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        // 1) load header
        Invoice header = new InvoiceDAO().findById(orderId);
        if (header == null) {
            JOptionPane.showMessageDialog(this,
                "Không tìm thấy hóa đơn: " + orderId,
                "Lỗi", JOptionPane.ERROR_MESSAGE
            );
            dispose();
            return;
        }
        lblInvoiceID.setText(header.getInvoiceId());
        lblOrderDate.setText(header.getDateGenerate().format(dtf));
        lblTotal.setText(fmt.format(header.getMoneyTotal()));
        lblPaymentMethod.setText(header.getPaymentMethod());
        lblGustMoney.setText(fmt.format(header.getGuestMoney()));
        lblChangeMoney.setText(fmt.format(header.getChange()));

        // 2) load chi tiết đơn hàng
        List<OrderDetail> items = (List<OrderDetail>) new OrderDetailDAO().findById(orderId);
        BookManagementDAO sachDAO = new BookManagementDAO();

        txtProducts.setText("");  // xóa sạch trước khi append
        txtProducts.append(String.format("%-30s %5s %15s %15s%n",
                "Sản phẩm", "SL", "Đơn giá", "Thành tiền"));
        txtProducts.append("------------------------------------------------------------\n");

        BigDecimal sum = BigDecimal.ZERO;
        for (OrderDetail od : items) {
            Book b = sachDAO.readBookById(od.getBookId());
            String name = b != null ? b.getBookName() : od.getBookId();
            int qty = od.getQuantity();
            BigDecimal unitPrice = od.getCoin();
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(qty));
            sum = sum.add(lineTotal);

            txtProducts.append(String.format("%-30s %5d %15s %15s%n",
                name, qty, fmt.format(unitPrice), fmt.format(lineTotal)));
        }
        txtProducts.append("------------------------------------------------------------\n");
        txtProducts.append(String.format("%-30s %37s%n", "Tổng cộng:", fmt.format(sum)));

        // 3) gán sự kiện in
        btnPrint.addActionListener((ActionEvent e) -> {
            boolean ok = InvoicePrinter.printInvoice(orderId);
            if (!ok) {
                JOptionPane.showMessageDialog(this,
                    "In hóa đơn thất bại hoặc bị hủy.",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE
                );
            }
                else{
                    JOptionPane.showMessageDialog(this, "COMPLETE!", "NOTFIFICATION", JOptionPane.INFORMATION_MESSAGE);
                }
        });
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnContent = new javax.swing.JPanel();
        lblInvoiceID = new javax.swing.JLabel();
        lblChangeMoney = new javax.swing.JLabel();
        lblGustMoney = new javax.swing.JLabel();
        lblPaymentMethod = new javax.swing.JLabel();
        lblOrderDate = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();
        pnButton = new javax.swing.JPanel();
        btnPrint = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        pnCenter = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtProducts = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        getContentPane().setLayout(new java.awt.BorderLayout(10, 10));

        pnContent.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pnContent.setLayout(new java.awt.GridLayout(5, 0, 2, 5));

        lblInvoiceID.setText("INVOICE ID");
        pnContent.add(lblInvoiceID);

        lblChangeMoney.setText("CHANGE MONEY");
        pnContent.add(lblChangeMoney);

        lblGustMoney.setText("GUST MONEY");
        pnContent.add(lblGustMoney);

        lblPaymentMethod.setText("PAYMENT METHOD");
        pnContent.add(lblPaymentMethod);

        lblOrderDate.setText("ORDER DATE");
        pnContent.add(lblOrderDate);

        lblTotal.setText("TOTAL");
        pnContent.add(lblTotal);

        getContentPane().add(pnContent, java.awt.BorderLayout.NORTH);

        pnButton.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        btnPrint.setText("PRINT");
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        pnButton.add(btnPrint);

        btnCancel.setText("CANCEL");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        pnButton.add(btnCancel);

        getContentPane().add(pnButton, java.awt.BorderLayout.SOUTH);

        pnCenter.setLayout(new java.awt.BorderLayout());

        txtProducts.setColumns(40);
        txtProducts.setRows(10);
        jScrollPane1.setViewportView(txtProducts);

        pnCenter.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        getContentPane().add(pnCenter, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchActionPerformed

    private void tblBookMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblBookMouseClicked

    }//GEN-LAST:event_tblBookMouseClicked

    private void btnTimKiemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTimKiemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnTimKiemActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_btnPrintActionPerformed

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
            java.util.logging.Logger.getLogger(InvoiceDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(InvoiceDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(InvoiceDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(InvoiceDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    InvoiceDialog dialog = new InvoiceDialog(null, null);
                    dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosing(java.awt.event.WindowEvent e) {
                            System.exit(0);
                        }
                    });
                    dialog.setVisible(true);
                } catch (SQLException ex) {
                    Logger.getLogger(InvoiceDialog.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnPrint;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblChangeMoney;
    private javax.swing.JLabel lblGustMoney;
    private javax.swing.JLabel lblInvoiceID;
    private javax.swing.JLabel lblOrderDate;
    private javax.swing.JLabel lblPaymentMethod;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JPanel pnButton;
    private javax.swing.JPanel pnCenter;
    private javax.swing.JPanel pnContent;
    private javax.swing.JTextArea txtProducts;
    // End of variables declaration//GEN-END:variables
}
