package edu.sm.dao;

import edu.sm.dto.Cart;
import edu.sm.frame.Dao;
import edu.sm.frame.Sql;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartDao implements Dao<Integer, Cart> {

    @Override
    public Cart insert(Cart cart, Connection conn) throws Exception {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(Sql.insertCartItem, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, cart.getCid());
            pstmt.setInt(2, cart.getPid());
            pstmt.setInt(3, cart.getCnt());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("장바구니 추가 실패, 영향받은 행이 없습니다.");
            }

            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                cart.setCartKey(rs.getInt(1));
            } else {
                throw new SQLException("장바구니 추가 실패, ID를 가져올 수 없습니다.");
            }
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
        }
        return cart;
    }

    @Override
    public Cart update(Cart cart, Connection conn) throws Exception {
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(Sql.updateCartItemCount);
            pstmt.setInt(1, cart.getCnt());
            pstmt.setInt(2, cart.getCid());
            pstmt.setInt(3, cart.getPid());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("장바구니 수정 실패, 해당 ID의 항목이 없습니다.");
            }
        } finally {
            if (pstmt != null) pstmt.close();
        }
        return cart;
    }

    @Override
    public boolean delete(Integer id, Connection conn) throws Exception {
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(Sql.deleteCartItem);
            pstmt.setInt(1, id);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } finally {
            if (pstmt != null) pstmt.close();
        }
    }

    @Override
    public Cart select(Integer id, Connection conn) throws Exception {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Cart cart = null;
        try {
            pstmt = conn.prepareStatement(Sql.selectCartItemById);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                cart = new Cart(
                        rs.getInt("cart_id"),
                        rs.getInt("cid"),
                        rs.getInt("pid"),
                        rs.getInt("cnt")
                );
            }
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
        }
        return cart;
    }

    @Override
    public List<Cart> select(Connection conn) throws Exception {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Cart> carts = new ArrayList<>();
        try {
            pstmt = conn.prepareStatement(Sql.selectAllCartItems);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                carts.add(new Cart(
                        rs.getInt("cart_id"),
                        rs.getInt("cid"),
                        rs.getInt("pid"),
                        rs.getInt("cnt"),
                        rs.getDate("cdate")
                ));
            }
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
        }
        return carts;
    }
}
