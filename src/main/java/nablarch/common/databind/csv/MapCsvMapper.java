package nablarch.common.databind.csv;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

/**
 * MapをCSVにマッピングするのをサポートするクラス。
 *
 * @author Hisaaki Shioiri
 */
public class MapCsvMapper extends ObjectCsvMapperSupport<Map<String, ?>> {

    /**
     * コンストラクタ。
     *
     * @param config フォーマット定義
     * @param outputStream 出力リソース
     */
    public MapCsvMapper(final CsvDataBindConfig config, final OutputStream outputStream) {
        this(config, new OutputStreamWriter(outputStream, config.getCharset()));
    }

    /**
     * コンストラクタ。
     *
     * @param config フォーマット定義
     * @param writer 出力リソース
     */
    public MapCsvMapper(final CsvDataBindConfig config, final Writer writer) {
        super(config, writer, config.getHeaderTitles());
        verify();
        writeHeader();
    }

    /**
     * オブジェクトの妥当性検証を行う。
     * <p/>
     * ヘッダーが任意の場合やヘッダータイトルが未設定の場合は検証エラーとする。
     */
    private void verify() {
        final String[] headers = config.getHeaderTitles();
        if (!config.isRequiredHeader() || headers == null || headers.length == 0) {
            throw new IllegalArgumentException("csv header is required.");
        }
    }

    @Override
    public Object[] convertValues(final Map<String, ?> object) {
        final String[] headers = config.getHeaderTitles();
        final Object[] fieldValues = new Object[headers.length];
        for (int i = 0; i < headers.length; i++) {
            fieldValues[i] = object.get(headers[i]);
        }
        return fieldValues;
    }
}
