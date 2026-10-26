package dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ItemDao {
	//DB接続情報	
	private static final String URL;
    private static final String USER;
    private static final String PASS;
    
    static {
        Properties p = new Properties();
        try (InputStream in = ItemDao.class.getClassLoader()
                                           .getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new IllegalStateException("db.properties が見つかりません");
            }
            p.load(in);
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (IOException | ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }
        URL  = p.getProperty("db.url");
        USER = p.getProperty("db.user");
        PASS = p.getProperty("db.password");
    }
    
    //商品登録処理
	public void insert(String name, String price, String category) throws SQLException{
		String sql = "INSERT INTO item (item_id, item_name, item_price, item_category, created_by) VALUES (item_seq.NEXTVAL, ?, ?, ?, ?)";
        try (Connection con = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = con.prepareStatement(sql)) {
            //idの自動生成、更新日と更新者を追加必要
            ps.setString(1, name);
            ps.setString(2, price);
            ps.setString(3, category);
            ps.setString(4, "app_user"); // 作成者をシステムユーザー名に設定
            ps.executeUpdate();
            System.out.println("OK");
        }
    }
	
	/** 一覧・編集で共通して取得する列 */
    private static final String SELECT_COLS =
        "item_id, item_name, item_price, item_category, created_at, updated_at";
 
    /** 1行分の ResultSet を ItemDto に変換する(以降のステップでも使い回す) */
    private ItemDto mapRow(ResultSet rs) throws SQLException {
        ItemDto d = new ItemDto();
        d.setItemId(rs.getLong("item_id"));
        d.setItemName(rs.getString("item_name"));
        d.setItemPrice(rs.getLong("item_price"));
        d.setItemCategory(rs.getString("item_category"));
        d.setCreatedAt(rs.getTimestamp("created_at"));
        d.setUpdatedAt(rs.getTimestamp("updated_at"));
        return d;
    }
 
    /** 商品を全件取得する(item_id の昇順)。 */
    public List<ItemDto> findAll() throws SQLException {
        String sql = "SELECT " + SELECT_COLS + " FROM item ORDER BY item_id";
 
        List<ItemDto> list = new ArrayList<ItemDto>();
        try (Connection con = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }
    
    /**
     * 商品名の部分一致で検索する(item_id の昇順)。
     * LIKE の特殊文字(% _ \)は検索文字として扱うようエスケープする。
     */
    public List<ItemDto> searchByName(String keyword) throws SQLException {
        String sql = "SELECT " + SELECT_COLS + " FROM item "
                   + "WHERE item_name LIKE ? ESCAPE '\\' "
                   + "ORDER BY item_id";
 
        String escaped = keyword.replace("\\", "\\\\")
                                .replace("%", "\\%")
                                .replace("_", "\\_");
 
        List<ItemDto> list = new ArrayList<ItemDto>();
        try (Connection con = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + escaped + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }
}
