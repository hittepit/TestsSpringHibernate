package be.fabrice.cache.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="PERS")
public class Personne {
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private String nom;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="STATUT_ID")
	private Statut statut;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ETAT_ID")
	private EtatCivil etatCivil;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="SIT_ID")
	private Situation situation;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="CIV_ID")
	private Civilite civilite;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public Statut getStatut() {
		return statut;
	}
	public void setStatut(Statut statut) {
		this.statut = statut;
	}
	public EtatCivil getEtatCivil() {
		return etatCivil;
	}
	public void setEtatCivil(EtatCivil etatCivil) {
		this.etatCivil = etatCivil;
	}
	public Situation getSituation() {
		return situation;
	}
	public void setSituation(Situation situation) {
		this.situation = situation;
	}
	public Civilite getCivilite() {
		return civilite;
	}
	public void setCivilite(Civilite civilite) {
		this.civilite = civilite;
	}
}
