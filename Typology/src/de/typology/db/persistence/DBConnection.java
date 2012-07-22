/**
 * Class for establishing connection to Neo4J database
 *
 * @author Paul Wagner
 */
package de.typology.db.persistence;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import de.typology.tools.ConfigHelper;
import de.typology.tools.IOHelper;

public class DBConnection implements IDBConnection {

	// PROPERTIES
	private final EmbeddedGraphDatabase graph;
	private final Index<Node> wordIndex;
	private final DynamicRelationshipType[] dn;
	private final Node REF_NODE;

	private String DB_PATH = ConfigHelper.getDB_PATH();
	private String INDEX_KEY = ConfigHelper.getNAME_KEY();
	private String CACHE_TYPE = ConfigHelper.getCACHE_TYPE();
	private Integer MAX_RELATIONS = ConfigHelper.getMAX_RELATIONS();

	private boolean shutdown = false;

	// CONSTRUCTORS
	/**
	 * Constructor using ConfigHelper
	 * 
	 * @throws Exception
	 */
	public DBConnection() throws Exception {
		this(ConfigHelper.getDB_PATH(), ConfigHelper.getMAX_RELATIONS(),
				ConfigHelper.getNAME_KEY(), ConfigHelper.getCACHE_TYPE());
	}

	/**
	 * Constructor using manual config values
	 * 
	 * @throws Exception
	 */
	public DBConnection(String database, int max_relations, String index_key,
			String cache_type) throws Exception {
		this.DB_PATH = database;
		this.INDEX_KEY = index_key;
		this.MAX_RELATIONS = max_relations;
		this.CACHE_TYPE = cache_type;

		IOHelper.logContext("(DBConn.init()) Create new connection with " + this.DB_PATH);
		Map<String, String> config = new HashMap<String, String>();
		config.put("cache_type", this.CACHE_TYPE);
		if(ConfigHelper.getALLOW_UPGRADE()){
			config.put("allow_store_upgrade", "true");
			IOHelper.logContext("WARNING: (DBConn.init()) Upgrade flag is set. If you don't want to upgrade neo4j and have no new libraries in classpath, remove it from config! ");
			IOHelper.logContext("WARNING: (DBConn.init()) DB will be upgrading if necessary. Don't stop the server while upgrade in progress! This may take a while... ");
		}
		this.graph = new EmbeddedGraphDatabase(this.DB_PATH, config);
		registerShutdownHook(this.graph);
		this.REF_NODE = this.graph.getReferenceNode();
		IndexManager ix = this.graph.index();
		this.wordIndex = ix.forNodes(this.INDEX_KEY);
		this.dn = new DynamicRelationshipType[this.MAX_RELATIONS + 1];
		for (int i = 1; i <= this.MAX_RELATIONS; i++) {
			this.dn[i] = DynamicRelationshipType.withName("rel:" + i);
		}
		IOHelper.logContext("(DBConn.init()) Graph db is up and running version " + getNeo4JVersion());		
	}

	// GETTER
	public String getINDEX_KEY() {
		return INDEX_KEY;
	}

	public Integer getMAX_RELATIONS() {
		return MAX_RELATIONS;
	}

	public Node getReferenceNode() {
		return REF_NODE;
	}
	
	public String getNeo4JVersion(){
		if(this.graph != null){
			return this.graph.getKernelData().version().getVersion();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.typology.db.persistence.IDBConnection#getDB_PATH()
	 */
	@Override
	public String getDB_PATH() {
		return DB_PATH;
	}

	public DynamicRelationshipType[] getDn() {
		return dn;
	}

	public EmbeddedGraphDatabase getGraph() {
		return graph;
	}

	public Index<Node> getWordIndex() {
		return wordIndex;
	}

	public Boolean isShutdown() {
		return (this.shutdown || this.graph == null);
	}

	// METHODS
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.typology.db.persistence.IDBConnection#closeConnection()
	 */
	@Override
	public void closeConnection() {
		IOHelper.logContext("(DBConn.closeConnection()) Shutdown requested for " + this.DB_PATH);
		if (this.graph != null) {
			this.graph.shutdown();
		}
		this.shutdown = true;
	}

	/**
	 * Check if given Node is the reference node.
	 * 
	 * @param n
	 *            Node to check
	 * @return is n the reference node
	 */
	public boolean isReferenceNode(Node n) {
		return (REF_NODE != null && REF_NODE.getId() == n.getId());
	}

	/**
	 * Buffer database
	 * 
	 * @deprecated Don't use this method anymore, because it justs load all
	 *             nodes and relationships to RAM. You should load everything
	 *             you need in your DBLayer corresponding to your Retrieval
	 * 
	 * @see de.typology.db.layer.IDBLayer#loadLayer()
	 */
	protected void bufferDatabase() {
		IOHelper.log("(DBConn) Start buffering database");
		Long ref = this.graph.getReferenceNode().getId();
		for (Node n : this.graph.getAllNodes()) {
			if (n.getId() != ref) {
				n.getProperty(ConfigHelper.getNAME_KEY());
			}
			for (Relationship r : n.getRelationships(Direction.OUTGOING)) {
				if (r.hasProperty(ConfigHelper.getREL_KEY())) {
					r.getProperty(ConfigHelper.getREL_KEY());
				}
				if (r.hasProperty(ConfigHelper.getCOUNT_KEY())) {
					r.getProperty(ConfigHelper.getCOUNT_KEY());
				}
				r.getEndNode().getProperty(ConfigHelper.getNAME_KEY());
			}
		}
		IOHelper.log("(DBConn) Successfully buffered database");
	}

	/**
	 * Shutdown Hook
	 * 
	 * @param graphDb
	 *            graph to register
	 */
	private static void registerShutdownHook(final EmbeddedGraphDatabase graphDb) {
		// Registers a shutdown hook for the Neo4j instance so that it
		// shuts down nicely when the VM exits (even if you "Ctrl-C" the
		// running example before it's completed)
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				if (graphDb != null) {
					graphDb.shutdown();
				}
			}
		});
	}
}
