/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.bookmanagement.view;

import com.bookmanagement.Dao.PromotionDAO;
import com.bookmanagement.model.Promotion;
import com.toedter.calendar.JDateChooser;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author ADMIN
 */
public class PromotionDialog extends javax.swing.JDialog {

    /**
     * Creates new form PromotionDialog
     */
    private static final Logger LOGGER = Logger.getLogger(PromotionDialog.class.getName());
    private PromotionDAO promotionDAO;
    private Promotion currentPromotion;
    private boolean dataSaved = false;

    public PromotionDialog(java.awt.Frame parent, boolean modal, Promotion promotion) {
        super(parent, modal);
        this.currentPromotion = promotion;
        initComponents();
        
        initDialog();
        setupEventListeners();
    }

    private void initDialog() {
        if (currentPromotion == null) {
            setTitle("ADD NEW PROMOTION");
            txtPromotionId.setText("AUTO GENERATE");
            txtPromotionId.setEnabled(false);
            chkActive.setSelected(true); // Mặc định là active khi thêm mới
            lblCodeError.setVisible(false);
            jdcStartDate.setDate(new Date());
        } else {
            setTitle("UPDATE PROMOTION");
            loadPromotionData();
            autoUpdateStatus();
        }
    }

    private void loadPromotionData() {
        if (currentPromotion != null) {
            txtPromotionId.setText(currentPromotion.getCode());
            txtPercentDiscount.setText(String.valueOf(currentPromotion.getDiscountPercentage()));
            // Sử dụng setDate từ java.sql.Date để tương thích với JDateChooser
            jdcStartDate.setDate(java.sql.Date.valueOf(currentPromotion.getStartDate()));
            jdcEndDate.setDate(java.sql.Date.valueOf(currentPromotion.getEndDate()));
            chkActive.setSelected(currentPromotion.isActive());
        }
    }

    private void autoUpdateStatus() {
        if (currentPromotion != null) {
            LocalDate today = LocalDate.now();
            LocalDate endDate = currentPromotion.getEndDate();

            // Nếu ngày kết thúc đã qua và khuyến mãi vẫn đang hoạt động
            if (endDate != null && today.isAfter(endDate) && currentPromotion.isActive()) {
                int option = JOptionPane.showConfirmDialog(
                        this,
                        "This promotion has expired. Do you want to automatically update the status of 'inactive'? ",
                        "Status update",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (option == JOptionPane.YES_OPTION) {
                    currentPromotion.setActive(false);
                    chkActive.setSelected(false);
                    promotionDAO.updatePromotion(currentPromotion);
                    JOptionPane.showMessageDialog(this, "Update the promotion status into 'inactive'.", "Notification", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }

    private void setupEventListeners() {
        btnSave.addActionListener(this::handleSaveAction);
        btnCancel.addActionListener(this::handleCancelAction);

        // Tự động kiểm tra mã khuyến mãi khi người dùng gõ
        txtPromotionId.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                checkCode();
            }

            public void removeUpdate(DocumentEvent e) {
                checkCode();
            }

            public void insertUpdate(DocumentEvent e) {
                checkCode();
            }

            private void checkCode() {
                String code = txtPromotionId.getText().trim();
                if (code.isEmpty()) {
                    lblCodeError.setVisible(false);
                    btnSave.setEnabled(true);
                    return;
                }

                Promotion existingPromo = promotionDAO.getPromotionByCode(code);
                // Nếu mã đã tồn tại, và không phải là mã của chính khuyến mãi đang chỉnh sửa
                if (existingPromo != null && (currentPromotion == null || currentPromotion.getPromotionId() != existingPromo.getPromotionId())) {
                    lblCodeError.setText("This promotion code has existed!");
                    lblCodeError.setVisible(true);
                    btnSave.setEnabled(false);
                } else {
                    lblCodeError.setText("");
                    lblCodeError.setVisible(false);
                    btnSave.setEnabled(true);
                }
            }
        });
    }
    
    
    private void handleSaveAction(ActionEvent evt) {
        if (!validateForm()) {
            return;
        }
        
        try {
            Promotion promotionToSave;
            String code = txtPromotionId.getText();
            BigDecimal discountPercentage = new BigDecimal(txtPercentDiscount.getText());
            LocalDate startDate = Instant.ofEpochMilli(jdcStartDate.getDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate endDate = Instant.ofEpochMilli(jdcEndDate.getDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
            boolean isActive = chkActive.isSelected();
            
            if (currentPromotion == null) {
                promotionToSave = new Promotion(0, code, discountPercentage, startDate, endDate, isActive);
                promotionDAO.addPromotion(promotionToSave);
                JOptionPane.showMessageDialog(this, "Add successful promotions!", "Complete", JOptionPane.INFORMATION_MESSAGE);
            } else {
                promotionToSave = currentPromotion;
                promotionToSave.setCode(code);
                promotionToSave.setDiscountPercentage(discountPercentage);
                promotionToSave.setStartDate(startDate);
                promotionToSave.setEndDate(endDate);
                promotionToSave.setActive(isActive);
                promotionDAO.updatePromotion(promotionToSave);
                JOptionPane.showMessageDialog(this, "Update promotion successfully!", "Complete", JOptionPane.INFORMATION_MESSAGE);
            }
            
            dataSaved = true;
            dispose();
            
        } catch (NumberFormatException ex) {
            LOGGER.log(Level.SEVERE, "Error when save promotion", ex);
            JOptionPane.showMessageDialog(this, "Error when saving promotion:" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Xử lý sự kiện khi nhấn nút "Hủy".
     */
    private void handleCancelAction(ActionEvent evt) {
        dataSaved = false;
        dispose();
    }
    
    
    private boolean validateForm() {
        String code = txtPromotionId.getText().trim();
        if (code.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter promotional code.", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        try {
            BigDecimal discount = new BigDecimal(txtPercentDiscount.getText());
            if (discount.compareTo(BigDecimal.ZERO) < 0 || discount.compareTo(BigDecimal.ONE) > 0) {
                 JOptionPane.showMessageDialog(this, "The percentage of discount must be from 0 to 1.", "Warning", JOptionPane.WARNING_MESSAGE);
                 return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "The percentage of discounts must be a valid number.", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (jdcStartDate.getDate() == null || jdcEndDate.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Please choose the start date and the end date.", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        LocalDate startDate = Instant.ofEpochMilli(jdcStartDate.getDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = Instant.ofEpochMilli(jdcEndDate.getDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        
        if (startDate.isAfter(endDate)) {
            JOptionPane.showMessageDialog(this, "The end date must be after or equal to the start date.", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        // Logic mới: Không cho phép tạo khuyến mãi với ngày bắt đầu trong quá khứ
        if (currentPromotion == null && startDate.isBefore(LocalDate.now())) {
            JOptionPane.showMessageDialog(this, "The start date is not in the past.", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        Promotion existingPromo = promotionDAO.getPromotionByCode(code);
        if (existingPromo != null && (currentPromotion == null || currentPromotion.getPromotionId() != existingPromo.getPromotionId())) {
            JOptionPane.showMessageDialog(this, "Promotion code has existed. Please choose another code.", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    public boolean isDataSaved() {
        return dataSaved;
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
        lblCode = new javax.swing.JLabel();
        txtPromotionId = new javax.swing.JTextField();
        lblDiscount = new javax.swing.JLabel();
        txtPercentDiscount = new javax.swing.JTextField();
        lblStartDate = new javax.swing.JLabel();
        lblEndDate = new javax.swing.JLabel();
        lblCodeError = new javax.swing.JLabel();
        jdcEndDate = new com.toedter.calendar.JDateChooser();
        jdcStartDate = new com.toedter.calendar.JDateChooser();
        lblActive = new javax.swing.JLabel();
        chkActive = new javax.swing.JCheckBox();
        pnButton = new javax.swing.JPanel();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        formPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        formPanel.setToolTipText("");

        lblCode.setText("Promotion ID: ");

        lblDiscount.setText("Discount (%):");

        lblStartDate.setText("Start Date:");

        lblEndDate.setText("End Date: ");

        lblCodeError.setForeground(new java.awt.Color(255, 0, 0));
        lblCodeError.setText("This code has existed!");

        lblActive.setText("Actice: ");

        chkActive.setText("Is Active");

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

        javax.swing.GroupLayout formPanelLayout = new javax.swing.GroupLayout(formPanel);
        formPanel.setLayout(formPanelLayout);
        formPanelLayout.setHorizontalGroup(
            formPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(formPanelLayout.createSequentialGroup()
                .addGroup(formPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(formPanelLayout.createSequentialGroup()
                        .addGroup(formPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(formPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(lblDiscount, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE)
                                .addComponent(lblStartDate, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(formPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(formPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblCodeError)
                                    .addComponent(lblCode)
                                    .addComponent(lblEndDate)
                                    .addComponent(lblActive, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(37, 37, 37)
                        .addGroup(formPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtPercentDiscount)
                            .addComponent(txtPromotionId)
                            .addComponent(chkActive, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jdcEndDate, javax.swing.GroupLayout.DEFAULT_SIZE, 459, Short.MAX_VALUE)
                            .addComponent(jdcStartDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(pnButton, javax.swing.GroupLayout.PREFERRED_SIZE, 632, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        formPanelLayout.setVerticalGroup(
            formPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(formPanelLayout.createSequentialGroup()
                .addContainerGap(49, Short.MAX_VALUE)
                .addGroup(formPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCode, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPromotionId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(lblCodeError)
                .addGap(24, 24, 24)
                .addGroup(formPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPercentDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(formPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblStartDate, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(formPanelLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jdcStartDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(formPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(formPanelLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(lblEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, formPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jdcEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)))
                .addGroup(formPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblActive, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkActive, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(122, 122, 122)
                .addComponent(formPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(formPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        // TODO add your handling code here:
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
            java.util.logging.Logger.getLogger(PromotionDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PromotionDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PromotionDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PromotionDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                PromotionDialog dialog = new PromotionDialog(new javax.swing.JFrame(), true, null);
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
    private javax.swing.JCheckBox chkActive;
    private javax.swing.JPanel formPanel;
    private com.toedter.calendar.JDateChooser jdcEndDate;
    private com.toedter.calendar.JDateChooser jdcStartDate;
    private javax.swing.JLabel lblActive;
    private javax.swing.JLabel lblCode;
    private javax.swing.JLabel lblCodeError;
    private javax.swing.JLabel lblDiscount;
    private javax.swing.JLabel lblEndDate;
    private javax.swing.JLabel lblStartDate;
    private javax.swing.JPanel pnButton;
    private javax.swing.JTextField txtPercentDiscount;
    private javax.swing.JTextField txtPromotionId;
    // End of variables declaration//GEN-END:variables
}
