package su.Jalapeno.AntiSpam.DAL.Domain;

import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "Sms")
public class Sms extends Entity {
	public static final String SENDER_FIELD_NAME = "SenderId";

	public static final String SMS_DATE_FIELD_NAME = "RecieveDate";

	@DatabaseField(canBeNull = false, dataType = DataType.STRING, index = true, columnName = SENDER_FIELD_NAME)
	public String SenderId;

	@DatabaseField(canBeNull = false, dataType = DataType.STRING)
	public String Text;

	@DatabaseField(canBeNull = false, dataType = DataType.DATE_STRING, format = "yyyy-MM-dd HH:mm:ss", columnName = SMS_DATE_FIELD_NAME)
	public Date RecieveDate;

	@DatabaseField(canBeNull = false, dataType = DataType.INTEGER)
	public int Status;

	public Sms() {
		Status = 0;
		Text = "";
	}
}
