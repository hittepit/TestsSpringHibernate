package be.fabrice.bidirectionnel.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

/**
 * RowMapper pour mapper (bien la lapalissade...) les données d'un ResultSet
 * vers un {@link TravailleurDto}. Utilisé dans les tests.
 * 
 * @author fabrice.claes
 * 
 */
public class EmployeurDtoRowMapper implements RowMapper<EmployeurDto> {

	public EmployeurDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		EmployeurDto e = new EmployeurDto();
		e.setId(rs.getInt("ID"));
		e.setName(rs.getString("NOM"));
		return e;
	}

}
