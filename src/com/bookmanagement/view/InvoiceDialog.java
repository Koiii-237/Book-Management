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
import com.bookmanagement.Dao.OrderDetailDAO;
import com.bookmanagement.model.Book;
import com.bookmanagement.model.Invoice;
import com.bookmanagement.model.OrderItem;

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
    private static final Logger LOGGER = Logger.getLogger(InvoiceDialog.class.getName());
    // Định dạng ngày tháng
    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    // Định dạng tiền tệ Việt Nam Đồng
    NumberFormat currencyFmt = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    
    private Invoice currentInvoice; // Lưu trữ đối tượng Invoice hiện tại

    /**
     * Constructor chính cho InvoiceDialog.
     * Hiển thị chi tiết của một hóa đơn đã tồn tại.
     *
     * @param parent Cửa sổ cha của dialog (có thể là null).
     * @param invoice Đối tượng Invoice chứa dữ liệu hóa đơn cần hiển thị.
     */
    public InvoiceDialog(Window parent, Invoice invoice) {
        
        super(parent, "Hóa Đơn", ModalityType.APPLICATION_MODAL);
        initComponents();
        this.currentInvoice = invoice; // Gán đối tượng Invoice được truyền vào
        
        System.out.println("InvoiceDialog: Khởi tạo với Invoice ID: " + (invoice != null ? invoice.getInvoiceId() : "NULL"));
        
        // Gán sự kiện cho nút Cancel
        btnCancel.addActionListener(e -> dispose());
        
        // Tải dữ liệu hóa đơn
        try {
            if (currentInvoice != null) {
                loadData(currentInvoice); // Gọi loadData với đối tượng Invoice
                pack(); // Đóng gói các thành phần UI
                setLocationRelativeTo(parent); // Đặt dialog ở giữa cửa sổ cha
                System.out.println("InvoiceDialog: Tải dữ liệu và hiển thị thành công.");
            } else {
                JOptionPane.showMessageDialog(this,
                    "Không có thông tin hóa đơn để hiển thị.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE
                );
                dispose(); // Đóng dialog nếu không có hóa đơn
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "InvoiceDialog: Lỗi khi tải dữ liệu hóa đơn.", ex);
            JOptionPane.showMessageDialog(this,
                "Lỗi khi tải dữ liệu hóa đơn: " + ex.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE
            );
            dispose(); // Đóng dialog nếu có lỗi nghiêm trọng khi tải dữ liệu
        }
        
        // Gán sự kiện in cho nút Print
        btnPrint.addActionListener((ActionEvent e) -> {
            if (currentInvoice != null) {
                System.out.println("InvoiceDialog: Nút Print được nhấn cho Invoice ID: " + currentInvoice.getInvoiceId());
                boolean ok = InvoicePrinter.printInvoice( currentInvoice.getOrderId()); // Gọi phương thức in hóa đơn với orderId
                if (!ok) {
                    JOptionPane.showMessageDialog(this,
                        "In hóa đơn thất bại hoặc bị hủy.",
                        "Thông báo",
                        JOptionPane.WARNING_MESSAGE
                    );
                } else {
                    JOptionPane.showMessageDialog(this, "In hóa đơn hoàn tất!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                 JOptionPane.showMessageDialog(this, "Không có hóa đơn để in.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            }
        });
    }
    
    /**
     * Tải dữ liệu hóa đơn và chi tiết đơn hàng dựa trên đối tượng Invoice.
     * @param invoice Đối tượng Invoice chứa dữ liệu hóa đơn.
     * @throws SQLException Nếu có lỗi khi truy vấn cơ sở dữ liệu.
     */
    private void loadData(Invoice invoice) throws SQLException {
        OrderDetailDAO orderDetailDAO = new OrderDetailDAO();
        BookManagementDAO bookManagementDAO = new BookManagementDAO();

        // 1. Xóa nội dung cũ và thiết lập trạng thái mặc định
        txtProducts.setText("");
        
        // 2. Điền thông tin header từ đối tượng Invoice
        lblInvoiceID.setText("MÃ HÓA ĐƠN: " + invoice.getInvoiceId());
        lblOrderDate.setText("NGÀY TẠO: " + invoice.getDateGenerate().format(fmt));
        lblTotal.setText("TỔNG CỘNG: " + currencyFmt.format(invoice.getMoneyTotal()));
        lblPaymentMethod.setText("PHƯƠNG THỨC TT: " + invoice.getPaymentMethod());
        lblGustMoney.setText("TIỀN KHÁCH TRẢ: " + currencyFmt.format(invoice.getGuestMoney()));
        lblChangeMoney.setText("TIỀN THỪA: " + currencyFmt.format(invoice.getChange()));
        
        System.out.println("InvoiceDialog.loadData: Đã điền thông tin hóa đơn ID: " + invoice.getInvoiceId());
        btnPrint.setEnabled(true); // Kích hoạt nút in
        System.out.println("InvoiceDialog.loadData: Hóa đơn tìm thấy, nút Print được kích hoạt.");

        // 3. Tải chi tiết đơn hàng (các mặt hàng trong hóa đơn)
        // Sử dụng orderId từ đối tượng Invoice để lấy chi tiết đơn hàng
        List<OrderItem> items = orderDetailDAO.getOrderDetailByOrderId(invoice.getOrderId()); 
        System.out.println("InvoiceDialog.loadData: Đã tìm kiếm chi tiết đơn hàng cho Order ID " + invoice.getOrderId() + ". Số lượng: " + (items != null ? items.size() : "null"));

        txtProducts.append(String.format("%-30s %5s %15s %15s%n",
                "Sản phẩm", "SL", "Đơn giá", "Thành tiền"));
        txtProducts.append("------------------------------------------------------------\n");

        if (items == null || items.isEmpty()) {
            txtProducts.append("Không có sản phẩm nào trong hóa đơn này.");
            System.out.println("InvoiceDialog.loadData: Không có chi tiết đơn hàng nào được tìm thấy cho hóa đơn này.");
        } else {
            BigDecimal sum = BigDecimal.ZERO;
            for (OrderItem od : items) {
                // Sử dụng bookManagementDAO để lấy tên sách nếu cần, hoặc dùng od.getBookName() nếu đã có
                Book b = bookManagementDAO.getBookById(od.getBookID());
                String name = (b != null) ? b.getBookName() : od.getBookName() + " (Không rõ tên)"; // Ưu tiên tên từ OrderDetail nếu có
                int qty = od.getQuantity();
                BigDecimal unitPrice = od.getUnitPrice();
                BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(qty));
                sum = sum.add(lineTotal);

                txtProducts.append(String.format("%-30s %5d %15s %15s%n",
                    name, qty, currencyFmt.format(unitPrice), currencyFmt.format(lineTotal)));
            }
            txtProducts.append("------------------------------------------------------------\n");
            txtProducts.append(String.format("%-30s %37s%n", "Tổng cộng:", currencyFmt.format(sum)));
            System.out.println("InvoiceDialog.loadData: Đã điền chi tiết sản phẩm.");
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
                InvoiceDialog dialog = new InvoiceDialog(null, null);
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
