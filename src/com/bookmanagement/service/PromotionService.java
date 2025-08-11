/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookmanagement.service;

/**
 *
 * @author ADMIN
 */


import com.bookmanagement.Dao.PromotionDAO;
import com.bookmanagement.model.Promotion;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PromotionService {
    private static final Logger LOGGER = Logger.getLogger(PromotionService.class.getName());
    private final PromotionDAO promotionDAO;

    public PromotionService() {
        this.promotionDAO = new PromotionDAO();
    }

    /**
     * Adds a new promotion.
     * @param promotion The Promotion object to add.
     * @return true if successful, false otherwise.
     */
    public boolean addPromotion(Promotion promotion) {
        try {
            return promotionDAO.addPromotion(promotion);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to add promotion", e);
            return false;
        }
    }

    /**
     * Updates an existing promotion.
     * @param promotion The Promotion object with updated data.
     * @return true if successful, false otherwise.
     */
    public boolean updatePromotion(Promotion promotion) {
        try {
            return promotionDAO.updatePromotion(promotion);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to update promotion", e);
            return false;
        }
    }

    /**
     * Deletes a promotion.
     * @param promotionId The ID of the promotion to delete.
     * @return true if successful, false otherwise.
     */
    public boolean deletePromotion(int promotionId) {
        try {
            return promotionDAO.deletePromotion(promotionId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to delete promotion", e);
            return false;
        }
    }

    /**
     * Retrieves all promotions.
     * @return A list of Promotion objects.
     */
    public List<Promotion> getAllPromotions() {
        try {
            return promotionDAO.getAllPromotions();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to get all promotions", e);
            return List.of();
        }
    }
    
    /**
     * Retrieves a promotion by its ID.
     * @param promotionId The ID of the promotion.
     * @return The Promotion object or null if not found.
     */
    public Promotion getPromotionById(int promotionId) {
        try {
            return promotionDAO.getPromotionById(promotionId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to get promotion by ID", e);
            return null;
        }
    }

    /**
     * Automatically updates the status of expired promotions.
     * This method checks if a promotion's end date has passed and deactivates it.
     */
    public void updateExpiredPromotions() {
        LOGGER.log(Level.INFO, "Checking for expired promotions...");
        List<Promotion> promotions = getAllPromotions();
        LocalDate today = LocalDate.now();
        int updatedCount = 0;

        for (Promotion promo : promotions) {
            if (promo.isActive() && promo.getEndDate().isBefore(today)) {
                promo.setActive(false);
                if (updatePromotion(promo)) {
                    updatedCount++;
                }
            }
        }
        LOGGER.log(Level.INFO, "Updated status for {0} expired promotions.", updatedCount);
    }
    
    /**
     * Automatically deletes promotions that have already expired.
     */
    public void deleteExpiredPromotions() {
        LOGGER.log(Level.INFO, "Checking for expired promotions to delete...");
        List<Promotion> promotions = getAllPromotions();
        LocalDate today = LocalDate.now();
        int deletedCount = 0;
        
        for (Promotion promo : promotions) {
            if (promo.getEndDate().isBefore(today)) {
                if (deletePromotion(promo.getPromotionId())) {
                    deletedCount++;
                }
            }
        }
        LOGGER.log(Level.INFO, "Deleted {0} expired promotions.", deletedCount);
    }
}
