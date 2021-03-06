package su.Jalapeno.AntiSpam.DAL.Domain;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "SmsHashes")
public class SmsHash extends Entity {
    public static final String SMSHASH_FIELD_NAME = "SmsHash";
    
    @DatabaseField(canBeNull = false, dataType = DataType.STRING, index = true, columnName = SMSHASH_FIELD_NAME)
    public String SmsHash;

    public SmsHash() {

    }
}
