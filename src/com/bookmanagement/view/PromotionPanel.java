/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.bookmanagement.view;

import com.bookmanagement.Dao.PromotionDAO;
import com.bookmanagement.model.Promotion;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author ADMIN
 */
public class PromotionPanel extends javax.swing.JPanel {

    private static final Logger LOGGER = Logger.getLogger(PromotionPanel.class.getName());
    private PromotionDAO promotionDAO;
    private DefaultTableModel tableModel;
    private List<Promotion> allPromotions;

    /**
     * Creates new form PromotionPanel
     */
    public PromotionPanel() {
        initComponents();
        promotionDAO = new PromotionDAO();
        allPromotions = new ArrayList<>();

        // Set up the table model with column names.
        String[] columnNames = {"ID", "Code", "Discount Percentage", "Start Date", "End Date", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Prevents direct editing of cells in the table
                return false;
            }
        };
        tblPromotion.setModel(tableModel);
        loadPromotions();
        setupEventListeners();
    }
    
    private void loadPromotions() {
        // Automatically update the status of expired promotions before loading
        promotionDAO.autoUpdatePromotionStatus();
        allPromotions = promotionDAO.getAllPromotions();
        displayPromotions(allPromotions);
    }
    
    private void displayPromotions(List<Promotion> promotions) {
        tableModel.setRowCount(0); // Clear all existing rows
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (Promotion promo : promotions) {
            Object[] row = new Object[6];
            row[0] = promo.getPromotionId();
            row[1] = promo.getCode();
            row[2] = promo.getDiscountPercentage();
            row[3] = promo.getStartDate().format(formatter);
            row[4] = promo.getEndDate().format(formatter);
            row[5] = promo.isActive() ? "Active" : "Inactive";
            tableModel.addRow(row);
        }
    }
    
    private void setupEventListeners() {
        btnAdd.addActionListener(this::handleAddAction);
        btnUpdate.addActionListener(this::handleUpdateAction);
        btnDelete.addActionListener(this::handleDeleteAction);
        btnRefresh.addActionListener(this::handleRefreshAction);
        btnSearch.addActionListener(this::handleSearchAction);

        // Add a DocumentListener to the search field for real-time filtering
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterPromotions();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterPromotions();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterPromotions();
            }
        });
    }

    /**
     * Loads the initial data from the database and populates the table.
     */

    private void filterPromotions() {
        String searchTerm = txtSearch.getText().trim().toLowerCase();
        if (searchTerm.isEmpty()) {
            displayPromotions(allPromotions);
        } else {
            List<Promotion> filteredList = new ArrayList<>();
            for (Promotion promo : allPromotions) {
                if (String.valueOf(promo.getPromotionId()).contains(searchTerm)
                        || promo.getCode().toLowerCase().contains(searchTerm)
                        || String.valueOf(promo.getDiscountPercentage()).contains(searchTerm)
                        || promo.getStartDate().toString().contains(searchTerm)
                        || promo.getEndDate().toString().contains(searchTerm)) {
                    filteredList.add(promo);
                }
            }
            displayPromotions(filteredList);
        }
    }

    private void handleAddAction(ActionEvent evt) {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        PromotionDialog dialog = new PromotionDialog(parentFrame, true, null);
        dialog.setVisible(true);
        if (dialog.isDataSaved()) {
            loadPromotions();
        }
    }

    /**
     * Handles the "Update" button click event. Opens a dialog for editing the
     * selected promotion.
     */
    private void handleUpdateAction(ActionEvent evt) {
        int selectedRow = tblPromotion.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a promotion to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int promotionId = (int) tableModel.getValueAt(selectedRow, 0);
        Promotion selectedPromotion = promotionDAO.getPromotionById(promotionId);
        if (selectedPromotion != null) {
            Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
            PromotionDialog dialog = new PromotionDialog(parentFrame, true, selectedPromotion);
            dialog.setVisible(true);
            if (dialog.isDataSaved()) {
                loadPromotions();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Promotion not found in the database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteAction(ActionEvent evt) {
        int selectedRow = tblPromotion.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a promotion to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this promotion?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int promotionId = (int) tableModel.getValueAt(selectedRow, 0);
            if (promotionDAO.deletePromotion(promotionId)) {
                JOptionPane.showMessageDialog(this, "Promotion deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadPromotions();
            } else {
                JOptionPane.showMessageDialog(this, "Could not delete promotion.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleRefreshAction(ActionEvent evt) {
        txtSearch.setText("");
        loadPromotions();
    }

    /**
     * Handles the action when the "Search" button is clicked.
     */
    private void handleSearchAction(ActionEvent evt) {
        // The search function is handled by the DocumentListener,
        // this button just re-triggers the filter
        filterPromotions();
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitle = new javax.swing.JLabel();
        pnToolbar = new javax.swing.JPanel();
        lblSearch = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        btnAdd = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        spPromotion = new javax.swing.JScrollPane();
        tblPromotion = new javax.swing.JTable();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setLayout(new java.awt.BorderLayout(10, 10));

        lblTitle.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setText("PROMOTION MANAGEMENT");
        add(lblTitle, java.awt.BorderLayout.NORTH);

        pnToolbar.setBorder(javax.swing.BorderFactory.createTitledBorder("Toolbar"));
        pnToolbar.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

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

        btnRefresh.setText("REFRESH");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });
        pnToolbar.add(btnRefresh);

        add(pnToolbar, java.awt.BorderLayout.CENTER);

        tblPromotion.setModel(new javax.swing.table.DefaultTableModel(
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
        spPromotion.setViewportView(tblPromotion);

        add(spPromotion, java.awt.BorderLayout.PAGE_END);
    }// </editor-fold>//GEN-END:initComponents

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed

    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRefreshActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblSearch;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel pnToolbar;
    private javax.swing.JScrollPane spPromotion;
    private javax.swing.JTable tblPromotion;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
