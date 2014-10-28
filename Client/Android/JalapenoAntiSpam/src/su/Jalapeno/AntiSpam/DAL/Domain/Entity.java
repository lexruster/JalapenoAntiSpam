package su.Jalapeno.AntiSpam.DAL.Domain;

import com.j256.ormlite.field.DatabaseField;

public class Entity {
	@DatabaseField(generatedId = true)
	private int Id;

	public int GetId() {
		return Id;
	}
}
