package com.bookmanagement.dao;

import com.bookmanagement.DBPool.DBConnection;
import com.bookmanagement.model.Book; // Giả sử đã có lớp DBConnection để lấy kết nối
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Lớp BookDAO xử lý các thao tác dữ liệu liên quan đến đối tượng Book.
 * Lớp này sử dụng các phương thức trả về giá trị để báo cáo kết quả thực hiện.
 */
public class BookDAO {

    private static final Logger LOGGER = Logger.getLogger(BookDAO.class.getName());

    /**
     * Thêm một cuốn sách mới vào cơ sở dữ liệu.
     *
     * @param book Đối tượng Book cần thêm.
     * @return true nếu thêm thành công, false nếu có lỗi xảy ra.
     */
    public boolean addBook(Book book) {
        // Câu lệnh INSERT không bao gồm cột book_id vì nó là khóa tự động tăng
        String sql = "INSERT INTO dbo.books (title, author, isbn, price, category) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             // Sử dụng Statement.RETURN_GENERATED_KEYS để lấy ID vừa được tạo
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getIsbn());
            stmt.setBigDecimal(4, book.getPrice());
            stmt.setString(5, book.getCategory());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                // Lấy ID tự động tăng và cập nhật cho đối tượng book
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        book.setBookId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi thêm sách mới: " + book.getTitle(), e);
        }
        return false;
    }
    
    
    public List<String> getAllCategories() throws SQLException {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT DISTINCT category FROM dbo.books";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy danh mục sách", e);
            throw e;
        }
        return categories;
    }
    
    public boolean isIsbnExists(String isbn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM dbo.books WHERE isbn = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, isbn);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking for existing ISBN", e);
            throw e;
        }
        return false;
    }
    
    /**
     * Cập nhật thông tin của một cuốn sách đã tồn tại.
     *
     * @param book Đối tượng Book chứa thông tin cập nhật.
     * @return true nếu cập nhật thành công, false nếu có lỗi hoặc không tìm thấy sách.
     */
    public boolean updateBook(Book book) {
        String sql = "UPDATE dbo.books SET title = ?, author = ?, isbn = ?, price = ?, category = ? WHERE book_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getIsbn());
            stmt.setBigDecimal(4, book.getPrice());
            stmt.setString(5, book.getCategory());
            stmt.setInt(6, book.getBookId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật sách có ID: " + book.getBookId(), e);
            return false;
        }
    }
    
    
    public boolean updateBookPriceFromPromotions() {
        // Giả định rằng có một bảng khác liên kết sách với khuyến mãi (book_promotions)
        // và một bảng promotions để lấy discount_percentage
        String sql = "UPDATE dbo.books "
                   + "SET price = books.price * (1 - (SELECT TOP 1 p.discount_percentage FROM dbo.promotions p "
                   + "JOIN dbo.book_promotions bp ON p.promotion_id = bp.promotion_id "
                   + "WHERE bp.book_id = books.book_id AND p.is_active = 1 AND p.end_date >= GETDATE())) "
                   + "WHERE EXISTS (SELECT 1 FROM dbo.book_promotions bp "
                   + "JOIN dbo.promotions p ON bp.promotion_id = p.promotion_id "
                   + "WHERE bp.book_id = books.book_id AND p.is_active = 1 AND p.end_date >= GETDATE())";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật giá sách tự động từ khuyến mãi", e);
            return false;
        }
    }

    /**
     * Xóa một cuốn sách khỏi cơ sở dữ liệu dựa trên ID.
     *
     * @param bookId ID của cuốn sách cần xóa.
     * @return true nếu xóa thành công, false nếu có lỗi hoặc không tìm thấy sách.
     */
    public boolean deleteBook(int bookId) {
        String sql = "DELETE FROM dbo.books WHERE book_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi xóa sách có ID: " + bookId, e);
            return false;
        }
    }

    /**
     * Lấy một cuốn sách dựa trên ID.
     *
     * @param bookId ID của cuốn sách cần tìm.
     * @return Đối tượng Book nếu tìm thấy, ngược lại trả về null.
     */
    public Book getBookById(int bookId) {
        String sql = "SELECT * FROM dbo.books WHERE book_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapBookFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy sách theo ID: " + bookId, e);
        }
        return null; // Trả về null nếu không tìm thấy hoặc có lỗi
    }

    /**
     * Lấy tất cả các cuốn sách có trong cơ sở dữ liệu.
     *
     * @return Một danh sách các đối tượng Book.
     */
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM dbo.books";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                books.add(mapBookFromResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy tất cả sách.", e);
        }
        return books;
    }

    /**
     * Tìm kiếm sách theo từ khóa trong tiêu đề, tác giả hoặc ISBN.
     *
     * @param searchTerm Từ khóa tìm kiếm.
     * @return Danh sách các cuốn sách phù hợp.
     */
    public List<Book> searchBooks(String searchTerm) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM dbo.books WHERE title LIKE ? OR author LIKE ? OR isbn LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapBookFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tìm kiếm sách với từ khóa: " + searchTerm, e);
        }
        return books;
    }
    
    public Book getBookByOrderId(int orderId) throws SQLException {
        // SQL để lấy book_id từ bảng order_items
        String sqlFindBookId = "SELECT book_id FROM dbo.order_items WHERE order_id = ?";
        int bookId = -1;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmtFindBookId = conn.prepareStatement(sqlFindBookId)) {
            
            stmtFindBookId.setInt(1, orderId);
            
            try (ResultSet rs = stmtFindBookId.executeQuery()) {
                if (rs.next()) {
                    bookId = rs.getInt("book_id");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy book_id từ order_items với order_id: " + orderId, e);
            throw e;
        }

        // Nếu tìm thấy book_id, gọi phương thức getBookById để lấy chi tiết sách
        if (bookId != -1) {
            return getBookById(bookId);
        }

        return null;
    }
    
    /**
     * Phương thức trợ giúp để ánh xạ một ResultSet thành một đối tượng Book.
     *
     * @param rs ResultSet chứa dữ liệu của một hàng trong bảng sách.
     * @return Một đối tượng Book đã được điền dữ liệu.
     * @throws SQLException nếu có lỗi xảy ra khi truy cập ResultSet.
     */
    private Book mapBookFromResultSet(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setBookId(rs.getInt("book_id"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setIsbn(rs.getString("isbn"));
        book.setPrice(rs.getBigDecimal("price"));
        book.setCategory(rs.getString("category"));
        return book;
    }
}
