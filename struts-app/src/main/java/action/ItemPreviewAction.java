package action;

import java.io.IOException;
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
import util.CsvPreview;
import util.FileStorage;

/**
 * プレビュー画面を表示するAction(ステップ7: PDF + CSV)。
 * ステップ6の ItemPreviewAction.java を、このファイルで置き換える。
 */
public class ItemPreviewAction extends Action {
 
    private static final Log log = LogFactory.getLog(ItemPreviewAction.class);
 
    /** CSV で表示する最大行数(見出しを除く) */
    private static final int CSV_MAX_ROWS = 50;
 
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
 
        ActionMessages errors = new ActionMessages();
        try {
            long itemId = Long.parseLong(request.getParameter("itemId").trim());
            ItemDto dto = new ItemDao().findById(itemId);
 
            if (dto != null && dto.getFilePath() != null) {
                request.setAttribute("item", dto);
 
                if ("csv".equals(dto.getFileType())) {
                    CsvPreview.Result r = CsvPreview.read(
                            FileStorage.resolve(dto.getFilePath()), CSV_MAX_ROWS);
                    request.setAttribute("csvHeader", r.header);
                    request.setAttribute("csvRows", r.rows);
                    request.setAttribute("csvTruncated", Boolean.valueOf(r.truncated));
                    request.setAttribute("csvCharset", r.charsetName);
                    request.setAttribute("csvMaxRows", Integer.valueOf(CSV_MAX_ROWS));
                }
                return mapping.findForward("success");
            }
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.file.notfound"));
 
        } catch (NumberFormatException | NullPointerException e) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.file.notfound"));
        } catch (SQLException e) {
            log.error("商品の取得に失敗しました。", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.db.select"));
        } catch (IOException e) {
            // DBにはあるがファイルが読めない(削除された・権限がない など)
            log.error("ファイルの読み込みに失敗しました。", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.file.notfound"));
        }
 
        saveErrors(request, errors);
        return mapping.findForward("notfound");
    }
}
