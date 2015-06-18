package be.fabrice.comparehql.appi;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity(name="TravailleurPme")
@Table(name="TRAV")
public class Travailleur {
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	@Column(name="NUM")
	private String numeroTravailleur;
	@OneToMany(mappedBy="travailleur")
	private List<Contrat> contrats;
	@ManyToOne
	@JoinColumn(name="RP_FK")
	private RelevePrestation relevePrestation;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getNumeroTravailleur() {
		return numeroTravailleur;
	}
	public void setNumeroTravailleur(String numeroTravailleur) {
		this.numeroTravailleur = numeroTravailleur;
	}
	public List<Contrat> getContrats() {
		return contrats;
	}
	public void setContrats(List<Contrat> contrats) {
		this.contrats = contrats;
	}
	public RelevePrestation getRelevePrestation() {
		return relevePrestation;
	}
	public void setRelevePrestation(RelevePrestation relevePrestation) {
		this.relevePrestation = relevePrestation;
	}
}
