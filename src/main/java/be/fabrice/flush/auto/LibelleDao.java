package be.fabrice.flush.auto;

public interface LibelleDao {
	Libelle find(Integer id);
	
	Libelle findByLabelStandard(String label);
	
	ImmutableLibelle findImmutableByLabelStandard(String label);

	Libelle findByLabelNewTransaction(String label);
	
	Libelle findByLabelFlushModeManual(String label);
}
