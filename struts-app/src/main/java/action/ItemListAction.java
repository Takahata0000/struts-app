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
 
/**
 * 商品一覧Action(ステップ3: 商品名での検索に対応)。
 * ステップ1の ItemListAction.java を、このファイルの内容で置き換える。
 */
public class ItemListAction extends Action {
 
    private static final Log log = LogFactory.getLog(ItemListAction.class);
 
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
 
        // 検索ボックスの値(GET の keyword パラメーター)
        String keyword = request.getParameter("keyword");
        keyword = (keyword == null) ? "" : keyword.trim();
 
        List<ItemDto> items;
        try {
            ItemDao dao = new ItemDao();
            items = keyword.isEmpty() ? dao.findAll() : dao.searchByName(keyword);
        } catch (SQLException e) {
            log.error("商品一覧の取得に失敗しました。keyword=" + keyword, e);
            ActionMessages errors = new ActionMessages();
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.db.select"));
            saveErrors(request, errors);
            items = new ArrayList<ItemDto>();
        }
 
        request.setAttribute("items", items);
        request.setAttribute("keyword", keyword);   // 検索ボックスに値を残すため
        return mapping.findForward("success");
    }
}