package be.fabrice.transaction.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InterfacedServiceImpl implements InterfacedService {
	
	@Transactional
	public String test(){
		throw new RuntimeException();
	}
	
	public void foo(){
		bar();
	}
	
	@Transactional
	public void bar(){
		throw new RuntimeException();
	}

}
