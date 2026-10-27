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
import dao.ItemDto;
import form.ItemEditForm;


/**
 * 編集画面の表示Action(/itemEdit.do?itemId=1)。
 * DBから取得した値を ItemEditForm に入れて edit.jsp へ forward する。
 * (itemId は Struts がリクエストパラメーターから自動でフォームに設定する)
 */
public class ItemEditAction extends Action {
 
    private static final Log log = LogFactory.getLog(ItemEditAction.class);
 
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
 
        ItemEditForm f = (ItemEditForm) form;
        ActionMessages errors = new ActionMessages();
 
        try {
            long itemId = Long.parseLong(f.getItemId().trim());
            ItemDto dto = new ItemDao().findById(itemId);
 
            if (dto != null) {
                f.setItemName(dto.getItemName());
                f.setItemPrice(String.valueOf(dto.getItemPrice()));
                f.setItemCategory(dto.getItemCategory() == null ? "" : dto.getItemCategory());
                return mapping.findForward("success");
            }
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.item.notfound"));
 
        } catch (NumberFormatException | NullPointerException e) {
            log.warn("不正な itemId が指定されました。", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.item.notfound"));
        } catch (SQLException e) {
            log.error("商品の取得に失敗しました。", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.db.select"));
        }
 
        saveErrors(request, errors);
        return mapping.findForward("notfound");   // 一覧へ戻す(エラー表示つき)
    }
}
