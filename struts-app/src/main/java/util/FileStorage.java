package util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.upload.FormFile;

public class FileStorage {
	private static final Log log = LogFactory.getLog(FileStorage.class);
	 
    /** アプリ側のサイズ上限(5MB) */
    public static final long MAX_SIZE = 5L * 1024 * 1024;
 
    private static final Set<String> ALLOWED = new HashSet<String>(Arrays.asList("pdf", "csv"));
 
    private static final Path BASE_DIR;
 
    static {
        Properties p = new Properties();
        try (InputStream in = FileStorage.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new IllegalStateException("db.properties が見つかりません");
            }
            p.load(in);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
        String dir = p.getProperty("upload.dir");
        if (dir == null || dir.trim().isEmpty()) {
            throw new IllegalStateException("db.properties に upload.dir が設定されていません");
        }
        BASE_DIR = Paths.get(dir.trim()).toAbsolutePath().normalize();
    }
 
    private FileStorage() { }
 
    /** ブラウザによってはフルパスで送られるため、ファイル名の部分だけを取り出す。 */
    public static String originalNameOf(FormFile file) {
        String name = file.getFileName();
        if (name == null) {
            return "";
        }
        int i = Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\'));
        return (i >= 0 ? name.substring(i + 1) : name).trim();
    }
 
    /** 拡張子(小文字)。無ければ空文字。 */
    public static String extensionOf(String fileName) {
        int i = fileName.lastIndexOf('.');
        if (i < 0 || i == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(i + 1).toLowerCase(Locale.ROOT);
    }
 
    public static boolean isAllowedExtension(String ext) {
        return ALLOWED.contains(ext);
    }
 
    /** PDF の先頭が「%PDF-」で始まるか(拡張子だけ偽装したファイルを弾く)。 */
    public static boolean looksLikePdf(FormFile file) throws IOException {
        byte[] head = new byte[5];
        try (InputStream in = file.getInputStream()) {
            int n = 0;
            while (n < head.length) {
                int r = in.read(head, n, head.length - n);
                if (r < 0) {
                    break;
                }
                n += r;
            }
            return n == head.length
                && head[0] == '%' && head[1] == 'P' && head[2] == 'D' && head[3] == 'F' && head[4] == '-';
        }
    }
 
    /**
     * ファイルを保存し、upload.dir からの相対パスを返す(例: 2026/10/xxxxxxxx.pdf)。
     * @param ext 検証済みの拡張子(pdf / csv)
     */
    public static String save(FormFile file, String ext) throws IOException {
        LocalDate today = LocalDate.now();
        String relative = today.getYear() + "/" + String.format("%02d", today.getMonthValue())
                        + "/" + UUID.randomUUID() + "." + ext;
 
        Path target = resolve(relative);
        Files.createDirectories(target.getParent());
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target);
        }
        log.info("ファイルを保存しました: " + target);
        return relative;
    }
 
    /** 相対パスを実際のパスに変換する。upload.dir の外を指していたら例外にする。 */
    public static Path resolve(String relativePath) throws IOException {
        Path p = BASE_DIR.resolve(relativePath).normalize();
        if (!p.startsWith(BASE_DIR)) {
            throw new IOException("不正なパスです: " + relativePath);
        }
        return p;
    }
 
    /** 保存済みファイルを削除する。失敗してもログに残すだけで、例外は投げない。 */
    public static void deleteQuietly(String relativePath) {
        if (relativePath == null) {
            return;
        }
        try {
            Files.deleteIfExists(resolve(relativePath));
        } catch (IOException e) {
            log.warn("ファイルの削除に失敗しました: " + relativePath, e);
        }
    }
}
