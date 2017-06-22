package nablarch.common.databind.fixedlength;

public interface MultiLayout {

    void setRecordName(RecordName recordName);

    RecordName getRecordName();

    RecordName getLayoutName(byte[] line);

    interface RecordName {
        String getRecordName();
    }
}
