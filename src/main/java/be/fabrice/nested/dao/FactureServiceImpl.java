package be.fabrice.nested.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import be.fabrice.nested.entity.Facture;
import be.fabrice.nested.entity.Ligne;

@Component
@Transactional
public class FactureServiceImpl implements FactureService {
	@Autowired
	private FactureDao factureDao;
	
	/**
	 * La transaction est en REQUIRES_NEW parce que le test est lui-même transactionnel
	 */
	@Transactional(readOnly=false,propagation=Propagation.REQUIRES_NEW)
	public void save(Facture facture, List<Ligne> lignes) {
		factureDao.save(facture);
		for(Ligne l:lignes){
			try{
				factureDao.save(l);
				facture.setnLignes(facture.getnLignes()+1);
				facture.addLigne(l);
			} catch(RuntimeException e){
				//LOG rejet d'un ligne
			}
		}
		
		if(facture.getnLignes()==1){
			throw new RuntimeException("rollback");
		}
	}

}
