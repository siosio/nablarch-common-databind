package nablarch.common.databind.fixedlength;

public interface LayoutCondition {

    String getLayoutName(byte[] record);

    class SingleLayoutCondition implements LayoutCondition {
        @Override
        public String getLayoutName(final byte[] record) {
            throw new UnsupportedOperationException();
        }
    }
}
