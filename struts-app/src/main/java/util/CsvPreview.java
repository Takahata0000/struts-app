package util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


/**
 * CSV の先頭部分を読み込んで、画面表示用のデータにする。
 *
 *  ・文字コードは自動判定する(UTF-8 として読めなければ Shift_JIS(MS932))
 *    → Excel で保存した日本語 CSV は、Shift_JIS が多い
 *  ・ダブルクォートで囲まれた項目(カンマ・改行・「""」を含むもの)に対応
 *  ・1行目を見出しとして扱う
 */
public final class CsvPreview {
 
    /** 表示する最大列数 */
    private static final int MAX_COLUMNS = 30;
 
    /** 読み込み結果 */
    public static final class Result {
        public final List<String> header;
        public final List<List<String>> rows;
        public final boolean truncated;       // maxRows を超えて省略したか
        public final String charsetName;      // 判定した文字コード(画面に表示する)
 
        Result(List<String> header, List<List<String>> rows, boolean truncated, String charsetName) {
            this.header = header;
            this.rows = rows;
            this.truncated = truncated;
            this.charsetName = charsetName;
        }
    }
 
    private CsvPreview() { }
 
    /** @param maxRows 見出しを除いて表示する最大行数 */
    public static Result read(Path path, int maxRows) throws IOException {
        byte[] bytes = Files.readAllBytes(path);        // 上限5MBなのでそのまま読み込む
 
        String text;
        String charsetName;
        try {
            text = StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .decode(ByteBuffer.wrap(bytes)).toString();
            charsetName = "UTF-8";
        } catch (CharacterCodingException e) {
            text = new String(bytes, Charset.forName("windows-31j"));
            charsetName = "Shift_JIS (MS932)";
        }
        if (!text.isEmpty() && text.charAt(0) == '\uFEFF') {   // BOM を取り除く
            text = text.substring(1);
        }
 
        // 見出し1行 + データ maxRows 行 + 「省略したか」の判定用に1行
        List<List<String>> all = parse(text, maxRows + 2);
 
        boolean truncated = all.size() > maxRows + 1;
        if (truncated) {
            all = new ArrayList<List<String>>(all.subList(0, maxRows + 1));
        }
        if (all.isEmpty()) {
            return new Result(new ArrayList<String>(), new ArrayList<List<String>>(), false, charsetName);
        }
 
        // 列数をそろえる(足りない行は空欄で埋める)
        int columns = 0;
        for (List<String> r : all) {
            columns = Math.max(columns, r.size());
        }
        columns = Math.min(columns, MAX_COLUMNS);
        List<List<String>> normalized = new ArrayList<List<String>>();
        for (List<String> r : all) {
            List<String> n = new ArrayList<String>(columns);
            for (int i = 0; i < columns; i++) {
                n.add(i < r.size() ? r.get(i) : "");
            }
            normalized.add(n);
        }
 
        List<String> header = normalized.get(0);
        List<List<String>> rows = new ArrayList<List<String>>(normalized.subList(1, normalized.size()));
        return new Result(header, rows, truncated, charsetName);
    }
 
    /** RFC 4180 に沿った簡易パーサー。limit 行ぶん読んだら打ち切る。 */
    private static List<List<String>> parse(String text, int limit) {
        List<List<String>> rows = new ArrayList<List<String>>();
        List<String> row = new ArrayList<String>();
        StringBuilder cell = new StringBuilder();
        boolean inQuotes = false;
        int len = text.length();
 
        for (int i = 0; i < len; i++) {
            char c = text.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < len && text.charAt(i + 1) == '"') {   // "" は " 1文字
                        cell.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    cell.append(c);
                }
            } else if (c == '"') {
                inQuotes = true;
            } else if (c == ',') {
                row.add(cell.toString());
                cell.setLength(0);
            } else if (c == '\r' || c == '\n') {
                if (c == '\r' && i + 1 < len && text.charAt(i + 1) == '\n') {
                    i++;                                              // CRLF は1回の改行
                }
                row.add(cell.toString());
                cell.setLength(0);
                if (!(row.size() == 1 && row.get(0).isEmpty())) {     // 空行は読み飛ばす
                    rows.add(row);
                    if (rows.size() >= limit) {
                        return rows;
                    }
                }
                row = new ArrayList<String>();
            } else {
                cell.append(c);
            }
        }
        // 最終行(改行で終わっていない場合)
        if (cell.length() > 0 || !row.isEmpty()) {
            row.add(cell.toString());
            if (!(row.size() == 1 && row.get(0).isEmpty())) {
                rows.add(row);
            }
        }
        return rows;
    }
}
