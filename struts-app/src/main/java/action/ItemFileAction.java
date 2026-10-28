package action;

import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import dao.ItemDao;
import dao.ItemDto;
import util.FileStorage;
/**
 * 保存済みファイルをブラウザに返すAction(/itemFile.do?itemId=1)。
 *
 * セキュリティ上のポイント
 *  ・リクエストでは「商品ID」だけを受け取り、ファイルの場所は必ず DB から引く
 *    (ファイルのパスをリクエストで受け取ると、他のファイルを読まれる危険がある)
 *  ・PDF は画面内に表示(inline)、CSV はダウンロード(attachment)
 *  ・X-Content-Type-Options: nosniff で、ブラウザによる種別の推測を止める
 */
public class ItemFileAction extends Action {
 
    private static final Log log = LogFactory.getLog(ItemFileAction.class);
 
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
 
        long itemId;
        try {
            itemId = Long.parseLong(request.getParameter("itemId").trim());
        } catch (NumberFormatException | NullPointerException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
 
        ItemDto dto;
        try {
            dto = new ItemDao().findById(itemId);
        } catch (SQLException e) {
            log.error("商品の取得に失敗しました。itemId=" + itemId, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
        if (dto == null || dto.getFilePath() == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
 
        Path path = FileStorage.resolve(dto.getFilePath());
        if (!Files.isRegularFile(path)) {
            log.warn("DBにはあるがファイルが存在しません: " + path);
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
 
        boolean pdf = "pdf".equals(dto.getFileType());
        // ?download=1 が付いていれば、PDF も強制的にダウンロードにする
        boolean inline = pdf && !"1".equals(request.getParameter("download"));
 
        response.setContentType(pdf ? "application/pdf" : "application/octet-stream");
        response.setHeader("X-Content-Type-Options", "nosniff");
 
        // 日本語のファイル名は RFC 5987 形式(filename*)で渡す。古いブラウザ向けに ASCII の代替名も付ける
        String encoded = URLEncoder.encode(dto.getFileName(), "UTF-8").replace("+", "%20");
        response.setHeader("Content-Disposition",
            (inline ? "inline" : "attachment")
            + "; filename=\"download." + dto.getFileType() + "\""
            + "; filename*=UTF-8''" + encoded);
 
        response.setContentLength((int) Files.size(path));     // 上限5MBなので int に収まる
        Files.copy(path, response.getOutputStream());
 
        return null;    // 自分でレスポンスを書き込んだので、Struts による画面遷移はしない
    }
}
