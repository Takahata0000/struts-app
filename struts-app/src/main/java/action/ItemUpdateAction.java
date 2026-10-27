package action;


import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import dao.ItemDao;
import form.ItemEditForm;
 
/** 商品更新Action。入力チェックは Struts-Validator(validation.xml)が済ませている。 */
public class ItemUpdateAction extends Action {
 
    private static final Log log = LogFactory.getLog(ItemUpdateAction.class);
 
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
 
        ItemEditForm f = (ItemEditForm) form;
 
        long itemId = Long.parseLong(f.getItemId().trim());
        String itemName = f.getItemName().trim();
        long itemPrice = isEmpty(f.getItemPrice()) ? 0L : Long.parseLong(f.getItemPrice().trim());
        String itemCategory = isEmpty(f.getItemCategory()) ? null : f.getItemCategory().trim();
 
        ActionMessages errors = new ActionMessages();
        try {
            // 更新者はログイン機能ができるまで固定値
            int count = new ItemDao().update(itemId, itemName, itemPrice, itemCategory, "system");
            if (count == 0) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.item.notfound"));
            }
        } catch (SQLException e) {
            log.error("商品の更新に失敗しました。itemId=" + itemId, e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.db.update"));
        }
 
        if (!errors.isEmpty()) {
            saveErrors(request, errors);
            return mapping.getInputForward();      // 編集画面に戻る(入力値は保持される)
        }
        return mapping.findForward("success");     // 一覧へリダイレクト
    }
 
    private static boolean isEmpty(String s) {
        return s == null || s.trim().length() == 0;
    }
}
