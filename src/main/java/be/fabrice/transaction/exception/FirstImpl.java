package be.fabrice.transaction.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FirstImpl implements First {
	@Autowired
	private Second second;
	
	@Override
	@Transactional
	public void foo1() {
		second.bar1();
	}

	@Override
	@Transactional
	public void foo2() {
		try{
			second.bar2();
		} catch(RuntimeException e)
		{}
	}

}
