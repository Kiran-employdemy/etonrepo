package com.eaton.platform.integration.endeca.bean;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

/**
 * <html> Description: This class is used to get the status details from a request.
 *
 * @author Katherine C Milby
 * @version 1.0
 * @since 2018
 *
 */
public class StatusDetailsBean implements Serializable {

	private static final long serialVersionUID = -430115710398932049L;
	private List<String> messages;
	
	
    public void setMessages(List<String> messages) {
    	this.messages = messages;
    }
    public List<String> getMessages() {
    	return messages;
    }

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		StatusDetailsBean that = (StatusDetailsBean) o;
		return Objects.equals(messages, that.messages);
	}

	@Override
	public int hashCode() {
		return Objects.hash(messages);
	}

	/**
      * Intended only for debugging.
      *
      * <P>Here, the contents of every field are placed into the result, with
      * one field per line.
      */
 	@Override
    public String toString() {
		StringBuilder result = new StringBuilder();
		String newLine = System.getProperty("line.separator");

		result.append(newLine);

		//determine fields declared in this class only (no fields of superclass)
		Field[] fields = this.getClass().getDeclaredFields();

		//print field names paired with their values
		for (Field field : fields) {
			if(!field.getName().equalsIgnoreCase("serialVersionUID")){
				result.append("  ");
				try {
					result.append(field.getName());
					result.append(": ");
					//requires access to private field:
					result.append(field.get(this));
				} catch (IllegalAccessException ex) {
					return "Error in toString of StatusDetailsBean"+ex.getMessage();
				}
				result.append(newLine);
			}
		}

		return result.toString();
     }
}
