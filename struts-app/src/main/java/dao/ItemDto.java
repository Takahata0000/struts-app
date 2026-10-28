package dao;

import java.io.Serializable;
import java.sql.Timestamp;
 
/** 商品1件分のデータ(一覧・編集画面で使う)。 */
public class ItemDto implements Serializable {
 
    private static final long serialVersionUID = 1L;
 
    private long itemId;
    private String itemName;
    private long itemPrice;
    private String itemCategory;   // NULL の場合あり
    private Timestamp createdAt;
    private Timestamp updatedAt;   // 未更新なら NULL
    private String fileName;       // NULL の場合あり
    private String filePath;       // NULL の場合あり
    private String fileType;       // NULL の場合あり
    
    
    public long getItemId() { return itemId; }
    public void setItemId(long itemId) { this.itemId = itemId; }
 
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
 
    public long getItemPrice() { return itemPrice; }
    public void setItemPrice(long itemPrice) { this.itemPrice = itemPrice; }
 
    public String getItemCategory() { return itemCategory; }
    public void setItemCategory(String itemCategory) { this.itemCategory = itemCategory; }
 
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
 
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
 
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
 
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
}