package be.fabrice.bidirectionnel.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

/**
 * RowMapper pour mapper les données d'un ResultSet vers un
 * {@link TravailleurDto}. Utilisé dans les tests.
 * 
 * @author fabrice.claes
 * 
 */
public class TravailleurDtoRowMapper implements RowMapper<TravailleurDto> {

	public TravailleurDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		TravailleurDto t = new TravailleurDto();
		t.setId(rs.getInt("ID"));
		t.setName(rs.getString("NOM"));
		Object fk = rs.getObject("EMP_ID");
		t.setEmployeurId(fk == null ? null : (Integer) fk);
		return t;
	}
}
