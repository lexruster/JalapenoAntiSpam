package su.Jalapeno.AntiSpam.DAL.Domain;

import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "Sms")
public class Sms extends Entity{
	public static final String SENDER_FIELD_NAME = "SenderId";

	@DatabaseField(canBeNull = false, dataType = DataType.STRING, index = true, columnName = SENDER_FIELD_NAME)
	public String SenderId;

	@DatabaseField(canBeNull = false, dataType = DataType.STRING, index = true)
	public String Text;

	@DatabaseField(canBeNull = false, dataType = DataType.DATE_TIME, index = true)
	public Date RecieveDate;

	public Sms() {

	}

}
