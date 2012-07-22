/**
 * Helper class that hold various repeating tasks in db maintanence
 *
 * @author Paul Wagner
 *
 */

package de.typology.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import de.typology.db.persistence.IDBConnection;
import de.typology.tools.ConfigHelper;
import de.typology.tools.IOHelper;

public class DBTools {

	/**
	 * Append text data to db without changing weights.
	 */
	public static void appendTextWithoutNorm() {		

	}

	/**
	 * Delete relationships from reference node
	 * 
	 * @param reference
	 *            Node for deleting relationships
	 */
	public static void deleteRelationships(Node reference) {
		for (Relationship rel : reference.getRelationships()) {
			rel.delete();
		}
	}

	/**
	 * Delete a node with all its relationships
	 * 
	 * @param node
	 *            Node to delete
	 */
	public static void deleteNode(Node node) {
		deleteRelationships(node);
		node.delete();
	}

	// FOR TESTS

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
	public static void appendTextAndNorm(String text, IDBConnection db,
			ArrayList<String> sepr_sentence, ArrayList<String> sepr_word,
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
		bufferDatabaseToMaps(db, nodeMap, relMap);

		while (text.contains("  ")) {
			text = text.replaceAll("  ", " ");
		}
		String[] stnces = text.split(pattern_sentence.toString());

		Transaction tx = db.getGraph().beginTx();
		
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
							Node n = db.getGraph().createNode();
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
											db.getDn()[s]);
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
		tx = db.getGraph().beginTx();
		
		for (Node n : db.getGraph().getAllNodes()) {
			if (n.hasProperty(ConfigHelper.getNAME_KEY())) {
				String word = (String) n
						.getProperty(ConfigHelper.getNAME_KEY());

				if (db.getWordIndex().get(ConfigHelper.getNAME_KEY(), word)
						.size() == 0) {
					db.getWordIndex().add(n, ConfigHelper.getNAME_KEY(), word);

				}
			}
		}

		tx.success();
		tx.finish();
	}

	/**
	 * Buffer database to maps. This shouldn't be done on large production dbs,
	 * it's just for tesing
	 */
	private static void bufferDatabaseToMaps(IDBConnection db,
			HashMap<String, Node> nodeMap, HashMap<String, Relationship> relMap) {
		String sn1 = "";
		String sn2 = "";
		String relMapKey = "";
		for (Node n1 : db.getGraph().getAllNodes()) {
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
			for (Relationship r : n1.getRelationships(db.getDn()[1],
					Direction.OUTGOING)) {
				sn2 = (String) r.getEndNode().getProperty(
						ConfigHelper.getNAME_KEY());
				relMapKey = sn1 + ":" + sn2 + ":1";
				relMap.put(relMapKey, r);
			}
			for (Relationship r : n1.getRelationships(db.getDn()[2],
					Direction.OUTGOING)) {
				sn2 = (String) r.getEndNode().getProperty(
						ConfigHelper.getNAME_KEY());
				relMapKey = sn1 + ":" + sn2 + ":2";
				relMap.put(relMapKey, r);
			}
			for (Relationship r : n1.getRelationships(db.getDn()[3],
					Direction.OUTGOING)) {
				sn2 = (String) r.getEndNode().getProperty(
						ConfigHelper.getNAME_KEY());
				relMapKey = sn1 + ":" + sn2 + ":3";
				relMap.put(relMapKey, r);
			}
			for (Relationship r : n1.getRelationships(db.getDn()[4],
					Direction.OUTGOING)) {
				sn2 = (String) r.getEndNode().getProperty(
						ConfigHelper.getNAME_KEY());
				relMapKey = sn1 + ":" + sn2 + ":4";
				relMap.put(relMapKey, r);
			}
		}
	}

	/**
	 * Norm whole database. Use with caution and only on test dbs!
	 */
	public static void normDatabase(IDBConnection db) {
		HashMap<String, HashMap<Long, Relationship>> nodeMap1 = new HashMap<String, HashMap<Long, Relationship>>();
		HashMap<String, HashMap<Long, Relationship>> nodeMap2 = new HashMap<String, HashMap<Long, Relationship>>();
		HashMap<String, HashMap<Long, Relationship>> nodeMap3 = new HashMap<String, HashMap<Long, Relationship>>();
		HashMap<String, HashMap<Long, Relationship>> nodeMap4 = new HashMap<String, HashMap<Long, Relationship>>();

		IOHelper.log("(DBTools.normDatabase()) Start norming whole database. This will take a while...");
		System.out.println("Start norming database");

		int cnt = 0;
		int max_loc = 0;
		int cur = 0;
		IOHelper.log("(DBTools.normDatabase()) Buffer node relationships...");
		System.out.println("Buffer database for norming...");
		// Buffer node rels
		String name = "";
		HashMap<Long, Relationship> tmp = null;
		for (Node n : db.getGraph().getAllNodes()) {
			cnt++;
			if (!n.hasProperty(ConfigHelper.getNAME_KEY())) {
				continue;
			}
			name = (String) n.getProperty(ConfigHelper.getNAME_KEY());
			tmp = new HashMap<Long, Relationship>();
			for (Relationship r : n.getRelationships(db.getDn()[1],
					Direction.OUTGOING)) {
				tmp.put(r.getId(), r);
			}
			nodeMap1.put(name, tmp);
			tmp = new HashMap<Long, Relationship>();
			for (Relationship r : n.getRelationships(db.getDn()[2],
					Direction.OUTGOING)) {
				tmp.put(r.getId(), r);
			}
			nodeMap2.put(name, tmp);
			tmp = new HashMap<Long, Relationship>();
			for (Relationship r : n.getRelationships(db.getDn()[3],
					Direction.OUTGOING)) {
				tmp.put(r.getId(), r);
			}
			nodeMap3.put(name, tmp);
			tmp = new HashMap<Long, Relationship>();
			for (Relationship r : n.getRelationships(db.getDn()[4],
					Direction.OUTGOING)) {
				tmp.put(r.getId(), r);
			}
			nodeMap4.put(name, tmp);
			System.out.println(cnt + ": Next node...");
		}
		IOHelper.log("(DBTools.normDatabase()) Finished buffer node relationships.");
		System.out.println("Finished prebuffering");

		// Now iterate nodes and relationship to obtain loc norm
		cnt = 0;
		Set<String> set;
		Set<Long> set2;
		Relationship r = null;
		HashMap<Long, Relationship> rel = new HashMap<Long, Relationship>();

		Transaction tx = db.getGraph().beginTx();

		// rel:1
		set = nodeMap1.keySet();
		for (String key : set) {
			rel = nodeMap1.get(key);
			set2 = rel.keySet();
			max_loc = 0;
			System.out.println("rel:1 " + (cnt + 1) + ": Get node maximum");
			for (Long key2 : set2) {
				// Get loc maximum
				cur = (Integer) rel.get(key2).getProperty(
						ConfigHelper.getCOUNT_KEY());
				if (cur > max_loc) {
					max_loc = cur;
				}
			}
			System.out.println("rel:1 " + (cnt + 1) + ": Write node rels");
			for (Long key2 : set2) {
				cnt++;
				// Write loc maximum
				r = rel.get(key2);
				cur = (Integer) r.getProperty(ConfigHelper.getCOUNT_KEY());
				r.setProperty(ConfigHelper.getREL_KEY(), cur / max_loc);
				if (cnt % 50000 == 49999) {
					System.out.println("Commiting...");
					tx.success();
					tx.finish();
					tx = db.getGraph().beginTx();
				}
			}
		}

		// rel:2
		set = nodeMap2.keySet();
		for (String key : set) {
			rel = nodeMap2.get(key);
			set2 = rel.keySet();
			max_loc = 0;
			System.out.println("rel:2 " + (cnt + 1) + ": Get node maximum");
			for (Long key2 : set2) {
				// Get loc maximum
				cur = (Integer) rel.get(key2).getProperty(
						ConfigHelper.getCOUNT_KEY());
				if (cur > max_loc) {
					max_loc = cur;
				}
			}
			System.out.println("rel:2 " + (cnt + 1) + ": Write node rels");
			for (Long key2 : set2) {
				cnt++;
				// Write loc maximum
				r = rel.get(key2);
				cur = (Integer) r.getProperty(ConfigHelper.getCOUNT_KEY());
				r.setProperty(ConfigHelper.getREL_KEY(), cur / max_loc);
				if (cnt % 50000 == 49999) {
					System.out.println("Commiting...");
					tx.success();
					tx.finish();
					tx = db.getGraph().beginTx();
				}
			}
		}

		// rel:3
		set = nodeMap3.keySet();
		for (String key : set) {
			rel = nodeMap3.get(key);
			set2 = rel.keySet();
			max_loc = 0;
			System.out.println("rel:3 " + (cnt + 1) + ": Get node maximum");
			for (Long key2 : set2) {
				// Get loc maximum
				cur = (Integer) rel.get(key2).getProperty(
						ConfigHelper.getCOUNT_KEY());
				if (cur > max_loc) {
					max_loc = cur;
				}
			}
			System.out.println("rel:3 " + (cnt + 1) + ": Write node rels");
			for (Long key2 : set2) {
				cnt++;
				// Write loc maximum
				r = rel.get(key2);
				cur = (Integer) r.getProperty(ConfigHelper.getCOUNT_KEY());
				r.setProperty(ConfigHelper.getREL_KEY(), cur / max_loc);
				if (cnt % 50000 == 49999) {
					System.out.println("Commiting...");
					tx.success();
					tx.finish();
					tx = db.getGraph().beginTx();
				}
			}
		}

		// rel:4
		set = nodeMap4.keySet();
		for (String key : set) {
			rel = nodeMap4.get(key);
			set2 = rel.keySet();
			max_loc = 0;
			System.out.println("rel:4 " + (cnt + 1) + ": Get node maximum");
			for (Long key2 : set2) {
				// Get loc maximum
				cur = (Integer) rel.get(key2).getProperty(
						ConfigHelper.getCOUNT_KEY());
				if (cur > max_loc) {
					max_loc = cur;
				}
			}
			System.out.println("rel:4 " + (cnt + 1) + ": Write node rels");
			for (Long key2 : set2) {
				cnt++;
				// Write loc maximum
				r = rel.get(key2);
				cur = (Integer) r.getProperty(ConfigHelper.getCOUNT_KEY());
				r.setProperty(ConfigHelper.getREL_KEY(), cur / max_loc);
				if (cnt % 50000 == 49999) {
					System.out.println("Commiting...");
					tx.success();
					tx.finish();
					tx = db.getGraph().beginTx();
				}
			}
		}
		tx.success();
		tx.finish();
		System.out.println("Commiting changes...");		
		tx = db.getGraph().beginTx();
		IOHelper.log("(DBTools.normDatabase()) Finished norming database...");
		System.out.println("Finished norming.");
	}

}
