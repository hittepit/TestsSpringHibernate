package be.fabrice.transaction.proxy;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProxiedService {
	
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

	@Transactional
	protected void test2(){
		throw new RuntimeException();
	}

	@Transactional
	void test3(){
		throw new RuntimeException();
	}
}
