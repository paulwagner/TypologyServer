/**
 * Impermanent database connection implementation.
 * This class will simulate a little test graph with specified data, wich may be interesting for many purposes...
 * 
 * @author Paul Wagner
 */
package de.typology.db.persistence.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.test.TestGraphDatabaseFactory;

import de.typology.db.DBTools;
import de.typology.db.persistence.IDBConnection;
import de.typology.tools.ConfigHelper;
import de.typology.tools.IOHelper;

public class ImpermanentDBConnection implements IDBConnection {

	// PROPERTIES

	private GraphDatabaseService graph;
	private Index<Node> wordIndex;
	private DynamicRelationshipType[] dn;
	private Node REF_NODE;

	private String DB_PATH = "test";
	private String INDEX_KEY = ConfigHelper.getNAME_KEY();
	private String CACHE_TYPE = ConfigHelper.getCACHE_TYPE();
	private Integer MAX_RELATIONS = ConfigHelper.getMAX_RELATIONS();

	private boolean shutdown = false;

	// CONSTRUCTORS

	/**
	 * Create database with standard text using ConfigHelper
	 * 
	 * @throws Exception
	 */
	public ImpermanentDBConnection() throws Exception {
		this(ConfigHelper.getMAX_RELATIONS(), ConfigHelper.getNAME_KEY(),
				ConfigHelper.getCACHE_TYPE());
	}

	/**
	 * Create database with standard text and speficied config values
	 * 
	 * @param max_relations
	 *            config
	 * @param index_key
	 *            config
	 * @param cache_type
	 *            config
	 * @throws Exception
	 *             config
	 */
	public ImpermanentDBConnection(int max_relations, String index_key,
			String cache_type) throws Exception {
		createDatabase(max_relations, index_key, cache_type);
	}

	// GETTERS

	@Override
	public String getDB_PATH() {
		return "test";
	}

	@Override
	public Boolean isShutdown() {
		return (this.shutdown || this.graph == null);
	}

	@Override
	public EmbeddedGraphDatabase getGraph() {
		return (EmbeddedGraphDatabase) this.graph;
	}

	@Override
	public DynamicRelationshipType[] getDn() {
		return this.dn;
	}

	@Override
	public Index<Node> getWordIndex() {
		return this.wordIndex;
	}

	@Override
	public boolean isReferenceNode(Node n) {
		return (REF_NODE != null && REF_NODE.getId() == n.getId());
	}

	// METHODS

	/**
	 * Get a standard dummy text.
	 * 
	 * TODO: Replace text by one with known metrics
	 */

	private String getStandardText() {
		StringBuilder sb = new StringBuilder();
		sb.append("Obiger Graph ist ein gerichteter benannter Graph.");
		sb.append("Der Graph ist gerichtet.");
		sb.append("Die Beziehung kennt ist im Allgemeinen symmetrisch, die Beziehung liebt und hasst jedoch nicht zwangsläufig.");
		sb.append("Der Graph ist benannt, da jedem Knoten ein Name und jeder Kante ein Typ zugeordnet ist.");
		sb.append("Ein anderes Beispiel sind Netzwerkverbindungen.");

		sb.append("Jeder Knoten entspricht einem Computer, Switch, Router.");
		sb.append("Jede Kante einer Verbindung.");
		sb.append("Jede Verbindung hat eine Bandbreite.");

		sb.append("		In diesem Fall spricht man auch von gewichteten Graphen.");
		sb.append("Eine Verallgemeinerung von benannten oder gewichteten Graphen sind Eigenschaftsgraphen.");
		sb.append("Ein Eigenschaftsgraph ist ein gerichteter Multigraph.");
		sb.append("Jedem Knoten und jeder Kante werden zusätzlich beliebige Eigenschaften zugeordnet.");
		sb.append("Graphdatenbanken speichern solche Eigenschaftsgraphen.");
		sb.append("Sie bieten im Allgemeinen Algorithmen, um Graphen zu traversieren, um Pfadberechnungen durchzuführen und um Hot-Spots zu identifizieren.");
		sb.append("Im obigem Beispiel können Traversionsalgorithmen benutzt werden, um alle direkten und indirekten Bekannten einer Person zu finden.");
		sb.append("Pfadberechnungen liefern die kürzeste Bekanntenbeziehung zwischen zwei Personen.");
		sb.append("Hot-Spots identifizieren gut vernetzte Personen.		");
		return sb.toString();
	}

	/**
	 * Create db using manual config values
	 * 
	 * @throws Exception
	 */
	private void createDatabase(int max_relations, String index_key,
			String cache_type) throws Exception {
		this.INDEX_KEY = index_key;
		this.MAX_RELATIONS = max_relations;
		this.CACHE_TYPE = cache_type;

		IOHelper.logContext("(DBConnectionTestImpl.init()) Creating new test graph...");
		Map<String, String> config = new HashMap<String, String>();
		config.put("cache_type", this.CACHE_TYPE);

		this.graph = new TestGraphDatabaseFactory()
				.newImpermanentDatabaseBuilder().newGraphDatabase();

		IndexManager ix = this.graph.index();
		this.wordIndex = ix.forNodes(this.INDEX_KEY);
		this.dn = new DynamicRelationshipType[this.MAX_RELATIONS + 1];
		for (int i = 1; i <= this.MAX_RELATIONS; i++) {
			this.dn[i] = DynamicRelationshipType.withName("rel:" + i);
		}
		this.REF_NODE = this.graph.getReferenceNode();
		IOHelper.logContext("(DBConn.init()) Test graph is up and running");
	}

	/**
	 * Fill db with standard text.
	 */
	public void fillWithText() {
		fillWithText(getStandardText());
	}

	/**
	 * Fill db with text.
	 * 
	 * @param text
	 *            The text to store in db
	 */
	public void fillWithText(String text) {
		ArrayList<String> sepr_word = new ArrayList<String>();
		ArrayList<String> sepr_sentence = new ArrayList<String>();
		sepr_word.add("\\s");
		sepr_sentence.add("\\.");
		sepr_sentence.add(";");
		sepr_sentence.add("!");
		sepr_sentence.add("\\?");
		sepr_sentence.add(":");
		DBTools.appendTextAndNorm(text, this, sepr_sentence, sepr_word,
				"a-zA-ZßüäöÜÄÖ,");
	}

	/**
	 * Close db connection
	 * 
	 * @see de.typology.db.persistence.IDBConnection#shutdown()
	 */
	@Override
	public void shutdown() {
		IOHelper.logContext("(DBConn.closeConnection()) Shutdown requested for "
				+ this.DB_PATH);
		if (this.graph != null) {
			this.graph.shutdown();
		}
		this.shutdown = true;
	}

}
