package be.fabrice.transaction.exception;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SecondImpl implements Second {

	@Override
	@Transactional
	public void bar1() {
		try{
			throw new RuntimeException();
		} catch(RuntimeException e)
		{}
	}

	@Override
	@Transactional
	public void bar2() {
		throw new RuntimeException();
	}

}
