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


/** 商品削除Action。POST のみ受け付け、完了後は一覧へリダイレクトする。 */
public class ItemDeleteAction extends Action {
 
    private static final Log log = LogFactory.getLog(ItemDeleteAction.class);
 
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
 
        // URL を直接開かれても削除されないよう、POST 以外は一覧へ戻す
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return mapping.findForward("success");
        }
 
        ActionMessages errors = new ActionMessages();
        try {
            long itemId = Long.parseLong(request.getParameter("itemId").trim());
            int count = new ItemDao().delete(itemId);
            if (count == 0) {
                // 既に他の画面で削除済みなど
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.item.notfound"));
            }
        } catch (NumberFormatException | NullPointerException e) {
            log.warn("不正な itemId が指定されました。", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.item.notfound"));
        } catch (SQLException e) {
            log.error("商品の削除に失敗しました。", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.db.delete"));
        }
 
        if (!errors.isEmpty()) {
            saveErrors(request, errors);
            return mapping.findForward("error");    // リダイレクトしない(エラーを一覧に引き継ぐ)
        }
        return mapping.findForward("success");      // リダイレクト(再読込での二重削除を防ぐ)
    }
}