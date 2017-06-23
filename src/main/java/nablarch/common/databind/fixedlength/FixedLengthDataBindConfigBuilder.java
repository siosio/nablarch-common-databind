package nablarch.common.databind.fixedlength;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nablarch.common.databind.fixedlength.FixedLengthDatBindConfig.FieldDefinition;

public class FixedLengthDataBindConfigBuilder {

    private Charset charset;

    private int length;

    private String lineSeparator;

    private boolean multiLayout;

    private final Map<String, FixedLengthDatBindConfig.RecordDefinition> recordDefinitions = new HashMap<String, FixedLengthDatBindConfig.RecordDefinition>();

    public static FixedLengthDataBindConfigBuilder newBuilder() {
        return new FixedLengthDataBindConfigBuilder();
    }

    public FixedLengthDataBindConfigBuilder charset(final Charset charset) {
        this.charset = charset;
        return this;
    }

    public FixedLengthDataBindConfigBuilder length(final int length) {
        this.length = length;
        return this;
    }

    public FixedLengthDataBindConfigBuilder lineSeparator(final String lineSeparator) {
        this.lineSeparator = lineSeparator;
        return this;
    }

    public FixedLengthDataBindConfigBuilder multiLayout(final boolean multiLayout) {
        this.multiLayout = multiLayout;
        return this;
    }

    public FixedLengthDatBindConfig build() {
        return new FixedLengthDatBindConfig(charset, length, lineSeparator, multiLayout, recordDefinitions);
    }

    public void addLayout(final String recordName, final FixedLengthDatBindConfig.RecordDefinition recordDefinition) {
        this.recordDefinitions.put(recordName, recordDefinition);
    }
}
