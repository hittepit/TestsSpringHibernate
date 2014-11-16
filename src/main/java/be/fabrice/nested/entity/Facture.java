package be.fabrice.nested.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="FACT")
public class Facture {
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private String num;
	private int nLignes;
	/**
	 * Le mapping inverse ne fonctionne pas car si on donne à la ligne la référence vers la facture,
	 * le dao ne permet pas de la sauver car il essaye d'insérer une FK vers une PK qui n'a pas encore
	 * été insérée (la transaction qui insère la facture n'a pas été commitée).
	 * Si on essaye de mettre à jour la ligne après l'avoir insérée, comme on ne peut le faire que dans
	 * la transaction parente (sinon, problème ci-dessus), l'update ne se fera pas car les lignes ne sont
	 * pas attachées à la session de la facture et ne sont donc pas flushées...
	 */
	@OneToMany()
	@JoinColumn(name="FACT_ID")
	private List<Ligne> lignes;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNum() {
		return num;
	}
	public void setNum(String num) {
		this.num = num;
	}
	public int getnLignes() {
		return nLignes;
	}
	public void setnLignes(int nLignes) {
		this.nLignes = nLignes;
	}
	public void addLigne(Ligne ligne){
		if(lignes==null){
			lignes = new ArrayList<Ligne>();
		}
		lignes.add(ligne);
	}
}
