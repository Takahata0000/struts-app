package form;

import org.apache.struts.validator.ValidatorForm;

/** 商品編集フォーム(編集画面の表示と更新の両方で使う)。 */
public class ItemEditForm extends ValidatorForm {
 
    private static final long serialVersionUID = 1L;
 
    private String itemId;        // 更新対象の商品ID(hidden で送る)
    private String itemName;
    private String itemPrice;
    private String itemCategory;
 
    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }
 
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
 
    public String getItemPrice() { return itemPrice; }
    public void setItemPrice(String itemPrice) { this.itemPrice = itemPrice; }
 
    public String getItemCategory() { return itemCategory; }
    public void setItemCategory(String itemCategory) { this.itemCategory = itemCategory; }
}