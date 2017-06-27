package nablarch.common.databind.fixedlength;

/**
 * マルチレイアウトなファイルを扱うインタフェース。
 * 
 */
public abstract class MultiLayout {

    private RecordName recordName;

    /**
     * 現在のレコードの名前を設定する。
     * @param recordName
     */
    public void setRecordName(final RecordName recordName) {
        this.recordName = recordName;
    }

    /**
     * 現在のレコードの名前を返す。
     * @return
     */
    public RecordName getRecordName() {
        return recordName;
    }

    /**
     * 現在のレコードの名前をレコードから判定し返す。
     * @param line
     * @return
     */
    public abstract RecordName getLayoutName(byte[] line);

    interface RecordName {
        String getRecordName();
    }
}
