/* Copyright (C) 2011 SpringSource
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.grails.datastore.gorm.dynamodb.plugin.support

import org.grails.datastore.gorm.dynamodb.bean.factory.DynamoDBDatastoreFactoryBean
import org.grails.datastore.gorm.dynamodb.bean.factory.DynamoDBMappingContextFactoryBean

import org.grails.datastore.gorm.plugin.support.SpringConfigurer
import org.grails.datastore.mapping.cache.impl.TPCacheAdapterRepositoryImpl
import org.grails.datastore.mapping.transactions.DatastoreTransactionManager

/**
 * DynamoDB specific configuration logic for Spring
 *
 * @author Roman Stepanenko after Graeme Rocher
 * @since 1.0
 */
class DynamoDBSpringConfigurer extends SpringConfigurer {
    @Override
    String getDatastoreType() {
        return "DynamoDB"
    }

    @Override
    Closure getSpringCustomizer() {
        return {
            def dynamoDBConfig = application.config?.grails?.dynamodb
            def cacheAdapters = application.config?.grails?.cacheAdapters

            def theCacheAdapterRepository = new TPCacheAdapterRepositoryImpl()
            cacheAdapters?.each { clazz, adapter ->
                theCacheAdapterRepository.setTPCacheAdapter(clazz, adapter)
            }


            dynamodbTransactionManager(DatastoreTransactionManager) {
                datastore = ref("dynamodbDatastore")
            }

            dynamodbMappingContext(DynamoDBMappingContextFactoryBean) {
                grailsApplication = ref('grailsApplication')
                pluginManager = ref('pluginManager')
            }

            dynamodbDatastore(DynamoDBDatastoreFactoryBean) {
                mappingContext = ref("dynamodbMappingContext")
                config = dynamoDBConfig.toProperties()
                cacheAdapterRepository = theCacheAdapterRepository
            }
        }
    }
}
