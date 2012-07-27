/**
 * Impermanent database connection implementation.
 * This class will simulate a little typology graph with specified data, wich may be interesting for many purposes...
 * 
 * @author Paul Wagner
 */
package de.typology.db.persistence.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.test.TestGraphDatabaseFactory;

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

		IOHelper.logContext("(DBConnectionTestImpl.init()) Creating new test graph...");
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
		appendTextAndNorm(text, sepr_sentence, sepr_word,
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
	
	// HELPER METHODS
	
	/**
	 * Append text data to db with calculating edge weights and norm afterwards
	 * the whole db. This method should NOT be used on large production data
	 * bases. It's just for testing.
	 * 
	 * @param text
	 *            The text to insert into db
	 * @param db
	 *            The db to insert to.
	 * @param sepr_sentence
	 *            Sentence seperators, ALREADY REGEX ESCAPED! So use '\.'
	 *            instead of '.'
	 * @param sepr_words
	 *            Word seperators, ALREADY REGEX ESCAPED! So use '\s' instead of
	 *            ' '
	 * @param dont_escape
	 *            String with regex not to delete from strings (eg
	 *            a-zA-ZßüäöÜÄÖ,). Whitespaces will never be deleted.
	 */
	private void appendTextAndNorm(String text, ArrayList<String> sepr_sentence, ArrayList<String> sepr_word,
			String dont_escape) {
		// Compile regex patterns
		StringBuilder pattern_sentence = new StringBuilder();
		for (int i = 0; i < sepr_sentence.size(); i++) {
			if(i > 0){
				pattern_sentence.append("|");
			}
			String sepr = sepr_sentence.get(i);
			pattern_sentence.append(sepr);
		}
		StringBuilder pattern_word = new StringBuilder();
		for (int i = 0; i < sepr_word.size(); i++) {
			if(i > 0){
				pattern_word.append("|");
			}
			String sepr = sepr_word.get(i);
			pattern_word.append(sepr);
		}

		HashMap<String, Node> nodeMap = new HashMap<String, Node>();
		HashMap<String, Relationship> relMap = new HashMap<String, Relationship>();
		bufferToMaps( nodeMap, relMap);

		while (text.contains("  ")) {
			text = text.replaceAll("  ", " ");
		}
		String[] stnces = text.split(pattern_sentence.toString());

		Transaction tx = graph.beginTx();
		
		for (String stnce : stnces) {
			stnce = stnce.trim().replaceAll("[^" + dont_escape + " ]", "");
			String[] words = stnce.split(pattern_word.toString());
			for (int s = 1; s < ConfigHelper.getMAX_RELATIONS() + 1; s++) {
				int i = 0;
				while (i < words.length) {
					// Wenn Step 1, dann Nodes hinzufügen
					if (s == 1) {
						String word = words[i].trim();
						if (nodeMap.get(word) != null) {
							Node n = nodeMap.get(word);
							Integer n_cnt = (Integer) nodeMap.get(word)
									.getProperty(ConfigHelper.getCOUNT_KEY());
							n.setProperty(ConfigHelper.getCOUNT_KEY(),
									n_cnt + 1);
						} else {
							Node n = graph.createNode();
							n.setProperty(ConfigHelper.getNAME_KEY(), word);
							n.setProperty(ConfigHelper.getCOUNT_KEY(), 1);
							nodeMap.put(word, n);
						}
					}
					// Wenn i>1, Beziehung von i-s zu i mit typ s hinzufügen
					if (i >= s) {
						String myrelMapKey = words[i - s] + ":" + words[i]
								+ ":" + s;
						Relationship rel = relMap.get(myrelMapKey);
						if (rel != null) {
							Integer mycnt = (Integer) rel
									.getProperty(ConfigHelper.getCOUNT_KEY());
							rel.setProperty(ConfigHelper.getCOUNT_KEY(),
									mycnt + 1);
						} else {
							rel = nodeMap.get(words[i - s])
									.createRelationshipTo(
											nodeMap.get(words[i]),
											dn[s]);
							rel.setProperty(ConfigHelper.getCOUNT_KEY(), 1);
							relMap.put(myrelMapKey, rel);
						}
					}
					i += 1;
				}
			}
		}
		
		tx.success();
		tx.finish();
		tx = graph.beginTx();
		
		for (Node n : graph.getAllNodes()) {
			if (n.hasProperty(ConfigHelper.getNAME_KEY())) {
				String word = (String) n
						.getProperty(ConfigHelper.getNAME_KEY());

				if (wordIndex.get(ConfigHelper.getNAME_KEY(), word)
						.size() == 0) {
					wordIndex.add(n, ConfigHelper.getNAME_KEY(), word);

				}
			}
		}

		tx.success();
		tx.finish();
		
		norm();
	}

	/**
	 * Buffer database to maps. This shouldn't be done on large production dbs,
	 * it's just for tesing
	 */
	private void bufferToMaps(HashMap<String, Node> nodeMap, HashMap<String, Relationship> relMap) {
		String sn1 = "";
		String sn2 = "";
		String relMapKey = "";
		for (Node n1 : graph.getAllNodes()) {
			if (!n1.hasProperty(ConfigHelper.getNAME_KEY())) {
				continue;
			}
			sn1 = (String) n1.getProperty(ConfigHelper.getNAME_KEY());
			nodeMap.put(sn1, n1);
			for (Relationship r : n1.getRelationships(Direction.OUTGOING)) {
				sn2 = (String) r.getEndNode().getProperty(
						ConfigHelper.getNAME_KEY());
				relMapKey = sn1 + ":" + sn2 + ":" + r.getType().name();
				relMap.put(relMapKey, r);
			}
			for (Relationship r : n1.getRelationships(dn[1],
					Direction.OUTGOING)) {
				sn2 = (String) r.getEndNode().getProperty(
						ConfigHelper.getNAME_KEY());
				relMapKey = sn1 + ":" + sn2 + ":1";
				relMap.put(relMapKey, r);
			}
			for (Relationship r : n1.getRelationships(dn[2],
					Direction.OUTGOING)) {
				sn2 = (String) r.getEndNode().getProperty(
						ConfigHelper.getNAME_KEY());
				relMapKey = sn1 + ":" + sn2 + ":2";
				relMap.put(relMapKey, r);
			}
			for (Relationship r : n1.getRelationships(dn[3],
					Direction.OUTGOING)) {
				sn2 = (String) r.getEndNode().getProperty(
						ConfigHelper.getNAME_KEY());
				relMapKey = sn1 + ":" + sn2 + ":3";
				relMap.put(relMapKey, r);
			}
			for (Relationship r : n1.getRelationships(dn[4],
					Direction.OUTGOING)) {
				sn2 = (String) r.getEndNode().getProperty(
						ConfigHelper.getNAME_KEY());
				relMapKey = sn1 + ":" + sn2 + ":4";
				relMap.put(relMapKey, r);
			}
		}
	}

	/**
	 * Norm whole database.
	 */
	private void norm() {
		HashMap<String, HashMap<Long, Relationship>> nodeMap1 = new HashMap<String, HashMap<Long, Relationship>>();
		HashMap<String, HashMap<Long, Relationship>> nodeMap2 = new HashMap<String, HashMap<Long, Relationship>>();
		HashMap<String, HashMap<Long, Relationship>> nodeMap3 = new HashMap<String, HashMap<Long, Relationship>>();
		HashMap<String, HashMap<Long, Relationship>> nodeMap4 = new HashMap<String, HashMap<Long, Relationship>>();

		int cnt = 0;
		double max_loc = 0d;
		int cur = 0;
		// Buffer node rels
		String name = "";
		HashMap<Long, Relationship> tmp = null;
		for (Node n : graph.getAllNodes()) {
			cnt++;
			if (!n.hasProperty(ConfigHelper.getNAME_KEY())) {
				continue;
			}
			name = (String) n.getProperty(ConfigHelper.getNAME_KEY());
			tmp = new HashMap<Long, Relationship>();
			for (Relationship r : n.getRelationships(dn[1],
					Direction.OUTGOING)) {
				tmp.put(r.getId(), r);
			}
			nodeMap1.put(name, tmp);
			tmp = new HashMap<Long, Relationship>();
			for (Relationship r : n.getRelationships(dn[2],
					Direction.OUTGOING)) {
				tmp.put(r.getId(), r);
			}
			nodeMap2.put(name, tmp);
			tmp = new HashMap<Long, Relationship>();
			for (Relationship r : n.getRelationships(dn[3],
					Direction.OUTGOING)) {
				tmp.put(r.getId(), r);
			}
			nodeMap3.put(name, tmp);
			tmp = new HashMap<Long, Relationship>();
			for (Relationship r : n.getRelationships(dn[4],
					Direction.OUTGOING)) {
				tmp.put(r.getId(), r);
			}
			nodeMap4.put(name, tmp);
		}

		// Now iterate nodes and relationship to obtain loc norm
		cnt = 0;
		Set<String> set;
		Set<Long> set2;
		Relationship r = null;
		HashMap<Long, Relationship> rel = new HashMap<Long, Relationship>();

		Transaction tx = graph.beginTx();

		// rel:1
		set = nodeMap1.keySet();
		for (String key : set) {
			rel = nodeMap1.get(key);
			set2 = rel.keySet();
			max_loc = 0;
			for (Long key2 : set2) {
				// Get loc maximum
				cur = (Integer) rel.get(key2).getProperty(
						ConfigHelper.getCOUNT_KEY());
				if (cur > max_loc) {
					max_loc = cur;
				}
			}
			for (Long key2 : set2) {
				cnt++;
				// Write loc maximum
				r = rel.get(key2);
				cur = (Integer) r.getProperty(ConfigHelper.getCOUNT_KEY());
				r.setProperty(ConfigHelper.getREL_KEY(), cur / max_loc);
				if (cnt % 50000 == 49999) {
					tx.success();
					tx.finish();
					tx = graph.beginTx();
				}
			}
		}

		// rel:2
		set = nodeMap2.keySet();
		for (String key : set) {
			rel = nodeMap2.get(key);
			set2 = rel.keySet();
			max_loc = 0;
			for (Long key2 : set2) {
				// Get loc maximum
				cur = (Integer) rel.get(key2).getProperty(
						ConfigHelper.getCOUNT_KEY());
				if (cur > max_loc) {
					max_loc = cur;
				}
			}
			for (Long key2 : set2) {
				cnt++;
				// Write loc maximum
				r = rel.get(key2);
				cur = (Integer) r.getProperty(ConfigHelper.getCOUNT_KEY());
				r.setProperty(ConfigHelper.getREL_KEY(), cur / max_loc);
				if (cnt % 50000 == 49999) {
					tx.success();
					tx.finish();
					tx = graph.beginTx();
				}
			}
		}

		// rel:3
		set = nodeMap3.keySet();
		for (String key : set) {
			rel = nodeMap3.get(key);
			set2 = rel.keySet();
			max_loc = 0;
			for (Long key2 : set2) {
				// Get loc maximum
				cur = (Integer) rel.get(key2).getProperty(
						ConfigHelper.getCOUNT_KEY());
				if (cur > max_loc) {
					max_loc = cur;
				}
			}
			for (Long key2 : set2) {
				cnt++;
				// Write loc maximum
				r = rel.get(key2);
				cur = (Integer) r.getProperty(ConfigHelper.getCOUNT_KEY());
				r.setProperty(ConfigHelper.getREL_KEY(), cur / max_loc);
				if (cnt % 50000 == 49999) {
					tx.success();
					tx.finish();
					tx = graph.beginTx();
				}
			}
		}

		// rel:4
		set = nodeMap4.keySet();
		for (String key : set) {
			rel = nodeMap4.get(key);
			set2 = rel.keySet();
			max_loc = 0;
			for (Long key2 : set2) {
				// Get loc maximum
				cur = (Integer) rel.get(key2).getProperty(
						ConfigHelper.getCOUNT_KEY());
				if (cur > max_loc) {
					max_loc = cur;
				}
			}
			for (Long key2 : set2) {
				cnt++;
				// Write loc maximum
				r = rel.get(key2);
				cur = (Integer) r.getProperty(ConfigHelper.getCOUNT_KEY());
				r.setProperty(ConfigHelper.getREL_KEY(), cur / max_loc);
				if (cnt % 50000 == 49999) {
					tx.success();
					tx.finish();
					tx = graph.beginTx();
				}
			}
		}
		tx.success();
		tx.finish();
		tx = graph.beginTx();
	}


}
