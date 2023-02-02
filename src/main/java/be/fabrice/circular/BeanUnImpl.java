package be.fabrice.circular;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly=true)
@Scope("prototype")
public class BeanUnImpl implements BeanUn {
	@Autowired
	private BeanBase beanBase;

	@Override
	public int doit() {
		// TODO Auto-generated method stub
		return 0;
	}

	public BeanBase getBeanBase() {
		return beanBase;
	}
}
