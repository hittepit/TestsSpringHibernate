package be.fabrice.equals;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

public class IsbnUserType1 implements UserType {

	@Override
	public int[] sqlTypes() {
		return new int[]{Types.VARCHAR};
	}

	@Override
	public Class returnedClass() {
		return Isbn.class;
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		EqualsCounter.tic(this.getClass());
		if(x==null){
			return y==null;
		}
		if(x instanceof Isbn && y instanceof Isbn){
			return ((Isbn)x).getValue().equals(((Isbn)y).getValue());
		}
		return false;
	}

	@Override
	public int hashCode(Object x) throws HibernateException {
		HashcodeCounter.tic(this.getClass());
		return 1;
	}

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException {
		String code = rs.getString(names[0]);
		return rs.wasNull()? null:new Isbn(code);
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
		st.setString(index,((Isbn)value).getValue());
	}

	@Override
	public Object deepCopy(Object value) throws HibernateException {
		return value==null?null:new Isbn(((Isbn)value).getValue());
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Serializable disassemble(Object value) throws HibernateException {
		return ((Isbn)value).getValue();
	}

	@Override
	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		return new Isbn((String)cached);
	}

	@Override
	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		return original;
	}

}
