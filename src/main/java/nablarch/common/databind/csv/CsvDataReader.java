package nablarch.common.databind.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nablarch.common.databind.DataReader;

/**
 * CSVの解析を行うクラス。
 *
 * @author Naoki Yamamoto
 */
public class CsvDataReader implements DataReader<String[]> {

    /** CSVの要素を分解して扱う{@link CsvTokenizer} */
    private final CsvTokenizer tokenizer;

    /** 入力リソース */
    private final BufferedReader reader;

    /**
     * コンストラクタ
     *
     * @param bufferedReader 解析を行うCSVの{@link BufferedReader}
     */
    public CsvDataReader(final BufferedReader bufferedReader) {
        this(bufferedReader, CsvDataBindConfig.DEFAULT);
    }

    /**
     * コンストラクタ
     *
     * @param bufferedReader 解析を行うCSVの{@link BufferedReader}
     * @param format CSVのフォーマットを定義した{@link CsvDataBindConfig}
     */
    public CsvDataReader(final BufferedReader bufferedReader, final CsvDataBindConfig format) {
        reader = bufferedReader;
        this.tokenizer = new CsvTokenizer(bufferedReader, format);
    }


    /**
     * CSVの解析を行い、1レコード分のデータを格納した{@link String}の配列を生成する。
     * <p/>
     * ファイルの終端に達した場合には、{@code null}を返す。
     *
     * @return 1レコード分のデータを格納した{@link String}の配列
     */
    @Override
    public String[] read() {
        try {
            if (tokenizer.isEndOfFile()) {
                // ファイルの終端に達している場合はnullを返す
                return null;
            } else {
                final List<String> record = readLine();
                return record.toArray(new String[record.size()]);
            }
        } catch (IOException e) {
            throw new RuntimeException("failed to read file.", e);
        }
    }

    /**
     * 1レコード分のデータを読み込む。
     *
     * @return 1レコード分のデータ
     * @throws IOException ファイルアクセスに失敗した場合
     */
    private List<String> readLine() throws IOException {
        final List<String> record = new ArrayList<String>();
        tokenizer.reset();
        while (!tokenizer.isEndOfLine()) {
            record.add(tokenizer.next());
        }
        return record;
    }

    /**
     * 現在のレコード番号を返す。
     *
     * @return レコード番号
     */
    public long getLineNumber() {
        return tokenizer.getLineNumber();
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
