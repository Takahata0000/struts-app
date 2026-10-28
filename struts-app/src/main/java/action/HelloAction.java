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
import org.apache.struts.upload.FormFile;

import dao.ItemDao;
import form.HelloForm;
import util.FileStorage;

//商品登録処理
public class HelloAction extends Action{
	//ログ用変数	
	private static final Log log = LogFactory.getLog(HelloAction.class);
	
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
    			HelloForm f = (HelloForm) form;
    			
    			String fileName = null;   // DBに保存する元のファイル名
    	        String filePath = null;   // DBに保存する相対パス
    	        String fileType = null;   // DBに保存する種別(pdf / csv)
    	        //-----------------------------------------//
    	        // ファイル保存処理  	 
    	        FormFile file = f.getFile();
    	        if (file != null && file.getFileName() != null && file.getFileName().length() > 0) {
    	 
    	            String original = FileStorage.originalNameOf(file);
    	            String ext = FileStorage.extensionOf(original);
    	 
    	            ActionMessages fileErrors = new ActionMessages();
    	            if (!FileStorage.isAllowedExtension(ext)) {
    	                fileErrors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.file.type"));
    	            } else if (file.getFileSize() == 0) {
    	                fileErrors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.file.empty"));
    	            } else if (file.getFileSize() > FileStorage.MAX_SIZE) {
    	                fileErrors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.file.size"));
    	            } else if ("pdf".equals(ext) && !FileStorage.looksLikePdf(file)) {
    	                fileErrors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.file.content"));
    	            }
    	            if (!fileErrors.isEmpty()) {
    	                saveErrors(request, fileErrors);
    	                return mapping.getInputForward();      // 登録画面に戻る
    	            }
    	 
    	            try {
    	                filePath = FileStorage.save(file, ext);
    	                fileName = original;
    	                fileType = ext;
    	            } catch (IOException e) {
    	                log.error("ファイルの保存に失敗しました。", e);
    	                fileErrors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.file.save"));
    	                saveErrors(request, fileErrors);
    	                return mapping.getInputForward();
    	            } finally {
    	                file.destroy();                         // 一時ファイルを解放
    	            }
    	        }
    	        log.info("fileName=" + fileName + ", filePath=" + filePath + ", fileType=" + fileType);
    	        //-----------------------------------------//
    	        // DB登録処理
    			try {
    	            new ItemDao().insert(f.getName(), f.getPrice(), f.getCategory(), "system",
                            fileName, filePath, fileType);
    	            
    	        } catch (SQLException e) {
    	        	e.printStackTrace(); 
    	            // ログ出力 + エラーメッセージを画面に返す
    	            ActionMessages errors = new ActionMessages();
    	            errors.add(ActionMessages.GLOBAL_MESSAGE,
    	                       new ActionMessage("errors.db.insert"));
    	            saveErrors(request, errors);
    	            // DB登録に失敗したら、先に保存したファイルが残らないように消す(ゴミファイル防止)
    	            FileStorage.deleteQuietly(filePath);
    	            log.error("商品登録に失敗しました。", e);	
    	            return mapping.getInputForward();
    	        }
    			//-----------------------------------------//
        return mapping.findForward("success");
    } 
    
}
