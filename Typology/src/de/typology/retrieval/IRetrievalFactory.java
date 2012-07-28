package de.typology.retrieval;

import de.typology.requests.IRequest;

public interface IRetrievalFactory {

	public abstract IRetrieval getInstanceOfPrimitiveRetrieval(
			IRequest request);

	public abstract IRetrieval getInstanceOfRetrieval(IRequest request);

}