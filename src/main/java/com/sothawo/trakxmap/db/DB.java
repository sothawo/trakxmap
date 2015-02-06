/*
 Copyright 2015 Peter-Josef Meisch (pj.meisch@sothawo.com)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package com.sothawo.trakxmap.db;

import com.sothawo.trakxmap.util.Failure;
import com.sothawo.trakxmap.util.I18N;
import com.sothawo.trakxmap.util.PathTools;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceProviderResolver;
import javax.persistence.spi.PersistenceProviderResolverHolder;
import java.util.*;

/**
 * a DB Object encapsulates the Hibernate Logic and methods. It must be closed to release it's resources.
 *
 * @author P.J.Meisch (pj.meisch@jaroso.de)
 */
public class DB implements AutoCloseable {
// ------------------------------ FIELDS ------------------------------

    /** the Logger */
    private final static Logger logger = LoggerFactory.getLogger(DB.class);
    /** name of the persistence unit */
    private static final String PERSISTENCE_UNIT_NAME = "trakxmap";

    /** Entity Manager Factory */
    private final EntityManagerFactory emf;

// -------------------------- STATIC METHODS --------------------------

    static {
        fixDeprecatedHibernateWarning();
    }

    /**
     * fixes the warning (Encountered a deprecated javax.persistence.spi.PersistenceProvider [org.hibernate.ejb
     * .HibernatePersistence]; use [org.hibernate.jpa.HibernatePersistenceProvider] instead.)
     *
     * must be called before getting the EntityManagerFactory
     */
    private static void fixDeprecatedHibernateWarning() {
        PersistenceProviderResolverHolder.setPersistenceProviderResolver(new PersistenceProviderResolver() {
            private final List<PersistenceProvider> providers_ =
                    Arrays.asList(new PersistenceProvider[]{new HibernatePersistenceProvider()});

            @Override
            public List<PersistenceProvider> getPersistenceProviders() {
                return providers_;
            }

            @Override
            public void clearCachedProviders() {

            }
        });
    }

// --------------------------- CONSTRUCTORS ---------------------------

    /**
     * creates a EntityManagerFactory for the persitence unit #PERSISTENCE_UNIT_NAME
     */
    public DB() {
        Map<String, String> props = new HashMap<>();
        props.put("hibernate.connection.url", PathTools.getJdbcUrl());
        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, props);
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    /**
     * loads the tracks from the database
     *
     * @return List of track, may be emtpy but not null
     */
    public List<Track> loadTracks() {
        List<Track> tracks = new ArrayList<>();
        logger.debug(I18N.get(I18N.LOG_LOADING_TRACKS));
        try {
            EntityManager em = emf.createEntityManager();
            List<Track> resultList = em.createQuery("select t from Track t", Track.class).getResultList();
            tracks.addAll(resultList);
            em.close();
        } catch (IllegalStateException | IllegalArgumentException | PersistenceException e) {
            logger.error(I18N.get(I18N.ERROR_LOADING_TRACK), e);
        }
        return tracks;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface AutoCloseable ---------------------

    @Override
    public void close() {
        try {
            if (null != emf) {
                emf.close();
            }
        } catch (RuntimeException e) {
            logger.warn("DB", e);
        }
    }

// -------------------------- OTHER METHODS --------------------------

    /**
     * stores a track in the database
     *
     * @param track
     *         the track to store
     */
    public Optional<Failure> store(Track track) {
        if (null != track) {
            try {
                EntityManager em = emf.createEntityManager();
                EntityTransaction tx = em.getTransaction();
                tx.begin();
                em.persist(track);
                tx.commit();
                em.close();
            } catch (IllegalStateException e) {
                return Optional.of(new Failure("store", e));
            }
        }
        return Optional.empty();
    }
}
