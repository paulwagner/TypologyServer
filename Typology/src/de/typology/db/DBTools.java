/**
 * Helper class that hold various repeating tasks in db maintanence
 *
 * @author Paul Wagner
 *
 */

package de.typology.db;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

public class DBTools {

    public static void deleteRelationships(Node reference){
        for(Relationship rel : reference.getRelationships()){
            rel.delete();
        }
    }

    public static void deleteNode(Node node){
        deleteRelationships(node);
        node.delete();
    }

}
