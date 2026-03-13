package edu.stevens.cs548.clinic.domain;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProviderFactory implements IProviderFactory {

	@Override
	public Provider createProvider() {
		return new Provider();
	}

}
