package com.srodrigues.test.rmi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

import com.srodrigues.test.Constants;
import com.srodrigues.test.rmi.CourierTasks;

public class Courier implements CourierTasks {

	private final CourierTasks courier ;
	
	// delegate methods from the CourierTasks interface to handler object
	@Override
	public boolean add(final String task) throws RemoteException {
		return courier.add(task);
	}

	@Override
	public boolean delete(final String task) throws RemoteException {
		return courier.delete(task);
	}

	@Override
	public List<String> get() throws RemoteException {
		return courier.get();
	}

	public Courier() throws MalformedURLException, IOException, NotBoundException {
		Registry registry = LocateRegistry.getRegistry(Constants.SERVER, Constants.PORT);
		this.courier =(CourierTasks) registry.lookup(Constants.SERVICE);
	}
}
