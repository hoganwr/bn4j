/*
 * Licensed to Neo4j under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Neo4j licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package edu.uf.bmi.bn4j;

import java.io.IOException;
import java.nio.file.Path;

import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.io.fs.FileUtils;

import static org.neo4j.configuration.GraphDatabaseSettings.DEFAULT_DATABASE_NAME;

public class Bn4jStarter {
    private static final Path databaseDirectory = Path.of( "target/neo4j-bn4j-db" );

    public String msg;

    // tag::vars[]
    GraphDatabaseService graphDb;
    Node n1;
    Node n2;
    Relationship rel;
    private DatabaseManagementService mgmtSvc;
    // end::vars[]

    // tag::createReltype[]
    private enum RelTypes implements RelationshipType
    {
        ISA
    }
    // end::createReltype[]

    void createDb() throws IOException
    {
        FileUtils.deleteDirectory( databaseDirectory );

        // tag::startDb[]
        mgmtSvc = new DatabaseManagementServiceBuilder( databaseDirectory ).build();
        graphDb = mgmtSvc.database( DEFAULT_DATABASE_NAME );
        registerShutdownHook( mgmtSvc );
        // end::startDb[]

        // tag::transaction[]
        try ( Transaction tx = graphDb.beginTx() )
        {
            // Database operations go here
            // end::transaction[]
            // tag::addData[]
            n1 = tx.createNode();
            n1.setProperty( "label", "electromagnetic force" );
            n2 = tx.createNode();
            n2.setProperty( "label", "fundamental physical force" );

            rel = n1.createRelationshipTo( n2, RelTypes.ISA );
            rel.setProperty( "label", "(shown by science)" );
            // end::addData[]

            // tag::readData[]
            System.out.print( n1.getProperty( "label" ) );
		System.out.print(" ");
		System.out.print(rel.getType().toString());
		System.out.print(" ");
            System.out.print( rel.getProperty( "label" ) );
		System.out.print(" ");
            System.out.print( n2.getProperty( "label" ) );
            // end::readData[]

            String stmt = ( (String) n1.getProperty( "label" ) )
                       + " " + rel.getType().toString() + " " 
			+ ( (String) rel.getProperty( "label" ) )
                       + " "+ ( (String) n2.getProperty( "label" ) );

            // tag::transaction[]
            tx.commit();
        }
        // end::transaction[]
    }

    void removeData()
    {
        try ( Transaction tx = graphDb.beginTx() )
        {
            // tag::removingData[]
            // let's remove the data
            n1 = tx.getNodeById( n1.getId() );
            n2 = tx.getNodeById( n2.getId() );
            n1.getSingleRelationship( RelTypes.ISA, Direction.OUTGOING ).delete();
            n1.delete();
            n2.delete();
            // end::removingData[]

            tx.commit();
        }
    }

    void shutDown()
    {
        System.out.println();
        System.out.println( "Shutting down database ..." );
        // tag::shutdownServer[]
        mgmtSvc.shutdown();
        // end::shutdownServer[]
    }

    // tag::shutdownHook[]
    private static void registerShutdownHook( final DatabaseManagementService mgmtSvc )
    {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running application).
        Runtime.getRuntime().addShutdownHook( new Thread()
        {
            @Override
            public void run()
            {
                mgmtSvc.shutdown();
            }
        } );
    }
    // end::shutdownHook[]
}
