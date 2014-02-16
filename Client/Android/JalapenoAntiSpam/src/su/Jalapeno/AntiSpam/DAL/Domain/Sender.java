package su.Jalapeno.AntiSpam.DAL.Domain;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by alexander.kiryushkin on 09.01.14.
 */

@DatabaseTable(tableName = "Senders")
public class Sender {
    public static final String SENDER_FIELD_NAME = "SenderId";
    
    @DatabaseField(generatedId = true)
    private int Id;
    
    @DatabaseField(canBeNull = false, dataType = DataType.STRING, index = true, columnName = SENDER_FIELD_NAME)
    public String SenderId;
    
    @DatabaseField(canBeNull = false, dataType = DataType.BOOLEAN)
    public boolean IsSpammer;

    public Sender() {

    }
}
