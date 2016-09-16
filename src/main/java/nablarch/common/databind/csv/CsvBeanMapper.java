package nablarch.common.databind.csv;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import nablarch.common.databind.DataBindUtil;
import nablarch.common.databind.InvalidDataFormatException;
import nablarch.core.util.StringUtil;

/**
 * CSVフォーマットとJava Beanをマッピングするクラス。
 *
 * @param <T> 読み取り、書き込み対象のJava型
 * @author Hisaaki Sioiri
 */
public class CsvBeanMapper<T> extends CsvObjectMapperSupport<T> {

    /** Beanのクラス */
    private final Class<T> clazz;

    /** プロパティ名の配列 */
    private final String[] propertyNames;

    /** 行番号を格納するプロパティ名 */
    private final String lineNumberPropertyName;

    /**
     * コンストラクタ。
     *
     * @param clazz Beanの{@link Class}
     * @param config CSV用の設定情報
     * @param inputStream 入力ストリーム
     */
    public CsvBeanMapper(final Class<T> clazz, final CsvDataBindConfig config, final InputStream inputStream) {
        this(clazz, config, new InputStreamReader(inputStream, config.getCharset()));
    }

    /**
     * コンストラクタ。
     *
     * @param clazz Beanの{@link Class}
     * @param config CSV用の設定情報
     * @param reader リーダー
     */
    public CsvBeanMapper(final Class<T> clazz, final CsvDataBindConfig config, final Reader reader) {
        super(config, reader);
        this.clazz = clazz;
        propertyNames = DataBindUtil.findCsvProperties(clazz);
        lineNumberPropertyName = DataBindUtil.findLineNumberProperty(clazz);
        readInitialize();
    }

    @Override
    protected T createObject(final String[] record) {
        if (propertyNames.length != record.length) {
            throw new InvalidDataFormatException(
                    "property size does not match. expected field count = [" + propertyNames.length + "],"
                            + " actual field count = [" + record.length + "].", reader.getLineNumber());
        }

        if(StringUtil.isNullOrEmpty(lineNumberPropertyName)){
            return DataBindUtil.getInstance(clazz, propertyNames, record);
        }else{
            return DataBindUtil.getInstanceWithLineNumber(clazz, propertyNames, record, lineNumberPropertyName, reader.getLineNumber());
        }
    }

    /**
     * データ読み込み時の初期処理を行う。
     * <p/>
     * ヘッダーが必須の場合は、ヘッダー行を読み飛ばす必要があるのでここで読みとばす。
     */
    private void readInitialize() {
        // ヘッダー行が必須の場合は読み飛ばす
        if (config.isRequiredHeader()) {
            readHeader();
        }
    }
}

