package padtools.core.models;

/**
 * Null値が許可されないフィールドがNullの場合に生じる例外。
 */
public class NullFieldException extends ModelValidationException {
    public NullFieldException(String fieldName){
        super(fieldName + " is null");
    }
}
