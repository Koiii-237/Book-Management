/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.bookmanagement.view;

/**
 *
 * @author ADMIN
 */

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SlidingPanel extends JPanel {

    private JPanel currentPanel;
    private JPanel newPanel;
    private Timer slideTimer;

    // Tốc độ và khoảng cách trượt
    private final int slideSpeed = 10; // miliseconds per frame
    private final int slideDistance = 20; // pixels per frame

    public SlidingPanel() {
        // Sử dụng null layout để có thể điều khiển vị trí tuyệt đối của các panel con
        super(null);
    }

    /**
     * Hiển thị panel mới với hiệu ứng trượt.
     * @param panel Màn hình mới cần hiển thị.
     */
    public void showPanel(JPanel panel) {
        if (panel == newPanel) {
            return; // Đang hiển thị panel này rồi
        }
        
        // Dừng timer cũ nếu đang chạy
        if (slideTimer != null && slideTimer.isRunning()) {
            slideTimer.stop();
        }

        this.currentPanel = (this.getComponentCount() > 0) ? (JPanel) this.getComponent(0) : null;
        this.newPanel = panel;
        
        this.add(newPanel, 0); // Thêm panel mới vào lớp trên cùng
        
        // Đặt panel mới ở bên ngoài màn hình, phía bên phải
        newPanel.setBounds(this.getWidth(), 0, this.getWidth(), this.getHeight());
        
        // Bắt đầu animation
        slideTimer = new Timer(slideSpeed, new ActionListener() {
            int currentX = getWidth();
            @Override
            public void actionPerformed(ActionEvent e) {
                currentX -= slideDistance;

                if (currentX <= 0) {
                    currentX = 0;
                    ((Timer) e.getSource()).stop();
                    // Đảm bảo panel mới ở vị trí cuối cùng
                    newPanel.setLocation(0, 0);
                    // Loại bỏ panel cũ sau khi animation kết thúc
                    if (currentPanel != null) {
                        remove(currentPanel);
                    }
                    revalidate();
                    repaint();
                } else {
                    newPanel.setLocation(currentX, 0);
                    if (currentPanel != null) {
                        currentPanel.setLocation(currentX - getWidth(), 0);
                    }
                }
            }
        });
        slideTimer.start();
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
