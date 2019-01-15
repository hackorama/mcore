package com.hackorama.mcore.server.spring;

import java.lang.reflect.Method;

import com.hackorama.mcore.common.HttpMethod;
import com.hackorama.mcore.server.Server;

public class SpringServer implements Server {

	@Override
	public boolean start() {
		Application.start();
		return true;
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setRoutes(HttpMethod method, String path, Method handler) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeRoutes(String path) {
		// TODO Auto-generated method stub
		
	}

}
