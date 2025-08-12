/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookmanagement.Dao;

/**
 *
 * @author ADMIN
 */
import com.bookmanagement.DBPool.DBConnection;
import com.bookmanagement.model.Customer;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.List;

public class CustomerDAO {

    private static final Logger LOGGER = Logger.getLogger(CustomerDAO.class.getName());

    /**
     * Thêm một khách hàng mới vào cơ sở dữ liệu.
     *
     * @param customer Đối tượng Customer cần thêm.
     * @return true nếu thêm thành công, false nếu có lỗi xảy ra.
     */
    public boolean addCustomer(Customer customer) {
        // Câu lệnh INSERT không bao gồm cột customer_id vì nó là khóa tự động tăng
        String sql = "INSERT INTO dbo.customers (full_name, email, phone, address) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             // Sử dụng Statement.RETURN_GENERATED_KEYS để lấy ID vừa được tạo
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, customer.getFullName());
            stmt.setString(2, customer.getEmail());
            stmt.setString(3, customer.getPhone());
            stmt.setString(4, customer.getAddress());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                // Lấy ID tự động tăng và cập nhật cho đối tượng customer
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        customer.setCustomerId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi thêm khách hàng mới: " + customer.getFullName(), e);
        }
        return false;
    }

    /**
     * Cập nhật thông tin của một khách hàng đã tồn tại.
     *
     * @param customer Đối tượng Customer chứa thông tin cập nhật.
     * @return true nếu cập nhật thành công, false nếu có lỗi hoặc không tìm thấy khách hàng.
     */
    public boolean updateCustomer(Customer customer) {
        String sql = "UPDATE dbo.customers SET full_name = ?, email = ?, phone = ?, address = ? WHERE customer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customer.getFullName());
            stmt.setString(2, customer.getEmail());
            stmt.setString(3, customer.getPhone());
            stmt.setString(4, customer.getAddress());
            stmt.setInt(5, customer.getCustomerId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật khách hàng có ID: " + customer.getCustomerId(), e);
            return false;
        }
    }

    /**
     * Xóa một khách hàng khỏi cơ sở dữ liệu dựa trên ID.
     *
     * @param customerId ID của khách hàng cần xóa.
     * @return true nếu xóa thành công, false nếu có lỗi hoặc không tìm thấy khách hàng.
     */
    public boolean deleteCustomer(int customerId) {
        String sql = "DELETE FROM dbo.customers WHERE customer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi xóa khách hàng có ID: " + customerId, e);
            return false;
        }
    }

    /**
     * Lấy một khách hàng dựa trên ID.
     *
     * @param customerId ID của khách hàng cần tìm.
     * @return Đối tượng Customer nếu tìm thấy, ngược lại trả về null.
     */
    public Customer getCustomerById(int customerId) {
        String sql = "SELECT * FROM dbo.customers WHERE customer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapCustomerFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy khách hàng theo ID: " + customerId, e);
        }
        return null; // Trả về null nếu không tìm thấy hoặc có lỗi
    }

    /**
     * Lấy tất cả các khách hàng có trong cơ sở dữ liệu.
     *
     * @return Một danh sách các đối tượng Customer.
     */
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM dbo.customers";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                customers.add(mapCustomerFromResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy tất cả khách hàng.", e);
        }
        return customers;
    }

    /**
     * Tìm kiếm khách hàng theo từ khóa trong tên, email hoặc số điện thoại.
     *
     * @param searchTerm Từ khóa tìm kiếm.
     * @return Danh sách các khách hàng phù hợp.
     */
    public List<Customer> searchCustomers(String searchTerm) {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM dbo.customers WHERE full_name LIKE ? OR email LIKE ? OR phone LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    customers.add(mapCustomerFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tìm kiếm khách hàng với từ khóa: " + searchTerm, e);
        }
        return customers;
    }

    /**
     * Phương thức trợ giúp để ánh xạ một ResultSet thành một đối tượng Customer.
     *
     * @param rs ResultSet chứa dữ liệu của một hàng trong bảng khách hàng.
     * @return Một đối tượng Customer đã được điền dữ liệu.
     * @throws SQLException nếu có lỗi xảy ra khi truy cập ResultSet.
     */
    private Customer mapCustomerFromResultSet(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setCustomerId(rs.getInt("customer_id"));
        customer.setFullName(rs.getString("full_name"));
        customer.setEmail(rs.getString("email"));
        customer.setPhone(rs.getString("phone"));
        customer.setAddress(rs.getString("address"));
        return customer;
    }
}
