package su.Jalapeno.AntiSpam.DAL.Domain;

import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "TrashSms")
public class TrashSms extends Sms {

	public TrashSms() {
		super();
	}

	public TrashSms(Sms sms) {
		super();
		SenderId = sms.SenderId;
		RecieveDate = sms.RecieveDate;
		Status = sms.Status;
		Text = sms.Text;
	}
}
