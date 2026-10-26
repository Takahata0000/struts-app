package action;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
 
/** 商品一覧Action(ステップ1: 全件表示のみ)。 */
public class ItemListAction extends Action {
 
    private static final Log log = LogFactory.getLog(ItemListAction.class);
 
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
 
        List<ItemDto> items;
        try {
            items = new ItemDao().findAll();
        } catch (SQLException e) {
            log.error("商品一覧の取得に失敗しました。", e);
            ActionMessages errors = new ActionMessages();
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.db.select"));
            saveErrors(request, errors);
            items = new ArrayList<ItemDto>();   // 画面は空の一覧+エラー表示
        }
 
        request.setAttribute("items", items);
        return mapping.findForward("success");
    }
}