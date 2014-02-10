package su.Jalapeno.AntiSpam.DAL.Domain;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by alexander.kiryushkin on 09.01.14.
 */

@DatabaseTable(tableName = "SpamerPhone")
public class SpamerPhone {

    public static final String PHONE_FIELD_NAME = "Phone";
    @DatabaseField(generatedId = true)
    private int Id;
    @DatabaseField(canBeNull = false, dataType = DataType.STRING, index = true, columnName = PHONE_FIELD_NAME)
    public String Phone;
    @DatabaseField(canBeNull = false, dataType = DataType.BOOLEAN)
    public boolean IsTrusted;

    public SpamerPhone() {

    }
}
