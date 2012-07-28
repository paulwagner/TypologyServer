/**
 * Factory class that returns instances of various retrievals
 * 
 * @author Paul Wagner
 */
package de.typology.retrieval;

import de.typology.requests.IRequest;

public class RetrievalFactory implements IRetrievalFactory {
	
	/* (non-Javadoc)
	 * @see de.typology.retrieval.IRetrievalFactory#getInstanceOfPrimitiveRetrieval(de.typology.requests.IRequest)
	 */
	@Override
	public PrimitiveRetrieval getInstanceOfPrimitiveRetrieval(IRequest request){
		return new PrimitiveRetrieval(request, request.getLang());
	}
	
	/* (non-Javadoc)
	 * @see de.typology.retrieval.IRetrievalFactory#getInstanceOfRetrieval(de.typology.requests.IRequest)
	 */
	@Override
	public Retrieval getInstanceOfRetrieval(IRequest request){
		return new Retrieval(request, request.getLang());
	}

}
