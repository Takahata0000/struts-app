package form;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.upload.FormFile;

public class HelloForm extends ActionForm{
	private String name;
	private String price;
	private String category;
	private FormFile file;      // アップロードされたファイル(未選択のときは、名前が空のオブジェクトか null)
	
	public String getName() {return name;}
	public void setName(String name) {this.name = name;};
	
	public String getPrice() {return price;}
	public void setPrice(String price) {this.price = price;};
	
	public String getCategory() {return category;}
	public void setCategory(String category) {this.category = category;};
	
	public FormFile getFile() {return file;}
    public void setFile(FormFile file) {this.file = file;}
	
	// バリデーション処理
    public ActionErrors validate(
            ActionMapping mapping,
            HttpServletRequest request) {

            ActionErrors errors = new ActionErrors();

            // Name must be entered
            if ((name == null) || (name.length() < 1)) {
                errors.add("name", new ActionMessage("errors.name.required"));
            }

            // Secret Phrase must be entered
            if ((price == null) || (price.length() < 1)) {
                errors.add("price", new ActionMessage("errors.price.required"));
            }

            return (errors);

        }
}
