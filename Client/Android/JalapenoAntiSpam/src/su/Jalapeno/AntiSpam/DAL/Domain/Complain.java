package su.Jalapeno.AntiSpam.DAL.Domain;

import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "Compalains")
public class Complain extends Entity {
	public static final String COMPLAIN_DATE_FIELD_NAME = "ComplainDate";

	@DatabaseField(canBeNull = true, dataType = DataType.STRING)
	public String SmsHash;

	@DatabaseField(canBeNull = false, dataType = DataType.STRING)
	public String SenderId;

	@DatabaseField(canBeNull = false, dataType = DataType.DATE_STRING, format = "yyyy-MM-dd HH:mm:ss", index = true, columnName = COMPLAIN_DATE_FIELD_NAME)
	public Date ComplainDate;

	public Complain() {

	}
}
