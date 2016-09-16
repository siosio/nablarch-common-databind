package nablarch.common.databind.csv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import org.junit.rules.TemporaryFolder;

/**
 * テストで使うCSVファイルを作成するためのクラス。
 */
public class CsvResource extends TemporaryFolder{

    /** ファイル名 */
    private final String fileName;

    /** 文字コード */
    private final String charSet;

    /** 改行コード */
    private final String lineSeparator;

    /** CSVファイル */
    private BufferedWriter br;

    private BufferedReader reader;


    /**
     * コンストラクタ。
     * @param fileName ファイル名
     * @param charSet エンコード
     */
    public CsvResource(String fileName, String charSet, String lineSeparator) {
        super();
        this.fileName = fileName;
        this.charSet = charSet;
        this.lineSeparator = lineSeparator;
    }

    public void close() throws IOException {
        br.close();
    }

    @Override
    protected void before() throws Throwable {
        super.before();
        this.br = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(getRoot(), fileName)), charSet));
    }

    public void writeLine(String line) throws IOException {
        br.write(line);
        br.write(lineSeparator);
    }

    public BufferedReader createReader() throws FileNotFoundException, UnsupportedEncodingException {
        reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(getRoot(),
                fileName)), charSet));
        return reader;
    }

    public InputStream createInputStream() throws FileNotFoundException {
        return new FileInputStream(new File(getRoot(), fileName));
    }

    @Override
    protected void after() {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException ignored) {
        }
        super.after();
    }
}
