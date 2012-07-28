package de.typology.retrieval;

import de.typology.requests.IRequest;

public interface IRetrievalFactory {

	public abstract PrimitiveRetrieval getInstanceOfPrimitiveRetrieval(
			IRequest request);

	public abstract Retrieval getInstanceOfRetrieval(IRequest request);

}