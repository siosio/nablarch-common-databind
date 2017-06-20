package nablarch.common.databind.csv;

import java.text.MessageFormat;

import nablarch.common.databind.DataBindConfig;
import nablarch.common.databind.DataBindConfigCreator;
import nablarch.common.databind.DataBindUtil;

public class CsvDataBindConfigCreator implements DataBindConfigCreator<Csv>{
    @Override
    public <BEAN> DataBindConfig create(Class<BEAN> beanClass) {
        Csv csv = beanClass.getAnnotation(Csv.class);
        verifyCsvConfig(beanClass, csv);

        CsvFormat csvFormat = beanClass.getAnnotation(CsvFormat.class);
        verifyCsvFormat(beanClass, csv, csvFormat);

        CsvDataBindConfig result;
        if (csvFormat == null) {
            result = (CsvDataBindConfig) csv.type().getConfig();
        } else {
            result = CsvDataBindConfig.DEFAULT
                    .withFieldSeparator(csvFormat.fieldSeparator())
                    .withLineSeparator(csvFormat.lineSeparator())
                    .withQuote(csvFormat.quote())
                    .withIgnoreEmptyLine(csvFormat.ignoreEmptyLine())
                    .withRequiredHeader(csvFormat.requiredHeader())
                    .withCharset(csvFormat.charset())
                    .withEmptyToNull(csvFormat.emptyToNull())
                    .withQuoteMode(csvFormat.quoteMode());
        }

        if (result.getQuoteMode() == CsvDataBindConfig.QuoteMode.CUSTOM) {
            result = result.withQuotedColumnNames(DataBindUtil.findQuotedItemList(beanClass));
        }

        if (result.isRequiredHeader()) {
            if (csv.headers().length == csv.properties().length) {
                result = result.withHeaderTitles(csv.headers());
            } else {
                throw new IllegalStateException(MessageFormat.format(
                        "headers and properties size does not match. class = [{0}]", beanClass.getName()));
            }
        }

        return result;
    }

    @Override
    public Class<Csv> type() {
        return Csv.class;
    }

    /**
     * CSVの設定が正しいことを検証する。
     * @param clazz Beanクラス
     * @param csv CSV設定
     * @param <T> Beanクラスの型
     */
    private static <T> void verifyCsvConfig(Class<T> clazz, Csv csv) {
        if (csv == null) {
            throw new IllegalStateException(MessageFormat.format(
                    "can not find config. class = [{0}]", clazz.getName()));
        }

        if (csv.properties().length == 0) {
            throw new IllegalStateException(MessageFormat.format(
                    "properties is required. class = [{0}]", clazz.getName()));
        }
    }

    /**
     * CSVフォーマットの設定が正しいことを検証する。
     * @param clazz Beanクラス
     * @param csv CSV設定
     * @param csvFormat CSVフォーマット
     * @param <T> Beanクラスの型
     */
    private static <T> void verifyCsvFormat(Class<T> clazz, Csv csv, CsvFormat csvFormat) {
        if (csv.type() != Csv.CsvType.CUSTOM && csvFormat != null) {
            throw new IllegalStateException(MessageFormat.format(
                    "CsvFormat annotation can not defined because CsvType is not CUSTOM. class = [{0}]", clazz.getName()));
        }
    }
}
