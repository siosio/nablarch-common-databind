package nablarch.common.databind;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;

import nablarch.common.databind.csv.BeanCsvMapper;
import nablarch.common.databind.csv.CsvBeanMapper;
import nablarch.common.databind.csv.CsvDataBindConfig;
import nablarch.common.databind.csv.CsvMapMapper;
import nablarch.common.databind.csv.MapCsvMapper;
import nablarch.common.databind.fixedlength.FixedLengthBeanMapper;
import nablarch.common.databind.fixedlength.FixedLengthDatBindConfig;

public class BasicObjectMapperFactory extends ObjectMapperFactory {

    @Override
    public <T> ObjectMapper<T> createMapper(
            final Class<T> clazz, final InputStream stream) {

        final DataBindConfig dataBindConfig = DataBindUtil.createDataBindConfig(clazz);
        final MapperType type = toMapperType(clazz, dataBindConfig);
        if (type == MapperType.CSV_BEAN || type == MapperType.FIXED_BEAN) {
            return type.createMapper(clazz, dataBindConfig, stream);
        }
        // 到達しない
        throw new IllegalArgumentException("Unsupported config or class. class = [" + toFQCN(clazz) + "],"
                + " config = [" + toFQCN(dataBindConfig) + ']');
    }

    /**
     * {@link ObjectMapper}を生成する。
     *
     * @param clazz データとのバインディングを行うクラス
     * @param stream 入力ストリーム
     * @param dataBindConfig マッピング設定
     * @param <T> バインディング対象のJavaのクラス
     * @return データとJava ObjectのMapper
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> ObjectMapper<T> createMapper(
            final Class<T> clazz, final InputStream stream, final DataBindConfig dataBindConfig) {

        final MapperType type = toMapperType(clazz, dataBindConfig);
        if (type == MapperType.CSV_BEAN) {
            throw new IllegalArgumentException("this class should not be set config. class = [" + toFQCN(clazz) + ']');
        } else if (type == MapperType.CSV_MAP) {
            return type.createMapper(clazz, dataBindConfig, stream);
        }
        // 到達しない
        throw new IllegalArgumentException("Unsupported config or class. class = [" + toFQCN(clazz) + "],"
                + " config = [" + toFQCN(dataBindConfig) + ']');
    }

    /**
     * {@link ObjectMapper}を生成する。
     *
     * @param clazz データとのバインディングを行うクラス
     * @param reader 入力ストリーム
     * @param <T> バインディング対象のJavaのクラス
     * @return データとJava ObjectのMapper
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> ObjectMapper<T> createMapper(
            final Class<T> clazz, final Reader reader) {
        final DataBindConfig dataBindConfig = DataBindUtil.createDataBindConfig(clazz);
        final MapperType type = toMapperType(clazz, dataBindConfig);

        if (type == MapperType.CSV_BEAN) {
            return type.createMapper(clazz, dataBindConfig, reader);
        }

        // 到達しない
        throw new IllegalArgumentException("Unsupported config or class. class = [" + toFQCN(clazz) + "],"
                + " config = [" + toFQCN(dataBindConfig) + ']');
    }

    /**
     * {@link ObjectMapper}を生成する。
     *
     * @param clazz データとのバインディングを行うクラス
     * @param reader 入力ストリーム
     * @param dataBindConfig マッピング設定
     * @param <T> バインディング対象のJavaのクラス
     * @return データとJava ObjectのMapper
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> ObjectMapper<T> createMapper(
            final Class<T> clazz, final Reader reader, final DataBindConfig dataBindConfig) {
        final MapperType type = toMapperType(clazz, dataBindConfig);

        if (type == MapperType.CSV_BEAN) {
            throw new IllegalArgumentException("this class should not be set config. class = [" + toFQCN(clazz) + ']');
        } else if (type == MapperType.CSV_MAP) {
            return type.createMapper(clazz, dataBindConfig, reader);
        }
        // 到達しない
        throw new IllegalArgumentException("Unsupported config or class. class = [" + toFQCN(clazz) + "],"
                + " config = [" + toFQCN(dataBindConfig) + ']');
    }

    /**
     * {@link ObjectMapper}を生成する。
     *
     * @param clazz データとのバインディングを行うクラス
     * @param stream 出力ストリーム
     * @param <T> バインディング対象のJavaのクラス
     * @return データとJava ObjectのMapper
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> ObjectMapper<T> createMapper(
            final Class<T> clazz, final OutputStream stream) {
        final DataBindConfig dataBindConfig = DataBindUtil.createDataBindConfig(clazz);
        final MapperType type = toMapperType(clazz, dataBindConfig);

        if (type == MapperType.CSV_BEAN) {
            final CsvDataBindConfig config = CsvDataBindConfig.class.cast(dataBindConfig);
            return new BeanCsvMapper<T>(clazz, config, stream);
        }
        // 到達しない
        throw new IllegalArgumentException("Unsupported config or class. class = [" + toFQCN(clazz) + "],"
                + " config = [" + toFQCN(dataBindConfig) + ']');
    }

    /**
     * {@link ObjectMapper}を生成する。
     *
     * @param clazz データとのバインディングを行うクラス
     * @param stream 出力ストリーム
     * @param dataBindConfig マッピング設定
     * @param <T> バインディング対象のJavaのクラス
     * @return データとJava ObjectのMapper
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> ObjectMapper<T> createMapper(
            final Class<T> clazz, final OutputStream stream, final DataBindConfig dataBindConfig) {
        final MapperType type = toMapperType(clazz, dataBindConfig);

        if (type == MapperType.CSV_BEAN) {
            throw new IllegalArgumentException("this class should not be set config. class = [" + toFQCN(clazz) + ']');
        } else if (type == MapperType.CSV_MAP) {
            final CsvDataBindConfig config = CsvDataBindConfig.class.cast(dataBindConfig);
            return (ObjectMapper<T>) new MapCsvMapper(config, stream);
        }
        // 到達しない
        throw new IllegalArgumentException("Unsupported config or class. class = [" + toFQCN(clazz) + "],"
                + " config = [" + toFQCN(dataBindConfig) + ']');
    }

    /**
     * {@link ObjectMapper}を生成する。
     *
     * @param clazz データとのバインディングを行うクラス
     * @param writer Writer
     * @param <T> バインディング対象のJavaのクラス
     * @return データとJava ObjectのMapper
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> ObjectMapper<T> createMapper(
            final Class<T> clazz, final Writer writer) {
        final DataBindConfig dataBindConfig = DataBindUtil.createDataBindConfig(clazz);
        final MapperType type = toMapperType(clazz, dataBindConfig);

        if (type == MapperType.CSV_BEAN) {
            final CsvDataBindConfig config = CsvDataBindConfig.class.cast(dataBindConfig);
            return new BeanCsvMapper<T>(clazz, config, writer);
        }
        // 到達しない
        throw new IllegalArgumentException("Unsupported config or class. class = [" + toFQCN(clazz) + "],"
                + " config = [" + toFQCN(dataBindConfig) + ']');
    }

    /**
     * {@link ObjectMapper}を生成する。
     *
     * @param clazz データとのバインディングを行うクラス
     * @param writer Writer
     * @param dataBindConfig マッピング設定
     * @param <T> バインディング対象のJavaのクラス
     * @return データとJava ObjectのMapper
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> ObjectMapper<T> createMapper(
            final Class<T> clazz, final Writer writer, final DataBindConfig dataBindConfig) {
        final MapperType type = toMapperType(clazz, dataBindConfig);

        if (type == MapperType.CSV_BEAN) {
            throw new IllegalArgumentException("this class should not be set config. class = [" + toFQCN(clazz) + ']');
        } else if (type == MapperType.CSV_MAP) {
            final CsvDataBindConfig config = CsvDataBindConfig.class.cast(dataBindConfig);
            return (ObjectMapper<T>) new MapCsvMapper(config, writer);
        }
        // 到達しない
        throw new IllegalArgumentException("Unsupported config or class. class = [" + toFQCN(clazz) + "],"
                + " config = [" + toFQCN(dataBindConfig) + ']');
    }

    /**
     * マッパーのタイプを返す。
     *
     * @param clazz クラス
     * @param dataBindConfig 設定情報
     * @return マッパーのタイプ
     */
    protected MapperType toMapperType(final Class<?> clazz, final DataBindConfig dataBindConfig) {
        if (Map.class.isAssignableFrom(clazz) && dataBindConfig instanceof CsvDataBindConfig) {
            return MapperType.CSV_MAP;
        } else if (dataBindConfig instanceof CsvDataBindConfig) {
            return MapperType.CSV_BEAN;
        } else if (dataBindConfig instanceof FixedLengthDatBindConfig) {
            return MapperType.FIXED_BEAN;
        }
        throw new IllegalArgumentException("Unsupported config or class. class = [" + toFQCN(clazz) + "],"
                + " config = [" + toFQCN(dataBindConfig) + ']');
    }

    /**
     * クラスからFQCNを返す。
     * <p/>
     * nullの場合は、文字列のnullを
     *
     * @param object オブジェクト
     * @return FQCN
     */
    private String toFQCN(final Object object) {
        if (object == null) {
            return "null";
        }
        if (object instanceof Class) {
            return ((Class<?>) object).getName();
        } else {
            return object.getClass()
                    .getName();
        }
    }

    /**
     * マッパータイプ。
     */
    enum MapperType {
        /** CSVとBeanとのマッパー */
        CSV_BEAN() {
            @Override
            <T> ObjectMapper<T> createMapper(final Class<T> clazz, final DataBindConfig config,
                    final InputStream stream) {
                final CsvDataBindConfig csvDataBindConfig = CsvDataBindConfig.class.cast(config);
                return new CsvBeanMapper<T>(clazz, csvDataBindConfig, stream);
            }

            @Override
            <T> ObjectMapper<T> createMapper(final Class<T> clazz, final DataBindConfig config, final Reader reader) {
                final CsvDataBindConfig csvDataBindConfig = CsvDataBindConfig.class.cast(config);
                return new CsvBeanMapper<T>(clazz, csvDataBindConfig, reader);
            }
        },
        /** CSVとMapとのマッパー */
        CSV_MAP {
            @Override
            <T> ObjectMapper<T> createMapper(final Class<T> clazz, final DataBindConfig config,
                    final InputStream stream) {
                final CsvDataBindConfig csvDataBindConfig = CsvDataBindConfig.class.cast(config);
                return (ObjectMapper<T>) new CsvMapMapper(csvDataBindConfig, stream);
            }

            @Override
            <T> ObjectMapper<T> createMapper(final Class<T> clazz, final DataBindConfig config, final Reader reader) {
                final CsvDataBindConfig csvDataBindConfig = CsvDataBindConfig.class.cast(config);
                return (ObjectMapper<T>) new CsvMapMapper(csvDataBindConfig, reader);
            }
        },
        FIXED_BEAN {
            @Override
            <T> ObjectMapper<T> createMapper(final Class<T> clazz, final DataBindConfig config,
                    final InputStream stream) {
                final FixedLengthDatBindConfig csvDataBindConfig = FixedLengthDatBindConfig.class.cast(config);
                return new FixedLengthBeanMapper<T>(clazz, csvDataBindConfig, stream);
            }

            @Override
            <T> ObjectMapper<T> createMapper(final Class<T> clazz, final DataBindConfig config, final Reader reader) {
                throw new UnsupportedOperationException("");
            }
        };


        abstract <T> ObjectMapper<T> createMapper(Class<T> clazz, DataBindConfig config, InputStream stream);

        abstract <T> ObjectMapper<T> createMapper(Class<T> clazz, DataBindConfig config, Reader reader);
    }
}
