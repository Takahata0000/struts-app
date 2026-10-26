package action;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import dao.ItemDao;
import form.HelloForm;

//商品登録処理
public class HelloAction extends Action{
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
    			HelloForm f = (HelloForm) form;
    			try {
    	            new ItemDao().insert(f.getName(), f.getPrice(), f.getCategory());
    	        } catch (SQLException e) {
    	        	e.printStackTrace(); 
    	            // ログ出力 + エラーメッセージを画面に返す
    	            ActionMessages errors = new ActionMessages();
    	            errors.add(ActionMessages.GLOBAL_MESSAGE,
    	                       new ActionMessage("errors.db.insert"));
    	            saveErrors(request, errors);
    	            return mapping.getInputForward();
    	        }
        return mapping.findForward("success");
    }
    
    
}
