package be.fabrice.circular;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly=true)
public class BeanBaseImpl implements BeanBase {
	@Autowired
	private BeanUn beanUn;
	
	@Override
	public int doit() {
		// TODO Auto-generated method stub
		return 0;
	}
}
