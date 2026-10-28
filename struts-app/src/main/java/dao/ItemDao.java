package dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
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
    
    /** NULL を許す文字列列に値を設定する。 */
    private static void setNullableString(PreparedStatement ps, int index, String value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.VARCHAR);
        } else {
            ps.setString(index, value);
        }
    }
    
    /**
     * 商品を1件登録し、採番された item_id を返す(添付ファイルの情報つき)。
     * ファイルが無い場合は fileName / filePath / fileType に null を渡す。
     */
    public long insert(String itemName, String itemPrice, String itemCategory, String createdBy,
                       String fileName, String filePath, String fileType) throws SQLException {
 
        String sql = "INSERT INTO item "
                   + "(item_id, item_name, item_price, item_category, created_by, "
                   + " file_name, file_path, file_type) "
                   + "VALUES (item_seq.NEXTVAL, ?, ?, ?, ?, ?, ?, ?)";
 
        try (Connection con = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = con.prepareStatement(sql, new String[] { "ITEM_ID" })) {
 
            ps.setString(1, itemName);                  // 1: item_name
            ps.setString(2, itemPrice);                   // 2: item_price
            setNullableString(ps, 3, itemCategory);     // 3: item_category
            ps.setString(4, createdBy);                 // 4: created_by
            setNullableString(ps, 5, fileName);         // 5: file_name
            setNullableString(ps, 6, filePath);         // 6: file_path
            setNullableString(ps, 7, fileType);         // 7: file_type
 
            ps.executeUpdate();
 
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
            return -1L;
        }
    }
    
    //商品登録処理(ファイル登録処理実装前)
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
        "item_id, item_name, item_price, item_category, created_at, updated_at, file_name, file_path, file_type";
 
    /** 1行分の ResultSet を ItemDto に変換する(以降のステップでも使い回す) */
    private ItemDto mapRow(ResultSet rs) throws SQLException {
        ItemDto d = new ItemDto();
        d.setItemId(rs.getLong("item_id"));
        d.setItemName(rs.getString("item_name"));
        d.setItemPrice(rs.getLong("item_price"));
        d.setItemCategory(rs.getString("item_category"));
        d.setCreatedAt(rs.getTimestamp("created_at"));
        d.setUpdatedAt(rs.getTimestamp("updated_at"));
        d.setFileName(rs.getString("file_name"));
        d.setFilePath(rs.getString("file_path"));
        d.setFileType(rs.getString("file_type"));
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
    
    /** 商品を1件削除する。削除できた件数(0 または 1)を返す。 */
    public int delete(long itemId) throws SQLException {
        String sql = "DELETE FROM item WHERE item_id = ?";
        try (Connection con = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, itemId);
            return ps.executeUpdate();
        }
    }
    
    /** 商品を1件取得する。存在しない場合は null を返す。 */
    public ItemDto findById(long itemId) throws SQLException {
        String sql = "SELECT " + SELECT_COLS + " FROM item WHERE item_id = ?";
        try (Connection con = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }
    
    /** 商品を1件更新する。更新できた件数(0 または 1)を返す。 */
    public int update(long itemId, String itemName, long itemPrice,
                      String itemCategory, String updatedBy) throws SQLException {
 
        String sql = "UPDATE item "
                   + "SET item_name = ?, item_price = ?, item_category = ?, "
                   + "    updated_at = SYSTIMESTAMP, updated_by = ? "
                   + "WHERE item_id = ?";
 
        try (Connection con = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, itemName);
            ps.setLong(2, itemPrice);
            if (itemCategory == null) {
                ps.setNull(3, Types.VARCHAR);
            } else {
                ps.setString(3, itemCategory);
            }
            ps.setString(4, updatedBy);
            ps.setLong(5, itemId);
            return ps.executeUpdate();
        }
    }
}
